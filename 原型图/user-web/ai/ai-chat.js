// AI 聊天功能的 JavaScript 代码

// DOM 元素
const messageInput = document.getElementById('messageInput');
const sendBtn = document.getElementById('sendBtn');
const messagesContainer = document.getElementById('messagesContainer');
const transferBtn = document.getElementById('transferBtn');
const clearBtn = document.getElementById('clearBtn');
const newChatBtn = document.getElementById('newChatBtn');

// 弹窗相关
const transferModal = document.getElementById('transferModal');
const closeModalBtn = document.getElementById('closeModalBtn');
const cancelTransferBtn = document.getElementById('cancelTransferBtn');
const confirmTransferBtn = document.getElementById('confirmTransferBtn');
const connectingModal = document.getElementById('connectingModal');

// 快捷问题按钮
const quickQuestionBtns = document.querySelectorAll('.quick-question-btn');

// 状态变量
let isHumanService = false; // 是否已转接人工客服
let messageId = 0; // 消息ID计数器

// ============ 初始化 ============
function init() {
    // 绑定事件监听器
    sendBtn.addEventListener('click', sendMessage);
    messageInput.addEventListener('keydown', handleKeyDown);
    transferBtn.addEventListener('click', showTransferModal);
    clearBtn.addEventListener('click', clearChat);
    newChatBtn.addEventListener('click', newChat);
    
    // 弹窗事件
    closeModalBtn.addEventListener('click', hideTransferModal);
    cancelTransferBtn.addEventListener('click', hideTransferModal);
    confirmTransferBtn.addEventListener('click', confirmTransfer);
    
    // 快捷问题点击事件
    quickQuestionBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const question = btn.getAttribute('data-question');
            sendUserMessage(question);
        });
    });
    
    // 点击弹窗外部关闭
    transferModal.addEventListener('click', (e) => {
        if (e.target === transferModal) {
            hideTransferModal();
        }
    });
}

// ============ 消息发送 ============
function sendMessage() {
    const message = messageInput.value.trim();
    if (!message) return;
    
    sendUserMessage(message);
    messageInput.value = '';
    messageInput.style.height = 'auto';
}

function sendUserMessage(message) {
    // 显示用户消息
    addMessage('user', message);
    
    // 模拟AI回复（延迟1-2秒）
    setTimeout(() => {
        if (isHumanService) {
            // 人工客服回复
            addMessage('human', getHumanResponse(message));
        } else {
            // AI回复
            addMessage('ai', getAIResponse(message));
        }
    }, 1000 + Math.random() * 1000);
}

