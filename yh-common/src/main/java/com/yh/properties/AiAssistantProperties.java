package com.yh.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 助理配置属性
 */
@Component
@ConfigurationProperties(prefix = "sky.ai")
@Data
public class AiAssistantProperties {

    //LLM 提供商
    private String provider = "deepseek";

    //API Key
    private String apiKey;

    //默认模型
    private String model = "deepseek-chat";

    //API 端点
    private String endpoint = "https://api.deepseek.com/v1/chat/completions";

    //最大 Token 数
    private Integer maxTokens = 4096;

    //温度（越低越确定性）
    private Double temperature = 0.3;

    //超时时间（秒）
    private Integer timeoutSeconds = 120;

    //对话历史消息上限
    private Integer maxHistoryMessages = 40;
}
