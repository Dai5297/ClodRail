// AI助手页面JavaScript

class AIAssistant {
    constructor() {
        this.messages = [];
        this.isTyping = false;
        this.settings = {
            voicePlayback: true,
            autoSuggestion: true,
            messageNotification: false,
            historyDuration: 7
        };
        this.currentRating = 0;
        this.init();
    }

    init() {
        this.loadSettings();
        this.loadChatHistory();
        this.bindEvents();
        this.updateCurrentTime();
        this.startTimeUpdate();
        this.initSuggestions();
    }

    loadSettings() {
        const savedSettings = Utils.getLocalStorage('aiAssistantSettings');
        if (savedSettings) {
            this.settings = { ...this.settings, ...savedSettings };
        }
        this.applySettings();
    }

    applySettings() {
        document.getElementById('voicePlayback').checked = this.settings.voicePlayback;
        document.getElementById('autoSuggestion').checked = this.settings.autoSuggestion;
        document.getElementById('messageNotification').checked = this.settings.messageNotification;
        document.getElementById('historyDuration').value = this.settings.historyDuration;
        
        // 应用自动建议设置
        const suggestions = document.getElementById('inputSuggestions');
        suggestions.style.display = this.settings.autoSuggestion ? 'flex' : 'none';
    }

    loadChatHistory() {
        const history = Utils.getLocalStorage('aiChatHistory') || [];
        const cutoffDate = new Date();
        cutoffDate.setDate(cutoffDate.getDate() - this.settings.historyDuration);
        
        // 过滤过期消息
        this.messages = history.filter(msg => {
            if (this.settings.historyDuration === 0) return true;
            return new Date(msg.timestamp) > cutoffDate;
        });
        
        this.renderMessages();
    }

