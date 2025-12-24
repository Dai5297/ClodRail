// 积分商城页面JavaScript

class PointsMall {
    constructor() {
        this.currentCategory = 'all';
        this.currentSort = 'default';
        this.currentView = 'grid';
        this.userPoints = 1200;
        this.products = [];
        this.filteredProducts = [];
        this.currentProduct = null;
        this.exchangeQuantity = 1;
        this.init();
    }

    init() {
        this.loadUserPoints();
        this.generateProducts();
        this.bindEvents();
        this.showProducts();
        this.updateUserPointsDisplay();
    }

    loadUserPoints() {
        this.userPoints = Utils.getLocalStorage('userPoints') || 1200;
    }

    updateUserPointsDisplay() {
        document.getElementById('userPoints').textContent = this.userPoints;
        document.getElementById('bannerUserPoints').textContent = this.userPoints;
        document.getElementById('currentPointsBalance').textContent = this.userPoints;
    }

    generateProducts() {
        this.products = [
            {
                id: 'p001',
                name: '20元火车票优惠券',
                description: '适用于所有高铁、动车票，满100元可用',
                category: 'coupons',
                points: 200,
                originalPrice: 20,
                image: 'https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=train%20ticket%20coupon%20voucher%20blue%20design&image_size=square',
                badge: 'hot',
                tags: ['优惠券', '火车票'],
                stock: 100,
                specs: {
                    '有效期': '30天',
                    '使用条件': '满100元可用',
                    '适用范围': '全国高铁、动车'
                }
            },
            {
                id: 'p002',
                name: '50元火车票优惠券',
                description: '适用于所有高铁、动车票，满300元可用',
                category: 'coupons',
                points: 500,
                originalPrice: 50,
                image: 'https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=premium%20train%20ticket%20coupon%20gold%20design&image_size=square',
                badge: 'new',
                tags: ['优惠券', '火车票', '高价值'],
                stock: 50,
                specs: {
                    '有效期': '60天',
                    '使用条件': '满300元可用',
                    '适用范围': '全国高铁、动车'
                }
            },
            {
                id: 'p003',
                name: '便携式充电宝',
                description: '10000mAh大容量，支持快充，旅行必备',
                category: 'travel',
                points: 800,
                originalPrice: 99,
                image: 'https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=portable%20power%20bank%20charger%20modern%20design&image_size=square',
                badge: '',
                tags: ['充电宝', '旅行', '便携'],
                stock: 30,
                specs: {
                    '容量': '10000mAh',
                    '输出': '5V/2A',
                    '重量': '230g',
                    '颜色': '黑色/白色'
                }
            },
            {
                id: 'p004',
                name: '旅行收纳包套装',
                description: '多功能收纳包，让旅行更整洁有序',
                category: 'travel',
                points: 600,
                originalPrice: 79,
                image: 'https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=travel%20organizer%20bag%20set%20luggage%20accessories&image_size=square',
                badge: '',
                tags: ['收纳', '旅行', '套装'],
                stock: 25,
                specs: {
                    '材质': '防水尼龙',
                    '规格': '大中小三件套',
                    '颜色': '灰色/蓝色',
                    '重量': '300g'
                }
            },
            {
                id: 'p005',
                name: '蓝牙无线耳机',
                description: '高品质音效，长续航，运动防汗',
                category: 'electronics',
                points: 1200,
                originalPrice: 199,
                image: 'https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=bluetooth%20wireless%20earphones%20modern%20technology&image_size=square',
                badge: 'hot',
                tags: ['耳机', '蓝牙', '无线'],
                stock: 20,
                specs: {
                    '连接方式': '蓝牙5.0',
                    '续航时间': '8小时',
                    '防水等级': 'IPX4',
                    '颜色': '黑色/白色'
                }
            },
            {
                id: 'p006',
                name: '智能手环',
                description: '健康监测，运动记录，消息提醒',
                category: 'electronics',
                points: 1500,
                originalPrice: 299,
                image: 'https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=smart%20fitness%20bracelet%20wearable%20technology&image_size=square',
                badge: 'new',
                tags: ['智能手环', '健康', '运动'],
                stock: 15,
                specs: {
                    '屏幕': '1.4英寸彩屏',
                    '续航': '7天',
                    '防水': '50米防水',
                    '功能': '心率/睡眠监测'
                }
            },
            {
                id: 'p007',
                name: '保温杯',
                description: '316不锈钢内胆，24小时保温保冷',
                category: 'lifestyle',
                points: 400,
                originalPrice: 59,
                image: 'https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=stainless%20steel%20thermos%20bottle%20insulated%20cup&image_size=square',
                badge: '',
                tags: ['保温杯', '不锈钢', '便携'],
                stock: 40,
                specs: {
                    '材质': '316不锈钢',
                    '容量': '500ml',
                    '保温时间': '24小时',
                    '颜色': '银色/黑色'
                }
            },
            {
                id: 'p008',
                name: '多功能背包',
                description: '商务休闲两用，大容量，防盗设计',
                category: 'lifestyle',
                points: 900,
                originalPrice: 139,
                image: 'https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=multifunctional%20backpack%20business%20travel%20bag&image_size=square',
                badge: '',
                tags: ['背包', '商务', '防盗'],
                stock: 18,
                specs: {
                    '材质': '防水牛津布',
                    '容量': '25L',
                    '尺寸': '45×30×15cm',
                    '特色': 'USB充电口'
                }
            },
            {
                id: 'p009',
                name: '特产零食大礼包',
                description: '精选各地特色小食，品味中华美食',
                category: 'food',
                points: 700,
                originalPrice: 89,
                image: 'https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=chinese%20specialty%20snacks%20gift%20box%20traditional%20food&image_size=square',
                badge: 'limited',
                tags: ['零食', '特产', '礼盒'],
                stock: 12,
                specs: {
                    '重量': '1.5kg',
                    '保质期': '6个月',
                    '包装': '精美礼盒装',
                    '内容': '8种特色小食'
                }
            },
            {
                id: 'p010',
                name: '茶叶礼盒',
                description: '精选名茶，传统工艺，送礼佳品',
                category: 'food',
                points: 1000,
                originalPrice: 168,
                image: 'https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=premium%20tea%20gift%20box%20traditional%20chinese%20tea&image_size=square',
                badge: '',
                tags: ['茶叶', '礼盒', '传统'],
                stock: 8,
                specs: {
                    '种类': '铁观音/龙井',
                    '重量': '250g×2',
                    '包装': '木质礼盒',
                    '产地': '福建/浙江'
                }
            }
        ];

        this.filteredProducts = [...this.products];
    }

