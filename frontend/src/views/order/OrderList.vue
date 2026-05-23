<template>
  <div class="page-container">
    <h2 class="page-title">📦 我的订单</h2>
    <div class="order-tabs">
      <el-tabs v-model="activeTab" @tab-change="fetchOrders">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待付款" name="pending" />
        <el-tab-pane label="待发货" name="processing" />
        <el-tab-pane label="待收货" name="shipped" />
        <el-tab-pane label="已完成" name="completed" />
        <el-tab-pane label="已取消" name="cancelled" />
      </el-tabs>
    </div>

    <div v-for="order in orders" :key="order.id" class="order-card" @click="$router.push('/order/' + order.id)">
      <div class="order-header">
        <span class="order-no">订单号：{{ order.orderNo }}</span>
        <el-tag :type="statusTag(order.status)" size="small">{{ statusText(order.status) }}</el-tag>
      </div>
      <div v-for="item in order.orderItems" :key="item.id" class="order-product">
        <img :src="item.productImage || 'https://picsum.photos/80/80?random=' + item.productId" />
        <div class="item-info">
          <h4>{{ item.productName }}</h4>
          <p>¥{{ item.price }} × {{ item.quantity }}</p>
        </div>
        <div class="item-total">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
      </div>
      <div class="order-footer">
        <span>共{{ order.orderItems?.length || 0 }}件商品 合计：<b class="total-price">¥{{ order.payAmount?.toFixed(2) || '0.00' }}</b></span>
        <div class="order-actions">
          <el-button v-if="order.status === 0" size="small" type="primary" @click.stop="payOrder(order)">去支付</el-button>
          <el-button v-if="order.status === 0" size="small" @click.stop="cancelOrder(order)">取消</el-button>
          <el-button v-if="order.status === 2" size="small" type="success" @click.stop="confirmReceive(order)">确认收货</el-button>
        </div>
      </div>
    </div>
    <el-empty v-if="!orders.length" description="暂无订单" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const orders = ref([])
const activeTab = ref('all')

onMounted(() => fetchOrders())

async function fetchOrders() {
  try {
    let status = undefined
    if (activeTab.value === 'pending') status = 0
    else if (activeTab.value === 'processing') status = 1
    else if (activeTab.value === 'shipped') status = 2
    else if (activeTab.value === 'completed') status = 3
    else if (activeTab.value === 'cancelled') status = 4

    console.log('请求订单列表 - status:', status)
    const res = await orderApi.getList({ status })
    console.log('订单列表响应:', res)
    
    if (res.code === 200) {
      if (res.data?.records) {
        orders.value = res.data.records
      } else if (Array.isArray(res.data)) {
        orders.value = res.data
      } else {
        orders.value = []
      }
      console.log('解析后的订单列表:', orders.value)
    }
  } catch (e) {
    console.error('获取订单列表失败', e)
  }
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

async function payOrder(order) {
  try {
    const res = await orderApi.pay(order.id)
    if (res.code === 200) {
      ElMessage.success('支付成功！')
      fetchOrders()
    } else {
      ElMessage.error(res.msg || '支付失败')
    }
  } catch (e) {
    ElMessage.error('支付失败')
  }
}

async function cancelOrder(order) {
  try {
    await ElMessageBox.confirm('确定取消该订单？')
    const res = await orderApi.cancel(order.id)
    if (res.code === 200) {
      ElMessage.success('已取消')
      fetchOrders()
    } else {
      ElMessage.error(res.msg || '取消失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

async function confirmReceive(order) {
  try {
    await ElMessageBox.confirm('确认已收到商品？')
    const res = await orderApi.confirm(order.id)
    if (res.code === 200) {
      ElMessage.success('确认收货成功')
      fetchOrders()
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
.order-tabs { margin-bottom: 20px; background: white; border-radius: 12px; padding: 0 16px; box-shadow: 0 2px 8px rgba(255,107,129,0.06); }
.order-card { background: white; border-radius: 12px; padding: 16px 20px; margin-bottom: 12px; cursor: pointer; box-shadow: 0 2px 8px rgba(255,107,129,0.06); transition: all 0.2s; }
.order-card:hover { box-shadow: 0 4px 16px rgba(255,107,129,0.12); }
.order-header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 10px; border-bottom: 1px solid #f0f0f0; margin-bottom: 10px; }
.order-no { font-size: 13px; color: #999; }
.order-product { display: flex; align-items: center; gap: 12px; padding: 8px 0; }
.order-product img { width: 60px; height: 60px; border-radius: 8px; object-fit: cover; }
.item-info { flex: 1; }
.item-info h4 { font-size: 14px; color: #333; }
.item-info p { font-size: 13px; color: #999; }
.item-total { font-size: 15px; color: #333; }
.order-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 10px; border-top: 1px solid #f0f0f0; margin-top: 8px; font-size: 13px; color: #999; }
.total-price { font-size: 16px; color: #ff4757; }
.order-actions { display: flex; gap: 8px; }
</style>