    bindEvents() {
        // 发送消息
        document.getElementById('sendBtn').addEventListener('click', () => {
            this.sendMessage();
        });

        // 输入框事件
        const messageInput = document.getElementById('messageInput');
        messageInput.addEventListener('input', () => {
            this.handleInputChange();
        });

        messageInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.sendMessage();
            }
        });

        // 快捷功能按钮
        document.querySelectorAll('.quick-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const action = btn.dataset.action;
                this.handleQuickAction(action);
            });
        });

        // 工具按钮
        document.getElementById('voiceBtn').addEventListener('click', () => {
            this.toggleVoiceInput();
        });

        document.getElementById('imageBtn').addEventListener('click', () => {
            document.getElementById('imageUpload').click();
        });

        document.getElementById('locationBtn').addEventListener('click', () => {
            this.getLocation();
        });

        // 图片上传
        document.getElementById('imageUpload').addEventListener('change', (e) => {
            this.handleImageUpload(e);
        });

        // 建议词点击
        document.querySelectorAll('.suggestion-item').forEach(item => {
            item.addEventListener('click', () => {
                this.selectSuggestion(item.textContent);
            });
        });

        // 常见问题点击
        document.querySelectorAll('.faq-item').forEach(item => {
            item.addEventListener('click', () => {
                const question = item.dataset.question;
                this.selectSuggestion(question);
            });
        });

        // 清空对话
        document.getElementById('clearChatBtn').addEventListener('click', () => {
            this.clearChat();
        });

        // 设置按钮
        document.getElementById('settingsBtn').addEventListener('click', () => {
            this.showSettingsModal();
        });

        // 设置模态框
        document.getElementById('closeSettingsModal').addEventListener('click', () => {
            this.hideSettingsModal();
        });

        document.getElementById('cancelSettingsBtn').addEventListener('click', () => {
            this.hideSettingsModal();
        });

        document.getElementById('saveSettingsBtn').addEventListener('click', () => {
            this.saveSettings();
        });

        // 评价按钮
        document.getElementById('rateServiceBtn').addEventListener('click', () => {
            this.showRatingModal();
        });

        // 评价模态框
        document.getElementById('closeRatingModal').addEventListener('click', () => {
            this.hideRatingModal();
        });

        document.getElementById('cancelRatingBtn').addEventListener('click', () => {
            this.hideRatingModal();
        });

        document.getElementById('submitRatingBtn').addEventListener('click', () => {
            this.submitRating();
        });

        // 评分星星
        document.querySelectorAll('#ratingStarsInput .star').forEach((star, index) => {
            star.addEventListener('click', () => {
                this.setRating(index + 1);
            });

            star.addEventListener('mouseenter', () => {
                this.highlightStars(index + 1);
            });
        });

        document.getElementById('ratingStarsInput').addEventListener('mouseleave', () => {
            this.highlightStars(this.currentRating);
        });

        // 模态框外部点击关闭
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal')) {
                e.target.classList.remove('show');
            }
        });
    }

    handleInputChange() {
        const input = document.getElementById('messageInput');
        const sendBtn = document.getElementById('sendBtn');
        
        // 自动调整高度
        input.style.height = 'auto';
        input.style.height = Math.min(input.scrollHeight, 120) + 'px';
        
        // 控制发送按钮状态
        sendBtn.disabled = !input.value.trim();
    }

    sendMessage() {
        const input = document.getElementById('messageInput');
        const message = input.value.trim();
        
        if (!message || this.isTyping) return;
        
        // 添加用户消息
        this.addMessage({
            type: 'user',
            content: message,
            timestamp: new Date().toISOString()
        });
        
        // 清空输入框
        input.value = '';
        input.style.height = 'auto';
        document.getElementById('sendBtn').disabled = true;
        
        // 模拟AI回复
        this.simulateAIResponse(message);
    }

    addMessage(message) {
        this.messages.push(message);
        this.renderMessage(message);
        this.scrollToBottom();
        this.saveChatHistory();
    }

    renderMessages() {
        const container = document.getElementById('chatMessages');
        // 保留欢迎消息，只渲染历史消息
        const welcomeMessage = container.querySelector('.ai-message');
        container.innerHTML = '';
        if (welcomeMessage) {
            container.appendChild(welcomeMessage);
        }
        
        this.messages.forEach(message => {
            this.renderMessage(message, false);
        });
        
        this.scrollToBottom();
    }

    renderMessage(message, animate = true) {
        const container = document.getElementById('chatMessages');
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${message.type}-message`;
        if (animate) {
            messageDiv.style.opacity = '0';
            messageDiv.style.transform = 'translateY(20px)';
        }
        
        const avatarSrc = message.type === 'user' 
            ? 'https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture%20simple&image_size=square'
            : 'https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=AI%20robot%20assistant%20friendly%20blue%20technology&image_size=square';
        
        messageDiv.innerHTML = `
            <div class="message-avatar">
                <img src="${avatarSrc}" alt="${message.type === 'user' ? '用户' : 'AI助手'}">
            </div>
            <div class="message-content">
                <div class="message-bubble">
                    ${this.formatMessageContent(message.content)}
                </div>
                <div class="message-time">${this.formatTime(message.timestamp)}</div>
            </div>
        `;
        
        container.appendChild(messageDiv);
        
        if (animate) {
            setTimeout(() => {
                messageDiv.style.transition = 'all 0.3s ease';
                messageDiv.style.opacity = '1';
                messageDiv.style.transform = 'translateY(0)';
            }, 50);
        }
    }

    formatMessageContent(content) {
        // 处理换行和简单的格式化
        return content
            .replace(/\n/g, '<br>')
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/\*(.*?)\*/g, '<em>$1</em>');
    }

    formatTime(timestamp) {
        const date = new Date(timestamp);
        const now = new Date();
        const diff = now - date;
        
        if (diff < 60000) {
            return '刚刚';
        } else if (diff < 3600000) {
            return `${Math.floor(diff / 60000)}分钟前`;
        } else if (date.toDateString() === now.toDateString()) {
            return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
        } else {
            return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
        }
    }

    scrollToBottom() {
        const container = document.getElementById('chatMessages');
        container.scrollTop = container.scrollHeight;
    }

    simulateAIResponse(userMessage) {
        this.isTyping = true;
        this.showTypingIndicator();
        
        // 模拟思考时间
        setTimeout(() => {
            this.hideTypingIndicator();
            const response = this.generateAIResponse(userMessage);
            
            this.addMessage({
                type: 'ai',
                content: response,
                timestamp: new Date().toISOString()
            });
            
            this.isTyping = false;
            
            // 语音播报
            if (this.settings.voicePlayback) {
                this.speakText(response);
            }
        }, 1000 + Math.random() * 2000);
    }

    generateAIResponse(userMessage) {
        const message = userMessage.toLowerCase();
        
        // 简单的关键词匹配回复
        if (message.includes('车次') || message.includes('火车') || message.includes('高铁')) {
            return '我可以帮您查询车次信息。请告诉我您的出发地和目的地，以及出行日期，我会为您推荐合适的车次。\n\n您也可以直接点击上方的"查询车次"快捷按钮进行查询。';
        }
        
        if (message.includes('购票') || message.includes('买票')) {
            return '关于购票，我来为您介绍一下流程：\n\n1. **选择车次**：在车次查询页面选择合适的班次\n2. **选择座位**：根据需要选择座位类型和具体座位\n3. **填写乘客信息**：添加乘客身份信息\n4. **确认支付**：选择支付方式完成购票\n\n有什么具体问题我可以详细解答。';
        }
        
        if (message.includes('改签') || message.includes('退票')) {
            return '关于改签和退票政策：\n\n**改签规则：**\n• 开车前48小时以上：免费改签一次\n• 开车前24-48小时：收取5%手续费\n• 开车前24小时内：收取20%手续费\n\n**退票规则：**\n• 开车前15天以上：免手续费\n• 开车前48小时-15天：收取5%手续费\n• 开车前24-48小时：收取10%手续费\n• 开车前24小时内：收取20%手续费';
        }
        
        if (message.includes('学生票')) {
            return '学生票购买指南：\n\n**购买条件：**\n• 全日制大中专学生\n• 持有有效学生证\n• 家庭居住地与学校不在同一城市\n\n**优惠幅度：**\n• 硬座：75折\n• 软座：无优惠\n• 硬卧：75折\n• 软卧：无优惠\n\n**购买次数：**\n每学年可购买4次学生票（10月1日-次年9月30日）';
        }
        
        if (message.includes('儿童票')) {
            return '儿童票购买说明：\n\n**免票儿童：**\n• 身高1.2米以下儿童免票\n• 需有成人陪同\n• 不占座位\n\n**半价票：**\n• 身高1.2-1.5米儿童\n• 享受半价优惠\n• 需购买儿童票\n\n**全价票：**\n• 身高1.5米以上按成人票价';
        }
        
        if (message.includes('天气')) {
            return '我可以为您查询目的地天气信息，帮助您做好出行准备。\n\n请告诉我您要查询哪个城市的天气，我会为您提供：\n• 当前天气状况\n• 未来3天天气预报\n• 出行建议\n\n这样您就可以提前准备合适的衣物了。';
        }
        
        if (message.includes('路线') || message.includes('规划')) {
            return '我可以为您制定最优出行路线：\n\n**路线规划包括：**\n• 最快路线（时间最短）\n• 最省钱路线（费用最低）\n• 最舒适路线（换乘最少）\n\n**考虑因素：**\n• 出发时间偏好\n• 座位类型偏好\n• 预算范围\n• 是否介意换乘\n\n请告诉我您的具体需求，我来为您定制专属路线。';
        }
        
        if (message.includes('车站')) {
            return '我可以为您提供车站相关信息：\n\n**车站服务：**\n• 车站地址和交通指南\n• 候车室和检票口信息\n• 车站内设施（餐厅、商店等）\n• 无障碍设施信息\n\n**实用信息：**\n• 停车场位置和收费\n• 地铁/公交换乘指南\n• 车站周边酒店推荐\n\n请告诉我您需要了解哪个车站的信息。';
        }
        
        if (message.includes('宠物')) {
            return '关于携带宠物乘车的规定：\n\n**不允许携带：**\n• 活体动物（除导盲犬等工作犬）\n• 宠物不能进入客车车厢\n\n**替代方案：**\n• 宠物托运服务（需提前办理）\n• 选择允许携带宠物的其他交通方式\n\n**导盲犬例外：**\n• 视力残疾人携带导盲犬可以免费乘车\n• 需提供相关证明文件';
        }
        
        // 默认回复
        const defaultResponses = [
            '感谢您的咨询！我是您的专属出行助手，随时为您提供帮助。如果您有具体的出行需求，请详细描述，我会为您提供最合适的建议。',
            '我理解您的问题。作为您的出行顾问，我建议您可以通过上方的快捷功能按钮快速获取相关服务，或者告诉我更多详细信息，我来为您提供个性化建议。',
            '很高兴为您服务！如果您需要查询车次、购票咨询、路线规划等服务，我都可以帮助您。请告诉我您的具体需求，我会尽力为您解答。'
        ];
        
        return defaultResponses[Math.floor(Math.random() * defaultResponses.length)];
    }

    showTypingIndicator() {
        const container = document.getElementById('chatMessages');
        const typingDiv = document.createElement('div');
        typingDiv.className = 'message ai-message typing-message';
        typingDiv.innerHTML = `
            <div class="message-avatar">
                <img src="https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=AI%20robot%20assistant%20friendly%20blue%20technology&image_size=square" alt="AI助手">
            </div>
            <div class="message-content">
                <div class="message-bubble">
                    <div class="typing-indicator">
                        <div class="typing-dot"></div>
                        <div class="typing-dot"></div>
                        <div class="typing-dot"></div>
                    </div>
                </div>
            </div>
        `;
        
        container.appendChild(typingDiv);
        this.scrollToBottom();
    }

    hideTypingIndicator() {
        const typingMessage = document.querySelector('.typing-message');
        if (typingMessage) {
            typingMessage.remove();
        }
    }

    handleQuickAction(action) {
        const actions = {
            'search-train': '我想查询车次信息',
            'ticket-info': '请告诉我购票的流程',
            'route-plan': '帮我规划一下出行路线',
            'weather-info': '查询目的地天气情况',
            'station-info': '我需要了解车站信息',
            'travel-guide': '给我一些出行建议'
        };
        
        const message = actions[action];
        if (message) {
            document.getElementById('messageInput').value = message;
            this.handleInputChange();
        }
    }

    selectSuggestion(text) {
        document.getElementById('messageInput').value = text;
        this.handleInputChange();
        document.getElementById('messageInput').focus();
    }

    initSuggestions() {
        // 动态更新建议词
        const suggestions = [
            '北京到上海的高铁',
            '明天的车票还有吗',
            '如何改签车票',
            '学生票怎么买',
            '儿童票购买规则',
            '退票手续费多少',
            '可以带宠物吗',
            '车站有什么设施'
        ];
        
        const container = document.getElementById('inputSuggestions');
        container.innerHTML = '';
        
        // 随机选择4个建议
        const randomSuggestions = suggestions.sort(() => 0.5 - Math.random()).slice(0, 4);
        
        randomSuggestions.forEach(suggestion => {
            const span = document.createElement('span');
            span.className = 'suggestion-item';
            span.textContent = suggestion;
            span.addEventListener('click', () => {
                this.selectSuggestion(suggestion);
            });
            container.appendChild(span);
        });
    }

    toggleVoiceInput() {
        const btn = document.getElementById('voiceBtn');
        
        if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
            const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
            const recognition = new SpeechRecognition();
            
            recognition.lang = 'zh-CN';
            recognition.continuous = false;
            recognition.interimResults = false;
            
            recognition.onstart = () => {
                btn.classList.add('active');
                Utils.showMessage('正在听取语音...', 'info');
            };
            
            recognition.onresult = (event) => {
                const transcript = event.results[0][0].transcript;
                document.getElementById('messageInput').value = transcript;
                this.handleInputChange();
            };
            
            recognition.onerror = () => {
                Utils.showMessage('语音识别失败，请重试', 'error');
            };
            
            recognition.onend = () => {
                btn.classList.remove('active');
            };
            
            recognition.start();
        } else {
            Utils.showMessage('您的浏览器不支持语音识别', 'warning');
        }
    }

    handleImageUpload(event) {
        const file = event.target.files[0];
        if (!file) return;
        
        if (file.size > 5 * 1024 * 1024) {
            Utils.showMessage('图片大小不能超过5MB', 'warning');
            return;
        }
        
        const reader = new FileReader();
        reader.onload = (e) => {
            const imageData = e.target.result;
            this.addMessage({
                type: 'user',
                content: `<img src="${imageData}" alt="上传的图片" style="max-width: 200px; border-radius: 8px;">`,
                timestamp: new Date().toISOString()
            });
            
            // 模拟图片识别回复
            setTimeout(() => {
                this.addMessage({
                    type: 'ai',
                    content: '我看到您上传了一张图片。虽然我目前还不能完全识别图片内容，但如果这是车票、身份证或其他出行相关文件，我可以为您提供相应的帮助和指导。请告诉我您需要什么帮助？',
                    timestamp: new Date().toISOString()
                });
            }, 1500);
        };
        
        reader.readAsDataURL(file);
        event.target.value = ''; // 清空文件输入
    }

    getLocation() {
        if ('geolocation' in navigator) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const { latitude, longitude } = position.coords;
                    this.addMessage({
                        type: 'user',
                        content: `我的当前位置：纬度 ${latitude.toFixed(6)}, 经度 ${longitude.toFixed(6)}`,
                        timestamp: new Date().toISOString()
                    });
                    
                    // 模拟位置服务回复
                    setTimeout(() => {
                        this.addMessage({
                            type: 'ai',
                            content: '已获取您的位置信息！我可以为您：\n\n• 推荐附近的火车站\n• 查询从当前位置到车站的交通方式\n• 提供周边的餐饮和住宿信息\n\n请告诉我您需要哪种帮助？',
                            timestamp: new Date().toISOString()
                        });
                    }, 1000);
                },
                (error) => {
                    Utils.showMessage('无法获取位置信息，请检查权限设置', 'warning');
                }
            );
        } else {
            Utils.showMessage('您的浏览器不支持定位功能', 'warning');
        }
    }

    speakText(text) {
        if ('speechSynthesis' in window) {
            // 移除HTML标签
            const cleanText = text.replace(/<[^>]*>/g, '').replace(/\n/g, ' ');
            
            const utterance = new SpeechSynthesisUtterance(cleanText);
            utterance.lang = 'zh-CN';
            utterance.rate = 0.9;
            utterance.pitch = 1;
            
            speechSynthesis.speak(utterance);
        }
    }

    clearChat() {
        if (confirm('确定要清空所有对话记录吗？')) {
            this.messages = [];
            this.saveChatHistory();
            
            // 重新渲染，只保留欢迎消息
            const container = document.getElementById('chatMessages');
            const welcomeMessage = container.querySelector('.ai-message');
            container.innerHTML = '';
            if (welcomeMessage) {
                container.appendChild(welcomeMessage);
            }
            
            Utils.showMessage('对话记录已清空', 'success');
        }
    }

    saveChatHistory() {
        Utils.setLocalStorage('aiChatHistory', this.messages);
    }

    updateCurrentTime() {
        const now = new Date();
        const timeString = now.toLocaleTimeString('zh-CN', {
            hour: '2-digit',
            minute: '2-digit'
        });
        document.getElementById('currentTime').textContent = timeString;
    }

    startTimeUpdate() {
        this.updateCurrentTime();
        setInterval(() => {
            this.updateCurrentTime();
        }, 60000); // 每分钟更新一次
    }

    // 设置相关方法
    showSettingsModal() {
        document.getElementById('settingsModal').classList.add('show');
    }

    hideSettingsModal() {
        document.getElementById('settingsModal').classList.remove('show');
    }

    saveSettings() {
        this.settings = {
            voicePlayback: document.getElementById('voicePlayback').checked,
            autoSuggestion: document.getElementById('autoSuggestion').checked,
            messageNotification: document.getElementById('messageNotification').checked,
            historyDuration: parseInt(document.getElementById('historyDuration').value)
        };
        
        Utils.setLocalStorage('aiAssistantSettings', this.settings);
        this.applySettings();
        this.hideSettingsModal();
        
        Utils.showMessage('设置已保存', 'success');
    }

    // 评价相关方法
    showRatingModal() {
        this.currentRating = 0;
        this.highlightStars(0);
        document.getElementById('feedbackText').value = '';
        document.getElementById('ratingModal').classList.add('show');
    }

    hideRatingModal() {
        document.getElementById('ratingModal').classList.remove('show');
    }

    setRating(rating) {
        this.currentRating = rating;
        this.highlightStars(rating);
    }

    highlightStars(rating) {
        const stars = document.querySelectorAll('#ratingStarsInput .star');
        stars.forEach((star, index) => {
            if (index < rating) {
                star.classList.add('active');
            } else {
                star.classList.remove('active');
            }
        });
    }

    submitRating() {
        if (this.currentRating === 0) {
            Utils.showMessage('请先选择评分', 'warning');
            return;
        }
        
        const feedback = document.getElementById('feedbackText').value.trim();
        
        // 保存评价
        const rating = {
            score: this.currentRating,
            feedback: feedback,
            timestamp: new Date().toISOString()
        };
        
        const ratings = Utils.getLocalStorage('aiServiceRatings') || [];
        ratings.push(rating);
        Utils.setLocalStorage('aiServiceRatings', ratings);
        
        this.hideRatingModal();
        Utils.showMessage('感谢您的评价！', 'success');
        
        // 添加感谢消息
        setTimeout(() => {
            this.addMessage({
                type: 'ai',
                content: `感谢您给出 ${this.currentRating} 星评价！您的反馈对我们非常重要，我会继续努力为您提供更好的服务。${feedback ? '\n\n我已记录您的建议：' + feedback : ''}`,
                timestamp: new Date().toISOString()
            });
        }, 500);
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    new AIAssistant();
});

// 导出类供其他模块使用
if (typeof module !== 'undefined' && module.exports) {
    module.exports = AIAssistant;
}