# Mock 功能问题分析

## 当前状态

### 工作正常的部分
1. ✅ Mock 配置持久化 - 配置可以保存和加载
2. ✅ ToolWindow UI - 可以添加、查看 Mock 配置
3. ✅ canRun 方法 - 正确返回 true
4. ✅ Executor 注册 - "Run with Mock Runner" 和 "Debug with Mock Runner" 出现在菜单中
5. ✅ 图标区分 - Run 和 Debug 使用不同图标

### 不工作的部分
1. ❌ doExecute/execute 方法从未被调用
2. ❌ JavaAgent 参数从未被添加
3. ❌ Mock Agent 从未被加载
4. ❌ Mock 功能完全不生效

## 问题根源

虽然我们的 Runner 的 `canRun()` 返回 true，但 `doExecute()` 和 `execute()` 方法都没有被调用。

**可能的原因：**

1. **Runner 优先级问题**
   - IDEA 有多个 Runner 都可以处理 ApplicationConfiguration
   - 当多个 Runner 的 canRun 都返回 true 时，IDEA 会选择优先级最高的
   - 我们的 Runner 可能优先级太低，被其他 Runner 抢先了

2. **Executor 和 Runner 的匹配问题**
   - 我们创建了自定义的 Executor (MyCustomExecutor, MyDebugExecutor)
   - 但可能 IDEA 的默认 Runner 也声称可以处理这些 Executor
   - 导致我们的 Runner 没有被选中

3. **GenericDebuggerRunner 的特殊行为**
   - GenericDebuggerRunner 可能有特殊的执行流程
   - 可能需要实现额外的方法或接口

## 日志证据

```
# canRun 被调用并返回 true
[MockDebugRunner] canRun called - executorId: MyDebugExecutor
[MockDebugRunner] isMyExecutor: true, isAppConfig: true

# 但是 execute/doExecute 从未被调用
# 没有看到：
# [MockDebugRunner] ========== execute() CALLED ==========
# [MockDebugRunner] ========== doExecute CALLED ==========
# [MockDebugRunner] Mock config saved to: ...
# [MockDebugRunner] Added agent: ...

# 被测试应用的日志中也没有：
# [MockAgent] Starting Mock Agent...
# [MockAgent] Mock Agent installed successfully
```

## 建议的解决方案

### 方案 1：提高 Runner 优先级（推荐）
在 plugin.xml 中调整 Runner 的注册顺序，或者实现 `getOrder()` 方法返回更高的优先级。

### 方案 2：使用 RunConfigurationExtension
不使用自定义 Runner，而是使用 RunConfigurationExtension 来修改 JavaParameters。
这种方式可以在任何 Runner 执行前修改参数。

### 方案 3：创建自定义的 RunConfiguration
不依赖 ApplicationConfiguration，创建完全自定义的 RunConfiguration 类型。
这样可以完全控制执行流程。

### 方案 4：Hook 到现有的 Debug Runner
使用 IDEA 的扩展点来 hook 到现有的 Debug Runner，在它执行前修改参数。

## 下一步行动

1. 先尝试方案 2（RunConfigurationExtension），因为它最简单且最可靠
2. 如果方案 2 不行，再尝试方案 3（自定义 RunConfiguration）
3. 保留当前的 Executor 和 UI，只改变参数注入的方式

## 临时解决方案

在解决 Runner 问题之前，可以让用户手动添加 VM 参数：
```
-javaagent:/path/to/mock-agent.jar=/tmp/mock-runner/mock-config.json
```

但这不是理想的解决方案，因为：
- 用户体验差
- 需要手动维护路径
- 失去了"一键 Mock"的便利性
