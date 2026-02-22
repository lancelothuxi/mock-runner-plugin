# JsonTableCellRenderer 使用指南

## 概述

`JsonTableCellRenderer` 是一个增强型的JSON表格单元格渲染器，专为IntelliJ IDEA插件设计。它提供了JSON格式化、语法高亮和优化的显示效果。

## 主要功能

### 1. JSON语法高亮
- **字符串值**：绿色（浅色主题）/ 浅绿色（深色主题）
- **JSON键**：蓝色（浅色主题）/ 浅蓝色（深色主题）
- **数字**：紫色（浅色主题）/ 浅紫色（深色主题）
- **布尔值**：红色（浅色主题）/ 粉色（深色主题）
- **null值**：灰色（浅色主题）/ 深灰色（深色主题）

### 2. 智能格式化
- **表格内显示**：压缩格式，节省空间
- **工具提示**：美化格式，便于阅读
- **长文本截断**：超过200字符自动截断并显示"..."

### 3. 性能优化
- **LRU缓存**：缓存格式化结果，提高重复渲染性能
- **智能识别**：自动识别JSON和非JSON文本
- **错误处理**：JSON解析失败时优雅降级

## 使用方法

### 基本使用

```java
// 创建表格
JTable table = new JTable(model);

// 设置JSON渲染器
table.getColumnModel().getColumn(jsonColumnIndex).setCellRenderer(new JsonTableCellRenderer());
```

### 示例数据

```java
// 简单JSON对象
String jsonObject = "{\"name\":\"John\",\"age\":30,\"active\":true}";

// JSON数组
String jsonArray = "[{\"name\":\"John\"},{\"name\":\"Jane\"}]";

// 复杂嵌套JSON
String complexJson = "{\"user\":{\"name\":\"John\",\"address\":{\"street\":\"Main St\"}},\"active\":true,\"count\":42}";
```

## 配置选项

### 显示长度限制

```java
// 修改最大显示长度（默认200）
private static final int MAX_DISPLAY_LENGTH = 200;

// 修改工具提示最大长度（默认1000）
private static final int TOOLTIP_MAX_LENGTH = 1000;
```

### 缓存大小

```java
// 修改缓存大小（默认100）
private static final int MAX_CACHE_SIZE = 100;
```

### 颜色自定义

```java
// 自定义字符串颜色
private static final Color STRING_COLOR = new JBColor(
    new Color(34, 134, 34),   // 浅色主题
    new Color(152, 195, 121)  // 深色主题
);
```

## 最佳实践

### 1. 性能考虑
- 避免在渲染器中进行耗时操作
- 合理设置缓存大小
- 对于大型JSON，考虑分页或懒加载

### 2. 用户体验
- 确保工具提示提供足够的信息
- 考虑为不同类型的JSON提供不同的显示策略
- 添加复制JSON内容的功能

### 3. 错误处理
- 提供有意义的错误信息
- 在JSON解析失败时优雅降级
- 记录解析错误以便调试

## 扩展功能

### 添加复制功能

```java
// 在渲染器中添加右键菜单
label.setComponentPopupMenu(createPopupMenu(json));

private JPopupMenu createPopupMenu(String json) {
    JPopupMenu menu = new JPopupMenu();
    JMenuItem copyItem = new JMenuItem("Copy JSON");
    copyItem.addActionListener(e -> {
        StringSelection selection = new StringSelection(json);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    });
    menu.add(copyItem);
    return menu;
}
```

### 添加搜索功能

```java
// 高亮匹配的文本
private String highlightSearchTerm(String text, String searchTerm) {
    if (searchTerm == null || searchTerm.isEmpty()) {
        return text;
    }
    
    String escapedTerm = Pattern.quote(searchTerm);
    Pattern pattern = Pattern.compile("(" + escapedTerm + ")", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(text);
    
    return matcher.replaceAll("<span style='background-color: yellow;'><b>$1</b></span>");
}
```

## 故障排除

### 常见问题

1. **JSON不显示语法高亮**
   - 检查JSON格式是否正确
   - 确认文本以{或[开头和结尾

2. **性能问题**
   - 增加缓存大小
   - 检查JSON字符串长度
   - 考虑异步处理大型JSON

3. **颜色显示不正确**
   - 检查主题设置
   - 确认JBColor配置正确

### 调试技巧

```java
// 启用调试日志
System.out.println("Original JSON: " + json);
System.out.println("Formatted JSON: " + formattedJson);
System.out.println("Highlighted: " + highlightedText);
```

## 版本历史

- **v1.0.0**：基础JSON渲染功能
- **v2.0.0**：添加语法高亮和格式化
- **v2.1.0**：性能优化和缓存机制

## 贡献

欢迎提交问题报告和功能请求！在提交代码前，请确保：

1. 所有测试通过
2. 代码符合项目风格
3. 添加必要的文档和注释

## 许可证

本项目采用MIT许可证。