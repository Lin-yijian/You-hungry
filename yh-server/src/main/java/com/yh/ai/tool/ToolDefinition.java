package com.yh.ai.tool;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * 工具定义，包含名称、描述、参数及对应的 Bean 和方法
 */
@Data
public class ToolDefinition {

    //工具名称
    private String name;

    //工具描述
    private String description;

    //参数 JSON Schema
    private String parameters;

    //对应的 Spring Bean
    private Object bean;

    //对应的方法
    private Method method;
}
