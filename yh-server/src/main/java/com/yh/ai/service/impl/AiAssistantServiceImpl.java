package com.yh.ai.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yh.ai.service.AiAssistantService;
import com.yh.ai.tool.AiToolRegistry;
import com.yh.ai.tool.ToolDefinition;
import com.yh.dto.AiChatRequestDTO;
import com.yh.entity.AiConversation;
import com.yh.entity.AiMessage;
import com.yh.mapper.AiConversationMapper;
import com.yh.mapper.AiMessageMapper;
import com.yh.properties.AiAssistantProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI 助理服务实现
 */
@Service
@Slf4j
public class AiAssistantServiceImpl implements AiAssistantService {

    private static final int MAX_TOOL_LOOPS = 5;

    @Autowired
    private AiAssistantProperties aiProperties;

    @Autowired
    private AiConversationMapper conversationMapper;

    @Autowired
    private AiMessageMapper messageMapper;

    @Autowired
    private AiToolRegistry toolRegistry;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public SseEmitter chat(AiChatRequestDTO request, Long employeeId, String employeeName) {
        SseEmitter emitter = new SseEmitter(aiProperties.getTimeoutSeconds() * 1000L);

        executor.execute(() -> {
            try {
                processChatInternal(request, employeeId, employeeName, emitter);
            } catch (Exception e) {
                log.error("AI 聊天处理异常", e);
                try {
                    emitter.send(SseEmitter.event().name("error")
                            .data(JSON.toJSONString(Map.of("message", e.getMessage()))));
                    emitter.complete();
                } catch (IOException ex) {
                    log.error("发送错误事件失败", ex);
                }
            }
        });

        return emitter;
    }

    private void processChatInternal(AiChatRequestDTO request, Long employeeId,
                                     String employeeName, SseEmitter emitter) throws Exception {
        // 1. 获取或创建对话
        AiConversation conversation = getOrCreateConversation(request.getConversationId(), employeeId);

        // 2. 保存用户消息
        AiMessage userMsg = AiMessage.builder()
                .conversationId(conversation.getId())
                .role(AiMessage.ROLE_USER)
                .content(request.getMessage())
                .createTime(LocalDateTime.now())
                .build();
        messageMapper.insert(userMsg);

        // 3. 如果是新对话，自动生成标题
        if (conversation.getTitle() == null || conversation.getTitle().isEmpty()) {
            String title = request.getMessage().length() > 30
                    ? request.getMessage().substring(0, 30) + "..."
                    : request.getMessage();
            conversation.setTitle(title);
            conversationMapper.update(conversation);
        }

        // 4. 构建消息列表
        List<Map<String, Object>> messages = buildMessages(conversation.getId(), employeeName);

        // 5. 调用 DeepSeek（支持多轮 tool-use）
        String requestBody = buildRequestBody(messages);
        log.info("DeepSeek 调用: messages={}, tools={}",
                messages.size(), toolRegistry.getAllTools().size());
        callDeepSeekStreaming(requestBody, messages, conversation.getId(), emitter, MAX_TOOL_LOOPS);
    }

    /**
     * 获取或创建对话
     */
    private AiConversation getOrCreateConversation(Long conversationId, Long employeeId) {
        if (conversationId != null) {
            AiConversation conv = conversationMapper.getById(conversationId);
            if (conv != null && conv.getEmployeeId().equals(employeeId)) {
                return conv;
            }
        }
        AiConversation conv = AiConversation.builder()
                .employeeId(employeeId)
                .model(aiProperties.getModel())
                .messageCount(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        conversationMapper.insert(conv);
        return conv;
    }

    /**
     * 构建发送给 LLM 的消息列表
     */
    private List<Map<String, Object>> buildMessages(Long conversationId, String employeeName) {
        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", buildSystemPrompt(employeeName)));

        List<AiMessage> recentMessages = messageMapper.getRecentByConversationId(
                conversationId, aiProperties.getMaxHistoryMessages());
        for (AiMessage msg : recentMessages) {
            if (AiMessage.ROLE_TOOL.equals(msg.getRole())) {
                messages.add(Map.of(
                        "role", "tool",
                        "tool_call_id", msg.getToolName() != null ? msg.getToolName() : "unknown",
                        "content", msg.getContent()
                ));
            } else {
                messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
            }
        }
        return messages;
    }

    /**
     * 构建 System Prompt
     */
    private String buildSystemPrompt(String employeeName) {
        return "你是「You-Hungry 饿了吧外卖」管理后台的 AI 智能助理。\n" +
                "你的职责是帮助食堂管理员：\n" +
                "- 查询和分析经营数据（营业额、订单、用户、菜品销量等）\n" +
                "- 搜索和查看订单信息\n" +
                "- 提供数据驱动的经营建议\n\n" +
                "## 规则\n" +
                "1. 涉及数字、统计的问题，必须调用对应的工具获取真实数据，绝对不要编造数据\n" +
                "2. 如果用户问的数据没有对应工具，诚实告知「我暂时无法获取这类数据」\n" +
                "3. 回答要简洁、专业、数据驱动，使用中文\n" +
                "4. 涉及订单操作等敏感操作，必须先向用户确认\n\n" +
                "## 当前上下文\n" +
                "- 当前时间：" + LocalDateTime.now().toString() + "\n" +
                "- 管理员：" + employeeName + "\n";
    }

    /**
     * 构建 DeepSeek API 请求体
     */
    private String buildRequestBody(List<Map<String, Object>> messages) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", aiProperties.getModel());
        body.put("messages", messages);
        body.put("stream", true);
        body.put("temperature", aiProperties.getTemperature());
        body.put("max_tokens", aiProperties.getMaxTokens());

        List<Map<String, Object>> toolList = toolRegistry.buildToolListForLLM();
        if (!toolList.isEmpty()) {
            body.put("tools", toolList);
        }

        return JSON.toJSONString(body);
    }

