<template>
  <div class="page-container">
    <h2 class="page-title">📋 订单详情</h2>
    
    <!-- AI祝福弹窗 -->
    <Transition name="blessing">
      <div v-if="showBlessing" class="blessing-overlay" @click="showBlessing = false">
        <div class="blessing-card" @click.stop>
          <div class="blessing-emoji">{{ blessingEmoji }}</div>
          <div class="blessing-text">{{ blessingMessage }}</div>
          <el-button type="primary" @click="showBlessing = false">好的</el-button>
        </div>
      </div>
    </Transition>
    
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="order" class="order-detail">
      <div class="detail-card">
        <div class="detail-header">
          <h3>订单信息</h3>
          <el-tag :type="statusTag(order.status)" size="large">{{ statusText(order.status) }}</el-tag>
        </div>
        <div class="detail-row">
          <span class="label">订单编号：</span>
          <span class="value">{{ order.orderNo }}</span>
        </div>
        <div class="detail-row">
          <span class="label">下单时间：</span>
          <span class="value">{{ formatDate(order.createdAt) }}</span>
        </div>
        <div v-if="order.payTime" class="detail-row">
          <span class="label">支付时间：</span>
          <span class="value">{{ formatDate(order.payTime) }}</span>
        </div>
        <div v-if="order.deliveryTime" class="detail-row">
          <span class="label">发货时间：</span>
          <span class="value">{{ formatDate(order.deliveryTime) }}</span>
        </div>
        <div v-if="order.receivedTime" class="detail-row">
          <span class="label">收货时间：</span>
          <span class="value">{{ formatDate(order.receivedTime) }}</span>
        </div>
        <div v-if="order.remark" class="detail-row">
          <span class="label">订单备注：</span>
          <span class="value">{{ order.remark }}</span>
        </div>
      </div>

      <div class="detail-card">
        <div class="detail-header">
          <h3>收货信息</h3>
        </div>
        <div class="detail-row">
          <span class="label">收货人：</span>
          <span class="value">{{ order.receiverName }}</span>
        </div>
        <div class="detail-row">
          <span class="label">联系电话：</span>
          <span class="value">{{ order.receiverPhone }}</span>
        </div>
        <div class="detail-row">
          <span class="label">收货地址：</span>
          <span class="value">{{ order.receiverAddress }}</span>
        </div>
      </div>

      <div class="detail-card">
        <div class="detail-header">
          <h3>商品信息</h3>
        </div>
        <div v-for="item in order.orderItems" :key="item.id" class="product-item">
          <img :src="item.productImage || 'https://picsum.photos/80/80?random=' + item.productId" />
          <div class="product-info">
            <h4>{{ item.productName }}</h4>
            <p class="product-price">¥{{ item.price }} × {{ item.quantity }}</p>
          </div>
          <div class="product-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
        </div>
      </div>

      <div class="detail-card">
        <div class="detail-header">
          <h3>费用明细</h3>
        </div>
        <div class="detail-row">
          <span class="label">商品总额：</span>
          <span class="value">¥{{ order.totalAmount?.toFixed(2) }}</span>
        </div>
        <div class="detail-row">
          <span class="label">优惠金额：</span>
          <span class="value">-¥{{ order.discountAmount?.toFixed(2) || '0.00' }}</span>
        </div>
        <div class="detail-row total">
          <span class="label">实付金额：</span>
          <span class="value total-price">¥{{ order.payAmount?.toFixed(2) }}</span>
        </div>
      </div>

      <div class="action-bar">
        <el-button @click="$router.back()">返回</el-button>
        <el-button v-if="order.status === 0" type="primary" @click="payOrder">去支付</el-button>
        <el-button v-if="order.status === 0" type="danger" @click="cancelOrder">取消订单</el-button>
        <el-button v-if="order.status === 2" type="success" @click="confirmReceive">确认收货</el-button>
      </div>
    </div>
    <el-empty v-else description="订单不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const loading = ref(true)
const showBlessing = ref(false)
const blessingMessage = ref('')
const blessingEmoji = ref('🌸')

onMounted(async () => {
  await fetchOrderDetail()
})

async function fetchOrderDetail() {
  loading.value = true
  try {
    const res = await orderApi.getDetail(route.params.id)
    if (res.code === 200) {
      order.value = res.data
    } else {
      ElMessage.error(res.msg || '获取订单详情失败')
    }
  } catch (e) {
    ElMessage.error('获取订单详情失败')
  }
  loading.value = false
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function statusText(status) {
  const map = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '已完成',
    4: '已取消',
    5: '退款中',
    6: '已退款'
  }
  return map[status] || '未知状态'
}

