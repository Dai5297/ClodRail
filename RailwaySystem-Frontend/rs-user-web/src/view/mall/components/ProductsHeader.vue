<template>
  <div class="products-header">
    <div class="products-info">
      <h2 class="products-title">{{ categoryTitle }}</h2>
      <span class="products-count">共 {{ totalCount }} 件商品</span>
    </div>
    <div class="products-controls">
      <div class="sort-options">
        <label class="sort-label">排序:</label>
        <el-select v-model="currentSort" class="sort-select" @change="handleSortChange">
          <el-option label="默认排序" value="" />
          <el-option label="积分从低到高" value="price" />
          <el-option label="积分从高到低" value="price_desc" />
          <el-option label="热门优先" value="sold" />
          <el-option label="最新上架" value="create_time" />
          <el-option label="最近更新" value="update_time" />
        </el-select>
      </div>
      <div class="view-toggle">
        <el-button-group>
          <el-button
            :type="viewMode === 'grid' ? 'primary' : ''"
            @click="handleViewChange('grid')"
            title="网格视图"
          >
            <el-icon><Grid /></el-icon>
          </el-button>
          <el-button
            :type="viewMode === 'list' ? 'primary' : ''"
            @click="handleViewChange('list')"
            title="列表视图"
          >
            <el-icon><List /></el-icon>
          </el-button>
        </el-button-group>
      </div>
    </div>
  </div>
</template>

<script>
import { Grid, List } from '@element-plus/icons-vue'

export default {
  name: 'ProductsHeader',
  components: {
    Grid,
    List,
  },
  props: {
    categoryTitle: {
      type: String,
      default: '全部商品',
    },
    totalCount: {
      type: Number,
      default: 0,
    },
    sortOption: {
      type: String,
      default: '',
    },
    viewMode: {
      type: String,
      default: 'grid',
    },
  },
  data() {
    return {
      currentSort: this.sortOption,
    }
  },
  watch: {
    sortOption(newVal) {
      this.currentSort = newVal
    },
  },
  methods: {
    handleSortChange(value) {
      this.$emit('sort-change', value)
    },
    handleViewChange(mode) {
      this.$emit('view-change', mode)
    },
  },
}
</script>

<style scoped>
.products-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.products-info {
  display: flex;
  align-items: baseline;
  gap: 16px;
}

.products-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.products-count {
  font-size: 14px;
  color: #999;
}

.products-controls {
  display: flex;
  align-items: center;
  gap: 20px;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 10px;
}

.sort-label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

.sort-select {
  width: 150px;
}

@media (max-width: 768px) {
  .products-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .products-info {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .products-controls {
    width: 100%;
    justify-content: space-between;
  }

  .sort-select {
    width: 180px;
  }
}
</style>
