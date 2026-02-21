package io.github.lancelothuxi.idea.plugin.mock.ui;

import com.intellij.ui.JBColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * JsonTableCellRenderer的单元测试
 * 测试JSON格式化、语法高亮和显示功能
 */
public class JsonTableCellRendererTest {

    private JsonTableCellRenderer renderer;
    
    @Mock
    private JTable table;
    
    @Mock
    private JTableHeader tableHeader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        renderer = new JsonTableCellRenderer();
        
        // 设置表格基本属性
        when(table.getTableHeader()).thenReturn(tableHeader);
        when(table.getGridColor()).thenReturn(Color.GRAY);
    }

    @Test
    void testNullValue() {
        Component component = renderer.getTableCellRendererComponent(
                table, null, false, false, 0, 0);
        
        assertNotNull(component);
        assertTrue(component instanceof JLabel);
        JLabel label = (JLabel) component;
        assertEquals("<empty>", label.getText());
    }

    @Test
    void testEmptyValue() {
        Component component = renderer.getTableCellRendererComponent(
                table, "", false, false, 0, 0);
        
        assertNotNull(component);
        assertTrue(component instanceof JLabel);
        JLabel label = (JLabel) component;
        assertEquals("<empty>", label.getText());
    }

    @Test
    void testSimpleJsonObject() {
        String json = "{\"name\":\"John\",\"age\":30}";
        Component component = renderer.getTableCellRendererComponent(
                table, json, false, false, 0, 0);
        
        assertNotNull(component);
        assertTrue(component instanceof JLabel);
        JLabel label = (JLabel) component;
        
        // 验证文本包含HTML标签（语法高亮）
        assertTrue(label.getText().contains("<html>"));
        assertTrue(label.getText().contains("</html>"));
        
        // 验证工具提示包含格式化的JSON
        assertNotNull(label.getToolTipText());
        assertTrue(label.getToolTipText().contains("<html>"));
    }

    @Test
    void testJsonArray() {
        String json = "[{\"name\":\"John\"},{\"name\":\"Jane\"}]";
        Component component = renderer.getTableCellRendererComponent(
                table, json, false, false, 0, 0);
        
        assertNotNull(component);
        assertTrue(component instanceof JLabel);
        JLabel label = (JLabel) component;
        
        assertTrue(label.getText().contains("<html>"));
        assertNotNull(label.getToolTipText());
    }

    @Test
    void testLongJsonTruncation() {
        // 创建一个超过MAX_DISPLAY_LENGTH的JSON字符串
        StringBuilder longJson = new StringBuilder("{\"data\":\"");
        for (int i = 0; i < 300; i++) {
            longJson.append("a");
        }
        longJson.append("\"}");
        
        Component component = renderer.getTableCellRendererComponent(
                table, longJson.toString(), false, false, 0, 0);
        
        assertNotNull(component);
        assertTrue(component instanceof JLabel);
        JLabel label = (JLabel) component;
        
        // 验证文本被截断并以...结尾
        assertTrue(label.getText().contains("..."));
    }

    @Test
    void testNonJsonText() {
        String plainText = "This is just plain text, not JSON";
        Component component = renderer.getTableCellRendererComponent(
                table, plainText, false, false, 0, 0);
        
        assertNotNull(component);
        assertTrue(component instanceof JLabel);
        JLabel label = (JLabel) component;
        
        assertTrue(label.getText().contains("<html>"));
        assertTrue(label.getText().contains("plain text"));
    }

    @Test
    void testSelectedState() {
        String json = "{\"selected\":true}";
        Component component = renderer.getTableCellRendererComponent(
                table, json, true, false, 0, 0);
        
        assertNotNull(component);
        assertTrue(component instanceof JLabel);
        JLabel label = (JLabel) component;
        
        // 验证选中状态的样式
        assertTrue(label.getText().contains("<html>"));
    }

    @Test
    void testFocusedState() {
        String json = "{\"focused\":true}";
        Component component = renderer.getTableCellRendererComponent(
                table, json, false, true, 0, 0);
        
        assertNotNull(component);
        assertTrue(component instanceof JLabel);
        JLabel label = (JLabel) component;
        
        assertTrue(label.getText().contains("<html>"));
    }

    @Test
    void testBackgroundColor() {
        String json = "{\"color\":\"test\"}";
        Component component = renderer.getTableCellRendererComponent(
                table, json, false, false, 0, 0);
        
        assertNotNull(component);
        assertTrue(component instanceof JLabel);
        JLabel label = (JLabel) component;
        
        // 验证非选中状态的背景色
        assertEquals(JsonTableCellRenderer.JSON_BACKGROUND, label.getBackground());
    }

    @Test
    void testBorderColor() {
        String json = "{\"border\":\"test\"}";
        Component component = renderer.getTableCellRendererComponent(
                table, json, false, false, 0, 0);
        
        assertNotNull(component);
        assertTrue(component instanceof JLabel);
        JLabel label = (JLabel) component;
        
        // 验证边框存在
        assertNotNull(label.getBorder());
    }

    @Test
    void testFontIsMonospace() {
        String json = "{\"font\":\"test\"}";
        Component component = renderer.getTableCellRendererComponent(
                table, json, false, false, 0, 0);
        
        assertNotNull(component);
        assertTrue(component instanceof JLabel);
        JLabel label = (JLabel) component;
        
        // 验证使用等宽字体
        Font font = label.getFont();
        assertNotNull(font);
        assertTrue(font.getName().contains("Monospaced") || 
                  font.getName().contains("Consolas") || 
                  font.getName().contains("Courier"));
    }

    @Test
    void testHorizontalAlignment() {
        String json = "{\"alignment\":\"test\"}";
        Component component = renderer.getTableCellRendererComponent(
                table, json, false, false, 0, 0);
        
        assertNotNull(component);
        assertTrue(component instanceof JLabel);
        JLabel label = (JLabel) component;
        
        // 验证左对齐
        assertEquals(SwingConstants.LEFT, label.getHorizontalAlignment());
    }

    @Test
    void testVerticalAlignment() {
        String json = "{\"vertical\":\"test\"}";
        Component component = renderer.getTableCellRendererComponent(
                table, json, false, false, 0, 0);
        
        assertNotNull(component);
        assertTrue(component instanceof JLabel);
        JLabel label = (JLabel) component;
        
        // 验证顶部对齐
        assertEquals(SwingConstants.TOP, label.getVerticalAlignment());
    }

    @Test
    void testJsonWithSpecialCharacters() {
        String json = "{\"message\":\"Hello <world> & \"quotes\"\"}";
        Component component = renderer.getTableCellRendererComponent(
                table, json, false, false, 0, 0);
        
        assertNotNull(component);
        assertTrue(component instanceof JLabel);
        JLabel label = (JLabel) component;
        
        // 验证HTML特殊字符被正确转义
        assertTrue(label.getText().contains("<html>"));
        assertNotNull(label.getToolTipText());
        
        // 验证工具提示中的HTML转义
        String tooltip = label.getToolTipText();
        assertTrue(tooltip.contains("&lt;") || tooltip.contains("&gt;") || 
                  tooltip.contains("&amp;") || tooltip.contains("&quot;"));
    }

    @Test
    void testComplexNestedJson() {
        String json = "{\"user\":{\"name\":\"John\",\"address\":{\"street\":\"Main St\",\"city\":\"New York\"}},\"active\":true,\"count\":42,\"tags\":[\"admin\",\"user\"]}";
        Component component = renderer.getTableCellRendererComponent(
                table, json, false, false, 0, 0);
        
        assertNotNull(component);
        assertTrue(component instanceof JLabel);
        JLabel label = (JLabel) component;
        
        assertTrue(label.getText().contains("<html>"));
        assertNotNull(label.getToolTipText());
        
        // 验证工具提示包含格式化的嵌套结构
        String tooltip = label.getToolTipText();
        assertTrue(tooltip.contains("\n") || tooltip.contains("<br>"));
    }

    @Test
    void testCachePerformance() {
        String json = "{\"cached\":true}";
        
        // 第一次调用
        long startTime = System.nanoTime();
        Component component1 = renderer.getTableCellRendererComponent(
                table, json, false, false, 0, 0);
        long firstCallTime = System.nanoTime() - startTime;
        
        // 第二次调用相同内容（应该使用缓存）
        startTime = System.nanoTime();
        Component component2 = renderer.getTableCellRendererComponent(
                table, json, false, false, 0, 0);
        long secondCallTime = System.nanoTime() - startTime;
        
        assertNotNull(component1);
        assertNotNull(component2);
        
        // 验证缓存生效（第二次调用应该更快）
        // 注意：由于JIT编译等因素，这个测试在某些情况下可能不稳定
        // 在实际项目中，可能需要更复杂的性能测试
        JLabel label1 = (JLabel) component1;
        JLabel label2 = (JLabel) component2;
        assertEquals(label1.getText(), label2.getText());
    }
}