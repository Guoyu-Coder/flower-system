<template>
  <div class="ai-assistant">
    <!-- 悬浮按钮 -->
    <div class="ai-float-btn" @click="visible = !visible" v-if="!visible">
      <span class="ai-icon">🤖</span>
      <span class="ai-text">AI导购</span>
    </div>

    <!-- 聊天窗口 -->
    <Transition name="slide">
      <div class="ai-chat-window" v-if="visible" :class="{ 'fullscreen': isFullscreen }">
        <div class="ai-header">
          <div class="ai-header-info">
            <span class="ai-avatar">🌸</span>
            <div>
              <div class="ai-name">花语AI助手</div>
              <div class="ai-status">在线 · 智能推荐</div>
            </div>
          </div>
          <div class="ai-actions">
            <el-button text circle @click="clearHistory" :icon="Delete" size="small" />
            <el-button text circle @click="toggleFullscreen" :icon="isFullscreen ? ArrowDown : FullScreen" size="small" />
            <el-button text circle @click="visible = false" :icon="Minus" size="small" />
          </div>
        </div>

        <div class="ai-messages" ref="msgRef">
          <div v-if="messages.length === 0" class="welcome-message">
            <div class="welcome-emoji">🌸</div>
            <div class="welcome-title">你好，我是花语轩的AI助手</div>
            <div class="welcome-text">
              我可以帮你：<br>
              ✿ 推荐节日鲜花搭配<br>
              ✿ 查询订单物流状态<br>
              ✿ 解答鲜花养护问题<br>
              ✿ 了解配送和售后政策<br><br>
              有什么可以帮到你的吗？💐
            </div>
          </div>
          <div v-for="(msg, idx) in messages" :key="idx"
            class="message"
            :class="msg.role === 'assistant' ? 'assistant' : 'user'">
            <div class="msg-avatar">{{ msg.role === 'assistant' ? '🌸' : '👤' }}</div>
            <div class="msg-content">
              <div class="msg-text" v-if="msg.content">{{ msg.content }}</div>
              
              <!-- 推荐商品卡片 -->
              <div v-if="msg.recommendedProducts && msg.recommendedProducts.length > 0" class="recommended-products">
                <div class="product-card" v-for="(product, pIdx) in msg.recommendedProducts" :key="product.id">
                  <div class="product-index" v-if="msg.role === 'assistant'">{{ pIdx + 1 }}</div>
                  <img :src="product.coverImage || 'https://picsum.photos/200/200?random=' + product.id" :alt="product.name" />
                  <div class="product-info">
                    <div class="product-name">{{ product.name }}</div>
                    <div class="product-language" v-if="product.flowerLanguage">{{ product.flowerLanguage }}</div>
                    <div class="product-price">
                      <span class="current-price">¥{{ product.price }}</span>
                      <span class="original-price" v-if="product.originalPrice > product.price">¥{{ product.originalPrice }}</span>
                    </div>
                    <div class="product-stock" v-if="product.stock">库存: {{ product.stock }}</div>
                  </div>
                  <div class="product-actions">
                    <el-button type="danger" size="small" @click="quickAddToCart(product)">
                      加入购物车
                    </el-button>
                    <el-button type="primary" size="small" @click="quickBuy(product)">
                      立即购买
                    </el-button>
                  </div>
                </div>
              </div>

              <!-- 🛒 购物篮搭配推荐 -->
              <div v-if="msg.bundledRecommendations && msg.bundledRecommendations.length > 0" class="bundled-products">
                <div class="bundled-header">💡 买这束花的人还买了这些👇</div>
                <div class="bundled-grid">
                  <div class="bundled-card" v-for="(product, pIdx) in msg.bundledRecommendations" :key="product.id">
                    <img :src="product.coverImage || 'https://picsum.photos/200/200?random=' + product.id" :alt="product.name" />
                    <div class="product-info-mini">
                      <div class="product-name">{{ product.name }}</div>
                      <div class="product-price">¥{{ product.price }}</div>
                    </div>
                    <div class="product-action-mini">
                      <el-button type="danger" size="small" @click="quickAddToCart(product)">
                      添加
                    </el-button>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 快速选择提示 -->
              <div v-if="msg.recommendedProducts && msg.recommendedProducts.length > 0" class="quick-hint">
                💡 您可以说"第2个"或"就要第1个"快速选择！
              </div>
            </div>
          </div>
          <div v-if="thinking" class="message assistant">
            <div class="msg-avatar">🌸</div>
            <div class="msg-content thinking">
              <span class="dot">·</span>
              <span class="dot">·</span>
              <span class="dot">·</span>
            </div>
          </div>
        </div>

        <div class="ai-input">
          <el-input
            v-model="inputText"
            placeholder="输入您的问题，例如：情人节送什么花好？"
            size="large"
            @keyup.enter="sendMessage"
          >
            <template #append>
              <el-button @click="sendMessage" :disabled="!inputText.trim() || thinking"
              style="background:#ff6b81;color:white;border:none;border-radius:0 12px 12px 0;">
                发送
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { aiApi, cartApi } from '@/api'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { Delete, Minus, FullScreen, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()
const visible = ref(false)
const isFullscreen = ref(false)
const inputText = ref('')
const messages = ref([])
const thinking = ref(false)
const msgRef = ref(null)
const sessionId = ref('')

onMounted(() => {
  initSession()
  loadHistory()
})

function initSession() {
  if (!userStore.isLoggedIn) {
    let guestSessionId = localStorage.getItem('ai_chat_session_id')
    if (!guestSessionId) {
      guestSessionId = 'guest_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
      localStorage.setItem('ai_chat_session_id', guestSessionId)
    }
    sessionId.value = guestSessionId
  }
}

function loadHistory() {
  const key = userStore.isLoggedIn ? `chat_${userStore.userInfo?.id}` : `chat_guest_${sessionId.value}`
  const saved = localStorage.getItem(key)
  if (saved) {
    try {
      messages.value = JSON.parse(saved)
    } catch (e) {
      console.error('加载历史记录失败:', e)
    }
  }
}

function saveHistory() {
  const key = userStore.isLoggedIn ? `chat_${userStore.userInfo?.id}` : `chat_guest_${sessionId.value}`
  try {
    localStorage.setItem(key, JSON.stringify(messages.value))
  } catch (e) {
    console.error('保存历史记录失败:', e)
  }
}

function clearHistory() {
  messages.value = []
  const key = userStore.isLoggedIn ? `chat_${userStore.userInfo?.id}` : `chat_guest_${sessionId.value}`
  localStorage.removeItem(key)
  ElMessage.success('对话历史已清空')
}

async function sendMessage() {
  const msg = inputText.value.trim()
  if (!msg || thinking.value) return

  messages.value.push({ role: 'user', content: msg })
  inputText.value = ''
  thinking.value = true
  scrollToBottom()

  try {
    const payload = {
      message: msg,
      sessionId: sessionId.value
    }

    const res = await aiApi.chat(payload)

    if (res.code === 200) {
      const assistantMsg = {
        role: 'assistant',
        content: res.data.reply,
        recommendedProducts: res.data.recommendedProducts || [],
        bundledRecommendations: res.data.bundledRecommendations || []
      }
      messages.value.push(assistantMsg)

      if (res.data.addToCart && res.data.addToCart.length > 0) {
        for (const item of res.data.addToCart) {
          messages.value.push({
            role: 'assistant',
            content: `🛒 已为您将商品「${item.name}」加入购物车！`
          })
        }
      }
    } else {
      messages.value.push({
        role: 'assistant',
        content: '抱歉，我暂时无法回答这个问题，请稍后再试。🌸'
      })
    }
  } catch (e) {
    console.error('发送消息失败:', e)
    messages.value.push({
      role: 'assistant',
      content: '网络连接失败，请检查网络后重试。💐'
    })
  } finally {
    thinking.value = false
    saveHistory()
    scrollToBottom()
  }
}

async function quickAddToCart(product) {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再操作')
    router.push('/login')
    return
  }

  try {
    const res = await aiApi.quickAddToCart({ productId: product.id, quantity: 1 })
    ElMessage.success(`「${product.name}」已加入购物车`)
    
    // 显示搭配推荐
    if (res.code === 200 && res.data && res.data.bundledRecommendations && res.data.bundledRecommendations.length > 0) {
      const bundledMsg = {
        role: 'assistant',
        content: '',
        bundledRecommendations: res.data.bundledRecommendations
      }
      messages.value.push(bundledMsg)
      saveHistory()
      scrollToBottom()
    }
  } catch (e) {
    console.error('加入购物车失败:', e)
    ElMessage.error('加入购物车失败，请重试')
  }
}

