<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()

const logout = () => {
  sessionStorage.clear()
  router.push('/login')
}
</script>

<template>
  <el-container style="height:100vh">
    <el-aside width="200px" style="background:#409EFF">
      <div style="color:#fff;text-align:center;padding:20px">学生端</div>
      <el-menu background-color="#409EFF" text-color="#fff">
        <el-menu-item @click="$router.push('/student/profile')">更改个人信息</el-menu-item>
        <el-menu-item @click="$router.push('/student/exams')">考试</el-menu-item>
        <el-menu-item @click="logout">退出登录</el-menu-item>
      </el-menu>
    </el-aside>

    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>