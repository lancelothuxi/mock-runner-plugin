package com.example.plugin.ui;

import com.example.plugin.mock.MockConfig;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock 配置对话框 - 类似 Coverage 界面
 */
public class MockConfigDialog extends DialogWrapper {
    
    private final Project project;
    private final MockConfig mockConfig;
    private JBTable table;
    private DefaultTableModel tableModel;
    
    public MockConfigDialog(Project project, MockConfig mockConfig) {
        super(project);
        this.project = project;
        this.mockConfig = mockConfig;
        setTitle("Mock Configuration");
        init();
    }
    
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 创建表格
        String[] columnNames = {"Enabled", "Class", "Method", "Return Type", "Mock Value"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 4; // 只允许编辑 Enabled 和 Mock Value
            }
        };
        
        table = new JBTable(tableModel);
        table.setRowHeight(25);
        
        // 加载项目中的所有类和方法
        loadProjectMethods();
        
        JBScrollPane scrollPane = new JBScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 添加说明
        JLabel hint = new JLabel("Check methods to mock and enter return values");
        panel.add(hint, BorderLayout.NORTH);
        
        return panel;
    }
    
    private void loadProjectMethods() {
        PsiManager psiManager = PsiManager.getInstance(project);
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
        
        // 获取项目中的所有 Java 类
        PsiClass[] allClasses = javaPsiFacade.findClasses("*", scope);
        
        List<MethodInfo> methods = new ArrayList<>();
        
        for (PsiClass psiClass : allClasses) {
            if (psiClass.getQualifiedName() == null) continue;
            
            String className = psiClass.getQualifiedName();
            
            // 跳过 JDK 类
            if (className.startsWith("java.") || className.startsWith("javax.")) {
                continue;
            }
            
            for (PsiMethod method : psiClass.getMethods()) {
                String methodName = method.getName();
                PsiType returnType = method.getReturnType();
                
                if (returnType != null && !returnType.equals(PsiType.VOID)) {
                    String returnTypeName = returnType.getPresentableText();
                    
                    // 检查是否已有 Mock 规则
                    MockConfig.MockRule existingRule = mockConfig.getMockRule(className, methodName);
                    boolean enabled = existingRule != null && existingRule.isEnabled();
                    String mockValue = existingRule != null ? existingRule.getReturnValue() : "";
                    
                    methods.add(new MethodInfo(enabled, className, methodName, returnTypeName, mockValue));
                }
            }
        }
        
        // 添加到表格
        for (MethodInfo method : methods) {
            tableModel.addRow(new Object[]{
                method.enabled,
                method.className,
                method.methodName,
                method.returnType,
                method.mockValue
            });
        }
    }
    
    @Override
    protected void doOKAction() {
        // 保存配置
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean enabled = (Boolean) tableModel.getValueAt(i, 0);
            String className = (String) tableModel.getValueAt(i, 1);
            String methodName = (String) tableModel.getValueAt(i, 2);
            String returnType = (String) tableModel.getValueAt(i, 3);
            String mockValue = (String) tableModel.getValueAt(i, 4);
            
            if (enabled && mockValue != null && !mockValue.trim().isEmpty()) {
                MockConfig.MockRule rule = new MockConfig.MockRule(mockValue, returnType);
                rule.setEnabled(true);
                mockConfig.addMockRule(className, methodName, rule);
            }
        }
        
        super.doOKAction();
    }
    
    private static class MethodInfo {
        boolean enabled;
        String className;
        String methodName;
        String returnType;
        String mockValue;
        
        MethodInfo(boolean enabled, String className, String methodName, String returnType, String mockValue) {
            this.enabled = enabled;
            this.className = className;
            this.methodName = methodName;
            this.returnType = returnType;
            this.mockValue = mockValue;
        }
    }
}
