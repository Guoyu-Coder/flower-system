import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '../api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const userId = computed(() => userInfo.value?.id)

  function setToken(t) {
    token.value = t
    localStorage.setItem('token', t)
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  async function login(credentials) {
    const res = await userApi.login(credentials)
    if (res.code === 200) {
      setToken(res.data.token)
      setUserInfo(res.data.user)
      return true
    }
    return false
  }

  async function register(data) {
    const res = await userApi.register(data)
    return res.code === 200
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  async function fetchUserInfo() {
    try {
      const res = await userApi.getInfo()
      if (res.code === 200) {
        setUserInfo(res.data)
      }
    } catch (e) {
      console.error('获取用户信息失败', e)
    }
  }

  return {
    token, userInfo, isLoggedIn, userId,
    setToken, setUserInfo, login, register, logout, fetchUserInfo
  }
})