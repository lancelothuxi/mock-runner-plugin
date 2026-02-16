package io.github.lancelothuxi.idea.plugin.mock.util;

import com.intellij.openapi.diagnostic.Logger;

/**
 * 统一的日志工具类
 */
public class PluginLogger {
    
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getInstance(clazz);
    }
    
    public static Logger getLogger(String category) {
        return Logger.getInstance(category);
    }
}
