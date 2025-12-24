// 选座购票页面JavaScript

class SeatSelection {
    constructor() {
        this.selectedTrain = null;
        this.searchParams = null;
        this.selectedSeats = [];
        this.passengers = [];
        this.currentSeatType = null;
        this.seatMap = {};
        this.insuranceFee = 30; // 保险费用
        this.init();
    }

    init() {
        this.loadTrainInfo();
        this.bindEvents();
        this.generateSeatTypes();
        this.updateOrderSummary();
    }

    loadTrainInfo() {
        // 从本地存储获取选中的车次信息
        this.selectedTrain = Utils.getLocalStorage('selectedTrain');
        this.searchParams = Utils.getLocalStorage('searchParams');

        if (!this.selectedTrain || !this.searchParams) {
            Utils.showMessage('未找到车次信息，请重新选择', 'error');
            setTimeout(() => {
                window.location.href = 'train-ticket.html';
            }, 2000);
            return;
        }

        // 更新页面显示
        this.updateTrainDisplay();
    }

    updateTrainDisplay() {
        document.getElementById('trainNumber').textContent = this.selectedTrain.number;
        document.getElementById('departureStation').textContent = this.selectedTrain.departure.station;
        document.getElementById('departureTime').textContent = this.selectedTrain.departure.time;
        document.getElementById('arrivalStation').textContent = this.selectedTrain.arrival.station;
        document.getElementById('arrivalTime').textContent = this.selectedTrain.arrival.time;
        document.getElementById('trainDate').textContent = this.formatDate(this.selectedTrain.date);
    }

