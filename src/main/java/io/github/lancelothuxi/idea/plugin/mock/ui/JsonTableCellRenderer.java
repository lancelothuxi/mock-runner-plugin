package io.github.lancelothuxi.idea.plugin.mock.ui;

import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 增强型JSON表格单元格渲染器
 * 支持JSON格式化、语法高亮和优化的显示效果
 */
public class JsonTableCellRenderer extends DefaultTableCellRenderer {
    
    private static final Color JSON_BACKGROUND = new JBColor(
        new Color(248, 249, 250), // Light theme
        new Color(45, 47, 49)     // Dark theme
    );
    
    private static final Color JSON_BORDER = new JBColor(
        new Color(209, 213, 219), // Light theme
        new Color(75, 77, 79)     // Dark theme
    );
    
    // JSON语法高亮颜色
    private static final Color STRING_COLOR = new JBColor(
        new Color(34, 134, 34),   // Light theme - 绿色
        new Color(152, 195, 121)  // Dark theme - 浅绿色
    );
    
    private static final Color KEY_COLOR = new JBColor(
        new Color(0, 0, 200),     // Light theme - 蓝色
        new Color(86, 156, 214)   // Dark theme - 浅蓝色
    );
    
    private static final Color NUMBER_COLOR = new JBColor(
        new Color(175, 0, 219),   // Light theme - 紫色
        new Color(181, 206, 168)  // Dark theme - 浅紫色
    );
    
    private static final Color BOOLEAN_COLOR = new JBColor(
        new Color(200, 0, 0),     // Light theme - 红色
        new Color(197, 134, 192)  // Dark theme - 粉色
    );
    
    private static final Color NULL_COLOR = new JBColor(
        new Color(128, 128, 128), // Light theme - 灰色
        new Color(92, 99, 112)    // Dark theme - 深灰色
    );
    
    // JSON解析正则表达式
    private static final Pattern JSON_STRING_PATTERN = Pattern.compile("\"([^\"]*)\"");
    private static final Pattern JSON_NUMBER_PATTERN = Pattern.compile("\\b-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?\\b");
    private static final Pattern JSON_BOOLEAN_PATTERN = Pattern.compile("\\b(true|false)\\b");
    private static final Pattern JSON_NULL_PATTERN = Pattern.compile("\\bnull\\b");
    private static final Pattern JSON_KEY_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:");
    
    // 缓存格式化结果以提高性能
    private static final int MAX_CACHE_SIZE = 100;
    private java.util.Map<String, String> formatCache = new java.util.LinkedHashMap<String, String>(MAX_CACHE_SIZE + 1, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(java.util.Map.Entry<String, String> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };
    
    private static final int MAX_DISPLAY_LENGTH = 200;
    private static final int TOOLTIP_MAX_LENGTH = 1000;
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                   boolean hasFocus, int row, int column) {
        
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            
            String text = value != null ? value.toString() : "";
            String displayText = formatJsonForDisplay(text);
            
            // 应用语法高亮
            String highlightedText = applyJsonSyntaxHighlighting(displayText);
            label.setText("<html>" + highlightedText + "</html>");
            
            // 设置工具提示，显示完整的格式化JSON
            String tooltipText = formatJsonForTooltip(text);
            label.setToolTipText("<html><pre style='font-family:monospace;'>" + escapeHtml(tooltipText) + "</pre></html>");
            
            // 设置样式
            if (!isSelected) {
                label.setBackground(JSON_BACKGROUND);
                label.setBorder(JBUI.Borders.customLine(JSON_BORDER));
            }
            
            // 使用默认等宽字体设置
            Font editorFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
            label.setFont(editorFont != null ? editorFont : new Font(Font.MONOSPACED, Font.PLAIN, 12));
            
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setVerticalAlignment(SwingConstants.TOP);
        }
        