    /**
     * 流式调用 DeepSeek API，支持 tool-use 递归
     */
    private void callDeepSeekStreaming(String requestBody, List<Map<String, Object>> messages,
                                       Long conversationId, SseEmitter emitter,
                                       int remainingLoops) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(aiProperties.getTimeoutSeconds()))
                .build();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(aiProperties.getEndpoint()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + aiProperties.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(aiProperties.getTimeoutSeconds()))
                .build();

        HttpResponse<InputStream> response = client.send(httpRequest,
                HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            String errorBody = new String(response.body().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            log.error("DeepSeek API 错误: status={}, body={}", response.statusCode(), errorBody);
            emitter.send(SseEmitter.event().name("error")
                    .data(JSON.toJSONString(Map.of("message", "AI 服务异常，请稍后重试"))));
            emitter.complete();
            return;
        }

        // 读取 SSE 流
        StringBuilder contentBuilder = new StringBuilder();
        List<Map<String, Object>> toolCalls = new ArrayList<>();
        Map<Integer, Map<String, Object>> toolCallAccumulator = new LinkedHashMap<>();

        try (InputStream inputStream = response.body()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            String leftover = "";

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                String chunk = leftover + new String(buffer, 0, bytesRead, java.nio.charset.StandardCharsets.UTF_8);
                String[] lines = chunk.split("\n");
                leftover = chunk.endsWith("\n") ? "" : lines[lines.length - 1];

                int endIndex = chunk.endsWith("\n") ? lines.length : lines.length - 1;
                for (int i = 0; i < endIndex; i++) {
                    String line = lines[i].trim();
                    if (line.isEmpty() || !line.startsWith("data: ")) continue;

                    String data = line.substring(6);
                    if ("[DONE]".equals(data)) continue;

                    JSONObject delta = JSON.parseObject(data);
                    JSONArray choices = delta.getJSONArray("choices");
                    if (choices == null || choices.isEmpty()) continue;

                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject deltaObj = choice.getJSONObject("delta");
                    if (deltaObj == null) continue;

                    // 处理文本内容
                    String contentChunk = deltaObj.getString("content");
                    if (contentChunk != null && !contentChunk.isEmpty()) {
                        contentBuilder.append(contentChunk);
                        emitter.send(SseEmitter.event().name("text").data(contentChunk));
                    }

                    // 处理 tool_calls
                    JSONArray deltaToolCalls = deltaObj.getJSONArray("tool_calls");
                    if (deltaToolCalls != null) {
                        for (int j = 0; j < deltaToolCalls.size(); j++) {
                            JSONObject tc = deltaToolCalls.getJSONObject(j);
                            int index = tc.getIntValue("index");
                            toolCallAccumulator.putIfAbsent(index, new LinkedHashMap<>());
                            Map<String, Object> acc = toolCallAccumulator.get(index);

                            if (tc.containsKey("id")) acc.put("id", tc.getString("id"));
                            if (tc.containsKey("type")) acc.put("type", tc.getString("type"));

                            JSONObject function = tc.getJSONObject("function");
                            if (function != null) {
                                if (function.containsKey("name")) {
                                    acc.put("name", function.getString("name"));
                                    emitter.send(SseEmitter.event().name("tool_call")
                                            .data(JSON.toJSONString(Map.of(
                                                    "name", function.getString("name")))));
                                }
                                if (function.containsKey("arguments")) {
                                    acc.put("arguments",
                                            acc.getOrDefault("arguments", "") + function.getString("arguments"));
                                }
                            }
                        }
                    }

                    // 检查 finish_reason
                    String finishReason = choice.getString("finish_reason");
                    if ("tool_calls".equals(finishReason)) {
                        for (Map.Entry<Integer, Map<String, Object>> entry : toolCallAccumulator.entrySet()) {
                            toolCalls.add(entry.getValue());
                        }
                    }
                }
            }
        }

        // 如果有工具调用
        if (!toolCalls.isEmpty()) {
            // 保存 assistant 消息（含 tool_calls）
            Map<String, Object> assistantContent = new LinkedHashMap<>();
            assistantContent.put("content", contentBuilder.length() > 0 ? contentBuilder.toString() : null);
            assistantContent.put("tool_calls", toolCalls);
            saveAssistantMessage(conversationId, JSON.toJSONString(assistantContent));

            // 追加 assistant 消息到 messages 列表
            Map<String, Object> assistantMap = new LinkedHashMap<>();
            assistantMap.put("role", "assistant");
            assistantMap.put("content", contentBuilder.length() > 0 ? contentBuilder.toString() : null);
            assistantMap.put("tool_calls", toolCalls);
            messages.add(assistantMap);

            // 执行每个工具调用
            for (Map<String, Object> toolCall : toolCalls) {
                String toolName = (String) toolCall.get("name");
                String toolCallId = (String) toolCall.get("id");
                String arguments = (String) toolCall.get("arguments");

                log.info("执行工具调用: name={}, args={}", toolName, arguments);

                emitter.send(SseEmitter.event().name("tool_call")
                        .data(JSON.toJSONString(Map.of("name", toolName, "arguments", arguments))));

                String toolResult = executeTool(toolName, arguments);

                emitter.send(SseEmitter.event().name("tool_result")
                        .data(JSON.toJSONString(Map.of("name", toolName, "data", toolResult))));

                // 保存 tool 消息
                AiMessage toolMsg = AiMessage.builder()
                        .conversationId(conversationId)
                        .role(AiMessage.ROLE_TOOL)
                        .content(toolResult)
                        .toolName(toolCallId)
                        .createTime(LocalDateTime.now())
                        .build();
                messageMapper.insert(toolMsg);

                messages.add(Map.of(
                        "role", "tool",
                        "tool_call_id", toolCallId,
                        "content", toolResult
                ));
            }

            // 递归调用，让 LLM 根据工具结果生成最终回答
            if (remainingLoops > 0) {
                callDeepSeekStreaming(buildRequestBody(messages), messages, conversationId,
                        emitter, remainingLoops - 1);
            } else {
                emitter.send(SseEmitter.event().name("error")
                        .data(JSON.toJSONString(Map.of("message", "工具调用次数超限，请简化问题后重试"))));
                emitter.complete();
            }
            return;
        }

        // 没有工具调用，保存最终回答
        saveAssistantMessage(conversationId, contentBuilder.toString());
        finalizeConversation(conversationId, emitter);
    }

    /**
     * 保存 assistant 消息
     */
    private void saveAssistantMessage(Long conversationId, String content) {
        AiMessage msg = AiMessage.builder()
                .conversationId(conversationId)
                .role(AiMessage.ROLE_ASSISTANT)
                .content(content)
                .createTime(LocalDateTime.now())
                .build();
        messageMapper.insert(msg);
    }

    /**
     * 完成对话，发送 done 事件
     */
    private void finalizeConversation(Long conversationId, SseEmitter emitter) throws IOException {
        AiConversation conv = conversationMapper.getById(conversationId);
        conv.setUpdateTime(LocalDateTime.now());
        conv.setMessageCount(conv.getMessageCount() + 1);
        conversationMapper.update(conv);

        emitter.send(SseEmitter.event().name("done")
                .data(JSON.toJSONString(Map.of(
                        "conversationId", conversationId,
                        "title", conv.getTitle() != null ? conv.getTitle() : ""
                ))));
        emitter.complete();
    }

    /**
     * 执行工具调用
     */
    private String executeTool(String toolName, String arguments) {
        try {
            ToolDefinition def = toolRegistry.getTool(toolName);
            if (def == null) {
                return "{\"error\": \"未知工具: " + toolName + "\"}";
            }

            JSONObject args = (arguments != null && !arguments.isEmpty())
                    ? JSON.parseObject(arguments)
                    : new JSONObject();

            Object bean = def.getBean();
            java.lang.reflect.Method method = def.getMethod();
            Object[] methodArgs = resolveMethodArgs(method, args);
            Object result = method.invoke(bean, methodArgs);

            return JSON.toJSONString(result);
        } catch (Exception e) {
            log.error("工具执行异常: toolName={}, args={}", toolName, arguments, e);
            return "{\"error\": \"工具执行失败: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 解析方法参数（将 JSON 对象映射到方法参数）
     */
    private Object[] resolveMethodArgs(java.lang.reflect.Method method, JSONObject args) {
        java.lang.reflect.Parameter[] parameters = method.getParameters();
        Object[] methodArgs = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            String paramName = parameters[i].getName();
            Class<?> paramType = parameters[i].getType();

            if (args.containsKey(paramName)) {
                methodArgs[i] = convertValue(args.get(paramName), paramType);
            } else if (i < args.size()) {
                methodArgs[i] = convertValue(args.values().toArray()[i], paramType);
            } else {
                methodArgs[i] = null;
            }
        }

        return methodArgs;
    }

    /**
     * 类型转换
     */
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.isInstance(value)) return value;
        if (targetType == Long.class || targetType == long.class) {
            return value instanceof Integer ? ((Integer) value).longValue() : Long.valueOf(value.toString());
        }
        if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf(value.toString());
        }
        if (targetType == String.class) {
            return value.toString();
        }
        return value;
    }
}
