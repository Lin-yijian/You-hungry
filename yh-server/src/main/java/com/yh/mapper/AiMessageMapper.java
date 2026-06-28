package com.yh.mapper;

import com.yh.entity.AiMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 消息 Mapper
 */
@Mapper
public interface AiMessageMapper {

    /**
     * 插入消息
     */
    void insert(AiMessage message);

    /**
     * 批量插入消息
     */
    void insertBatch(List<AiMessage> messages);

    /**
     * 根据对话ID查询所有消息，按时间升序
     */
    List<AiMessage> getByConversationId(Long conversationId);

    /**
     * 根据对话ID查询最近N条消息
     */
    List<AiMessage> getRecentByConversationId(Long conversationId, Integer limit);

    /**
     * 删除对话下的所有消息
     */
    void deleteByConversationId(Long conversationId);
}