        return component;
    }
    
    private String formatJsonForDisplay(String json) {
        if (json == null || json.trim().isEmpty()) {
            return "<empty>";
        }
        
        // 检查缓存
        if (formatCache.containsKey(json)) {
            return formatCache.get(json);
        }
        
        String result;
        
        // 尝试格式化JSON
        try {
            if (isJson(json)) {
                result = minifyJson(json);
            } else {
                // 非JSON文本，简单处理
                result = json.replaceAll("\\s+", " ").trim();
            }
        } catch (Exception e) {
            // JSON解析失败，使用原始文本
            result = json.replaceAll("\\s+", " ").trim();
        }
        
        // 截断长文本
        if (result.length() > MAX_DISPLAY_LENGTH) {
            result = result.substring(0, MAX_DISPLAY_LENGTH - 3) + "...";
        }
        
        // 缓存结果
        formatCache.put(json, result);
        
        return result;
    }
    
    private String formatJsonForTooltip(String json) {
        if (json == null || json.trim().isEmpty()) {
            return "<empty>";
        }
        
        String result;
        
        try {
            if (isJson(json)) {
                result = prettyPrintJson(json);
            } else {
                result = json;
            }
        } catch (Exception e) {
            result = json;
        }
        
        // 限制工具提示长度
        if (result.length() > TOOLTIP_MAX_LENGTH) {
            result = result.substring(0, TOOLTIP_MAX_LENGTH - 3) + "...";
        }
        
        return result;
    }
    
    private String applyJsonSyntaxHighlighting(String text) {
        if (text == null || text.equals("<empty>")) {
            return text;
        }
        
        String result = escapeHtml(text);
        
        // 高亮null值
        Matcher nullMatcher = JSON_NULL_PATTERN.matcher(result);
        result = nullMatcher.replaceAll("<font color=" + colorToHex(NULL_COLOR) + "><b>$0</b></font>");
        
        // 高亮布尔值
        Matcher booleanMatcher = JSON_BOOLEAN_PATTERN.matcher(result);
        result = booleanMatcher.replaceAll("<font color=" + colorToHex(BOOLEAN_COLOR) + "><b>$0</b></font>");
        
        // 高亮数字
        Matcher numberMatcher = JSON_NUMBER_PATTERN.matcher(result);
        result = numberMatcher.replaceAll("<font color=" + colorToHex(NUMBER_COLOR) + "><b>$0</b></font>");
        
        // 高亮字符串（但排除键）
        Matcher keyMatcher = JSON_KEY_PATTERN.matcher(result);
        // 先标记键，避免后续字符串高亮影响
        result = keyMatcher.replaceAll("<font color=" + colorToHex(KEY_COLOR) + "><b>\"$1\"</b></font>:");
        
        // 高亮剩余的字符串值
        Matcher stringMatcher = JSON_STRING_PATTERN.matcher(result);
        result = stringMatcher.replaceAll("<font color=" + colorToHex(STRING_COLOR) + ">\"$1\"</font>");
        
        return result;
    }
    
    private String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
    
    private boolean isJson(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = text.trim();
        return (trimmed.startsWith("{") && trimmed.endsWith("}")) || 
               (trimmed.startsWith("[") && trimmed.endsWith("]"));
    }
    
    private String minifyJson(String json) {
        // 简单的JSON压缩实现
        StringBuilder result = new StringBuilder();
        boolean inString = false;
        boolean escaped = false;
        
        for (char c : json.toCharArray()) {
            if (escaped) {
                result.append(c);
                escaped = false;
                continue;
            }
            
            if (c == '\\') {
                result.append(c);
                escaped = true;
                continue;
            }
            
            if (c == '"') {
                inString = !inString;
                result.append(c);
                continue;
            }
            
            if (inString) {
                result.append(c);
                continue;
            }
            
            // 跳过空白字符
            if (Character.isWhitespace(c)) {
                continue;
            }
            
            result.append(c);
        }
        
        return result.toString();
    }
    
    private String prettyPrintJson(String json) {
        // 简单的JSON美化实现
        StringBuilder result = new StringBuilder();
        boolean inString = false;
        boolean escaped = false;
        int indent = 0;
        
        for (char c : json.toCharArray()) {
            if (escaped) {
                result.append(c);
                escaped = false;
                continue;
            }
            
            if (c == '\\') {
                result.append(c);
                escaped = true;
                continue;
            }
            
            if (c == '"') {
                inString = !inString;
                result.append(c);
                continue;
            }
            
            if (inString) {
                result.append(c);
                continue;
            }
            
            switch (c) {
                case '{':
                case '[':
                    result.append(c).append('\n');
                    indent++;
                    addIndent(result, indent);
                    break;
                case '}':
                case ']':
                    result.append('\n');
                    indent--;
                    addIndent(result, indent);
                    result.append(c);
                    break;
                case ',':
                    result.append(c).append('\n');
                    addIndent(result, indent);
                    break;
                case ':':
                    result.append(c).append(' ');
                    break;
                default:
                    if (!Character.isWhitespace(c)) {
                        result.append(c);
                    }
                    break;
            }
        }
        
        return result.toString();
    }
    
    private void addIndent(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;")
                  .replace(" ", "&nbsp;")
                  .replace("\n", "<br>")
                  .replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    }
}