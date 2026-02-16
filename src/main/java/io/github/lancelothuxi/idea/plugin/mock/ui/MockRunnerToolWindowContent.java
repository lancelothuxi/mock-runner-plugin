package io.github.lancelothuxi.idea.plugin.mock.ui;

import io.github.lancelothuxi.idea.plugin.mock.mock.MockConfig;
import io.github.lancelothuxi.idea.plugin.mock.mock.MockMethodConfig;
import io.github.lancelothuxi.idea.plugin.mock.service.MockConfigService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MockRunnerToolWindowContent {
    private final Project project;
    private final JPanel contentPanel;
    private final JBTable mockTable;
    private final MockTableModel tableModel;
    private final JLabel statsLabel;
    private final JBTextField searchField;
    private final TableRowSorter<MockTableModel> sorter;
    
    // 分页相关
    private final int PAGE_SIZE = 20;
    private int currentPage = 0;
    private final JLabel pageLabel;
    private final JButton prevButton;
    private final JButton nextButton;
    
    // 全局enable/disable
    private boolean globalEnabled = true;
    private final JButton globalToggleButton;
    
    public MockRunnerToolWindowContent(Project project) {
        this.project = project;
        contentPanel = new JPanel(new BorderLayout());
        
        // 创建自定义表格模型
        tableModel = new MockTableModel();
        mockTable = new JBTable(tableModel);
        mockTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // 设置排序和过滤
        sorter = new TableRowSorter<>(tableModel);
        mockTable.setRowSorter(sorter);
        
        // 延迟设置列宽，确保表格已经初始化
        SwingUtilities.invokeLater(() -> {
            if (mockTable.getColumnCount() > 0) {
                mockTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // Enabled
                mockTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Class
                mockTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Method
                mockTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Args
                mockTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Mode
                mockTable.getColumnModel().getColumn(5).setPreferredWidth(300); // Value
                
                // 设置Mode列的下拉编辑器
                JComboBox<String> modeCombo = new JComboBox<>(new String[]{"Return Value", "Exception"});
                mockTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(modeCombo));
                
                // 设置Value列的渲染器和编辑器
                mockTable.getColumnModel().getColumn(5).setCellRenderer(new JsonTableCellRenderer());
                mockTable.getColumnModel().getColumn(5).setCellEditor(new JsonTableCellEditor(project));
            }
        });
        
        JBScrollPane scrollPane = new JBScrollPane(mockTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 创建顶部工具栏
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // 搜索栏
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JBTextField(20);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        searchPanel.add(searchField);
        
        // 全局开关
        globalToggleButton = new JButton("Disable All");
        globalToggleButton.addActionListener(e -> toggleGlobalEnabled());
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(globalToggleButton);
        
        topPanel.add(searchPanel, BorderLayout.NORTH);
        
        // 工具栏
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> clearAllWithConfirm());
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refresh());
        
        statsLabel = new JLabel("Mock Methods: 0");
        
        toolbarPanel.add(clearButton);
        toolbarPanel.add(refreshButton);
        toolbarPanel.add(Box.createHorizontalStrut(20));
        toolbarPanel.add(statsLabel);
        
        topPanel.add(toolbarPanel, BorderLayout.SOUTH);
        contentPanel.add(topPanel, BorderLayout.NORTH);
        
        // 分页控制栏
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        pageLabel = new JLabel("Page 1");
        
        prevButton.addActionListener(e -> previousPage());
        nextButton.addActionListener(e -> nextPage());
        
        paginationPanel.add(prevButton);
        paginationPanel.add(Box.createHorizontalStrut(10));
        paginationPanel.add(pageLabel);
        paginationPanel.add(Box.createHorizontalStrut(10));
        paginationPanel.add(nextButton);
        
        contentPanel.add(paginationPanel, BorderLayout.SOUTH);
        
        // 初始化数据
        try {
            loadMockConfigs();
            updatePaginationControls();
        } catch (Exception e) {
            System.err.println("Error initializing MockRunnerToolWindowContent: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public JPanel getContentPanel() {
        return contentPanel;
    }
    
    private void loadMockConfigs() {
        MockConfigService service = MockConfigService.getInstance(project);
        MockConfig config = service.getConfig();
        tableModel.setMockMethods(config.getMockMethods());
        updateStats();
    }
    
    private void filterTable() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
        updateStats();
    }
    
    private void toggleGlobalEnabled() {
        globalEnabled = !globalEnabled;
        globalToggleButton.setText(globalEnabled ? "Disable All" : "Enable All");
        
        MockConfigService service = MockConfigService.getInstance(project);
        MockConfig config = service.getConfig();
        
        for (MockMethodConfig method : config.getMockMethods()) {
            method.setEnabled(globalEnabled);
        }
        
        service.saveConfig();
        tableModel.fireTableDataChanged();
        updateStats();
    }
    
    private void clearAllWithConfirm() {
        int result = Messages.showYesNoDialog(
            project,
            "Are you sure you want to clear all mock configurations?",
            "Confirm Clear All",
            Messages.getQuestionIcon()
        );
        
        if (result == Messages.YES) {
            clearResults();
        }
    }
    
    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            updatePaginationControls();
            tableModel.fireTableDataChanged();
        }
    }
    
    private void nextPage() {
        int totalPages = getTotalPages();
        if (currentPage < totalPages - 1) {
            currentPage++;
            updatePaginationControls();
            tableModel.fireTableDataChanged();
        }
    }
    
    private int getTotalPages() {
        int totalRows = tableModel.getRowCount();
        return (totalRows + PAGE_SIZE - 1) / PAGE_SIZE;
    }
    
    private void updatePaginationControls() {
        int totalPages = getTotalPages();
        pageLabel.setText("Page " + (currentPage + 1) + " of " + Math.max(1, totalPages));
        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(currentPage < totalPages - 1);
    }
    
    private void updateStats() {
        int total = tableModel.getRowCount();
        int enabled = 0;
        MockConfigService service = MockConfigService.getInstance(project);
        MockConfig config = service.getConfig();
        
        for (MockMethodConfig method : config.getMockMethods()) {
            if (method.isEnabled()) {
                enabled++;
            }
        }
        
        statsLabel.setText("Mock Methods: " + total + " (Enabled: " + enabled + ")");
    }
    
    public void addMockMethod(String className, String methodName, String signature, String returnValue) {
        SwingUtilities.invokeLater(this::refresh);
    }
    
    public void addResult(String time, String method, String status, String duration) {
        // 运行历史可以添加到另一个标签页或者底部
        // 暂时不实现，专注于 Mock 配置显示
    }
    
    public void addCoverageData(String className, String methodName, int coverage) {
        // 覆盖率数据可以后续添加
    }
    
    public void clearResults() {
        MockConfigService service = MockConfigService.getInstance(project);
        service.getConfig().clearAll();
        service.saveConfig();
        
        loadMockConfigs();
        updatePaginationControls();
        updateStats();
    }
    
    public void refresh() {
        loadMockConfigs();
        updatePaginationControls();
        updateStats();
    }
    
    public static MockRunnerToolWindowContent getInstance(Project project) {
        return project.getService(MockRunnerToolWindowContent.class);
    }
    
    // 自定义表格模型
