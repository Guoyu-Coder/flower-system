<template>
  <div class="page-container">
    <div class="list-header">
      <h2>{{ categoryName }} <span class="count">共{{ total }}件商品</span></h2>
      <div class="filter-bar">
        <el-select v-model="sortBy" placeholder="排序方式" @change="fetchProducts" style="width:140px">
          <el-option label="默认排序" value="default" />
          <el-option label="销量优先" value="sales" />
          <el-option label="价格 ↑" value="price_asc" />
          <el-option label="价格 ↓" value="price_desc" />
        </el-select>
        <el-input v-model="priceMin" placeholder="最低价" style="width:100px" />
        <span class="price-sep">-</span>
        <el-input v-model="priceMax" placeholder="最高价" style="width:100px" />
        <el-button type="primary" @click="fetchProducts" :icon="Search">筛选</el-button>
      </div>
    </div>

    <div class="category-sidebar">
      <div class="sidebar-title">全部分类</div>
      <div v-for="cat in categories" :key="cat.id"
        class="sidebar-item"
        :class="{ active: cat.id === currentCategory }"
        @click="switchCategory(cat.id)">
        {{ cat.name }}
      </div>
    </div>

    <div class="product-grid">
      <div v-for="p in products" :key="p.id" class="product-card" @click="$router.push('/product/' + p.id)">
        <div class="product-image">
          <img :src="p.coverImage || 'https://picsum.photos/300/300?random=' + p.id" :alt="p.name" />
          <div class="product-tag" v-if="p.isNew">新品</div>
          <div class="product-tag hot" v-if="p.sales > 500">热卖</div>
        </div>
        <div class="product-info">
          <h3>{{ p.name }}</h3>
          <p class="product-desc">{{ p.description }}</p>
          <div class="product-meta">
            <span class="price">¥{{ p.discountPrice || p.price }}</span>
            <span v-if="p.discountPrice" class="original-price">¥{{ p.price }}</span>
          </div>
          <div class="product-sales">已售 {{ p.sales || 0 }} 笔</div>
        </div>
      </div>
      <el-empty v-if="!products.length" description="暂无商品" style="grid-column:1/-1;padding:60px" />
    </div>

    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchProducts"
        background
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { productApi, categoryApi } from '@/api'
import { Search } from '@element-plus/icons-vue'

const route = useRoute()
const products = ref([])
const categories = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 12
const sortBy = ref('default')
const priceMin = ref('')
const priceMax = ref('')
const currentCategory = ref(null)
const categoryName = ref('全部鲜花')
const keyword = ref('')

onMounted(async () => {
  await fetchCategories()
  if (route.params.id) {
    currentCategory.value = Number(route.params.id)
    const cat = categories.value.find(c => c.id === currentCategory.value)
    categoryName.value = cat ? cat.name : '全部分类'
  }
  if (route.query.keyword) {
    keyword.value = route.query.keyword
    categoryName.value = `搜索"${keyword.value}"`
  }
  fetchProducts()
})

watch(() => route.query, (q) => {
  if (q.keyword !== undefined) {
    keyword.value = q.keyword
    categoryName.value = `搜索"${q.keyword}"`
    page.value = 1
    fetchProducts()
  }
})

async function fetchCategories() {
  const res = await categoryApi.getList()
  if (res.code === 200) categories.value = res.data
}

function switchCategory(catId) {
  currentCategory.value = catId
  categoryName.value = categories.value.find(c => c.id === catId)?.name || '全部分类'
  keyword.value = ''
  page.value = 1
  fetchProducts()
}

async function fetchProducts() {
  const params = {
    page: page.value,
    pageSize,
    sort: sortBy.value,
    keyword: keyword.value,
    minPrice: priceMin.value || undefined,
    maxPrice: priceMax.value || undefined,
    categoryId: currentCategory.value || undefined
  }
  const res = await productApi.getList(params)
  if (res.code === 200) {
    products.value = res.data.records || res.data
    total.value = res.data.total || 0
  }
}
</script>

<style scoped>
.list-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.list-header h2 { font-size: 22px; color: #333; }
.count { font-size: 14px; color: #999; font-weight: normal; margin-left: 10px; }
.filter-bar { display: flex; align-items: center; gap: 10px; }
.price-sep { color: #999; }
.category-sidebar { float: left; width: 180px; background: white; border-radius: 12px; padding: 16px; margin-right: 24px; box-shadow: 0 2px 8px rgba(255,107,129,0.08); }
.sidebar-title { font-size: 16px; font-weight: bold; color: #333; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 2px solid #ffe4e9; }
.sidebar-item { padding: 8px 12px; cursor: pointer; border-radius: 8px; font-size: 14px; color: #666; margin-bottom: 4px; }
.sidebar-item:hover, .sidebar-item.active { background: #ffe4e9; color: #ff6b81; }
.product-grid { margin-left: 204px; display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.product-card { background: white; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all 0.3s; box-shadow: 0 2px 8px rgba(255,107,129,0.08); }
.product-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(255,107,129,0.15); }
.product-image { position: relative; height: 220px; overflow: hidden; }
.product-image img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.product-card:hover .product-image img { transform: scale(1.1); }
.product-tag { position: absolute; top: 10px; left: 10px; background: linear-gradient(135deg, #ff6b81, #ff4757); color: white; padding: 3px 10px; border-radius: 20px; font-size: 12px; }
.product-tag.hot { background: linear-gradient(135deg, #ffd700, #ffa502); }
.product-info { padding: 14px; }
.product-info h3 { font-size: 15px; color: #333; margin-bottom: 4px; }
.product-desc { font-size: 13px; color: #999; margin-bottom: 8px; }
.product-sales { font-size: 12px; color: #ccc; margin-top: 4px; }
.pagination-wrap { margin-left: 204px; display: flex; justify-content: center; margin-top: 30px; }
</style>