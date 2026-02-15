# Mock 配置持久化功能

## 功能说明

Mock 配置现在会自动保存到项目配置文件中，重新打开项目时会自动加载。

## 实现方式

### 1. 使用 PersistentStateComponent
```java
@State(
    name = "MockConfigService",
    storages = @Storage("mockRunnerConfig.xml")
)
public class MockConfigService implements PersistentStateComponent<MockConfigService.State>
```

### 2. 配置文件位置
```
.idea/mockRunnerConfig.xml
```

### 3. 自动保存时机
- 添加 Mock 配置时
- 删除 Mock 配置时
- 清空所有配置时
- 项目关闭时

### 4. 自动加载时机
- 项目打开时
- Service 初始化时

## 使用流程

### 首次使用
1. 打开项目
2. 在 My Runner ToolWindow 中添加 Mock 配置
3. 配置自动保存到 `.idea/mockRunnerConfig.xml`

### 再次打开项目
1. 打开项目
2. Mock 配置自动从 `.idea/mockRunnerConfig.xml` 加载
3. ToolWindow 自动显示之前配置的 Mock
4. 代码编辑器中自动显示 Mock 图标

## 配置文件格式

```xml
<application>
  <component name="MockConfigService">
    <option name="mockConfigJson" value="{
  &quot;mockMethods&quot;: [
    {
      &quot;className&quot;: &quot;com.student.StudentService&quot;,
      &quot;methodName&quot;: &quot;getAllStudents&quot;,
      &quot;signature&quot;: &quot;()Ljava/util/List;&quot;,
      &quot;returnValue&quot;: &quot;[]&quot;,
      &quot;returnType&quot;: &quot;java.util.List&quot;,
      &quot;enabled&quot;: true
    }
  ]
}" />
  </component>
</application>
```

## 日志输出

### 保存时
```
[MockConfigService] Added mock: com.student.StudentService.getAllStudents
[MockConfigService] Saving state: {"mockMethods":[...]}
```

### 加载时
```
[MockConfigService] Loaded state: {"mockMethods":[...]}
[MockConfigService] Updated ToolWindow with 1 mock methods
```

## 注意事项

1. **版本控制**：`.idea/mockRunnerConfig.xml` 可以提交到 Git，团队共享 Mock 配置
2. **项目级别**：每个项目有独立的 Mock 配置
3. **自动同步**：配置变更会自动同步到 UI 和编辑器图标

## 测试步骤

1. 添加一个 Mock 配置
2. 关闭项目
3. 重新打开项目
4. 检查 ToolWindow 是否显示之前的 Mock 配置
5. 检查代码编辑器是否显示 Mock 图标

## 优势

- **无需手动保存**：所有操作自动持久化
- **团队协作**：可以共享 Mock 配置
- **快速恢复**：重启 IDE 后立即可用
- **项目隔离**：不同项目的配置互不影响