private class MockTableModel extends AbstractTableModel {
            private final String[] columnNames = {"Enabled", "Class", "Method", "Args", "Mode", "Value"};
            private List<MockMethodConfig> mockMethods = new ArrayList<>();

            public void setMockMethods(List<MockMethodConfig> methods) {
                this.mockMethods = new ArrayList<>(methods);
                fireTableDataChanged();
            }

            @Override
            public int getRowCount() {
                return mockMethods.size();
            }

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public String getColumnName(int column) {
                return columnNames[column];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == 0 || columnIndex == 4 || columnIndex == 5; // Enabled, Mode, Value可编辑
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (rowIndex >= mockMethods.size()) {
                    return null;
                }

                MockMethodConfig method = mockMethods.get(rowIndex);
                switch (columnIndex) {
                    case 0: return method.isEnabled();
                    case 1: return method.getClassName().substring(method.getClassName().lastIndexOf('.') + 1);
                    case 2: return method.getMethodName();
                    case 3: return method.getSignature();
                    case 4: return method.isThrowException() ? "Exception" : "Return Value";
                    case 5: 
                        if (method.isThrowException()) {
                            return method.getExceptionType() + ": " + method.getExceptionMessage();
                        } else {
                            return method.getReturnValue();
                        }
                    default: return null;
                }
            }

            @Override
            public void setValueAt(Object value, int rowIndex, int columnIndex) {
                if (rowIndex >= mockMethods.size()) {
                    return;
                }

                MockMethodConfig method = mockMethods.get(rowIndex);

                if (columnIndex == 0) {
                    method.setEnabled((Boolean) value);
                    fireTableCellUpdated(rowIndex, columnIndex);
                    updateStats();
                } else if (columnIndex == 4) {
                    // Toggle mode between Return Value and Exception
                    String mode = value.toString();
                    boolean isException = mode.equals("Exception");
                    method.setThrowException(isException);
                    fireTableDataChanged(); // Refresh entire row to update Value column display
                } else if (columnIndex == 5) {
                    // Update value based on current mode
                    if (method.isThrowException()) {
                        // Parse exception info: "ExceptionType: message"
                        String val = value.toString();
                        if (val.contains(":")) {
                            String[] parts = val.split(":", 2);
                            method.setExceptionType(parts[0].trim());
                            method.setExceptionMessage(parts.length > 1 ? parts[1].trim() : "Mocked exception");
                        } else {
                            method.setExceptionMessage(val);
                        }
                    } else {
                        method.setReturnValue(value.toString());
                    }
                    fireTableCellUpdated(rowIndex, columnIndex);
                }

                // 保存到配置服务
                MockConfigService service = MockConfigService.getInstance(project);
                service.saveConfig();
            }

            public MockMethodConfig getMethodAt(int rowIndex) {
                if (rowIndex >= 0 && rowIndex < mockMethods.size()) {
                    return mockMethods.get(rowIndex);
                }
                return null;
            }
        }
}
