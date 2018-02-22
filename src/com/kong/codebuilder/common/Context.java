package com.kong.codebuilder.common;

import com.intellij.openapi.project.Project;

import java.util.HashMap;

/**
 * 消息上下文
 */
public class Context {
    public static Project project;

    public static HashMap<String, Object> paramMap = new HashMap<>();

    /**
     * 设置属性
     *
     * @param name
     * @param value
     */
    public static void addAttribute(String name, Object value) {
        paramMap.put(name, value);
    }

    /**
     * 获取属性
     *
     * @param name
     * @return
     */
    public static Object getAttribute(String name) {
        return paramMap.get(name);
    }
}
