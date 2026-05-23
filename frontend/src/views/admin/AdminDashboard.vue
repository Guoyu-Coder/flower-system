<template>
  <div class="admin-layout">
    <div class="admin-sidebar">
      <div class="admin-logo">🌸 花语轩</div>
      <div class="admin-menu">
        <div v-for="item in menuItems" :key="item.key"
          class="admin-menu-item"
          :class="{ active: activeMenu === item.key }"
          @click="activeMenu = item.key">
          <el-icon><component :is="item.icon" /></el-icon>
          {{ item.label }}
        </div>
      </div>
    </div>
    <div class="admin-main">
      <div class="admin-header">
        <h2>{{ currentMenu?.label }}</h2>
        <div class="admin-user">
          <span>{{ adminInfo?.username }}</span>
          <el-button text @click="logout">退出</el-button>
        </div>
      </div>
      <div class="admin-content">
        <!-- 数据概览 -->
        <div v-if="activeMenu === 'dashboard'" class="dashboard">
          <div class="stat-cards">
            <div class="stat-card pink"><div class="stat-num">{{ stats.totalUsers || 0 }}</div><div>用户总数</div></div>
            <div class="stat-card"><div class="stat-num">{{ stats.totalProducts || 0 }}</div><div>商品总数</div></div>
            <div class="stat-card orange"><div class="stat-num">{{ stats.totalOrders || 0 }}</div><div>订单总数</div></div>
            <div class="stat-card green"><div class="stat-num">¥{{ stats.totalRevenue || 0 }}</div><div>总收入</div></div>
          </div>
          <div class="chart-section">
            <el-card class="chart-card">
              <template #header>订单状态分布</template>
              <div class="order-stat-grid">
                <div class="stat-item"><span>待付款</span><span>{{ stats.pendingOrders || 0 }}</span></div>
                <div class="stat-item"><span>待发货</span><span>{{ orderStatusMap['待发货'] || 0 }}</span></div>
                <div class="stat-item"><span>待收货</span><span>{{ orderStatusMap['已发货'] || 0 }}</span></div>
                <div class="stat-item"><span>已完成</span><span>{{ orderStatusMap['已完成'] || 0 }}</span></div>
                <div class="stat-item"><span>已取消</span><span>{{ orderStatusMap['已取消'] || 0 }}</span></div>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 用户管理 -->
        <div v-if="activeMenu === 'users'" class="table-page">
          <el-table :data="users" stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="nickname" label="昵称" />
            <el-table-column prop="phone" label="手机号" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column label="注册时间">
              <template #default="{ row }">{{ row.createTime?.slice(0, 10) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button text type="danger" @click="deleteUser(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 商品管理 -->
        <div v-if="activeMenu === 'products'" class="table-page">
          <div style="margin-bottom:16px">
            <el-input v-model="productKeyword" placeholder="搜索商品" style="width:200px;margin-right:10px" />
            <el-button type="primary" @click="fetchProducts">搜索</el-button>
          </div>
          <el-table :data="products" stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="name" label="商品名" />
            <el-table-column prop="price" label="价格" width="100" />
            <el-table-column prop="sales" label="销量" width="80" />
            <el-table-column prop="stock" label="库存" width="80" />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
                  {{ row.status === 'active' ? '上架' : '下架' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button text size="small" @click="toggleProduct(row)">
                  {{ row.status === 'active' ? '下架' : '上架' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 订单管理 -->
        <div v-if="activeMenu === 'orders'" class="table-page">
          <el-table :data="orderList" stripe>
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column label="用户" prop="userName" />
            <el-table-column label="总价" width="100">
              <template #default="{ row }">¥{{ row.totalAmount }}</template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="statusTag(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160">
              <template #default="{ row }">
                <el-select v-if="row.status === 0" size="small" @change="v => updateOrderStatus(row, v)" placeholder="修改状态" style="width:110px">
                  <el-option label="确认付款" :value="1" />
                  <el-option label="取消订单" :value="4" />
                </el-select>
                <el-select v-else-if="row.status === 1" size="small" @change="v => updateOrderStatus(row, v)" placeholder="发货" style="width:110px">
                  <el-option label="确认发货" :value="2" />
                </el-select>
                <span v-else style="font-size:12px;color:#999">不可操作</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { dashboardApi, userApi, productApi, orderApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const activeMenu = ref('dashboard')
const adminInfo = ref(JSON.parse(localStorage.getItem('adminInfo') || '{}'))
const stats = ref({})
const orderStatusMap = ref({})
const users = ref([])
const products = ref([])
const orderList = ref([])
const productKeyword = ref('')

const menuItems = [
  { key: 'dashboard', label: '数据概览', icon: 'DataAnalysis' },
  { key: 'users', label: '用户管理', icon: 'User' },
  { key: 'products', label: '商品管理', icon: 'Goods' },
  { key: 'orders', label: '订单管理', icon: 'List' }
]

const currentMenu = computed(() => menuItems.find(i => i.key === activeMenu.value))

onMounted(() => fetchStats())

function fetchStats() {
  dashboardApi.getStats().then(res => {
    if (res.code === 200) {
      stats.value = res.data
      orderStatusMap.value = res.data.orderStatusMap || {}
    }
  })
}

function statusText(s) {
  const map = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '已完成',
    4: '已取消',
    5: '退款中',
    6: '已退款'
  }
  return map[s] || s
}
function statusTag(s) {
  const map = {
    0: 'warning',
    1: 'primary',
    2: 'info',
    3: 'success',
    4: 'info',
    5: 'warning',
    6: 'success'
  }
  return map[s] || ''
}

async function logout() {
  localStorage.removeItem('adminInfo')
  localStorage.removeItem('adminToken')
  router.push('/admin/login')
}

async function deleteUser(row) {
  try {
    await ElMessageBox.confirm('确定删除？')
    await userApi.remove(row.id)
    ElMessage.success('已删除')
    fetchUsers()
  } catch (e) {}
}

async function toggleProduct(row) {
  try {
    const status = row.status === 'active' ? 'inactive' : 'active'
    await productApi.updateStatus(row.id, status)
    ElMessage.success('操作成功')
    fetchProducts()
  } catch (e) {}
}

async function updateOrderStatus(row, status) {
  try {
    await adminApi.updateOrderStatus(row.id, status)
    ElMessage.success('更新成功')
    fetchOrders()
  } catch (e) {
    ElMessage.error('更新失败')
  }
}

function fetchUsers() {
  userApi.getList().then(res => { if (res.code === 200) users.value = res.data || [] })
}
function fetchProducts() {
  productApi.getList({ keyword: productKeyword.value, pageSize: 100 }).then(res => {
    if (res.code === 200) products.value = res.data.records || res.data || []
  })
}
function fetchOrders() {
  adminApi.getOrders({ page: 1, size: 100 }).then(res => {
    if (res.code === 200) orderList.value = res.data?.records || res.data || []
  })
}

// Watch for menu changes to load data
import { watch } from 'vue'
watch(activeMenu, (v) => {
  if (v === 'users') fetchUsers()
  if (v === 'products') fetchProducts()
  if (v === 'orders') fetchOrders()
  if (v === 'dashboard') fetchStats()
})
</script>

<style scoped>
.admin-layout { display: flex; min-height: 100vh; background: #f5f5f5; }
.admin-sidebar { width: 220px; background: white; border-right: 1px solid #eee; }
.admin-logo { padding: 20px; font-size: 22px; font-weight: bold; color: #ff6b81; text-align: center; border-bottom: 1px solid #eee; }
.admin-menu { padding: 10px; }
.admin-menu-item { display: flex; align-items: center; gap: 8px; padding: 12px 16px; border-radius: 8px; cursor: pointer; color: #666; margin-bottom: 4px; }
.admin-menu-item:hover, .admin-menu-item.active { background: #ffe4e9; color: #ff6b81; }
.admin-main { flex: 1; display: flex; flex-direction: column; }
.admin-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 24px; background: white; border-bottom: 1px solid #eee; }
.admin-header h2 { font-size: 18px; color: #333; }
.admin-content { padding: 24px; flex: 1; }
.stat-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
.stat-card { background: white; border-radius: 12px; padding: 24px; text-align: center; }
.stat-num { font-size: 28px; font-weight: bold; color: #ff6b81; margin-bottom: 4px; }
.pink { border-top: 3px solid #ff6b81; }
.orange { border-top: 3px solid #ffa502; }
.green { border-top: 3px solid #2ed573; }
.chart-section { background: white; border-radius: 12px; padding: 20px; }
.order-stat-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.stat-item { display: flex; justify-content: space-between; padding: 8px 12px; background: #fafafa; border-radius: 8px; }
.table-page { background: white; border-radius: 12px; padding: 20px; }
</style>