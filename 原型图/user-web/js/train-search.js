// 车次查询页面JavaScript

class TrainSearch {
    constructor() {
        this.currentFilters = {
            departureTime: [],
            trainType: [],
            seatType: []
        };
        this.currentSort = 'departure';
        this.trainData = [];
        this.filteredData = [];
        this.isLoading = false;
        this.init();
    }

    init() {
        this.bindEvents();
        this.loadSearchParams();
        this.searchTrains();
    }

    bindEvents() {
        // 搜索表单提交
        const searchForm = document.getElementById('searchForm');
        if (searchForm) {
            searchForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.searchTrains();
            });
        }

        // 筛选条件变化
        const filterCheckboxes = document.querySelectorAll('.filter-option input[type="checkbox"]');
        filterCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', () => {
                this.updateFilters();
                this.applyFilters();
            });
        });

        // 排序变化
        const sortSelect = document.getElementById('sortSelect');
        if (sortSelect) {
            sortSelect.addEventListener('change', (e) => {
                this.currentSort = e.target.value;
                this.sortAndRender();
            });
        }

        // 预订按钮点击
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('btn-book')) {
                const trainNumber = e.target.dataset.trainNumber;
                this.bookTrain(trainNumber);
            }
        });

        // 加载更多
        const loadMoreBtn = document.getElementById('loadMoreBtn');
        if (loadMoreBtn) {
            loadMoreBtn.addEventListener('click', () => {
                this.loadMoreTrains();
            });
        }
    }

    loadSearchParams() {
        const urlParams = new URLSearchParams(window.location.search);
        const departure = urlParams.get('departure');
        const destination = urlParams.get('destination');
        const date = urlParams.get('date');

        if (departure) {
            const departureInput = document.getElementById('departure');
            if (departureInput) departureInput.value = departure;
        }

        if (destination) {
            const destinationInput = document.getElementById('destination');
            if (destinationInput) destinationInput.value = destination;
        }

        if (date) {
            const dateInput = document.getElementById('departureDate');
            if (dateInput) dateInput.value = date;
        }
    }

    async searchTrains() {
        if (this.isLoading) return;

        const departure = document.getElementById('departure')?.value;
        const destination = document.getElementById('destination')?.value;
        const date = document.getElementById('departureDate')?.value;

        if (!departure || !destination || !date) {
            Utils.showMessage('请填写完整的查询条件', 'warning');
            return;
        }

        this.showLoading();
        this.isLoading = true;

        try {
            // 模拟API调用
            await Utils.delay(1000);
            this.trainData = this.generateMockTrainData(departure, destination, date);
            this.filteredData = [...this.trainData];
            this.applyFilters();
            Utils.showMessage('查询成功', 'success');
        } catch (error) {
            console.error('搜索失败:', error);
            Utils.showMessage('查询失败，请重试', 'error');
            this.showEmptyState();
        } finally {
            this.isLoading = false;
        }
    }

    generateMockTrainData(departure, destination, date) {
        const trainTypes = ['G', 'D', 'C', 'K', 'T', 'Z'];
        const trains = [];

        for (let i = 0; i < 15; i++) {
            const type = trainTypes[Math.floor(Math.random() * trainTypes.length)];
            const number = type + (Math.floor(Math.random() * 9000) + 1000);
            
            const departureHour = Math.floor(Math.random() * 18) + 6;
            const departureMinute = Math.floor(Math.random() * 60);
            const duration = Math.floor(Math.random() * 8) + 2; // 2-10小时
            
            const departureTime = `${departureHour.toString().padStart(2, '0')}:${departureMinute.toString().padStart(2, '0')}`;
            const arrivalTime = this.addHours(departureTime, duration);

            trains.push({
                number: number,
                type: type,
                departure: {
                    station: departure,
                    time: departureTime
                },
                arrival: {
                    station: destination,
                    time: arrivalTime
                },
                duration: `${duration}小时${Math.floor(Math.random() * 60)}分钟`,
                seats: this.generateSeatInfo(type),
                date: date
            });
        }

        return trains.sort((a, b) => a.departure.time.localeCompare(b.departure.time));
    }

    generateSeatInfo(trainType) {
        const seatTypes = {
            'G': ['商务座', '一等座', '二等座'],
            'D': ['一等座', '二等座'],
            'C': ['一等座', '二等座'],
            'K': ['硬卧', '软卧', '硬座'],
            'T': ['硬卧', '软卧', '硬座'],
            'Z': ['硬卧', '软卧', '硬座']
        };

        const types = seatTypes[trainType] || ['二等座'];
        const seats = [];

        types.forEach(type => {
            const basePrice = this.getBasePrice(type);
            const available = Math.floor(Math.random() * 100);
            let status = 'available';
            
            if (available < 10) {
                status = 'limited';
            } else if (available === 0) {
                status = 'sold-out';
            }

            seats.push({
                type: type,
                price: basePrice + Math.floor(Math.random() * 100),
                available: available,
                status: status
            });
        });

        return seats;
    }

    getBasePrice(seatType) {
        const prices = {
            '商务座': 800,
            '一等座': 400,
            '二等座': 200,
            '硬卧': 150,
            '软卧': 250,
            '硬座': 80
        };
        return prices[seatType] || 200;
    }

    addHours(time, hours) {
        const [h, m] = time.split(':').map(Number);
        const totalMinutes = h * 60 + m + hours * 60;
        const newHours = Math.floor(totalMinutes / 60) % 24;
        const newMinutes = totalMinutes % 60;
        return `${newHours.toString().padStart(2, '0')}:${newMinutes.toString().padStart(2, '0')}`;
    }

    updateFilters() {
        // 重置筛选条件
        this.currentFilters = {
            departureTime: [],
            trainType: [],
            seatType: []
        };

        // 获取选中的筛选条件
        const checkedFilters = document.querySelectorAll('.filter-option input[type="checkbox"]:checked');
        checkedFilters.forEach(checkbox => {
            const filterType = checkbox.dataset.filterType;
            const filterValue = checkbox.dataset.filterValue;
            
            if (this.currentFilters[filterType]) {
                this.currentFilters[filterType].push(filterValue);
            }
        });
    }

    applyFilters() {
        this.filteredData = this.trainData.filter(train => {
            // 出发时间筛选
            if (this.currentFilters.departureTime.length > 0) {
                const hour = parseInt(train.departure.time.split(':')[0]);
                let timeMatch = false;
                
                this.currentFilters.departureTime.forEach(timeRange => {
                    switch (timeRange) {
                        case 'morning':
                            if (hour >= 6 && hour < 12) timeMatch = true;
                            break;
                        case 'afternoon':
                            if (hour >= 12 && hour < 18) timeMatch = true;
                            break;
                        case 'evening':
                            if (hour >= 18 && hour < 24) timeMatch = true;
                            break;
                        case 'night':
                            if (hour >= 0 && hour < 6) timeMatch = true;
                            break;
                    }
                });
                
                if (!timeMatch) return false;
            }

            // 车次类型筛选
            if (this.currentFilters.trainType.length > 0) {
                if (!this.currentFilters.trainType.includes(train.type)) {
                    return false;
                }
            }

            // 座位类型筛选
            if (this.currentFilters.seatType.length > 0) {
                const trainSeatTypes = train.seats.map(seat => seat.type);
                const hasMatchingSeat = this.currentFilters.seatType.some(seatType => 
                    trainSeatTypes.includes(seatType)
                );
                if (!hasMatchingSeat) return false;
            }

            return true;
        });

        this.sortAndRender();
    }

    sortAndRender() {
        // 排序
        this.filteredData.sort((a, b) => {
            switch (this.currentSort) {
                case 'departure':
                    return a.departure.time.localeCompare(b.departure.time);
                case 'duration':
                    return a.duration.localeCompare(b.duration);
                case 'price':
                    const priceA = Math.min(...a.seats.map(s => s.price));
                    const priceB = Math.min(...b.seats.map(s => s.price));
                    return priceA - priceB;
                default:
                    return 0;
            }
        });

        this.renderTrainList();
    }

    renderTrainList() {
        const container = document.getElementById('trainList');
        if (!container) return;

        if (this.filteredData.length === 0) {
            this.showEmptyState();
            return;
        }

        container.innerHTML = this.filteredData.map(train => `
            <div class="train-item">
                <div class="train-number">
                    <div>${train.number}</div>
                    <div class="train-type">${this.getTrainTypeName(train.type)}</div>
                </div>
                
                <div class="train-time">
                    <div class="time-row">
                        <span class="departure-time">${train.departure.time}</span>
                        <span class="arrow">→</span>
                        <span class="arrival-time">${train.arrival.time}</span>
                    </div>
                    <div class="time-row">
                        <span class="departure-station">${train.departure.station}</span>
                        <span class="arrival-station">${train.arrival.station}</span>
                    </div>
                </div>
                
                <div class="train-duration">
                    <div class="duration-time">${train.duration}</div>
                    <div class="duration-label">历时</div>
                </div>
                
                <div class="train-seats">
                    ${train.seats.map(seat => `
                        <div class="seat-info">
                            <div class="seat-type">${seat.type}</div>
                            <div class="seat-price">¥${seat.price}</div>
                            <div class="seat-status ${seat.status}">
                                ${this.getSeatStatusText(seat.status, seat.available)}
                            </div>
                        </div>
                    `).join('')}
                </div>
                
                <div class="train-actions">
                    <button class="btn btn-primary btn-book" 
                            data-train-number="${train.number}"
                            ${train.seats.every(s => s.status === 'sold-out') ? 'disabled' : ''}>
                        ${train.seats.every(s => s.status === 'sold-out') ? '已售完' : '预订'}
                    </button>
                </div>
            </div>
        `).join('');

        // 添加动画效果
        const items = container.querySelectorAll('.train-item');
        items.forEach((item, index) => {
            item.style.animationDelay = `${index * 0.1}s`;
        });
    }

    getTrainTypeName(type) {
        const names = {
            'G': '高速',
            'D': '动车',
            'C': '城际',
            'K': '快速',
            'T': '特快',
            'Z': '直达'
        };
        return names[type] || '普通';
    }

    getSeatStatusText(status, available) {
        switch (status) {
            case 'available':
                return `有票(${available})`;
            case 'limited':
                return `紧张(${available})`;
            case 'sold-out':
                return '无票';
            default:
                return '有票';
        }
    }

    showLoading() {
        const container = document.getElementById('trainList');
        if (!container) return;

        container.innerHTML = `
            <div class="loading-state">
                <div class="loading-spinner"></div>
                <div>正在查询车次信息...</div>
            </div>
        `;
    }

    showEmptyState() {
        const container = document.getElementById('trainList');
        if (!container) return;

        container.innerHTML = `
            <div class="empty-state">
                <span class="empty-icon">🚄</span>
                <div class="empty-text">暂无符合条件的车次</div>
                <div class="empty-desc">请尝试调整查询条件或筛选条件</div>
            </div>
        `;
    }

    bookTrain(trainNumber) {
        const train = this.filteredData.find(t => t.number === trainNumber);
        if (!train) return;

        // 保存选中的车次信息
        const searchParams = {
            departure: document.getElementById('departure')?.value,
            destination: document.getElementById('destination')?.value,
            date: document.getElementById('departureDate')?.value,
            trainNumber: trainNumber
        };

        Utils.setLocalStorage('selectedTrain', train);
        Utils.setLocalStorage('searchParams', searchParams);

        // 跳转到选座页面
        window.location.href = `seat-selection.html?train=${trainNumber}`;
    }

    loadMoreTrains() {
        // 模拟加载更多数据
        Utils.showMessage('已显示全部车次', 'info');
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    new TrainSearch();
});

// 导出类供其他模块使用
if (typeof module !== 'undefined' && module.exports) {
    module.exports = TrainSearch;
}