    formatDate(dateStr) {
        const date = new Date(dateStr);
        return date.toLocaleDateString('zh-CN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            weekday: 'long'
        });
    }

    bindEvents() {
        // 添加乘客按钮
        document.getElementById('addPassengerBtn').addEventListener('click', () => {
            this.showPassengerModal();
        });

        // 乘客模态框事件
        document.getElementById('closePassengerModal').addEventListener('click', () => {
            this.hidePassengerModal();
        });

        document.getElementById('cancelPassengerBtn').addEventListener('click', () => {
            this.hidePassengerModal();
        });

        document.getElementById('savePassengerBtn').addEventListener('click', () => {
            this.savePassenger();
        });

        // 确认订单按钮
        document.getElementById('confirmOrderBtn').addEventListener('click', () => {
            this.confirmOrder();
        });

        // 座位类型选择事件
        document.addEventListener('click', (e) => {
            if (e.target.closest('.seat-type-option')) {
                const option = e.target.closest('.seat-type-option');
                this.selectSeatType(option.dataset.seatType);
            }
        });

        // 座位选择事件
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('seat') && !e.target.classList.contains('occupied')) {
                this.toggleSeat(e.target);
            }
        });

        // 删除乘客事件
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('delete-passenger')) {
                const index = parseInt(e.target.dataset.index);
                this.removePassenger(index);
            }
        });

        // 模态框外部点击关闭
        document.getElementById('passengerModal').addEventListener('click', (e) => {
            if (e.target.id === 'passengerModal') {
                this.hidePassengerModal();
            }
        });
    }

    generateSeatTypes() {
        const container = document.getElementById('seatTypes');
        if (!container || !this.selectedTrain.seats) return;

        container.innerHTML = this.selectedTrain.seats.map(seat => `
            <div class="seat-type-option" data-seat-type="${seat.type}">
                <div class="seat-type-name">${seat.type}</div>
                <div class="seat-type-price">¥${seat.price}</div>
                <div class="seat-type-status ${seat.status}">
                    ${this.getSeatStatusText(seat.status, seat.available)}
                </div>
            </div>
        `).join('');

        // 默认选择第一个可用的座位类型
        const firstAvailable = this.selectedTrain.seats.find(seat => seat.status !== 'sold-out');
        if (firstAvailable) {
            this.selectSeatType(firstAvailable.type);
        }
    }

    getSeatStatusText(status, available) {
        switch (status) {
            case 'available':
                return `有票 ${available}张`;
            case 'limited':
                return `紧张 ${available}张`;
            case 'sold-out':
                return '无票';
            default:
                return '有票';
        }
    }

    selectSeatType(seatType) {
        // 更新选中状态
        document.querySelectorAll('.seat-type-option').forEach(option => {
            option.classList.remove('selected');
        });
        
        const selectedOption = document.querySelector(`[data-seat-type="${seatType}"]`);
        if (selectedOption) {
            selectedOption.classList.add('selected');
        }

        this.currentSeatType = seatType;
        this.generateSeatMap(seatType);
    }

    generateSeatMap(seatType) {
        const container = document.getElementById('seatMap');
        if (!container) return;

        // 生成座位图数据
        this.seatMap = this.createSeatMapData(seatType);
        
        container.innerHTML = this.seatMap.carriages.map((carriage, carriageIndex) => `
            <div class="carriage">
                <div class="carriage-header">${carriage.name}</div>
                <div class="seat-rows">
                    ${carriage.rows.map((row, rowIndex) => `
                        <div class="seat-row">
                            <div class="row-number">${row.number}</div>
                            ${this.renderSeatRow(row, carriageIndex, rowIndex)}
                        </div>
                    `).join('')}
                </div>
            </div>
        `).join('');
    }

    createSeatMapData(seatType) {
        const carriageCount = seatType === '商务座' ? 1 : (seatType === '一等座' ? 2 : 3);
        const rowsPerCarriage = seatType === '商务座' ? 8 : (seatType === '一等座' ? 13 : 18);
        const seatsPerRow = seatType === '商务座' ? 3 : (seatType === '一等座' ? 4 : 5);
        
        const carriages = [];
        
        for (let c = 0; c < carriageCount; c++) {
            const carriage = {
                name: `${c + 1}车厢`,
                rows: []
            };
            
            for (let r = 0; r < rowsPerCarriage; r++) {
                const row = {
                    number: r + 1,
                    seats: []
                };
                
                for (let s = 0; s < seatsPerRow; s++) {
                    const seatNumber = `${c + 1}车${(r + 1).toString().padStart(2, '0')}${String.fromCharCode(65 + s)}`;
                    const isOccupied = Math.random() < 0.3; // 30%的座位被占用
                    
                    row.seats.push({
                        number: seatNumber,
                        label: String.fromCharCode(65 + s),
                        status: isOccupied ? 'occupied' : 'available',
                        selected: false
                    });
                }
                
                carriage.rows.push(row);
            }
            
            carriages.push(carriage);
        }
        
        return { carriages };
    }

    renderSeatRow(row, carriageIndex, rowIndex) {
        const seatType = this.currentSeatType;
        let html = '';
        
        if (seatType === '商务座') {
            // 商务座：A C F (1+2布局)
            html += '<div class="seat-group">';
            html += this.renderSeat(row.seats[0], carriageIndex, rowIndex, 0);
            html += '</div>';
            html += '<div class="aisle"></div>';
            html += '<div class="seat-group">';
            html += this.renderSeat(row.seats[1], carriageIndex, rowIndex, 1);
            html += this.renderSeat(row.seats[2], carriageIndex, rowIndex, 2);
            html += '</div>';
        } else if (seatType === '一等座') {
            // 一等座：A C D F (2+2布局)
            html += '<div class="seat-group">';
            html += this.renderSeat(row.seats[0], carriageIndex, rowIndex, 0);
            html += this.renderSeat(row.seats[1], carriageIndex, rowIndex, 1);
            html += '</div>';
            html += '<div class="aisle"></div>';
            html += '<div class="seat-group">';
            html += this.renderSeat(row.seats[2], carriageIndex, rowIndex, 2);
            html += this.renderSeat(row.seats[3], carriageIndex, rowIndex, 3);
            html += '</div>';
        } else {
            // 二等座：A B C D F (3+2布局)
            html += '<div class="seat-group">';
            html += this.renderSeat(row.seats[0], carriageIndex, rowIndex, 0);
            html += this.renderSeat(row.seats[1], carriageIndex, rowIndex, 1);
            html += this.renderSeat(row.seats[2], carriageIndex, rowIndex, 2);
            html += '</div>';
            html += '<div class="aisle"></div>';
            html += '<div class="seat-group">';
            html += this.renderSeat(row.seats[3], carriageIndex, rowIndex, 3);
            html += this.renderSeat(row.seats[4], carriageIndex, rowIndex, 4);
            html += '</div>';
        }
        
        return html;
    }

    renderSeat(seat, carriageIndex, rowIndex, seatIndex) {
        const classes = ['seat', seat.status];
        if (seat.selected) classes.push('selected');
        
        return `
            <div class="${classes.join(' ')}" 
                 data-seat-number="${seat.number}"
                 data-carriage="${carriageIndex}"
                 data-row="${rowIndex}"
                 data-seat="${seatIndex}">
                ${seat.label}
            </div>
        `;
    }

    toggleSeat(seatElement) {
        const seatNumber = seatElement.dataset.seatNumber;
        const carriageIndex = parseInt(seatElement.dataset.carriage);
        const rowIndex = parseInt(seatElement.dataset.row);
        const seatIndex = parseInt(seatElement.dataset.seat);
        
        const seat = this.seatMap.carriages[carriageIndex].rows[rowIndex].seats[seatIndex];
        
        if (seat.selected) {
            // 取消选择
            seat.selected = false;
            seatElement.classList.remove('selected');
            this.selectedSeats = this.selectedSeats.filter(s => s.number !== seatNumber);
        } else {
            // 检查是否超过乘客数量
            if (this.selectedSeats.length >= this.passengers.length && this.passengers.length > 0) {
                Utils.showMessage('选择的座位数量不能超过乘客数量', 'warning');
                return;
            }
            
            // 选择座位
            seat.selected = true;
            seatElement.classList.add('selected');
            
            const seatInfo = {
                number: seatNumber,
                type: this.currentSeatType,
                price: this.getSeatPrice(this.currentSeatType),
                carriage: carriageIndex + 1,
                row: rowIndex + 1,
                seat: seat.label
            };
            
            this.selectedSeats.push(seatInfo);
        }
        
        this.updateOrderSummary();
        this.updateConfirmButton();
    }

    getSeatPrice(seatType) {
        const seat = this.selectedTrain.seats.find(s => s.type === seatType);
        return seat ? seat.price : 0;
    }

    showPassengerModal() {
        document.getElementById('passengerModal').classList.add('show');
        this.resetPassengerForm();
    }

    hidePassengerModal() {
        document.getElementById('passengerModal').classList.remove('show');
    }

    resetPassengerForm() {
        document.getElementById('passengerForm').reset();
    }

    savePassenger() {
        const form = document.getElementById('passengerForm');
        const formData = new FormData(form);
        
        const passenger = {
            id: Date.now(),
            name: document.getElementById('passengerName').value,
            idType: document.getElementById('passengerIdType').value,
            idNumber: document.getElementById('passengerIdNumber').value,
            phone: document.getElementById('passengerPhone').value,
            type: document.getElementById('passengerType').value,
            needInsurance: document.getElementById('needInsurance').checked
        };
        
        // 验证表单
        if (!this.validatePassenger(passenger)) {
            return;
        }
        
        this.passengers.push(passenger);
        this.renderPassengerList();
        this.hidePassengerModal();
        this.updateOrderSummary();
        this.updateConfirmButton();
        
        Utils.showMessage('乘客信息添加成功', 'success');
    }

    validatePassenger(passenger) {
        if (!passenger.name.trim()) {
            Utils.showMessage('请输入乘客姓名', 'warning');
            return false;
        }
        
        if (!passenger.idType) {
            Utils.showMessage('请选择证件类型', 'warning');
            return false;
        }
        
        if (!passenger.idNumber.trim()) {
            Utils.showMessage('请输入证件号码', 'warning');
            return false;
        }
        
        if (!passenger.phone.trim()) {
            Utils.showMessage('请输入手机号码', 'warning');
            return false;
        }
        
        // 验证手机号格式
        const phoneRegex = /^1[3-9]\d{9}$/;
        if (!phoneRegex.test(passenger.phone)) {
            Utils.showMessage('请输入正确的手机号码', 'warning');
            return false;
        }
        
        // 验证身份证号格式（简单验证）
        if (passenger.idType === '身份证') {
            const idRegex = /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/;
            if (!idRegex.test(passenger.idNumber)) {
                Utils.showMessage('请输入正确的身份证号码', 'warning');
                return false;
            }
        }
        
        return true;
    }

    renderPassengerList() {
        const container = document.getElementById('passengerList');
        if (!container) return;
        
        if (this.passengers.length === 0) {
            container.innerHTML = '<div class="empty-passengers">暂无乘客信息，请点击上方按钮添加乘客</div>';
            return;
        }
        
        container.innerHTML = this.passengers.map((passenger, index) => `
            <div class="passenger-item">
                <div class="passenger-header">
                    <div class="passenger-title">乘客${index + 1}</div>
                    <div class="passenger-actions">
                        <button class="btn-icon delete delete-passenger" data-index="${index}" title="删除乘客">
                            ✕
                        </button>
                    </div>
                </div>
                <div class="passenger-form">
                    <div class="form-group">
                        <label>姓名</label>
                        <input type="text" class="form-control" value="${passenger.name}" readonly>
                    </div>
                    <div class="form-group">
                        <label>证件类型</label>
                        <input type="text" class="form-control" value="${passenger.idType}" readonly>
                    </div>
                    <div class="form-group">
                        <label>证件号码</label>
                        <input type="text" class="form-control" value="${passenger.idNumber}" readonly>
                    </div>
                    <div class="form-group">
                        <label>手机号码</label>
                        <input type="text" class="form-control" value="${passenger.phone}" readonly>
                    </div>
                </div>
                ${passenger.needInsurance ? '<div class="insurance-note">已选择旅行意外险</div>' : ''}
            </div>
        `).join('');
    }

    removePassenger(index) {
        if (confirm('确定要删除这个乘客信息吗？')) {
            this.passengers.splice(index, 1);
            this.renderPassengerList();
            this.updateOrderSummary();
            this.updateConfirmButton();
            
            // 如果选择的座位数量超过乘客数量，清除多余的座位选择
            if (this.selectedSeats.length > this.passengers.length) {
                const excessSeats = this.selectedSeats.splice(this.passengers.length);
                excessSeats.forEach(seat => {
                    const seatElement = document.querySelector(`[data-seat-number="${seat.number}"]`);
                    if (seatElement) {
                        seatElement.classList.remove('selected');
                        // 更新座位图数据
                        const carriageIndex = parseInt(seatElement.dataset.carriage);
                        const rowIndex = parseInt(seatElement.dataset.row);
                        const seatIndex = parseInt(seatElement.dataset.seat);
                        this.seatMap.carriages[carriageIndex].rows[rowIndex].seats[seatIndex].selected = false;
                    }
                });
            }
            
            Utils.showMessage('乘客信息删除成功', 'success');
        }
    }

    updateOrderSummary() {
        // 更新选中座位信息
        const selectedSeatsText = this.selectedSeats.length > 0 
            ? this.selectedSeats.map(seat => `${seat.number}(${seat.type})`).join('、')
            : '暂未选择';
        document.getElementById('selectedSeatsText').textContent = selectedSeatsText;
        
        // 计算费用
        const ticketSubtotal = this.selectedSeats.reduce((total, seat) => total + seat.price, 0);
        const insuranceTotal = this.passengers.filter(p => p.needInsurance).length * this.insuranceFee;
        const totalAmount = ticketSubtotal + insuranceTotal;
        
        document.getElementById('ticketSubtotal').textContent = `¥${ticketSubtotal}`;
        document.getElementById('insuranceFee').textContent = `¥${insuranceTotal}`;
        document.getElementById('totalAmount').textContent = `¥${totalAmount}`;
    }

    updateConfirmButton() {
        const confirmBtn = document.getElementById('confirmOrderBtn');
        const canConfirm = this.passengers.length > 0 && 
                          this.selectedSeats.length > 0 && 
                          this.selectedSeats.length === this.passengers.length;
        
        confirmBtn.disabled = !canConfirm;
        
        if (canConfirm) {
            confirmBtn.textContent = '确认订单';
        } else if (this.passengers.length === 0) {
            confirmBtn.textContent = '请添加乘客信息';
        } else if (this.selectedSeats.length === 0) {
            confirmBtn.textContent = '请选择座位';
        } else if (this.selectedSeats.length !== this.passengers.length) {
            confirmBtn.textContent = '座位数量与乘客数量不匹配';
        }
    }

    confirmOrder() {
        if (this.passengers.length === 0) {
            Utils.showMessage('请先添加乘客信息', 'warning');
            return;
        }
        
        if (this.selectedSeats.length === 0) {
            Utils.showMessage('请先选择座位', 'warning');
            return;
        }
        
        if (this.selectedSeats.length !== this.passengers.length) {
            Utils.showMessage('选择的座位数量必须与乘客数量一致', 'warning');
            return;
        }
        
        // 生成订单信息
        const order = {
            id: 'ORDER' + Date.now(),
            train: this.selectedTrain,
            searchParams: this.searchParams,
            passengers: this.passengers,
            seats: this.selectedSeats,
            pricing: {
                ticketSubtotal: this.selectedSeats.reduce((total, seat) => total + seat.price, 0),
                insuranceFee: this.passengers.filter(p => p.needInsurance).length * this.insuranceFee,
                totalAmount: this.selectedSeats.reduce((total, seat) => total + seat.price, 0) + 
                           this.passengers.filter(p => p.needInsurance).length * this.insuranceFee
            },
            createTime: new Date().toISOString()
        };
        
        // 保存订单信息
        Utils.setLocalStorage('currentOrder', order);
        
        // 跳转到支付页面
        window.location.href = 'mall-payment.html';
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    new SeatSelection();
});

// 导出类供其他模块使用
if (typeof module !== 'undefined' && module.exports) {
    module.exports = SeatSelection;
}