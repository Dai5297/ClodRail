// 购票页面JavaScript功能

// 购票页面应用对象
const TicketApp = {
    data: {
        ticketInfo: {},
        selectedSeatType: 'second', // business, first, second
        selectedSeats: [], // 选中的座位
        passengers: [], // 乘车人列表
        selectedPassengers: [], // 选中的乘车人
        seatMap: [], // 座位图数据
        currentCarriage: 1, // 当前车厢
        orderSummary: {
            totalPrice: 0,
            ticketCount: 0
        }
    },
    
    init() {
        this.loadTicketInfo();
        this.loadPassengers();
        this.updateTrainInfo();
        this.generateSeatMap();
        this.bindEvents();
        this.updateOrderSummary();
    },
    
    // 加载购票信息
    loadTicketInfo() {
        this.data.ticketInfo = Storage.get('ticket_info', {
            from: '北京',
            to: '上海',
            departDate: Utils.formatDate(new Date()),
            train: {
                number: 'G1',
                type: 'G',
                departTime: '08:00',
                arriveTime: '12:28',
                duration: 268,
                seats: {
                    business: { price: 1748, available: 9 },
                    first: { price: 933, available: 15 },
                    second: { price: 553, available: 99 }
                }
            }
        });
    },
    
    // 加载乘车人信息
    loadPassengers() {
        this.data.passengers = Storage.get('passengers', [
            {
                id: '1',
                name: '张三',
                idCard: '110101199001011234',
                phone: '13800138000',
                type: 'adult'
            },
            {
                id: '2',
                name: '李四',
                idCard: '110101199501011234',
                phone: '13900139000',
                type: 'adult'
            }
        ]);
    },
    
    // 更新列车信息显示
    updateTrainInfo() {
        const { train, from, to, departDate } = this.data.ticketInfo;
        
        // 更新列车号
        const trainNumberElement = document.querySelector('.train-number');
        if (trainNumberElement) {
            trainNumberElement.textContent = train.number;
        }
        
        // 更新列车类型
        const trainTypeElement = document.querySelector('.train-type');
        if (trainTypeElement) {
            trainTypeElement.textContent = this.getTrainTypeName(train.type);
        }
        
        // 更新时刻表
        const departTimeElement = document.querySelector('.depart-time');
        const arriveTimeElement = document.querySelector('.arrive-time');
        const departStationElement = document.querySelector('.depart-station');
        const arriveStationElement = document.querySelector('.arrive-station');
        
        if (departTimeElement) departTimeElement.textContent = train.departTime;
        if (arriveTimeElement) arriveTimeElement.textContent = train.arriveTime;
        if (departStationElement) departStationElement.textContent = from;
        if (arriveStationElement) arriveStationElement.textContent = to;
        
        // 更新日期
        const dateElement = document.querySelector('.travel-date');
        if (dateElement) {
            const date = new Date(departDate);
            dateElement.textContent = `${Utils.formatDate(date, 'MM月DD日')} ${Utils.getWeekDay(date)}`;
        }
        
        // 更新座位价格信息
        this.updateSeatPrices();
    },
    
    // 更新座位价格信息
    updateSeatPrices() {
        const { seats } = this.data.ticketInfo.train;
        
        Object.keys(seats).forEach(seatType => {
            const priceElement = document.querySelector(`.seat-option[data-seat="${seatType}"] .seat-price`);
            const countElement = document.querySelector(`.seat-option[data-seat="${seatType}"] .seat-count`);
            
            if (priceElement) {
                priceElement.textContent = `¥${seats[seatType].price}`;
            }
            
            if (countElement) {
                const available = seats[seatType].available;
                countElement.textContent = available > 0 ? `余${available}张` : '无票';
                
                // 更新座位选项状态
                const seatOption = document.querySelector(`.seat-option[data-seat="${seatType}"]`);
                if (seatOption) {
                    seatOption.classList.toggle('disabled', available === 0);
                }
            }
        });
    },
    
    // 获取车次类型名称
    getTrainTypeName(type) {
        const typeNames = {
            'G': '高速动车',
            'D': '动车组',
            'C': '城际列车',
            'K': '快速列车',
            'T': '特快列车',
            'Z': '直达列车'
        };
        return typeNames[type] || type;
    },
    
    // 生成座位图
    generateSeatMap() {
        const rows = 3; // 显示3排座位
        const seatsPerRow = this.data.selectedSeatType === 'business' ? 3 : 
                           this.data.selectedSeatType === 'first' ? 4 : 5;
        
        this.data.seatMap = [];
        
        for (let row = 1; row <= rows; row++) {
            const rowSeats = [];
            for (let seat = 1; seat <= seatsPerRow; seat++) {
                const seatNumber = `${this.data.currentCarriage}车${row}排${this.getSeatLetter(seat, seatsPerRow)}座`;
                const isOccupied = Math.random() < 0.3; // 30%概率已被占用
                const isSelected = this.data.selectedSeats.includes(seatNumber);
                
                rowSeats.push({
                    number: seatNumber,
                    row,
                    seat,
                    isOccupied,
                    isSelected,
                    isAvailable: !isOccupied
                });
            }
            this.data.seatMap.push(rowSeats);
        }
        
        this.renderSeatMap();
    },
    
    // 获取座位字母
    getSeatLetter(seatIndex, seatsPerRow) {
        if (seatsPerRow === 3) {
            return ['A', 'C', 'F'][seatIndex - 1];
        } else if (seatsPerRow === 4) {
            return ['A', 'C', 'D', 'F'][seatIndex - 1];
        } else {
            return ['A', 'B', 'C', 'D', 'F'][seatIndex - 1];
        }
    },
    
    // 渲染座位图
    renderSeatMap() {
        const seatMapElement = document.querySelector('.seat-map');
        if (!seatMapElement) return;
        
        seatMapElement.innerHTML = `
            <div class="seat-map-header">
                <div class="carriage-info">${this.data.currentCarriage}车厢 ${this.getSeatTypeName(this.data.selectedSeatType)}</div>
                <div class="seat-legend">
                    <div class="legend-item">
                        <div class="legend-seat available"></div>
                        <span>可选</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-seat occupied"></div>
                        <span>已售</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-seat selected"></div>
                        <span>已选</span>
                    </div>
                </div>
            </div>
            <div class="seat-rows">
                ${this.data.seatMap.map(row => `
                    <div class="seat-row">
                        <div class="row-number">${row[0].row}排</div>
                        <div class="seats">
                            ${row.map(seat => `
                                <div class="seat ${seat.isOccupied ? 'occupied' : ''} ${seat.isSelected ? 'selected' : ''}" 
                                     data-seat="${seat.number}" 
                                     ${seat.isAvailable ? '' : 'disabled'}>
                                    ${this.getSeatLetter(seat.seat, row.length)}
                                </div>
                            `).join('')}
                        </div>
                    </div>
                `).join('')}
            </div>
        `;
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
    
    // 绑定事件
    bindEvents() {
        // 返回按钮
        const backBtn = document.querySelector('.back-btn');
        if (backBtn) {
            backBtn.addEventListener('click', () => {
                window.history.back();
            });
        }
        
        // 座位类型选择
        document.querySelectorAll('.seat-option').forEach(option => {
            option.addEventListener('click', (e) => {
                if (e.currentTarget.classList.contains('disabled')) return;
                
                const seatType = e.currentTarget.getAttribute('data-seat');
                this.selectSeatType(seatType);
            });
        });
        
        // 座位选择
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('seat') && !e.target.hasAttribute('disabled')) {
                const seatNumber = e.target.getAttribute('data-seat');
                this.toggleSeat(seatNumber);
            }
        });
        
        // 乘车人选择
        document.querySelectorAll('.passenger-item').forEach(item => {
            item.addEventListener('click', (e) => {
                const passengerId = e.currentTarget.getAttribute('data-passenger');
                this.togglePassenger(passengerId);
            });
        });
        
        // 添加乘车人
        const addPassengerBtn = document.querySelector('.add-passenger-btn');
        if (addPassengerBtn) {
            addPassengerBtn.addEventListener('click', () => {
                Modal.show('.passenger-modal');
            });
        }
        
        // 提交订单
        const submitBtn = document.querySelector('.submit-order-btn');
        if (submitBtn) {
            submitBtn.addEventListener('click', () => {
                this.submitOrder();
            });
        }
        
        // 乘车人弹窗事件
        this.bindPassengerModalEvents();
        
        // 支付弹窗事件
        this.bindPaymentModalEvents();
    },
    
    // 绑定乘车人弹窗事件
    bindPassengerModalEvents() {
        // 关闭按钮
        const closeBtn = document.querySelector('.passenger-modal .close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                Modal.hide('.passenger-modal');
            });
        }
        
        // 保存乘车人
        const saveBtn = document.querySelector('.save-passenger-btn');
        if (saveBtn) {
            saveBtn.addEventListener('click', () => {
                this.savePassenger();
            });
        }
        
        // 表单验证
        const form = document.querySelector('.passenger-form');
        if (form) {
            form.addEventListener('submit', (e) => {
                e.preventDefault();
                this.savePassenger();
            });
        }
    },
    
    // 绑定支付弹窗事件
    bindPaymentModalEvents() {
        // 关闭按钮
        const closeBtn = document.querySelector('.mall-payment-modal .close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                Modal.hide('.mall-payment-modal');
            });
        }
        
        // 支付方式选择
        document.querySelectorAll('.mall-payment-option').forEach(option => {
            option.addEventListener('click', (e) => {
                document.querySelectorAll('.mall-payment-option').forEach(opt => {
                    opt.classList.remove('selected');
                });
                e.currentTarget.classList.add('selected');
            });
        });
        
        // 确认支付
        const confirmPayBtn = document.querySelector('.confirm-pay-btn');
        if (confirmPayBtn) {
            confirmPayBtn.addEventListener('click', () => {
                this.processPayment();
            });
        }
    },
    
    // 选择座位类型
    selectSeatType(seatType) {
        this.data.selectedSeatType = seatType;
        this.data.selectedSeats = []; // 清空已选座位
        
        // 更新UI状态
        document.querySelectorAll('.seat-option').forEach(option => {
            option.classList.toggle('active', option.getAttribute('data-seat') === seatType);
        });
        
        // 重新生成座位图
        this.generateSeatMap();
        this.updateOrderSummary();
    },
    
    // 切换座位选择
    toggleSeat(seatNumber) {
        const index = this.data.selectedSeats.indexOf(seatNumber);
        
        if (index > -1) {
            // 取消选择
            this.data.selectedSeats.splice(index, 1);
        } else {
            // 检查是否超过乘车人数量
            if (this.data.selectedSeats.length >= this.data.selectedPassengers.length && this.data.selectedPassengers.length > 0) {
                Toast.error('选择座位数量不能超过乘车人数量');
                return;
            }
            
            // 添加选择
            this.data.selectedSeats.push(seatNumber);
        }
        
        // 重新渲染座位图
        this.generateSeatMap();
        this.updateOrderSummary();
    },
    
    // 切换乘车人选择
    togglePassenger(passengerId) {
        const index = this.data.selectedPassengers.indexOf(passengerId);
        
        if (index > -1) {
            // 取消选择
            this.data.selectedPassengers.splice(index, 1);
        } else {
            // 添加选择
            this.data.selectedPassengers.push(passengerId);
        }
        
        // 更新UI状态
        document.querySelectorAll('.passenger-item').forEach(item => {
            const id = item.getAttribute('data-passenger');
            item.classList.toggle('selected', this.data.selectedPassengers.includes(id));
        });
        
        // 如果座位数量超过乘车人数量，清空多余座位
        if (this.data.selectedSeats.length > this.data.selectedPassengers.length) {
            this.data.selectedSeats = this.data.selectedSeats.slice(0, this.data.selectedPassengers.length);
            this.generateSeatMap();
        }
        
        this.updateOrderSummary();
    },
    
    // 保存乘车人
    savePassenger() {
        const form = document.querySelector('.passenger-form');
        const formData = new FormData(form);
        
        const passengerData = {
            name: formData.get('name'),
            idCard: formData.get('idCard'),
            phone: formData.get('phone'),
            type: formData.get('type') || 'adult'
        };
        
        // 验证表单
        const validation = Validator.validateForm(passengerData, {
            name: [
                { type: 'required', message: '请输入姓名' },
                { type: 'minLength', length: 2, message: '姓名至少2个字符' }
            ],
            idCard: [
                { type: 'required', message: '请输入身份证号' },
                { type: 'idCard', message: '身份证号格式不正确' }
            ],
            phone: [
                { type: 'required', message: '请输入手机号' },
                { type: 'phone', message: '手机号格式不正确' }
            ]
        });
        
        if (!validation.isValid) {
            const firstError = Object.values(validation.errors)[0];
            Toast.error(firstError);
            return;
        }
        
        // 添加到乘车人列表
        const newPassenger = {
            id: Utils.generateId(),
            ...passengerData
        };
        
        this.data.passengers.push(newPassenger);
        Storage.set('passengers', this.data.passengers);
        
        // 更新UI
        this.renderPassengerList();
        
        // 关闭弹窗
        Modal.hide('.passenger-modal');
        
        // 清空表单
        form.reset();
        
        Toast.success('乘车人添加成功');
    },
    
    // 渲染乘车人列表
    renderPassengerList() {
        const passengerList = document.querySelector('.passenger-list');
        if (!passengerList) return;
        
        passengerList.innerHTML = this.data.passengers.map(passenger => `
            <div class="passenger-item ${this.data.selectedPassengers.includes(passenger.id) ? 'selected' : ''}" 
                 data-passenger="${passenger.id}">
                <div class="passenger-info">
                    <div class="passenger-name">${passenger.name}</div>
                    <div class="passenger-id">${passenger.idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2')}</div>
                </div>
                <div class="passenger-type">${passenger.type === 'adult' ? '成人' : '儿童'}</div>
            </div>
        `).join('');
        
        // 重新绑定点击事件
        document.querySelectorAll('.passenger-item').forEach(item => {
            item.addEventListener('click', (e) => {
                const passengerId = e.currentTarget.getAttribute('data-passenger');
                this.togglePassenger(passengerId);
            });
        });
    },
    
    // 更新订单汇总
    updateOrderSummary() {
        const ticketCount = this.data.selectedPassengers.length;
        const seatPrice = this.data.ticketInfo.train.seats[this.data.selectedSeatType]?.price || 0;
        const totalPrice = ticketCount * seatPrice;
        
        this.data.orderSummary = {
            ticketCount,
            totalPrice
        };
        
        // 更新UI显示
        const ticketCountElement = document.querySelector('.ticket-count');
        const totalPriceElement = document.querySelector('.total-price');
        const submitBtn = document.querySelector('.submit-order-btn');
        
        if (ticketCountElement) {
            ticketCountElement.textContent = `${ticketCount}张票`;
        }
        
        if (totalPriceElement) {
            totalPriceElement.textContent = `¥${totalPrice}`;
        }
        
        if (submitBtn) {
            submitBtn.disabled = ticketCount === 0 || this.data.selectedSeats.length !== ticketCount;
            submitBtn.textContent = ticketCount === 0 ? '请选择乘车人' : 
                                   this.data.selectedSeats.length !== ticketCount ? '请选择座位' : 
                                   `提交订单 ¥${totalPrice}`;
        }
    },
    
    // 提交订单
    submitOrder() {
        // 验证订单信息
        if (this.data.selectedPassengers.length === 0) {
            Toast.error('请选择乘车人');
            return;
        }
        
        if (this.data.selectedSeats.length !== this.data.selectedPassengers.length) {
            Toast.error('座位数量与乘车人数量不匹配');
            return;
        }
        
        // 显示支付弹窗
        this.showPaymentModal();
    },
    
    // 显示支付弹窗
    showPaymentModal() {
        const modal = document.querySelector('.mall-payment-modal');
        if (!modal) return;
        
        // 更新支付信息
        const orderInfoElement = modal.querySelector('.order-info');
        if (orderInfoElement) {
            const { train, from, to, departDate } = this.data.ticketInfo;
            const selectedPassengerNames = this.data.selectedPassengers
                .map(id => this.data.passengers.find(p => p.id === id)?.name)
                .filter(Boolean)
                .join('、');
            
            orderInfoElement.innerHTML = `
                <div class="info-item">
                    <span class="label">车次：</span>
                    <span class="value">${train.number}</span>
                </div>
                <div class="info-item">
                    <span class="label">行程：</span>
                    <span class="value">${from} → ${to}</span>
                </div>
                <div class="info-item">
                    <span class="label">日期：</span>
                    <span class="value">${Utils.formatDate(new Date(departDate), 'YYYY年MM月DD日')}</span>
                </div>
                <div class="info-item">
                    <span class="label">座位：</span>
                    <span class="value">${this.getSeatTypeName(this.data.selectedSeatType)} ${this.data.selectedSeats.join('、')}</span>
                </div>
                <div class="info-item">
                    <span class="label">乘车人：</span>
                    <span class="value">${selectedPassengerNames}</span>
                </div>
                <div class="info-item total">
                    <span class="label">总价：</span>
                    <span class="value price">¥${this.data.orderSummary.totalPrice}</span>
                </div>
            `;
        }
        
        Modal.show('.mall-payment-modal');
    },
    
    // 处理支付
    async processPayment() {
        const selectedPayment = document.querySelector('.mall-payment-option.selected');
        if (!selectedPayment) {
            Toast.error('请选择支付方式');
            return;
        }
        
        const paymentMethod = selectedPayment.getAttribute('data-mall-payment');
        
        Loading.show('正在处理支付...');
        
        try {
            // 模拟支付处理
            await new Promise(resolve => setTimeout(resolve, 2000));
            
            // 创建订单
            const order = {
                id: Utils.generateId(),
                trainNumber: this.data.ticketInfo.train.number,
                from: this.data.ticketInfo.from,
                to: this.data.ticketInfo.to,
                departDate: this.data.ticketInfo.departDate,
                departTime: this.data.ticketInfo.train.departTime,
                arriveTime: this.data.ticketInfo.train.arriveTime,
                seatType: this.data.selectedSeatType,
                seats: this.data.selectedSeats,
                passengers: this.data.selectedPassengers.map(id => 
                    this.data.passengers.find(p => p.id === id)
                ),
                totalPrice: this.data.orderSummary.totalPrice,
                status: 'paid',
                paymentMethod,
                createTime: new Date().toISOString(),
                payTime: new Date().toISOString()
            };
            
            // 保存订单
            const orders = Storage.get('orders', []);
            orders.unshift(order);
            Storage.set('orders', orders);
            
            Loading.hide();
            Modal.hide('.mall-payment-modal');
            
            Toast.success('支付成功！');
            
            // 跳转到订单页面
            setTimeout(() => {
                window.location.href = 'order.html';
            }, 1500);
            
        } catch (error) {
            Loading.hide();
            Toast.error('支付失败，请重试');
        }
    }
};

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    TicketApp.init();
});

// 导出到全局
window.TicketApp = TicketApp;