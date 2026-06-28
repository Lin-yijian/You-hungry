package com.yh.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 助理对话
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiConversation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //所属员工ID
    private Long employeeId;

    //对话标题
    private String title;

    //使用的模型名
    private String model;

    //消息数量
    private Integer messageCount;

    //创建时间
    private LocalDateTime createTime;

    //最后更新时间
    private LocalDateTime updateTime;
}
