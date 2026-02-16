package com.example.plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * 表格中JSON编辑的单元格编辑器
 */
public class JsonTableCellEditor extends AbstractCellEditor implements TableCellEditor {
    
    private final Project project;
    private final JButton editorButton;
    private String currentValue;
    private JBPopup popup;
    private InlineJsonEditor jsonEditor;
    
    public JsonTableCellEditor(Project project) {
        this.project = project;
        this.editorButton = new JButton();
        this.editorButton.setBorderPainted(false);
        this.editorButton.setContentAreaFilled(false);
        this.editorButton.setFocusPainted(false);
        this.editorButton.setHorizontalAlignment(SwingConstants.LEFT);
        this.editorButton.setFont(this.editorButton.getFont().deriveFont(Font.PLAIN, 12f));
        
        this.editorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showJsonEditor();
            }
        });
    }
    
    private JTable currentTable;
    private int currentRow;
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.currentTable = table;
        this.currentRow = row;
        currentValue = value != null ? value.toString() : "{}";
        
        // 显示截断的值
        String displayValue = truncateValue(currentValue);
        editorButton.setText(displayValue);
        editorButton.setToolTipText("Click to edit: " + currentValue);
        
        return editorButton;
    }
    
    @Override
    public Object getCellEditorValue() {
        return currentValue;
    }
    
    @Override
    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            return ((MouseEvent) e).getClickCount() >= 1;
        }
        return true;
    }
    
    private void showJsonEditor() {
        if (popup != null && !popup.isDisposed()) {
            popup.cancel();
        }
        
        // 检查是否是异常模式（通过检查Mode列的值）
        boolean isExceptionMode = false;
        if (currentTable != null && currentRow >= 0) {
            Object modeValue = currentTable.getValueAt(currentRow, 4); // Mode列是第4列
            isExceptionMode = "Exception".equals(modeValue);
        }
        
        if (isExceptionMode) {
            showExceptionEditor();
        } else {
            showJsonValueEditor();
        }
    }
    
    private void showJsonValueEditor() {
        // 创建编辑器面板
        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.setBorder(JBUI.Borders.empty(10));
        editorPanel.setPreferredSize(new Dimension(500, 300));
        
        // 标题
        JLabel titleLabel = new JLabel("Edit JSON Value");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        titleLabel.setBorder(JBUI.Borders.empty(0, 0, 10, 0));
        editorPanel.add(titleLabel, BorderLayout.NORTH);
        
        // JSON编辑器
        jsonEditor = InlineJsonEditor.createLarge(project, currentValue);
        editorPanel.add(jsonEditor, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(JBUI.Borders.empty(10, 0, 0, 0));
        
        JButton cancelButton = new JButton("Cancel");
        JButton okButton = new JButton("OK");
        
        cancelButton.addActionListener(e -> {
            if (popup != null) {
                popup.cancel();
            }
            fireEditingCanceled();
        });
        
        okButton.addActionListener(e -> {
            if (jsonEditor.isValidJson()) {
                currentValue = jsonEditor.getJsonValue();
                if (popup != null) {
                    popup.cancel();
                }
                fireEditingStopped();
            } else {
                JOptionPane.showMessageDialog(
                    editorPanel,
                    "Please fix JSON syntax errors before saving.",
                    "Invalid JSON",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        editorPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 创建弹出窗口
        popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(editorPanel, jsonEditor)
            .setTitle("JSON Editor")
            .setResizable(true)
            .setMovable(true)
            .setRequestFocus(true)
            .setFocusable(true)
            .createPopup();
        
        // 显示在按钮附近
        popup.show(new RelativePoint(editorButton, new Point(0, editorButton.getHeight())));
    }
    
    private void showExceptionEditor() {
        // 解析当前值: "ExceptionType: message"
        String exceptionType = "java.lang.RuntimeException";
        String exceptionMessage = "Mocked exception";
        
        if (currentValue != null && currentValue.contains(":")) {
            String[] parts = currentValue.split(":", 2);
            exceptionType = parts[0].trim();
            exceptionMessage = parts.length > 1 ? parts[1].trim() : "Mocked exception";
        }
        
        // 创建编辑器面板
        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.setBorder(JBUI.Borders.empty(10));
        editorPanel.setPreferredSize(new Dimension(500, 200));
        
        // 标题
        JLabel titleLabel = new JLabel("Edit Exception Configuration");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        titleLabel.setBorder(JBUI.Borders.empty(0, 0, 10, 0));
        editorPanel.add(titleLabel, BorderLayout.NORTH);
        
        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = JBUI.insets(5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Exception Type
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Exception Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField exceptionTypeField = new JTextField(exceptionType);
        formPanel.add(exceptionTypeField, gbc);
        
        // Exception Message
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Exception Message:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField exceptionMessageField = new JTextField(exceptionMessage);
        formPanel.add(exceptionMessageField, gbc);
        
        editorPanel.add(formPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(JBUI.Borders.empty(10, 0, 0, 0));
        
        JButton cancelButton = new JButton("Cancel");
        JButton okButton = new JButton("OK");
        
        cancelButton.addActionListener(e -> {
            if (popup != null) {
                popup.cancel();
            }
            fireEditingCanceled();
        });
        
        okButton.addActionListener(e -> {
            String type = exceptionTypeField.getText().trim();
            String message = exceptionMessageField.getText().trim();
            
            if (type.isEmpty()) {
                JOptionPane.showMessageDialog(
                    editorPanel,
                    "Exception type cannot be empty.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            currentValue = type + ": " + message;
            if (popup != null) {
                popup.cancel();
            }
            fireEditingStopped();
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        editorPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 创建弹出窗口
        popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(editorPanel, exceptionTypeField)
            .setTitle("Exception Editor")
            .setResizable(true)
            .setMovable(true)
            .setRequestFocus(true)
            .setFocusable(true)
            .createPopup();
        
        // 显示在按钮附近
        popup.show(new RelativePoint(editorButton, new Point(0, editorButton.getHeight())));
    }
    
    private String truncateValue(String value) {
        if (value == null) return "";
        
        // 移除换行符和多余空格
        String cleaned = value.replaceAll("\\s+", " ").trim();
        
        // 截断长文本
        if (cleaned.length() > 50) {
            return cleaned.substring(0, 47) + "...";
        }
        
        return cleaned;
    }
    
    @Override
    public boolean stopCellEditing() {
        if (popup != null && !popup.isDisposed()) {
            popup.cancel();
        }
        if (jsonEditor != null) {
            jsonEditor.disposeEditor();
        }
        return super.stopCellEditing();
    }
    
    @Override
    public void cancelCellEditing() {
        if (popup != null && !popup.isDisposed()) {
            popup.cancel();
        }
        if (jsonEditor != null) {
            jsonEditor.disposeEditor();
        }
        super.cancelCellEditing();
    }
}