async function quickBuy(product) {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再操作')
    router.push('/login')
    return
  }

  try {
    await cartApi.add({ productId: product.id, quantity: 1 })
    ElMessage.success(`「${product.name}」已加入购物车，正在跳转...`)
    setTimeout(() => {
      router.push('/create-order')
    }, 500)
  } catch (e) {
    console.error('立即购买失败:', e)
    ElMessage.error('操作失败，请重试')
  }
}

function toggleFullscreen() {
  isFullscreen.value = !isFullscreen.value
  scrollToBottom()
}

function scrollToBottom() {
  nextTick(() => {
    if (msgRef.value) {
      msgRef.value.scrollTop = msgRef.value.scrollHeight
    }
  })
}
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 9999;
}

.ai-float-btn {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #ff6b81, #ff4757);
  border-radius: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(255, 107, 129, 0.4);
  transition: all 0.3s;
}

.ai-float-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 24px rgba(255, 107, 129, 0.5);
}

.ai-icon {
  font-size: 22px;
  line-height: 1;
}

.ai-text {
  font-size: 10px;
  color: white;
  margin-top: 2px;
}

.ai-chat-window {
  width: 420px;
  height: 580px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: all 0.3s ease;
}

.ai-chat-window.fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  border-radius: 0;
}

.ai-header {
  background: linear-gradient(135deg, #ff6b81, #ff4757);
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
}

.ai-header-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.ai-avatar {
  font-size: 28px;
}

.ai-name {
  font-size: 16px;
  font-weight: bold;
}

.ai-status {
  font-size: 12px;
  opacity: 0.8;
}

.ai-actions {
  display: flex;
  gap: 4px;
}

.ai-actions :deep(.el-button) {
  color: white;
}

.ai-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: #f8f9fa;
}

