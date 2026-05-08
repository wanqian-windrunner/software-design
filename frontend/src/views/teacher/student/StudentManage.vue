<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const teacher = JSON.parse(sessionStorage.getItem('userInfo'))
const students = ref([])

const verifyMap = {
  0: { text: '待审', type: 'warning' },
  1: { text: '已通过', type: 'success' }
}

const loadStudents = async () => {
  const res = await axios.get('/api/teacher/students', {
    params: { clazzId: teacher.clazzId }
  })
  students.value = res.data.data
}

onMounted(loadStudents)

const audit = async (id, verify) => {
  await axios.put('/api/teacher/student/verify', null, {
    params: { studentId: id, verify }
  })
  ElMessage.success('操作成功')
  loadStudents()
}

const remove = (id) => {
  ElMessageBox.confirm('确定将该学生移出班级吗？', '提示', { type: 'warning' })
    .then(async () => {
      await axios.delete('/api/teacher/student', {
        params: { clazzId: teacher.clazzId, studentId: id }
      })
      ElMessage.success('已移除')
      loadStudents()
    })
}
</script>

<template>
  <el-card>
    <h2>学生管理</h2>

    <el-table :data="students" style="width: 100%">
      <el-table-column prop="id" label="学号" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="clazzId" label="班级" />

      <el-table-column label="状态">
        <template #default="{ row }">
          <el-tag :type="verifyMap[row.verify].type">
            {{ verifyMap[row.verify].text }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button
            v-if="row.verify === 0"
            type="success"
            size="small"
            @click="audit(row.id, 1)"
          >
            通过
          </el-button>


          <el-button
            type="danger"
            size="small"
            @click="remove(row.id)"
          >
            踢出
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>