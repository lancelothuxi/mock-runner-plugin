package io.github.lancelothuxi.idea.plugin.mock.demo;

import io.github.lancelothuxi.idea.plugin.mock.ui.JsonTableCellRenderer;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

/**
 * JsonTableCellRenderer使用示例
 * 演示如何在表格中使用JSON渲染器
 */
public class JsonTableCellRendererDemo extends JFrame {
    
    public JsonTableCellRendererDemo() {
        setTitle("JSON表格渲染器演示");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 创建表格模型
        JsonTableModel model = new JsonTableModel();
        
        // 创建表格
        JTable table = new JTable(model);
        
        // 设置JSON渲染器
        table.getColumnModel().getColumn(1).setCellRenderer(new JsonTableCellRenderer());
        
        // 设置列宽
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(600);
        
        // 设置行高
        table.setRowHeight(30);
        
        // 添加滚动面板
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // 添加说明面板
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea infoArea = new JTextArea(
            "使用说明：\n" +
            "1. 将鼠标悬停在JSON内容上查看完整格式化内容\n" +
            "2. JSON会自动应用语法高亮\n" +
            "3. 长JSON内容会自动截断显示\n" +
            "4. 支持浅色和深色主题"
        );
        infoArea.setEditable(false);
        infoArea.setBackground(panel.getBackground());
        infoArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        
        panel.add(infoArea, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * 自定义表格模型，包含JSON数据
     */
    private static class JsonTableModel extends AbstractTableModel {
        private final String[] columnNames = {"描述", "JSON内容"};
        private final Object[][] data = {
            {"简单对象", "{\"name\":\"张三\",\"age\":30,\"active\":true}"},
            {"数组", "[{\"name\":\"张三\"},{\"name\":\"李四\"},{\"name\":\"王五\"}]"},
            {"嵌套对象", "{\"user\":{\"name\":\"张三\",\"address\":{\"street\":\"中山路\",\"city\":\"北京\"}},\"score\":95.5}"},
            {"混合类型", "{\"string\":\"hello\",\"number\":42,\"boolean\":true,\"null\":null,\"array\":[1,2,3]}"},
            {"长JSON", createLongJson()},
            {"特殊字符", "{\"message\":\"Hello <world> & \\\"quotes\\\"\",\"emoji\":\"😀\"}"},
            {"空对象", "{}"},
            {"空数组", "[]"},
            {"非JSON文本", "这不是JSON格式的文本，只是普通字符串"},
            {"包含数字", "{\"price\":99.99,\"count\":100,\"ratio\":0.75,\"scientific\":1.23e-4}"}
        };
        
        private static String createLongJson() {
            StringBuilder sb = new StringBuilder("{\"longText\":\"");
            for (int i = 0; i < 50; i++) {
                sb.append("这是一段很长的文本内容，用于测试截断功能。");
            }
            sb.append("\"}");
            return sb.toString();
        }
        
        @Override
        public int getRowCount() {
            return data.length;
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
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
    
    /**
     * 主方法，启动演示程序
     */
    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 在事件调度线程中创建和显示GUI
        SwingUtilities.invokeLater(() -> {
            JsonTableCellRendererDemo demo = new JsonTableCellRendererDemo();
            demo.setVisible(true);
        });
    }
}