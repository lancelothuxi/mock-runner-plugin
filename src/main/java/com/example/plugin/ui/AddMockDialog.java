package com.example.plugin.ui;

import com.example.plugin.util.MockValueGenerator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiType;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class AddMockDialog extends DialogWrapper {
    
    private final Project project;
    private final String className;
    private final String methodName;
    private final String signature;
    private final PsiType returnType;
    private InlineJsonEditor jsonEditor;
    private JBCheckBox throwExceptionCheckbox;
    private JBTextField exceptionTypeField;
    private JBTextField exceptionMessageField;
    private JPanel exceptionPanel;
    
    public AddMockDialog(Project project, String className, String methodName, String signature, PsiType returnType) {
        super(project);
        this.project = project;
        this.className = className;
        this.methodName = methodName;
        this.signature = signature;
        this.returnType = returnType;
        
        setTitle("Add Mock Configuration");
        setSize(700, 500);
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
        
        // 中间面板：JSON编辑器 + 异常配置
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        
        // JSON编辑器面板
        JPanel editorPanel = createEditorPanel();
        centerPanel.add(editorPanel, BorderLayout.CENTER);
        
        // 异常配置面板
        exceptionPanel = createExceptionPanel();
        centerPanel.add(exceptionPanel, BorderLayout.SOUTH);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
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
        String returnTypeText = returnType != null ? returnType.getCanonicalText() : "void";
        JBLabel returnTypeLabel = new JBLabel(returnTypeText);
        returnTypeLabel.setForeground(Color.BLUE);
        panel.add(returnTypeLabel, gbc);
        
        return panel;
    }
    
    private JPanel createEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Mock Return Value (JSON)"));
        
        // 生成默认的mock值
        String defaultValue = MockValueGenerator.generateMockValue(returnType);
        
        // 创建JSON编辑器
        jsonEditor = new InlineJsonEditor(project, defaultValue);
        panel.add(jsonEditor, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createExceptionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Exception Configuration (Optional)"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = JBUI.insets(5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Checkbox: Throw Exception
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        throwExceptionCheckbox = new JBCheckBox("Throw exception instead of returning value");
        throwExceptionCheckbox.addActionListener(e -> updateExceptionFieldsState());
        panel.add(throwExceptionCheckbox, gbc);
        
        // Exception Type
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JBLabel("Exception Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        exceptionTypeField = new JBTextField("java.lang.RuntimeException");
        exceptionTypeField.setEnabled(false);
        panel.add(exceptionTypeField, gbc);
        
        // Exception Message
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JBLabel("Exception Message:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        exceptionMessageField = new JBTextField("Mocked exception");
        exceptionMessageField.setEnabled(false);
        panel.add(exceptionMessageField, gbc);
        
        return panel;
    }
    
    private void updateExceptionFieldsState() {
        boolean throwException = throwExceptionCheckbox.isSelected();
        exceptionTypeField.setEnabled(throwException);
        exceptionMessageField.setEnabled(throwException);
        jsonEditor.setEnabled(!throwException);
    }
    
    public String getMockValue() {
        return jsonEditor.getJsonValue();
    }
    
    public boolean isThrowException() {
        return throwExceptionCheckbox.isSelected();
    }
    
    public String getExceptionType() {
        return exceptionTypeField.getText().trim();
    }
    
    public String getExceptionMessage() {
        return exceptionMessageField.getText().trim();
    }
}
