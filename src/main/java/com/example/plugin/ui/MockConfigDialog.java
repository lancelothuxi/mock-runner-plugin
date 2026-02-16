package com.example.plugin.ui;

import com.example.plugin.mock.MockConfig;
import com.example.plugin.util.MockValueGenerator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        setSize(1000, 600);
        init();
    }
    
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 创建表格
        String[] columnNames = {"Enabled", "Class", "Method", "Return Type", "Mock Value", "Edit"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class;
                if (column == 5) return JButton.class;
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 4 || column == 5; // Enabled, Mock Value, Edit按钮
            }
        };
        
        table = new JBTable(tableModel);
        table.setRowHeight(30);
        
        // 设置列宽
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // Enabled
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Class
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Method
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Return Type
        table.getColumnModel().getColumn(4).setPreferredWidth(300); // Mock Value
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Edit
        
        // 设置Edit按钮的渲染器和编辑器
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor());
        
        // 加载项目中的所有类和方法
        loadProjectMethods();
        
        JBScrollPane scrollPane = new JBScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(950, 500));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 添加说明和工具栏
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel hint = new JLabel("Check methods to mock, edit return values, and use JSON editor for complex data");
        hint.setBorder(JBUI.Borders.empty(5));
        topPanel.add(hint, BorderLayout.NORTH);
        
        // 工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton generateAllButton = new JButton("Generate Smart Mocks");
        JButton clearAllButton = new JButton("Clear All");
        
        generateAllButton.addActionListener(e -> generateSmartMocks());
        clearAllButton.addActionListener(e -> clearAllMocks());
        
        toolBar.add(generateAllButton);
        toolBar.add(clearAllButton);
        topPanel.add(toolBar, BorderLayout.SOUTH);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
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
            
            // 跳过 JDK 类和测试类
            if (className.startsWith("java.") || className.startsWith("javax.") ||
                className.contains("Test") || className.contains("test")) {
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
                    String mockValue = existingRule != null ? existingRule.getReturnValue() : 
                                     MockValueGenerator.generateMockValue(returnType);
                    
                    methods.add(new MethodInfo(enabled, className, methodName, returnTypeName, mockValue, returnType));
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
                method.mockValue,
                "Edit JSON"
            });
        }
    }
    
    private void generateSmartMocks() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean enabled = (Boolean) tableModel.getValueAt(i, 0);
            if (enabled) {
                String className = (String) tableModel.getValueAt(i, 1);
                String methodName = (String) tableModel.getValueAt(i, 2);
                
                // 重新生成智能mock数据
                PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.projectScope(project));
                if (psiClass != null) {
                    PsiMethod[] methods = psiClass.findMethodsByName(methodName, false);
                    if (methods.length > 0) {
                        PsiType returnType = methods[0].getReturnType();
                        if (returnType != null) {
                            String smartMockValue = MockValueGenerator.generateMockValue(returnType);
                            tableModel.setValueAt(smartMockValue, i, 4);
                        }
                    }
                }
            }
        }
    }
    
    private void clearAllMocks() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(false, i, 0);
            tableModel.setValueAt("", i, 4);
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
    
    // 按钮渲染器
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("JSON Editor");
            return this;
        }
    }
    
    // 按钮编辑器
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int editingRow;
        
        public ButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = "JSON Editor";
            button.setText(label);
            isPushed = true;
            editingRow = row;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // 打开JSON编辑器
                String currentValue = (String) tableModel.getValueAt(editingRow, 4);
                JsonEditorDialog dialog = new JsonEditorDialog(project, currentValue);
                if (dialog.showAndGet()) {
                    String newValue = dialog.getJsonValue();
                    tableModel.setValueAt(newValue, editingRow, 4);
                }
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    private static class MethodInfo {
        boolean enabled;
        String className;
        String methodName;
        String returnType;
        String mockValue;
        PsiType psiReturnType;
        
        MethodInfo(boolean enabled, String className, String methodName, String returnType, String mockValue, PsiType psiReturnType) {
            this.enabled = enabled;
            this.className = className;
            this.methodName = methodName;
            this.returnType = returnType;
            this.mockValue = mockValue;
            this.psiReturnType = psiReturnType;
        }
    }
}
