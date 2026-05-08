import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },

  /* 登录 / 注册 */
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },

  /* ✅ 教师端 */
  {
    path: '/teacher',
    name: 'Teacher',
    component: () => import('../views/teacher/TeacherLayout.vue'),
    meta: { requiresAuth: true, identity: 1 },
    children: [
      {
        path: 'profile',
        name: 'TeacherProfile',
        component: () => import('../views/teacher/profile/TeacherProfile.vue')
      },
      {
        path: 'students',
        name: 'StudentManage',
        component: () => import('../views/teacher/student/StudentManage.vue')
      },
      {
        path: 'exams',
        name: 'ExamList',
        component: () => import('../views/teacher/exam/ExamList.vue')
      },
      {
        path: 'exam/:examId/questions',
        name: 'QuestionBank',
        component: () => import('../views/teacher/exam/QuestionBank.vue')
      },
      {
      path: 'courseware',
      name: 'Courseware',
      component: () => import('../views/teacher/index.vue')
      }
    ]
  },

  /* ✅ 学生端 */
  {
    path: '/student',
    name: 'Student',
    component: () => import('../views/student/StudentLayout.vue'),
    meta: { requiresAuth: true, identity: 0 },
    children: [
      {
        path: 'profile',
        name: 'StudentProfile',
        component: () => import('../views/student/profile/StudentProfile.vue')
      },
      {
        path: 'exams',
        name: 'StudentExams',
        component: () => import('../views/student/exam/ExamList.vue')
      },
      {
        path: 'exam/:examId',
        name: 'StudentExamDo',
        component: () => import('../views/student/exam/ExamDo.vue')
      },
      {
        path: 'exam/:examId/result',
        name: 'StudentExamResult',
        component: () => import('../views/student/ExamResult.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫：检查登录态和身份
router.beforeEach((to, from, next) => {
  const userInfo = sessionStorage.getItem('userInfo')
  const isAuthenticated = !!userInfo

  // 不需要登录的页面直接放行
  if (!to.meta.requiresAuth) {
    // 如果已登录且访问登录/注册页，跳转到对应主页
    if (isAuthenticated && (to.path === '/login' || to.path === '/register')) {
      const user = JSON.parse(userInfo)
      next(user.identity === 1 ? '/teacher/profile' : '/student/profile')
      return
    }
    next()
    return
  }

  // 需要登录但未登录
  if (!isAuthenticated) {
    ElMessage.warning('请先登录')
    next('/login')
    return
  }

  // 已登录，检查身份
  const user = JSON.parse(userInfo)
  
  // 检查身份是否匹配
  if (to.meta.identity !== undefined && to.meta.identity !== user.identity) {
    ElMessage.warning('无权访问该页面')
    next(user.identity === 1 ? '/teacher/profile' : '/student/profile')
    return
  }

  next()
})

export default router