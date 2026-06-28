package com.yh.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 消息视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiMessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

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
