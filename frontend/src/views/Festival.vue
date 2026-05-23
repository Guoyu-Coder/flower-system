<template>
  <div class="page-container">
    <div class="festival-hero">
      <h1>🎄 节日鲜花专区</h1>
      <p>每一个节日，都有专属的鲜花来表达心意</p>
    </div>

    <div class="festival-tabs">
      <el-tabs v-model="activeFestival" @tab-change="fetchProducts">
        <el-tab-pane v-for="f in festivals" :key="f.key" :label="f.label" :name="f.key" />
      </el-tabs>
    </div>

    <div class="products-grid">
      <div v-for="p in products" :key="p.id" class="product-card" @click="$router.push('/product/' + p.id)">
        <div class="card-img-wrap">
          <img :src="p.coverImage || 'https://picsum.photos/300/300?random=' + p.id" />
          <div class="festival-tag">{{ currentFestival?.label }}</div>
        </div>
        <div class="card-body">
          <h4>{{ p.name }}</h4>
          <p class="card-desc">{{ p.description }}</p>
          <div class="card-footer">
            <span class="price">¥{{ p.price }}</span>
            <span class="sales">已售 {{ p.sales || 0 }}</span>
          </div>
        </div>
      </div>
    </div>
    <el-empty v-if="!products.length" description="暂无该节日鲜花" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { productApi } from '@/api'

const activeFestival = ref('valentine')
const products = ref([])

const festivals = [
  { key: 'valentine', label: '💕 情人节' },
  { key: 'mother', label: '🌷 母亲节' },
  { key: 'christmas', label: '🎄 圣诞节' },
  { key: 'birthday', label: '🎂 生日' },
  { key: 'teacher', label: '📚 教师节' },
  { key: 'newyear', label: '🧧 新年' }
]

const currentFestival = computed(() => festivals.find(f => f.key === activeFestival.value))

onMounted(() => fetchProducts())

async function fetchProducts() {
  try {
    const res = await productApi.getList({ festival: activeFestival.value, pageSize: 20 })
    if (res.code === 200) {
      products.value = res.data?.records || res.data || []
    }
  } catch (e) {}
}
</script>

<style scoped>
.festival-hero { text-align: center; padding: 40px 20px; background: linear-gradient(135deg, #ffe4e9, #ffb3c6); border-radius: 20px; margin-bottom: 24px; }
.festival-hero h1 { font-size: 32px; color: white; margin-bottom: 8px; }
.festival-hero p { font-size: 16px; color: rgba(255,255,255,0.8); }
.festival-tabs { background: white; border-radius: 12px; padding: 0 16px; margin-bottom: 24px; box-shadow: 0 2px 8px rgba(255,107,129,0.06); }
.products-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.product-card { background: white; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all 0.3s; box-shadow: 0 2px 8px rgba(255,107,129,0.06); }
.product-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(255,107,129,0.15); }
.card-img-wrap { position: relative; height: 200px; overflow: hidden; }
.card-img-wrap img { width: 100%; height: 100%; object-fit: cover; }
.festival-tag { position: absolute; top: 10px; left: 10px; background: #ff6b81; color: white; padding: 4px 12px; border-radius: 20px; font-size: 12px; }
.card-body { padding: 14px; }
.card-body h4 { font-size: 15px; color: #333; margin-bottom: 6px; }
.card-desc { font-size: 13px; color: #999; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.card-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
.price { color: #ff4757; font-size: 18px; font-weight: bold; }
.sales { font-size: 12px; color: #999; }
</style>