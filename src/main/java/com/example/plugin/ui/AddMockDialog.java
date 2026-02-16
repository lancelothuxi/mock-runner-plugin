package com.example.plugin.ui;

import com.example.plugin.util.MockValueGenerator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * 添加Mock配置的对话框，使用内联JSON编辑器
 */
public class AddMockDialog extends DialogWrapper {
    
    private final Project project;
    private final String className;
    private final String methodName;
    private final String signature;
    private final PsiType returnType;
    private InlineJsonEditor jsonEditor;
    
    public AddMockDialog(Project project, String className, String methodName, String signature, PsiType returnType) {
        super(project);
        this.project = project;
        this.className = className;
        this.methodName = methodName;
        this.signature = signature;
        this.returnType = returnType;
        
        setTitle("Add Mock Configuration");
        setSize(600, 400);
        init();
    }
    
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(JBUI.Borders.empty(10));
        
        // 方法信息面板
        JPanel infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        // JSON编辑器面板
        JPanel editorPanel = createEditorPanel();
        mainPanel.add(editorPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(JBUI.Borders.customLine(Color.LIGHT_GRAY, 0, 0, 1, 0));
        panel.setBackground(UIManager.getColor("Panel.background"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = JBUI.insets(5);
        
        // 类名
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JBLabel("Class:"), gbc);
        gbc.gridx = 1;
        JBLabel classLabel = new JBLabel(className);
        classLabel.setFont(classLabel.getFont().deriveFont(Font.BOLD));
        panel.add(classLabel, gbc);
        
        // 方法名
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JBLabel("Method:"), gbc);
        gbc.gridx = 1;
        JBLabel methodLabel = new JBLabel(methodName + signature);
        methodLabel.setFont(methodLabel.getFont().deriveFont(Font.BOLD));
        panel.add(methodLabel, gbc);
        
        // 返回类型
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JBLabel("Return Type:"), gbc);
        gbc.gridx = 1;
        String returnTypeText = returnType != null ? returnType.getPresentableText() : "void";
        JBLabel returnTypeLabel = new JBLabel(returnTypeText);
        returnTypeLabel.setFont(returnTypeLabel.getFont().deriveFont(Font.BOLD));
        returnTypeLabel.setForeground(UIManager.getColor("Component.infoForeground"));
        panel.add(returnTypeLabel, gbc);
        
        return panel;
    }
    
    private JPanel createEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(JBUI.Borders.empty(10, 0, 0, 0));
        
        // 标题
        JBLabel titleLabel = new JBLabel("Mock Return Value (JSON):");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // 生成智能默认值
        String smartValue = MockValueGenerator.generateMockValue(returnType);
        
        // 创建JSON编辑器
        jsonEditor = InlineJsonEditor.createLarge(project, smartValue);
        
        JPanel editorWrapper = new JPanel(new BorderLayout());
        editorWrapper.setBorder(JBUI.Borders.empty(10, 0, 0, 0));
        editorWrapper.add(jsonEditor, BorderLayout.CENTER);
        
        panel.add(editorWrapper, BorderLayout.CENTER);
        
        // 提示信息
        JBLabel hintLabel = new JBLabel("<html><i>The editor provides syntax highlighting, auto-formatting, and validation.<br/>" +
                                       "Use the Format button to beautify your JSON, or Validate to check syntax.</i></html>");
        hintLabel.setForeground(UIManager.getColor("Component.infoForeground"));
        hintLabel.setBorder(JBUI.Borders.empty(10, 0, 0, 0));
        panel.add(hintLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    public String getMockValue() {
        return jsonEditor != null ? jsonEditor.getJsonValue() : "{}";
    }
    
    @Override
    protected void doOKAction() {
        if (jsonEditor != null && !jsonEditor.isValidJson()) {
            // 显示错误消息但不关闭对话框
            JOptionPane.showMessageDialog(
                getContentPanel(),
                "Please fix the JSON syntax errors before proceeding.",
                "Invalid JSON",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        super.doOKAction();
    }
    
    @Override
    protected void dispose() {
        if (jsonEditor != null) {
            jsonEditor.disposeEditor();
        }
        super.dispose();
    }
}