// 支付页面JavaScript

class Payment {
    constructor() {
        this.currentOrder = null;
        this.selectedPaymentMethod = 'alipay';
        this.selectedCoupon = null;
        this.serviceFee = 5;
        this.countdownTimer = null;
        this.remainingTime = 15 * 60; // 15分钟倒计时
        this.availablePoints = 1200; // 模拟用户积分
        this.init();
    }

    init() {
        this.loadOrderInfo();
        this.bindEvents();
        this.generateAvailableCoupons();
        this.updatePaymentSummary();
        this.startCountdown();
        this.updateAvailablePoints();
    }

    loadOrderInfo() {
        // 从本地存储获取订单信息
        this.currentOrder = Utils.getLocalStorage('currentOrder');

        if (!this.currentOrder) {
            Utils.showMessage('未找到订单信息，请重新下单', 'error');
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 2000);
            return;
        }

        this.displayOrderInfo();
    }

    displayOrderInfo() {
        // 显示车次信息
        document.getElementById('trainNumber').textContent = this.currentOrder.train.number;
        document.getElementById('departureStation').textContent = this.currentOrder.train.departure.station;
        document.getElementById('departureTime').textContent = this.currentOrder.train.departure.time;
        document.getElementById('arrivalStation').textContent = this.currentOrder.train.arrival.station;
        document.getElementById('arrivalTime').textContent = this.currentOrder.train.arrival.time;
        
        const departureDate = new Date(this.currentOrder.train.date);
        document.getElementById('departureDate').textContent = this.formatShortDate(departureDate);
        document.getElementById('arrivalDate').textContent = this.formatShortDate(departureDate);
        document.getElementById('trainDuration').textContent = this.currentOrder.train.duration;

        // 显示乘客信息
        this.displayPassengers();
        
        // 显示座位信息
        this.displaySeats();
        
        // 显示费用信息
        this.displayPricing();
    }

    formatShortDate(date) {
        return `${date.getMonth() + 1}月${date.getDate()}日`;
    }

    displayPassengers() {
        const container = document.getElementById('passengerList');
        if (!container || !this.currentOrder.passengers) return;

        container.innerHTML = this.currentOrder.passengers.map((passenger, index) => `
            <div class="passenger-item">
                <div class="passenger-info">
                    <div class="passenger-name">${passenger.name}</div>
                    <div class="passenger-details">
                        ${passenger.idType} ${this.maskIdNumber(passenger.idNumber)} | ${passenger.phone}
                    </div>
                </div>
                <div class="passenger-type">${passenger.type}</div>
            </div>
        `).join('');
    }

    maskIdNumber(idNumber) {
        if (idNumber.length <= 8) return idNumber;
        return idNumber.substring(0, 4) + '****' + idNumber.substring(idNumber.length - 4);
    }

    displaySeats() {
        const container = document.getElementById('seatList');
        if (!container || !this.currentOrder.seats) return;

        container.innerHTML = this.currentOrder.seats.map(seat => `
            <div class="seat-item">
                <div class="seat-info">
                    <div class="seat-number">${seat.number}</div>
                    <div class="seat-details">${seat.type} | ${seat.carriage}车厢${seat.row}排${seat.seat}座</div>
                </div>
                <div class="seat-price">¥${seat.price}</div>
            </div>
        `).join('');
    }

    displayPricing() {
        const pricing = this.currentOrder.pricing;
        document.getElementById('ticketFee').textContent = `¥${pricing.ticketSubtotal}`;
        document.getElementById('insuranceFee').textContent = `¥${pricing.insuranceFee}`;
        document.getElementById('serviceFee').textContent = `¥${this.serviceFee}`;
        
        const totalAmount = pricing.ticketSubtotal + pricing.insuranceFee + this.serviceFee;
        document.getElementById('totalAmount').textContent = `¥${totalAmount}`;
        document.getElementById('finalAmount').textContent = `¥${totalAmount}`;
    }

    bindEvents() {
        // 支付方式选择
        document.querySelectorAll('input[name="paymentMethod"]').forEach(radio => {
            radio.addEventListener('change', (e) => {
                this.selectPaymentMethod(e.target.value);
            });
        });

        // 支付方式卡片点击
        document.querySelectorAll('.mall-payment-method').forEach(method => {
            method.addEventListener('click', () => {
                const radio = method.querySelector('input[type="radio"]');
                if (radio) {
                    radio.checked = true;
                    this.selectPaymentMethod(radio.value);
                }
            });
        });

        // 优惠券相关
        document.getElementById('applyCouponBtn').addEventListener('click', () => {
            this.applyCoupon();
        });

        // 支付按钮
        document.getElementById('payBtn').addEventListener('click', () => {
            this.initiatePayment();
        });

        // 支付模态框事件
        document.getElementById('closePaymentModal').addEventListener('click', () => {
            this.hidePaymentModal();
        });

        document.getElementById('cancelPaymentBtn').addEventListener('click', () => {
            this.hidePaymentModal();
        });

        document.getElementById('confirmPaymentBtn').addEventListener('click', () => {
            this.processPayment();
        });

        // 优惠券选择事件
        document.addEventListener('click', (e) => {
            if (e.target.closest('.coupon-item')) {
                const couponItem = e.target.closest('.coupon-item');
                this.selectCoupon(couponItem);
            }
        });

        // 模态框外部点击关闭
        document.getElementById('paymentModal').addEventListener('click', (e) => {
            if (e.target.id === 'paymentModal') {
                this.hidePaymentModal();
            }
        });
    }

    selectPaymentMethod(method) {
        this.selectedPaymentMethod = method;
        
        // 更新选中状态
        document.querySelectorAll('.mall-payment-method').forEach(item => {
            item.classList.remove('selected');
        });
        
        const selectedMethod = document.querySelector(`[data-method="${method}"]`);
        if (selectedMethod) {
            selectedMethod.classList.add('selected');
        }
        
        // 检查积分支付是否可用
        if (method === 'points') {
            const totalAmount = this.calculateTotalAmount();
            const requiredPoints = totalAmount * 100; // 1元 = 100积分
            
            if (this.availablePoints < requiredPoints) {
                Utils.showMessage(`积分不足，需要${requiredPoints}积分，当前仅有${this.availablePoints}积分`, 'warning');
                // 重新选择支付宝
                document.getElementById('alipay').checked = true;
                this.selectPaymentMethod('alipay');
                return;
            }
        }
    }

    updateAvailablePoints() {
        document.getElementById('availablePoints').textContent = this.availablePoints;
    }

    generateAvailableCoupons() {
        const container = document.getElementById('availableCoupons');
        if (!container) return;

        const coupons = [
            {
                id: 'COUPON001',
                name: '新用户专享券',
                desc: '满100减20',
                value: 20,
                minAmount: 100
            },
            {
                id: 'COUPON002',
                name: '高铁出行券',
                desc: '满300减50',
                value: 50,
                minAmount: 300
            },
            {
                id: 'COUPON003',
                name: '周末特惠券',
                desc: '满200减30',
                value: 30,
                minAmount: 200
            }
        ];

        const totalAmount = this.calculateTotalAmount();
        const availableCoupons = coupons.filter(coupon => totalAmount >= coupon.minAmount);

        if (availableCoupons.length === 0) {
            container.innerHTML = '<div class="no-coupons">暂无可用优惠券</div>';
            return;
        }

        container.innerHTML = availableCoupons.map(coupon => `
            <div class="coupon-item" data-coupon-id="${coupon.id}">
                <div class="coupon-info">
                    <div class="coupon-name">${coupon.name}</div>
                    <div class="coupon-desc">${coupon.desc}</div>
                </div>
                <div class="coupon-value">-¥${coupon.value}</div>
            </div>
        `).join('');
    }

    selectCoupon(couponElement) {
        // 移除之前的选中状态
        document.querySelectorAll('.coupon-item').forEach(item => {
            item.classList.remove('selected');
        });

        // 添加选中状态
        couponElement.classList.add('selected');
        
        const couponId = couponElement.dataset.couponId;
        const couponValue = parseInt(couponElement.querySelector('.coupon-value').textContent.replace(/[^\d]/g, ''));
        
        this.selectedCoupon = {
            id: couponId,
            value: couponValue
        };
        
        this.updatePaymentSummary();
        Utils.showMessage('优惠券应用成功', 'success');
    }

    applyCoupon() {
        const couponCode = document.getElementById('couponCode').value.trim();
        
        if (!couponCode) {
            Utils.showMessage('请输入优惠券码', 'warning');
            return;
        }
        
        // 模拟验证优惠券
        const validCoupons = {
            'WELCOME20': { value: 20, desc: '新用户欢迎券' },
            'SAVE50': { value: 50, desc: '满减优惠券' },
            'DISCOUNT30': { value: 30, desc: '折扣优惠券' }
        };
        
        if (validCoupons[couponCode]) {
            this.selectedCoupon = {
                id: couponCode,
                value: validCoupons[couponCode].value
            };
            
            this.updatePaymentSummary();
            document.getElementById('couponCode').value = '';
            Utils.showMessage(`优惠券应用成功，减免¥${validCoupons[couponCode].value}`, 'success');
        } else {
            Utils.showMessage('优惠券码无效或已过期', 'error');
        }
    }

    calculateTotalAmount() {
        const pricing = this.currentOrder.pricing;
        return pricing.ticketSubtotal + pricing.insuranceFee + this.serviceFee;
    }

    updatePaymentSummary() {
        const totalAmount = this.calculateTotalAmount();
        const discount = this.selectedCoupon ? this.selectedCoupon.value : 0;
        const finalAmount = Math.max(0, totalAmount - discount);
        
        document.getElementById('finalAmount').textContent = `¥${finalAmount}`;
        
        // 如果有优惠券，显示优惠信息
        if (this.selectedCoupon) {
            const existingDiscount = document.querySelector('.price-item.discount');
            if (existingDiscount) {
                existingDiscount.remove();
            }
            
            const totalItem = document.querySelector('.price-item.total');
            const discountItem = document.createElement('div');
            discountItem.className = 'price-item discount';
            discountItem.innerHTML = `
                <span class="label">优惠券减免：</span>
                <span class="value">-¥${this.selectedCoupon.value}</span>
            `;
            totalItem.parentNode.insertBefore(discountItem, totalItem);
        }
    }

    startCountdown() {
        this.updateCountdownDisplay();
        
        this.countdownTimer = setInterval(() => {
            this.remainingTime--;
            
            if (this.remainingTime <= 0) {
                this.handleTimeout();
                return;
            }
            
            this.updateCountdownDisplay();
        }, 1000);
    }

    updateCountdownDisplay() {
        const minutes = Math.floor(this.remainingTime / 60);
        const seconds = this.remainingTime % 60;
        const display = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        
        document.getElementById('countdown').textContent = display;
        
        // 最后1分钟时变红
        if (this.remainingTime <= 60) {
            document.getElementById('countdown').style.color = '#ff4d4f';
        }
    }

    handleTimeout() {
        clearInterval(this.countdownTimer);
        Utils.showMessage('订单已超时，请重新下单', 'error');
        
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 3000);
    }

    initiatePayment() {
        if (!this.selectedPaymentMethod) {
            Utils.showMessage('请选择支付方式', 'warning');
            return;
        }
        
        const finalAmount = this.calculateTotalAmount() - (this.selectedCoupon ? this.selectedCoupon.value : 0);
        
        if (finalAmount <= 0) {
            Utils.showMessage('支付金额异常', 'error');
            return;
        }
        
        this.showPaymentModal(finalAmount);
    }

    showPaymentModal(amount) {
        const modal = document.getElementById('paymentModal');
        const methodNames = {
            'alipay': '支付宝',
            'wechat': '微信支付',
            'unionpay': '银联支付',
            'points': '积分支付'
        };
        
        document.getElementById('confirmAmount').textContent = `¥${amount}`;
        document.getElementById('confirmMethod').textContent = methodNames[this.selectedPaymentMethod];
        
        // 重置模态框状态
        document.getElementById('paymentQR').style.display = 'none';
        document.getElementById('paymentProcessing').style.display = 'none';
        document.getElementById('confirmPaymentBtn').style.display = 'inline-block';
        
        modal.classList.add('show');
    }

    hidePaymentModal() {
        document.getElementById('paymentModal').classList.remove('show');
    }

    async processPayment() {
        const confirmBtn = document.getElementById('confirmPaymentBtn');
        const processingDiv = document.getElementById('paymentProcessing');
        const qrDiv = document.getElementById('paymentQR');
        
        confirmBtn.style.display = 'none';
        
        // 根据支付方式显示不同的处理流程
        if (this.selectedPaymentMethod === 'wechat' || this.selectedPaymentMethod === 'alipay') {
            // 显示二维码
            qrDiv.style.display = 'block';
            
            // 模拟扫码支付
            await Utils.delay(3000);
            
            qrDiv.style.display = 'none';
            processingDiv.style.display = 'block';
        } else {
            // 直接显示处理中
            processingDiv.style.display = 'block';
        }
        
        // 模拟支付处理
        await Utils.delay(2000);
        
        // 支付成功
        this.handlePaymentSuccess();
    }

    handlePaymentSuccess() {
        // 清除倒计时
        if (this.countdownTimer) {
            clearInterval(this.countdownTimer);
        }
        
        // 保存订单到已支付订单列表
        const paidOrder = {
            ...this.currentOrder,
            status: 'paid',
            paymentMethod: this.selectedPaymentMethod,
            coupon: this.selectedCoupon,
            finalAmount: this.calculateTotalAmount() - (this.selectedCoupon ? this.selectedCoupon.value : 0),
            paymentTime: new Date().toISOString()
        };
        
        // 保存到本地存储
        const existingOrders = Utils.getLocalStorage('userOrders') || [];
        existingOrders.push(paidOrder);
        Utils.setLocalStorage('userOrders', existingOrders);
        
        // 清除当前订单
        Utils.removeLocalStorage('currentOrder');
        
        // 如果使用积分支付，扣除积分
        if (this.selectedPaymentMethod === 'points') {
            const usedPoints = paidOrder.finalAmount * 100;
            this.availablePoints -= usedPoints;
            Utils.setLocalStorage('userPoints', this.availablePoints);
        }
        
        // 隐藏支付模态框
        this.hidePaymentModal();
        
        // 显示成功模态框
        this.showSuccessModal(paidOrder);
    }

    showSuccessModal(order) {
        document.getElementById('successOrderNumber').textContent = order.id;
        document.getElementById('successModal').classList.add('show');
        
        // 播放成功音效（如果需要）
        Utils.showMessage('支付成功！', 'success');
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    new Payment();
});

// 导出类供其他模块使用
if (typeof module !== 'undefined' && module.exports) {
    module.exports = Payment;
}