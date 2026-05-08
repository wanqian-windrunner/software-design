<template>
  <div class="courseware-container">
    <!-- PPT模板查询区域 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon :size="24" color="#409eff"><Search /></el-icon>
          <h2>PPT模板筛选查询</h2>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="8">
          <div class="filter-item">
            <label class="filter-label">🎨 风格</label>
            <el-select v-model="filters.style" placeholder="选择风格" clearable style="width: 100%">
              <el-option label="全部" value="" />
              <el-option label="简约" value="简约" />
              <el-option label="卡通" value="卡通" />
              <el-option label="商务" value="商务" />
              <el-option label="创意" value="创意" />
              <el-option label="国风" value="国风" />
              <el-option label="清新" value="清新" />
              <el-option label="扁平" value="扁平" />
              <el-option label="插画" value="插画" />
              <el-option label="节日" value="节日" />
            </el-select>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="filter-item">
            <label class="filter-label">🎯 颜色</label>
            <el-select v-model="filters.color" placeholder="选择颜色" clearable style="width: 100%">
              <el-option label="全部" value="" />
              <el-option label="蓝色" value="蓝色" />
              <el-option label="绿色" value="绿色" />
              <el-option label="红色" value="红色" />
              <el-option label="紫色" value="紫色" />
              <el-option label="黑色" value="黑色" />
              <el-option label="灰色" value="灰色" />
              <el-option label="黄色" value="黄色" />
              <el-option label="粉色" value="粉色" />
              <el-option label="橙色" value="橙色" />
            </el-select>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="filter-item">
            <label class="filter-label">📋 行业</label>
            <el-select v-model="filters.industry" placeholder="选择行业" clearable style="width: 100%">
              <el-option label="全部" value="" />
              <el-option label="科技互联网" value="科技互联网" />
              <el-option label="教育培训" value="教育培训" />
              <el-option label="政务" value="政务" />
              <el-option label="学院" value="学院" />
              <el-option label="电子商务" value="电子商务" />
              <el-option label="金融战略" value="金融战略" />
              <el-option label="法律" value="法律" />
              <el-option label="医疗健康" value="医疗健康" />
              <el-option label="文旅体育" value="文旅体育" />
              <el-option label="艺术广告" value="艺术广告" />
              <el-option label="人力资源" value="人力资源" />
              <el-option label="游戏娱乐" value="游戏娱乐" />
            </el-select>
          </div>
        </el-col>
      </el-row>

      <div class="query-btn">
        <el-button type="primary" :icon="Search" @click="queryTemplates" :loading="loading" size="large">
          查询模板
        </el-button>
      </div>

      <!-- 模板列表 -->
      <div v-if="templates.length > 0" class="template-grid">
        <div v-for="tpl in templates" :key="tpl.templateIndexId" class="template-card">
          <el-image :src="getCoverImage(tpl)" fit="cover" class="template-image">
            <template #error>
              <div class="image-placeholder">
                <el-icon :size="40"><Picture /></el-icon>
                <span>暂无封面</span>
              </div>
            </template>
          </el-image>
          <div class="template-info">
            <el-tag type="primary" size="small">ID: {{ tpl.templateIndexId }}</el-tag>
            <div class="template-tags">
              <el-tag size="small">{{ tpl.style }}</el-tag>
              <el-tag size="small" type="success">{{ tpl.color }}</el-tag>
              <el-tag size="small" type="warning">{{ tpl.industry }}</el-tag>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else-if="!loading && !errorMsg" description="暂无匹配模板" />
      <div v-if="errorMsg" class="error-msg">
        <el-alert :title="errorMsg" type="error" show-icon :closable="false" />
      </div>
    </el-card>

    <!-- PPT生成区域 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon :size="24" color="#67c23a"><MagicStick /></el-icon>
          <h2>AI 生成 PPT</h2>
        </div>
      </template>

      <el-form label-position="top">
        <el-form-item label="📝 生成要求">
          <el-input
            v-model="queryText"
            type="textarea"
            :rows="5"
            placeholder="请输入你要生成PPT的要求，例如：创建一份关于人工智能发展的PPT，包含发展历程、核心技术、应用场景等内容..."
          />
        </el-form-item>

        <el-form-item label="📄 模板ID">
          <el-input
            v-model="templateId"
            placeholder="请输入模板ID（可从上方模板查询中获取）"
            clearable
          >
            <template #prefix>
              <el-icon><Key /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="6">
            <el-checkbox v-model="isCardNote" border>
              <el-icon :size="16"><Notebook /></el-icon>
              演讲备注
            </el-checkbox>
          </el-col>
          <el-col :span="6">
            <el-checkbox v-model="search" border>
              <el-icon :size="16"><Connection /></el-icon>
              联网搜索
            </el-checkbox>
          </el-col>
          <el-col :span="6">
            <el-checkbox v-model="isFigure" border>
              <el-icon :size="16"><PictureFilled /></el-icon>
              自动配图
            </el-checkbox>
          </el-col>
          <el-col :span="6">
            <el-select v-model="aiImage" :disabled="!isFigure" placeholder="配图类型" style="width: 100%">
              <el-option label="普通配图" value="normal" />
              <el-option label="高级配图" value="advanced" />
            </el-select>
          </el-col>
        </el-row>

        <div class="generate-btn">
          <el-button type="success" :icon="MagicStick" @click="createPPT" :loading="generating" size="large">
            生成PPT
          </el-button>
        </div>
      </el-form>

      <!-- 生成结果 -->
      <div v-if="pptResult" class="ppt-result">
        <el-divider />
        <div v-html="pptResult"></div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Picture, MagicStick, Key, Notebook, Connection, PictureFilled } from '@element-plus/icons-vue'

