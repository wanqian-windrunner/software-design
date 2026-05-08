<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, School } from '@element-plus/icons-vue'

const router = useRouter()
const loginFormRef = ref(null)

const formInline = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  identity: [{ required: true, message: '请选择身份', trigger: 'change' }]
}

const onSubmit = async () => {
  if (!loginFormRef.value) return

  loginFormRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.error('请填写完整的登录信息')
      return
    }

    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formInline)
      }).then(r => r.json())

      // ✅ 和你后端 Result 对齐
      if (res.code === 1 && res.data) {
        if (res.data.identity !== formInline.identity) {
          ElMessage.error(
            res.data.identity === 1
              ? '该账号是老师账号，请切换身份登录'
              : '该账号是学生账号，请切换身份登录'
          )
          return
        }
        ElMessage.success('登录成功')

        // ✅ 存用户信息
        sessionStorage.setItem('userInfo', JSON.stringify(res.data))

        // ✅ 按身份跳转
        if (res.data.identity === 1) {
          router.push('/teacher')
        } else {
          router.push('/student')
        }
      } else {
        ElMessage.error(res.msg || '登录失败')
      }
    } catch (err) {
      console.error(err)
      ElMessage.error('网络错误')
    }
  })
}
</script>

<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2> AI 教育辅助系统</h2>
        </div>
      </template>

      <el-form 
        ref="loginFormRef" 
        :model="formInline" 
        :rules="rules" 
        label-position="top"
        size="large"
      >
        <!-- 身份选择 -->
       <el-form-item label="身份" prop="identity">
          <el-radio-group v-model="formInline.identity">
            <el-radio :label="0" border>学生</el-radio>
            <el-radio :label="1" border>老师</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 用户名 -->
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="formInline.username" 
            placeholder="请输入用户名" 
            clearable
            :prefix-icon="User"
          />
        </el-form-item>

        <!-- 密码 -->
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="formInline.password" 
            type="password" 
            placeholder="请输入密码" 
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="onSubmit" style="width: 100%" :icon="School">
            登 录
          </el-button>
        </el-form-item>
        
        <el-divider />
        
        <div class="register-link">
          还没有账号？ 
          <el-link type="primary" @click="router.push('/register')">立即注册</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.login-card {
  width: 400px;
  border-radius: 12px;
}

.card-header h2 {
  margin: 0;
  text-align: center;
  color: #409eff;
  font-weight: bold;
}

.register-link {
  text-align: center;
  font-size: 14px;
}
</style>