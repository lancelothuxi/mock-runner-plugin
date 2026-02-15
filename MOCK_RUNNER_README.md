# Mock Runner Plugin - 使用说明

## 功能概述

这是一个类似 "Run with Coverage" 的 IntelliJ IDEA 插件，可以在运行 Java 程序时动态 Mock 方法返回值。

## 核心特性

1. **可视化 Mock 配置界面** - 类似 Coverage 界面，显示所有类和方法
2. **基于 ByteBuddy 的 JavaAgent** - 运行时字节码增强，无需修改源代码
3. **灵活的 Mock 规则** - 支持多种返回类型（int, String, boolean 等）
4. **即时生效** - 配置后立即在程序运行时生效

## 使用步骤

### 1. 启动插件

1. 在 IDEA 中打开你的 Java 项目
2. 找到运行按钮旁边的下拉箭头
3. 选择 **"Run with My Custom Runner"**

### 2. 配置 Mock 规则

启动后会弹出 Mock 配置对话框，显示项目中所有的类和方法：

| Enabled | Class | Method | Return Type | Mock Value |
|---------|-------|--------|-------------|------------|
| ☐ | Calculator | add | int | 100 |
| ☐ | Calculator | multiply | int | 999 |
| ☐ | Calculator | getMessage | String | Mocked! |

**操作说明：**
- 勾选 "Enabled" 列来启用 Mock
- 在 "Mock Value" 列输入要返回的值
- 点击 OK 开始运行

### 3. 查看效果

程序运行时，被 Mock 的方法会返回你配置的值，而不是原始实现的值。

控制台会显示：
```
[MockAgent] Starting Mock Agent...
[MockAgent] Loaded 2 mock rules
[MockAgent] Intercepting Calculator.add
[MockAgent] Mocking Calculator.add -> 100
5 + 3 = 100
```

## 技术架构

### 1. JavaAgent (mock-agent.jar)
- 使用 ByteBuddy 进行字节码增强
- 在方法执行后拦截并替换返回值
- 支持运行时动态加载 Mock 配置

### 2. IDEA 插件
- **MyCustomExecutor**: 在运行菜单中添加自定义选项
- **MyProgramRunner**: 处理运行逻辑，注入 JavaAgent
- **MockConfigDialog**: 可视化配置界面
- **MockConfig**: Mock 规则数据结构

### 3. 工作流程

```
用户点击 "Run with My Custom Runner"
    ↓
显示 Mock 配置对话框
    ↓
用户选择要 Mock 的方法并配置返回值
    ↓
保存配置到临时 JSON 文件
    ↓
启动 Java 程序，添加 -javaagent 参数
    ↓
Agent 加载配置并拦截方法调用
    ↓
返回 Mock 值而不是真实值
```

## 示例代码

### 原始代码
```java
public class Main {
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        int result = calc.add(5, 3);
        System.out.println("5 + 3 = " + result);
    }
}

class Calculator {
    public int add(int a, int b) {
        return a + b;  // 正常返回 8
    }
}
```

### Mock 后的效果
配置 `Calculator.add` 返回 `100`：
```
5 + 3 = 100  // 返回 Mock 值而不是 8
```

## 支持的返回类型

当前支持以下类型的 Mock：
- `int` / `Integer`
- `long` / `Long`
- `double` / `Double`
- `float` / `Float`
- `boolean` / `Boolean`
- `String`

## 扩展功能（未来计划）

1. **When-Then 规则** - 类似 Mockito 的条件 Mock
   ```java
   when(calc.add(5, 3)).thenReturn(100);
   when(calc.add(10, 20)).thenReturn(999);
   ```

2. **参数匹配** - 根据输入参数决定返回值
3. **Mock 次数控制** - 限制 Mock 生效次数
4. **异常 Mock** - Mock 方法抛出异常
5. **对象 Mock** - 支持复杂对象的 Mock
6. **Mock 历史记录** - 保存和加载 Mock 配置

## 文件结构

```
src/main/java/com/example/plugin/
├── agent/
│   ├── MockAgent.java          # JavaAgent 入口
│   └── MockInterceptor.java    # ByteBuddy 拦截器
├── mock/
│   └── MockConfig.java         # Mock 配置数据结构
├── ui/
│   └── MockConfigDialog.java  # Mock 配置界面
├── MyCustomExecutor.java       # 自定义执行器
└── MyProgramRunner.java        # 程序运行器
```

## 构建说明

```bash
# 编译插件
./gradlew compileJava

# 构建 Agent JAR
./gradlew agentJar

# 准备插件沙箱
./gradlew prepareSandbox

# 运行 IDEA 测试
./gradlew runIde
```

## 依赖

- ByteBuddy 1.14.9 - 字节码操作
- Gson 2.10.1 - JSON 序列化
- IntelliJ Platform SDK 2022.3

## 注意事项

1. Agent 只能 Mock 项目中的类，不能 Mock JDK 类
2. Mock 配置保存在临时目录，重启后需要重新配置
3. 某些方法可能因为 JVM 优化而无法 Mock（如 final 方法）
4. 建议先在测试环境验证 Mock 效果

## 故障排查

### Agent 未生效
检查控制台是否有 `[MockAgent] Starting Mock Agent...` 输出

### Mock 不生效
1. 确认方法在配置界面中已勾选
2. 确认 Mock Value 已填写
3. 检查方法签名是否正确

### 找不到 Agent JAR
运行 `./gradlew agentJar` 重新构建

## 参考资料

- [ByteBuddy 官方文档](https://bytebuddy.net/)
- [Java Instrumentation API](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/package-summary.html)
- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)