const baseUrl = 'http://127.0.0.1:5000'

const filters = reactive({
  style: '',
  color: '',
  industry: ''
})

const templates = ref([])
const loading = ref(false)
const errorMsg = ref('')
const generating = ref(false)

const queryText = ref('')
const templateId = ref('')
const isCardNote = ref(false)
const search = ref(false)
const isFigure = ref(false)
const aiImage = ref('normal')
const pptResult = ref('')

function getCoverImage(tpl) {
  try {
    const imgObj = JSON.parse(tpl.detailImage || '{}')
    return imgObj?.titleCoverImage || ''
  } catch {
    return ''
  }
}

async function queryTemplates() {
  loading.value = true
  errorMsg.value = ''

  try {
    const res = await fetch(baseUrl + '/ppt/templates', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        style: filters.style,
        color: filters.color,
        industry: filters.industry,
        pageNum: 1,
        pageSize: 12
      })
    })

    const result = await res.json()
    templates.value = result?.data?.records || []
    if (templates.value.length === 0) {
      ElMessage.info('暂无匹配的模板')
    } else {
      ElMessage.success(`找到 ${templates.value.length} 个模板`)
    }
  } catch (err) {
    errorMsg.value = '查询失败，请稍后重试'
    templates.value = []
  } finally {
    loading.value = false
  }
}

async function checkPptProgress(sid) {
  try {
    const res = await fetch(`${baseUrl}/ppt/progress?sid=${sid}`)
    const text = await res.text()

    if (text.startsWith('<!DOCTYPE') || text.startsWith('<html')) {
      throw new Error('接口返回了HTML')
    }

    const data = JSON.parse(text)

    if (!data.flag) {
      pptResult.value = `<el-alert title="查询进度失败：${data.desc}" type="error" show-icon />`
      return
    }

    const status = data.data.pptStatus
    if (status === 'done') {
      const pptUrl = data.data.pptUrl
      pptResult.value += `
        <div style="text-align: center; margin-top: 20px;">
          <a href="${pptUrl}" target="_blank" style="display: inline-block; background: #409eff; color: white; padding: 12px 30px; border-radius: 8px; text-decoration: none; font-size: 16px;">
            ⬇️ 下载PPT
          </a>
        </div>
      `
    } else if (status === 'building') {
      pptResult.value = `
        <div style="text-align: center; padding: 20px;">
          <el-icon class="is-loading" :size="24"><Loading /></el-icon>
          <p style="margin-top: 10px; color: #409eff;">PPT生成中，请稍候...</p>
        </div>
      `
      setTimeout(() => checkPptProgress(sid), 3000)
    } else if (status === 'build_failed') {
      pptResult.value = `<el-alert title="PPT生成失败：${data.data.errMsg || '未知错误'}" type="error" show-icon />`
    }
  } catch (e) {
    pptResult.value = '<el-alert title="查询进度接口请求失败" type="error" show-icon />'
  }
}

async function createPPT() {
  if (!queryText.value.trim()) {
    ElMessage.warning('请输入生成PPT的内容！')
    return
  }

  generating.value = true
  pptResult.value = `
    <div style="text-align: center; padding: 20px;">
      <el-icon class="is-loading" :size="24"><Loading /></el-icon>
      <p style="margin-top: 10px;">正在生成PPT，请稍候...</p>
    </div>
  `

  const formData = new FormData()
  formData.append('query', queryText.value)
  formData.append('templateId', templateId.value || 'default')
  formData.append('author', '智文用户')
  formData.append('isCardNote', isCardNote.value)
  formData.append('search', search.value)
  formData.append('language', 'cn')
  formData.append('isFigure', isFigure.value)
  formData.append('aiImage', aiImage.value)

  try {
    const res = await fetch(baseUrl + '/ppt/create', {
      method: 'POST',
      body: formData
    })

    const result = await res.json()
    if (result?.flag && result?.data?.sid) {
      const { sid, coverImgSrc, title, subTitle } = result.data

      pptResult.value = `
        <div style="text-align: center;">
          <img src="${coverImgSrc}" style="max-width: 300px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); margin-bottom: 16px;" />
          <h3 style="color: #333;">${title}</h3>
          <p style="color: #909399;">${subTitle || ''}</p>
          <el-tag type="info" style="margin-top: 10px;">任务ID：${sid}</el-tag>
          <div style="margin-top: 20px; color: #67c23a; font-weight: bold;">等待PPT生成中...</div>
        </div>
      `

      checkPptProgress(sid)
    } else {
      pptResult.value = `<el-alert title="生成失败：${result?.desc || '未知错误'}" type="error" show-icon />`
    }
  } catch (err) {
    pptResult.value = '<el-alert title="请求出错，请稍后重试" type="error" show-icon />'
  } finally {
    generating.value = false
  }
}
</script>

<style scoped>
.courseware-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.section-card {
  margin-bottom: 24px;
  border-radius: 12px;
}

.section-card :deep(.el-card__header) {
  border-bottom: 1px solid #f0f0f0;
  padding: 20px 24px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.filter-item {
  margin-bottom: 10px;
}

.filter-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #555;
  font-size: 14px;
}

.query-btn {
  text-align: center;
  margin-top: 24px;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
  margin-top: 24px;
}

.template-card {
  border: 1px solid #eee;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  background: white;
}

.template-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.template-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.image-placeholder {
  height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
  gap: 8px;
}

.template-info {
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.template-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.error-msg {
  margin-top: 20px;
}

.generate-btn {
  text-align: center;
  margin-top: 24px;
}

.ppt-result {
  padding: 20px 0;
}

:deep(.el-checkbox) {
  margin-right: 0;
}

:deep(.el-checkbox__label) {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>