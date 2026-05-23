<template>
  <div class="page-container">
    <h2 class="page-title">👤 个人中心</h2>
    <div class="profile-layout">
      <div class="profile-sidebar">
        <div class="user-card">
          <div class="user-avatar">{{ userInfo?.nickname?.charAt(0) || 'U' }}</div>
          <h3>{{ userInfo?.nickname || '未登录' }}</h3>
          <p>{{ userInfo?.phone || '' }}</p>
        </div>
        <div class="sidebar-menu">
          <div class="menu-item" :class="{ active: activeMenu === 'info' }" @click="activeMenu = 'info'">
            <el-icon><User /></el-icon> 个人资料
          </div>
          <div class="menu-item" :class="{ active: activeMenu === 'address' }" @click="activeMenu = 'address'">
            <el-icon><Location /></el-icon> 收货地址
          </div>
          <div class="menu-item" :class="{ active: activeMenu === 'favorites' }" @click="activeMenu = 'favorites'">
            <el-icon><Star /></el-icon> 我的收藏
          </div>
          <div class="menu-item" :class="{ active: activeMenu === 'orders' }" @click="activeMenu = 'orders'">
            <el-icon><List /></el-icon> 我的订单
          </div>
          <div class="menu-item logout" @click="handleLogout">
            <el-icon><LogOut /></el-icon> 退出登录
          </div>
        </div>
      </div>
      <div class="profile-content">
        <!-- 个人资料 -->
        <div v-if="activeMenu === 'info'" class="content-card">
          <h3>个人资料</h3>
          <el-form :model="profileForm" label-width="80px">
            <el-form-item label="昵称">
              <el-input v-model="profileForm.nickname" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="profileForm.phone" disabled />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profileForm.email" />
            </el-form-item>
            <el-form-item label="头像">
              <el-upload :auto-upload="false" :show-file-list="false" @change="handleAvatar">
                <el-avatar :size="80" style="background:#ffe4e9;color:#ff6b81;font-size:32px;cursor:pointer">
                  {{ profileForm.nickname?.charAt(0) || 'U' }}
                </el-avatar>
              </el-upload>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile" :loading="saving">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 收货地址 -->
        <div v-if="activeMenu === 'address'" class="content-card">
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
            <h3>收货地址</h3>
            <el-button type="primary" size="small" @click="showAddressDialog = true">新增地址</el-button>
          </div>
          <div v-for="addr in addresses" :key="addr.id" class="address-card">
            <div class="addr-main">
              <span class="addr-name">{{ addr.name }}</span>
              <span class="addr-phone">{{ addr.phone }}</span>
              <el-tag v-if="addr.isDefault" size="small" type="danger" style="margin-left:8px">默认</el-tag>
            </div>
            <p class="addr-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</p>
            <div class="addr-actions">
              <el-button text size="small" @click="setDefault(addr)" v-if="!addr.isDefault">设为默认</el-button>
              <el-button text size="small" type="primary" @click="editAddress(addr)">编辑</el-button>
              <el-button text size="small" type="danger" @click="deleteAddress(addr)">删除</el-button>
            </div>
          </div>
          <el-empty v-if="!addresses.length" description="暂无地址" />
        </div>

        <!-- 我的收藏 -->
        <div v-if="activeMenu === 'favorites'" class="content-card">
          <h3>我的收藏</h3>
          <div class="favorites-grid">
            <div v-for="fav in favorites" :key="fav.id" class="fav-item" @click="$router.push('/product/' + fav.productId)">
              <img :src="fav.coverImage || 'https://picsum.photos/200/200?random=' + fav.productId" />
              <h4>{{ fav.productName }}</h4>
              <div class="fav-price">¥{{ fav.price }}</div>
              <el-button text type="danger" size="small" @click.stop="removeFavorite(fav)">取消收藏</el-button>
            </div>
          </div>
          <el-empty v-if="!favorites.length" description="暂无收藏" />
        </div>

        <!-- 订单跳转 -->
        <div v-if="activeMenu === 'orders'" class="content-card">
          <h3>我的订单</h3>
          <p style="color:#999;margin-bottom:16px">查看和管理您的所有订单</p>
          <el-button type="primary" @click="$router.push('/orders')">查看订单</el-button>
        </div>
      </div>
    </div>
    <AddressDialog v-model:visible="showAddressDialog" :address="editingAddress" @success="fetchAddresses" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { addressApi, favoriteApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Location, Star, List, LogOut } from '@element-plus/icons-vue'
