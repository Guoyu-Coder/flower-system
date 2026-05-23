<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="$emit('update:visible', $event)"
    :title="address ? '编辑地址' : '新增地址'"
    width="520px"
    :close-on-click-modal="false"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="80px"
      style="padding-right: 20px"
    >
      <el-form-item label="收货人" prop="name">
        <el-input v-model="form.name" placeholder="请输入收货人姓名" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
      </el-form-item>
      <el-form-item label="所在地区" prop="province">
        <div style="display:flex;gap:8px;width:100%">
          <el-input v-model="form.province" placeholder="省" style="flex:1" />
          <el-input v-model="form.city" placeholder="市" style="flex:1" />
          <el-input v-model="form.district" placeholder="区" style="flex:1" />
        </div>
      </el-form-item>
      <el-form-item label="详细地址" prop="detail">
        <el-input v-model="form.detail" type="textarea" :rows="2" placeholder="请输入详细地址" />
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="form.isDefault">设为默认地址</el-checkbox>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { addressApi } from '@/api'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: Boolean,
  address: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'success'])

const formRef = ref(null)
const loading = ref(false)
const form = ref({
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false
})

const rules = {
  name: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  province: [{ required: true, message: '请填写省份', trigger: 'blur' }],
  city: [{ required: true, message: '请填写城市', trigger: 'blur' }],
  district: [{ required: true, message: '请填写区县', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

watch(() => props.visible, (val) => {
  if (val) {
    if (props.address) {
      form.value = {
        name: props.address.name || '',
        phone: props.address.phone || '',
        province: props.address.province || '',
        city: props.address.city || '',
        district: props.address.district || '',
        detail: props.address.detail || '',
        isDefault: props.address.isDefault === 1
      }
    } else {
      resetForm()
    }
  }
})

function resetForm() {
  form.value = {
    name: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: false
  }
  formRef.value?.clearValidate()
}

async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    loading.value = true
    
    const submitData = {
      ...form.value,
      isDefault: form.value.isDefault ? 1 : 0
    }
    
    let res
    if (props.address) {
      submitData.id = props.address.id
      res = await addressApi.update(submitData)
    } else {
      res = await addressApi.add(submitData)
    }
    if (res.code === 200) {
      ElMessage.success(props.address ? '地址更新成功' : '地址添加成功')
      emit('success')
      emit('update:visible', false)
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    if (e?.message) ElMessage.error(e.message)
  }
  loading.value = false
}
</script>
