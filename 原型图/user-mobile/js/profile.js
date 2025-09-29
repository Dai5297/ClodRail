// 个人中心页面JavaScript功能

// 个人中心应用对象
const ProfileApp = {
    data: {
        user: null,
        isLoggedIn: false,
        recentOrders: [],
        orderStats: {
            pending: 0,
            paid: 0,
            traveled: 0,
            refunded: 0
        },
        menuItems: [
            {
                section: '出行服务',
                items: [
                    { id: 'passengers', icon: 'users', text: '常用乘车人', badge: null, value: null },
                    { id: 'addresses', icon: 'map-pin', text: '收货地址', badge: null, value: null },
                    { id: 'coupons', icon: 'ticket', text: '优惠券', badge: '3', value: null },
                    { id: 'points', icon: 'star', text: '积分商城', badge: null, value: '1280积分' }
                ]
            },
            {
                section: '账户管理',
                items: [
                    { id: 'security', icon: 'ChromeFilled', text: '账户安全', badge: null, value: null },
                    { id: 'payment', icon: 'credit-card', text: '支付管理', badge: null, value: null },
                    { id: 'notifications', icon: 'bell', text: '消息通知', badge: '5', value: null },
                    { id: 'settings', icon: 'settings', text: '设置', badge: null, value: null }
                ]
            },
            {
                section: '帮助支持',
                items: [
                    { id: 'help', icon: 'help-circle', text: '帮助中心', badge: null, value: null },
                    { id: 'feedback', icon: 'message-circle', text: '意见反馈', badge: null, value: null },
                    { id: 'about', icon: 'info', text: '关于我们', badge: null, value: 'v1.0.0' }
                ]
            }
        ]
    },
    
    init() {
        this.checkLoginStatus();
        this.loadUserData();
        this.loadRecentOrders();
        this.updateOrderStats();
        this.renderUserInfo();
        this.renderOrderStats();
        this.renderRecentOrders();
        this.renderMenuItems();
        this.bindEvents();
    },
    
    // 检查登录状态
    checkLoginStatus() {
        this.data.isLoggedIn = Auth.isLoggedIn();
        this.data.user = Auth.getUserInfo();
    },
    
    // 加载用户数据
    loadUserData() {
        if (this.data.isLoggedIn && !this.data.user.name) {
            // 模拟用户数据
            this.data.user = {
                name: '张三',
                phone: '138****8000',
                avatar: 'https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture%20friendly%20person%20blue%20theme&image_size=square',
                level: 'VIP',
                points: 1280,
                ...this.data.user
            };
            Auth.setUserInfo(this.data.user);
        }
    },
    
    // 加载最近订单
    loadRecentOrders() {
        if (!this.data.isLoggedIn) {
            this.data.recentOrders = [];
            return;
        }
        
        const allOrders = Storage.get('orders', []);
        this.data.recentOrders = allOrders.slice(0, 3); // 显示最近3个订单
    },
    
    // 更新订单统计
    updateOrderStats() {
        if (!this.data.isLoggedIn) {
            this.data.orderStats = { pending: 0, paid: 0, traveled: 0, refunded: 0 };
            return;
        }
        
        const allOrders = Storage.get('orders', []);
        
        this.data.orderStats = {
            pending: allOrders.filter(order => order.status === 'pending').length,
            paid: allOrders.filter(order => order.status === 'paid').length,
            traveled: allOrders.filter(order => order.status === 'traveled').length,
            refunded: allOrders.filter(order => order.status === 'refunded').length
        };
    },
    
    // 渲染用户信息
    renderUserInfo() {
        const userInfoElement = document.querySelector('.user-info');
        if (!userInfoElement) return;
        
        if (this.data.isLoggedIn) {
            userInfoElement.innerHTML = `
                <div class="logged-in">
                    <div class="user-avatar">
                        <img src="${this.data.user.avatar}" alt="头像">
                    </div>
                    <div class="user-details">
                        <div class="user-name">${this.data.user.name}</div>
                        <div class="user-phone">${this.data.user.phone}</div>
                        <div class="user-level">
                            <span class="level-badge">${this.data.user.level}</span>
                            <span class="points">${this.data.user.points}积分</span>
                        </div>
                    </div>
                    <div class="user-actions">
                        <img src="https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=settings%20gear%20icon%20white%20simple&image_size=square" alt="设置" class="settings-icon">
                    </div>
                </div>
            `;
        } else {
            userInfoElement.innerHTML = `
                <div class="not-logged-in" onclick="ProfileApp.showLoginModal()">
                    <div class="login-prompt">
                        <img src="https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=user%20login%20icon%20white%20simple&image_size=square" alt="登录" class="login-icon">
                        <div class="login-text">
                            <div class="login-title">立即登录</div>
                            <div class="login-subtitle">登录后享受更多服务</div>
                        </div>
                    </div>
                    <img src="https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=arrow%20right%20icon%20white%20simple&image_size=square" alt=">" class="login-arrow">
                </div>
            `;
        }
    },
    
    // 渲染订单统计
    renderOrderStats() {
        const statsGrid = document.querySelector('.stats-grid');
        if (!statsGrid) return;
        
        const stats = [
            { key: 'pending', label: '待支付', icon: 'clock', count: this.data.orderStats.pending },
            { key: 'paid', label: '已支付', icon: 'check-circle', count: this.data.orderStats.paid },
            { key: 'traveled', label: '已出行', icon: 'map', count: this.data.orderStats.traveled },
            { key: 'refunded', label: '已退款', icon: 'x-circle', count: this.data.orderStats.refunded }
        ];
        
        statsGrid.innerHTML = stats.map(stat => `
            <div class="stat-item" data-status="${stat.key}">
                <div class="stat-icon">
                    <img src="https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=${stat.icon}%20icon%20blue%20simple&image_size=square" alt="${stat.label}">
                    ${stat.count > 0 ? `<span class="stat-badge">${stat.count}</span>` : ''}
                </div>
                <div class="stat-label">${stat.label}</div>
            </div>
        `).join('');
    },
    
    // 渲染最近订单
    renderRecentOrders() {
        const orderList = document.querySelector('.order-list');
        if (!orderList) return;
        
        if (!this.data.isLoggedIn || this.data.recentOrders.length === 0) {
            orderList.innerHTML = `
                <div class="empty-orders">
                    <img src="https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=empty%20order%20list%20illustration%20blue%20theme&image_size=square" alt="暂无订单" class="empty-image">
                    <div class="empty-text">暂无订单记录</div>
                    ${!this.data.isLoggedIn ? 
                        '<button class="book-now-btn" onclick="ProfileApp.showLoginModal()">立即登录</button>' :
                        '<a href="index.html" class="book-now-btn">立即购票</a>'
                    }
                </div>
            `;
            return;
        }
        
        orderList.innerHTML = this.data.recentOrders.map(order => {
            const statusMap = {
                pending: { text: '待支付', class: 'pending' },
                paid: { text: '已支付', class: 'paid' },
                traveled: { text: '已出行', class: 'traveled' },
                refunded: { text: '已退款', class: 'refunded' }
            };
            
            const status = statusMap[order.status] || { text: '未知', class: 'unknown' };
            
            return `
                <div class="order-item" data-order="${order.id}">
                    <div class="order-header">
                        <div class="order-number">订单号：${order.id.slice(-8)}</div>
                        <div class="order-status ${status.class}">${status.text}</div>
                    </div>
                    <div class="order-content">
                        <div class="train-info">
                            <div class="train-number">${order.trainNumber}</div>
                            <div class="route">${order.from} → ${order.to}</div>
                        </div>
                        <div class="order-details">
                            <div class="travel-date">${Utils.formatDate(new Date(order.departDate), 'MM-DD')} ${order.departTime}</div>
                            <div class="seat-info">${this.getSeatTypeName(order.seatType)} ${order.passengers.length}张</div>
                            <div class="price">¥${order.totalPrice}</div>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    },
    
    // 获取座位类型名称
    getSeatTypeName(seatType) {
        const typeNames = {
            'business': '商务座',
            'first': '一等座',
            'second': '二等座'
        };
        return typeNames[seatType] || seatType;
    },
    
    // 渲染菜单项
    renderMenuItems() {
        const functionMenu = document.querySelector('.function-menu');
        if (!functionMenu) return;
        
        functionMenu.innerHTML = this.data.menuItems.map(section => `
            <div class="menu-section">
                <div class="section-title">${section.section}</div>
                <div class="menu-items">
                    ${section.items.map(item => `
                        <div class="menu-item" data-action="${item.id}">
                            <img src="https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=${item.icon}%20icon%20gray%20simple&image_size=square" alt="${item.text}" class="menu-icon">
                            <div class="menu-text">${item.text}</div>
                            ${item.badge ? `<div class="menu-badge">${item.badge}</div>` : ''}
                            ${item.value ? `<div class="menu-value">${item.value}</div>` : ''}
                            <img src="https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=arrow%20right%20icon%20gray%20simple&image_size=square" alt=">" class="menu-arrow">
                        </div>
                    `).join('')}
                </div>
            </div>
        `).join('');
        
        // 添加退出登录按钮
        if (this.data.isLoggedIn) {
            functionMenu.innerHTML += `
                <div class="logout-section">
                    <button class="logout-btn" onclick="ProfileApp.logout()">退出登录</button>
                </div>
            `;
        }
    },
    
    // 绑定事件
    bindEvents() {
        // 订单统计点击
        document.querySelectorAll('.stat-item').forEach(item => {
            item.addEventListener('click', (e) => {
                const status = e.currentTarget.getAttribute('data-status');
                this.viewOrdersByStatus(status);
            });
        });
        
        // 订单项点击
        document.addEventListener('click', (e) => {
            const orderItem = e.target.closest('.order-item');
            if (orderItem) {
                const orderId = orderItem.getAttribute('data-order');
                this.viewOrderDetail(orderId);
            }
        });
        
        // 菜单项点击
        document.querySelectorAll('.menu-item').forEach(item => {
            item.addEventListener('click', (e) => {
                const action = e.currentTarget.getAttribute('data-action');
                this.handleMenuAction(action);
            });
        });
        
        // 设置按钮点击
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('settings-icon')) {
                this.handleMenuAction('settings');
            }
        });
        
        // 登录弹窗事件
        this.bindLoginModalEvents();
    },
    
    // 绑定登录弹窗事件
    bindLoginModalEvents() {
        // 关闭按钮
        const closeBtn = document.querySelector('.login-modal .close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                Modal.hide('.login-modal');
            });
        }
        
        // 发送验证码
        const sendCodeBtn = document.querySelector('.send-code-btn');
        if (sendCodeBtn) {
            sendCodeBtn.addEventListener('click', () => {
                this.sendVerificationCode();
            });
        }
        
        // 登录提交
        const loginForm = document.querySelector('.login-form');
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.processLogin();
            });
        }
        
        const loginSubmitBtn = document.querySelector('.login-submit-btn');
        if (loginSubmitBtn) {
            loginSubmitBtn.addEventListener('click', () => {
                this.processLogin();
            });
        }
    },
    
    // 显示登录弹窗
    showLoginModal() {
        Modal.show('.login-modal');
    },
    
    // 发送验证码
    async sendVerificationCode() {
        const phoneInput = document.querySelector('input[name="phone"]');
        const phone = phoneInput?.value;
        
        if (!phone) {
            Toast.error('请输入手机号');
            return;
        }
        
        if (!Utils.validatePhone(phone)) {
            Toast.error('手机号格式不正确');
            return;
        }
        
        const sendCodeBtn = document.querySelector('.send-code-btn');
        if (!sendCodeBtn) return;
        
        // 开始倒计时
        let countdown = 60;
        sendCodeBtn.disabled = true;
        sendCodeBtn.textContent = `${countdown}s`;
        
        const timer = setInterval(() => {
            countdown--;
            sendCodeBtn.textContent = `${countdown}s`;
            
            if (countdown <= 0) {
                clearInterval(timer);
                sendCodeBtn.disabled = false;
                sendCodeBtn.textContent = '发送验证码';
            }
        }, 1000);
        
        // 模拟发送验证码
        try {
            await new Promise(resolve => setTimeout(resolve, 1000));
            Toast.success('验证码已发送');
        } catch (error) {
            Toast.error('验证码发送失败');
            clearInterval(timer);
            sendCodeBtn.disabled = false;
            sendCodeBtn.textContent = '发送验证码';
        }
    },
    
    // 处理登录
    async processLogin() {
        const form = document.querySelector('.login-form');
        const formData = new FormData(form);
        
        const loginData = {
            phone: formData.get('phone'),
            code: formData.get('code')
        };
        
        // 验证表单
        const validation = Validator.validateForm(loginData, {
            phone: [
                { type: 'required', message: '请输入手机号' },
                { type: 'phone', message: '手机号格式不正确' }
            ],
            code: [
                { type: 'required', message: '请输入验证码' },
                { type: 'minLength', length: 4, message: '验证码至少4位' }
            ]
        });
        
        if (!validation.isValid) {
            const firstError = Object.values(validation.errors)[0];
            Toast.error(firstError);
            return;
        }
        
        Loading.show('正在登录...');
        
        try {
            // 模拟登录请求
            await new Promise(resolve => setTimeout(resolve, 1500));
            
            // 模拟登录成功
            const userData = {
                name: '张三',
                phone: loginData.phone,
                avatar: 'https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture%20friendly%20person%20blue%20theme&image_size=square',
                level: 'VIP',
                points: 1280
            };
            
            const token = 'mock_token_' + Date.now();
            
            Auth.setToken(token);
            Auth.setUserInfo(userData);
            
            this.data.isLoggedIn = true;
            this.data.user = userData;
            
            Loading.hide();
            Modal.hide('.login-modal');
            
            Toast.success('登录成功');
            
            // 刷新页面数据
            this.loadRecentOrders();
            this.updateOrderStats();
            this.renderUserInfo();
            this.renderOrderStats();
            this.renderRecentOrders();
            this.renderMenuItems();
            
            // 清空表单
            form.reset();
            
        } catch (error) {
            Loading.hide();
            Toast.error('登录失败，请重试');
        }
    },
    
    // 退出登录
    logout() {
        if (confirm('确定要退出登录吗？')) {
            Auth.logout();
        }
    },
    
    // 按状态查看订单
    viewOrdersByStatus(status) {
        if (!this.data.isLoggedIn) {
            this.showLoginModal();
            return;
        }
        
        // 跳转到订单页面并传递状态参数
        Storage.set('order_filter_status', status);
        window.location.href = 'order.html';
    },
    
    // 查看订单详情
    viewOrderDetail(orderId) {
        if (!this.data.isLoggedIn) {
            this.showLoginModal();
            return;
        }
        
        // 跳转到订单详情
        Storage.set('selected_order_id', orderId);
        window.location.href = 'order.html';
    },
    
    // 处理菜单操作
    handleMenuAction(action) {
        const actionHandlers = {
            passengers: () => {
                if (!this.data.isLoggedIn) {
                    this.showLoginModal();
                    return;
                }
                Toast.info('常用乘车人功能开发中...');
            },
            addresses: () => {
                if (!this.data.isLoggedIn) {
                    this.showLoginModal();
                    return;
                }
                Toast.info('收货地址功能开发中...');
            },
            coupons: () => {
                if (!this.data.isLoggedIn) {
                    this.showLoginModal();
                    return;
                }
                Toast.info('优惠券功能开发中...');
            },
            points: () => {
                if (!this.data.isLoggedIn) {
                    this.showLoginModal();
                    return;
                }
                Toast.info('积分商城功能开发中...');
            },
            security: () => {
                if (!this.data.isLoggedIn) {
                    this.showLoginModal();
                    return;
                }
                Toast.info('账户安全功能开发中...');
            },
            payment: () => {
                if (!this.data.isLoggedIn) {
                    this.showLoginModal();
                    return;
                }
                Toast.info('支付管理功能开发中...');
            },
            notifications: () => {
                if (!this.data.isLoggedIn) {
                    this.showLoginModal();
                    return;
                }
                Toast.info('消息通知功能开发中...');
            },
            settings: () => {
                Toast.info('设置功能开发中...');
            },
            help: () => {
                Toast.info('帮助中心功能开发中...');
            },
            feedback: () => {
                Toast.info('意见反馈功能开发中...');
            },
            about: () => {
                Toast.info('关于我们：铁路订票系统 v1.0.0');
            }
        };
        
        const handler = actionHandlers[action];
        if (handler) {
            handler();
        } else {
            Toast.info('功能开发中...');
        }
    }
};

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    ProfileApp.init();
});

// 导出到全局
window.ProfileApp = ProfileApp;