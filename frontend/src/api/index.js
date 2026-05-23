import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json;charset=utf-8' }
})

// 请求拦截器
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
}, error => {
  return Promise.reject(error)
})

// 响应拦截器
request.interceptors.response.use(response => {
  const res = response.data
  if (res.code === 401) {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    ElMessage.error('登录已过期，请重新登录')
    router.push('/login')
    return Promise.reject(new Error('登录已过期'))
  }
  return res
}, error => {
  if (error.response && error.response.status === 401) {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }
  ElMessage.error(error.message || '请求失败')
  return Promise.reject(error)
})

export default request

// ========== 用户模块 ==========
export const userApi = {
  register(data) { return request.post('/user/register', data) },
  login(data) { return request.post('/user/login', data) },
  getInfo() { return request.get('/user/info') },
  updateInfo(data) { return request.put('/user/info', data) },
  updatePassword(data) { return request.put('/user/password', data) }
}

// ========== 商品模块 ==========
export const productApi = {
  getList(params) { return request.get('/product/list', { params }) },
  getDetail(id) { return request.get('/product/detail/' + id) },
  getHot() { return request.get('/product/hot') },
  getNew() { return request.get('/product/new') },
  getByCategory(categoryId) { return request.get('/product/category/' + categoryId) },
  search(params) { return request.get('/product/search', { params }) }
}

// ========== 分类模块 ==========
export const categoryApi = {
  getList() { return request.get('/category/list') }
}

// ========== 购物车模块 ==========
export const cartApi = {
  getList() { return request.get('/cart/list') },
  add(data) { return request.post('/cart/add', data) },
  updateQuantity(id, quantity) { return request.put('/cart/quantity', { id, quantity }) },
  update(data) { return request.put('/cart/quantity', data) },
  delete(id) { return request.delete('/cart/delete/' + id) },
  toggleSelect(id, selected) { return request.put('/cart/selected', { id, selected }) },
  selectAll(selected) { return request.put('/cart/selectAll?selected=' + selected) },
  getSelected() { return request.get('/cart/selected') },
  getByIds(ids) { return request.get('/cart/batch', { params: { ids: ids.join(',') } }) },
  getCount() { return request.get('/cart/count') }
}

// ========== 订单模块 ==========
export const orderApi = {
  create(data) { return request.post('/order/create', data) },
  pay(orderId) { return request.post('/order/pay/' + orderId) },
  cancel(orderId) { return request.post('/order/cancel/' + orderId) },
  getList(params) { return request.get('/order/list', { params }) },
  getDetail(id) { return request.get('/order/detail/' + id) },
  confirm(orderId) { return request.post('/order/receive/' + orderId) }
}

// ========== 地址模块 ==========
export const addressApi = {
  getList() { return request.get('/address/list') },
  getById(id) { return request.get('/address/' + id) },
  add(data) { return request.post('/address/add', data) },
  update(data) { return request.put('/address/update', data) },
  delete(id) { return request.delete('/address/delete/' + id) },
  setDefault(id) { return request.put('/address/default/' + id) }
}

// ========== 收藏模块 ==========
export const favoriteApi = {
  getList(params) { return request.get('/favorite/list', { params }) },
  add(productId) { return request.post('/favorite/add/' + productId) },
  remove(productId) { return request.delete('/favorite/remove/' + productId) },
  check(productId) { return request.get('/favorite/check/' + productId) }
}

// ========== 管理员模块 ==========
export const adminApi = {
  login(data) { return request.post('/admin/login', data) },
  getInfo() { return request.get('/admin/info') },

  // 用户管理
  getUsers(params) { return request.get('/admin/users', { params }) },
  deleteUser(id) { return request.delete('/admin/user/' + id) },

  // 商品管理
  getProducts(params) { return request.get('/admin/products', { params }) },
  addProduct(data) { return request.post('/admin/product', data) },
  updateProduct(data) { return request.put('/admin/product', data) },
  deleteProduct(id) { return request.delete('/admin/product/' + id) },
  toggleStatus(id) { return request.put('/admin/product/status/' + id) },

  // 分类管理
  getCategories() { return request.get('/admin/categories') },
  addCategory(data) { return request.post('/admin/category', data) },
  updateCategory(data) { return request.put('/admin/category', data) },
  deleteCategory(id) { return request.delete('/admin/category/' + id) },

  // 订单管理
  getOrders(params) { return request.get('/admin/orders', { params }) },
  updateOrderStatus(orderId, status) {
    return request.put('/admin/order/status/' + orderId + '?status=' + status)
  },

  // 仪表盘
  getDashboard() { return request.get('/admin/dashboard') }
}

// ========== 仪表盘模块 ==========
export const dashboardApi = {
  getStats() { return request.get('/admin/dashboard') }
}

// ========== AI聊天模块 ==========
export const aiApi = {
  chat(data) { return request.post('/ai/chat', data) },
  getHistory() { return request.get('/ai/history') },
  getRecommendations(params) { return request.get('/ai/recommend', { params }) },
  quickAddToCart(data) { return request.post('/ai/quick-add-cart', data) },
  getBanners() { return request.get('/ai/banners') }
}
