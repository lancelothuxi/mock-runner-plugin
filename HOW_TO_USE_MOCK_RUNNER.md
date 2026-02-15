# 如何使用 Mock Runner

## 重要提示
Mock 功能只有在使用 "Run with Mock Runner" 或 "Debug with Mock Runner" 时才会生效！

## 正确的使用步骤

### 1. 添加 Mock 配置
1. 打开右侧的 "My Runner" ToolWindow
2. 点击 "Add Mock" 按钮
3. 填写 Mock 配置：
   - Class: `com.student.StudentService`
   - Method: `getAllStudents`
   - Return Type: `java.util.List`
   - Return Value: `[]`
   - 勾选 Enabled

### 2. 使用 Mock Runner 运行（关键步骤！）

#### 方法 1：通过运行按钮下拉菜单
1. 点击 IDEA 右上角的运行按钮旁边的下拉箭头 ▼
2. 在下拉菜单中找到并选择：
   - **"Run with Mock Runner"** （绿色运行图标）- 普通运行
   - **"Debug with Mock Runner"** （红色调试图标）- 调试运行

#### 方法 2：通过右键菜单
1. 在代码编辑器中右键点击 `main` 方法
2. 在弹出菜单中选择 "Run with Mock Runner" 或 "Debug with Mock Runner"

### 3. 查看日志确认 Mock 生效
在控制台中应该看到：
```
[MockRunner] Running with My Custom Runner!
[MockRunner] Mock config saved to: /tmp/mock-runner/mock-config.json
[MockRunner] Added agent: -javaagent:...
[MockAgent] Starting Mock Agent...
[MockAgent] Loaded 1 mock rules
[MockAgent] Mocking com.student.StudentService.getAllStudents -> []
```

## 常见错误

### ❌ 错误：使用普通的 "Run" 按钮
如果你直接点击绿色的运行按钮，或者选择 "Run 'Main.main()'"，Mock 不会生效！
这是因为普通的 Run 使用的是标准的 Java Runner，不会加载我们的 Mock Agent。

### ✅ 正确：使用 "Run with Mock Runner"
必须明确选择 "Run with Mock Runner" 或 "Debug with Mock Runner"，这样才会使用我们的自定义 Runner，Mock 才会生效。

## 如何确认你使用了正确的 Runner

查看日志文件：
```bash
tail -f build/idea-sandbox/system/log/idea.log | grep "MockRunner"
```

如果看到：
- `[MockRunner] Running with My Custom Runner!` - 说明使用了正确的 Runner
- 没有任何输出 - 说明使用了普通的 Run，需要重新选择 "Run with Mock Runner"

## 调试技巧

1. **确认 Mock 配置已保存**：
   - 查看 My Runner ToolWindow，确认 Mock 配置在列表中
   - 配置会自动保存到 `.idea/mockRunnerConfig.xml`

2. **确认使用了正确的 Runner**：
   - 查看日志中是否有 `[MockRunner]` 开头的消息
   - 如果没有，说明没有使用 Mock Runner

3. **确认 Agent jar 存在**：
   ```bash
   ls -la build/libs/mock-agent-*.jar
   ```

4. **查看完整日志**：
   ```bash
   tail -100 build/idea-sandbox/system/log/idea.log
   ```
