-- AI 助理功能数据库表
-- 在 sky_take_out 数据库中执行

CREATE TABLE IF NOT EXISTS ai_conversation (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    employee_id   BIGINT NOT NULL COMMENT '所属员工ID',
    title         VARCHAR(200) COMMENT '对话标题（自动生成/手动修改）',
    model         VARCHAR(50) DEFAULT 'deepseek-chat' COMMENT '使用的模型名',
    message_count INT DEFAULT 0 COMMENT '消息数量',
    create_time   DATETIME NOT NULL COMMENT '创建时间',
    update_time   DATETIME NOT NULL COMMENT '最后更新时间',
    INDEX idx_employee_id (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 助理对话表';

CREATE TABLE IF NOT EXISTS ai_message (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    conversation_id BIGINT NOT NULL COMMENT '所属对话ID',
    role            VARCHAR(20) NOT NULL COMMENT 'system/user/assistant/tool',
    content         TEXT NOT NULL COMMENT '消息内容',
    tool_name       VARCHAR(100) COMMENT '工具名称（role=tool时有值）',
    create_time     DATETIME NOT NULL COMMENT '创建时间',
    INDEX idx_conv_id (conversation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 助理消息表';
