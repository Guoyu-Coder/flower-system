<template>
  <div class="page-container">
    <h2 class="page-title">🛒 购物车</h2>
    <div class="cart-content" v-if="cartItems.length > 0">
      <div class="cart-header">
        <el-checkbox v-model="allChecked" @change="toggleAll">全选</el-checkbox>
        <span>商品信息</span>
        <span>单价</span>
        <span>数量</span>
        <span>小计</span>
        <span>操作</span>
      </div>
      <div class="cart-items">
        <div v-for="item in cartItems" :key="item.id" class="cart-item">
          <el-checkbox v-model="item.checked" @change="updateTotal" />
          <div class="item-info" @click="$router.push('/product/' + item.productId)">
            <img :src="item.productImage || item.coverImage || 'https://picsum.photos/100/100?random=' + item.productId" />
            <div>
              <h4>{{ item.productName }}</h4>
              <p>{{ item.description }}</p>
            </div>
          </div>
          <div class="item-price">¥{{ item.price }}</div>
          <div class="item-qty">
            <el-button :icon="Minus" circle size="small" @click="updateQuantity(item, -1)" :disabled="item.quantity <= 1" />
            <span class="qty-num">{{ item.quantity }}</span>
            <el-button :icon="Plus" circle size="small" @click="updateQuantity(item, 1)" />
          </div>
          <div class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
          <div class="item-actions">
            <el-button text type="danger" @click="removeItem(item)">删除</el-button>
          </div>
        </div>
      </div>
      <div class="cart-footer">
        <el-button text @click="removeSelected" :disabled="!hasChecked">删除选中</el-button>
        <div class="cart-summary">
          <span>已选 <b>{{ checkedCount }}</b> 件，合计：<b class="total-price">¥{{ totalAmount }}</b></span>
          <el-button type="danger" size="large" round @click="checkout" :disabled="!hasChecked">结算</el-button>
        </div>
      </div>
    </div>
    <el-empty v-else description="购物车还是空的，快去逛逛吧~" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { cartApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Minus, Plus } from '@element-plus/icons-vue'

const router = useRouter()
const cartItems = ref([])

onMounted(() => { fetchCart() })

async function fetchCart() {
  try {
    const res = await cartApi.getList()
    if (res.code === 200) {
      cartItems.value = (res.data || []).map(i => ({ ...i, checked: false }))
    }
  } catch (e) {}
}

const allChecked = computed({
  get: () => cartItems.value.length > 0 && cartItems.value.every(i => i.checked),
  set: () => {}
})

function toggleAll(val) {
  cartItems.value.forEach(i => i.checked = val)
}

const hasChecked = computed(() => cartItems.value.some(i => i.checked))
const checkedCount = computed(() => cartItems.value.filter(i => i.checked).length)
const totalAmount = computed(() => cartItems.value.filter(i => i.checked).reduce((s, i) => s + i.price * i.quantity, 0).toFixed(2))

function updateTotal() {}

async function updateQuantity(item, delta) {
  const newQty = item.quantity + delta
  if (newQty < 1) return
  try {
    await cartApi.update({ id: item.id, quantity: newQty })
    item.quantity = newQty
  } catch (e) {}
}

async function removeItem(item) {
  try {
    await cartApi.delete(item.id)
    cartItems.value = cartItems.value.filter(i => i.id !== item.id)
    ElMessage.success('已删除')
  } catch (e) {}
}

async function removeSelected() {
  const ids = cartItems.value.filter(i => i.checked).map(i => i.id)
  for (const id of ids) {
    await cartApi.delete(id)
  }
  cartItems.value = cartItems.value.filter(i => !i.checked)
  ElMessage.success('已删除选中')
}

function checkout() {
  const ids = cartItems.value.filter(i => i.checked).map(i => i.id)
  router.push({ path: '/create-order', query: { cartIds: ids.join(',') } })
}
</script>

<style scoped>
.page-title { font-size: 24px; margin-bottom: 20px; color: #333; }
.cart-header { display: grid; grid-template-columns: 40px 3fr 1fr 1fr 1fr 80px; align-items: center; padding: 12px 16px; background: #fafafa; border-radius: 12px; font-size: 14px; color: #666; }
.cart-item { display: grid; grid-template-columns: 40px 3fr 1fr 1fr 1fr 80px; align-items: center; padding: 20px 16px; background: white; border-bottom: 1px solid #f0f0f0; }
.item-info { display: flex; align-items: center; gap: 12px; cursor: pointer; }
.item-info img { width: 80px; height: 80px; border-radius: 8px; object-fit: cover; }
.item-info h4 { font-size: 15px; color: #333; margin-bottom: 4px; }
.item-info p { font-size: 13px; color: #999; }
.item-qty { display: flex; align-items: center; gap: 8px; }
.qty-num { font-size: 16px; width: 30px; text-align: center; }
.cart-footer { display: flex; justify-content: space-between; align-items: center; padding: 16px; background: white; border-radius: 12px; margin-top: 16px; }
.cart-summary { display: flex; align-items: center; gap: 20px; font-size: 16px; }
.total-price { font-size: 22px; color: #ff4757; }
</style>