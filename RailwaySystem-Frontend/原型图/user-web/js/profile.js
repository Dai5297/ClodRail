// 个人中心页面JavaScript

class Profile {
    constructor() {
        this.currentSection = 'orders';
        this.currentUser = null;
        this.userOrders = [];
        this.userPassengers = [];
        this.userPoints = 1200;
        this.userCoupons = [];
        this.init();
    }

    init() {
        // 检查用户是否已登录
        const currentUser = Utils.getLocalStorage('currentUser');
        const userToken = Utils.getLocalStorage('userToken');
        
        if (!currentUser && !userToken) {
            // 用户未登录，跳转到首页
            window.location.href = 'index.html';
            return;
        }
        
        this.loadUserData();
        this.bindEvents();
        this.showSection('orders');
        this.loadOrders();
        this.loadPassengers();
        this.loadPointsHistory();
        this.loadCoupons();
    }

    loadUserData() {
        // 从本地存储获取用户数据
        this.currentUser = Utils.getLocalStorage('currentUser') || {
            name: '张三',
            phone: '138****8888',
            email: 'zhangsan@example.com',
            idNumber: '1234****5678',
            avatar: 'https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20user%20avatar%20icon%20blue%20background%20simple%20design&image_size=square'
        };

        this.userPoints = Utils.getLocalStorage('userPoints') || 1200;
        this.userOrders = Utils.getLocalStorage('userOrders') || [];
        this.userPassengers = Utils.getLocalStorage('userPassengers') || this.getDefaultPassengers();
        this.userCoupons = Utils.getLocalStorage('userCoupons') || this.getDefaultCoupons();

        this.updateUserDisplay();
    }

    updateUserDisplay() {
        // 更新导航栏用户信息
        document.getElementById('userName').textContent = this.currentUser.name;
        document.getElementById('userPoints').textContent = this.userPoints;

        // 更新侧边栏用户信息
        document.getElementById('sidebarUserName').textContent = this.currentUser.name;
        document.getElementById('totalOrders').textContent = this.userOrders.length;
        document.getElementById('totalPoints').textContent = this.userPoints;
        document.getElementById('pointsTotal').textContent = this.userPoints;
        document.getElementById('userAvatar').src = this.currentUser.avatar;

        // 更新个人信息表单
        document.getElementById('profileName').value = this.currentUser.name;
        document.getElementById('profilePhone').value = this.currentUser.phone;
        document.getElementById('profileEmail').value = this.currentUser.email;
        document.getElementById('profileIdNumber').value = this.currentUser.idNumber;
    }

    getDefaultPassengers() {
        return [
            {
                id: 'p001',
                name: '张三',
                idType: '身份证',
                idNumber: '123456789012345678',
                phone: '13800138000',
                type: '成人'
            },
            {
                id: 'p002',
                name: '李四',
                idType: '身份证',
                idNumber: '123456789012345679',
                phone: '13800138001',
                type: '成人'
            }
        ];
    }

    getDefaultCoupons() {
        const now = new Date();
        const futureDate = new Date(now.getTime() + 30 * 24 * 60 * 60 * 1000); // 30天后
        const pastDate = new Date(now.getTime() - 10 * 24 * 60 * 60 * 1000); // 10天前

        return [
            {
                id: 'c001',
                title: '新用户专享券',
                desc: '满100减20',
                value: 20,
                minAmount: 100,
                status: 'available',
                expiry: futureDate.toISOString()
            },
            {
                id: 'c002',
                title: '高铁出行券',
                desc: '满300减50',
                value: 50,
                minAmount: 300,
                status: 'used',
                expiry: futureDate.toISOString(),
                usedDate: now.toISOString()
            },
            {
                id: 'c003',
                title: '周末特惠券',
                desc: '满200减30',
                value: 30,
                minAmount: 200,
                status: 'expired',
                expiry: pastDate.toISOString()
            }
        ];
    }

