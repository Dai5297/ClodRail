// 首页JavaScript文件

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    initIndexPage();
});

// 初始化首页
function initIndexPage() {
    initSearchForm();
    initPopularRoutes();
    initAuthButtons();
    initAnimations();
}

// 初始化搜索表单
function initSearchForm() {
    const searchBtn = document.getElementById('searchBtn');
    const departureSelect = document.getElementById('departure');
    const destinationSelect = document.getElementById('destination');
    const dateInput = document.getElementById('date');
    const passengersSelect = document.getElementById('passengers');
    
    // 搜索按钮点击事件
    if (searchBtn) {
        searchBtn.addEventListener('click', handleSearch);
    }
    
    // 出发地和目的地联动
    if (departureSelect && destinationSelect) {
        departureSelect.addEventListener('change', function() {
            const selectedDeparture = this.value;
            const destinationOptions = destinationSelect.options;
            
            // 重置目的地选择
            for (let i = 0; i < destinationOptions.length; i++) {
                const option = destinationOptions[i];
                if (option.value === selectedDeparture) {
                    option.style.display = 'none';
                } else {
                    option.style.display = 'block';
                }
            }
            
            // 如果当前目的地和出发地相同，自动切换
            if (destinationSelect.value === selectedDeparture) {
                for (let i = 0; i < destinationOptions.length; i++) {
                    const option = destinationOptions[i];
                    if (option.value !== selectedDeparture && option.style.display !== 'none') {
                        destinationSelect.value = option.value;
                        break;
                    }
                }
            }
        });
        
        destinationSelect.addEventListener('change', function() {
            const selectedDestination = this.value;
            const departureOptions = departureSelect.options;
            
            // 重置出发地选择
            for (let i = 0; i < departureOptions.length; i++) {
                const option = departureOptions[i];
                if (option.value === selectedDestination) {
                    option.style.display = 'none';
                } else {
                    option.style.display = 'block';
                }
            }
            
            // 如果当前出发地和目的地相同，自动切换
            if (departureSelect.value === selectedDestination) {
                for (let i = 0; i < departureOptions.length; i++) {
                    const option = departureOptions[i];
                    if (option.value !== selectedDestination && option.style.display !== 'none') {
                        departureSelect.value = option.value;
                        break;
                    }
                }
            }
        });
    }
    
    // 日期输入验证
    if (dateInput) {
        dateInput.addEventListener('change', function() {
            const selectedDate = new Date(this.value);
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            
            if (selectedDate < today) {
                Utils.showMessage('出发日期不能早于今天', 'warning');
                this.value = Utils.getTomorrowDate();
            }
        });
    }
}

// 处理搜索
function handleSearch() {
    const departure = document.getElementById('departure').value;
    const destination = document.getElementById('destination').value;
    const date = document.getElementById('date').value;
    const passengers = document.getElementById('passengers').value;
    
    // 验证表单
    if (!departure || !destination || !date) {
        Utils.showMessage('请填写完整的搜索信息', 'warning');
        return;
    }
    
    if (departure === destination) {
        Utils.showMessage('出发地和目的地不能相同', 'warning');
        return;
    }
    
    // 显示加载状态
    const searchBtn = document.getElementById('searchBtn');
    const originalText = searchBtn.innerHTML;
    Utils.showLoading(searchBtn);
    
    // 模拟搜索延迟
    setTimeout(() => {
        Utils.hideLoading(searchBtn, originalText);
        
        // 保存搜索条件到本地存储
        const searchParams = {
            departure,
            destination,
            date,
            passengers,
            timestamp: Date.now()
        };
        Utils.storage.set('searchParams', searchParams);
        
        // 跳转到车次查询页面
        const queryString = new URLSearchParams(searchParams).toString();
        window.location.href = `train-search.html?${queryString}`;
    }, 1000);
}

// 初始化热门线路
function initPopularRoutes() {
    const routeCards = document.querySelectorAll('.route-card');
    
    routeCards.forEach((card, index) => {
        card.addEventListener('click', function() {
            const routeName = this.querySelector('.route-name').textContent;
            const [departure, destination] = routeName.split(' → ');
            
            // 自动填充搜索表单
            document.getElementById('departure').value = departure;
            document.getElementById('destination').value = destination;
            
            // 滚动到搜索区域
            document.querySelector('.search-section').scrollIntoView({
                behavior: 'smooth'
            });
            
            Utils.showMessage(`已为您选择 ${routeName} 线路`, 'success');
        });
        
        // 添加悬停效果
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-4px)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(-2px)';
        });
    });
}

