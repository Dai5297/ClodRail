// 管理端主要JavaScript文件

// 管理端工具类
class AdminUtils {
    // 本地存储操作
    static storage = {
        set: (key, value) => {
            localStorage.setItem(key, JSON.stringify(value));
        },
        get: (key) => {
            const value = localStorage.getItem(key);
            return value ? JSON.parse(value) : null;
        },
        remove: (key) => {
            localStorage.removeItem(key);
        },
        clear: () => {
            localStorage.clear();
        }
    };

    // 显示消息提示
    static showMessage(message, type = 'info', duration = 3000) {
        const messageContainer = document.getElementById('message-container') || this.createMessageContainer();
        
        const messageEl = document.createElement('div');
        messageEl.className = `message message-${type}`;
        messageEl.innerHTML = `
            <i class="fas ${this.getMessageIcon(type)}"></i>
            <span>${message}</span>
            <button class="message-close" onclick="this.parentElement.remove()">&times;</button>
        `;
        
        messageContainer.appendChild(messageEl);
        
        // 自动移除消息
        setTimeout(() => {
            if (messageEl.parentElement) {
                messageEl.remove();
            }
        }, duration);
    }

    static createMessageContainer() {
        const container = document.createElement('div');
        container.id = 'message-container';
        container.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 10000;
            max-width: 400px;
        `;
        document.body.appendChild(container);
        return container;
    }

    static getMessageIcon(type) {
        const icons = {
            success: 'fa-check-circle',
            error: 'fa-exclamation-circle',
            warning: 'fa-exclamation-triangle',
            info: 'fa-info-circle'
        };
        return icons[type] || icons.info;
    }

    // 格式化日期
    static formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
        if (!date) return '';
        
        const d = new Date(date);
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        const hours = String(d.getHours()).padStart(2, '0');
        const minutes = String(d.getMinutes()).padStart(2, '0');
        const seconds = String(d.getSeconds()).padStart(2, '0');
        
        return format
            .replace('YYYY', year)
            .replace('MM', month)
            .replace('DD', day)
            .replace('HH', hours)
            .replace('mm', minutes)
            .replace('ss', seconds);
    }

    // 格式化金额
    static formatMoney(amount, currency = '¥') {
        if (amount === null || amount === undefined) return '';
        return currency + Number(amount).toLocaleString('zh-CN', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });
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
    static generateId(prefix = 'id') {
        return prefix + '_' + Math.random().toString(36).substr(2, 9) + Date.now().toString(36);
    }

    // 深拷贝
    static deepClone(obj) {
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
    }

    // 验证邮箱
    static validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }

    // 验证手机号
    static validatePhone(phone) {
        const re = /^1[3-9]\d{9}$/;
        return re.test(phone);
    }

    // 验证身份证号
    static validateIdCard(idCard) {
        const re = /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/;
        return re.test(idCard);
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

    // 显示加载状态
    static showLoading(target = document.body) {
        const loading = document.createElement('div');
        loading.className = 'loading-overlay';
        loading.innerHTML = `
            <div class="loading-spinner">
                <div class="loading"></div>
                <p>加载中...</p>
            </div>
        `;
        loading.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(255, 255, 255, 0.8);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 9999;
        `;
        target.appendChild(loading);
        return loading;
    }

    // 隐藏加载状态
    static hideLoading(target = document.body) {
        const loading = target.querySelector('.loading-overlay');
        if (loading) {
            loading.remove();
        }
    }

    // 确认对话框
    static confirm(message, callback) {
        if (window.confirm(message)) {
            callback && callback();
        }
    }

    // 导出数据为CSV
    static exportToCSV(data, filename = 'export.csv') {
        if (!data || !data.length) {
            this.showMessage('没有数据可导出', 'warning');
            return;
        }

        const headers = Object.keys(data[0]);
        const csvContent = [
            headers.join(','),
            ...data.map(row => headers.map(header => `"${row[header] || ''}"`).join(','))
        ].join('\n');

        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = filename;
        link.click();
        URL.revokeObjectURL(link.href);
    }
}

