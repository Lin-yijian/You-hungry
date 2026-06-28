package com.yh.ai.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yh.ai.service.AiAssistantService;
import com.yh.context.BaseContext;
import com.yh.dto.AiChatRequestDTO;
import com.yh.dto.AiConversationPageQueryDTO;
import com.yh.entity.AiConversation;
import com.yh.entity.AiMessage;
import com.yh.entity.Employee;
import com.yh.mapper.AiConversationMapper;
import com.yh.mapper.AiMessageMapper;
import com.yh.result.PageResult;
import com.yh.result.Result;
import com.yh.service.EmployeeService;
import com.yh.vo.AiConversationVO;
import com.yh.vo.AiMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 助理控制器
 */
@RestController
@RequestMapping("/admin/ai")
@Slf4j
public class AiAssistantController {

    @Autowired
    private AiAssistantService aiAssistantService;

    @Autowired
    private AiConversationMapper conversationMapper;

    @Autowired
    private AiMessageMapper messageMapper;

    @Autowired
    private EmployeeService employeeService;

    /**
     * 聊天接口（SSE 流式返回）
     */
    @PostMapping("/chat")
    public SseEmitter chat(@RequestBody AiChatRequestDTO request) {
        Long employeeId = BaseContext.getCurrentId();
        Employee employee = employeeService.getById(employeeId);
        String employeeName = employee != null ? employee.getName() : "未知用户";
        log.info("AI 聊天请求: employeeId={}, name={}, message={}", employeeId, employeeName, request.getMessage());
        return aiAssistantService.chat(request, employeeId, employeeName);
    }

    /**
     * 分页查询对话列表
     */
    @GetMapping("/conversations")
    public Result<PageResult> conversations(AiConversationPageQueryDTO queryDTO) {
        Long employeeId = BaseContext.getCurrentId();
        log.info("查询对话列表: employeeId={}, page={}, pageSize={}",
                employeeId, queryDTO.getPage(), queryDTO.getPageSize());

        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        AiConversation query = AiConversation.builder().employeeId(employeeId).build();
        List<AiConversation> list = conversationMapper.pageQuery(query);
        Page<AiConversation> page = (Page<AiConversation>) list;

        List<AiConversationVO> voList = list.stream().map(conv -> {
            AiConversationVO vo = new AiConversationVO();
            BeanUtils.copyProperties(conv, vo);
            return vo;
        }).collect(Collectors.toList());

        return Result.success(new PageResult(page.getTotal(), voList));
    }

    /**
     * 获取对话详情（含全部消息）
     */
    @GetMapping("/conversations/{id}")
    public Result<AiConversationVO> getConversation(@PathVariable Long id) {
        Long employeeId = BaseContext.getCurrentId();
        log.info("查询对话详情: id={}", id);

        AiConversation conv = conversationMapper.getById(id);
        if (conv == null || !conv.getEmployeeId().equals(employeeId)) {
            return Result.error("对话不存在");
        }

        List<AiMessage> messages = messageMapper.getByConversationId(id);
        List<AiMessageVO> messageVOs = messages.stream().map(msg -> {
            AiMessageVO vo = new AiMessageVO();
            BeanUtils.copyProperties(msg, vo);
            return vo;
        }).collect(Collectors.toList());

        AiConversationVO vo = new AiConversationVO();
        BeanUtils.copyProperties(conv, vo);
        vo.setMessages(messageVOs);

        return Result.success(vo);
    }

    /**
     * 删除对话
     */
    @DeleteMapping("/conversations/{id}")
    public Result deleteConversation(@PathVariable Long id) {
        Long employeeId = BaseContext.getCurrentId();
        log.info("删除对话: id={}", id);

        AiConversation conv = conversationMapper.getById(id);
        if (conv == null || !conv.getEmployeeId().equals(employeeId)) {
            return Result.error("对话不存在");
        }

        messageMapper.deleteByConversationId(id);
        conversationMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 重命名对话
     */
    @PutMapping("/conversations/{id}/title")
    public Result renameConversation(@PathVariable Long id, @RequestBody AiConversationVO request) {
        Long employeeId = BaseContext.getCurrentId();
        log.info("重命名对话: id={}, title={}", id, request.getTitle());

        AiConversation conv = conversationMapper.getById(id);
        if (conv == null || !conv.getEmployeeId().equals(employeeId)) {
            return Result.error("对话不存在");
        }

        conv.setTitle(request.getTitle());
        conversationMapper.update(conv);
        return Result.success();
    }
}
