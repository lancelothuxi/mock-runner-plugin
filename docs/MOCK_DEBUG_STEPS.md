# Mock 功能调试步骤

## 当前问题
Mock 配置已添加，但运行时没有生效。需要确认：
1. 是否使用了 "Run with Mock Runner" 而不是普通的 "Run"
2. Runner 的 canRun 方法是否被调用
3. Runner 的 doExecute 方法是否被调用

## 调试步骤

### 1. 重新启动插件
```bash
pkill -f "idea-sandbox"
./gradlew runIde > runide-mock-debug.log 2>&1 &
```

### 2. 在测试项目中操作
1. 打开 student-management 项目
2. 在 My Runner ToolWindow 中添加 Mock：
   - Class: `com.student.StudentService`
   - Method: `getAllStudents`
   - Return Type: `java.util.List`
   - Return Value: `[]` (空列表)
   - 勾选 Enabled

### 3. 运行程序（重要！）
**必须使用自定义的 Runner：**
- 右键点击 Main 类或 main 方法
- 在弹出菜单中选择 "Run with Mock Runner" （绿色图标）
- **不要**选择普通的 "Run 'Main.main()'"

### 4. 查看日志
```bash
# 查看插件日志
tail -f build/idea-sandbox/system/log/idea.log | grep -E "MockRunner|MockDebugRunner|canRun|doExecute|MockAgent"

# 或者查看完整日志
tail -100 build/idea-sandbox/system/log/idea.log
```

### 5. 期望看到的日志

#### 如果正确使用了 "Run with Mock Runner"：
```
[MockRunner] canRun called - executorId: MyCustomExecutor, profile: com.intellij.execution.application.ApplicationConfiguration, MyCustomExecutor.EXECUTOR_ID: MyCustomExecutor
[MockRunner] isMyExecutor: true, isAppConfig: true
[MockRunner] Running with My Custom Runner!
[MockRunner] Mock config saved to: /tmp/mock-runner/mock-config.json
[MockRunner] Added agent: -javaagent:/path/to/mock-agent-1.0.0-agent.jar=/tmp/mock-runner/mock-config.json
[MockAgent] Starting Mock Agent...
[MockAgent] Loaded 1 mock rules
[MockAgent] Intercepting com.student.StudentService.getAllStudents
[MockAgent] Mocking com.student.StudentService.getAllStudents -> []
```

#### 如果使用了普通的 Run：
```
# 不会看到任何 [MockRunner] 日志
# 因为普通 Run 使用的是标准的 DefaultJavaProgramRunner
```

## 常见问题

### Q1: 看不到 "Run with Mock Runner" 选项
**原因**：canRun 返回 false
**解决**：
- 确保 profile 是 ApplicationConfiguration
- 检查 Executor ID 是否匹配

### Q2: 看到选项但点击后没有日志
**原因**：doExecute 没有被调用
**解决**：
- 检查是否有异常
- 确认 Runner 已正确注册在 plugin.xml

### Q3: Agent jar 找不到
**原因**：agentJar task 没有生成 jar
**解决**：
```bash
./gradlew clean agentJar
ls -la build/libs/mock-agent-*.jar
```

### Q4: Mock 配置文件为空
**原因**：MockConfigService 没有正确保存配置
**解决**：
- 检查 /tmp/mock-runner/mock-config.json 内容
- 确认 MockConfigService.getMockConfig() 返回正确数据

## 下一步
根据日志输出确定问题所在，然后针对性修复。
