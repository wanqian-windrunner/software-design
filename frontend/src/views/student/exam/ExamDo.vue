<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const examId = route.params.examId
const student = JSON.parse(sessionStorage.getItem('userInfo'))

const questions = ref([])
const currentIndex = ref(0)
const answer = ref('')
const result = ref(null)
const loading = ref(false)
const finalResult = ref(null)
const showResult = ref(false)
const answeredQuestions = ref([])

// 检查学生状态
const checkStudentStatus = async () => {
  try {
    const res = await axios.get('/api/student/check-status', {
      params: { studentId: student.id }
    })
    
    if (res.data.code !== 1) {
      ElMessage.error(res.data.msg || '您已被移出班级，请重新登录')
      sessionStorage.clear()
      router.push('/login')
      return false
    }
    return true
  } catch (err) {
    console.error('检查状态失败', err)
    ElMessage.error('网络错误，请稍后重试')
    router.push('/student/exams')
    return false
  }
}

// 获取题目和答题记录
onMounted(async () => {
  // 先检查学生状态
  const statusOk = await checkStudentStatus()
  if (!statusOk) return
  
  try {
    // 获取题目
    const questionsRes = await axios.get('/api/student/exam/questions', {
      params: { 
        examId,
        studentId: student.id 
      }
    })
    questions.value = questionsRes.data.data
    
    if (questions.value.length === 0) {
      ElMessage.warning('该考试暂无题目')
      return
    }
    
    // 获取已答题记录
    const answersRes = await axios.get('/api/student/exam/answers', {
      params: { 
        examId: Number(examId), 
        studentId: student.id 
      }
    })
    
    if (answersRes.data.data && answersRes.data.data.length > 0) {
      answeredQuestions.value = answersRes.data.data
      
      const answeredIds = answeredQuestions.value.map(a => a.questionId)
      const firstUnanswered = questions.value.findIndex(q => !answeredIds.includes(q.questionId))
      
      if (firstUnanswered === -1) {
        currentIndex.value = questions.value.length - 1
      } else {
        currentIndex.value = firstUnanswered
      }
    }
    
    // 开始或继续考试
    await axios.post('/api/student/exam/start', null, {
      params: { 
        examId: Number(examId), 
        studentId: student.id 
      }
    })
  } catch (err) {
    console.error('初始化考试失败', err)
    
    // 检查是否是权限问题
    if (err.response?.data?.code === 0) {
      ElMessage.error(err.response.data.msg || '操作失败')
      if (err.response.data.msg?.includes('移出班级') || err.response.data.msg?.includes('未通过审核')) {
        sessionStorage.clear()
        router.push('/login')
        return
      }
    } else {
      ElMessage.error('加载考试失败')
    }
  }
})

// 提交答案（带状态检查）
const submitAnswer = async () => {
  if (!answer.value.trim()) {
    ElMessage.warning('请输入答案')
    return
  }
  
  loading.value = true
  try {
    const res = await axios.post('/api/student/exam/submit-question', {
      examId: Number(examId),
      questionId: questions.value[currentIndex.value].questionId,
      answer: answer.value,
      studentId: student.id
    })
    result.value = res.data.data
    
    const answered = answeredQuestions.value.find(
      a => a.questionId === questions.value[currentIndex.value].questionId
    )
    if (!answered) {
      answeredQuestions.value.push({
        questionId: questions.value[currentIndex.value].questionId
      })
    }
    
    ElMessage.success('提交成功')
  } catch (err) {
    console.error('提交失败', err)
    
    // 检查是否是权限问题
    if (err.response?.data?.code === 0) {
      ElMessage.error(err.response.data.msg || '提交失败')
      if (err.response.data.msg?.includes('移出班级') || err.response.data.msg?.includes('未通过审核')) {
        sessionStorage.clear()
        router.push('/login')
        return
      }
    } else {
      ElMessage.error('提交失败')
    }
  } finally {
    loading.value = false
  }
}

// 跳转到指定题目
const goToQuestion = (index) => {
  currentIndex.value = index
  answer.value = ''
  result.value = null
}

// 下一题
const nextQuestion = () => {
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value++
    answer.value = ''
    result.value = null
  } else {
    finishExam()
  }
}

// 完成考试
const finishExam = async () => {
  const unansweredCount = questions.value.length - answeredQuestions.value.length
  if (unansweredCount > 0) {
    ElMessage.warning(`还有 ${unansweredCount} 道题未作答`)
    return
  }
  
  try {
    const res = await axios.post('/api/student/exam/finish', null, {
      params: { 
        examId: Number(examId), 
        studentId: student.id 
      }
    })
    finalResult.value = res.data.data
    showResult.value = true
    ElMessage.success('考试完成！')
  } catch (err) {
    console.error('完成考试失败', err)
    
    if (err.response?.data?.code === 0) {
      ElMessage.error(err.response.data.msg || '提交失败')
      if (err.response.data.msg?.includes('移出班级') || err.response.data.msg?.includes('未通过审核')) {
        sessionStorage.clear()
        router.push('/login')
      }
    } else {
      ElMessage.error('提交失败')
    }
  }
}

// 返回列表
const backToList = () => {
  router.push('/student/exams')
}
</script>

