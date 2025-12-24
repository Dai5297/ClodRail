// 公共JavaScript文件
// Utils类已在utils.js中定义，此文件不再重复定义

// 模拟数据
const MockData = {
    // 城市列表
    cities: [
        '北京', '上海', '广州', '深圳', '成都', '重庆', '杭州', '南京',
        '武汉', '西安', '天津', '苏州', '长沙', '郑州', '青岛', '大连'
    ],
    
    // 车次数据
    trains: [
        {
            id: 'G1',
            number: 'G1',
            departure: '北京南',
            arrival: '上海虹桥',
            departureTime: '08:00',
            arrivalTime: '13:28',
            duration: '5h28m',
            price: {
                secondClass: 553,
                firstClass: 933,
                business: 1748
            },
            available: {
                secondClass: 'available',
                firstClass: 'available',
                business: 'limited'
            }
        },
        {
            id: 'D3',
            number: 'D3',
            departure: '北京',
            arrival: '上海',
            departureTime: '09:15',
            arrivalTime: '17:45',
            duration: '8h30m',
            price: {
                secondClass: 428,
                firstClass: 687,
                sleeper: 598
            },
            available: {
                secondClass: 'available',
                firstClass: 'available',
                sleeper: 'limited'
            }
        },
        {
            id: 'T109',
            number: 'T109',
            departure: '北京',
            arrival: '上海',
            departureTime: '19:30',
            arrivalTime: '07:05',
            duration: '11h35m',
            price: {
                hardSeat: 156,
                hardSleeper: 263,
                softSleeper: 414
            },
            available: {
                hardSeat: 'limited',
                hardSleeper: 'limited',
                softSleeper: 'available'
            }
        }
    ],
    
    // 热门线路
    popularRoutes: [
        { from: '北京', to: '上海', price: 553 },
        { from: '广州', to: '深圳', price: 75 },
        { from: '成都', to: '重庆', price: 154 },
        { from: '杭州', to: '南京', price: 267 }
    ],
    
    // 积分商品
    pointsProducts: [
        {
            id: 1,
            name: '星巴克咖啡券',
            points: 500,
            stock: 100,
            image: 'images/products/starbucks.jpg',
            category: 'food'
        },
        {
            id: 2,
            name: '电影票',
            points: 800,
            stock: 50,
            image: 'images/products/movie.jpg',
            category: 'entertainment'
        },
        {
            id: 3,
            name: '手机充值卡',
            points: 1000,
            stock: 200,
            image: 'images/products/phone-card.jpg',
            category: 'telecom'
        }
    ]
};

// 页面初始化
document.addEventListener('DOMContentLoaded', function() {
    // 设置默认日期为明天
    const dateInput = document.getElementById('date');
    if (dateInput) {
        dateInput.value = Utils.getTomorrowDate();
        dateInput.min = Utils.getTodayDate();
    }
    
    // 添加消息动画样式
    if (!document.getElementById('message-styles')) {
        const style = document.createElement('style');
        style.id = 'message-styles';
        style.textContent = `
            @keyframes slideInRight {
                from {
                    transform: translateX(100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
            
            @keyframes slideOutRight {
                from {
                    transform: translateX(0);
                    opacity: 1;
                }
                to {
                    transform: translateX(100%);
                    opacity: 0;
                }
            }
            
            .message-content {
                display: flex;
                align-items: center;
                justify-content: space-between;
                gap: 12px;
            }
            
            .message-close {
                background: none;
                border: none;
                font-size: 18px;
                cursor: pointer;
                color: #8c8c8c;
                padding: 0;
                width: 20px;
                height: 20px;
                display: flex;
                align-items: center;
                justify-content: center;
            }
            
            .message-close:hover {
                color: #262626;
            }
        `;
        document.head.appendChild(style);
    }
});

// 导出到全局
window.Utils = Utils;
window.MockData = MockData;