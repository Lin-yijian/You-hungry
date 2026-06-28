package com.yh.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 聊天请求 DTO
 */
@Data
public class AiChatRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    //对话ID（可选，为空则新建对话）
    private Long conversationId;

    //用户消息内容
    private String message;
}
