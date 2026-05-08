<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const examId = route.params.examId
const user = JSON.parse(sessionStorage.getItem('userInfo'))

const result = ref(null)
const loading = ref(false)

const fetchResult = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/student/exam/result', {
      params: { 
        examId: Number(examId), 
        studentId: user.id 
      }
    })
    result.value = res.data.data
  } catch (err) {
    console.error('获取考试结果失败', err)
  } finally {
    loading.value = false
  }
}

const backToList = () => {
  router.push('/student/exams')
}

onMounted(fetchResult)
</script>

<template>
  <div class="result-container">
    <el-card v-loading="loading" class="result-card">
      <template v-if="result">
        <div class="result-header">
          <el-icon :size="80" color="#67c23a">
            <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" width="80" height="80">
              <path fill="#67c23a" d="M512 64a448 448 0 1 1 0 896 448 448 0 0 1 0-896zm-55.808 536.384l-99.52-99.584a38.4 38.4 0 1 0-54.336 54.336l126.72 126.72a38.272 38.272 0 0 0 54.336 0l262.4-262.464a38.4 38.4 0 1 0-54.272-54.336L456.192 600.384z"/>
            </svg>
          </el-icon>
          <h1>考试完成</h1>
          <p style="color: #909399; margin-top: 10px;">{{ result.examName }}</p>
        </div>

        <el-divider />

        <el-descriptions :column="2" border size="large">
          <el-descriptions-item label="平均准确率" :span="2">
            <div style="display: flex; align-items: center;">
              <el-progress 
                :percentage="Number(result.accuracy)" 
                :color="result.accuracy >= 60 ? '#67c23a' : '#f56c6c'"
                :stroke-width="20"
                style="width: 300px;"
              />
            </div>
          </el-descriptions-item>
          
          <el-descriptions-item label="考试时间" :span="2">
            {{ result.createTime }}
          </el-descriptions-item>
          
          <el-descriptions-item label="AI综合评价" :span="2">
            <div class="ai-review">
              {{ result.aiReview || '暂无评价' }}
            </div>
          </el-descriptions-item>
        </el-descriptions>

        <div style="text-align: center; margin-top: 30px;">
          <el-button type="primary" size="large" @click="backToList">
            返回考试列表
          </el-button>
        </div>
      </template>

      <el-empty v-else-if="!loading" description="暂无考试结果" />
    </el-card>
  </div>
</template>

<style scoped>
.result-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

.result-card {
  padding: 20px;
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