<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const teacher = JSON.parse(sessionStorage.getItem('userInfo'))
const exams = ref([])

// 创建考试对话框
const createDialogVisible = ref(false)
const createForm = ref({
  examName: '',
  clazzId: teacher.clazzId
})

// 成绩详情对话框
const scoreDialogVisible = ref(false)
const currentExam = ref(null)
const scores = ref([])
const scoreDetail = ref(null)
const scoreDetailDialogVisible = ref(false)

// 加载考试列表
const loadExams = async () => {
  try {
    const res = await axios.get('/api/teacher/exams', {
      params: { clazzId: teacher.clazzId }
    })
    exams.value = res.data.data
  } catch (err) {
    console.error('获取考试列表失败', err)
    ElMessage.error('获取考试列表失败')
  }
}

// 创建考试
const createExam = async () => {
  if (!createForm.value.examName.trim()) {
    ElMessage.warning('请输入考试名称')
    return
  }
  
  try {
    await axios.post('/api/teacher/exam', {
      examName: createForm.value.examName,
      clazzId: teacher.clazzId,
      state: 0
    })
    ElMessage.success('考试创建成功')
    createDialogVisible.value = false
    createForm.value.examName = ''
    loadExams()
  } catch (err) {
    console.error('创建考试失败', err)
    ElMessage.error('创建失败：' + (err.response?.data?.msg || '服务器错误'))
  }
}

// 切换考试状态
const toggleState = async (exam) => {
  const newState = exam.state === 1 ? 0 : 1
  const actionText = newState === 1 ? '启用' : '停用'
  
  try {
    await axios.put('/api/teacher/exam/state', null, {
      params: {
        examId: exam.examId,
        state: newState
      }
    })
    ElMessage.success(`考试已${actionText}`)
    loadExams()
  } catch (err) {
    console.error('状态更新失败', err)
    ElMessage.error('操作失败')
  }
}

// 删除考试
const deleteExam = (exam) => {
  ElMessageBox.confirm(
    `确定删除考试"${exam.examName}"吗？删除后相关题目和成绩也会被删除！`,
    '警告',
    {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    }
  ).then(async () => {
    try {
      await axios.delete('/api/teacher/exam', {
        params: { examId: exam.examId }
      })
      ElMessage.success('考试已删除')
      loadExams()
    } catch (err) {
      console.error('删除失败', err)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 查看考试成绩
const viewScores = async (exam) => {
  currentExam.value = exam
  try {
    const res = await axios.get('/api/teacher/exam/scores', {
      params: { examId: exam.examId }
    })
    scores.value = res.data.data || []
    scoreDialogVisible.value = true
  } catch (err) {
    console.error('获取成绩失败', err)
    ElMessage.error('获取成绩失败')
  }
}

// 查看成绩详情
const viewScoreDetail = async (score) => {
  try {
    const res = await axios.get('/api/teacher/exam/score/detail', {
      params: { testId: score.testId }
    })
    scoreDetail.value = res.data.data
    scoreDetailDialogVisible.value = true
  } catch (err) {
    console.error('获取详情失败', err)
    ElMessage.error('获取详情失败')
  }
}

onMounted(loadExams)
</script>

<template>
  <el-card>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h2>考试管理 - 班级 {{ teacher.clazzId }}</h2>
      <el-button type="primary" @click="createDialogVisible = true">
        创建考试
      </el-button>
    </div>

    <el-table :data="exams" border stripe>
      <el-table-column prop="examId" label="考试ID" width="80" align="center" />
      <el-table-column prop="examName" label="考试名称" min-width="200" show-overflow-tooltip />
      <el-table-column prop="clazzId" label="班级" width="80" align="center" />
      
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.state === 1 ? 'success' : 'info'">
            {{ row.state === 1 ? '已启用' : '未启用' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="创建时间" width="180" align="center">
        <template #default="{ row }">
          {{ row.createTime || '-' }}
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" min-width="380">
        <template #default="{ row }">
          <el-button 
            type="info" 
            size="small" 
            @click="$router.push(`/teacher/exam/${row.examId}/questions`)"
          >
            题库管理
          </el-button>
          
          <el-button 
            type="success" 
            size="small" 
            @click="viewScores(row)"
          >
            查看成绩
          </el-button>
          
          <el-button 
            :type="row.state === 1 ? 'warning' : 'success'" 
            size="small"
            @click="toggleState(row)"
          >
            {{ row.state === 1 ? '停用' : '启用' }}
          </el-button>
          
          <el-button 
            type="danger" 
            size="small"
            @click="deleteExam(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建考试对话框 -->
    <el-dialog v-model="createDialogVisible" title="创建考试" width="500px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="考试名称" required>
          <el-input 
            v-model="createForm.examName" 
            placeholder="请输入考试名称，如：期中考试、期末考试"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="所属班级">
          <el-input :model-value="teacher.clazzId" disabled />
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">
            只能为自己班级（{{ teacher.clazzId }}）创建考试
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createExam" :disabled="!createForm.examName.trim()">
          确认创建
        </el-button>
      </template>
    </el-dialog>

    <!-- 考试成绩对话框 -->
    <el-dialog v-model="scoreDialogVisible" :title="`考试成绩 - ${currentExam?.examName}`" width="800px">
      <el-table :data="scores" border stripe max-height="500">
        <el-table-column prop="testId" label="记录ID" width="80" align="center" />
        <el-table-column prop="id" label="学生ID" width="80" align="center" />
        <el-table-column prop="username" label="学生姓名" width="120" />
        <el-table-column prop="examName" label="考试名称" min-width="150" />
        <el-table-column prop="accuracy" label="准确率" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.accuracy >= 60 ? 'success' : 'danger'">
              {{ row.accuracy }}%
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewScoreDetail(row)">
              详细
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="scores.length === 0" style="text-align: center; padding: 30px; color: #909399;">
        暂无学生成绩记录
      </div>

      <template #footer>
        <el-button @click="scoreDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 成绩详情对话框 -->
    <el-dialog v-model="scoreDetailDialogVisible" title="成绩详情" width="600px">
      <el-descriptions v-if="scoreDetail" :column="2" border>
        <el-descriptions-item label="记录ID">{{ scoreDetail.testId }}</el-descriptions-item>
        <el-descriptions-item label="学生ID">{{ scoreDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ scoreDetail.username }}</el-descriptions-item>
        <el-descriptions-item label="考试名称">{{ scoreDetail.examName }}</el-descriptions-item>
        <el-descriptions-item label="准确率">
          <el-tag :type="scoreDetail.accuracy >= 60 ? 'success' : 'danger'">
            {{ scoreDetail.accuracy }}%
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ scoreDetail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="AI综合评价" :span="2">
          <div style="white-space: pre-wrap; line-height: 1.6; padding: 10px; background: #f5f7fa; border-radius: 4px;">
            {{ scoreDetail.aiReview || '暂无评价' }}
          </div>
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button type="primary" @click="scoreDetailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>