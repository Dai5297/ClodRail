<template>
  <div class="space-y-6">
    <!-- 页面头部 -->
    <div class="border-b border-slate-200 pb-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div>
        <h2 class="text-2xl font-bold text-slate-800">常用联系人</h2>
        <p class="mt-2 text-sm text-slate-500">管理您的常用联系人信息，方便快速购票</p>
      </div>
      <button 
        @click="handleAdd"
        class="inline-flex items-center px-4 py-2 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all active:scale-95"
      >
        <svg class="mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        添加联系人
      </button>
    </div>

    <!-- 搜索栏组件 -->
    <SearchBar 
      :search-form="searchForm"
      @search="handleSearch"
      @reset="handleReset"
    />

    <!-- 联系人列表 -->
    <div class="min-h-[400px]">
      <div v-if="loading" class="flex justify-center items-center py-20">
        <svg class="animate-spin h-8 w-8 text-blue-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
      </div>
      
      <div v-else-if="contactList.length === 0" class="flex flex-col items-center justify-center py-16 text-center bg-slate-50 rounded-xl border border-dashed border-slate-300">
        <div class="mx-auto h-12 w-12 text-slate-400">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
        </div>
        <h3 class="mt-2 text-sm font-medium text-slate-900">暂无联系人</h3>
        <p class="mt-1 text-sm text-slate-500">开始添加您的第一位常用联系人吧。</p>
        <div class="mt-6">
          <button 
            @click="handleAdd"
            class="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            <svg class="-ml-1 mr-2 h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
            </svg>
            添加联系人
          </button>
        </div>
      </div>
      
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        <ContactCard
          v-for="contact in contactList"
          :key="contact.id"
          :contact="contact"
          @edit="handleEdit"
          @delete="confirmDelete"
        />
      </div>
    </div>

    <!-- 分页 -->
    <div class="flex items-center justify-between border-t border-slate-200 bg-white px-4 py-3 sm:px-6 mt-6" v-if="contactList.length > 0">
      <div class="flex flex-1 justify-between sm:hidden">
        <button 
          @click="handleCurrentChange(pagination.current - 1)"
          :disabled="pagination.current === 1"
          class="relative inline-flex items-center rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          上一页
        </button>
        <button 
          @click="handleCurrentChange(pagination.current + 1)"
          :disabled="pagination.current >= pagination.pages"
          class="relative ml-3 inline-flex items-center rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          下一页
        </button>
      </div>
      <div class="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
        <div>
          <p class="text-sm text-slate-700">
            显示第
            <span class="font-medium">{{ (pagination.current - 1) * pagination.size + 1 }}</span>
            到
            <span class="font-medium">{{ Math.min(pagination.current * pagination.size, pagination.total) }}</span>
            条，共
            <span class="font-medium">{{ pagination.total }}</span>
            条结果
          </p>
        </div>
        <div>
          <nav class="isolate inline-flex -space-x-px rounded-md shadow-sm" aria-label="Pagination">
            <button 
              @click="handleCurrentChange(pagination.current - 1)"
              :disabled="pagination.current === 1"
              class="relative inline-flex items-center rounded-l-md px-2 py-2 text-slate-400 ring-1 ring-inset ring-slate-300 hover:bg-slate-50 focus:z-20 focus:outline-offset-0 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span class="sr-only">Previous</span>
              <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                <path fill-rule="evenodd" d="M12.79 5.23a.75.75 0 01-.02 1.06L8.832 10l3.938 3.71a.75.75 0 11-1.04 1.08l-4.5-4.25a.75.75 0 010-1.08l4.5-4.25a.75.75 0 011.06.02z" clip-rule="evenodd" />
              </svg>
            </button>
            
            <template v-for="page in displayedPages" :key="page">
              <span v-if="page === '...'" class="relative inline-flex items-center px-4 py-2 text-sm font-semibold text-slate-700 ring-1 ring-inset ring-slate-300 focus:outline-offset-0">...</span>
              <button 
                v-else
                @click="handleCurrentChange(page)"
                :class="[
                  page === pagination.current ? 'z-10 bg-blue-600 text-white focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600' : 'text-slate-900 ring-1 ring-inset ring-slate-300 hover:bg-slate-50 focus:outline-offset-0',
                  'relative inline-flex items-center px-4 py-2 text-sm font-semibold focus:z-20'
                ]"
              >
                {{ page }}
              </button>
            </template>

            <button 
              @click="handleCurrentChange(pagination.current + 1)"
              :disabled="pagination.current >= pagination.pages"
              class="relative inline-flex items-center rounded-r-md px-2 py-2 text-slate-400 ring-1 ring-inset ring-slate-300 hover:bg-slate-50 focus:z-20 focus:outline-offset-0 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span class="sr-only">Next</span>
              <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                <path fill-rule="evenodd" d="M7.21 14.77a.75.75 0 01.02-1.06L11.168 10 7.23 6.29a.75.75 0 111.04-1.08l4.5 4.25a.75.75 0 010 1.08l-4.5 4.25a.75.75 0 01-1.06-.02z" clip-rule="evenodd" />
              </svg>
            </button>
          </nav>
        </div>
      </div>
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

    <!-- 删除确认弹窗 -->
    <Transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div v-if="deleteModalVisible" class="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6" role="dialog" aria-modal="true">
        <div class="fixed inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity" @click="cancelDelete"></div>
        
        <div class="relative bg-white rounded-lg shadow-xl w-full max-w-sm overflow-hidden transform transition-all">
          <div class="p-6">
            <div class="flex items-center gap-4">
              <div class="flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-full bg-red-100 sm:h-10 sm:w-10">
                <svg class="h-6 w-6 text-red-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                </svg>
              </div>
              <div>
                <h3 class="text-lg leading-6 font-medium text-slate-900">删除联系人</h3>
                <div class="mt-2">
                  <p class="text-sm text-slate-500">
                    确定要删除联系人 "{{ contactToDelete?.name }}" 吗？此操作无法撤销。
                  </p>
                </div>
              </div>
            </div>
          </div>
          <div class="bg-slate-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
            <button 
              type="button" 
              class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-red-600 text-base font-medium text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sm:ml-3 sm:w-auto sm:text-sm"
              @click="handleDelete"
            >
              删除
            </button>
            <button 
              type="button" 
              class="mt-3 w-full inline-flex justify-center rounded-md border border-slate-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-slate-700 hover:bg-slate-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
              @click="cancelDelete"
            >
              取消
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getContactList, getContactById, addContact, updateContact, deleteContact } from '@/api/contact'
import toast from '@/components/Toast'
import SearchBar from './components/SearchBar.vue'
import ContactCard from './components/ContactCard.vue'
import ContactForm from './components/ContactForm.vue'

