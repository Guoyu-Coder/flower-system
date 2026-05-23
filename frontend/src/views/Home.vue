<template>
  <div class="home">
    <!-- 轮播横幅 -->
    <div class="banner-section">
      <el-carousel :interval="4000" height="420px" indicator-position="inside">
        <el-carousel-item v-for="(banner, idx) in banners" :key="idx">
          <div class="banner-item" :style="{ background: banner.bg }">
            <div class="banner-content">
              <h2>{{ banner.title }}</h2>
              <p>{{ banner.subtitle }}</p>
              <el-button type="primary" round size="large" @click="$router.push('/products')">
                立即选购
              </el-button>
            </div>
            <div class="banner-emoji">{{ banner.emoji }}</div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="page-container">
      <!-- 分类快捷入口 -->
      <div class="categories-section">
        <h2 class="section-title">
          <el-icon><Grid /></el-icon> 鲜花分类
        </h2>
        <div class="category-grid">
          <div
            v-for="cat in categories"
            :key="cat.id"
            class="category-card"
            @click="$router.push('/category/' + cat.id)"
          >
            <div class="cat-icon">{{ getCategoryIcon(cat.name) }}</div>
            <span>{{ cat.name }}</span>
          </div>
        </div>
      </div>

      <!-- 热门鲜花 -->
      <div class="section">
        <h2 class="section-title">
          <el-icon><StarFilled /></el-icon> 热门鲜花
        </h2>
        <div class="product-grid">
          <div v-for="p in hotProducts" :key="p.id" class="product-card" @click="$router.push('/product/' + p.id)">
            <div class="product-image">
              <img :src="p.coverImage || 'https://picsum.photos/300/300?random=' + p.id" :alt="p.name" />
              <div class="product-tag" v-if="p.isNew">新品</div>
            </div>
            <div class="product-info">
              <h3>{{ p.name }}</h3>
              <p class="product-desc">{{ p.description }}</p>
              <div class="product-price">
                <span class="price">¥{{ p.discountPrice || p.price }}</span>
                <span v-if="p.discountPrice" class="original-price">¥{{ p.price }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 节日鲜花专区 -->
      <div class="section festival-section">
        <h2 class="section-title">
          <el-icon><Crop /></el-icon> 节日鲜花专区
        </h2>
        <div class="festival-banner" @click="$router.push('/festival')">
          <div class="festival-content">
            <h3>🌺 节日主题花束</h3>
            <p>母亲节、情人节、七夕、圣诞节… 为每个重要日子精心准备</p>
            <el-button type="primary" round>查看更多 →</el-button>
          </div>
          <div class="festival-emoji">🎉</div>
        </div>
      </div>

      <!-- 新品上市 -->
      <div class="section">
        <h2 class="section-title">
          <el-icon><Cherry /></el-icon> 新品上市
        </h2>
        <div class="product-grid">
          <div v-for="p in newProducts" :key="p.id" class="product-card" @click="$router.push('/product/' + p.id)">
            <div class="product-image">
              <img :src="p.coverImage || 'https://picsum.photos/300/300?random=' + (p.id + 100)" :alt="p.name" />
              <div class="product-tag hot">热卖</div>
            </div>
            <div class="product-info">
              <h3>{{ p.name }}</h3>
              <p class="product-desc">{{ p.description }}</p>
              <div class="product-price">
                <span class="price">¥{{ p.discountPrice || p.price }}</span>
                <span v-if="p.discountPrice" class="original-price">¥{{ p.price }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 送礼场景 -->
      <div class="section gift-section">
        <h2 class="section-title">
          <el-icon><Present /></el-icon> 送礼场景推荐
        </h2>
        <div class="gift-grid">
          <div class="gift-card" @click="$router.push('/gift')">
            <div class="gift-emoji">💑</div>
            <h4>送给恋人</h4>
            <p>玫瑰、百合、郁金香</p>
          </div>
          <div class="gift-card" @click="$router.push('/gift')">
            <div class="gift-emoji">👩</div>
            <h4>送给母亲</h4>
            <p>康乃馨、玫瑰、百合</p>
          </div>
          <div class="gift-card" @click="$router.push('/gift')">
            <div class="gift-emoji">🤝</div>
            <h4>送给朋友</h4>
            <p>向日葵、雏菊、满天星</p>
          </div>
          <div class="gift-card" @click="$router.push('/gift')">
            <div class="gift-emoji">💼</div>
            <h4>商务送礼</h4>
            <p>蝴蝶兰、富贵竹、红掌</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { productApi, categoryApi, aiApi } from '@/api'

const banners = ref([
  { title: '🌹 每一朵花，都是一句情话', subtitle: '甄选全球优质鲜花，用心传递温暖', bg: 'linear-gradient(135deg, #ff9a9e, #fad0c4)', emoji: '🌹' },
  { title: '🎁 节日送礼首选花语轩', subtitle: '专业花艺师搭配，精美礼盒包装', bg: 'linear-gradient(135deg, #a18cd1, #fbc2eb)', emoji: '🎁' },
  { title: '💐 新品鲜花限时特惠', subtitle: '全场满199包邮，买二送一', bg: 'linear-gradient(135deg, #fccb90, #d57eeb)', emoji: '💐' }
])
const categories = ref([])
const hotProducts = ref([])
const newProducts = ref([])
const bannersLoading = ref(false)

onMounted(() => {
  fetchBanners()
  fetchCategories()
  fetchHotProducts()
  fetchNewProducts()
})

async function fetchBanners() {
  bannersLoading.value = true
  try {
    const res = await aiApi.getBanners()
    if (res.code === 200 && res.data && res.data.length > 0) {
      banners.value = res.data
    }
  } catch (e) {
    console.warn('获取AI轮播图失败，使用默认数据')
  } finally {
    bannersLoading.value = false
  }
}

async function fetchCategories() {
  try {
    const res = await categoryApi.getList()
    if (res.code === 200) categories.value = res.data.slice(0, 8)
  } catch (e) {}
}

async function fetchHotProducts() {
  try {
    const res = await productApi.getHot()
    if (res.code === 200) hotProducts.value = res.data
  } catch (e) {}
}

async function fetchNewProducts() {
  try {
    const res = await productApi.getNew()
    if (res.code === 200) newProducts.value = res.data
  } catch (e) {}
}

function getCategoryIcon(name) {
  const iconMap = {
    '玫瑰': '🌹',
    '百合': '🌸',
    '康乃馨': '💐',
    '向日葵': '🌻',
    '郁金香': '🌷',
    '绣球': '💜',
    '多肉': '🪴',
    '绿植': '🌿',
    '情人节': '💕',
    '母亲节': '💝',
    '生日': '🎂',
    '纪念日': '🎀',
    '求婚': '💍',
    '结婚': '💒',
    '探病': '🏥',
    '道歉': '🙏',
    '毕业': '🎓',
    '教师节': '📚',
    '圣诞节': '🎄',
    '七夕': '⭐'
  }
  
  for (const [key, icon] of Object.entries(iconMap)) {
    if (name.includes(key)) {
      return icon
    }
  }
  return '🌸'
}
</script>

<style scoped>
.banner-section { margin-bottom: 0; }

.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 80px;
}

.banner-content h2 {
  font-size: 36px;
  color: #333;
  margin-bottom: 12px;
}

.banner-content p {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.banner-emoji {
  font-size: 120px;
  animation: float 3s ease-in-out infinite;
}

.categories-section {
  margin: 40px 0;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 16px;
  margin-top: 20px;
}

.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 10px;
  background: white;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(255,107,129,0.08);
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(255,107,129,0.2);
}

.cat-icon { font-size: 32px; }
.category-card span { font-size: 14px; color: #333; }

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 24px;
  color: #333;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid #ffe4e9;
}

.section { margin-bottom: 50px; }

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(255,107,129,0.08);
}