// ============ 添加消息到界面 ============
function addMessage(type, content) {
    messageId++;
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}-message`;
    messageDiv.setAttribute('data-id', messageId);
    
    const avatarDiv = document.createElement('div');
    avatarDiv.className = 'message-avatar';
    
    const avatarPlaceholder = document.createElement('div');
    avatarPlaceholder.className = 'avatar-placeholder';
    
    if (type === 'ai') {
        avatarPlaceholder.textContent = '🤖';
    } else if (type === 'human') {
        avatarPlaceholder.textContent = '👤';
    } else if (type === 'user') {
        avatarPlaceholder.textContent = '😊';
    }
    
    avatarDiv.appendChild(avatarPlaceholder);
    
    const contentDiv = document.createElement('div');
    contentDiv.className = 'message-content';
    
    const bubbleDiv = document.createElement('div');
    bubbleDiv.className = 'message-bubble';
    
    if (typeof content === 'string') {
        bubbleDiv.innerHTML = formatMessageContent(content);
    } else {
        bubbleDiv.appendChild(content);
    }
    
    const timeDiv = document.createElement('div');
    timeDiv.className = 'message-time';
    timeDiv.textContent = getCurrentTime();
    
    contentDiv.appendChild(bubbleDiv);
    contentDiv.appendChild(timeDiv);
    
    messageDiv.appendChild(avatarDiv);
    messageDiv.appendChild(contentDiv);
    
    messagesContainer.appendChild(messageDiv);
    
    // 滚动到底部
    scrollToBottom();
}

// 添加系统消息
function addSystemMessage(content) {
    const messageDiv = document.createElement('div');
    messageDiv.className = 'message system-message';
    
    const contentDiv = document.createElement('div');
    contentDiv.className = 'message-content';
    
    const bubbleDiv = document.createElement('div');
    bubbleDiv.className = 'message-bubble';
    bubbleDiv.textContent = content;
    
    contentDiv.appendChild(bubbleDiv);
    messageDiv.appendChild(contentDiv);
    
    messagesContainer.appendChild(messageDiv);
    scrollToBottom();
}

// ============ AI 回复逻辑 ============
function getAIResponse(userMessage) {
    const lowerMessage = userMessage.toLowerCase();
    
    // 购票相关
    if (lowerMessage.includes('购票') || lowerMessage.includes('订票') || lowerMessage.includes('买票')) {
        return `
            <p>关于购票流程，我为您详细说明：</p>
            <ul>
                <li>📱 登录系统：使用手机号和密码登录</li>
                <li>🔍 查询车次：选择出发地、目的地和日期</li>
                <li>🎫 选择车次：根据时间和座位选择合适车次</li>
                <li>👥 添加乘客：填写乘客信息</li>
                <li>💳 在线支付：支持支付宝、微信支付</li>
                <li>✅ 获取车票：支付成功后可查看电子票</li>
            </ul>
            <p>如需帮助，我可以转接人工客服为您服务 😊</p>
        `;
    }
    
    // 退票改签
    if (lowerMessage.includes('退票') || lowerMessage.includes('改签')) {
        return `
            <p>退票和改签政策如下：</p>
            <p><strong>退票规定：</strong></p>
            <ul>
                <li>开车前15天以上退票：不收取手续费</li>
                <li>开车前48小时至15天：收取票价5%手续费</li>
                <li>开车前24-48小时：收取票价10%手续费</li>
                <li>开车前24小时内：收取票价20%手续费</li>
            </ul>
            <p><strong>改签规定：</strong></p>
            <ul>
                <li>开车前48小时以上：可改签预售期内任意车次</li>
                <li>开车前48小时以内：只能改签开车前的其他车次</li>
                <li>改签只能办理一次</li>
            </ul>
        `;
    }
    
    // 学生票
    if (lowerMessage.includes('学生票')) {
        return `
            <p>学生票购买须知：</p>
            <ul>
                <li>📚 优惠范围：每年可购买4次学生优惠票</li>
                <li>💰 优惠幅度：普通硬座票价的50%，动车二等座75%</li>
                <li>📅 优惠时间：每年寒暑假（12月1日-3月31日，6月1日-9月30日）</li>
                <li>🎓 购票条件：需持有学生证和优惠卡</li>
                <li>✅ 取票验证：需携带学生证和身份证</li>
            </ul>
            <p>首次购买学生票需要在车站窗口核验学生资质哦！</p>
        `;
    }
    
    // 儿童票
    if (lowerMessage.includes('儿童') || lowerMessage.includes('小孩')) {
        return `
            <p>儿童乘车规定：</p>
            <ul>
                <li>👶 免票：身高1.2米以下儿童免票（需成人陪同）</li>
                <li>🧒 儿童票：身高1.2-1.5米儿童购买儿童票（票价的50%）</li>
                <li>👦 全票：身高1.5米以上需购买全价票</li>
                <li>🎫 每位成人可免费携带1名身高1.2米以下儿童</li>
                <li>📝 购票时需提供儿童身份信息</li>
            </ul>
        `;
    }
    
    // 车站信息
    if (lowerMessage.includes('车站') || lowerMessage.includes('站点')) {
        return `
            <p>关于车站信息查询：</p>
            <p>您可以通过以下方式查询车站信息：</p>
            <ul>
                <li>🔍 在首页搜索框输入车站名称</li>
                <li>📍 查看车站地址、联系电话</li>
                <li>🚇 了解车站周边交通信息</li>
                <li>🏢 查询车站设施和服务</li>
            </ul>
            <p>如需查询具体车站信息，请告诉我车站名称！</p>
        `;
    }
    
    // 支付问题
    if (lowerMessage.includes('支付') || lowerMessage.includes('付款')) {
        return `
            <p>支付相关说明：</p>
            <ul>
                <li>💳 支付方式：支持支付宝、微信支付</li>
                <li>⏰ 支付时限：下单后30分钟内完成支付</li>
                <li>🔒 支付安全：采用加密通道，保障资金安全</li>
                <li>📧 支付凭证：支付成功后会发送电子凭证</li>
            </ul>
            <p>如遇支付问题，建议您转接人工客服处理！</p>
        `;
    }
    
    // 默认回复
    return `
        <p>感谢您的咨询！关于"${userMessage}"的问题，我建议您：</p>
        <ul>
            <li>📖 查看常见问题解答</li>
            <li>👤 转接人工客服获取更详细的帮助</li>
            <li>📞 拨打客服热线：12306</li>
        </ul>
        <p>如需转接人工客服，请点击右上角的"转人工"按钮！</p>
    `;
}

// 人工客服回复
function getHumanResponse(userMessage) {
    const responses = [
        '好的，我已经收到您的问题，正在为您查询相关信息...',
        '明白了，让我帮您核实一下...',
        '您的问题我已经了解，稍等片刻...',
        '我会尽快为您处理这个问题，请稍候...',
    ];
    
    return responses[Math.floor(Math.random() * responses.length)];
}

// ============ 转接人工客服 ============
function showTransferModal() {
    transferModal.classList.add('active');
}

function hideTransferModal() {
    transferModal.classList.remove('active');
}

function confirmTransfer() {
    hideTransferModal();
    
    // 显示正在连接弹窗
    connectingModal.classList.add('active');
    
    // 模拟连接过程（2-3秒）
    setTimeout(() => {
        connectingModal.classList.remove('active');
        
        // 更新状态
        isHumanService = true;
        
        // 更新头部信息
        document.querySelector('.chat-name').textContent = '人工客服 - 小李';
        document.querySelector('.chat-status').textContent = '在线 - 正在服务中';
        document.querySelector('.chat-avatar .avatar-placeholder').textContent = '👤';
        
        // 显示转接成功消息
        addSystemMessage('已成功转接人工客服，客服小李为您服务');
        
        // 添加人工客服欢迎消息
        setTimeout(() => {
            addMessage('human', '您好！我是客服小李，很高兴为您服务。请问有什么可以帮到您的吗？😊');
        }, 500);
        
    }, 2000 + Math.random() * 1000);
}

// ============ 其他功能 ============
function clearChat() {
    if (confirm('确定要清空当前对话吗？')) {
        // 保留欢迎消息
        const messages = messagesContainer.querySelectorAll('.message');
        messages.forEach((msg, index) => {
            if (index > 0) { // 跳过第一条欢迎消息
                msg.remove();
            }
        });
        
        addSystemMessage('对话已清空');
    }
}

function newChat() {
    if (confirm('确定要新建对话吗？当前对话将被保存到历史记录。')) {
        location.reload();
    }
}

// ============ 辅助函数 ============
function handleKeyDown(e) {
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
    }
}

function getCurrentTime() {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
}

function scrollToBottom() {
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

function formatMessageContent(content) {
    // 将换行符转换为<br>
    return content.replace(/\n/g, '<br>');
}

// 初始化
init();

// 模拟打字效果（可选）
function typeWriter(element, text, speed = 50) {
    let i = 0;
    element.innerHTML = '';
    
    function type() {
        if (i < text.length) {
            element.innerHTML += text.charAt(i);
            i++;
            setTimeout(type, speed);
        }
    }
    
    type();
}

// 自动调整输入框高度
messageInput.addEventListener('input', function() {
    this.style.height = 'auto';
    this.style.height = (this.scrollHeight) + 'px';
});

console.log('AI 聊天系统已加载');



