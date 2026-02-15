# 数据库 MCP 配置完成 ✅

## 已完成的配置

### 1. SQLite 示例数据库（已启用）
- **位置**: `~/example.db`
- **状态**: ✅ 已创建并填充示例数据

**数据库内容**:

**users 表**:
| id | name | email | status | created_at |
|----|------|-------|--------|------------|
| 1 | 张三 | zhangsan@example.com | active | 2026-02-14 |
| 2 | 李四 | lisi@example.com | active | 2026-02-14 |
| 3 | 王五 | wangwu@example.com | inactive | 2026-02-14 |

**orders 表**:
| id | user_id | product_name | amount | status | created_at |
|----|---------|--------------|--------|--------|------------|
| 1 | 1 | MacBook Pro | 12999 | completed | 2026-02-14 |
| 2 | 1 | iPhone 15 | 5999 | completed | 2026-02-14 |
| 3 | 2 | iPad Air | 4599 | pending | 2026-02-14 |
| 4 | 3 | AirPods Pro | 1899 | cancelled | 2026-02-14 |

### 2. MCP 配置文件
- **位置**: `~/.kiro/settings/mcp.json`
- **已配置的数据库**:
  - ✅ SQLite (启用)
  - ⏸️ PostgreSQL (禁用，模板已配置)
  - ⏸️ MySQL (禁用，模板已配置)

## 下一步操作

### 启用 SQLite MCP 服务器
1. 在 Kiro 中打开命令面板
2. 搜索 "MCP" 相关命令
3. 重新连接或重启 MCP 服务器
4. 或者直接重启 Kiro

### 测试数据库连接
启用后，你可以在对话中直接请求：
```
"查询 users 表中的所有数据"
"统计每个用户的订单总金额"
"查找状态为 pending 的订单"
```

## 修改配置

### 更改 SQLite 数据库路径
编辑 `~/.kiro/settings/mcp.json`:
```json
"sqlite": {
  "args": ["mcp-server-sqlite", "--db-path", "/your/custom/path.db"]
}
```

### 启用 PostgreSQL
1. 修改连接字符串:
```json
"env": {
  "POSTGRES_CONNECTION_STRING": "postgresql://user:pass@host:5432/dbname"
}
```
2. 设置 `"disabled": false`

### 启用 MySQL
1. 修改连接参数:
```json
"env": {
  "MYSQL_HOST": "localhost",
  "MYSQL_USER": "root",
  "MYSQL_PASSWORD": "your_password",
  "MYSQL_DATABASE": "your_db"
}
```
2. 设置 `"disabled": false`

## 手动测试数据库

### SQLite 命令行
```bash
# 连接数据库
sqlite3 ~/example.db

# 查看所有表
.tables

# 查看表结构
.schema users

# 执行查询
SELECT * FROM users;

# 退出
.quit
```

### 示例查询
```sql
-- 查询活跃用户
SELECT * FROM users WHERE status = 'active';

-- 用户订单统计
SELECT u.name, COUNT(o.id) as order_count, SUM(o.amount) as total_amount
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id;

-- 查找高价值订单
SELECT * FROM orders WHERE amount > 5000 ORDER BY amount DESC;
```

## 故障排查

**MCP 服务器无法启动**:
- 确保已安装 `uv` 和 `uvx`: https://docs.astral.sh/uv/getting-started/installation/
- 检查数据库文件路径是否正确
- 查看 Kiro 的 MCP 日志

**数据库文件不存在**:
- SQLite 会自动创建新文件
- 确保路径有写入权限

**查询失败**:
- 检查表名和列名是否正确
- 验证 SQL 语法
- 查看错误消息获取详细信息