// 初始化认证按钮
function initAuthButtons() {
    const loginBtn = document.getElementById('loginBtn');
    const registerBtn = document.getElementById('registerBtn');
    
    if (loginBtn) {
        loginBtn.addEventListener('click', function() {
            showLoginModal();
        });
    }
    
    if (registerBtn) {
        registerBtn.addEventListener('click', function() {
            showRegisterModal();
        });
    }
    
    // 检查登录状态
    checkLoginStatus();
}

// 检查登录状态
function checkLoginStatus() {
    const userInfo = Utils.storage.get('userInfo');
    if (userInfo) {
        updateNavForLoggedInUser(userInfo);
    }
}

// 更新导航栏为已登录状态
function updateNavForLoggedInUser(userInfo) {
    const navAuth = document.querySelector('.nav-auth');
    if (navAuth) {
        navAuth.innerHTML = `
            <div class="user-info">
                <span class="user-name">欢迎，${userInfo.name}</span>
                <button class="btn btn-outline" id="logoutBtn">退出</button>
            </div>
        `;
        
        // 添加退出登录事件
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', function() {
                Utils.storage.remove('userInfo');
                location.reload();
            });
        }
    }
}

// 显示登录模态框
function showLoginModal() {
    const modal = createModal('登录', `
        <form id="loginForm" class="auth-form">
            <div class="form-group">
                <label for="loginPhone">手机号</label>
                <input type="tel" id="loginPhone" class="form-input" placeholder="请输入手机号" required>
            </div>
            <div class="form-group">
                <label for="loginPassword">密码</label>
                <input type="password" id="loginPassword" class="form-input" placeholder="请输入密码" required>
            </div>
            <button type="submit" class="btn btn-primary btn-block">登录</button>
            <div class="auth-links">
                <a href="#" id="showRegister">还没有账号？立即注册</a>
            </div>
        </form>
    `);
    
    // 登录表单提交
    const loginForm = document.getElementById('loginForm');
    loginForm.addEventListener('submit', function(e) {
        e.preventDefault();
        handleLogin();
    });
    
    // 切换到注册
    const showRegisterLink = document.getElementById('showRegister');
    showRegisterLink.addEventListener('click', function(e) {
        e.preventDefault();
        closeModal();
        showRegisterModal();
    });
}

// 显示注册模态框
function showRegisterModal() {
    const modal = createModal('注册', `
        <form id="registerForm" class="auth-form">
            <div class="form-group">
                <label for="registerName">姓名</label>
                <input type="text" id="registerName" class="form-input" placeholder="请输入真实姓名" required>
            </div>
            <div class="form-group">
                <label for="registerPhone">手机号</label>
                <input type="tel" id="registerPhone" class="form-input" placeholder="请输入手机号" required>
            </div>
            <div class="form-group">
                <label for="registerPassword">密码</label>
                <input type="password" id="registerPassword" class="form-input" placeholder="请输入密码" required>
            </div>
            <div class="form-group">
                <label for="confirmPassword">确认密码</label>
                <input type="password" id="confirmPassword" class="form-input" placeholder="请再次输入密码" required>
            </div>
            <button type="submit" class="btn btn-primary btn-block">注册</button>
            <div class="auth-links">
                <a href="#" id="showLogin">已有账号？立即登录</a>
            </div>
        </form>
    `);
    
    // 注册表单提交
    const registerForm = document.getElementById('registerForm');
    registerForm.addEventListener('submit', function(e) {
        e.preventDefault();
        handleRegister();
    });
    
    // 切换到登录
    const showLoginLink = document.getElementById('showLogin');
    showLoginLink.addEventListener('click', function(e) {
        e.preventDefault();
        closeModal();
        showLoginModal();
    });
}

