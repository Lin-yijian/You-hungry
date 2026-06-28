package com.yh.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 对话分页查询 DTO
 */
@Data
public class AiConversationPageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer page;

    private Integer pageSize;
}
