<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const examId = route.params.examId

const questions = ref([])
const dialogVisible = ref(false)
const editDialogVisible = ref(false)
const isEdit = ref(false)
const editingQuestion = ref(null)

const form = ref({
  question: '',
  answer: '',
  state: 1
})

/* 获取题目 */
const fetchQuestions = async () => {
  try {
    const res = await axios.get('/api/teacher/questions', {
      params: { examId }
    })
    questions.value = res.data.data
  } catch (err) {
    console.error('获取题目失败', err)
    ElMessage.error('获取题目失败')
  }
}

/* 新增题目 */
const addQuestion = async () => {
  if (!form.value.question.trim()) {
    ElMessage.warning('请输入题目内容')
    return
  }
  if (!form.value.answer.trim()) {
    ElMessage.warning('请输入正确答案')
    return
  }

  try {
    if (isEdit.value && editingQuestion.value) {
      // 编辑模式
      await axios.put('/api/teacher/question', {
        examId: Number(examId),
        questionId: editingQuestion.value.questionId,
        question: form.value.question,
        answer: form.value.answer,
        state: form.value.state
      })
      ElMessage.success('修改成功')
    } else {
      // 新增模式
      await axios.post('/api/teacher/question', {
        examId: Number(examId),
        question: form.value.question,
        answer: form.value.answer,
        state: form.value.state
      })
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    editDialogVisible.value = false
    
    resetForm()
    fetchQuestions()
  } catch (err) {
    console.error('操作失败', err)
    ElMessage.error('操作失败：' + (err.response?.data?.msg || '服务器错误'))
  }
}

/* 打开编辑对话框 */
const openEditDialog = (row) => {
  isEdit.value = true
  editingQuestion.value = row
  form.value = {
    question: row.question,
    answer: row.answer,
    state: row.state
  }
  editDialogVisible.value = true
}

/* 打开新增对话框 */
const openAddDialog = () => {
  isEdit.value = false
  editingQuestion.value = null
  resetForm()
  dialogVisible.value = true
}

/* 重置表单 */
const resetForm = () => {
  form.value = {
    question: '',
    answer: '',
    state: 1
  }
}

/* 切换题目状态 */
const toggleState = async (row) => {
  const newState = row.state === 1 ? 0 : 1
  const actionText = newState === 1 ? '启用' : '禁用'
  
  try {
    await axios.put('/api/teacher/question/state', null, {
      params: {
        examId: examId,
        questionId: row.questionId,
        state: newState
      }
    })
    ElMessage.success(`${actionText}成功`)
    fetchQuestions()
  } catch (err) {
    console.error('状态更新失败', err)
    ElMessage.error('操作失败')
  }
}

/* 删除题目 */
const deleteQuestion = (row) => {
  ElMessageBox.confirm(
    '确定删除该题目吗？删除后不可恢复！',
    '警告',
    {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    }
  ).then(async () => {
    try {
      await axios.delete('/api/teacher/question', {
        params: {
          examId: examId,
          questionId: row.questionId
        }
      })
      ElMessage.success('删除成功')
      fetchQuestions()
    } catch (err) {
      console.error('删除失败', err)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(fetchQuestions)
</script>

<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h2>题库管理 - 考试 {{ examId }}</h2>
      <el-button type="primary" @click="openAddDialog">
        + 新增题目
      </el-button>
    </div>

    <el-table :data="questions" border stripe style="margin-top: 20px">
      <el-table-column prop="questionId" label="题号" width="80" align="center" />
      <el-table-column prop="question" label="题目" min-width="300" show-overflow-tooltip />
      

      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.state === 1 ? 'success' : 'info'">
            {{ row.state === 1 ? '启用' : '未启用' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="250" align="center">
        <template #default="{ row }">
          <el-button 
            size="small" 
            type="primary"
            @click="openEditDialog(row)"
          >
            编辑
          </el-button>

          <el-button 
            size="small" 
            :type="row.state === 1 ? 'warning' : 'success'"
            @click="toggleState(row)"
          >
            {{ row.state === 1 ? '禁用' : '启用' }}
          </el-button>
          
          <el-button 
            size="small" 
            type="danger"
            @click="deleteQuestion(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增题目对话框 -->
    <el-dialog v-model="dialogVisible" title="新增题目" width="700px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="题目内容" required>
          <el-input 
            v-model="form.question" 
            type="textarea"
            :rows="4"
            placeholder="请输入题目内容" 
          />
        </el-form-item>
        
        <el-form-item label="正确答案" required>
          <el-input 
            v-model="form.answer" 
            type="textarea"
            :rows="4"
            placeholder="请输入正确答案" 
          />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-radio-group v-model="form.state">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addQuestion">确认添加</el-button>
      </template>
    </el-dialog>

    <!-- 编辑题目对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑题目" width="700px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="题号">
          <el-input :model-value="editingQuestion?.questionId" disabled />
        </el-form-item>
        
        <el-form-item label="题目内容" required>
          <el-input 
            v-model="form.question" 
            type="textarea"
            :rows="4"
            placeholder="请输入题目内容" 
          />
        </el-form-item>
        
        <el-form-item label="正确答案" required>
          <el-input 
            v-model="form.answer" 
            type="textarea"
            :rows="4"
            placeholder="请输入正确答案" 
          />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-radio-group v-model="form.state">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addQuestion">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
h2 {
  margin: 0;
}
</style>