.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(255,107,129,0.15);
}

.product-image {
  position: relative;
  height: 240px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.product-card:hover .product-image img {
  transform: scale(1.1);
}

.product-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  background: linear-gradient(135deg, #ff6b81, #ff4757);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
}

.product-tag.hot {
  background: linear-gradient(135deg, #ffd700, #ffa502);
}

.product-info {
  padding: 16px;
}

.product-info h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 13px;
  color: #999;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.festival-section { margin: 50px 0; }

.festival-banner {
  background: linear-gradient(135deg, #ffecd2, #fcb69f);
  border-radius: 16px;
  padding: 50px 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
}

.festival-content h3 {
  font-size: 28px;
  margin-bottom: 10px;
}

.festival-content p {
  font-size: 15px;
  color: #666;
  margin-bottom: 20px;
}

.festival-emoji {
  font-size: 80px;
  animation: float 3s ease-in-out infinite;
}

.gift-section { margin: 50px 0; }

.gift-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.gift-card {
  background: white;
  border-radius: 16px;
  padding: 32px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(255,107,129,0.08);
}

.gift-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(255,107,129,0.15);
}

.gift-emoji { font-size: 48px; margin-bottom: 12px; }
.gift-card h4 { font-size: 18px; color: #333; margin-bottom: 8px; }
.gift-card p { font-size: 14px; color: #999; }
</style>
