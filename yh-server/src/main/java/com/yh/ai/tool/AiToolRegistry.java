package com.yh.ai.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 工具注册中心
 * 扫描所有 @AiTool 注解的方法，构建工具列表供 LLM 调用
 */
@Component
@Slf4j
public class AiToolRegistry implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    //工具名称 → 工具定义
    private final Map<String, ToolDefinition> tools = new LinkedHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        log.info("开始扫描 AI 工具...");
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            for (Method method : bean.getClass().getDeclaredMethods()) {
                AiTool annotation = method.getAnnotation(AiTool.class);
                if (annotation != null) {
                    ToolDefinition def = new ToolDefinition();
                    def.setName(annotation.name());
                    def.setDescription(annotation.description());
                    def.setParameters(annotation.parameters());
                    def.setBean(bean);
                    def.setMethod(method);
                    tools.put(annotation.name(), def);
                    log.info("注册 AI 工具: {} -> {}.{}",
                            annotation.name(), bean.getClass().getSimpleName(), method.getName());
                }
            }
        }
        log.info("AI 工具扫描完成，共 {} 个工具", tools.size());
    }

    /**
     * 获取所有工具定义
     */
    public List<ToolDefinition> getAllTools() {
        return new ArrayList<>(tools.values());
    }

    /**
     * 根据工具名获取工具定义
     */
    public ToolDefinition getTool(String name) {
        return tools.get(name);
    }

    /**
     * 构建 LLM 工具列表（JSON 格式）
     */
    public List<Map<String, Object>> buildToolListForLLM() {
        List<Map<String, Object>> toolList = new ArrayList<>();
        for (ToolDefinition def : tools.values()) {
            Map<String, Object> tool = new LinkedHashMap<>();
            tool.put("type", "function");
            Map<String, Object> function = new LinkedHashMap<>();
            function.put("name", def.getName());
            function.put("description", def.getDescription());
            // parameters 是 JSON 字符串，需要解析为对象
            function.put("parameters", parseParameters(def.getParameters()));
            tool.put("function", function);
            toolList.add(tool);
        }
        return toolList;
    }

    /**
     * 解析 JSON 参数字符串为 Map
     */
    private Map<String, Object> parseParameters(String paramsJson) {
        try {
            return new com.alibaba.fastjson.JSONObject(
                    com.alibaba.fastjson.JSON.parseObject(paramsJson)
            );
        } catch (Exception e) {
            log.error("解析工具参数失败: {}", paramsJson, e);
            Map<String, Object> fallback = new LinkedHashMap<>();
            fallback.put("type", "object");
            fallback.put("properties", new LinkedHashMap<>());
            return fallback;
        }
    }
}