.welcome-message {
  text-align: center;
  padding: 40px 20px;
  color: #666;
}

.welcome-emoji {
  font-size: 48px;
  margin-bottom: 16px;
}

.welcome-title {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b81;
  margin-bottom: 12px;
}

.welcome-text {
  font-size: 14px;
  line-height: 1.8;
  text-align: left;
  color: #888;
}

.message {
  display: flex;
  gap: 8px;
  max-width: 85%;
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.msg-avatar {
  width: 32px;
  height: 32px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
  background: #fff0f3;
}

.msg-content {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-all;
}

.assistant .msg-content {
  background: white;
  border: 1px solid #ffe4e9;
  border-top-left-radius: 4px;
}

.user .msg-content {
  background: linear-gradient(135deg, #ff6b81, #ff4757);
  color: white;
  border-top-right-radius: 4px;
}

.thinking {
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

.dot {
  animation: bounce 1.4s infinite ease-in-out both;
}

.dot:nth-child(1) {
  animation-delay: -0.32s;
}

.dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.ai-input {
  padding: 12px 16px;
  border-top: 1px solid #eee;
  background: white;
}

.ai-input :deep(.el-input__wrapper) {
  border-radius: 12px 0 0 12px;
}

/* 推荐商品卡片样式 */
.recommended-products {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-card {
  background: #fff;
  border: 1px solid #ffe4e9;
  border-radius: 12px;
  padding: 12px;
  display: flex;
  gap: 12px;
  transition: all 0.3s;
  position: relative;
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(255, 107, 129, 0.2);
  transform: translateY(-2px);
}

.product-index {
  position: absolute;
  left: -12px;
  top: -8px;
  width: 24px;
  height: 24px;
  background: linear-gradient(135deg, #ff6b81, #ff4757);
  border-radius: 12px;
  color: white;
  font-weight: bold;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-card img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-language {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.current-price {
  font-size: 16px;
  color: #ff4757;
  font-weight: bold;
}

.original-price {
  font-size: 12px;
  color: #ccc;
  text-decoration: line-through;
}

.product-stock {
  font-size: 11px;
  color: #67c23a;
}

.product-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  justify-content: center;
}

.product-actions :deep(.el-button) {
  width: 100%;
  font-size: 12px;
}

/* 购物篮搭配推荐 */
.bundled-products {
  margin-top: 16px;
  background: #fff;
  border: 1px solid #ffe4e9;
  border-radius: 8px;
  padding: 12px;
}

.bundled-header {
  font-size: 13px;
  font-weight: bold;
  color: #ff6b81;
  margin-bottom: 12px;
}

.bundled-grid {
  display: flex;
  gap: 12px;
}

.bundled-card {
  background: #fff5f7;
  border: 1px solid #ffe4e9;
  border-radius: 8px;
  padding: 10px;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.bundled-card img {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
  margin-bottom: 8px;
}

.product-info-mini {
  flex: 1;
  width: 100%;
  text-align: left;
}

.product-action-mini :deep(.el-button) {
  width: 100%;
  font-size: 12px;
}

.quick-hint {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
  padding: 6px 10px;
  background: #fff;
  border-radius: 6px;
}

.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
}

.slide-enter-from,
.slide-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.9);
}
</style>
