// 车次查询页面JavaScript功能

// 查询页面应用对象
const SearchApp = {
    data: {
        searchParams: {},
        trainList: [],
        filteredTrainList: [],
        currentSort: 'time', // time: 按时间, duration: 按耗时, price: 按价格
        filters: {
            timeRange: 'all', // all, morning, afternoon, evening, night
            trainType: 'all', // all, G, D, C, K, T, Z
            seatType: 'all' // all, business, first, second
        },
        isLoading: false
    },
    
    init() {
        this.loadSearchParams();
        this.updateSearchHeader();
        this.bindEvents();
        this.loadTrainData();
    },
    
    // 加载搜索参数
    loadSearchParams() {
        this.data.searchParams = Storage.get('search_params', {
            from: '北京',
            to: '上海',
            departDate: Utils.formatDate(new Date()),
            tripType: 'oneWay'
        });
    },
    
    // 更新搜索头部显示
    updateSearchHeader() {
        const { from, to, departDate } = this.data.searchParams;
        
        const fromElement = document.querySelector('.search-from');
        const toElement = document.querySelector('.search-to');
        const dateElement = document.querySelector('.search-date');
        
        if (fromElement) fromElement.textContent = from;
        if (toElement) toElement.textContent = to;
        if (dateElement) {
            const date = new Date(departDate);
            dateElement.innerHTML = `
                <span class="date-main">${Utils.formatDate(date, 'MM-DD')}</span>
                <span class="date-week">${Utils.getWeekDay(date)}</span>
            `;
        }
    },
    
    // 绑定事件
    bindEvents() {
        // 返回按钮
        const backBtn = document.querySelector('.back-btn');
        if (backBtn) {
            backBtn.addEventListener('click', () => {
                window.history.back();
            });
        }
        
        // 搜索头部点击重新搜索
        const searchHeader = document.querySelector('.search-header');
        if (searchHeader) {
            searchHeader.addEventListener('click', () => {
                window.location.href = 'index.html';
            });
        }
        
        // 快速筛选标签
        document.querySelectorAll('.filter-tag').forEach(tag => {
            tag.addEventListener('click', (e) => {
                const filterType = e.target.getAttribute('data-filter');
                const filterValue = e.target.getAttribute('data-value');
                this.toggleQuickFilter(filterType, filterValue, e.target);
            });
        });
        
        // 排序按钮
        document.querySelectorAll('.sort-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const sortType = e.target.getAttribute('data-sort');
                this.changeSortType(sortType);
            });
        });
        
        // 筛选按钮
        const filterBtn = document.querySelector('.filter-btn');
        if (filterBtn) {
            filterBtn.addEventListener('click', () => {
                Modal.show('.filter-modal');
            });
        }
        
        // 车次列表项点击
        document.addEventListener('click', (e) => {
            const trainItem = e.target.closest('.train-item');
            if (trainItem && !trainItem.classList.contains('no-ticket')) {
                const trainNumber = trainItem.getAttribute('data-train');
                this.selectTrain(trainNumber);
            }
        });
        
        // 筛选弹窗事件
        this.bindFilterModalEvents();
        
        // 下拉刷新
        this.bindPullRefresh();
    },
    
    // 绑定筛选弹窗事件
    bindFilterModalEvents() {
        // 关闭按钮
        const closeBtn = document.querySelector('.filter-modal .close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                Modal.hide('.filter-modal');
            });
        }
        
        // 重置按钮
        const resetBtn = document.querySelector('.filter-reset');
        if (resetBtn) {
            resetBtn.addEventListener('click', () => {
                this.resetFilters();
            });
        }
        
        // 确认按钮
        const confirmBtn = document.querySelector('.filter-confirm');
        if (confirmBtn) {
            confirmBtn.addEventListener('click', () => {
                this.applyFilters();
                Modal.hide('.filter-modal');
            });
        }
        
        // 时间段选择
        document.querySelectorAll('.time-option').forEach(option => {
            option.addEventListener('click', (e) => {
                const timeRange = e.target.getAttribute('data-time');
                this.selectTimeRange(timeRange);
            });
        });
        
        // 车次类型选择
        document.querySelectorAll('.train-type-option').forEach(option => {
            option.addEventListener('click', (e) => {
                const trainType = e.target.getAttribute('data-type');
                this.toggleTrainType(trainType);
            });
        });
        
        // 座席类型选择
        document.querySelectorAll('.seat-type-option').forEach(option => {
            option.addEventListener('click', (e) => {
                const seatType = e.target.getAttribute('data-seat');
                this.toggleSeatType(seatType);
            });
        });
    },
    
    // 绑定下拉刷新
    bindPullRefresh() {
        let startY = 0;
        let currentY = 0;
        let isPulling = false;
        const pullThreshold = 80;
        
        const trainList = document.querySelector('.train-list');
        if (!trainList) return;
        
        trainList.addEventListener('touchstart', (e) => {
            if (trainList.scrollTop === 0) {
                startY = e.touches[0].clientY;
                isPulling = true;
            }
        });
        
        trainList.addEventListener('touchmove', (e) => {
            if (!isPulling) return;
            
            currentY = e.touches[0].clientY;
            const pullDistance = currentY - startY;
            
            if (pullDistance > 0 && trainList.scrollTop === 0) {
                e.preventDefault();
                
                if (pullDistance > pullThreshold) {
                    // 显示释放刷新提示
                    this.showPullRefreshHint('release');
                } else {
                    // 显示下拉刷新提示
                    this.showPullRefreshHint('pull');
                }
            }
        });
        
        trainList.addEventListener('touchend', (e) => {
            if (!isPulling) return;
            
            const pullDistance = currentY - startY;
            
            if (pullDistance > pullThreshold) {
                this.refreshTrainData();
            }
            
            this.hidePullRefreshHint();
            isPulling = false;
        });
    },
    
    // 显示下拉刷新提示
    showPullRefreshHint(type) {
        let hint = document.querySelector('.pull-refresh-hint');
        if (!hint) {
            hint = document.createElement('div');
            hint.className = 'pull-refresh-hint';
            document.querySelector('.train-list').prepend(hint);
        }
        
        hint.textContent = type === 'release' ? '释放立即刷新' : '下拉可以刷新';
        hint.style.display = 'block';
    },
    
    // 隐藏下拉刷新提示
    hidePullRefreshHint() {
        const hint = document.querySelector('.pull-refresh-hint');
        if (hint) {
            hint.style.display = 'none';
        }
    },
    
    // 切换快速筛选
    toggleQuickFilter(filterType, filterValue, element) {
        const isActive = element.classList.contains('active');
        
        // 移除同类型其他标签的激活状态
        document.querySelectorAll(`[data-filter="${filterType}"]`).forEach(tag => {
            tag.classList.remove('active');
        });
        
        if (!isActive) {
            element.classList.add('active');
            this.data.filters[filterType] = filterValue;
        } else {
            this.data.filters[filterType] = 'all';
        }
        
        this.applyFilters();
    },
    
    // 改变排序方式
    changeSortType(sortType) {
        this.data.currentSort = sortType;
        
        // 更新排序按钮状态
        document.querySelectorAll('.sort-btn').forEach(btn => {
            btn.classList.toggle('active', btn.getAttribute('data-sort') === sortType);
        });
        
        this.sortTrainList();
        this.renderTrainList();
    },
    
    // 选择时间段
    selectTimeRange(timeRange) {
        document.querySelectorAll('.time-option').forEach(option => {
            option.classList.toggle('selected', option.getAttribute('data-time') === timeRange);
        });
        
        this.data.filters.timeRange = timeRange;
    },
    
    // 切换车次类型
    toggleTrainType(trainType) {
        const option = document.querySelector(`[data-type="${trainType}"]`);
        if (option) {
            option.classList.toggle('selected');
        }
        
        // 更新筛选条件
        const selectedTypes = Array.from(document.querySelectorAll('.train-type-option.selected'))
            .map(el => el.getAttribute('data-type'))
            .filter(type => type !== 'all');
        
        this.data.filters.trainType = selectedTypes.length > 0 ? selectedTypes : 'all';
    },
    
    // 切换座席类型
    toggleSeatType(seatType) {
        const option = document.querySelector(`[data-seat="${seatType}"]`);
        if (option) {
            option.classList.toggle('selected');
        }
        
        // 更新筛选条件
        const selectedSeats = Array.from(document.querySelectorAll('.seat-type-option.selected'))
            .map(el => el.getAttribute('data-seat'))
            .filter(seat => seat !== 'all');
        
        this.data.filters.seatType = selectedSeats.length > 0 ? selectedSeats : 'all';
    },
    
    // 重置筛选条件
    resetFilters() {
        this.data.filters = {
            timeRange: 'all',
            trainType: 'all',
            seatType: 'all'
        };
        
        // 重置UI状态
        document.querySelectorAll('.time-option, .train-type-option, .seat-type-option').forEach(option => {
            option.classList.remove('selected');
        });
        
        // 默认选中"全部"
        document.querySelectorAll('[data-time="all"], [data-type="all"], [data-seat="all"]').forEach(option => {
            option.classList.add('selected');
        });
    },
    
    // 应用筛选条件
    applyFilters() {
        this.data.filteredTrainList = this.data.trainList.filter(train => {
            // 时间段筛选
            if (this.data.filters.timeRange !== 'all') {
                const departHour = parseInt(train.departTime.split(':')[0]);
                const timeRanges = {
                    morning: [6, 12],
                    afternoon: [12, 18],
                    evening: [18, 24],
                    night: [0, 6]
                };
                
                const [start, end] = timeRanges[this.data.filters.timeRange];
                if (this.data.filters.timeRange === 'night') {
                    if (!(departHour >= start || departHour < end)) return false;
                } else {
                    if (!(departHour >= start && departHour < end)) return false;
                }
            }
            
            // 车次类型筛选
            if (this.data.filters.trainType !== 'all') {
                const trainTypes = Array.isArray(this.data.filters.trainType) 
                    ? this.data.filters.trainType 
                    : [this.data.filters.trainType];
                
                if (!trainTypes.includes(train.type)) return false;
            }
            
            // 座席类型筛选
            if (this.data.filters.seatType !== 'all') {
                const seatTypes = Array.isArray(this.data.filters.seatType) 
                    ? this.data.filters.seatType 
                    : [this.data.filters.seatType];
                
                const hasAvailableSeats = seatTypes.some(seatType => {
                    return train.seats[seatType] && train.seats[seatType].available > 0;
                });
                
                if (!hasAvailableSeats) return false;
            }
            
            return true;
        });
        
        this.sortTrainList();
        this.renderTrainList();
        
        // 更新结果数量
        this.updateResultCount();
    },
    
    // 排序车次列表
    sortTrainList() {
        this.data.filteredTrainList.sort((a, b) => {
            switch (this.data.currentSort) {
                case 'time':
                    return a.departTime.localeCompare(b.departTime);
                case 'duration':
                    return a.duration - b.duration;
                case 'price':
                    return a.minPrice - b.minPrice;
                default:
                    return 0;
            }
        });
    },
    
    // 加载车次数据
    async loadTrainData() {
        this.data.isLoading = true;
        this.showLoading();
        
        try {
            // 模拟API请求
            await new Promise(resolve => setTimeout(resolve, 1000));
            
            // 模拟车次数据
            this.data.trainList = this.generateMockTrainData();
            this.data.filteredTrainList = [...this.data.trainList];
            
            this.sortTrainList();
            this.renderTrainList();
            this.updateResultCount();
        } catch (error) {
            Toast.error('加载车次信息失败');
            this.showEmptyState();
        } finally {
            this.data.isLoading = false;
            this.hideLoading();
        }
    },
    
    // 刷新车次数据
    async refreshTrainData() {
        this.showPullRefreshHint('loading');
        
        try {
            await new Promise(resolve => setTimeout(resolve, 1500));
            await this.loadTrainData();
            Toast.success('刷新成功');
        } catch (error) {
            Toast.error('刷新失败');
        } finally {
            this.hidePullRefreshHint();
        }
    },
    
    // 生成模拟车次数据
    generateMockTrainData() {
        const trainTypes = ['G', 'D', 'C', 'K', 'T', 'Z'];
        const trains = [];
        
        for (let i = 1; i <= 15; i++) {
            const type = trainTypes[Math.floor(Math.random() * trainTypes.length)];
            const number = `${type}${Math.floor(Math.random() * 9000) + 1000}`;
            
            const departHour = Math.floor(Math.random() * 18) + 6;
            const departMinute = Math.floor(Math.random() * 60);
            const departTime = `${departHour.toString().padStart(2, '0')}:${departMinute.toString().padStart(2, '0')}`;
            
            const duration = Math.floor(Math.random() * 480) + 120; // 2-10小时
            const arriveTime = this.calculateArriveTime(departTime, duration);
            
            const basePrice = Math.floor(Math.random() * 500) + 100;
            const hasTicket = Math.random() > 0.2; // 80%有票
            
            trains.push({
                number,
                type,
                departTime,
                arriveTime,
                duration,
                minPrice: basePrice,
                hasTicket,
                seats: {
                    business: {
                        price: Math.floor(basePrice * 2.5),
                        available: hasTicket ? Math.floor(Math.random() * 20) : 0
                    },
                    first: {
                        price: Math.floor(basePrice * 1.8),
                        available: hasTicket ? Math.floor(Math.random() * 50) : 0
                    },
                    second: {
                        price: basePrice,
                        available: hasTicket ? Math.floor(Math.random() * 100) : 0
                    }
                }
            });
        }
        
        return trains;
    },
    
    // 计算到达时间
    calculateArriveTime(departTime, duration) {
        const [hour, minute] = departTime.split(':').map(Number);
        const departMinutes = hour * 60 + minute;
        const arriveMinutes = (departMinutes + duration) % (24 * 60);
        
        const arriveHour = Math.floor(arriveMinutes / 60);
        const arriveMin = arriveMinutes % 60;
        
        return `${arriveHour.toString().padStart(2, '0')}:${arriveMin.toString().padStart(2, '0')}`;
    },
    
    // 渲染车次列表
    renderTrainList() {
        const trainList = document.querySelector('.train-list');
        if (!trainList) return;
        
        if (this.data.filteredTrainList.length === 0) {
            this.showEmptyState();
            return;
        }
        
        trainList.innerHTML = this.data.filteredTrainList.map(train => {
            const durationHours = Math.floor(train.duration / 60);
            const durationMinutes = train.duration % 60;
            const durationText = `${durationHours}小时${durationMinutes}分`;
            
            return `
                <div class="train-item ${!train.hasTicket ? 'no-ticket' : ''}" data-train="${train.number}">
                    <div class="train-header">
                        <div class="train-number">
                            <span class="number">${train.number}</span>
                            <span class="type">${this.getTrainTypeName(train.type)}</span>
                        </div>
                        <div class="train-status">
                            ${train.hasTicket ? '<span class="status available">有票</span>' : '<span class="status unavailable">无票</span>'}
                        </div>
                    </div>
                    
                    <div class="train-schedule">
                        <div class="schedule-item">
                            <div class="time">${train.departTime}</div>
                            <div class="station">${this.data.searchParams.from}</div>
                        </div>
                        <div class="schedule-duration">
                            <div class="duration-line"></div>
                            <div class="duration-text">${durationText}</div>
                        </div>
                        <div class="schedule-item">
                            <div class="time">${train.arriveTime}</div>
                            <div class="station">${this.data.searchParams.to}</div>
                        </div>
                    </div>
                    
                    <div class="train-seats">
                        ${train.seats.business.available > 0 ? `
                            <div class="seat-info">
                                <div class="seat-type">商务座</div>
                                <div class="seat-price">¥${train.seats.business.price}</div>
                                <div class="seat-count">${train.seats.business.available}张</div>
                            </div>
                        ` : ''}
                        ${train.seats.first.available > 0 ? `
                            <div class="seat-info">
                                <div class="seat-type">一等座</div>
                                <div class="seat-price">¥${train.seats.first.price}</div>
                                <div class="seat-count">${train.seats.first.available}张</div>
                            </div>
                        ` : ''}
                        ${train.seats.second.available > 0 ? `
                            <div class="seat-info">
                                <div class="seat-type">二等座</div>
                                <div class="seat-price">¥${train.seats.second.price}</div>
                                <div class="seat-count">${train.seats.second.available}张</div>
                            </div>
                        ` : ''}
                        ${!train.hasTicket ? '<div class="no-ticket-info">暂无余票</div>' : ''}
                    </div>
                </div>
            `;
        }).join('');
    },
    
    // 获取车次类型名称
    getTrainTypeName(type) {
        const typeNames = {
            'G': '高速',
            'D': '动车',
            'C': '城际',
            'K': '快速',
            'T': '特快',
            'Z': '直达'
        };
        return typeNames[type] || type;
    },
    
    // 更新结果数量
    updateResultCount() {
        const countElement = document.querySelector('.result-count');
        if (countElement) {
            countElement.textContent = `共${this.data.filteredTrainList.length}个车次`;
        }
    },
    
    // 显示加载状态
    showLoading() {
        const trainList = document.querySelector('.train-list');
        if (trainList) {
            trainList.innerHTML = `
                <div class="loading-state">
                    <div class="loading-spinner"></div>
                    <div class="loading-text">正在查询车次信息...</div>
                </div>
            `;
        }
    },
    
    // 隐藏加载状态
    hideLoading() {
        // 加载状态会被renderTrainList替换
    },
    
    // 显示空状态
    showEmptyState() {
        const trainList = document.querySelector('.train-list');
        if (trainList) {
            trainList.innerHTML = `
                <div class="empty-state">
                    <img src="https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=empty%20train%20search%20results%20illustration%20minimalist%20blue%20theme&image_size=square" alt="暂无结果" class="empty-image">
                    <div class="empty-text">暂无符合条件的车次</div>
                    <div class="empty-subtext">请尝试调整筛选条件或选择其他日期</div>
                    <button class="retry-btn" onclick="SearchApp.loadTrainData()">重新查询</button>
                </div>
            `;
        }
    },
    
    // 选择车次
    selectTrain(trainNumber) {
        const train = this.data.filteredTrainList.find(t => t.number === trainNumber);
        if (!train) return;
        
        // 保存选中的车次信息
        const ticketInfo = {
            ...this.data.searchParams,
            train,
            selectedTrain: trainNumber
        };
        
        Storage.set('ticket_info', ticketInfo);
        
        Toast.success(`已选择 ${trainNumber} 次列车`);
        setTimeout(() => {
            window.location.href = 'ticket.html';
        }, 1000);
    }
};

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    SearchApp.init();
});

// 导出到全局
window.SearchApp = SearchApp;