<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const teacher = ref({})

onMounted(async () => {
  const user = JSON.parse(sessionStorage.getItem('userInfo'))
  const res = await axios.get('/api/teacher/profile', {
    params: { teacherId: user.id }
  })
  teacher.value = res.data.data
})

const updateUsername = async () => {
  await axios.put('/api/teacher/update-username', null, {
    params: {
      teacherId: teacher.value.id,
      username: teacher.value.username
    }
  })
  ElMessage.success('用户名修改成功')
}

const updatePassword = async () => {
  await axios.put('/api/teacher/update-password', null, {
    params: {
      teacherId: teacher.value.id,
      password: teacher.value.password
    }
  })
  ElMessage.success('密码修改成功')
}
</script>

<template>
  <el-card>
    <h2>个人信息</h2>

    <el-form label-width="100px">
      <el-form-item label="用户名">
        <el-input v-model="teacher.username" />
        <el-button @click="updateUsername">修改</el-button>
      </el-form-item>

      <el-form-item label="新密码">
        <el-input v-model="teacher.password" type="password" />
        <el-button @click="updatePassword">修改</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>