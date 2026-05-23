<template>
  <div class="admin-login-page">
    <div class="login-card">
      <h2>🌸 花语轩 · 管理后台</h2>
      <el-form :model="form" label-width="0" @keyup.enter="login">
        <el-form-item>
          <el-input v-model="form.username" placeholder="管理员账号" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="管理员密码" size="large" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="danger" size="large" @click="login" :loading="loading" style="width:100%">登录后台</el-button>
        </el-form-item>
      </el-form>
      <p class="back-link"><a href="/">返回前台</a></p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = ref({ username: 'admin', password: 'admin123' })
const loading = ref(false)

async function login() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入账号密码')
    return
  }
  loading.value = true
  try {
    const res = await adminApi.login(form.value)
    if (res.code === 200) {
      localStorage.setItem('adminInfo', JSON.stringify(res.data.admin))
      localStorage.setItem('adminToken', res.data.token)
      ElMessage.success('登录成功')
      router.push('/admin/dashboard')
    } else {
      ElMessage.error(res.msg || '登录失败')
    }
  } catch (e) {
    ElMessage.error('登录失败')
  }
  loading.value = false
}
</script>

<style scoped>
.admin-login-page { display: flex; justify-content: center; align-items: center; min-height: 100vh; background: linear-gradient(135deg, #ffe4e9, #ffb3c6); }
.login-card { background: white; border-radius: 20px; padding: 40px; width: 400px; box-shadow: 0 8px 32px rgba(255,107,129,0.15); }
.login-card h2 { text-align: center; font-size: 24px; color: #ff6b81; margin-bottom: 30px; }
.back-link { text-align: center; margin-top: 16px; }
.back-link a { color: #999; text-decoration: none; font-size: 14px; }
.back-link a:hover { color: #ff6b81; }
</style>