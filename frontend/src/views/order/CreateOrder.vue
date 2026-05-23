<template>
  <div class="page-container">
    <h2 class="page-title">📋 确认订单</h2>
    <div class="order-layout">
      <div class="order-main">
        <div class="section-card">
          <h3>收货地址</h3>
          <div v-if="addresses.length === 0" class="no-address">
            <p>暂无收货地址，请先添加</p>
            <el-button type="primary" @click="showAddressDialog = true">添加地址</el-button>
          </div>
          <div v-for="addr in addresses" :key="addr.id" class="address-item"
            :class="{ selected: selectedAddress === addr.id }"
            @click="selectedAddress = addr.id">
            <el-radio v-model="selectedAddress" :label="addr.id" class="addr-radio">
              <div>
                <span class="addr-name">{{ addr.name }}</span>
                <span class="addr-phone">{{ addr.phone }}</span>
                <p class="addr-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</p>
              </div>
            </el-radio>
          </div>
          <el-button text type="primary" @click="showAddressDialog = true" style="margin-top:10px">+ 新增地址</el-button>
        </div>

        <div class="section-card">
          <h3>商品信息</h3>
          <div v-for="item in items" :key="item.id" class="order-item">
            <img :src="item.productImage || item.coverImage || 'https://picsum.photos/80/80?random=' + item.productId" />
            <div class="item-detail">
              <h4>{{ item.productName }}</h4>
              <p class="item-desc">{{ item.description }}</p>
            </div>
            <div class="item-qty">×{{ item.quantity }}</div>
            <div class="item-price">¥{{ item.price }}</div>
          </div>
        </div>
      </div>
      <div class="order-sidebar">
        <div class="summary-card">
          <h3>订单摘要</h3>
          <div class="summary-row"><span>商品总额</span><span>¥{{ totalAmount }}</span></div>
          <div class="summary-row"><span>运费</span><span>¥0.00</span></div>
          <div class="summary-row total"><span>应付总额</span><span class="total-price">¥{{ totalAmount }}</span></div>
          <el-button type="danger" size="large" round @click="submitOrder" :loading="submitting" style="width:100%;margin-top:20px">
            提交订单
          </el-button>
        </div>
      </div>
    </div>
    <AddressDialog v-model:visible="showAddressDialog" @success="fetchAddresses" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'
import { cartApi, orderApi, addressApi } from '@/api'
import { ElMessage } from 'element-plus'
import AddressDialog from '@/components/AddressDialog.vue'

const router = useRouter()
const route = useRoute()
const items = ref([])
const addresses = ref([])
const selectedAddress = ref(null)
const submitting = ref(false)
const showAddressDialog = ref(false)

const totalAmount = computed(() => items.value.reduce((s, i) => s + (i.price || 0) * (i.quantity || 0), 0).toFixed(2))

onMounted(async () => {
  await fetchAddresses()
  await fetchItems()
})

async function fetchAddresses() {
  try {
    const res = await addressApi.getList()
    if (res.code === 200) {
      addresses.value = res.data || []
      const def = addresses.value.find(i => i.isDefault)
      selectedAddress.value = def ? def.id : (addresses.value[0]?.id || null)
    }
  } catch (e) {
    console.error('获取地址失败', e)
  }
}

async function fetchItems() {
  const cartIds = route.query.cartIds
  if (cartIds) {
    const ids = cartIds.split(',').map(Number)
    try {
      const res = await cartApi.getByIds(ids)
      if (res.code === 200) items.value = res.data || []
    } catch (e) {
      console.error('获取商品失败', e)
    }
  } else {
    try {
      const res = await cartApi.getSelected()
      if (res.code === 200) items.value = res.data || []
    } catch (e) {
      console.error('获取选中商品失败', e)
    }
  }
}

async function submitOrder() {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  if (items.value.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  submitting.value = true
  try {
    const payload = {
      addressId: selectedAddress.value,
      cartIds: route.query.cartIds?.split(',').map(Number) || []
    }
    const res = await orderApi.create(payload)
    if (res.code === 200) {
      const orderData = res.data
      ElMessage.success(`订单创建成功！订单号：${orderData.orderNo}`)
      router.push('/orders')
    } else {
      ElMessage.error(res.msg || '创建失败')
    }
  } catch (e) {
    ElMessage.error('创建失败')
  }
  submitting.value = false
}
</script>

<style scoped>
.page-title { font-size: 24px; margin-bottom: 20px; }
.order-layout { display: flex; gap: 24px; }
.order-main { flex: 1; }
.section-card { background: white; border-radius: 12px; padding: 20px; margin-bottom: 16px; box-shadow: 0 2px 8px rgba(255,107,129,0.06); }
.section-card h3 { font-size: 16px; color: #333; margin-bottom: 16px; }
.address-item { padding: 12px; border: 1px solid #eee; border-radius: 8px; margin-bottom: 8px; cursor: pointer; }
.address-item:hover, .address-item.selected { border-color: #ff6b81; background: #fff5f7; }
.addr-name { font-weight: bold; margin-right: 12px; }
.addr-phone { color: #999; }
.addr-detail { font-size: 14px; color: #666; margin-top: 4px; }
.no-address { text-align: center; padding: 20px; color: #999; }
.no-address p { margin-bottom: 10px; }
.order-item { display: flex; align-items: center; gap: 12px; padding: 12px 0; border-bottom: 1px solid #f0f0f0; }
.order-item img { width: 70px; height: 70px; border-radius: 8px; object-fit: cover; }
.item-detail { flex: 1; }
.item-detail h4 { font-size: 15px; color: #333; }
.item-desc { font-size: 13px; color: #999; }
.order-sidebar { width: 320px; }
.summary-card { background: white; border-radius: 12px; padding: 24px; box-shadow: 0 2px 8px rgba(255,107,129,0.06); position: sticky; top: 90px; }
.summary-card h3 { font-size: 16px; color: #333; margin-bottom: 16px; }
.summary-row { display: flex; justify-content: space-between; padding: 8px 0; font-size: 14px; color: #666; }
.summary-row.total { border-top: 1px solid #eee; margin-top: 10px; padding-top: 16px; font-size: 16px; }
.total-price { color: #ff4757; font-size: 22px; font-weight: bold; }
</style>