// 侧边栏管理
class SidebarManager {
    constructor() {
        this.sidebar = document.querySelector('.sidebar');
        this.mainContent = document.querySelector('.main-content');
        this.toggleBtn = document.querySelector('.sidebar-toggle');
        this.init();
    }

    init() {
        if (this.toggleBtn) {
            this.toggleBtn.addEventListener('click', () => this.toggle());
        }

        // 设置当前活动菜单项
        this.setActiveMenuItem();

        // 响应式处理
        this.handleResize();
        window.addEventListener('resize', () => this.handleResize());
    }

    toggle() {
        if (this.sidebar) {
            this.sidebar.classList.toggle('collapsed');
            AdminUtils.storage.set('sidebar-collapsed', this.sidebar.classList.contains('collapsed'));
        }
    }

    setActiveMenuItem() {
        const currentPath = window.location.pathname;
        const menuItems = document.querySelectorAll('.menu-item');
        
        menuItems.forEach(item => {
            item.classList.remove('active');
            if (item.getAttribute('href') && currentPath.includes(item.getAttribute('href'))) {
                item.classList.add('active');
            }
        });
    }

    handleResize() {
        if (window.innerWidth <= 768) {
            this.sidebar?.classList.add('mobile');
        } else {
            this.sidebar?.classList.remove('mobile');
            // 恢复桌面端的折叠状态
            const isCollapsed = AdminUtils.storage.get('sidebar-collapsed');
            if (isCollapsed) {
                this.sidebar?.classList.add('collapsed');
            }
        }
    }
}

// 数据表格管理
class DataTableManager {
    constructor(tableId, options = {}) {
        this.table = document.getElementById(tableId);
        this.options = {
            pageSize: 10,
            searchable: true,
            sortable: true,
            ...options
        };
        this.currentPage = 1;
        this.totalPages = 1;
        this.data = [];
        this.filteredData = [];
        this.init();
    }

    init() {
        if (!this.table) return;
        
        this.createControls();
        this.bindEvents();
    }

    createControls() {
        const container = this.table.parentElement;
        
        // 搜索栏
        if (this.options.searchable) {
            const searchBar = document.createElement('div');
            searchBar.className = 'table-search-bar';
            searchBar.innerHTML = `
                <div class="search-bar">
                    <div class="form-group search-input">
                        <input type="text" class="form-control" placeholder="搜索..." id="${this.table.id}-search">
                    </div>
                    <button class="btn btn-primary" onclick="${this.table.id}Manager.search()">
                        <i class="fas fa-search"></i> 搜索
                    </button>
                </div>
            `;
            container.insertBefore(searchBar, this.table);
        }

        // 分页控件
        const pagination = document.createElement('div');
        pagination.className = 'pagination';
        pagination.id = `${this.table.id}-pagination`;
        container.appendChild(pagination);
    }

    bindEvents() {
        // 搜索事件
        const searchInput = document.getElementById(`${this.table.id}-search`);
        if (searchInput) {
            searchInput.addEventListener('input', AdminUtils.debounce(() => this.search(), 300));
        }

        // 排序事件
        if (this.options.sortable) {
            const headers = this.table.querySelectorAll('th[data-sort]');
            headers.forEach(header => {
                header.style.cursor = 'pointer';
                header.addEventListener('click', () => this.sort(header.dataset.sort));
            });
        }
    }

    setData(data) {
        this.data = data;
        this.filteredData = [...data];
        this.updateTable();
        this.updatePagination();
    }

    search() {
        const searchInput = document.getElementById(`${this.table.id}-search`);
        const searchTerm = searchInput ? searchInput.value.toLowerCase() : '';
        
        if (!searchTerm) {
            this.filteredData = [...this.data];
        } else {
            this.filteredData = this.data.filter(item => 
                Object.values(item).some(value => 
                    String(value).toLowerCase().includes(searchTerm)
                )
            );
        }
        
        this.currentPage = 1;
        this.updateTable();
        this.updatePagination();
    }

    sort(field) {
        this.filteredData.sort((a, b) => {
            const aVal = a[field];
            const bVal = b[field];
            
            if (typeof aVal === 'number' && typeof bVal === 'number') {
                return aVal - bVal;
            }
            
            return String(aVal).localeCompare(String(bVal));
        });
        
        this.updateTable();
    }

