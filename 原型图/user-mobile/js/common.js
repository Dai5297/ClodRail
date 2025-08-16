// 通用JavaScript功能模块

// 工具函数
const Utils = {
    // 格式化日期
    formatDate(date, format = 'YYYY-MM-DD') {
        const d = new Date(date);
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        const hour = String(d.getHours()).padStart(2, '0');
        const minute = String(d.getMinutes()).padStart(2, '0');
        
        return format
            .replace('YYYY', year)
            .replace('MM', month)
            .replace('DD', day)
            .replace('HH', hour)
            .replace('mm', minute);
    },
    
    // 获取星期几
    getWeekDay(date) {
        const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
        return weekDays[new Date(date).getDay()];
    },
    
    // 计算两个日期之间的天数差
    getDaysDiff(date1, date2) {
        const d1 = new Date(date1);
        const d2 = new Date(date2);
        return Math.ceil((d2 - d1) / (1000 * 60 * 60 * 24));
    },
    
    // 防抖函数
    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },
    
    // 节流函数
    throttle(func, limit) {
        let inThrottle;
        return function() {
            const args = arguments;
            const context = this;
            if (!inThrottle) {
                func.apply(context, args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        };
    },
    
    // 生成唯一ID
    generateId() {
        return Date.now().toString(36) + Math.random().toString(36).substr(2);
    },
    
    // 深拷贝
    deepClone(obj) {
        if (obj === null || typeof obj !== 'object') return obj;
        if (obj instanceof Date) return new Date(obj.getTime());
        if (obj instanceof Array) return obj.map(item => this.deepClone(item));
        if (typeof obj === 'object') {
            const clonedObj = {};
            for (const key in obj) {
                if (obj.hasOwnProperty(key)) {
                    clonedObj[key] = this.deepClone(obj[key]);
                }
            }
            return clonedObj;
        }
    },
    
    // 格式化价格
    formatPrice(price) {
        return `¥${parseFloat(price).toFixed(2)}`;
    },
    
    // 验证手机号
    validatePhone(phone) {
        const phoneRegex = /^1[3-9]\d{9}$/;
        return phoneRegex.test(phone);
    },
    
    // 验证身份证号
    validateIdCard(idCard) {
        const idCardRegex = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
        return idCardRegex.test(idCard);
    }
};

// 本地存储管理
const Storage = {
    // 设置数据
    set(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
        } catch (e) {
            console.error('Storage set error:', e);
        }
    },
    
    // 获取数据
    get(key, defaultValue = null) {
        try {
            const value = localStorage.getItem(key);
            return value ? JSON.parse(value) : defaultValue;
        } catch (e) {
            console.error('Storage get error:', e);
            return defaultValue;
        }
    },
    
    // 删除数据
    remove(key) {
        try {
            localStorage.removeItem(key);
        } catch (e) {
            console.error('Storage remove error:', e);
        }
    },
    
    // 清空所有数据
    clear() {
        try {
            localStorage.clear();
        } catch (e) {
            console.error('Storage clear error:', e);
        }
    }
};

// 消息提示
const Toast = {
    show(message, type = 'info', duration = 3000) {
        // 移除已存在的toast
        const existingToast = document.querySelector('.toast');
        if (existingToast) {
            existingToast.remove();
        }
        
        // 创建toast元素
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.textContent = message;
        
        // 添加样式
        Object.assign(toast.style, {
            position: 'fixed',
            top: '20px',
            left: '50%',
            transform: 'translateX(-50%)',
            backgroundColor: type === 'error' ? '#f44336' : type === 'success' ? '#4CAF50' : '#1976D2',
            color: '#fff',
            padding: '12px 20px',
            borderRadius: '6px',
            fontSize: '14px',
            fontWeight: '500',
            zIndex: '9999',
            opacity: '0',
            transition: 'opacity 0.3s ease',
            maxWidth: '80%',
            textAlign: 'center',
            boxShadow: '0 4px 12px rgba(0,0,0,0.15)'
        });
        
        document.body.appendChild(toast);
        
        // 显示动画
        setTimeout(() => {
            toast.style.opacity = '1';
        }, 10);
        
        // 自动隐藏
        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            }, 300);
        }, duration);
    },
    
    success(message, duration) {
        this.show(message, 'success', duration);
    },
    
    error(message, duration) {
        this.show(message, 'error', duration);
    },
    
    info(message, duration) {
        this.show(message, 'info', duration);
    }
};

