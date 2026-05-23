<template>
  <div class="page-container">
    <div class="gift-hero">
      <h1>🎁 送礼场景专区</h1>
      <p>根据对象、场景、预算，智能推荐最适合的鲜花礼盒</p>
    </div>

    <!-- 场景筛选 -->
    <div class="gift-filters">
      <div class="filter-group">
        <span class="filter-label">送礼对象：</span>
        <el-radio-group v-model="filters.target" @change="searchGifts">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="girlfriend">女朋友</el-radio-button>
          <el-radio-button value="boyfriend">男朋友</el-radio-button>
          <el-radio-button value="mother">母亲</el-radio-button>
          <el-radio-button value="friend">朋友</el-radio-button>
          <el-radio-button value="teacher">老师</el-radio-button>
          <el-radio-button value="colleague">同事</el-radio-button>
        </el-radio-group>
      </div>
      <div class="filter-group">
        <span class="filter-label">场景风格：</span>
        <el-radio-group v-model="filters.style" @change="searchGifts">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="romantic">浪漫</el-radio-button>
          <el-radio-button value="warm">温馨</el-radio-button>
          <el-radio-button value="elegant">高雅</el-radio-button>
          <el-radio-button value="fresh">清新</el-radio-button>
          <el-radio-button value="luxury">奢华</el-radio-button>
        </el-radio-group>
      </div>
      <div class="filter-group">
        <span class="filter-label">预算范围：</span>
        <el-radio-group v-model="filters.budget" @change="searchGifts">
          <el-radio-button value="">不限</el-radio-button>
          <el-radio-button value="0-99">¥100以下</el-radio-button>
          <el-radio-button value="100-199">¥100-199</el-radio-button>
          <el-radio-button value="200-399">¥200-399</el-radio-button>
          <el-radio-button value="400-999">¥400-999</el-radio-button>
          <el-radio-button value="1000+">¥1000+</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- AI推荐横幅 -->
    <div class="ai-tip" v-if="aiRecommendation">
      <div class="ai-icon">🤖</div>
      <div class="ai-text">{{ aiRecommendation }}</div>
    </div>

    <!-- 结果 -->
    <div class="products-grid">
      <div v-for="p in products" :key="p.id" class="product-card" @click="$router.push('/product/' + p.id)">
        <div class="card-img-wrap">
          <img :src="p.coverImage || 'https://picsum.photos/300/300?random=' + (p.id + 100)" />
          <div class="scene-tags">
            <span v-for="tag in (p.tags || '浪漫').split(',')" :key="tag" class="scene-tag">{{ tag }}</span>
          </div>
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
    <el-empty v-if="!products.length" description="暂无匹配商品，试试其他条件" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { productApi } from '@/api'

const filters = ref({ target: '', style: '', budget: '' })
const products = ref([])
const aiRecommendation = ref('')

onMounted(() => searchGifts())

async function searchGifts() {
  const params = { pageSize: 30 }
  if (filters.value.target) params.target = filters.value.target
  if (filters.value.style) params.style = filters.value.style
  if (filters.value.budget) {
    const [min, max] = filters.value.budget.split('-')
    if (min) params.minPrice = parseInt(min)
    if (max) params.maxPrice = parseInt(max)
    if (filters.value.budget === '1000+') params.minPrice = 1000
  }
  try {
    const res = await productApi.getList(params)
    if (res.code === 200) {
      products.value = res.data?.records || res.data || []
    }
    // AI推荐建议
    const targetMap = { girlfriend: '女朋友', mother: '妈妈', friend: '朋友', teacher: '老师', colleague: '同事' }
    const styleMap = { romantic: '浪漫', warm: '温馨', elegant: '高雅', fresh: '清新', luxury: '奢华' }
    const targetText = targetMap[filters.value.target] || '对方'
    const styleText = styleMap[filters.value.style] || ''
    if (filters.value.target) {
      aiRecommendation.value = `💡 送给${targetText}${styleText ? '，推荐' + styleText + '风格' : ''}，以下鲜花非常适合表达心意哦！`
    } else {
      aiRecommendation.value = ''
    }
  } catch (e) {}
}
</script>

<style scoped>
.gift-hero { text-align: center; padding: 40px 20px; background: linear-gradient(135deg, #fce4ec, #f8bbd0); border-radius: 20px; margin-bottom: 24px; }
.gift-hero h1 { font-size: 32px; color: #c62828; margin-bottom: 8px; }
.gift-hero p { font-size: 16px; color: rgba(198,40,40,0.7); }
.gift-filters { background: white; border-radius: 12px; padding: 16px 20px; margin-bottom: 16px; box-shadow: 0 2px 8px rgba(255,107,129,0.06); }
.filter-group { margin-bottom: 12px; display: flex; align-items: center; gap: 10px; }
.filter-group:last-child { margin-bottom: 0; }
.filter-label { font-size: 14px; color: #666; white-space: nowrap; min-width: 80px; }
.ai-tip { background: #fff3e0; border: 1px solid #ffcc80; border-radius: 12px; padding: 12px 16px; display: flex; align-items: center; gap: 10px; margin-bottom: 16px; }
.ai-icon { font-size: 24px; }
.ai-text { font-size: 14px; color: #e65100; }
.products-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.product-card { background: white; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all 0.3s; box-shadow: 0 2px 8px rgba(255,107,129,0.06); }
.product-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(255,107,129,0.15); }
.card-img-wrap { position: relative; height: 200px; overflow: hidden; }
.card-img-wrap img { width: 100%; height: 100%; object-fit: cover; }
.scene-tags { position: absolute; bottom: 8px; left: 8px; display: flex; gap: 4px; flex-wrap: wrap; }
.scene-tag { background: rgba(255,107,129,0.85); color: white; padding: 2px 8px; border-radius: 10px; font-size: 11px; }
.card-body { padding: 14px; }
.card-body h4 { font-size: 15px; color: #333; margin-bottom: 6px; }
.card-desc { font-size: 13px; color: #999; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.card-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
.price { color: #ff4757; font-size: 18px; font-weight: bold; }
.sales { font-size: 12px; color: #999; }
</style>