    updateTable() {
        const tbody = this.table.querySelector('tbody');
        if (!tbody) return;
        
        const startIndex = (this.currentPage - 1) * this.options.pageSize;
        const endIndex = startIndex + this.options.pageSize;
        const pageData = this.filteredData.slice(startIndex, endIndex);
        
        tbody.innerHTML = '';
        
        if (pageData.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="100%" class="text-center">
                        <div class="empty-state">
                            <i class="fas fa-inbox"></i>
                            <h3>暂无数据</h3>
                            <p>没有找到符合条件的记录</p>
                        </div>
                    </td>
                </tr>
            `;
            return;
        }
        
        pageData.forEach(item => {
            const row = this.createTableRow(item);
            tbody.appendChild(row);
        });
    }

    createTableRow(item) {
        const row = document.createElement('tr');
        // 子类需要重写此方法
        return row;
    }

    updatePagination() {
        this.totalPages = Math.ceil(this.filteredData.length / this.options.pageSize);
        const pagination = document.getElementById(`${this.table.id}-pagination`);
        
        if (!pagination || this.totalPages <= 1) {
            pagination && (pagination.style.display = 'none');
            return;
        }
        
        pagination.style.display = 'flex';
        pagination.innerHTML = `
            <button ${this.currentPage === 1 ? 'disabled' : ''} onclick="${this.table.id}Manager.goToPage(${this.currentPage - 1})">
                <i class="fas fa-chevron-left"></i>
            </button>
            <span>第 ${this.currentPage} 页，共 ${this.totalPages} 页</span>
            <button ${this.currentPage === this.totalPages ? 'disabled' : ''} onclick="${this.table.id}Manager.goToPage(${this.currentPage + 1})">
                <i class="fas fa-chevron-right"></i>
            </button>
        `;
    }

    goToPage(page) {
        if (page < 1 || page > this.totalPages) return;
        this.currentPage = page;
        this.updateTable();
        this.updatePagination();
    }
}

// 模态框管理
class ModalManager {
    static show(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('show');
            document.body.style.overflow = 'hidden';
        }
    }

    static hide(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('show');
            document.body.style.overflow = '';
        }
    }

    static create(options) {
        const modal = document.createElement('div');
        modal.className = 'modal';
        modal.id = options.id || AdminUtils.generateId('modal');
        modal.innerHTML = `
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title">${options.title || ''}</h3>
                    <button class="modal-close" onclick="ModalManager.hide('${modal.id}')">&times;</button>
                </div>
                <div class="modal-body">
                    ${options.content || ''}
                </div>
                ${options.footer ? `<div class="modal-footer">${options.footer}</div>` : ''}
            </div>
        `;
        
        document.body.appendChild(modal);
        
        // 点击背景关闭
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                this.hide(modal.id);
            }
        });
        
        return modal;
    }
}

// 初始化管理端
document.addEventListener('DOMContentLoaded', function() {
    // 初始化侧边栏
    new SidebarManager();
    
    // 检查登录状态
    checkAdminAuth();
    
    // 初始化消息样式
    initMessageStyles();
});

// 检查管理员认证
function checkAdminAuth() {
    const adminToken = AdminUtils.storage.get('admin-token');
    const currentPath = window.location.pathname;
    
    // 如果不在登录页面且没有token，跳转到登录页
    if (!currentPath.includes('login.html') && !adminToken) {
        window.location.href = 'login.html';
        return;
    }
    
    // 如果在登录页面且有token，跳转到首页
    if (currentPath.includes('login.html') && adminToken) {
        window.location.href = 'index.html';
        return;
    }
}

// 管理员登出
function adminLogout() {
    AdminUtils.confirm('确定要退出登录吗？', () => {
        AdminUtils.storage.remove('admin-token');
        AdminUtils.storage.remove('admin-user');
        window.location.href = 'login.html';
    });
}

// 初始化消息样式
function initMessageStyles() {
    if (document.getElementById('message-styles')) return;
    
    const styles = document.createElement('style');
    styles.id = 'message-styles';
    styles.textContent = `
        .message {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 12px 16px;
            margin-bottom: 10px;
            border-radius: 6px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            animation: slideInRight 0.3s ease;
        }
        
        .message-success {
            background: #d4edda;
            color: #155724;
            border-left: 4px solid #28a745;
        }
        
        .message-error {
            background: #f8d7da;
            color: #721c24;
            border-left: 4px solid #dc3545;
        }
        
        .message-warning {
            background: #fff3cd;
            color: #856404;
            border-left: 4px solid #ffc107;
        }
        
        .message-info {
            background: #d1ecf1;
            color: #0c5460;
            border-left: 4px solid #17a2b8;
        }
        
        .message-close {
            background: none;
            border: none;
            font-size: 18px;
            cursor: pointer;
            opacity: 0.7;
            margin-left: auto;
        }
        
        .message-close:hover {
            opacity: 1;
        }
        
        @keyframes slideInRight {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
    `;
    
    document.head.appendChild(styles);
}

// 模拟数据生成器
class MockDataGenerator {
    static generateUsers(count = 50) {
        const users = [];
        const names = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十'];
        const statuses = ['active', 'inactive', 'pending'];
        
        for (let i = 1; i <= count; i++) {
            users.push({
                id: i,
                username: `user${i.toString().padStart(3, '0')}`,
                name: names[Math.floor(Math.random() * names.length)],
                email: `user${i}@example.com`,
                phone: `138${Math.floor(Math.random() * 100000000).toString().padStart(8, '0')}`,
                status: statuses[Math.floor(Math.random() * statuses.length)],
                registerTime: new Date(Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000),
                lastLogin: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000)
            });
        }
        
        return users;
    }
    
    static generateTrains(count = 30) {
        const trains = [];
        const cities = ['北京', '上海', '广州', '深圳', '杭州', '南京', '武汉', '成都', '西安', '重庆'];
        const trainTypes = ['G', 'D', 'C', 'K', 'T'];
        
        for (let i = 1; i <= count; i++) {
            const type = trainTypes[Math.floor(Math.random() * trainTypes.length)];
            const number = type + (Math.floor(Math.random() * 9000) + 1000);
            const fromCity = cities[Math.floor(Math.random() * cities.length)];
            let toCity = cities[Math.floor(Math.random() * cities.length)];
            while (toCity === fromCity) {
                toCity = cities[Math.floor(Math.random() * cities.length)];
            }
            
            trains.push({
                id: i,
                number: number,
                name: `${fromCity}-${toCity}`,
                fromCity: fromCity,
                toCity: toCity,
                departureTime: '08:00',
                arrivalTime: '12:00',
                duration: '4小时',
                price: Math.floor(Math.random() * 500) + 100,
                seats: Math.floor(Math.random() * 1000) + 500,
                availableSeats: Math.floor(Math.random() * 200) + 50,
                status: Math.random() > 0.1 ? 'active' : 'inactive'
            });
        }
        
        return trains;
    }
    
    static generateOrders(count = 100) {
        const orders = [];
        const statuses = ['pending', 'paid', 'cancelled', 'refunded'];
        const trains = this.generateTrains(10);
        
        for (let i = 1; i <= count; i++) {
            const train = trains[Math.floor(Math.random() * trains.length)];
            const seatCount = Math.floor(Math.random() * 4) + 1;
            
            orders.push({
                id: i,
                orderNo: `ORD${Date.now()}${i.toString().padStart(3, '0')}`,
                userId: Math.floor(Math.random() * 50) + 1,
                userName: `用户${i}`,
                trainNumber: train.number,
                fromCity: train.fromCity,
                toCity: train.toCity,
                departureDate: new Date(Date.now() + Math.random() * 30 * 24 * 60 * 60 * 1000),
                seatCount: seatCount,
                totalAmount: train.price * seatCount,
                status: statuses[Math.floor(Math.random() * statuses.length)],
                createTime: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000),
                payTime: Math.random() > 0.3 ? new Date() : null
            });
        }
        
        return orders;
    }
}