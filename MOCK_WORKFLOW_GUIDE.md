# Mock 工作流程指南

## 功能概述

现在插件支持完整的 Mock 工作流程：

1. **选中方法添加 Mock**：在代码中右键选择方法，添加 Mock 配置
2. **侧边栏查看 Mock**：右侧 "My Runner" 面板显示所有 Mock 配置
3. **运行时自动应用**：使用 "Run with My Custom Runner" 时自动应用 Mock

## 使用步骤

### 1. 添加 Mock 配置

1. 在代码编辑器中，将光标放在要 Mock 的方法上
2. 右键点击，选择 "Add Mock for Method"
3. 在弹出的对话框中输入 Mock 返回值
4. 点击 OK

示例：
```java
public class UserService {
    public String getUserName(int userId) {  // 光标放这里
        // 实际实现
        return database.query(...);
    }
}
```

右键 → Add Mock for Method → 输入 "MockedUser" → OK

### 2. 查看 Mock 配置

打开右侧的 "My Runner" 面板，你会看到：

```
Mock Runner
├── Mock Methods (1)
│   └── UserService.getUserName(int) → MockedUser
└── Run History
```

### 3. 运行程序

1. 找到 main 方法
2. 点击旁边的运行按钮
3. 选择 "Run with My Custom Runner"
4. 程序会自动应用所有 Mock 配置运行

### 4. 查看运行历史

运行后，"Run History" 节点会显示：
```
Run History
└── [14:30:25] Main.main - Starting (-)
```

## 面板功能

### Mock Methods 节点
- 显示所有已配置的 Mock 方法
- 格式：`ClassName.methodName(params) → returnValue`
- 计数显示：`Mock Methods (N)`

### Run History 节点
- 显示运行历史记录
- 包含时间、方法名、状态

### 工具栏按钮

- **Clear All**：清除所有 Mock 配置和运行历史
- **Refresh**：刷新面板显示
- **统计信息**：显示当前 Mock 方法数量

## 工作原理

1. **MockConfigService**：管理所有 Mock 配置
2. **MyRunnerToolWindowContent**：显示 Mock 配置和运行历史
3. **MyProgramRunner**：运行时读取 Mock 配置并应用
4. **JavaAgent**：在运行时拦截方法调用并返回 Mock 值

## 示例场景

### 场景 1：Mock 数据库调用

```java
public class OrderService {
    public Order getOrder(int orderId) {
        return database.query("SELECT * FROM orders WHERE id = ?", orderId);
    }
}
```

1. 光标放在 `getOrder` 方法上
2. 右键 → Add Mock for Method
3. 输入：`{"id": 123, "amount": 100.0}`
4. 运行程序，`getOrder` 会返回 Mock 数据

### 场景 2：Mock 外部 API

```java
public class PaymentService {
    public boolean processPayment(double amount) {
        return externalAPI.charge(amount);
    }
}
```

1. Mock `processPayment` 返回 `true`
2. 测试时不会真正调用外部 API

## 快捷操作

- **快速添加 Mock**：选中方法名 → 右键 → Add Mock
- **批量清除**：点击 "Clear All" 按钮
- **查看配置**：打开右侧 "My Runner" 面板

## 注意事项

1. Mock 配置在 IDEA 会话期间保持
2. 重启 IDEA 后需要重新配置
3. Mock 只在使用 "Run with My Custom Runner" 时生效
4. 普通 Run 不会应用 Mock 配置

## 下一步功能

- [ ] 支持编辑已有的 Mock 配置
- [ ] 支持启用/禁用单个 Mock
- [ ] 支持导出/导入 Mock 配置
- [ ] 支持更复杂的 Mock 规则（条件、次数等）
- [ ] 支持 Mock 静态方法
