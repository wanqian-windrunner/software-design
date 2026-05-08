<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

// 安全地获取用户信息
const getUserInfo = () => {
  try {
    const userInfo = sessionStorage.getItem('userInfo')
    if (!userInfo) {
      ElMessage.error('用户信息不存在，请重新登录')
      router.push('/login')
      return null
    }
    return JSON.parse(userInfo)
  } catch (err) {
    console.error('解析用户信息失败', err)
    ElMessage.error('用户信息异常，请重新登录')
    sessionStorage.clear()
    router.push('/login')
    return null
  }
}

const userInfo = getUserInfo()

// 如果没有用户信息，不继续执行
if (!userInfo) {
  throw new Error('未登录')
}

const user = ref(userInfo)
const loading = ref(false)

const form = ref({
  username: user.value.username || '',
  password: '',
  clazzId: user.value.clazzId || 0
})

// 班级列表
const classList = ref([
  { id: 0, name: '无归属' }
])

// 刷新用户信息（从后端获取最新数据）
const refreshUserInfo = async () => {
  try {
    const res = await axios.get('/api/student/profile', {
      params: { userId: user.value.id }
    })
    
    if (res.data.code === 1) {
      const latestUserInfo = res.data.data
      // 更新本地数据
      user.value = latestUserInfo
      form.value.username = latestUserInfo.username || ''
      form.value.clazzId = latestUserInfo.clazzId || 0
      
      // 更新 sessionStorage
      sessionStorage.setItem('userInfo', JSON.stringify(latestUserInfo))
    }
  } catch (err) {
    console.error('获取用户信息失败', err)
  }
}

// 从后端获取班级列表
const loadClassList = async () => {
  try {
    const res = await axios.get('/api/classes')
    const clazzIds = res.data.data || []
    
    const classOptions = clazzIds.map(id => ({
      id: id,
      name: `班级 ${id}`
    }))
    
    classList.value = [
      { id: 0, name: '无归属' },
      ...classOptions
    ]
  } catch (err) {
    console.error('获取班级列表失败', err)
    // 保持默认的班级列表
  }
}

// 初始化数据
const initData = async () => {
  loading.value = true
  try {
    // 先刷新用户信息，获取最新的班级
    await refreshUserInfo()
    // 再加载班级列表
    await loadClassList()
  } catch (err) {
    console.error('初始化失败', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  initData()
})

const updateUsername = async () => {
  if (!form.value.username.trim()) {
    ElMessage.warning('用户名不能为空')
    return
  }
  
  if (form.value.username === user.value.username) {
    ElMessage.warning('用户名未改变')
    return
  }
  
  try {
    const res = await axios.put('/api/student/update-username', null, {
      params: { userId: user.value.id, username: form.value.username }
    })
    
    if (res.data.code === 1) {
      ElMessage.success('用户名修改成功')
      // 刷新用户信息
      await refreshUserInfo()
    } else {
      ElMessage.error(res.data.msg || '修改失败')
    }
  } catch (err) {
    console.error('修改用户名失败', err)
    ElMessage.error('修改失败：' + (err.response?.data?.msg || '服务器错误'))
  }
}

const updatePassword = async () => {
  if (!form.value.password) {
    ElMessage.warning('请输入新密码')
    return
  }
  
  if (form.value.password.length < 6) {
    ElMessage.warning('密码长度不能少于6位')
    return
  }
  
  try {
    const res = await axios.put('/api/student/update-password', null, {
      params: { userId: user.value.id, password: form.value.password }
    })
    
    if (res.data.code === 1) {
      ElMessage.success('密码修改成功')
      form.value.password = ''
    } else {
      ElMessage.error(res.data.msg || '修改失败')
    }
  } catch (err) {
    console.error('修改密码失败', err)
    ElMessage.error('修改失败：' + (err.response?.data?.msg || '服务器错误'))
  }
}

const changeClazz = async () => {
  if (form.value.clazzId === user.value.clazzId) {
    ElMessage.warning('请选择不同的班级')
    return
  }
  
  try {
    const res = await axios.put('/api/student/change-clazz', null, {
      params: { 
        userId: user.value.id, 
        clazzId: form.value.clazzId
      }
    })
    
    if (res.data.code === 1) {
      // 刷新用户信息
      await refreshUserInfo()
      ElMessage.success(form.value.clazzId === 0 ? '已退出班级' : '已申请更换班级，等待审核')
    } else {
      ElMessage.error(res.data.msg || '操作失败')
    }
  } catch (err) {
    console.error('更换班级失败', err)
    ElMessage.error('操作失败：' + (err.response?.data?.msg || '服务器错误'))
  }
}
</script>

<template>
  <el-card v-if="user" v-loading="loading">
    <h2>更改个人信息</h2>

    <el-form label-width="100px">
      <el-form-item label="学号">
        <el-input :model-value="user.id" disabled />
      </el-form-item>
      
      <el-form-item label="当前班级">
        <el-input 
          :model-value="user.clazzId && user.clazzId !== 0 ? `班级 ${user.clazzId}` : '无归属'" 
          disabled 
        />
      </el-form-item>

      <el-form-item label="审核状态">
        <el-tag :type="user.verify === 1 ? 'success' : 'warning'">
          {{ user.verify === 1 ? '已通过' : '待审核' }}
        </el-tag>
      </el-form-item>

      <el-divider />

      <el-form-item label="用户名">
        <div style="display: flex; gap: 10px;">
          <el-input v-model="form.username" style="flex: 1;" />
          <el-button 
            type="primary" 
            @click="updateUsername"
            :disabled="!form.username.trim() || form.username === user.username"
          >
            修改
          </el-button>
        </div>
      </el-form-item>

      <el-form-item label="新密码">
        <div style="display: flex; gap: 10px;">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入新密码（不少于6位）"
            show-password
            style="flex: 1;" 
          />
          <el-button 
            type="primary" 
            @click="updatePassword"
            :disabled="!form.password || form.password.length < 6"
          >
            修改
          </el-button>
        </div>
      </el-form-item>

      <el-form-item label="更换班级">
        <div style="display: flex; gap: 10px;">
          <el-select 
            v-model="form.clazzId" 
            placeholder="请选择班级" 
            style="flex: 1;"
          >
            <el-option 
              v-for="item in classList" 
              :key="item.id" 
              :label="item.name" 
              :value="item.id"
            >
              <span v-if="item.id === 0" style="color: #909399;">{{ item.name }}</span>
              <span v-else>{{ item.name }}</span>
            </el-option>
          </el-select>
          <el-button 
            type="warning" 
            @click="changeClazz"
            :disabled="form.clazzId === user.clazzId"
          >
            {{ form.clazzId === 0 ? '退出班级' : '申请更换' }}
          </el-button>
        </div>
      </el-form-item>
    </el-form>
  </el-card>
  <div v-else style="text-align: center; padding: 50px;">
    <p>请先登录</p>
  </div>
</template>