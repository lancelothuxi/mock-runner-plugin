package com.example.plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MyRunnerToolWindowContent {
    private final Project project;
    private final JPanel contentPanel;
    private final JBTable mockTable;
    private final DefaultTableModel tableModel;
    private final JLabel statsLabel;
    
    public MyRunnerToolWindowContent(Project project) {
        this.project = project;
        contentPanel = new JPanel(new BorderLayout());
        
        // 创建表格，列：Class, Method, Args, Return Value
        String[] columnNames = {"Class", "Method", "Args", "Return Value"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格只读
            }
        };
        
        mockTable = new JBTable(tableModel);
        mockTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // 设置列宽
        mockTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Class
        mockTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Method
        mockTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Args
        mockTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Return Value
        
        JBScrollPane scrollPane = new JBScrollPane(mockTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 添加工具栏
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> clearResults());
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refresh());
        
        statsLabel = new JLabel("Mock Methods: 0");
        
        toolbarPanel.add(clearButton);
        toolbarPanel.add(refreshButton);
        toolbarPanel.add(Box.createHorizontalStrut(20));
        toolbarPanel.add(statsLabel);
        
        contentPanel.add(toolbarPanel, BorderLayout.NORTH);
    }
    
    public JPanel getContentPanel() {
        return contentPanel;
    }
    
    public void addMockMethod(String className, String methodName, String signature, String returnValue) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("[ToolWindow] addMockMethod called: " + className + "." + methodName);
            
            // 提取简短的类名
            String shortClassName = className.substring(className.lastIndexOf('.') + 1);
            
            // 添加到表格
            tableModel.addRow(new Object[]{
                shortClassName,
                methodName,
                signature,
                returnValue
            });
            
            System.out.println("[ToolWindow] Row added to table. Row count: " + tableModel.getRowCount());
            
            // 更新统计
            statsLabel.setText("Mock Methods: " + tableModel.getRowCount());
            
            // 强制刷新表格
            tableModel.fireTableDataChanged();
            mockTable.repaint();
            contentPanel.revalidate();
            contentPanel.repaint();
        });
    }
    
    public void addResult(String time, String method, String status, String duration) {
        // 运行历史可以添加到另一个标签页或者底部
        // 暂时不实现，专注于 Mock 配置显示
    }
    
    public void addCoverageData(String className, String methodName, int coverage) {
        // 覆盖率数据可以后续添加
    }
    
    public void clearResults() {
        tableModel.setRowCount(0);
        statsLabel.setText("Mock Methods: 0");
    }
    
    public void refresh() {
        tableModel.fireTableDataChanged();
    }
    
    public static MyRunnerToolWindowContent getInstance(Project project) {
        return project.getService(MyRunnerToolWindowContent.class);
    }
}
