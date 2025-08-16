// 工具函数库

class Utils {
    // 本地存储相关
    static setLocalStorage(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
        } catch (error) {
            console.error('设置本地存储失败:', error);
        }
    }

    static getLocalStorage(key) {
        try {
            const value = localStorage.getItem(key);
            return value ? JSON.parse(value) : null;
        } catch (error) {
            console.error('获取本地存储失败:', error);
            return null;
        }
    }

    static removeLocalStorage(key) {
        try {
            localStorage.removeItem(key);
        } catch (error) {
            console.error('删除本地存储失败:', error);
        }
    }

    // 存储对象（兼容性）
    static storage = {
        set: Utils.setLocalStorage,
        get: Utils.getLocalStorage,
        remove: Utils.removeLocalStorage
    }

    // 消息提示
    static showMessage(message, type = 'info', duration = 3000) {
        // 移除已存在的消息
        const existingMessage = document.querySelector('.toast-message');
        if (existingMessage) {
            existingMessage.remove();
        }

        // 创建消息元素
        const messageEl = document.createElement('div');
        messageEl.className = `toast-message toast-${type}`;
        messageEl.textContent = message;

        // 添加样式
        Object.assign(messageEl.style, {
            position: 'fixed',
            top: '20px',
            right: '20px',
            padding: '12px 20px',
            borderRadius: '6px',
            color: '#fff',
            fontSize: '14px',
            fontWeight: '500',
            zIndex: '10000',
            opacity: '0',
            transform: 'translateX(100%)',
            transition: 'all 0.3s ease',
            maxWidth: '300px',
            wordWrap: 'break-word'
        });

        // 设置不同类型的背景色
        const colors = {
            success: '#52c41a',
            error: '#ff4d4f',
            warning: '#faad14',
            info: '#1890ff'
        };
        messageEl.style.backgroundColor = colors[type] || colors.info;

        // 添加到页面
        document.body.appendChild(messageEl);

        // 显示动画
        setTimeout(() => {
            messageEl.style.opacity = '1';
            messageEl.style.transform = 'translateX(0)';
        }, 10);

        // 自动隐藏
        setTimeout(() => {
            messageEl.style.opacity = '0';
            messageEl.style.transform = 'translateX(100%)';
            setTimeout(() => {
                if (messageEl.parentNode) {
                    messageEl.parentNode.removeChild(messageEl);
                }
            }, 300);
        }, duration);
    }

    // 日期格式化
    static formatDate(date, format = 'YYYY-MM-DD') {
        const d = new Date(date);
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        const hour = String(d.getHours()).padStart(2, '0');
        const minute = String(d.getMinutes()).padStart(2, '0');
        const second = String(d.getSeconds()).padStart(2, '0');

        return format
            .replace('YYYY', year)
            .replace('MM', month)
            .replace('DD', day)
            .replace('HH', hour)
            .replace('mm', minute)
            .replace('ss', second);
    }

    // 防抖函数
    static debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // 节流函数
    static throttle(func, limit) {
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
    }

    // 生成随机ID
    static generateId(length = 8) {
        const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        let result = '';
        for (let i = 0; i < length; i++) {
            result += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        return result;
    }

    // 验证手机号
    static validatePhone(phone) {
        const phoneRegex = /^1[3-9]\d{9}$/;
        return phoneRegex.test(phone);
    }

    // 验证身份证号
    static validateIdCard(idCard) {
        const idCardRegex = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
        return idCardRegex.test(idCard);
    }

    // 验证邮箱
    static validateEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    // 格式化金额
    static formatMoney(amount, currency = '¥') {
        const num = parseFloat(amount);
        if (isNaN(num)) return currency + '0.00';
        return currency + num.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    }

    // 获取URL参数
    static getUrlParam(name) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(name);
    }

    // 设置URL参数
    static setUrlParam(name, value) {
        const url = new URL(window.location);
        url.searchParams.set(name, value);
        window.history.pushState({}, '', url);
    }

    // 深拷贝
    static deepClone(obj) {
        if (obj === null || typeof obj !== 'object') return obj;
        if (obj instanceof Date) return new Date(obj.getTime());
        if (obj instanceof Array) return obj.map(item => Utils.deepClone(item));
        if (typeof obj === 'object') {
            const clonedObj = {};
            for (const key in obj) {
                if (obj.hasOwnProperty(key)) {
                    clonedObj[key] = Utils.deepClone(obj[key]);
                }
            }
            return clonedObj;
        }
    }

    // 加载状态管理
    static showLoading(element, text = '加载中...') {
        if (!element) return;
        
        const loadingEl = document.createElement('div');
        loadingEl.className = 'loading-overlay';
        loadingEl.innerHTML = `
            <div class="loading-spinner">
                <div class="spinner"></div>
                <div class="loading-text">${text}</div>
            </div>
        `;
        
        // 添加样式
        Object.assign(loadingEl.style, {
            position: 'absolute',
            top: '0',
            left: '0',
            right: '0',
            bottom: '0',
            backgroundColor: 'rgba(255, 255, 255, 0.8)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: '1000'
        });
        
        element.style.position = 'relative';
        element.appendChild(loadingEl);
    }

    static hideLoading(element) {
        if (!element) return;
        const loadingEl = element.querySelector('.loading-overlay');
        if (loadingEl) {
            loadingEl.remove();
        }
    }

    // 模拟数据
    static getMockCities() {
        return [
            '北京', '上海', '广州', '深圳', '杭州', '南京', '苏州', '武汉',
            '成都', '重庆', '西安', '天津', '青岛', '大连', '厦门', '宁波',
            '无锡', '长沙', '郑州', '济南', '福州', '合肥', '昆明', '南昌',
            '贵阳', '南宁', '海口', '兰州', '银川', '西宁', '乌鲁木齐', '拉萨'
        ];
    }

    static getMockTrains() {
        return [
            {
                trainNumber: 'G1',
                departure: '北京南',
                arrival: '上海虹桥',
                departureTime: '06:00',
                arrivalTime: '10:58',
                duration: '4小时58分',
                price: {
                    secondClass: 553,
                    firstClass: 933,
                    business: 1748
                }
            },
            {
                trainNumber: 'G7',
                departure: '北京南',
                arrival: '上海虹桥',
                departureTime: '07:00',
                arrivalTime: '11:54',
                duration: '4小时54分',
                price: {
                    secondClass: 553,
                    firstClass: 933,
                    business: 1748
                }
            },
            {
                trainNumber: 'G103',
                departure: '北京南',
                arrival: '上海虹桥',
                departureTime: '08:05',
                arrivalTime: '13:28',
                duration: '5小时23分',
                price: {
                    secondClass: 553,
                    firstClass: 933,
                    business: 1748
                }
            }
        ];
    }
}

// 导出工具类
if (typeof module !== 'undefined' && module.exports) {
    module.exports = Utils;
}