// 创建模态框
function createModal(title, content) {
    const modal = document.createElement('div');
    modal.className = 'modal';
    modal.innerHTML = `
        <div class="modal-overlay"></div>
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">${title}</h3>
                <button class="modal-close">&times;</button>
            </div>
            <div class="modal-body">
                ${content}
            </div>
        </div>
    `;
    
    // 添加样式
    if (!document.getElementById('modal-styles')) {
        const style = document.createElement('style');
        style.id = 'modal-styles';
        style.textContent = `
            .modal {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                z-index: 10000;
                display: flex;
                align-items: center;
                justify-content: center;
            }
            
            .modal-overlay {
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.5);
            }
            
            .modal-content {
                position: relative;
                background: #fff;
                border-radius: 8px;
                width: 90%;
                max-width: 400px;
                max-height: 90vh;
                overflow-y: auto;
            }
            
            .modal-header {
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 16px 24px;
                border-bottom: 1px solid #f0f0f0;
            }
            
            .modal-title {
                margin: 0;
                font-size: 18px;
                font-weight: bold;
            }
            
            .modal-close {
                background: none;
                border: none;
                font-size: 24px;
                cursor: pointer;
                color: #8c8c8c;
                padding: 0;
                width: 32px;
                height: 32px;
                display: flex;
                align-items: center;
                justify-content: center;
            }
            
            .modal-body {
                padding: 24px;
            }
            
            .auth-form .form-group {
                margin-bottom: 16px;
            }
            
            .btn-block {
                width: 100%;
                margin-top: 8px;
            }
            
            .auth-links {
                text-align: center;
                margin-top: 16px;
            }
            
            .auth-links a {
                color: #1890ff;
                text-decoration: none;
                font-size: 14px;
            }
            
            .auth-links a:hover {
                text-decoration: underline;
            }
        `;
        document.head.appendChild(style);
    }
    
    document.body.appendChild(modal);
    
    // 关闭事件
    const closeBtn = modal.querySelector('.modal-close');
    const overlay = modal.querySelector('.modal-overlay');
    
    closeBtn.addEventListener('click', closeModal);
    overlay.addEventListener('click', closeModal);
    
    return modal;
}

// 关闭模态框
function closeModal() {
    const modal = document.querySelector('.modal');
    if (modal) {
        modal.remove();
    }
}

// 处理登录
function handleLogin() {
    const phone = document.getElementById('loginPhone').value;
    const password = document.getElementById('loginPassword').value;
    
    if (!phone || !password) {
        Utils.showMessage('请填写完整信息', 'warning');
        return;
    }
    
    // 模拟登录
    const submitBtn = document.querySelector('#loginForm button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    Utils.showLoading(submitBtn);
    
    setTimeout(() => {
        Utils.hideLoading(submitBtn, originalText);
        
        // 模拟登录成功
        const userInfo = {
            id: 1,
            name: '张三',
            phone: phone,
            points: 1200
        };
        
        Utils.storage.set('userInfo', userInfo);
        Utils.showMessage('登录成功', 'success');
        closeModal();
        updateNavForLoggedInUser(userInfo);
    }, 1500);
}

// 处理注册
function handleRegister() {
    const name = document.getElementById('registerName').value;
    const phone = document.getElementById('registerPhone').value;
    const password = document.getElementById('registerPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    if (!name || !phone || !password || !confirmPassword) {
        Utils.showMessage('请填写完整信息', 'warning');
        return;
    }
    
    if (password !== confirmPassword) {
        Utils.showMessage('两次输入的密码不一致', 'warning');
        return;
    }
    
    // 模拟注册
    const submitBtn = document.querySelector('#registerForm button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    Utils.showLoading(submitBtn);
    
    setTimeout(() => {
        Utils.hideLoading(submitBtn, originalText);
        
        // 模拟注册成功
        const userInfo = {
            id: Date.now(),
            name: name,
            phone: phone,
            points: 0
        };
        
        Utils.storage.set('userInfo', userInfo);
        Utils.showMessage('注册成功', 'success');
        closeModal();
        updateNavForLoggedInUser(userInfo);
    }, 1500);
}

// 初始化动画
function initAnimations() {
    // 监听滚动事件，添加动画
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, observerOptions);
    
    // 观察需要动画的元素
    const animatedElements = document.querySelectorAll('.route-card, .feature-item');
    animatedElements.forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(30px)';
        el.style.transition = 'all 0.6s ease-out';
        observer.observe(el);
    });
}