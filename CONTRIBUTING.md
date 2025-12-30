# 贡献指南

感谢你对 ClodRail 项目的关注！我们欢迎任何形式的贡献，包括但不限于：

- 🐛 Bug 报告
- 💡 功能建议
- 📝 文档改进
- 🔧 代码贡献

## 如何贡献

### 报告 Bug

1. 在提交 Bug 之前，请先搜索 [Issues](https://github.com/Dai5297/ClodRail/issues) 确认是否已有相同问题
2. 如果没有，请创建新的 Issue，并提供以下信息：
   - 问题描述
   - 复现步骤
   - 期望行为
   - 实际行为
   - 环境信息（操作系统、Java 版本、浏览器等）
   - 相关日志或截图

### 提交功能建议

1. 在 Issues 中创建新的 Feature Request
2. 清晰描述你希望添加的功能
3. 说明该功能的使用场景和价值

### 代码贡献

#### 开发环境准备

1. Fork 本仓库到你的 GitHub 账号
2. Clone 你 Fork 的仓库到本地
   ```bash
   git clone https://github.com/YOUR_USERNAME/ClodRail.git
   cd ClodRail
   ```
3. 添加上游仓库
   ```bash
   git remote add upstream https://github.com/Dai5297/ClodRail.git
   ```
4. 安装开发依赖
   - JDK 17+
   - Maven 3.8+
   - Node.js 18+
   - MySQL 8.0+
   - Redis 7.0+

#### 开发流程

1. 同步上游代码
   ```bash
   git fetch upstream
   git checkout main
   git merge upstream/main
   ```

2. 创建功能分支
   ```bash
   git checkout -b feature/your-feature-name
   # 或修复分支
   git checkout -b fix/your-fix-name
   ```

3. 进行开发并提交
   ```bash
   git add .
   git commit -m "feat: 添加xxx功能"
   ```

4. 推送到你的仓库
   ```bash
   git push origin feature/your-feature-name
   ```

5. 创建 Pull Request
   - 前往 GitHub 创建 PR
   - 填写 PR 描述，说明改动内容
   - 等待代码审查

#### 提交规范

我们使用 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式调整（不影响功能）
- `refactor`: 代码重构
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 构建/工具相关

示例：
```
feat(order): 添加订单超时自动取消功能
fix(user): 修复登录验证码校验失败问题
docs: 更新 API 接口文档
```

#### 代码规范

**后端（Java）**
- 遵循阿里巴巴 Java 开发手册
- 使用 4 空格缩进
- 类名使用 PascalCase
- 方法名和变量名使用 camelCase
- 常量使用 UPPER_SNAKE_CASE
- 添加必要的注释和 JavaDoc

**前端（Vue）**
- 遵循 Vue 3 官方风格指南
- 使用 2 空格缩进
- 组件名使用 PascalCase
- Props 和 Events 使用 camelCase
- 使用 ESLint + Prettier 格式化代码

### 文档贡献

- 修复文档中的错误或过时内容
- 补充缺失的文档
- 改进文档的可读性
- 翻译文档（欢迎英文翻译）

## 行为准则

- 尊重所有贡献者
- 保持友善和专业的交流
- 接受建设性的批评
- 关注项目的最佳利益

## 获取帮助

如果你在贡献过程中遇到问题：

1. 查阅项目文档
2. 搜索已有的 Issues
3. 创建新的 Issue 寻求帮助
4. 联系项目维护者

再次感谢你的贡献！🎉
