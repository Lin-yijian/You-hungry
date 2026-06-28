package com.yh.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 对话视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiConversationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long employeeId;

    //对话标题
    private String title;

    //使用的模型名
    private String model;

    //消息数量
    private Integer messageCount;

    //对话中的消息列表
    private List<AiMessageVO> messages;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
