# Mock Runner Plugin - 更新说明

## 新增功能

### 1. Debug 支持
- 新增 `MyDebugExecutor` - 支持 "Debug with Mock Runner"
- 现在可以在运行菜单中看到两个选项：
  - "Run with My Custom Runner" - 运行并启用 Mock
  - "Debug with Mock Runner" - 调试并启用 Mock

### 2. 自定义图标
创建了体现 Mock 含义的戏剧面具图标：
- 绿色渐变的面具，代表"模拟/替身"
- 笑脸表情，友好且易识别
- 闪光点装饰，强调"假的/模拟的"含义

图标文件：
- `src/main/resources/icons/mockRunner.svg` - 16x16 标准版本
- `src/main/resources/icons/mockRunner_dark.svg` - 深色主题版本
- `src/main/resources/icons/mockRunner_13x13.svg` - 13x13 推荐尺寸

### 3. 图标应用位置
- ToolWindow 侧边栏图标
- Run/Debug 菜单中的执行器图标
- 代码编辑器中被 Mock 方法旁边的标记图标

## 技术实现

### 新增文件
1. `MockRunnerIcons.java` - 图标加载工具类
2. `MyDebugExecutor.java` - Debug 执行器
3. 图标资源文件（SVG 格式）

### 修改文件
1. `MyProgramRunner.java` - 支持两个 Executor（Run 和 Debug）
2. `MyCustomExecutor.java` - 使用自定义图标
3. `MockedMethodLineMarkerProvider.java` - 使用自定义图标
4. `plugin.xml` - 注册 Debug Executor 和更新图标路径

## 使用方法

1. 右键点击方法 → "Add Mock for Method"
2. 在右侧 "My Runner" 面板中查看 Mock 配置
3. 运行时选择：
   - "Run with My Custom Runner" - 正常运行
   - "Debug with Mock Runner" - 调试模式
4. 被 Mock 的方法旁边会显示绿色面具图标

## 图标设计理念

戏剧面具是 Mock（模拟/替身）的完美象征：
- 在戏剧中，面具代表扮演不同角色
- 在编程中，Mock 就是为真实对象创建"替身"
- 绿色代表测试通过、安全、可控
- 笑脸让图标更友好，降低使用门槛

## 下一步

运行插件测试：
```bash
./gradlew runIde
```

构建插件：
```bash
./gradlew buildPlugin
```

插件将在 `build/distributions/` 目录生成。