const loading = ref(false)
const submitting = ref(false)
const contactList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)

// 删除相关
const deleteModalVisible = ref(false)
const contactToDelete = ref(null)

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

// 计算显示的页码
const displayedPages = computed(() => {
  const pages = []
  const { current, pages: totalPages } = pagination
  
  if (totalPages <= 7) {
    for (let i = 1; i <= totalPages; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 4) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(totalPages)
    } else if (current >= totalPages - 3) {
      pages.push(1)
      pages.push('...')
      for (let i = totalPages - 4; i <= totalPages; i++) {
        pages.push(i)
      }
    } else {
      pages.push(1)
      pages.push('...')
      for (let i = current - 1; i <= current + 1; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(totalPages)
    }
  }
  return pages
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
    toast.error('获取联系人列表失败')
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

const handleCurrentChange = (newPage) => {
  if (newPage < 1 || newPage > pagination.pages) return
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
      toast.error(response.message || '获取联系人信息失败')
    }
  } catch (error) {
    toast.error('获取联系人信息失败')
  } finally {
    loading.value = false
  }
}

const confirmDelete = (contact) => {
  contactToDelete.value = contact
  deleteModalVisible.value = true
}

const cancelDelete = () => {
  deleteModalVisible.value = false
  contactToDelete.value = null
}

const handleDelete = async () => {
  if (!contactToDelete.value) return
  
  try {
    loading.value = true
    const response = await deleteContact(contactToDelete.value.id);
    if (response.code === 200) {
      toast.success('删除成功')
      deleteModalVisible.value = false
      await fetchContactList()
    } else {
      toast.error(response.message || '删除失败')
    }
  } catch (error) {
    toast.error('删除失败')
  } finally {
    loading.value = false
    contactToDelete.value = null
  }
}

const handleSubmit = async () => {
  try {
    submitting.value = true
    
    if (isEdit.value) {
      const response = await updateContact(formData);
      if (response.code === 200) {
        toast.success('更新成功')
        dialogVisible.value = false
        await fetchContactList()
      } else {
        toast.error(response.message || '更新失败')
      }
    } else {
      const response = await addContact(formData);
      if (response.code === 200) {
        toast.success('添加成功')
        dialogVisible.value = false
        await fetchContactList()
      } else {
        toast.error(response.message || '添加失败')
      }
    }
  } catch (error) {
    if (isEdit.value) {
      toast.error('更新失败')
    } else {
      toast.error('添加失败')
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