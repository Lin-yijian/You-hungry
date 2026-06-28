package com.yh.mapper;

import com.yh.entity.AiConversation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 对话 Mapper
 */
@Mapper
public interface AiConversationMapper {

    /**
     * 插入对话
     */
    void insert(AiConversation conversation);

    /**
     * 更新对话（标题、消息数量、更新时间）
     */
    void update(AiConversation conversation);

    /**
     * 根据ID查询对话
     */
    AiConversation getById(Long id);

    /**
     * 分页查询某个员工的所有对话
     */
    List<AiConversation> pageQuery(AiConversation conversation);

    /**
     * 删除对话
     */
    void deleteById(Long id);
}
