package com.yh.ai.tool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AI 工具注解，标记可被 LLM 调用的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AiTool {

    /**
     * 工具名称（LLM function name）
     */
    String name();

    /**
     * 工具描述（供 LLM 理解用途）
     */
    String description();

    /**
     * 参数 JSON Schema
     */
    String parameters();
}