// 加载状态管理
const Loading = {
    show(message = '加载中...') {
        // 移除已存在的loading
        this.hide();
        
        const loading = document.createElement('div');
        loading.className = 'loading-overlay';
        loading.innerHTML = `
            <div class="loading-content">
                <div class="loading-spinner"></div>
                <div class="loading-text">${message}</div>
            </div>
        `;
        
        // 添加样式
        const style = document.createElement('style');
        style.textContent = `
            .loading-overlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.5);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 9998;
            }
            .loading-content {
                background-color: #fff;
                padding: 30px;
                border-radius: 12px;
                text-align: center;
                box-shadow: 0 4px 20px rgba(0,0,0,0.15);
            }
            .loading-spinner {
                width: 40px;
                height: 40px;
                border: 3px solid #f0f0f0;
                border-top: 3px solid #1976D2;
                border-radius: 50%;
                animation: spin 1s linear infinite;
                margin: 0 auto 16px;
            }
            .loading-text {
                font-size: 14px;
                color: #666;
            }
            @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
        `;
        
        document.head.appendChild(style);
        document.body.appendChild(loading);
    },
    
    hide() {
        const loading = document.querySelector('.loading-overlay');
        if (loading) {
            loading.remove();
        }
    }
};

// 模态框管理
const Modal = {
    show(selector) {
        const modal = document.querySelector(selector);
        if (modal) {
            modal.classList.add('show');
            document.body.style.overflow = 'hidden';
        }
    },
    
    hide(selector) {
        const modal = document.querySelector(selector);
        if (modal) {
            modal.classList.remove('show');
            document.body.style.overflow = '';
        }
    },
    
    hideAll() {
        const modals = document.querySelectorAll('.modal.show, .popup.show');
        modals.forEach(modal => {
            modal.classList.remove('show');
        });
        document.body.style.overflow = '';
    }
};

// 底部导航管理
const BottomNav = {
    init() {
        const navItems = document.querySelectorAll('.nav-item');
        const currentPage = this.getCurrentPage();
        
        navItems.forEach(item => {
            const page = item.getAttribute('data-page');
            if (page === currentPage) {
                item.classList.add('active');
            }
            
            // 更新图标
            const icon = item.querySelector('.nav-icon');
            if (icon) {
                if (item.classList.contains('active')) {
                    icon.classList.add('active');
                } else {
                    icon.classList.remove('active');
                }
            }
            
            item.addEventListener('click', (e) => {
                e.preventDefault();
                const targetPage = item.getAttribute('data-page');
                this.navigateTo(targetPage);
            });
        });
    },
    
    getCurrentPage() {
        const path = window.location.pathname;
        if (path.includes('index.html') || path.endsWith('/')) {
            return 'home';
        } else if (path.includes('search.html')) {
            return 'search';
        } else if (path.includes('order.html')) {
            return 'order';
        } else if (path.includes('profile.html')) {
            return 'profile';
        }
        return 'home';
    },
    
    navigateTo(page) {
        const pageMap = {
            'home': 'index.html',
            'search': 'search.html',
            'order': 'order.html',
            'profile': 'profile.html'
        };
        
        if (pageMap[page]) {
            window.location.href = pageMap[page];
        }
    }
};