    bindEvents() {
        // 分类切换
        document.querySelectorAll('.category-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const category = link.dataset.category;
                this.switchCategory(category);
            });
        });

        // 排序切换
        document.getElementById('sortSelect').addEventListener('change', (e) => {
            this.currentSort = e.target.value;
            this.sortProducts();
            this.showProducts();
        });

        // 视图切换
        document.querySelectorAll('.view-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                this.currentView = btn.dataset.view;
                this.updateViewButtons();
                this.updateProductsView();
            });
        });

        // 加载更多
        document.getElementById('loadMoreBtn').addEventListener('click', () => {
            this.loadMoreProducts();
        });

        // 商品详情模态框
        document.getElementById('closeProductModal').addEventListener('click', () => {
            this.hideProductModal();
        });

        // 数量控制
        document.getElementById('decreaseBtn').addEventListener('click', () => {
            this.changeQuantity(-1);
        });

        document.getElementById('increaseBtn').addEventListener('click', () => {
            this.changeQuantity(1);
        });

        document.getElementById('quantityInput').addEventListener('change', (e) => {
            this.exchangeQuantity = Math.max(1, Math.min(10, parseInt(e.target.value) || 1));
            this.updateTotalPoints();
        });

        // 兑换按钮
        document.getElementById('exchangeBtn').addEventListener('click', () => {
            this.showExchangeModal();
        });

        // 兑换确认模态框
        document.getElementById('closeExchangeModal').addEventListener('click', () => {
            this.hideExchangeModal();
        });

        document.getElementById('cancelExchangeBtn').addEventListener('click', () => {
            this.hideExchangeModal();
        });

        document.getElementById('confirmExchangeBtn').addEventListener('click', () => {
            this.confirmExchange();
        });

        // 成功模态框
        document.getElementById('continueShoppingBtn').addEventListener('click', () => {
            this.hideSuccessModal();
        });

        // 模态框外部点击关闭
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal')) {
                e.target.classList.remove('show');
            }
        });
    }

    switchCategory(category) {
        this.currentCategory = category;
        
        // 更新分类菜单状态
        document.querySelectorAll('.category-link').forEach(link => {
            link.classList.remove('active');
        });
        
        const activeLink = document.querySelector(`[data-category="${category}"]`);
        if (activeLink) {
            activeLink.classList.add('active');
        }
        
        // 筛选商品
        this.filterProducts();
        this.sortProducts();
        this.showProducts();
        
        // 更新标题
        const categoryNames = {
            'all': '全部商品',
            'coupons': '优惠券',
            'travel': '旅行用品',
            'electronics': '数码产品',
            'lifestyle': '生活用品',
            'food': '美食特产'
        };
        
        document.getElementById('productsTitle').textContent = categoryNames[category];
    }

    filterProducts() {
        if (this.currentCategory === 'all') {
            this.filteredProducts = [...this.products];
        } else {
            this.filteredProducts = this.products.filter(product => 
                product.category === this.currentCategory
            );
        }
    }

    sortProducts() {
        switch (this.currentSort) {
            case 'points-asc':
                this.filteredProducts.sort((a, b) => a.points - b.points);
                break;
            case 'points-desc':
                this.filteredProducts.sort((a, b) => b.points - a.points);
                break;
            case 'popularity':
                this.filteredProducts.sort((a, b) => {
                    const aScore = (a.badge === 'hot' ? 3 : 0) + (a.badge === 'new' ? 2 : 0) + (a.badge === 'limited' ? 1 : 0);
                    const bScore = (b.badge === 'hot' ? 3 : 0) + (b.badge === 'new' ? 2 : 0) + (b.badge === 'limited' ? 1 : 0);
                    return bScore - aScore;
                });
                break;
            default:
                // 默认排序保持原有顺序
                break;
        }
    }

    showProducts() {
        const container = document.getElementById('productsGrid');
        const countElement = document.getElementById('productsCount');
        
        if (this.filteredProducts.length === 0) {
            container.innerHTML = this.getEmptyState();
            countElement.textContent = '共 0 件商品';
            return;
        }
        
        countElement.textContent = `共 ${this.filteredProducts.length} 件商品`;
        
        container.innerHTML = this.filteredProducts.map(product => `
            <div class="product-card" onclick="pointsMall.showProductDetail('${product.id}')">
                <div class="product-image">
                    <img src="${product.image}" alt="${product.name}">
                    ${product.badge ? `<div class="product-badge ${product.badge}">${this.getBadgeText(product.badge)}</div>` : ''}
                </div>
                <div class="product-content">
                    <h3 class="product-name">${product.name}</h3>
                    <p class="product-description">${product.description}</p>
                    <div class="product-price">
                        <span class="points-price">${product.points}</span>
                        <span class="original-price">¥${product.originalPrice}</span>
                    </div>
                    <div class="product-tags">
                        ${product.tags.map(tag => `<span class="product-tag">${tag}</span>`).join('')}
                    </div>
                    <div class="product-actions">
                        <button class="exchange-btn" ${this.userPoints < product.points ? 'disabled' : ''} 
                                onclick="event.stopPropagation(); pointsMall.quickExchange('${product.id}')">
                            ${this.userPoints < product.points ? '积分不足' : '立即兑换'}
                        </button>
                        <button class="favorite-btn" onclick="event.stopPropagation(); pointsMall.toggleFavorite('${product.id}')">
                            ♡
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
    }

    getBadgeText(badge) {
        const badgeTexts = {
            'hot': '热门',
            'new': '新品',
            'limited': '限量'
        };
        return badgeTexts[badge] || '';
    }

    updateViewButtons() {
        document.querySelectorAll('.view-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        
        const activeBtn = document.querySelector(`[data-view="${this.currentView}"]`);
        if (activeBtn) {
            activeBtn.classList.add('active');
        }
    }

    updateProductsView() {
        const container = document.getElementById('productsGrid');
        if (this.currentView === 'list') {
            container.classList.add('list-view');
        } else {
            container.classList.remove('list-view');
        }
    }

    showProductDetail(productId) {
        const product = this.products.find(p => p.id === productId);
        if (!product) return;
        
        this.currentProduct = product;
        this.exchangeQuantity = 1;
        
        // 填充商品详情
        document.getElementById('productModalTitle').textContent = product.name;
        document.getElementById('productMainImage').src = product.image;
        document.getElementById('productDetailName').textContent = product.name;
        document.getElementById('productDetailPoints').textContent = product.points;
        document.getElementById('productDetailOriginal').textContent = `¥${product.originalPrice}`;
        document.getElementById('productDetailDescription').textContent = product.description;
        
        // 填充标签
        const tagsContainer = document.getElementById('productDetailTags');
        tagsContainer.innerHTML = product.tags.map(tag => 
            `<span class="product-tag">${tag}</span>`
        ).join('');
        
        // 填充规格
        const specsContainer = document.getElementById('productDetailSpecs');
        if (product.specs && Object.keys(product.specs).length > 0) {
            specsContainer.innerHTML = `
                <h4>商品规格</h4>
                <ul class="specs-list">
                    ${Object.entries(product.specs).map(([key, value]) => 
                        `<li class="specs-item">
                            <span class="specs-label">${key}:</span>
                            <span class="specs-value">${value}</span>
                        </li>`
                    ).join('')}
                </ul>
            `;
        } else {
            specsContainer.innerHTML = '';
        }
        
        // 重置数量
        document.getElementById('quantityInput').value = 1;
        this.updateTotalPoints();
        
        // 更新兑换按钮状态
        const exchangeBtn = document.getElementById('exchangeBtn');
        if (this.userPoints < product.points) {
            exchangeBtn.disabled = true;
            exchangeBtn.textContent = '积分不足';
        } else {
            exchangeBtn.disabled = false;
            exchangeBtn.textContent = '立即兑换';
        }
        
        this.showProductModal();
    }

    showProductModal() {
        document.getElementById('productModal').classList.add('show');
    }

    hideProductModal() {
        document.getElementById('productModal').classList.remove('show');
    }

    changeQuantity(delta) {
        this.exchangeQuantity = Math.max(1, Math.min(10, this.exchangeQuantity + delta));
        document.getElementById('quantityInput').value = this.exchangeQuantity;
        this.updateTotalPoints();
    }

    updateTotalPoints() {
        if (!this.currentProduct) return;
        
        const total = this.currentProduct.points * this.exchangeQuantity;
        document.getElementById('totalPoints').textContent = total;
        
        // 更新兑换按钮状态
        const exchangeBtn = document.getElementById('exchangeBtn');
        if (this.userPoints < total) {
            exchangeBtn.disabled = true;
            exchangeBtn.textContent = '积分不足';
        } else {
            exchangeBtn.disabled = false;
            exchangeBtn.textContent = '立即兑换';
        }
    }

    quickExchange(productId) {
        this.showProductDetail(productId);
        // 可以直接跳转到兑换确认
    }

    toggleFavorite(productId) {
        // 收藏功能实现
        Utils.showMessage('收藏功能开发中', 'info');
    }

    showExchangeModal() {
        if (!this.currentProduct) return;
        
        const totalPoints = this.currentProduct.points * this.exchangeQuantity;
        
        if (this.userPoints < totalPoints) {
            Utils.showMessage('积分不足，无法兑换', 'warning');
            return;
        }
        
        // 填充确认信息
        document.getElementById('confirmProductImage').src = this.currentProduct.image;
        document.getElementById('confirmProductName').textContent = this.currentProduct.name;
        document.getElementById('confirmQuantity').textContent = this.exchangeQuantity;
        document.getElementById('confirmPoints').textContent = `${totalPoints} 积分`;
        document.getElementById('afterExchangeBalance').textContent = this.userPoints - totalPoints;
        
        // 清空表单
        document.getElementById('recipientName').value = '';
        document.getElementById('recipientPhone').value = '';
        document.getElementById('recipientAddress').value = '';
        
        this.hideProductModal();
        document.getElementById('exchangeModal').classList.add('show');
    }

    hideExchangeModal() {
        document.getElementById('exchangeModal').classList.remove('show');
    }

    confirmExchange() {
        // 验证收货信息
        const recipientName = document.getElementById('recipientName').value.trim();
        const recipientPhone = document.getElementById('recipientPhone').value.trim();
        const recipientAddress = document.getElementById('recipientAddress').value.trim();
        
        if (!recipientName || !recipientPhone || !recipientAddress) {
            Utils.showMessage('请填写完整的收货信息', 'warning');
            return;
        }
        
        // 验证手机号
        if (!/^1[3-9]\d{9}$/.test(recipientPhone)) {
            Utils.showMessage('请输入正确的手机号', 'warning');
            return;
        }
        
        const totalPoints = this.currentProduct.points * this.exchangeQuantity;
        
        // 扣除积分
        this.userPoints -= totalPoints;
        Utils.setLocalStorage('userPoints', this.userPoints);
        
        // 生成兑换订单
        const exchangeOrder = {
            id: 'EX' + Date.now(),
            productId: this.currentProduct.id,
            productName: this.currentProduct.name,
            quantity: this.exchangeQuantity,
            pointsUsed: totalPoints,
            recipient: {
                name: recipientName,
                phone: recipientPhone,
                address: recipientAddress
            },
            status: 'processing',
            createTime: new Date().toISOString()
        };
        
        // 保存兑换记录
        const exchangeHistory = Utils.getLocalStorage('exchangeHistory') || [];
        exchangeHistory.push(exchangeOrder);
        Utils.setLocalStorage('exchangeHistory', exchangeHistory);
        
        // 更新积分显示
        this.updateUserPointsDisplay();
        
        // 显示成功模态框
        document.getElementById('exchangeOrderNumber').textContent = exchangeOrder.id;
        this.hideExchangeModal();
        this.showSuccessModal();
        
        Utils.showMessage('兑换成功！', 'success');
    }

    showSuccessModal() {
        document.getElementById('successModal').classList.add('show');
    }

    hideSuccessModal() {
        document.getElementById('successModal').classList.remove('show');
    }

    loadMoreProducts() {
        // 模拟加载更多商品
        Utils.showMessage('已显示全部商品', 'info');
        document.getElementById('loadMoreContainer').style.display = 'none';
    }

    getEmptyState() {
        return `
            <div class="empty-state">
                <img src="https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=empty%20shopping%20cart%20illustration%20simple%20gray&image_size=square" alt="暂无商品">
                <h3>暂无商品</h3>
                <p>该分类下暂时没有商品，请选择其他分类</p>
            </div>
        `;
    }
}

// 全局变量，供HTML中的onclick使用
let pointsMall;

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    pointsMall = new PointsMall();
});

// 导出类供其他模块使用
if (typeof module !== 'undefined' && module.exports) {
    module.exports = PointsMall;
}