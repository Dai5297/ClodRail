// 订单页面JavaScript功能

// 订单应用对象
const OrderApp = {
    data: {
        orders: [],
        filteredOrders: [],
        currentFilter: 'all',
        searchKeyword: '',
        isLoading: false,
        selectedOrder: null,
        statusTabs: [
            { key: 'all', label: '全部', count: 0 },
            { key: 'pending', label: '待支付', count: 0 },
            { key: 'paid', label: '已支付', count: 0 },
            { key: 'traveled', label: '已出行', count: 0 },
            { key: 'refunded', label: '已退款', count: 0 }
        ]
    },
    
    init() {
        this.checkLoginStatus();
        this.loadInitialFilter();
        this.loadOrders();
        this.updateStatusTabs();
        this.renderStatusTabs();
        this.renderOrderList();
        this.bindEvents();
    },
    
    // 检查登录状态
    checkLoginStatus() {
        if (!Auth.isLoggedIn()) {
            Toast.error('请先登录');
            setTimeout(() => {
                window.location.href = 'user.html';
            }, 1500);
            return;
        }
    },
    
    // 加载初始筛选状态
    loadInitialFilter() {
        const filterStatus = Storage.get('order_filter_status');
        if (filterStatus) {
            this.data.currentFilter = filterStatus;
            Storage.remove('order_filter_status');
        }
    },
    
    // 加载订单数据
    loadOrders() {
        this.data.isLoading = true;
        this.showLoading();
        
        // 模拟异步加载
        setTimeout(() => {
            this.data.orders = Storage.get('orders', []);
            
            // 如果没有订单，生成一些示例数据
            if (this.data.orders.length === 0) {
                this.generateSampleOrders();
            }
            
            this.applyFilter();
            this.updateStatusTabs();
            this.renderStatusTabs();
            this.renderOrderList();
            this.hideLoading();
            this.data.isLoading = false;
        }, 800);
    },
    
    // 生成示例订单数据
    generateSampleOrders() {
        const sampleOrders = [
            {
                id: 'ORDER' + Date.now() + '001',
                trainNumber: 'G1234',
                from: '北京南',
                to: '上海虹桥',
                departDate: '2024-01-15',
                departTime: '08:30',
                arriveTime: '13:45',
                seatType: 'second',
                passengers: [{ name: '张三', idCard: '110101199001011234' }],
                totalPrice: 553,
                status: 'pending',
                createTime: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
                paymentDeadline: new Date(Date.now() + 28 * 60 * 1000).toISOString()
            },
            {
                id: 'ORDER' + Date.now() + '002',
                trainNumber: 'D3142',
                from: '上海',
                to: '杭州东',
                departDate: '2024-01-10',
                departTime: '14:20',
                arriveTime: '15:45',
                seatType: 'first',
                passengers: [{ name: '张三', idCard: '110101199001011234' }],
                totalPrice: 128,
                status: 'paid',
                createTime: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
                payTime: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000 + 10 * 60 * 1000).toISOString()
            },
            {
                id: 'ORDER' + Date.now() + '003',
                trainNumber: 'G7564',
                from: '广州南',
                to: '深圳北',
                departDate: '2023-12-28',
                departTime: '16:45',
                arriveTime: '17:20',
                seatType: 'second',
                passengers: [{ name: '张三', idCard: '110101199001011234' }],
                totalPrice: 75,
                status: 'traveled',
                createTime: new Date(Date.now() - 18 * 24 * 60 * 60 * 1000).toISOString(),
                payTime: new Date(Date.now() - 18 * 24 * 60 * 60 * 1000 + 5 * 60 * 1000).toISOString()
            },
            {
                id: 'ORDER' + Date.now() + '004',
                trainNumber: 'K1234',
                from: '北京',
                to: '西安',
                departDate: '2023-12-20',
                departTime: '20:15',
                arriveTime: '07:30+1',
                seatType: 'second',
                passengers: [{ name: '张三', idCard: '110101199001011234' }],
                totalPrice: 263,
                status: 'refunded',
                createTime: new Date(Date.now() - 26 * 24 * 60 * 60 * 1000).toISOString(),
                payTime: new Date(Date.now() - 26 * 24 * 60 * 60 * 1000 + 8 * 60 * 1000).toISOString(),
                refundTime: new Date(Date.now() - 25 * 24 * 60 * 60 * 1000).toISOString(),
                refundAmount: 263
            }
        ];
        
        this.data.orders = sampleOrders;
        Storage.set('orders', this.data.orders);
    },
    
    // 应用筛选
    applyFilter() {
        let filtered = this.data.orders;
        
        // 按状态筛选
        if (this.data.currentFilter !== 'all') {
            filtered = filtered.filter(order => order.status === this.data.currentFilter);
        }
        
        // 按关键词搜索
        if (this.data.searchKeyword) {
            const keyword = this.data.searchKeyword.toLowerCase();
            filtered = filtered.filter(order => 
                order.trainNumber.toLowerCase().includes(keyword) ||
                order.from.toLowerCase().includes(keyword) ||
                order.to.toLowerCase().includes(keyword) ||
                order.id.toLowerCase().includes(keyword)
            );
        }
        
        // 按创建时间倒序排列
        filtered.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
        
        this.data.filteredOrders = filtered;
    },
    
    // 更新状态标签
    updateStatusTabs() {
        const statusCounts = {
            all: this.data.orders.length,
            pending: this.data.orders.filter(order => order.status === 'pending').length,
            paid: this.data.orders.filter(order => order.status === 'paid').length,
            traveled: this.data.orders.filter(order => order.status === 'traveled').length,
            refunded: this.data.orders.filter(order => order.status === 'refunded').length
        };
        
        this.data.statusTabs.forEach(tab => {
            tab.count = statusCounts[tab.key] || 0;
        });
    },
    
    // 渲染状态标签
    renderStatusTabs() {
        const statusTabs = document.querySelector('.status-tabs');
        if (!statusTabs) return;
        
        statusTabs.innerHTML = this.data.statusTabs.map(tab => `
            <div class="status-tab ${tab.key === this.data.currentFilter ? 'active' : ''}" data-status="${tab.key}">
                <span class="tab-label">${tab.label}</span>
                ${tab.count > 0 ? `<span class="tab-count">${tab.count}</span>` : ''}
            </div>
        `).join('');
    },
    
    // 渲染订单列表
    renderOrderList() {
        const orderList = document.querySelector('.order-list');
        if (!orderList) return;
        
        if (this.data.filteredOrders.length === 0) {
            this.showEmptyState();
            return;
        }
        
        orderList.innerHTML = this.data.filteredOrders.map(order => {
            const statusInfo = this.getOrderStatusInfo(order);
            const seatTypeName = this.getSeatTypeName(order.seatType);
            
            return `
                <div class="order-item" data-order="${order.id}">
                    <div class="order-header">
                        <div class="order-number">订单号：${order.id.slice(-8)}</div>
                        <div class="order-status ${statusInfo.class}">${statusInfo.text}</div>
                    </div>
                    <div class="order-content">
                        <div class="train-info">
                            <div class="train-number">${order.trainNumber}</div>
                            <div class="route">${order.from} → ${order.to}</div>
                        </div>
                        <div class="travel-info">
                            <div class="travel-date">${Utils.formatDate(new Date(order.departDate), 'MM-DD')} ${order.departTime}</div>
                            <div class="seat-info">${seatTypeName} ${order.passengers.length}张</div>
                        </div>
                        <div class="price-info">
                            <div class="total-price">¥${order.totalPrice}</div>
                            ${this.getOrderTimeInfo(order)}
                        </div>
                    </div>
                    <div class="order-actions">
                        ${this.getOrderActions(order)}
                    </div>
                </div>
            `;
        }).join('');
    },
    
    // 获取订单状态信息
    getOrderStatusInfo(order) {
        const statusMap = {
            pending: { text: '待支付', class: 'pending' },
            paid: { text: '已支付', class: 'paid' },
            traveled: { text: '已出行', class: 'traveled' },
            refunded: { text: '已退款', class: 'refunded' }
        };
        return statusMap[order.status] || { text: '未知', class: 'unknown' };
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
    
    // 获取订单时间信息
    getOrderTimeInfo(order) {
        const now = new Date();
        const createTime = new Date(order.createTime);
        
        switch (order.status) {
            case 'pending':
                if (order.paymentDeadline) {
                    const deadline = new Date(order.paymentDeadline);
                    const remaining = Math.max(0, deadline - now);
                    const minutes = Math.floor(remaining / (1000 * 60));
                    return `<div class="time-info warning">剩余${minutes}分钟</div>`;
                }
                break;
            case 'paid':
                if (order.payTime) {
                    return `<div class="time-info">支付时间：${Utils.formatDate(new Date(order.payTime), 'MM-DD HH:mm')}</div>`;
                }
                break;
            case 'refunded':
                if (order.refundTime) {
                    return `<div class="time-info">退款时间：${Utils.formatDate(new Date(order.refundTime), 'MM-DD HH:mm')}</div>`;
                }
                break;
            default:
                return `<div class="time-info">下单时间：${Utils.formatDate(createTime, 'MM-DD HH:mm')}</div>`;
        }
        
        return `<div class="time-info">下单时间：${Utils.formatDate(createTime, 'MM-DD HH:mm')}</div>`;
    },
    
    // 获取订单操作按钮
    getOrderActions(order) {
        const actions = [];
        
        switch (order.status) {
            case 'pending':
                actions.push(`<button class="action-btn primary" onclick="OrderApp.payOrder('${order.id}')">立即支付</button>`);
                actions.push(`<button class="action-btn secondary" onclick="OrderApp.cancelOrder('${order.id}')">取消订单</button>`);
                break;
            case 'paid':
                const departDate = new Date(order.departDate + ' ' + order.departTime);
                const now = new Date();
                if (departDate > now) {
                    actions.push(`<button class="action-btn secondary" onclick="OrderApp.refundOrder('${order.id}')">申请退票</button>`);
                }
                actions.push(`<button class="action-btn secondary" onclick="OrderApp.viewOrderDetail('${order.id}')">查看详情</button>`);
                break;
            case 'traveled':
                actions.push(`<button class="action-btn secondary" onclick="OrderApp.viewOrderDetail('${order.id}')">查看详情</button>`);
                actions.push(`<button class="action-btn secondary" onclick="OrderApp.rebookOrder('${order.id}')">再次购买</button>`);
                break;
            case 'refunded':
                actions.push(`<button class="action-btn secondary" onclick="OrderApp.viewOrderDetail('${order.id}')">查看详情</button>`);
                actions.push(`<button class="action-btn secondary" onclick="OrderApp.rebookOrder('${order.id}')">再次购买</button>`);
                break;
        }
        
        return actions.join('');
    },
    
    // 显示空状态
    showEmptyState() {
        const orderList = document.querySelector('.order-list');
        if (!orderList) return;
        
        const emptyMessages = {
            all: '暂无订单记录',
            pending: '暂无待支付订单',
            paid: '暂无已支付订单',
            traveled: '暂无已出行订单',
            refunded: '暂无已退款订单'
        };
        
        const message = emptyMessages[this.data.currentFilter] || '暂无相关订单';
        
        orderList.innerHTML = `
            <div class="empty-state">
                <img src="https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=empty%20order%20list%20illustration%20blue%20theme&image_size=square" alt="暂无订单" class="empty-image">
                <div class="empty-text">${message}</div>
                <a href="index.html" class="book-now-btn">立即购票</a>
            </div>
        `;
    },
    
    // 显示加载状态
    showLoading() {
        const orderList = document.querySelector('.order-list');
        if (!orderList) return;
        
        orderList.innerHTML = `
            <div class="loading-state">
                <div class="loading-spinner"></div>
                <div class="loading-text">加载中...</div>
            </div>
        `;
    },
    
    // 隐藏加载状态
    hideLoading() {
        // 加载状态会被订单列表替换，无需特殊处理
    },
    
    // 绑定事件
    bindEvents() {
        // 状态标签点击
        document.addEventListener('click', (e) => {
            const statusTab = e.target.closest('.status-tab');
            if (statusTab) {
                const status = statusTab.getAttribute('data-status');
                this.switchFilter(status);
            }
        });
        
        // 搜索输入
        const searchInput = document.querySelector('.ticket-input');
        if (searchInput) {
            searchInput.addEventListener('input', Utils.debounce((e) => {
                this.data.searchKeyword = e.target.value.trim();
                this.applyFilter();
                this.renderOrderList();
            }, 300));
        }
        
        // 搜索清除
        const clearBtn = document.querySelector('.ticket-clear');
        if (clearBtn) {
            clearBtn.addEventListener('click', () => {
                searchInput.value = '';
                this.data.searchKeyword = '';
                this.applyFilter();
                this.renderOrderList();
            });
        }
        
        // 下拉刷新
        this.bindPullRefresh();
        
        // 订单详情弹窗事件
        this.bindOrderDetailEvents();
    },
    
    // 绑定下拉刷新
    bindPullRefresh() {
        let startY = 0;
        let currentY = 0;
        let isPulling = false;
        const threshold = 60;
        
        const container = document.querySelector('.order-container');
        if (!container) return;
        
        container.addEventListener('touchstart', (e) => {
            if (container.scrollTop === 0) {
                startY = e.touches[0].clientY;
                isPulling = true;
            }
        });
        
        container.addEventListener('touchmove', (e) => {
            if (!isPulling) return;
            
            currentY = e.touches[0].clientY;
            const distance = currentY - startY;
            
            if (distance > 0 && distance < threshold * 2) {
                e.preventDefault();
                container.style.transform = `translateY(${distance * 0.5}px)`;
                
                if (distance > threshold) {
                    container.classList.add('pull-refresh-ready');
                } else {
                    container.classList.remove('pull-refresh-ready');
                }
            }
        });
        
        container.addEventListener('touchend', () => {
            if (!isPulling) return;
            
            const distance = currentY - startY;
            
            if (distance > threshold) {
                this.refreshOrders();
            }
            
            container.style.transform = '';
            container.classList.remove('pull-refresh-ready');
            isPulling = false;
        });
    },
    
    // 绑定订单详情弹窗事件
    bindOrderDetailEvents() {
        // 关闭按钮
        const closeBtn = document.querySelector('.order-detail-modal .close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                Modal.hide('.order-detail-modal');
            });
        }
    },
    
    // 切换筛选
    switchFilter(status) {
        if (this.data.currentFilter === status) return;
        
        this.data.currentFilter = status;
        this.applyFilter();
        this.renderStatusTabs();
        this.renderOrderList();
    },
    
    // 刷新订单
    refreshOrders() {
        if (this.data.isLoading) return;
        
        this.loadOrders();
        Toast.success('刷新成功');
    },
    
    // 支付订单
    async payOrder(orderId) {
        const order = this.data.orders.find(o => o.id === orderId);
        if (!order) {
            Toast.error('订单不存在');
            return;
        }
        
        // 检查支付时限
        if (order.paymentDeadline && new Date() > new Date(order.paymentDeadline)) {
            Toast.error('订单已超时，无法支付');
            return;
        }
        
        Loading.show('正在跳转支付...');
        
        try {
            // 模拟跳转支付
            await new Promise(resolve => setTimeout(resolve, 1500));
            
            // 模拟支付成功
            order.status = 'paid';
            order.payTime = new Date().toISOString();
            
            // 更新存储
            Storage.set('orders', this.data.orders);
            
            Loading.hide();
            Toast.success('支付成功');
            
            // 刷新页面
            this.applyFilter();
            this.updateStatusTabs();
            this.renderStatusTabs();
            this.renderOrderList();
            
        } catch (error) {
            Loading.hide();
            Toast.error('支付失败，请重试');
        }
    },
    
    // 取消订单
    cancelOrder(orderId) {
        if (!confirm('确定要取消这个订单吗？')) return;
        
        const orderIndex = this.data.orders.findIndex(o => o.id === orderId);
        if (orderIndex === -1) {
            Toast.error('订单不存在');
            return;
        }
        
        // 删除订单
        this.data.orders.splice(orderIndex, 1);
        Storage.set('orders', this.data.orders);
        
        Toast.success('订单已取消');
        
        // 刷新页面
        this.applyFilter();
        this.updateStatusTabs();
        this.renderStatusTabs();
        this.renderOrderList();
    },
    
    // 申请退票
    async refundOrder(orderId) {
        if (!confirm('确定要申请退票吗？退票可能产生手续费。')) return;
        
        const order = this.data.orders.find(o => o.id === orderId);
        if (!order) {
            Toast.error('订单不存在');
            return;
        }
        
        Loading.show('正在处理退票...');
        
        try {
            // 模拟退票处理
            await new Promise(resolve => setTimeout(resolve, 2000));
            
            // 模拟退票成功
            order.status = 'refunded';
            order.refundTime = new Date().toISOString();
            order.refundAmount = Math.floor(order.totalPrice * 0.95); // 扣除5%手续费
            
            // 更新存储
            Storage.set('orders', this.data.orders);
            
            Loading.hide();
            Toast.success('退票申请已提交，预计1-3个工作日到账');
            
            // 刷新页面
            this.applyFilter();
            this.updateStatusTabs();
            this.renderStatusTabs();
            this.renderOrderList();
            
        } catch (error) {
            Loading.hide();
            Toast.error('退票申请失败，请重试');
        }
    },
    
    // 查看订单详情
    viewOrderDetail(orderId) {
        const order = this.data.orders.find(o => o.id === orderId);
        if (!order) {
            Toast.error('订单不存在');
            return;
        }
        
        this.data.selectedOrder = order;
        this.renderOrderDetail();
        Modal.show('.order-detail-modal');
    },
    
    // 渲染订单详情
    renderOrderDetail() {
        const modal = document.querySelector('.order-detail-modal');
        if (!modal || !this.data.selectedOrder) return;
        
        const order = this.data.selectedOrder;
        const statusInfo = this.getOrderStatusInfo(order);
        const seatTypeName = this.getSeatTypeName(order.seatType);
        
        modal.innerHTML = `
            <div class="modal-content">
                <div class="modal-header">
                    <h3>订单详情</h3>
                    <button class="close-btn">&times;</button>
                </div>
                <div class="modal-body">
                    <div class="detail-section">
                        <div class="section-title">订单信息</div>
                        <div class="detail-item">
                            <span class="label">订单号：</span>
                            <span class="value">${order.id}</span>
                        </div>
                        <div class="detail-item">
                            <span class="label">订单状态：</span>
                            <span class="value ${statusInfo.class}">${statusInfo.text}</span>
                        </div>
                        <div class="detail-item">
                            <span class="label">下单时间：</span>
                            <span class="value">${Utils.formatDate(new Date(order.createTime), 'YYYY-MM-DD HH:mm:ss')}</span>
                        </div>
                        ${order.payTime ? `
                            <div class="detail-item">
                                <span class="label">支付时间：</span>
                                <span class="value">${Utils.formatDate(new Date(order.payTime), 'YYYY-MM-DD HH:mm:ss')}</span>
                            </div>
                        ` : ''}
                        ${order.refundTime ? `
                            <div class="detail-item">
                                <span class="label">退款时间：</span>
                                <span class="value">${Utils.formatDate(new Date(order.refundTime), 'YYYY-MM-DD HH:mm:ss')}</span>
                            </div>
                            <div class="detail-item">
                                <span class="label">退款金额：</span>
                                <span class="value">¥${order.refundAmount}</span>
                            </div>
                        ` : ''}
                    </div>
                    
                    <div class="detail-section">
                        <div class="section-title">车次信息</div>
                        <div class="detail-item">
                            <span class="label">车次：</span>
                            <span class="value">${order.trainNumber}</span>
                        </div>
                        <div class="detail-item">
                            <span class="label">出发站：</span>
                            <span class="value">${order.from}</span>
                        </div>
                        <div class="detail-item">
                            <span class="label">到达站：</span>
                            <span class="value">${order.to}</span>
                        </div>
                        <div class="detail-item">
                            <span class="label">出发时间：</span>
                            <span class="value">${order.departDate} ${order.departTime}</span>
                        </div>
                        <div class="detail-item">
                            <span class="label">到达时间：</span>
                            <span class="value">${order.departDate} ${order.arriveTime}</span>
                        </div>
                        <div class="detail-item">
                            <span class="label">座位类型：</span>
                            <span class="value">${seatTypeName}</span>
                        </div>
                    </div>
                    
                    <div class="detail-section">
                        <div class="section-title">乘车人信息</div>
                        ${order.passengers.map((passenger, index) => `
                            <div class="passenger-info">
                                <div class="detail-item">
                                    <span class="label">乘车人${index + 1}：</span>
                                    <span class="value">${passenger.name}</span>
                                </div>
                                <div class="detail-item">
                                    <span class="label">身份证号：</span>
                                    <span class="value">${passenger.idCard}</span>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                    
                    <div class="detail-section">
                        <div class="section-title">费用信息</div>
                        <div class="detail-item">
                            <span class="label">票价：</span>
                            <span class="value">¥${order.totalPrice}</span>
                        </div>
                        <div class="detail-item">
                            <span class="label">张数：</span>
                            <span class="value">${order.passengers.length}张</span>
                        </div>
                        <div class="detail-item total">
                            <span class="label">总计：</span>
                            <span class="value">¥${order.totalPrice}</span>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        // 重新绑定关闭事件
        const closeBtn = modal.querySelector('.close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                Modal.hide('.order-detail-modal');
            });
        }
    },
    
    // 再次购买
    rebookOrder(orderId) {
        const order = this.data.orders.find(o => o.id === orderId);
        if (!order) {
            Toast.error('订单不存在');
            return;
        }
        
        // 将订单信息存储到搜索参数中
        const searchParams = {
            from: order.from,
            to: order.to,
            departDate: Utils.formatDate(new Date(), 'YYYY-MM-DD'),
            tripType: 'single'
        };
        
        Storage.set('search_params', searchParams);
        Toast.success('正在为您查询相同路线...');
        
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 1000);
    }
};

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    OrderApp.init();
});

// 导出到全局
window.OrderApp = OrderApp;