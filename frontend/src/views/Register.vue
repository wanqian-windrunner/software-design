<script setup>
import { ref, reactive } from 'vue';
import { ElForm, ElFormItem, ElInput, ElButton, ElRadioGroup, ElRadio, ElSelect, ElOption, ElMessage, ElCard, ElDivider } from 'element-plus';
import { User, Lock, Phone, OfficeBuilding } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { register } from '../api/auth'

const router = useRouter();
const registerFormRef = ref(null);
const formInline = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  identity: 0,
  clazzId: ''   // ✅ 对应后端 clazzId
});

// 表单验证规则
const validatePass = (rule, value, callback) => {
  if (value !== formInline.password) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};

// 在 script 中添加
const handleClazzIdInput = (value) => {
  // 只允许输入数字
  formInline.clazzId = value.replace(/[^\d]/g, '')
}

// 修改 clazzId 的验证规则
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ],
  clazzId: [
    { required: true, message: '请输入班级ID', trigger: 'blur' },
    { 
      pattern: /^\d+$/, 
      message: '班级ID必须为正整数', 
      trigger: 'blur' 
    },
    {
      validator: (rule, value, callback) => {
        if (value && parseInt(value) <= 0) {
          callback(new Error('班级ID必须大于0'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const onSubmit = async () => {
  console.log('点击注册')
  await registerFormRef.value.validate(async (valid) => {
    console.log('校验结果', valid)
    if (!valid) return

    try {
      const res = await register({
        username: formInline.username,
        password: formInline.password,
        identity: formInline.identity,
        clazzId: formInline.clazzId ? Number(formInline.clazzId) : null
      })

      if (res.code === 1) {
        ElMessage.success('注册成功')
        router.push('/login')
      } else {
        ElMessage.error(res.msg)
      }
    } catch (err) {
      console.error(err)
      ElMessage.error('服务器错误')
    }
  })
}
</script>

<template>
  <div class="register-container">
    <el-card class="register-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>账号注册</h2>
        </div>
      </template>

      <el-form 
        ref="registerFormRef" 
        :model="formInline" 
        :rules="rules" 
        label-position="top"
        size="large"
      >
        <!-- 身份选择 -->
        <el-form-item label="身份" prop="identity">
          <el-radio-group v-model="formInline.identity">
            <el-radio :label="0" border> 学生</el-radio>
            <el-radio :label="1" border> 老师</el-radio>
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
            placeholder="请输入密码 (不少于6位)" 
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="formInline.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码" 
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>

        <!-- 班级选择：仅老师可见 -->
       <el-form-item v-if="formInline.identity === 1" label="所属班级" prop="clazzId">
          <el-input v-model="formInline.clazzId" placeholder="请输入班级ID" clearable @input="handleClazzIdInput" />
          <span style="color: #909399; font-size: 12px;">请输入整数班级ID，如：1、2、3</span>
        </el-form-item>
        <!-- 如果是学生，后端可能会自动分配或留空，这里可以根据实际需求加隐藏字段 -->

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="onSubmit" style="width: 100%" :icon="OfficeBuilding">
            注 册
          </el-button>
        </el-form-item>
        
        <el-divider />
        
        <div class="login-link">
          已有账号？ 
          <el-link type="primary" @click="router.push('/login')">立即登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #e0c3fc 0%, #8ec5fc 100%);
}

.register-card {
  width: 450px;
  border-radius: 12px;
}

.card-header h2 {
  margin: 0;
  text-align: center;
  color: #67c23a;
  font-weight: bold;
}

.login-link {
  text-align: center;
  font-size: 14px;
}
</style>