function statusTag(status) {
  const map = {
    0: 'warning',
    1: 'primary',
    2: 'info',
    3: 'success',
    4: 'info',
    5: 'warning',
    6: 'success'
  }
  return map[status] || ''
}

async function payOrder() {
  try {
    const res = await orderApi.pay(order.value.id)
    if (res.code === 200) {
      // ✨ 显示AI祝福消息
      if (res.data && res.data.blessing) {
        blessingMessage.value = res.data.blessing
        blessingEmoji.value = getRandomEmoji()
        showBlessing.value = true
      } else {
        ElMessage.success('支付成功！')
      }
      fetchOrderDetail()
    } else {
      ElMessage.error(res.msg || '支付失败')
    }
  } catch (e) {
    ElMessage.error('支付失败')
  }
}

function getRandomEmoji() {
  const emojis = ['🌸', '💐', '🌹', '🌷', '🌺', '💖', '✨', '🎁']
  return emojis[Math.floor(Math.random() * emojis.length)]
}

async function cancelOrder() {
  try {
    await ElMessageBox.confirm('确定取消该订单？')
    const res = await orderApi.cancel(order.value.id)
    if (res.code === 200) {
      ElMessage.success('已取消')
      fetchOrderDetail()
    } else {
      ElMessage.error(res.msg || '取消失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

async function confirmReceive() {
  try {
    await ElMessageBox.confirm('确认已收到商品？')
    const res = await orderApi.confirm(order.value.id)
    if (res.code === 200) {
      ElMessage.success('确认收货成功')
      fetchOrderDetail()
    } else {
      ElMessage.error(res.msg || '确认收货失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('确认收货失败')
    }
  }
}
</script>

<style scoped>
.page-title { font-size: 24px; margin-bottom: 20px; }
.loading { text-align: center; padding: 40px; color: #999; }
.order-detail { max-width: 800px; margin: 0 auto; }
.detail-card { background: white; border-radius: 12px; padding: 20px; margin-bottom: 16px; box-shadow: 0 2px 8px rgba(255,107,129,0.06); }
.detail-header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 12px; border-bottom: 1px solid #f0f0f0; margin-bottom: 16px; }
.detail-header h3 { font-size: 16px; color: #333; margin: 0; }
.detail-row { display: flex; padding: 8px 0; font-size: 14px; }
.detail-row.total { border-top: 1px solid #eee; margin-top: 12px; padding-top: 16px; font-size: 16px; font-weight: bold; }
.label { color: #666; min-width: 100px; }
.value { color: #333; }
.total-price { color: #ff4757; font-size: 20px; }
.product-item { display: flex; align-items: center; gap: 12px; padding: 12px 0; border-bottom: 1px solid #f0f0f0; }
.product-item:last-child { border-bottom: none; }
.product-item img { width: 70px; height: 70px; border-radius: 8px; object-fit: cover; }
.product-info { flex: 1; }
.product-info h4 { font-size: 15px; color: #333; margin: 0 0 4px 0; }
.product-price { font-size: 13px; color: #999; margin: 0; }
.product-subtotal { font-size: 15px; color: #333; font-weight: bold; }
.action-bar { display: flex; gap: 12px; justify-content: flex-end; margin-top: 20px; }

/* AI祝福弹窗样式 */
.blessing-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(5px);
}

.blessing-card {
  background: linear-gradient(135deg, #fff5f7, #fff);
  border: 2px solid #ffe4e9;
  border-radius: 20px;
  padding: 40px 50px;
  text-align: center;
  box-shadow: 0 10px 40px rgba(255, 107, 129, 0.2);
  max-width: 400px;
  animation: float 0.5s ease-out;
}

.blessing-emoji {
  font-size: 64px;
  margin-bottom: 20px;
  animation: bounce 1s ease infinite;
}

.blessing-text {
  font-size: 18px;
  color: #333;
  line-height: 1.8;
  margin-bottom: 24px;
}

@keyframes float {
  from {
    transform: scale(0.8) translateY(20px);
    opacity: 0;
  }
  to {
    transform: scale(1) translateY(0);
    opacity: 1;
  }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.blessing-enter-active,
.blessing-leave-active {
  transition: all 0.3s ease;
}

.blessing-enter-from,
.blessing-leave-to {
  opacity: 0;
}

.blessing-enter-from .blessing-card,
.blessing-leave-to .blessing-card {
  transform: scale(0.8);
}
</style>