<template>
  <div class="exam-container">
    <!-- 考试答题界面 -->
    <el-card v-if="!showResult && questions.length > 0" class="exam-card">
      <div class="exam-header">
        <h3>在线考试</h3>
        <el-tag type="info">
          第 {{ currentIndex + 1 }} / {{ questions.length }} 题
        </el-tag>
      </div>
      
      <el-progress 
        :percentage="Math.round((answeredQuestions.length / questions.length) * 100)" 
        :stroke-width="8"
        :color="'#409eff'"
        style="margin-bottom: 20px;"
      />
      
      <div class="question-nav">
        <div 
          v-for="(q, index) in questions" 
          :key="q.questionId"
          :class="['question-dot', {
            'active': index === currentIndex,
            'answered': answeredQuestions.some(a => a.questionId === q.questionId)
          }]"
          @click="goToQuestion(index)"
        >
          {{ index + 1 }}
        </div>
      </div>

      <div class="question-area">
        <h4>题目 {{ currentIndex + 1 }}：</h4>
        <div class="question-content">
          {{ questions[currentIndex].question }}
        </div>
      </div>

      <div class="answer-area">
        <h4>你的答案：</h4>
        <el-input
          v-model="answer"
          type="textarea"
          :rows="4"
          placeholder="请输入你的答案"
          :disabled="result !== null"
        />
      </div>

      <div class="action-buttons">
        <el-button 
          v-if="currentIndex > 0"
          @click="goToQuestion(currentIndex - 1)"
        >
          上一题
        </el-button>
        
        <el-button 
          v-if="result === null" 
          type="primary" 
          @click="submitAnswer" 
          :loading="loading"
          :disabled="!answer.trim()"
        >
          提交答案
        </el-button>
        
        <template v-if="result !== null">
          <el-button type="warning" @click="result = null; answer = ''">
            修改答案
          </el-button>
          <el-button type="success" @click="nextQuestion">
            {{ currentIndex < questions.length - 1 ? '下一题' : '完成考试' }}
          </el-button>
        </template>
      </div>

      <div v-if="result" class="result-feedback">
        <el-alert 
          :title="result.isCorrect ? '回答正确！' : '回答有误'" 
          :type="result.isCorrect ? 'success' : 'error'"
          :closable="false"
          show-icon
        />
        
        <el-descriptions :column="1" border style="margin-top: 20px;">
          <el-descriptions-item label="准确率">
            {{ result.accuracy }}%
          </el-descriptions-item>
          <el-descriptions-item label="正确答案">
            {{ result.correctAnswer }}
          </el-descriptions-item>
          <el-descriptions-item label="AI评价">
            {{ result.reason }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

    <!-- 考试完成结果 -->
    <el-card v-if="showResult && finalResult" class="result-card">
      <div class="result-header">
        <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" width="80" height="80">
          <path fill="#67c23a" d="M512 64a448 448 0 1 1 0 896 448 448 0 0 1 0-896zm-55.808 536.384l-99.52-99.584a38.4 38.4 0 1 0-54.336 54.336l126.72 126.72a38.272 38.272 0 0 0 54.336 0l262.4-262.464a38.4 38.4 0 1 0-54.272-54.336L456.192 600.384z"/>
        </svg>
        <h1>考试完成！</h1>
      </div>

      <el-divider />

      <el-descriptions :column="2" border size="large">
        <el-descriptions-item label="总题数">
          <el-tag type="info" size="large">{{ finalResult.totalQuestions }} 题</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="正确数">
          <el-tag type="success" size="large">{{ finalResult.correctCount }} 题</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="平均准确率" :span="2">
          <div style="display: flex; align-items: center;">
            <el-progress 
              :percentage="Number(finalResult.avgAccuracy)" 
              :color="finalResult.avgAccuracy >= 60 ? '#67c23a' : '#f56c6c'"
              :stroke-width="20"
              style="width: 300px;"
            />
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="AI综合评价" :span="2">
          <div class="ai-review">
            {{ finalResult.aiReview }}
          </div>
        </el-descriptions-item>
      </el-descriptions>

      <div style="text-align: center; margin-top: 30px;">
        <el-button type="primary" size="large" @click="backToList">
          返回考试列表
        </el-button>
      </div>
    </el-card>

    <el-card v-if="!showResult && questions.length === 0">
      <el-empty description="该考试暂无题目">
        <el-button type="primary" @click="backToList">返回列表</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<style scoped>
.exam-container {
  max-width: 900px;
  margin: 20px auto;
  padding: 0 20px;
}

.exam-card, .result-card {
  padding: 20px;
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.question-nav {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 20px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 8px;
}

.question-dot {
  width: 35px;
  height: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  border: 2px solid #dcdfe6;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.question-dot.active {
  border-color: #409eff;
  background: #ecf5ff;
  color: #409eff;
}

.question-dot.answered {
  background: #67c23a;
  border-color: #67c23a;
  color: white;
}

.question-content {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  font-size: 16px;
  line-height: 1.8;
  margin: 10px 0;
}

.action-buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  justify-content: center;
}

.result-feedback {
  margin-top: 30px;
}

.result-header {
  text-align: center;
  padding: 30px 0;
}

.result-header h1 {
  margin-top: 20px;
  color: #67c23a;
}

.ai-review {
  background: #f0f9eb;
  padding: 20px;
  border-radius: 8px;
  line-height: 1.8;
  font-size: 15px;
  color: #333;
  border-left: 4px solid #67c23a;
}
</style>