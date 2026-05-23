<template>
  <div class="app-layout">
    <!-- 顶栏 -->
    <header class="app-header">
      <div class="header-inner">
        <div class="logo" @click="$router.push('/')">
          <span class="logo-icon">🌸</span>
          <span class="logo-text">花语轩</span>
        </div>
        <nav class="nav-menu">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/products" class="nav-link">全部鲜花</router-link>
          <router-link to="/festival" class="nav-link">节日专区</router-link>
          <router-link to="/gift" class="nav-link">送礼场景</router-link>
        </nav>
        <div class="header-actions">
          <template v-if="userStore.token">
            <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
              <el-button text @click="$router.push('/cart')" class="action-btn">
                <el-icon style="font-size:20px"><ShoppingCart /></el-icon>
                <span>购物车</span>
              </el-button>
            </el-badge>
            <el-dropdown>
              <el-button text class="action-btn">
                <el-icon style="font-size:20px"><User /></el-icon>
                <span>{{ userStore.userInfo?.nickname || userStore.userInfo?.phone }}</span>
                <el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/orders')">我的订单</el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button text @click="$router.push('/login')">登录</el-button>
            <el-button type="danger" @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>

    <!-- 主体 -->
    <main class="app-main">
      <router-view />
    </main>

    <!-- AI悬浮助手 -->
    <AiAssistant />

    <!-- 底部 -->
    <footer class="app-footer">
      <div class="footer-content">
        <div class="footer-section">
          <h4>🌸 花语轩</h4>
          <p>用心传递每一份花香，让爱在花间绽放</p>
        </div>
        <div class="footer-section">
          <h4>快速链接</h4>
          <p @click="$router.push('/products')">全部鲜花</p>
          <p @click="$router.push('/festival')">节日专区</p>
          <p @click="$router.push('/gift')">送礼场景</p>
        </div>
        <div class="footer-section">
          <h4>联系我们</h4>
          <p>客服电话：400-888-8888</p>
          <p>营业时间：9:00 - 21:00</p>
        </div>
      </div>
      <div class="footer-bottom">© 2026 花语轩鲜花商城 - 用心传递花香</div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { cartApi } from '@/api'
import { ElMessage } from 'element-plus'
import AiAssistant from '@/components/AiAssistant.vue'

const router = useRouter()
const userStore = useUserStore()
const cartCount = ref(0)
let cartTimer = null

// 获取购物车数量（仅登录用户）
async function fetchCartCount() {
  if (!userStore.token) { cartCount.value = 0; return }
  try {
    const res = await cartApi.getList()
    if (res.code === 200) {
      const items = res.data || []
      cartCount.value = items.reduce((sum, item) => sum + (item.quantity || 0), 0)
    }
  } catch (e) {}
}

onMounted(() => {
  fetchCartCount()
  cartTimer = setInterval(fetchCartCount, 30000)
})

onUnmounted(() => {
  if (cartTimer) {
    clearInterval(cartTimer)
    cartTimer = null
  }
})

// 监听token变化刷新购物车
watch(() => userStore.token, () => fetchCartCount())

function logout() {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<style scoped>
.app-layout { min-height: 100vh; display: flex; flex-direction: column; }
.app-header { background: white; border-bottom: 1px solid #f0e0e6; position: sticky; top: 0; z-index: 100; box-shadow: 0 2px 8px rgba(255,107,129,0.06); }
.header-inner { max-width: 1200px; margin: 0 auto; display: flex; align-items: center; height: 64px; padding: 0 20px; }
.logo { display: flex; align-items: center; gap: 6px; cursor: pointer; margin-right: 40px; }
.logo-icon { font-size: 28px; }
.logo-text { font-size: 22px; font-weight: bold; color: #ff6b81; }
.nav-menu { display: flex; gap: 4px; flex: 1; }
.nav-link { padding: 8px 16px; border-radius: 8px; color: #666; text-decoration: none; font-size: 15px; transition: all 0.2s; }
.nav-link:hover, .nav-link.router-link-active { background: #ffe4e9; color: #ff6b81; }
.header-actions { display: flex; align-items: center; gap: 8px; }
.action-btn { display: flex; align-items: center; gap: 4px; color: #666; }
.app-main { flex: 1; background: #faf5f7; }
.app-footer { background: white; border-top: 1px solid #f0e0e6; padding: 40px 0 0; margin-top: 40px; }
.footer-content { max-width: 1200px; margin: 0 auto; display: grid; grid-template-columns: repeat(3, 1fr); gap: 40px; padding: 0 20px; }
.footer-section h4 { font-size: 16px; color: #ff6b81; margin-bottom: 12px; }
.footer-section p { color: #888; font-size: 14px; line-height: 2; cursor: default; }
.footer-section p:not(:first-of-type) { cursor: pointer; }
.footer-section p:not(:first-of-type):hover { color: #ff6b81; }
.footer-bottom { text-align: center; padding: 20px; color: #aaa; font-size: 13px; border-top: 1px solid #f0e0e6; margin-top: 30px; }
.cart-badge { margin-right: 4px; }
</style>