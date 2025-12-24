/**
 * 悬浮AI助手组件
 * 提供悬浮按钮和对话框功能
 */
class FloatingAI {
    constructor() {
        this.isOpen = false;
        this.messages = [];
        this.isTyping = false;
        this.container = null;
        this.dialog = null;
        this.messagesContainer = null;
        this.inputField = null;
        this.sendBtn = null;
        
        this.init();
    }

    /**
     * 初始化悬浮AI助手
     */
    init() {
        this.createFloatingButton();
        this.bindEvents();
        this.loadMessages();
        
        // 添加欢迎消息
        if (this.messages.length === 0) {
            this.addWelcomeMessage();
        }
    }

    /**
     * 创建悬浮按钮和对话框
     */
    createFloatingButton() {
        const html = `
            <div class="floating-ai-container" id="floatingAI">
                <!-- 悬浮按钮 -->
                <button class="floating-ai-btn" id="floatingAIBtn">
                    <div class="floating-ai-icon"></div>
                    <div class="floating-ai-status"></div>
                </button>
                
                <!-- 对话框 -->
                <div class="floating-ai-dialog" id="floatingAIDialog">
                    <!-- 头部 -->
                    <div class="floating-ai-header">
                        <h3 class="floating-ai-title">
                            <div class="floating-ai-avatar"></div>
                            AI助手
                        </h3>
                        <button class="floating-ai-close" id="floatingAIClose">&times;</button>
                    </div>
                    
                    <!-- 内容区域 -->
                    <div class="floating-ai-content">
                        <!-- 消息区域 -->
                        <div class="floating-ai-messages" id="floatingAIMessages">
                            <!-- 消息将在这里动态添加 -->
                        </div>
                        
                        <!-- 快捷操作 -->
                        <div class="floating-quick-actions">
                            <div class="floating-quick-buttons">
                                <button class="floating-quick-btn" data-action="ticket-query">车次查询</button>
                                <button class="floating-quick-btn" data-action="booking-help">购票咨询</button>
                                <button class="floating-quick-btn" data-action="refund-info">退改签</button>
                                <button class="floating-quick-btn" data-action="station-info">车站信息</button>
                            </div>
                        </div>
                        
                        <!-- 输入区域 -->
                        <div class="floating-ai-input">
                            <div class="floating-input-container">
                                <textarea 
                                    class="floating-input-field" 
                                    id="floatingAIInput" 
                                    placeholder="请输入您的问题..."
                                    rows="1"
                                ></textarea>
                                <button class="floating-send-btn" id="floatingAISend" disabled>
                                    <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                                        <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
                                    </svg>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        document.body.insertAdjacentHTML('beforeend', html);
        
        // 获取DOM元素引用
        this.container = document.getElementById('floatingAI');
        this.dialog = document.getElementById('floatingAIDialog');
        this.messagesContainer = document.getElementById('floatingAIMessages');
        this.inputField = document.getElementById('floatingAIInput');
        this.sendBtn = document.getElementById('floatingAISend');
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        // 悬浮按钮点击事件
        document.getElementById('floatingAIBtn').addEventListener('click', () => {
            this.toggleDialog();
        });
        
        // 关闭按钮点击事件
        document.getElementById('floatingAIClose').addEventListener('click', () => {
            this.closeDialog();
        });
        
        // 发送按钮点击事件
        this.sendBtn.addEventListener('click', () => {
            this.sendMessage();
        });
        
        // 输入框事件
        this.inputField.addEventListener('input', () => {
            this.handleInputChange();
        });
        
        this.inputField.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.sendMessage();
            }
        });
        
        // 快捷按钮事件
        document.querySelectorAll('.floating-quick-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const action = e.target.dataset.action;
                this.handleQuickAction(action);
            });
        });
        
        // 点击对话框外部关闭
        document.addEventListener('click', (e) => {
            if (this.isOpen && !this.container.contains(e.target)) {
                this.closeDialog();
            }
        });
        
        // ESC键关闭
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.isOpen) {
                this.closeDialog();
            }
        });
    }

    /**
     * 切换对话框显示状态
     */
    toggleDialog() {
        if (this.isOpen) {
            this.closeDialog();
        } else {
            this.openDialog();
        }
    }

    /**
     * 打开对话框
     */
    openDialog() {
        this.isOpen = true;
        this.dialog.classList.add('show');
        this.inputField.focus();
        this.scrollToBottom();
    }

    /**
     * 关闭对话框
     */
    closeDialog() {
        this.isOpen = false;
        this.dialog.classList.remove('show');
    }

    /**
     * 处理输入框变化
     */
    handleInputChange() {
        const value = this.inputField.value.trim();
        this.sendBtn.disabled = !value;
        
        // 自动调整输入框高度
        this.inputField.style.height = 'auto';
        this.inputField.style.height = Math.min(this.inputField.scrollHeight, 80) + 'px';
    }

    /**
     * 发送消息
     */
    sendMessage() {
        const message = this.inputField.value.trim();
        if (!message || this.isTyping) return;
        
        // 添加用户消息
        this.addMessage({
            type: 'user',
            content: message,
            timestamp: new Date()
        });
        
        // 清空输入框
        this.inputField.value = '';
        this.handleInputChange();
        
        // 模拟AI回复
        this.simulateAIResponse(message);
    }

    /**
     * 处理快捷操作
     */
    handleQuickAction(action) {
        const quickMessages = {
            'ticket-query': '我想查询车次信息',
            'booking-help': '购票流程是怎样的？',
            'refund-info': '如何办理退票和改签？',
            'station-info': '请告诉我车站相关信息'
        };
        
        const message = quickMessages[action];
        if (message) {
            this.inputField.value = message;
            this.handleInputChange();
            this.sendMessage();
        }
    }

    /**
     * 添加消息
     */
    addMessage(message) {
        this.messages.push(message);
        this.renderMessage(message);
        this.saveMessages();
        this.scrollToBottom();
    }

    /**
     * 渲染消息
     */
    renderMessage(message) {
        const messageEl = document.createElement('div');
        messageEl.className = `floating-message ${message.type}`;
        
        messageEl.innerHTML = `
            <div class="floating-message-avatar ${message.type}"></div>
            <div class="floating-message-bubble">
                ${this.formatMessageContent(message.content)}
            </div>
        `;
        
        this.messagesContainer.appendChild(messageEl);
        
        // 添加动画效果
        setTimeout(() => {
            messageEl.style.opacity = '0';
            messageEl.style.transform = 'translateY(10px)';
            messageEl.style.transition = 'all 0.3s ease';
            
            requestAnimationFrame(() => {
                messageEl.style.opacity = '1';
                messageEl.style.transform = 'translateY(0)';
            });
        }, 10);
    }

    /**
     * 格式化消息内容
     */
    formatMessageContent(content) {
        // 处理换行
        content = content.replace(/\n/g, '<br>');
        
        // 处理链接
        content = content.replace(/(https?:\/\/[^\s]+)/g, '<a href="$1" target="_blank">$1</a>');
        
        return content;
    }

    /**
     * 模拟AI回复
     */
    simulateAIResponse(userMessage) {
        this.showTypingIndicator();
        
        // 模拟延迟
        setTimeout(() => {
            this.hideTypingIndicator();
            
            const response = this.generateAIResponse(userMessage);
            this.addMessage({
                type: 'ai',
                content: response,
                timestamp: new Date()
            });
        }, 1000 + Math.random() * 2000);
    }

    /**
     * 生成AI回复
     */
    generateAIResponse(userMessage) {
        const message = userMessage.toLowerCase();
        
        // 车次查询相关
        if (message.includes('车次') || message.includes('查询') || message.includes('时刻表')) {
            return '您可以在首页的车票查询功能中输入出发地和目的地来查询车次信息。我们提供实时的列车时刻表、票价和余票信息。需要我帮您查询具体的车次吗？';
        }
        
        // 购票相关
        if (message.includes('购票') || message.includes('买票') || message.includes('订票')) {
            return '购票流程很简单：\n1. 在首页选择出发地、目的地和出行日期\n2. 点击查询，选择合适的车次\n3. 选择座位类型和数量\n4. 填写乘客信息\n5. 确认订单并支付\n\n整个过程大约需要3-5分钟。有什么具体问题吗？';
        }
        
        // 退改签相关
        if (message.includes('退票') || message.includes('改签') || message.includes('退改')) {
            return '关于退改签规定：\n\n🎫 退票：\n• 开车前2小时以上：收取5%手续费\n• 开车前2小时以内：收取10%手续费\n• 开车后：不予退票\n\n🔄 改签：\n• 免费改签一次\n• 只能改签到同等级或更高等级座位\n• 需在开车前30分钟完成\n\n您可以在"个人中心-我的订单"中操作。';
        }
        
        // 车站信息相关
        if (message.includes('车站') || message.includes('地址') || message.includes('位置')) {
            return '我可以为您提供全国主要车站的信息，包括：\n• 车站地址和交通指南\n• 车站设施和服务\n• 进站和候车须知\n• 周边酒店和餐饮\n\n请告诉我您需要了解哪个车站的信息？';
        }
        
        // 积分相关
        if (message.includes('积分') || message.includes('会员') || message.includes('等级')) {
            return '关于会员积分系统：\n\n💎 积分获取：\n• 每消费1元获得1积分\n• 完成订单评价额外获得10积分\n• 邀请好友注册获得50积分\n\n🎁 积分用途：\n• 积分商城兑换礼品\n• 抵扣车票费用（100积分=1元）\n• 升级会员等级享受更多优惠\n\n您当前的积分可以在个人中心查看。';
        }
        
        // 默认回复
        const defaultResponses = [
            '感谢您的咨询！我是您的专属AI助手，可以帮您解答关于购票、车次查询、退改签等问题。请告诉我您需要什么帮助？',
            '我理解您的问题。作为铁路订票系统的AI助手，我可以为您提供购票指导、车次信息查询、政策解答等服务。有什么具体需要帮助的吗？',
            '您好！我可以协助您处理各种铁路出行相关的问题。无论是购票流程、车次查询还是退改签政策，我都很乐意为您详细解答。',
            '感谢您使用我们的服务！如果您有任何关于火车票预订、车次时刻、车站信息或其他出行相关的问题，请随时告诉我，我会尽力帮助您。'
        ];
        
        return defaultResponses[Math.floor(Math.random() * defaultResponses.length)];
    }

    /**
     * 显示打字指示器
     */
    showTypingIndicator() {
        if (this.isTyping) return;
        
        this.isTyping = true;
        const typingEl = document.createElement('div');
        typingEl.className = 'floating-typing';
        typingEl.id = 'floatingTyping';
        typingEl.innerHTML = `
            <span>AI助手正在输入</span>
            <div class="floating-typing-dots">
                <div class="floating-typing-dot"></div>
                <div class="floating-typing-dot"></div>
                <div class="floating-typing-dot"></div>
            </div>
        `;
        
        this.messagesContainer.appendChild(typingEl);
        this.scrollToBottom();
    }

    /**
     * 隐藏打字指示器
     */
    hideTypingIndicator() {
        this.isTyping = false;
        const typingEl = document.getElementById('floatingTyping');
        if (typingEl) {
            typingEl.remove();
        }
    }

    /**
     * 添加欢迎消息
     */
    addWelcomeMessage() {
        const welcomeMessage = {
            type: 'ai',
            content: '您好！我是您的专属AI助手 🤖\n\n我可以帮助您：\n• 查询车次信息和时刻表\n• 指导购票和选座流程\n• 解答退改签政策\n• 提供车站和出行信息\n\n有什么问题请随时告诉我！',
            timestamp: new Date()
        };
        
        this.addMessage(welcomeMessage);
    }

    /**
     * 滚动到底部
     */
    scrollToBottom() {
        setTimeout(() => {
            this.messagesContainer.scrollTop = this.messagesContainer.scrollHeight;
        }, 100);
    }

    /**
     * 保存消息到本地存储
     */
    saveMessages() {
        try {
            localStorage.setItem('floatingAI_messages', JSON.stringify(this.messages));
        } catch (e) {
            console.warn('无法保存消息到本地存储:', e);
        }
    }

    /**
     * 从本地存储加载消息
     */
    loadMessages() {
        try {
            const saved = localStorage.getItem('floatingAI_messages');
            if (saved) {
                this.messages = JSON.parse(saved);
                this.messages.forEach(message => {
                    this.renderMessage(message);
                });
            }
        } catch (e) {
            console.warn('无法从本地存储加载消息:', e);
            this.messages = [];
        }
    }

    /**
     * 清空对话历史
     */
    clearMessages() {
        this.messages = [];
        this.messagesContainer.innerHTML = '';
        this.saveMessages();
        this.addWelcomeMessage();
    }

    /**
     * 显示/隐藏悬浮按钮
     */
    show() {
        this.container.classList.remove('hidden');
    }

    hide() {
        this.container.classList.add('hidden');
        this.closeDialog();
    }

    /**
     * 销毁组件
     */
    destroy() {
        if (this.container) {
            this.container.remove();
        }
    }
}

// 自动初始化
document.addEventListener('DOMContentLoaded', () => {
    // 检查是否已经存在悬浮AI助手
    if (!document.getElementById('floatingAI')) {
        window.floatingAI = new FloatingAI();
    }
});

// 导出类供其他脚本使用
if (typeof module !== 'undefined' && module.exports) {
    module.exports = FloatingAI;
}

// 全局访问
window.FloatingAI = FloatingAI;