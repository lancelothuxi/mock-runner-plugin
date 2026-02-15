# Debug 和 Run 模式改进

## 改进内容

### 1. 图标区分
- **Run with Mock Runner**: 使用绿色运行图标 (AllIcons.Actions.Execute)
- **Debug with Mock Runner**: 使用红色调试图标 (AllIcons.Actions.StartDebugger)

### 2. 真正的调试功能
创建了两个独立的 Runner：

#### MyProgramRunner (Run 模式)
- 继承 `GenericProgramRunner`
- 只处理 `MyCustomExecutor` (Run 模式)
- 直接执行程序，不启用调试功能

#### MyDebugProgramRunner (Debug 模式)
- 继承 `GenericDebuggerRunner`
- 只处理 `MyDebugExecutor` (Debug 模式)
- 调用 `super.doExecute()` 启动真正的调试会话
- 支持断点、单步调试等所有调试功能

### 3. 工作原理

```
用户点击 "Run with Mock Runner"
  ↓
MyCustomExecutor (绿色运行图标)
  ↓
MyProgramRunner.doExecute()
  ↓
添加 Mock Agent → 执行程序 (无调试)

用户点击 "Debug with Mock Runner"
  ↓
MyDebugExecutor (红色调试图标)
  ↓
MyDebugProgramRunner.doExecute()
  ↓
添加 Mock Agent → super.doExecute() → 启动调试会话
```

### 4. 使用方法

1. 在代码中右键点击 main 方法或测试方法
2. 在运行按钮下拉菜单中选择：
   - **Run with Mock Runner** (绿色图标) - 普通运行
   - **Debug with Mock Runner** (红色图标) - 调试运行，可以打断点

### 5. 调试功能
Debug 模式下支持：
- 设置断点
- 单步执行 (Step Over, Step Into, Step Out)
- 查看变量值
- 表达式求值
- 所有标准的 IntelliJ IDEA 调试功能

## 技术细节

### Executor 配置
```java
// Run Executor
MyCustomExecutor.EXECUTOR_ID = "MyCustomExecutor"
Icon = AllIcons.Actions.Execute (绿色)

// Debug Executor  
MyDebugExecutor.EXECUTOR_ID = "MyDebugExecutor"
Icon = AllIcons.Actions.StartDebugger (红色)
```

### Runner 注册
```xml
<programRunner implementation="com.example.plugin.MyProgramRunner"/>
<programRunner implementation="com.example.plugin.MyDebugProgramRunner"/>
```

### 关键代码
Debug Runner 通过调用父类方法启用真正的调试：
```java
// MyDebugProgramRunner.java
return super.doExecute(state, environment);  // 启动调试会话
```

## 测试
1. 启动插件后，打开一个 Java 项目
2. 在 main 方法上右键，查看运行选项
3. 选择 "Run with Mock Runner" - 应该看到绿色图标
4. 选择 "Debug with Mock Runner" - 应该看到红色图标
5. 在代码中设置断点，使用 Debug 模式运行，断点应该生效
