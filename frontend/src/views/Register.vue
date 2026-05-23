<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <span class="logo-icon">🌸</span>
        <h2>花语轩 · 用户注册</h2>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="nickname">
          <el-input v-model="form.nickname" placeholder="昵称" :prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" :prefix-icon="Iphone" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" :prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-button type="primary" size="large" class="submit-btn" @click="handleRegister" :loading="loading">
          注册
        </el-button>
      </el-form>
      <div class="register-footer">
        <span @click="$router.push('/login')">已有账号？立即登录</span>
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
import { User, Lock, Iphone } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ nickname: '', phone: '', password: '', confirmPassword: '' })
const rules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule, value, callback) => { if (value !== form.password) callback(new Error('两次密码不一致')); else callback() }, trigger: 'blur' }
  ]
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  loading.value = true
  try {
    const success = await userStore.register({ nickname: form.nickname, phone: form.phone, password: form.password })
    if (success) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error('注册失败，手机号可能已存在')
    }
  } catch (e) {
    ElMessage.error('注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #ffe4e9, #fff5f7, #ffb6c1);
  display: flex;
  align-items: center;
  justify-content: center;
}
.register-card {
  background: white;
  border-radius: 20px;
  padding: 50px 40px;
  width: 420px;
  box-shadow: 0 8px 32px rgba(255,107,129,0.15);
}
.register-header { text-align: center; margin-bottom: 30px; }
.logo-icon { font-size: 48px; display: block; margin-bottom: 10px; }
.register-header h2 { font-size: 22px; color: #333; }
.submit-btn { width: 100%; margin-top: 10px; border-radius: 25px; height: 48px; font-size: 16px; }
.register-footer { display: flex; justify-content: space-between; margin-top: 16px; font-size: 14px; color: #999; cursor: pointer; }
.register-footer span:hover { color: #ff6b81; }
</style>