// 网络请求封装
const Http = {
    async request(url, options = {}) {
        const defaultOptions = {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        };
        
        const config = { ...defaultOptions, ...options };
        
        try {
            const response = await fetch(url, config);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const data = await response.json();
            return { success: true, data };
        } catch (error) {
            console.error('Request error:', error);
            return { success: false, error: error.message };
        }
    },
    
    async get(url, params = {}) {
        const urlParams = new URLSearchParams(params);
        const fullUrl = urlParams.toString() ? `${url}?${urlParams}` : url;
        return this.request(fullUrl);
    },
    
    async post(url, data = {}) {
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    
    async put(url, data = {}) {
        return this.request(url, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },
    
    async delete(url) {
        return this.request(url, {
            method: 'DELETE'
        });
    }
};

// 用户认证管理
const Auth = {
    // 检查登录状态
    isLoggedIn() {
        return !!Storage.get('user_token');
    },
    
    // 获取用户信息
    getUserInfo() {
        return Storage.get('user_info', {});
    },
    
    // 设置用户信息
    setUserInfo(userInfo) {
        Storage.set('user_info', userInfo);
    },
    
    // 设置token
    setToken(token) {
        Storage.set('user_token', token);
    },
    
    // 获取token
    getToken() {
        return Storage.get('user_token');
    },
    
    // 登出
    logout() {
        Storage.remove('user_token');
        Storage.remove('user_info');
        Toast.success('已退出登录');
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 1000);
    }
};

// 表单验证
const Validator = {
    // 验证规则
    rules: {
        required: (value) => {
            return value !== null && value !== undefined && value.toString().trim() !== '';
        },
        phone: (value) => {
            return Utils.validatePhone(value);
        },
        idCard: (value) => {
            return Utils.validateIdCard(value);
        },
        email: (value) => {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            return emailRegex.test(value);
        },
        minLength: (value, length) => {
            return value.toString().length >= length;
        },
        maxLength: (value, length) => {
            return value.toString().length <= length;
        }
    },
    
    // 验证单个字段
    validateField(value, rules) {
        for (const rule of rules) {
            const { type, message, ...params } = rule;
            const validator = this.rules[type];
            
            if (validator && !validator(value, ...Object.values(params))) {
                return { valid: false, message };
            }
        }
        return { valid: true };
    },
    
    // 验证表单
    validateForm(formData, schema) {
        const errors = {};
        let isValid = true;
        
        for (const [field, rules] of Object.entries(schema)) {
            const result = this.validateField(formData[field], rules);
            if (!result.valid) {
                errors[field] = result.message;
                isValid = false;
            }
        }
        
        return { isValid, errors };
    }
};

// 页面初始化
const App = {
    init() {
        // 初始化底部导航
        BottomNav.init();
        
        // 绑定全局事件
        this.bindGlobalEvents();
        
        // 设置页面标题
        this.setPageTitle();
    },
    
    bindGlobalEvents() {
        // 点击遮罩层关闭模态框
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal') || e.target.classList.contains('popup')) {
                Modal.hide('.' + e.target.className.split(' ')[0]);
            }
        });
        
        // ESC键关闭模态框
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                Modal.hideAll();
            }
        });
        
        // 阻止模态框内容区域的点击事件冒泡
        document.addEventListener('click', (e) => {
            if (e.target.closest('.modal-content, .popup-content')) {
                e.stopPropagation();
            }
        });
    },
    
    setPageTitle() {
        const titleMap = {
            'index.html': '首页 - 铁路订票系统',
            'search.html': '车次查询 - 铁路订票系统',
            'ticket.html': '购票 - 铁路订票系统',
            'order.html': '我的订单 - 铁路订票系统',
            'profile.html': '个人中心 - 铁路订票系统'
        };
        
        const path = window.location.pathname;
        const filename = path.split('/').pop();
        
        if (titleMap[filename]) {
            document.title = titleMap[filename];
        }
    }
};

// 导出全局对象
window.Utils = Utils;
window.Storage = Storage;
window.Toast = Toast;
window.Loading = Loading;
window.Modal = Modal;
window.BottomNav = BottomNav;
window.Http = Http;
window.Auth = Auth;
window.Validator = Validator;
window.App = App;

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    App.init();
});