import AddressDialog from '@/components/AddressDialog.vue'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo || {})
const activeMenu = ref('info')
const saving = ref(false)
const profileForm = ref({ nickname: userInfo.value.nickname || '', phone: userInfo.value.phone || '', email: userInfo.value.email || '' })
const addresses = ref([])
const favorites = ref([])
const showAddressDialog = ref(false)
const editingAddress = ref(null)

onMounted(() => {
  fetchAddresses()
  fetchFavorites()
})

async function saveProfile() {
  saving.value = true
  try {
    await userStore.updateProfile(profileForm.value)
    ElMessage.success('保存成功')
  } catch (e) {
    ElMessage.error('保存失败')
  }
  saving.value = false
}

async function fetchAddresses() {
  try {
    const res = await addressApi.getList()
    if (res.code === 200) addresses.value = res.data || []
  } catch (e) {}
}

async function fetchFavorites() {
  try {
    const res = await favoriteApi.getList()
    if (res.code === 200) favorites.value = res.data || []
  } catch (e) {}
}

function editAddress(addr) {
  editingAddress.value = addr
  showAddressDialog.value = true
}

async function deleteAddress(addr) {
  try {
    await ElMessageBox.confirm('确定删除该地址？')
    await addressApi.remove(addr.id)
    ElMessage.success('已删除')
    fetchAddresses()
  } catch (e) {}
}

async function setDefault(addr) {
  try {
    await addressApi.setDefault(addr.id)
    ElMessage.success('已设为默认')
    fetchAddresses()
  } catch (e) {}
}

async function removeFavorite(fav) {
  try {
    await favoriteApi.remove(fav.productId)
    ElMessage.success('已取消收藏')
    fetchFavorites()
  } catch (e) {}
}

function handleAvatar(file) {}

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？')
    userStore.logout()
    ElMessage.success('退出成功')
    window.location.href = '/login'
  } catch (e) {}
}
</script>

<style scoped>
.page-title { font-size: 24px; margin-bottom: 20px; }
.profile-layout { display: flex; gap: 24px; }
.profile-sidebar { width: 240px; }
.user-card { background: linear-gradient(135deg, #ff6b81, #ff4757); border-radius: 12px; padding: 30px 20px; text-align: center; color: white; margin-bottom: 12px; }
.user-avatar { width: 64px; height: 64px; border-radius: 32px; background: rgba(255,255,255,0.2); display: flex; align-items: center; justify-content: center; font-size: 28px; margin: 0 auto 12px; }
.user-card h3 { font-size: 18px; }
.user-card p { font-size: 13px; opacity: 0.8; }
.sidebar-menu { background: white; border-radius: 12px; overflow: hidden; }
.menu-item { display: flex; align-items: center; gap: 8px; padding: 14px 20px; cursor: pointer; font-size: 14px; color: #666; }
.menu-item:hover, .menu-item.active { background: #ffe4e9; color: #ff6b81; }
.menu-item.logout { color: #ff4757; border-top: 1px solid #f0f0f0; margin-top: 8px; }
.menu-item.logout:hover { background: #fff0f0; }
.profile-content { flex: 1; }
.content-card { background: white; border-radius: 12px; padding: 24px; min-height: 400px; }
.content-card h3 { font-size: 18px; color: #333; margin-bottom: 20px; }
.address-card { border: 1px solid #eee; border-radius: 8px; padding: 14px; margin-bottom: 10px; }
.addr-main { display: flex; align-items: center; margin-bottom: 6px; }
.addr-name { font-weight: bold; margin-right: 12px; }
.addr-phone { color: #999; }
.addr-detail { font-size: 14px; color: #666; }
.addr-actions { display: flex; gap: 8px; margin-top: 8px; }
.favorites-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.fav-item { background: #fafafa; border-radius: 8px; padding: 12px; text-align: center; cursor: pointer; transition: all 0.2s; }
.fav-item:hover { background: #ffe4e9; }
.fav-item img { width: 100%; height: 150px; object-fit: cover; border-radius: 8px; margin-bottom: 8px; }
.fav-item h4 { font-size: 14px; color: #333; }
.fav-price { color: #ff4757; font-weight: bold; font-size: 15px; margin: 4px 0; }
</style>