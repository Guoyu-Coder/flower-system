<template>
  <div class="page-container">
    <el-skeleton :loading="loading" animated>
      <div class="product-detail">
        <div class="product-gallery">
          <img :src="product.coverImage || 'https://picsum.photos/500/500?random=' + product.id" :alt="product.name" />
        </div>
        <div class="product-info-section">
          <div class="product-tags">
            <el-tag v-if="product.isNew" type="danger" effect="dark" round>新品</el-tag>
            <el-tag v-if="product.isHot" type="warning" effect="dark" round>热卖</el-tag>
          </div>
          <h1 class="product-name">{{ product.name }}</h1>
          <p class="product-brief">{{ product.description }}</p>
          <div class="product-price">
            <span class="current-price">¥{{ product.discountPrice || product.price }}</span>
            <span v-if="product.discountPrice" class="old-price">¥{{ product.price }}</span>
          </div>
          <div class="product-sales-info">
            <span>已售 {{ product.sales || 0 }} 笔</span>
            <span>库存 {{ product.stock || 0 }} 件</span>
          </div>

          <div class="product-actions">
            <div class="quantity-selector">
              <el-button :icon="Minus" circle size="small" @click="changeQty(-1)" :disabled="quantity <= 1" />
              <span class="qty">{{ quantity }}</span>
              <el-button :icon="Plus" circle size="small" @click="changeQty(1)" :disabled="quantity >= (product.stock || 99)" />
            </div>
            <div class="action-buttons">
              <el-button type="primary" size="large" round :icon="ShoppingCart" @click="addToCart" :loading="cartLoading">
                加入购物车
              </el-button>
              <el-button size="large" round type="danger" @click="buyNow" :loading="buyLoading">
                立即购买
              </el-button>
              <el-button circle size="large" @click="toggleFavorite" :type="isFavorited ? 'danger' : 'default'">
                <el-icon><StarFilled v-if="isFavorited" /><Star v-else /></el-icon>
              </el-button>
            </div>
          </div>

          <div class="product-extra">
            <div class="extra-item">
              <el-icon><Van /></el-icon>
              <span>满199包邮</span>
            </div>
            <div class="extra-item">
              <el-icon><Clock /></el-icon>
              <span>3小时极速配送</span>
            </div>
            <div class="extra-item">
              <el-icon><CircleCheck /></el-icon>
              <span>支持7天退换</span>
            </div>
          </div>
        </div>
      </div>
    </el-skeleton>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { productApi, cartApi, favoriteApi } from '@/api'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Minus, Plus, ShoppingCart, Star, StarFilled, Van, Clock, CircleCheck } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const product = ref({})
const loading = ref(true)
const quantity = ref(1)
const cartLoading = ref(false)
const buyLoading = ref(false)
const isFavorited = ref(false)

onMounted(async () => {
  await fetchProduct()
  if (userStore.isLoggedIn) {
    checkFavorite()
  }
})

async function fetchProduct() {
  try {
    const res = await productApi.getDetail(route.params.id)
    if (res.code === 200) product.value = res.data
  } catch (e) {}
  loading.value = false
}

async function checkFavorite() {
  try {
    const res = await favoriteApi.check(product.value.id)
    if (res.code === 200) isFavorited.value = res.data
  } catch (e) {}
}

function changeQty(delta) {
  quantity.value = Math.max(1, Math.min(quantity.value + delta, product.value.stock || 99))
}

async function addToCart() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  cartLoading.value = true
  try {
    const res = await cartApi.add({ productId: product.value.id, quantity: quantity.value })
    if (res.code === 200) {
      ElMessage.success('已加入购物车')
    }
  } catch (e) {
    ElMessage.error('添加失败')
  }
  cartLoading.value = false
}

async function buyNow() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  buyLoading.value = true
  try {
    const res = await cartApi.add({ productId: product.value.id, quantity: quantity.value })
    if (res.code === 200) {
      router.push('/create-order')
    }
  } catch (e) {}
  buyLoading.value = false
}

async function toggleFavorite() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    if (isFavorited.value) {
      await favoriteApi.remove(product.value.id)
      isFavorited.value = false
      ElMessage.success('已取消收藏')
    } else {
      await favoriteApi.add(product.value.id)
      isFavorited.value = true
      ElMessage.success('已收藏')
    }
  } catch (e) {}
}
</script>

<style scoped>
.product-detail { display: flex; gap: 50px; padding: 30px 0; }
.product-gallery { flex: 1; max-width: 500px; border-radius: 16px; overflow: hidden; }
.product-gallery img { width: 100%; border-radius: 16px; box-shadow: 0 4px 20px rgba(0,0,0,0.08); }
.product-info-section { flex: 1; }
.product-tags { display: flex; gap: 8px; margin-bottom: 12px; }
.product-name { font-size: 28px; color: #333; margin-bottom: 10px; }
.product-brief { font-size: 16px; color: #999; margin-bottom: 20px; }
.product-price { margin-bottom: 20px; }
.current-price { font-size: 32px; color: #ff4757; font-weight: bold; }
.old-price { font-size: 18px; color: #ccc; text-decoration: line-through; margin-left: 12px; }
.product-sales-info { display: flex; gap: 30px; font-size: 14px; color: #999; margin-bottom: 24px; }
.quantity-selector { display: flex; align-items: center; gap: 16px; margin-bottom: 20px; }
.qty { font-size: 20px; width: 40px; text-align: center; }
.action-buttons { display: flex; gap: 12px; align-items: center; }
.product-extra { display: flex; gap: 30px; margin-top: 30px; padding-top: 24px; border-top: 1px solid #f0f0f0; }
.extra-item { display: flex; align-items: center; gap: 6px; font-size: 14px; color: #666; }
</style>