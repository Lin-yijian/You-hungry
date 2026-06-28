package com.yh.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 助理消息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息角色常量
     */
    public static final String ROLE_SYSTEM = "system";
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";
    public static final String ROLE_TOOL = "tool";

    private Long id;

    //所属对话ID
    private Long conversationId;

    //消息角色 system/user/assistant/tool
    private String role;

    //消息内容
    private String content;

    //工具名称（role=tool时有值）
    private String toolName;

    //创建时间
    private LocalDateTime createTime;
}
