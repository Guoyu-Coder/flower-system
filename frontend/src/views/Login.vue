<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <span class="logo-icon">🌸</span>
        <h2>花语轩 · 用户登录</h2>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" :prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-button type="primary" size="large" class="submit-btn" @click="handleLogin" :loading="loading">
          登录
        </el-button>
      </el-form>
      <div class="login-footer">
        <span @click="$router.push('/register')">还没有账号？立即注册</span>
        <span @click="$router.push('/')">返回首页</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ phone: '', password: '' })
const rules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  loading.value = true
  try {
    const success = await userStore.login(form)
    if (success) {
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error('手机号或密码错误')
    }
  } catch (e) {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #ffe4e9, #fff5f7, #ffb6c1);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-card {
  background: white;
  border-radius: 20px;
  padding: 50px 40px;
  width: 420px;
  box-shadow: 0 8px 32px rgba(255,107,129,0.15);
}
.login-header { text-align: center; margin-bottom: 30px; }
.logo-icon { font-size: 48px; display: block; margin-bottom: 10px; }
.login-header h2 { font-size: 22px; color: #333; }
.submit-btn { width: 100%; margin-top: 10px; border-radius: 25px; height: 48px; font-size: 16px; }
.login-footer { display: flex; justify-content: space-between; margin-top: 16px; font-size: 14px; color: #999; cursor: pointer; }
.login-footer span:hover { color: #ff6b81; }
</style>