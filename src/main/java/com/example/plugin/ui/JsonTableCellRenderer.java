package com.example.plugin.ui;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * JSON表格单元格渲染器
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
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                   boolean hasFocus, int row, int column) {
        
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            
            String text = value != null ? value.toString() : "";
            String displayText = formatJsonForDisplay(text);
            
            label.setText(displayText);
            label.setToolTipText("<html><pre>" + escapeHtml(text) + "</pre></html>");
            
            // 设置样式
            if (!isSelected) {
                label.setBackground(JSON_BACKGROUND);
                label.setBorder(JBUI.Borders.customLine(JSON_BORDER));
            }
            
            label.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setVerticalAlignment(SwingConstants.TOP);
        }
        
        return component;
    }
    
    private String formatJsonForDisplay(String json) {
        if (json == null || json.trim().isEmpty()) {
            return "<empty>";
        }
        
        // 移除多余的空白字符
        String cleaned = json.replaceAll("\\s+", " ").trim();
        
        // 截断长文本
        if (cleaned.length() > 60) {
            return cleaned.substring(0, 57) + "...";
        }
        
        return cleaned;
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
}