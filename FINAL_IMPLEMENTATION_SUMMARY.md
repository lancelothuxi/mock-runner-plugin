# Mock Runner 插件 - 最终实现总结

## 已完成的功能

### 1. Mock 配置管理
- ✅ Mock 配置持久化（保存到 `.idea/mockRunnerConfig.xml`）
- ✅ ToolWindow UI（添加、删除、查看 Mock 配置）
- ✅ 自动加载之前的配置
- ✅ 数据结构同步（mockMethods 和 mockRules）

### 2. JavaAgent 集成
- ✅ 使用 ByteBuddy 实现方法拦截
- ✅ 使用 FixedValue 直接返回 Mock 值（不执行原方法）
- ✅ 支持多种返回类型（int, String, List 等）
- ✅ Agent jar 自动打包和部署

### 3. 运行配置扩展
- ✅ RunConfigurationExtension 自动添加 javaagent 参数
- ✅ 适用于所有 Java Application 配置
- ✅ 支持 Run 和 Debug 模式
- ✅ 通过 PluginManager 正确查找 agent jar 路径

### 4. UI 功能
- ✅ 自定义 Executor（Run with Mock Runner, Debug with Mock Runner）
- ✅ 图标区分（Run 用绿色，Debug 用红色）
- ✅ LineMarker 显示已 Mock 的方法
- ✅ 右键菜单添加 Mock

## 当前实现方案

### 方案选择
最终采用了 **RunConfigurationExtension** 方案，因为：
1. 不依赖 Runner 选择机制
2. 适用于所有运行方式（Run, Debug, Coverage 等）
3. 更可靠和稳定
4. 是 IntelliJ 推荐的方式

### 核心组件

#### 1. MockAgent.java (JavaAgent)
```java
// 使用 FixedValue 直接返回 Mock 值
builder.method(ElementMatchers.named(methodName))
    .intercept(FixedValue.value(mockValue));
```

**为什么使用 System.out：**
- Agent 在 JVM 早期加载，logging 框架未初始化
- Agent 运行在被测试应用中，不是插件中
- 输出会显示在被测试应用的控制台

#### 2. MockRunConfigurationExtension.java
```java
@Override
public <T extends RunConfigurationBase<?>> void updateJavaParameters(
        @NotNull T configuration,
        @NotNull JavaParameters params,
        RunnerSettings runnerSettings) throws ExecutionException {
    // 添加 -javaagent 参数
}
```

#### 3. MockConfig.java
```java
// 同时维护两个数据结构
private Map<String, MockRule> mockRules;  // Agent 使用
private List<MockMethodConfig> mockMethods;  // UI 使用
```

## 使用方法

### 1. 添加 Mock 配置
1. 打开右侧 "My Runner" ToolWindow
2. 点击 "Add Mock" 按钮
3. 填写配置：
   - Class: `com.student.StudentService`
   - Method: `getAllStudents`
   - Return Value: `[]`
   - 勾选 Enabled

### 2. 运行程序
- 使用普通的 Run 或 Debug 按钮即可
- 不需要特意选择 "Run with Mock Runner"
- Mock 会自动生效

### 3. 查看日志
在被测试应用的控制台中查看：
```
[MockAgent] Starting Mock Agent...
[MockAgent] Loaded 1 mock rules
[MockAgent] Intercepting com.student.StudentService.getAllStudents
[MockAgent] Will return: []
[MockAgent] Mock Agent installed successfully
```

## 技术细节

### Agent Jar 路径查找
```java
// 通过 PluginManager 获取插件路径
IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(
    PluginId.getId("com.example.myplugin"));
Path libPath = plugin.getPluginPath().resolve("lib");
// 查找 mock-agent-*.jar
```

### Mock 值解析
```java
// 支持的类型
- int, long, double, float, boolean
- String
- List ([] 返回空 ArrayList)
```

### 数据持久化
```java
@State(
    name = "MockConfigService",
    storages = @Storage("mockRunnerConfig.xml")
)
public class MockConfigService implements PersistentStateComponent<State>
```

## 已知限制

1. **复杂对象**：目前只支持简单类型和空列表，不支持复杂对象
2. **方法签名**：按方法名匹配，不考虑参数类型（可能匹配到重载方法）
3. **日志**：Agent 使用 System.out，插件代码应该改用 Logger

## 后续改进建议

### 1. 使用 Logger（插件代码）
```java
// 替换所有 System.out.println
private static final Logger LOG = Logger.getInstance(MockExtension.class);
LOG.info("[MockExtension] ...");
```

### 2. 支持复杂对象
- 使用 JSON 反序列化
- 支持自定义对象构造

### 3. 方法签名匹配
- 考虑参数类型
- 支持重载方法的精确匹配

### 4. UI 改进
- 支持批量导入/导出 Mock 配置
- 支持 Mock 配置模板
- 支持条件 Mock（根据参数值）

## 文件结构

```
src/main/java/com/example/plugin/
├── agent/
│   ├── MockAgent.java           # JavaAgent 入口
│   └── MockInterceptor.java     # (已废弃，使用 FixedValue)
├── extension/
│   └── MockRunConfigurationExtension.java  # 自动添加 agent
├── mock/
│   ├── MockConfig.java          # Mock 配置数据结构
│   └── MockMethodConfig.java    # 单个 Mock 方法配置
├── service/
│   └── MockConfigService.java   # 配置管理和持久化
├── ui/
│   ├── MyRunnerToolWindowFactory.java
│   └── MyRunnerToolWindowContent.java
├── marker/
│   └── MockedMethodLineMarkerProvider.java  # 代码图标
└── action/
    └── AddMockAction.java       # 右键菜单
```

## 测试步骤

1. 启动插件：`./gradlew runIde`
2. 打开 Java 项目
3. 添加 Mock 配置
4. 运行程序
5. 验证 Mock 生效

## 总结

经过多次迭代，最终实现了一个可用的 Mock Runner 插件：
- ✅ Agent 能正确加载
- ✅ Mock 配置能持久化
- ✅ 使用 FixedValue 直接返回 Mock 值
- ✅ 支持 Run 和 Debug 模式
- ✅ UI 友好，操作简单

主要挑战和解决方案：
1. **Runner 不被调用** → 改用 RunConfigurationExtension
2. **Agent jar 找不到** → 使用 PluginManager 查找
3. **数据结构不匹配** → 同步 mockRules 和 mockMethods
4. **方法不被拦截** → 使用 FixedValue 替代 Advice
