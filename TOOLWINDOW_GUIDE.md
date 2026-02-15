# ToolWindow 功能说明

## 功能概述

插件现在包含一个自定义的 ToolWindow 面板，类似于 "Run with Coverage" 的体验：

1. **底部面板显示**：运行时会在 IDEA 底部显示 "My Runner" 面板
2. **运行记录**：记录每次运行的时间、方法名、状态和持续时间
3. **表格展示**：以表格形式展示所有运行历史
4. **清除功能**：可以清除历史记录

## 使用方式

### 1. 代码旁边的运行按钮

在 main 方法旁边会显示一个运行图标，点击后选择 "Run with My Custom Runner"

### 2. 运行菜单

Run → Run with My Custom Runner

### 3. 快捷键

可以在 Settings → Keymap 中为 "MyCustomRun" 配置快捷键

## ToolWindow 面板

运行后会自动打开底部的 "My Runner" 面板，显示：

| Time     | Method      | Status   | Duration |
|----------|-------------|----------|----------|
| 14:30:25 | Main.main   | Starting | -        |
| 14:31:10 | Test.test   | Running  | 2.5s     |

## 组件说明

### MyRunnerToolWindowFactory
- 创建 ToolWindow 的工厂类
- 在 IDEA 启动时注册面板

### MyRunnerToolWindowContent  
- ToolWindow 的内容组件
- 管理表格数据和UI
- 提供 addResult() 方法添加运行记录

### MyProgramRunner
- 运行器实现
- 在运行时自动显示 ToolWindow
- 记录运行信息到面板

## 自定义扩展

### 修改面板位置

在 plugin.xml 中修改 anchor 属性：
```xml
<toolWindow id="My Runner" 
            anchor="bottom"  <!-- 可选: bottom, left, right, top -->
            .../>
```

### 添加更多列

在 MyRunnerToolWindowContent.java 中修改 columnNames 数组

### 修改图标

在 plugin.xml 中修改 icon 属性：
```xml
<toolWindow id="My Runner" 
            icon="AllIcons.Toolwindows.ToolWindowRun"
            .../>
```

## 与 Coverage 的对比

| 特性 | Coverage | My Runner |
|------|----------|-----------|
| 触发方式 | Run with Coverage 按钮 | Run with My Custom Runner 按钮 |
| 面板位置 | 底部 | 底部（可配置） |
| 显示内容 | 代码覆盖率 | 运行历史记录 |
| 交互方式 | 点击查看详情 | 表格展示 |

## 下一步开发

1. 添加进程监听，实时更新运行状态
2. 添加持续时间计算
3. 添加过滤和搜索功能
4. 添加导出功能
5. 集成 Mock 配置的显示
