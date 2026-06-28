package com.yh.ai.service;

import com.yh.dto.AiChatRequestDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI 助理服务接口
 */
public interface AiAssistantService {

    /**
     * 处理聊天消息，通过 SseEmitter 流式返回
     */
    SseEmitter chat(AiChatRequestDTO request, Long employeeId, String employeeName);
}
