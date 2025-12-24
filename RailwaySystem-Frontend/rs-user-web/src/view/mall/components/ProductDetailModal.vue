<template>
  <el-dialog
    v-model="visible"
    title="商品详情"
    width="800px"
    :before-close="handleClose"
    class="product-detail-modal"
  >
    <div v-if="product" class="product-detail">
      <div class="product-images">
        <div class="main-image">
          <img :src="currentImage" :alt="product.name" />
        </div>
        <div v-if="productImages && productImages.length > 1" class="image-thumbnails">
          <div
            v-for="(img, index) in productImages"
            :key="index"
            class="thumbnail"
            :class="{ active: currentImage === img }"
            @click="currentImage = img"
          >
            <img :src="img" :alt="`${product.name} ${index + 1}`" />
          </div>
        </div>
      </div>

      <div class="product-info">
        <h2 class="product-name">{{ product.name }}</h2>

        <div class="product-price">
          <span class="points-price">{{ product.price }} 积分</span>
        </div>

        <div v-if="product.tags && product.tags.length" class="product-tags">
          <el-tag v-for="tag in product.tags" :key="tag" type="primary">{{ tag }}</el-tag>
        </div>

        <div class="product-description">
          <h4>商品描述</h4>
          <p>{{ product.description || '暂无描述' }}</p>
        </div>

        <div v-if="parsedSpecs && Object.keys(parsedSpecs).length > 0" class="product-specs">
          <h4>商品规格</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item v-for="(value, key) in parsedSpecs" :key="key" :label="key">
              {{ value }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="exchange-section">
          <div class="quantity-selector">
            <label>数量:</label>
            <el-input-number v-model="quantity" :min="1" :max="10" @change="handleQuantityChange" />
          </div>

          <div class="total-points">
            <span>总计: </span>
            <span class="total-value">{{ totalPoints }}</span>
            <span> 积分</span>
          </div>

          <el-button
            type="primary"
            size="large"
            class="exchange-btn"
            @click="handleExchange"
            :disabled="!canExchange"
          >
            立即兑换
          </el-button>

          <div v-if="!canExchange" class="warning-text">积分不足，无法兑换</div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { ref, computed, watch } from 'vue'

export default {
  name: 'ProductDetailModal',
  props: {
    modelValue: {
      type: Boolean,
      default: false,
    },
    product: {
      type: Object,
      default: null,
    },
    userPoints: {
      type: Number,
      default: 0,
    },
  },
  emits: ['update:modelValue', 'exchange-click'],
  setup(props, { emit }) {
    const visible = computed({
      get: () => props.modelValue,
      set: (val) => emit('update:modelValue', val),
    })

    const currentImage = ref('')
    const quantity = ref(1)

    // 计算总积分
    const totalPoints = computed(() => {
      return (props.product?.price || 0) * quantity.value
    })

    // 是否可以兑换
    const canExchange = computed(() => {
      return props.userPoints >= totalPoints.value
    })

    // 解析规格信息
    const parsedSpecs = computed(() => {
      if (!props.product || !props.product.spec) return {}

      // 如果已经是对象，直接返回
      if (typeof props.product.spec === 'object' && !Array.isArray(props.product.spec)) {
        return props.product.spec
      }

      // 如果是字符串，尝试解析为JSON
      if (typeof props.product.spec === 'string') {
        try {
          return JSON.parse(props.product.spec)
        } catch (e) {
          return {}
        }
      }

      return {}
    })

    // 获取商品图片数组
    const productImages = computed(() => {
      const images = []

      // 添加主图
      if (props.product?.image) {
        images.push(props.product.image)
      }

      // 添加多图
      if (props.product?.images) {
        if (Array.isArray(props.product.images)) {
          images.push(...props.product.images)
        } else if (typeof props.product.images === 'string') {
          try {
            const parsedImages = JSON.parse(props.product.images)
            if (Array.isArray(parsedImages)) {
              images.push(...parsedImages)
            }
          } catch (e) {
          }
        }
      }

      // 去重
      return [...new Set(images)]
    })

    // 监听product变化，更新图片
    watch(
      () => props.product,
      (newProduct) => {
        if (newProduct) {
          currentImage.value =
            newProduct.image || (productImages.value && productImages.value[0]) || ''
          quantity.value = 1
        }
      },
      { immediate: true },
    )

    const handleQuantityChange = (val) => {
      quantity.value = val
    }

    const handleClose = () => {
      visible.value = false
    }

    const handleExchange = () => {
      emit('exchange-click', {
        product: props.product,
        quantity: quantity.value,
        totalPoints: totalPoints.value,
      })
    }

    return {
      visible,
      currentImage,
      quantity,
      totalPoints,
      canExchange,
      parsedSpecs,
      productImages,
      handleQuantityChange,
      handleClose,
      handleExchange,
    }
  },
}
</script>

<style scoped>
.product-detail {
  display: flex;
  gap: 30px;
}

.product-images {
  flex-shrink: 0;
  width: 350px;
}

.main-image {
  width: 100%;
  height: 350px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f5f5;
  margin-bottom: 10px;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-thumbnails {
  display: flex;
  gap: 10px;
}

.thumbnail {
  width: 70px;
  height: 70px;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.thumbnail:hover,
.thumbnail.active {
  border-color: #667eea;
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  margin: 0 0 16px 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 16px;
}

.points-price {
  font-size: 28px;
  font-weight: 700;
  color: #667eea;
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
}

.product-tags {
  margin-bottom: 20px;
}

.product-description {
  margin-bottom: 20px;
}

.product-description h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.product-description p {
  margin: 0;
  line-height: 1.6;
  color: #666;
}

.product-specs {
  margin-bottom: 20px;
}

.product-specs h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.exchange-section {
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.quantity-selector label {
  font-size: 14px;
  color: #666;
}

.total-points {
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.total-value {
  color: #667eea;
  font-size: 24px;
}

.exchange-btn {
  width: 100%;
}

.warning-text {
  margin-top: 10px;
  color: #f56c6c;
  font-size: 14px;
  text-align: center;
}

@media (max-width: 768px) {
  .product-detail {
    flex-direction: column;
  }

  .product-images {
    width: 100%;
  }

  .product-name {
    font-size: 20px;
  }

  .points-price {
    font-size: 24px;
  }
}
</style>
