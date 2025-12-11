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

    // 权限判定
    static getCurrentRoles() {
        const user = this.storage.get('admin-user');
        return (user && user.roles) ? user.roles : [];
    }

    static hasAnyRole(required) {
        const roles = this.getCurrentRoles();
        if (!required || required.length === 0) return true;
        return required.some(r => roles.includes(r));
    }

    static applyPermissions(root = document) {
        const nodes = root.querySelectorAll('[data-roles]');
        nodes.forEach(el => {
            const list = (el.getAttribute('data-roles') || '').split(',').map(s => s.trim()).filter(Boolean);
            if (!AdminUtils.hasAnyRole(list)) {
                el.style.display = 'none';
            }
        });
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
            searchBar.className = 'table-ticket-bar';
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

    // 应用权限控制
    AdminUtils.applyPermissions();
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
        const stations = ['北京南', '上海虹桥', '广州南', '深圳北', '杭州东', '南京南', '武汉', '成都东', '西安北', '重庆北'];
        const trainTypes = ['G', 'D', 'C', 'Z', 'T', 'K'];
        for (let i = 1; i <= count; i++) {
            const trainType = trainTypes[Math.floor(Math.random() * trainTypes.length)];
            const trainNumber = trainType + (Math.floor(Math.random() * 9000) + 1000);
            const startStation = stations[Math.floor(Math.random() * stations.length)];
            let endStation = stations[Math.floor(Math.random() * stations.length)];
            while (endStation === startStation) endStation = stations[Math.floor(Math.random() * stations.length)];
            const depHour = Math.floor(Math.random() * 24);
            const depMin = Math.floor(Math.random() * 60);
            const arrHour = (depHour + Math.floor(Math.random() * 6) + 1) % 24;
            const arrMin = Math.floor(Math.random() * 60);
            const departureTime = `${String(depHour).padStart(2,'0')}:${String(depMin).padStart(2,'0')}`;
            const arrivalTime = `${String(arrHour).padStart(2,'0')}:${String(arrMin).padStart(2,'0')}`;
            const durationHours = (arrHour - depHour + 24) % 24 || 1;
            const duration = `${durationHours}小时${Math.floor(Math.random()*60)}分钟`;
            const prices = {
                business: Math.floor(Math.random()*400)+600,
                firstClass: Math.floor(Math.random()*200)+300,
                secondClass: Math.floor(Math.random()*100)+120,
                standing: Math.floor(Math.random()*60)+60
            };
            const minPrice = Math.min(...Object.values(prices));
            const seatConfig = {
                business: Math.floor(Math.random()*60)+20,
                firstClass: Math.floor(Math.random()*200)+80,
                secondClass: Math.floor(Math.random()*400)+200,
                standing: Math.floor(Math.random()*200)+100
            };
            const status = ['active','suspended','maintenance'][Math.floor(Math.random()*3)];
            trains.push({ id: i, trainNumber, trainType, startStation, endStation, departureTime, arrivalTime, duration, prices, minPrice, seatConfig, status });
        }
        return trains;
    }
    
    static generateOrders(count = 100) {
        const orders = [];
        const statuses = ['pending', 'paid', 'cancelled', 'refunded', 'completed', 'refund_pending'];
        const trains = this.generateTrains(10);
        for (let i = 1; i <= count; i++) {
            const train = trains[Math.floor(Math.random() * trains.length)];
            const passengerCount = Math.floor(Math.random() * 4) + 1;
            const ticketPrice = train.minPrice + Math.floor(Math.random()*50);
            const serviceFee = Math.floor(ticketPrice * 0.05);
            const totalAmount = ticketPrice * passengerCount + serviceFee;
            const status = statuses[Math.floor(Math.random() * statuses.length)];
            orders.push({
                id: i,
                orderNumber: `ORD${Date.now()}${String(i).padStart(3,'0')}`,
                username: `用户${i}`,
                trainNumber: train.trainNumber,
                startStation: train.startStation,
                endStation: train.endStation,
                seatType: ['商务座','一等座','二等座','无座'][Math.floor(Math.random()*4)],
                passengerCount,
                ticketPrice,
                serviceFee,
                totalAmount,
                status,
                createTime: new Date(Date.now() - Math.random()*30*24*60*60*1000),
                payTime: Math.random()>0.5 ? new Date() : null,
                departureTime: new Date(Date.now() + Math.random()*30*24*60*60*1000),
                contactPhone: `138${Math.floor(Math.random() * 100000000).toString().padStart(8, '0')}`,
                contactEmail: `user${i}@example.com`,
                passengers: Array.from({length: passengerCount}).map((_,idx)=>({
                    name: `乘客${idx+1}`,
                    idNumber: `4201************${String(idx).padStart(2,'0')}`,
                    type: ['成人','学生','儿童'][Math.floor(Math.random()*3)]
                })),
                seatNumbers: Array.from({length: passengerCount}).map(()=>`${['01A','02B','03C','04D'][Math.floor(Math.random()*4)]}`).join(', ')
            });
        }
        return orders;
    }
}
