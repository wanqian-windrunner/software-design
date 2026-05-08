<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  User,
  UserFilled,
  Document,
  Setting,
  SwitchButton,
  MagicStick
} from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const teacherInfo = ref(
  JSON.parse(sessionStorage.getItem('userInfo')) || {}
)

const menuItems = [
  { index: '/teacher/profile', title: '个人信息', icon: User },
  { index: '/teacher/students', title: '学生管理', icon: UserFilled },
  { index: '/teacher/exams', title: '考试管理', icon: Document },
  { index: '/teacher/courseware', title: 'PPT生成', icon: MagicStick }
]

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    type: 'warning'
  }).then(() => {
    sessionStorage.clear()
    router.push('/login')
    ElMessage.success('已退出登录')
  })
}
</script>

<template>
  <el-container class="layout">
    <!-- 侧边栏 -->
    <el-aside width="200px" class="aside">
      <div class="logo">
        👩‍🏫 教师端
      </div>
      <el-menu
        :default-active="route.path"
        router
        class="menu"
      >
        <el-menu-item
          v-for="item in menuItems"
          :key="item.index"
          :index="item.index"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          {{ item.title }}
        </el-menu-item>
      </el-menu>

      <div class="logout">
        <el-button type="danger" :icon="SwitchButton" @click="handleLogout">
          退出登录
        </el-button>
      </div>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <el-header class="header">
        <span>欢迎，{{ teacherInfo.username }}</span>
        <span>（班级：{{ teacherInfo.clazzId }}）</span>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout {
  height: 100vh;
}

.aside {
  background: #2d3a4b;
  color: #fff;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.menu {
  flex: 1;
  border-right: none;
}

.logout {
  padding: 16px;
}

.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  border-bottom: 1px solid #eee;
}

.main {
  background: #f5f7fa;
  padding: 20px;
}
</style>
