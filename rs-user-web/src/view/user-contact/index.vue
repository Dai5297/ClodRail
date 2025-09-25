<template>
  <div class="user-contact-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2 class="page-title">常用联系人</h2>
      <p class="page-desc">管理您的常用联系人信息，方便快速购票</p>
    </div>

    <!-- 搜索栏组件 -->
    <SearchBar 
      :search-form="searchForm"
      @search="handleSearch"
      @reset="handleReset"
    />

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加联系人
      </el-button>
    </div>

    <!-- 联系人列表 -->
    <div class="contact-list" v-loading="loading">
      <div v-if="contactList.length === 0" class="empty-state">
        <el-empty description="暂无联系人数据" />
      </div>
      <div v-else class="contact-grid">
        <ContactCard
          v-for="contact in contactList"
          :key="contact.id"
          :contact="contact"
          @edit="handleEdit"
          @delete="handleDelete"
        />
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="contactList.length > 0">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/编辑联系人表单组件 -->
    <ContactForm
      :visible="dialogVisible"
      :is-edit="isEdit"
      :form-data="formData"
      :submitting="submitting"
      @close="handleCloseDialog"
      @submit="handleSubmit"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getContactList, getContactById, addContact, updateContact, deleteContact } from '@/api/contact'
import SearchBar from './component/SearchBar.vue'
import ContactCard from './component/ContactCard.vue'
import ContactForm from './component/ContactForm.vue'

const loading = ref(false)
const submitting = ref(false)
const contactList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
  pages: 0
})

const searchForm = reactive({
  name: '',
  passengerType: null,
  status: null
})

const formData = reactive({
  id: null,
  name: '',
  idCard: '',
  phone: '',
  email: '',
  passengerType: 1, // 默认成人
  isDefault: 0, // 默认非默认联系人
  status: 1, // 默认启用
  remark: ''
})

const fetchContactList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    
    // 过滤空值参数
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })
    
    const response = await getContactList(params)
    
    if (response.data) {
      contactList.value = response.data.records || []
      pagination.total = response.data.total || 0
      pagination.pages = response.data.pages || 0
      pagination.current = response.data.current || 1
      pagination.size = response.data.size || 10
    } else {
      contactList.value = []
      pagination.total = 0
      pagination.pages = 0
    }
  } catch (error) {
    ElMessage.error('获取联系人列表失败')
    contactList.value = []
    pagination.total = 0
    pagination.pages = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1 // 重置到第一页
  fetchContactList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    passengerType: null,
    status: null
  })
  pagination.current = 1
  fetchContactList()
}

const handleSizeChange = (newSize) => {
  pagination.size = newSize
  pagination.current = 1
  fetchContactList()
}

const handleCurrentChange = (newPage) => {
  pagination.current = newPage
  fetchContactList()
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = async (contact) => {
  try {
    isEdit.value = true
    loading.value = true
    
    // 根据ID获取联系人详细信息
    const response = await getContactById(contact.id)
    if (response.code === 200) {
      Object.assign(formData, response.data)
      dialogVisible.value = true
    } else {
      ElMessage.error(response.message || '获取联系人信息失败')
    }
  } catch (error) {
    console.error('获取联系人信息失败:', error)
    ElMessage.error('获取联系人信息失败')
  } finally {
    loading.value = false
  }
}

const handleDelete = async (contact) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除联系人"${contact.name}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    loading.value = true
    const response = await deleteContact(contact.id);
    if (response.code === 200) {
      ElMessage.success('删除成功')
      await fetchContactList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  try {
    submitting.value = true
    
    if (isEdit.value) {
      const response = await updateContact(formData);
      if (response.code === 200) {
        ElMessage.success('更新成功')
        dialogVisible.value = false
        await fetchContactList()
      } else {
        ElMessage.error(response.message || '更新失败')
      }
    } else {
      const response = await addContact(formData);
      if (response.code === 200) {
        ElMessage.success('添加成功')
        dialogVisible.value = false
        await fetchContactList()
      } else {
        ElMessage.error(response.message || '添加失败')
        // 添加失败时不关闭弹窗
      }
    }
  } catch (error) {
    if (isEdit.value) {
      ElMessage.error('更新失败')
    } else {
      ElMessage.error('添加失败')
      // 添加失败时不关闭弹窗
    }
  } finally {
    submitting.value = false
  }
}

const handleCloseDialog = () => {
  dialogVisible.value = false
  resetForm()
}

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    name: '',
    idCard: '',
    phone: '',
    email: '',
    passengerType: 1, // 默认成人
    isDefault: 0, // 默认非默认联系人
    status: 1, // 默认启用
    remark: ''
  })
}

onMounted(async () => {
  await fetchContactList()
})
</script>

<style scoped>
.user-contact-page {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 8px 0;
}

.page-desc {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.action-bar {
  margin-bottom: 24px;
}

.contact-list {
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding: 16px 0;
}
</style>