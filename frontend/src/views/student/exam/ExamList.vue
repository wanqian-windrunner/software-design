<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const user = JSON.parse(sessionStorage.getItem('userInfo'))
const exams = ref([])
const loading = ref(false)
const errorMsg = ref('')

// 获取考试列表（包含完成状态）
const fetchExams = async () => {
  loading.value = true
  errorMsg.value = ''
  
  try {
    const res = await axios.get('/api/student/exams', {
      params: { 
        clazzId: user.clazzId, 
        studentId: user.id 
      }
    })
    
    // 判断返回结果
    if (res.data.code === 1) {
      exams.value = res.data.data || []
    } else {
      // 显示后端返回的错误信息
      errorMsg.value = res.data.msg || '获取考试列表失败'
      ElMessage.warning(errorMsg.value)
      exams.value = []
    }
  } catch (err) {
    console.error('获取考试列表失败', err)
    errorMsg.value = '网络错误，请稍后重试'
    ElMessage.error('网络错误，请稍后重试')
    exams.value = []
  } finally {
    loading.value = false
  }
}

onMounted(fetchExams)
</script>

<template>
  <el-card v-loading="loading">
    <h2>考试列表</h2>

    <!-- 错误提示 -->
    <el-alert 
      v-if="errorMsg" 
      :title="errorMsg" 
      type="warning" 
      show-icon 
      :closable="false"
      style="margin-bottom: 20px;"
    />

    <el-table v-if="exams.length > 0" :data="exams" border stripe>
      <el-table-column prop="examId" label="考试ID" width="80" align="center" />
      <el-table-column prop="examName" label="考试名称" min-width="200" show-overflow-tooltip />
      
      <el-table-column label="状态" width="120" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.finished" type="success">已完成</el-tag>
          <el-tag v-else-if="row.started" type="warning">进行中</el-tag>
          <el-tag v-else type="info">未开始</el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="准确率" width="100" align="center">
        <template #default="{ row }">
          <span v-if="row.finished">
            <el-tag :type="row.accuracy >= 60 ? 'success' : 'danger'">
              {{ row.accuracy }}%
            </el-tag>
          </span>
          <span v-else style="color: #909399;">-</span>
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="150" align="center">
        <template #default="{ row }">
          <el-button 
            v-if="row.finished" 
            type="info" 
            size="small"
            @click="router.push(`/student/exam/${row.examId}/result`)"
          >
            查看结果
          </el-button>
          <el-button 
            v-else-if="row.started" 
            type="warning" 
            size="small"
            @click="router.push(`/student/exam/${row.examId}`)"
          >
            继续考试
          </el-button>
          <el-button 
            v-else 
            type="primary" 
            size="small"
            @click="router.push(`/student/exam/${row.examId}`)"
          >
            开始考试
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && exams.length === 0 && !errorMsg" description="暂无可用考试" />
  </el-card>
</template>