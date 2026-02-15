# IntelliJ IDEA Mock Runner Plugin

一个强大的 IntelliJ IDEA 插件，通过 JavaAgent 技术实现方法级别的 Mock，无需修改源代码即可拦截和替换方法返回值。

## 功能特性

- ✅ 自定义 Run/Debug Executor（绿色运行图标 / 红色调试图标）
- ✅ JavaAgent 方法拦截（使用 ByteBuddy MethodDelegation）
- ✅ ToolWindow UI 管理 Mock 配置
- ✅ Mock 配置持久化（保存到 `.idea/mockRunnerConfig.xml`）
- ✅ LineMarker 显示被 Mock 的方法
- ✅ 支持 Debug 模式（断点、单步调试）
- ✅ 支持复杂类型（List、Map 等）的 JSON 解析

## 快速开始

### 使用 Gradle 运行插件（推荐）

```bash
# 运行插件（会启动新的 IDEA 实例）
./gradlew runIde

# 快速编译 Agent（1-2 秒）
./scripts/ultra-fast-build.sh

# 打包插件
./gradlew buildPlugin
```

### 使用插件

1. 在 ToolWindow 中添加 Mock 配置
2. 右键点击运行配置，选择 "Run with Mock Runner" 或 "Debug with Mock Runner"
3. 程序运行时会自动拦截配置的方法并返回 Mock 值

## 项目结构

```
.
├── src/main/java/com/example/plugin/
│   ├── agent/
│   │   └── MockAgent.java                    # JavaAgent 实现（ByteBuddy）
│   ├── extension/
│   │   └── MockRunConfigurationExtension.java # 添加 javaagent 参数
│   ├── service/
│   │   └── MockConfigService.java            # 配置持久化服务
│   ├── ui/
│   │   └── MyRunnerToolWindowContent.java    # ToolWindow UI
│   ├── mock/
│   │   ├── MockConfig.java                   # Mock 配置数据结构
│   │   └── MockMethodConfig.java             # 方法配置
│   ├── MyProgramRunner.java                  # Run 模式执行器
│   ├── MyDebugProgramRunner.java             # Debug 模式执行器
│   ├── MyCustomExecutor.java                 # Run Executor
│   └── MyDebugExecutor.java                  # Debug Executor
├── scripts/                                   # 构建脚本
│   ├── ultra-fast-build.sh                   # 快速编译（1-2秒）
│   ├── quick-build.sh                        # Gradle 快速构建
│   ├── run-sandbox.sh                        # 运行沙箱
│   └── ...
├── docs/                                      # 文档归档
│   ├── MOCK_RUNNER_README.md                 # Mock Runner 详细文档
│   ├── FINAL_IMPLEMENTATION_SUMMARY.md       # 最终实现总结
│   └── ...
└── build.gradle.kts                          # Gradle 构建配置
```

## 开发要求

- IntelliJ IDEA 2022.3+
- Java 8+
- Maven 3.6+ 或 Gradle 8.0+

## 常见问题

**Q: Maven 编译失败，找不到 IDEA 依赖？**
A: 推荐使用 Gradle 方式，或者在 IDEA 中直接开发。

**Q: 如何调试插件？**
A: 在 IDEA 中使用 Debug 模式运行 Plugin 配置，或使用 `./gradlew runIde` 然后在代码中设置断点。

**Q: 插件安装后不显示？**
A: 检查 plugin.xml 配置是否正确，确保 depends 标签包含了必要的模块。
