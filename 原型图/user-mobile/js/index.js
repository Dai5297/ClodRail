// 首页JavaScript功能

// 首页应用对象
const IndexApp = {
    data: {
        currentSwiperIndex: 0,
        tripType: 'oneWay', // oneWay: 单程, roundTrip: 往返
        fromStation: '北京',
        toStation: '上海',
        departDate: '',
        returnDate: '',
        swiperTimer: null,
        hotRoutes: [
            { from: '北京', to: '上海', price: 553, duration: '4小时28分' },
            { from: '广州', to: '深圳', price: 75, duration: '1小时19分' },
            { from: '杭州', to: '南京', price: 134, duration: '1小时17分' }
        ],
        quickActions: [
            { name: '抢票', icon: 'grab-ticket', color: '#f44336' },
            { name: '改签', icon: 'change-ticket', color: '#FF9800' },
            { name: '退票', icon: 'refund-ticket', color: '#4CAF50' },
            { name: '候补', icon: 'waitlist', color: '#9C27B0' },
            { name: '时刻表', icon: 'schedule', color: '#2196F3' },
            { name: '正晚点', icon: 'delay-info', color: '#607D8B' },
            { name: '乘车码', icon: 'qr-code', color: '#795548' },
            { name: '更多', icon: 'more', color: '#666' }
        ]
    },
    
    init() {
        this.initDate();
        this.initSwiper();
        this.bindEvents();
        this.loadHotRoutes();
    },
    
    // 初始化日期
    initDate() {
        const today = new Date();
        this.data.departDate = Utils.formatDate(today);
        
        const tomorrow = new Date(today);
        tomorrow.setDate(tomorrow.getDate() + 1);
        this.data.returnDate = Utils.formatDate(tomorrow);
        
        this.updateDateDisplay();
    },
    
    // 更新日期显示
    updateDateDisplay() {
        const departElement = document.querySelector('.depart-date');
        const returnElement = document.querySelector('.return-date');
        
        if (departElement) {
            const departDate = new Date(this.data.departDate);
            departElement.innerHTML = `
                <div class="date-main">${Utils.formatDate(departDate, 'MM-DD')}</div>
                <div class="date-week">${Utils.getWeekDay(departDate)}</div>
            `;
        }
        
        if (returnElement) {
            const returnDate = new Date(this.data.returnDate);
            returnElement.innerHTML = `
                <div class="date-main">${Utils.formatDate(returnDate, 'MM-DD')}</div>
                <div class="date-week">${Utils.getWeekDay(returnDate)}</div>
            `;
        }
    },
    
    // 初始化轮播图
    initSwiper() {
        const swiperContainer = document.querySelector('.swiper-wrapper');
        const indicators = document.querySelector('.swiper-indicators');
        
        if (!swiperContainer || !indicators) return;
        
        const slides = swiperContainer.children;
        const slideCount = slides.length;
        
        // 创建指示器
        indicators.innerHTML = '';
        for (let i = 0; i < slideCount; i++) {
            const indicator = document.createElement('div');
            indicator.className = `indicator ${i === 0 ? 'active' : ''}`;
            indicator.addEventListener('click', () => this.goToSlide(i));
            indicators.appendChild(indicator);
        }
        
        // 自动轮播
        this.startAutoSwiper();
        
        // 触摸滑动支持
        this.initTouchSwiper(swiperContainer);
    },
    
    // 开始自动轮播
    startAutoSwiper() {
        this.swiperTimer = setInterval(() => {
            const slideCount = document.querySelectorAll('.swiper-slide').length;
            this.data.currentSwiperIndex = (this.data.currentSwiperIndex + 1) % slideCount;
            this.updateSwiper();
        }, 4000);
    },
    
    // 停止自动轮播
    stopAutoSwiper() {
        if (this.swiperTimer) {
            clearInterval(this.swiperTimer);
            this.swiperTimer = null;
        }
    },
    
    // 跳转到指定幻灯片
    goToSlide(index) {
        this.data.currentSwiperIndex = index;
        this.updateSwiper();
        this.stopAutoSwiper();
        setTimeout(() => this.startAutoSwiper(), 5000);
    },
    
    // 更新轮播图
    updateSwiper() {
        const swiperWrapper = document.querySelector('.swiper-wrapper');
        const indicators = document.querySelectorAll('.indicator');
        
        if (swiperWrapper) {
            const translateX = -this.data.currentSwiperIndex * 100;
            swiperWrapper.style.transform = `translateX(${translateX}%)`;
        }
        
        indicators.forEach((indicator, index) => {
            indicator.classList.toggle('active', index === this.data.currentSwiperIndex);
        });
    },
    
    // 初始化触摸滑动
    initTouchSwiper(container) {
        let startX = 0;
        let startY = 0;
        let isDragging = false;
        
        container.addEventListener('touchstart', (e) => {
            startX = e.touches[0].clientX;
            startY = e.touches[0].clientY;
            isDragging = true;
            this.stopAutoSwiper();
        });
        
        container.addEventListener('touchmove', (e) => {
            if (!isDragging) return;
            e.preventDefault();
        });
        
        container.addEventListener('touchend', (e) => {
            if (!isDragging) return;
            
            const endX = e.changedTouches[0].clientX;
            const endY = e.changedTouches[0].clientY;
            const deltaX = endX - startX;
            const deltaY = endY - startY;
            
            // 判断是否为水平滑动
            if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 50) {
                const slideCount = document.querySelectorAll('.swiper-slide').length;
                
                if (deltaX > 0) {
                    // 向右滑动，上一张
                    this.data.currentSwiperIndex = (this.data.currentSwiperIndex - 1 + slideCount) % slideCount;
                } else {
                    // 向左滑动，下一张
                    this.data.currentSwiperIndex = (this.data.currentSwiperIndex + 1) % slideCount;
                }
                
                this.updateSwiper();
            }
            
            isDragging = false;
            setTimeout(() => this.startAutoSwiper(), 3000);
        });
    },
    
    // 绑定事件
    bindEvents() {
        // 单程/往返切换
        document.querySelectorAll('.trip-type-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const type = e.target.getAttribute('data-type');
                this.switchTripType(type);
            });
        });
        
        // 车站交换
        const exchangeBtn = document.querySelector('.exchange-btn');
        if (exchangeBtn) {
            exchangeBtn.addEventListener('click', () => this.exchangeStations());
        }
        
        // 出发站选择
        const fromStationBtn = document.querySelector('.from-station');
        if (fromStationBtn) {
            fromStationBtn.addEventListener('click', () => this.showStationPicker('from'));
        }
        
        // 到达站选择
        const toStationBtn = document.querySelector('.to-station');
        if (toStationBtn) {
            toStationBtn.addEventListener('click', () => this.showStationPicker('to'));
        }
        
        // 出发日期选择
        const departDateBtn = document.querySelector('.depart-date');
        if (departDateBtn) {
            departDateBtn.addEventListener('click', () => this.showDatePicker('depart'));
        }
        
        // 返程日期选择
        const returnDateBtn = document.querySelector('.return-date');
        if (returnDateBtn) {
            returnDateBtn.addEventListener('click', () => this.showDatePicker('return'));
        }
        
        // 查询按钮
        const searchBtn = document.querySelector('.search-btn');
        if (searchBtn) {
            searchBtn.addEventListener('click', () => this.searchTickets());
        }
        
        // 快捷功能
        document.querySelectorAll('.quick-item').forEach(item => {
            item.addEventListener('click', (e) => {
                const action = e.currentTarget.getAttribute('data-action');
                this.handleQuickAction(action);
            });
        });
        
        // 热门线路
        document.querySelectorAll('.route-item').forEach(item => {
            item.addEventListener('click', (e) => {
                const from = e.currentTarget.getAttribute('data-from');
                const to = e.currentTarget.getAttribute('data-to');
                this.selectRoute(from, to);
            });
        });
        
        // 城市选择器事件
        this.bindStationPickerEvents();
        
        // 日期选择器事件
        this.bindDatePickerEvents();
    },
    
    // 切换单程/往返
    switchTripType(type) {
        this.data.tripType = type;
        
        document.querySelectorAll('.trip-type-btn').forEach(btn => {
            btn.classList.toggle('active', btn.getAttribute('data-type') === type);
        });
        
        const returnDateSection = document.querySelector('.return-date-section');
        if (returnDateSection) {
            returnDateSection.style.display = type === 'roundTrip' ? 'flex' : 'none';
        }
    },
    
    // 交换车站
    exchangeStations() {
        const temp = this.data.fromStation;
        this.data.fromStation = this.data.toStation;
        this.data.toStation = temp;
        
        this.updateStationDisplay();
        
        // 添加动画效果
        const exchangeBtn = document.querySelector('.exchange-btn');
        if (exchangeBtn) {
            exchangeBtn.style.transform = 'rotate(180deg)';
            setTimeout(() => {
                exchangeBtn.style.transform = 'rotate(0deg)';
            }, 300);
        }
    },
    
    // 更新车站显示
    updateStationDisplay() {
        const fromStationElement = document.querySelector('.from-station .station-name');
        const toStationElement = document.querySelector('.to-station .station-name');
        
        if (fromStationElement) {
            fromStationElement.textContent = this.data.fromStation;
        }
        
        if (toStationElement) {
            toStationElement.textContent = this.data.toStation;
        }
    },
    
    // 显示车站选择器
    showStationPicker(type) {
        const modal = document.querySelector('.station-picker');
        if (modal) {
            modal.setAttribute('data-type', type);
            Modal.show('.station-picker');
        }
    },
    
    // 显示日期选择器
    showDatePicker(type) {
        const modal = document.querySelector('.date-picker');
        if (modal) {
            modal.setAttribute('data-type', type);
            this.generateDateOptions(type);
            Modal.show('.date-picker');
        }
    },
    
    // 生成日期选项
    generateDateOptions(type) {
        const dateList = document.querySelector('.date-list');
        if (!dateList) return;
        
        const today = new Date();
        const dates = [];
        
        // 生成未来30天的日期
        for (let i = 0; i < 30; i++) {
            const date = new Date(today);
            date.setDate(today.getDate() + i);
            dates.push(date);
        }
        
        dateList.innerHTML = dates.map(date => {
            const dateStr = Utils.formatDate(date);
            const isSelected = (type === 'depart' && dateStr === this.data.departDate) ||
                             (type === 'return' && dateStr === this.data.returnDate);
            
            return `
                <div class="date-option ${isSelected ? 'selected' : ''}" data-date="${dateStr}">
                    <div class="date-day">${Utils.formatDate(date, 'MM-DD')}</div>
                    <div class="date-week">${Utils.getWeekDay(date)}</div>
                    ${i === 0 ? '<div class="date-label">今天</div>' : ''}
                    ${i === 1 ? '<div class="date-label">明天</div>' : ''}
                </div>
            `;
        }).join('');
    },
    
    // 绑定车站选择器事件
    bindStationPickerEvents() {
        // 关闭按钮
        const closeBtn = document.querySelector('.station-picker .close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => Modal.hide('.station-picker'));
        }
        
        // 车站选择
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('station-option')) {
                const station = e.target.getAttribute('data-station');
                const modal = document.querySelector('.station-picker');
                const type = modal.getAttribute('data-type');
                
                if (type === 'from') {
                    this.data.fromStation = station;
                } else {
                    this.data.toStation = station;
                }
                
                this.updateStationDisplay();
                Modal.hide('.station-picker');
            }
        });
        
        // 搜索功能
        const searchInput = document.querySelector('.station-search');
        if (searchInput) {
            searchInput.addEventListener('input', Utils.debounce((e) => {
                this.filterStations(e.target.value);
            }, 300));
        }
    },
    
    // 绑定日期选择器事件
    bindDatePickerEvents() {
        // 关闭按钮
        const closeBtn = document.querySelector('.date-picker .close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => Modal.hide('.date-picker'));
        }
        
        // 日期选择
        document.addEventListener('click', (e) => {
            if (e.target.closest('.date-option')) {
                const dateOption = e.target.closest('.date-option');
                const date = dateOption.getAttribute('data-date');
                const modal = document.querySelector('.date-picker');
                const type = modal.getAttribute('data-type');
                
                if (type === 'depart') {
                    this.data.departDate = date;
                    
                    // 如果选择的出发日期晚于返程日期，自动调整返程日期
                    if (this.data.tripType === 'roundTrip' && date >= this.data.returnDate) {
                        const returnDate = new Date(date);
                        returnDate.setDate(returnDate.getDate() + 1);
                        this.data.returnDate = Utils.formatDate(returnDate);
                    }
                } else {
                    this.data.returnDate = date;
                }
                
                this.updateDateDisplay();
                Modal.hide('.date-picker');
            }
        });
    },
    
    // 过滤车站
    filterStations(keyword) {
        const stations = document.querySelectorAll('.station-option');
        
        stations.forEach(station => {
            const stationName = station.getAttribute('data-station');
            const isMatch = stationName.toLowerCase().includes(keyword.toLowerCase());
            station.style.display = isMatch ? 'block' : 'none';
        });
    },
    
    // 查询车票
    searchTickets() {
        // 验证输入
        if (this.data.fromStation === this.data.toStation) {
            Toast.error('出发地和目的地不能相同');
            return;
        }
        
        if (!this.data.departDate) {
            Toast.error('请选择出发日期');
            return;
        }
        
        if (this.data.tripType === 'roundTrip' && !this.data.returnDate) {
            Toast.error('请选择返程日期');
            return;
        }
        
        // 保存查询条件
        const searchParams = {
            from: this.data.fromStation,
            to: this.data.toStation,
            departDate: this.data.departDate,
            returnDate: this.data.returnDate,
            tripType: this.data.tripType
        };
        
        Storage.set('search_params', searchParams);
        
        // 跳转到查询结果页
        Toast.success('正在查询车次信息...');
        setTimeout(() => {
            window.location.href = 'search.html';
        }, 1000);
    },
    
    // 处理快捷功能
    handleQuickAction(action) {
        const actionMap = {
            'grab-ticket': () => {
                Toast.info('抢票功能开发中...');
            },
            'change-ticket': () => {
                if (!Auth.isLoggedIn()) {
                    Toast.error('请先登录');
                    return;
                }
                window.location.href = 'order.html';
            },
            'refund-ticket': () => {
                if (!Auth.isLoggedIn()) {
                    Toast.error('请先登录');
                    return;
                }
                window.location.href = 'order.html';
            },
            'waitlist': () => {
                Toast.info('候补功能开发中...');
            },
            'schedule': () => {
                Toast.info('时刻表功能开发中...');
            },
            'delay-info': () => {
                Toast.info('正晚点查询功能开发中...');
            },
            'qr-code': () => {
                if (!Auth.isLoggedIn()) {
                    Toast.error('请先登录');
                    return;
                }
                Toast.info('乘车码功能开发中...');
            },
            'more': () => {
                Toast.info('更多功能开发中...');
            }
        };
        
        const handler = actionMap[action];
        if (handler) {
            handler();
        }
    },
    
    // 选择热门线路
    selectRoute(from, to) {
        this.data.fromStation = from;
        this.data.toStation = to;
        this.updateStationDisplay();
        
        Toast.success(`已选择 ${from} → ${to}`);
        
        // 滚动到查询表单
        const searchForm = document.querySelector('.search-form');
        if (searchForm) {
            searchForm.scrollIntoView({ behavior: 'smooth' });
        }
    },
    
    // 加载热门线路
    loadHotRoutes() {
        const routeList = document.querySelector('.route-list');
        if (!routeList) return;
        
        routeList.innerHTML = this.data.hotRoutes.map(route => `
            <div class="route-item" data-from="${route.from}" data-to="${route.to}">
                <div class="route-info">
                    <div class="route-cities">
                        <span class="from-city">${route.from}</span>
                        <img src="https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=simple%20blue%20arrow%20pointing%20right%20icon%20minimalist%20design&image_size=square" alt="→" class="route-arrow">
                        <span class="to-city">${route.to}</span>
                    </div>
                    <div class="route-duration">${route.duration}</div>
                </div>
                <div class="route-price">
                    <span class="price-label">起</span>
                    <span class="price-value">¥${route.price}</span>
                </div>
            </div>
        `).join('');
        
        // 重新绑定点击事件
        document.querySelectorAll('.route-item').forEach(item => {
            item.addEventListener('click', (e) => {
                const from = e.currentTarget.getAttribute('data-from');
                const to = e.currentTarget.getAttribute('data-to');
                this.selectRoute(from, to);
            });
        });
    },
    
    // 页面销毁时清理
    destroy() {
        this.stopAutoSwiper();
    }
};

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    IndexApp.init();
});

// 页面卸载时清理
window.addEventListener('beforeunload', () => {
    IndexApp.destroy();
});

// 导出到全局
window.IndexApp = IndexApp;