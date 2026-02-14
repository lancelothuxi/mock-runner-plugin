# My IDEA Plugin - 自定义 Run/Debug 插件

这是一个类似 JProfiler 的 IntelliJ IDEA 插件，可以添加自定义的 "Run with XX" 和 "Debug with XX" 功能。

## 功能特性

- 自定义运行配置类型
- 支持 Run 和 Debug 模式
- 可配置的参数设置
- 类似 JProfiler 的使用体验

## 在 IntelliJ IDEA 中开发和运行

### 方法一：使用 IntelliJ IDEA（推荐）

1. **打开项目**
   - 在 IDEA 中打开此项目
   - IDEA 会自动识别这是一个插件项目

2. **配置 Plugin SDK**
   - File → Project Structure → Project Settings → Project
   - 设置 SDK 为 "IntelliJ Platform Plugin SDK"
   - 如果没有，点击 "New" → "IntelliJ Platform Plugin SDK"
   - 选择你的 IDEA 安装目录

3. **配置运行配置**
   - Run → Edit Configurations
   - 点击 "+" → "Plugin"
   - 命名为 "Run Plugin"
   - 点击 OK

4. **运行插件**
   - 点击运行按钮或按 Shift+F10
   - 会启动一个新的 IDEA 实例，其中已安装你的插件

5. **测试插件**
   - 在新启动的 IDEA 中，打开任意项目
   - Run → Edit Configurations
   - 点击 "+" 应该能看到 "My Custom Runner"
   - 创建配置并运行

### 方法二：使用 Gradle（更简单）

如果你想使用 Gradle 而不是 Maven：

1. **使用 Gradle 构建文件**
   ```bash
   ./gradlew runIde
   ```
   这会自动下载 IDEA SDK 并启动插件

2. **打包插件**
   ```bash
   ./gradlew buildPlugin
   ```
   插件 ZIP 文件会生成在 `build/distributions/`

### 方法三：手动打包安装

1. **编译源代码**
   ```bash
   /home/lancelot/software/apache-maven-3.6.0/bin/mvn clean compile
   ```

2. **手动打包**
   - 将编译后的 class 文件和 plugin.xml 打包成 jar
   - 创建插件目录结构：
     ```
     my-idea-plugin/
     └── lib/
         └── my-idea-plugin.jar
     ```
   - 压缩成 ZIP 文件

3. **安装插件**
   - IDEA → Settings → Plugins
   - 齿轮图标 → Install Plugin from Disk
   - 选择 ZIP 文件
   - 重启 IDEA

## 推荐方式

**最简单的方式是使用 Gradle：**

```bash
# 运行插件（会启动新的 IDEA 实例）
./gradlew runIde

# 打包插件
./gradlew buildPlugin
```

Gradle 方式的优点：
- 自动下载 IDEA SDK，无需手动配置
- 一键运行和调试
- 自动打包成标准格式

## 使用方法

1. 安装插件后，打开 Run/Debug Configurations
2. 点击 "+" 添加新配置
3. 选择 "My Custom Runner"
4. 配置参数
5. 使用 Run 或 Debug 按钮运行

## 自定义扩展

### 修改运行逻辑

编辑 `MyProgramRunner.java` 中的 `doExecute` 方法来添加自定义的启动逻辑。

### 添加更多配置选项

在 `MyRunConfiguration.java` 中添加更多字段和 UI 组件。

### 修改图标和名称

在 `MyConfigurationType.java` 中修改显示名称和图标。

## 项目结构

```
src/main/
├── java/com/example/plugin/
│   ├── MyConfigurationType.java       # 配置类型定义
│   ├── MyRunConfigurationFactory.java # 配置工厂
│   ├── MyRunConfiguration.java        # 运行配置实现
│   └── MyProgramRunner.java           # 程序运行器
└── resources/META-INF/
    └── plugin.xml                      # 插件描述文件
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