    bindEvents() {
        // 侧边菜单点击事件
        document.querySelectorAll('.sidebar-nav .nav-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const section = link.dataset.section;
                this.showSection(section);
            });
        });

        // 订单筛选事件
        document.querySelectorAll('.order-filters .filter-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                this.filterOrders(btn.dataset.status);
                this.updateFilterButtons(btn, '.order-filters');
            });
        });

        // 优惠券筛选事件
        document.querySelectorAll('.coupon-filters .filter-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                this.filterCoupons(btn.dataset.status);
                this.updateFilterButtons(btn, '.coupon-filters');
            });
        });

        // 个人信息编辑
        document.getElementById('editProfileBtn').addEventListener('click', () => {
            this.toggleProfileEdit(true);
        });

        document.getElementById('saveProfileBtn').addEventListener('click', () => {
            this.saveProfile();
        });

        document.getElementById('cancelEditBtn').addEventListener('click', () => {
            this.toggleProfileEdit(false);
        });

        // 添加乘客
        document.getElementById('addPassengerBtn').addEventListener('click', () => {
            this.showAddPassengerModal();
        });

        document.getElementById('confirmAddPassenger').addEventListener('click', () => {
            this.addPassenger();
        });

        document.getElementById('cancelAddPassenger').addEventListener('click', () => {
            this.hideAddPassengerModal();
        });

        document.getElementById('closePassengerModal').addEventListener('click', () => {
            this.hideAddPassengerModal();
        });

        // 修改密码
        document.getElementById('changePasswordBtn').addEventListener('click', () => {
            this.showChangePasswordModal();
        });

        document.getElementById('confirmChangePassword').addEventListener('click', () => {
            this.changePassword();
        });

        document.getElementById('cancelChangePassword').addEventListener('click', () => {
            this.hideChangePasswordModal();
        });

        document.getElementById('closePasswordModal').addEventListener('click', () => {
            this.hideChangePasswordModal();
        });

        // 退出登录
        document.getElementById('logoutBtn').addEventListener('click', () => {
            this.logout();
        });

        // 模态框外部点击关闭
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal')) {
                e.target.classList.remove('show');
            }
        });
    }

    showSection(sectionName) {
        // 隐藏所有内容区域
        document.querySelectorAll('.content-section').forEach(section => {
            section.classList.remove('active');
        });

        // 显示指定区域
        const targetSection = document.getElementById(sectionName + 'Section');
        if (targetSection) {
            targetSection.classList.add('active');
        }

        // 更新侧边菜单状态
        document.querySelectorAll('.sidebar-nav .nav-link').forEach(link => {
            link.classList.remove('active');
        });

        const activeLink = document.querySelector(`[data-section="${sectionName}"]`);
        if (activeLink) {
            activeLink.classList.add('active');
        }

        this.currentSection = sectionName;
    }

    loadOrders() {
        const container = document.getElementById('ordersContainer');
        if (!container) return;

        if (this.userOrders.length === 0) {
            container.innerHTML = this.getEmptyState('暂无订单记录', '您还没有任何订单，快去预订车票吧！');
            return;
        }

        this.renderOrders(this.userOrders);
    }

    renderOrders(orders) {
        const container = document.getElementById('ordersContainer');
        if (!container) return;

        container.innerHTML = orders.map(order => `
            <div class="order-item">
                <div class="order-header">
                    <div class="order-info">
                        <span class="order-number">订单号: ${order.id}</span>
                        <span class="order-date">${this.formatDate(order.paymentTime || order.createTime)}</span>
                    </div>
                    <span class="order-status ${order.status}">${this.getStatusText(order.status)}</span>
                </div>
                <div class="order-content">
                    <div class="train-info">
                        <div class="route-info">
                            <div class="station">
                                <div class="station-name">${order.train.departure.station}</div>
                                <div class="station-time">${order.train.departure.time}</div>
                            </div>
                            <div class="route-arrow">→</div>
                            <div class="station">
                                <div class="station-name">${order.train.arrival.station}</div>
                                <div class="station-time">${order.train.arrival.time}</div>
                            </div>
                        </div>
                        <div class="train-number">${order.train.number}</div>
                    </div>
                    <div class="order-details">
                        <div class="passenger-info">
                            乘客: ${order.passengers.map(p => p.name).join(', ')} | 
                            座位: ${order.seats.map(s => s.number).join(', ')}
                        </div>
                        <div class="order-amount">¥${order.finalAmount || order.pricing.total}</div>
                    </div>
                </div>
            </div>
        `).join('');
    }

    filterOrders(status) {
        let filteredOrders = this.userOrders;
        
        if (status !== 'all') {
            filteredOrders = this.userOrders.filter(order => order.status === status);
        }
        
        this.renderOrders(filteredOrders);
    }

    getStatusText(status) {
        const statusMap = {
            'paid': '已支付',
            'completed': '已完成',
            'cancelled': '已取消',
            'refunded': '已退款'
        };
        return statusMap[status] || status;
    }

    updateFilterButtons(activeBtn, containerSelector) {
        document.querySelectorAll(`${containerSelector} .filter-btn`).forEach(btn => {
            btn.classList.remove('active');
        });
        activeBtn.classList.add('active');
    }

    toggleProfileEdit(isEdit) {
        const inputs = document.querySelectorAll('#profileSection .form-input');
        const editBtn = document.getElementById('editProfileBtn');
        const actions = document.getElementById('profileActions');

        inputs.forEach(input => {
            if (input.id !== 'profileIdNumber') { // 身份证号不允许修改
                input.readOnly = !isEdit;
            }
        });

        editBtn.style.display = isEdit ? 'none' : 'inline-block';
        actions.style.display = isEdit ? 'flex' : 'none';
    }

    saveProfile() {
        const name = document.getElementById('profileName').value.trim();
        const phone = document.getElementById('profilePhone').value.trim();
        const email = document.getElementById('profileEmail').value.trim();

        if (!name || !phone || !email) {
            Utils.showMessage('请填写完整信息', 'warning');
            return;
        }

        // 验证手机号
        if (!/^1[3-9]\d{9}$/.test(phone.replace(/\*/g, ''))) {
            Utils.showMessage('请输入正确的手机号', 'warning');
            return;
        }

        // 验证邮箱
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            Utils.showMessage('请输入正确的邮箱地址', 'warning');
            return;
        }

        // 更新用户信息
        this.currentUser.name = name;
        this.currentUser.phone = phone;
        this.currentUser.email = email;

        Utils.setLocalStorage('currentUser', this.currentUser);
        this.updateUserDisplay();
        this.toggleProfileEdit(false);
        
        Utils.showMessage('个人信息更新成功', 'success');
    }

    loadPassengers() {
        const container = document.getElementById('passengersContainer');
        if (!container) return;

        if (this.userPassengers.length === 0) {
            container.innerHTML = this.getEmptyState('暂无常用乘客', '添加常用乘客，让订票更便捷');
            return;
        }

        container.innerHTML = this.userPassengers.map(passenger => `
            <div class="passenger-card">
                <div class="passenger-details">
                    <h4>${passenger.name}</h4>
                    <div class="passenger-meta">
                        ${passenger.idType}: ${this.maskIdNumber(passenger.idNumber)} | 
                        ${passenger.phone} | ${passenger.type}
                    </div>
                </div>
                <div class="passenger-actions">
                    <button class="btn btn-outline btn-small" onclick="profile.editPassenger('${passenger.id}')">编辑</button>
                    <button class="btn btn-outline btn-small" onclick="profile.deletePassenger('${passenger.id}')">删除</button>
                </div>
            </div>
        `).join('');
    }

    maskIdNumber(idNumber) {
        if (idNumber.length <= 8) return idNumber;
        return idNumber.substring(0, 4) + '****' + idNumber.substring(idNumber.length - 4);
    }

    showAddPassengerModal() {
        document.getElementById('addPassengerModal').classList.add('show');
        document.getElementById('addPassengerForm').reset();
    }

    hideAddPassengerModal() {
        document.getElementById('addPassengerModal').classList.remove('show');
    }

    addPassenger() {
        const form = document.getElementById('addPassengerForm');
        const formData = new FormData(form);
        
        const passenger = {
            id: 'p' + Date.now(),
            name: document.getElementById('passengerName').value.trim(),
            idType: document.getElementById('passengerIdType').value,
            idNumber: document.getElementById('passengerIdNumber').value.trim(),
            phone: document.getElementById('passengerPhone').value.trim(),
            type: document.getElementById('passengerType').value
        };

        // 验证必填字段
        if (!passenger.name || !passenger.idNumber) {
            Utils.showMessage('请填写必填信息', 'warning');
            return;
        }

        // 验证身份证号（简单验证）
        if (passenger.idType === '身份证' && !/^\d{17}[\dX]$/.test(passenger.idNumber)) {
            Utils.showMessage('请输入正确的身份证号', 'warning');
            return;
        }

        // 检查是否已存在
        const exists = this.userPassengers.some(p => p.idNumber === passenger.idNumber);
        if (exists) {
            Utils.showMessage('该乘客已存在', 'warning');
            return;
        }

        this.userPassengers.push(passenger);
        Utils.setLocalStorage('userPassengers', this.userPassengers);
        this.loadPassengers();
        this.hideAddPassengerModal();
        
        Utils.showMessage('乘客添加成功', 'success');
    }

    editPassenger(passengerId) {
        // 这里可以实现编辑乘客功能
        Utils.showMessage('编辑功能开发中', 'info');
    }

    deletePassenger(passengerId) {
        if (confirm('确定要删除这个乘客吗？')) {
            this.userPassengers = this.userPassengers.filter(p => p.id !== passengerId);
            Utils.setLocalStorage('userPassengers', this.userPassengers);
            this.loadPassengers();
            Utils.showMessage('乘客删除成功', 'success');
        }
    }

    loadPointsHistory() {
        const container = document.getElementById('pointsHistory');
        if (!container) return;

        // 模拟积分记录
        const pointsHistory = [
            {
                id: 1,
                type: 'earn',
                amount: 100,
                desc: '购票获得积分',
                detail: '订单号: T20231201001',
                date: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000)
            },
            {
                id: 2,
                type: 'spend',
                amount: -50,
                desc: '积分兑换优惠券',
                detail: '兑换20元优惠券',
                date: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000)
            },
            {
                id: 3,
                type: 'earn',
                amount: 200,
                desc: '注册奖励',
                detail: '新用户注册奖励',
                date: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000)
            }
        ];

        container.innerHTML = pointsHistory.map(record => `
            <div class="points-item">
                <div class="points-desc">
                    <h4>${record.desc}</h4>
                    <p>${record.detail} | ${this.formatDate(record.date)}</p>
                </div>
                <div class="points-change ${record.type === 'earn' ? 'positive' : 'negative'}">
                    ${record.amount > 0 ? '+' : ''}${record.amount}
                </div>
            </div>
        `).join('');
    }

    loadCoupons() {
        this.renderCoupons(this.userCoupons);
    }

    renderCoupons(coupons) {
        const container = document.getElementById('couponsContainer');
        if (!container) return;

        if (coupons.length === 0) {
            container.innerHTML = this.getEmptyState('暂无优惠券', '快去积分商城兑换优惠券吧！');
            return;
        }

        container.innerHTML = coupons.map(coupon => `
            <div class="coupon-card ${coupon.status}">
                <div class="coupon-header">
                    <h4 class="coupon-title">${coupon.title}</h4>
                    <div class="coupon-value">¥${coupon.value}</div>
                </div>
                <div class="coupon-desc">${coupon.desc}</div>
                <div class="coupon-validity">
                    ${this.getCouponStatusText(coupon)}
                </div>
            </div>
        `).join('');
    }

    filterCoupons(status) {
        let filteredCoupons = this.userCoupons;
        
        if (status !== 'all') {
            filteredCoupons = this.userCoupons.filter(coupon => coupon.status === status);
        }
        
        this.renderCoupons(filteredCoupons);
    }

    getCouponStatusText(coupon) {
        const now = new Date();
        const expiry = new Date(coupon.expiry);
        
        switch (coupon.status) {
            case 'available':
                return `有效期至: ${this.formatDate(expiry)}`;
            case 'used':
                return `已使用 | ${this.formatDate(coupon.usedDate)}`;
            case 'expired':
                return `已过期 | ${this.formatDate(expiry)}`;
            default:
                return '';
        }
    }

    showChangePasswordModal() {
        document.getElementById('changePasswordModal').classList.add('show');
        document.getElementById('changePasswordForm').reset();
    }

    hideChangePasswordModal() {
        document.getElementById('changePasswordModal').classList.remove('show');
    }

    changePassword() {
        const currentPassword = document.getElementById('currentPassword').value;
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (!currentPassword || !newPassword || !confirmPassword) {
            Utils.showMessage('请填写完整信息', 'warning');
            return;
        }

        if (newPassword !== confirmPassword) {
            Utils.showMessage('两次输入的密码不一致', 'warning');
            return;
        }

        if (newPassword.length < 6) {
            Utils.showMessage('密码长度不能少于6位', 'warning');
            return;
        }

        // 模拟密码修改
        Utils.showMessage('密码修改成功，即将退出登录', 'success');
        this.hideChangePasswordModal();
        
        // 密码修改成功后自动退出登录
        setTimeout(() => {
            this.logout();
        }, 2000);
    }

    logout() {
        if (confirm('确定要退出登录吗？')) {
            try {
                // 清除用户数据
                Utils.removeLocalStorage('currentUser');
                Utils.removeLocalStorage('userToken');
                Utils.removeLocalStorage('userInfo');
                Utils.removeLocalStorage('userPoints');
                Utils.removeLocalStorage('userOrders');
                Utils.removeLocalStorage('userPassengers');
                Utils.removeLocalStorage('userCoupons');
                
                Utils.showMessage('已退出登录', 'success');
                
                setTimeout(() => {
                    window.location.href = 'index.html';
                }, 1000);
            } catch (error) {
                console.error('退出登录时发生错误:', error);
                // 即使出错也要跳转到首页
                window.location.href = 'index.html';
            }
        }
    }

    formatDate(date) {
        if (typeof date === 'string') {
            date = new Date(date);
        }
        return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
    }

    getEmptyState(title, description) {
        return `
            <div class="empty-state">
                <img src="https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=empty%20state%20illustration%20simple%20gray%20icon&image_size=square" alt="空状态">
                <h3>${title}</h3>
                <p>${description}</p>
            </div>
        `;
    }
}

// 全局变量，供HTML中的onclick使用
let profile;

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    profile = new Profile();
});

// 导出类供其他模块使用
if (typeof module !== 'undefined' && module.exports) {
    module.exports = Profile;
}