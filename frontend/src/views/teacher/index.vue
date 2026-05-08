<template>
  <div class="courseware-container">
    <!-- 接口鉴权配置 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon :size="24" color="#e6a23c"><Key /></el-icon>
          <h2>接口鉴权</h2>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="12">
          <div class="filter-item">
            <label class="filter-label">🔐 AppId</label>
            <el-input v-model="appId" placeholder="请输入应用ID" clearable />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="filter-item">
            <label class="filter-label">🗝️ Secret</label>
            <el-input v-model="secret" placeholder="请输入应用秘钥" show-password clearable />
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- PPT主题查询区域 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon :size="24" color="#409eff"><Search /></el-icon>
          <h2>PPT主题列表查询</h2>
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
          查询主题
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
      <el-empty v-else-if="!loading && !errorMsg" description="暂无匹配主题" />
      <div v-if="errorMsg" class="error-msg">
        <el-alert :title="errorMsg" type="error" show-icon :closable="false" />
      </div>
    </el-card>

    <!-- PPT生成区域 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon :size="24" color="#67c23a"><MagicStick /></el-icon>
          <h2>PPT生成</h2>
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
            placeholder="请输入模板ID（templateIndexId，可从上方主题列表获取）"
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
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Picture, MagicStick, Key, Notebook, Connection, PictureFilled } from '@element-plus/icons-vue'

const apiBaseUrl = 'https://zwapi.xfyun.cn/api/ppt/v2'

const appId = ref(sessionStorage.getItem('pptAppId') || '')
const secret = ref(sessionStorage.getItem('pptSecret') || '')
const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

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

watch(appId, (value) => {
  sessionStorage.setItem('pptAppId', value.trim())
})

watch(secret, (value) => {
  sessionStorage.setItem('pptSecret', value.trim())
})

function ensureAuth() {
  if (!appId.value.trim() || !secret.value.trim()) {
    ElMessage.warning('请先填写 AppId 和 Secret')
    throw new Error('missing-auth')
  }
}

async function buildAuthHeaders() {
  ensureAuth()
  const timestamp = Math.floor(Date.now() / 1000).toString()
  const signature = await getSignature(appId.value.trim(), secret.value.trim(), timestamp)
  return {
    appId: appId.value.trim(),
    timestamp,
    signature
  }
}

async function getSignature(appIdValue, secretValue, timestamp) {
  const auth = md5(`${appIdValue}${timestamp}`)
  return hmacSha1Base64(auth, secretValue)
}

async function hmacSha1Base64(message, key) {
  const encoder = new TextEncoder()
  const cryptoKey = await crypto.subtle.importKey(
    'raw',
    encoder.encode(key),
    { name: 'HMAC', hash: 'SHA-1' },
    false,
    ['sign']
  )
  const signature = await crypto.subtle.sign('HMAC', cryptoKey, encoder.encode(message))
  return toBase64(signature)
}

function toBase64(buffer) {
  const bytes = new Uint8Array(buffer)
  let binary = ''
  bytes.forEach((byte) => {
    binary += String.fromCharCode(byte)
  })
  return btoa(binary)
}

function md5(str) {
  return hex(md51(str))
}

function md51(str) {
  const n = str.length
  const state = [1732584193, -271733879, -1732584194, 271733878]
  let i

  for (i = 64; i <= n; i += 64) {
    md5cycle(state, md5blk(str.substring(i - 64, i)))
  }
  str = str.substring(i - 64)
  const tail = Array(16).fill(0)
  for (i = 0; i < str.length; i += 1) {
    tail[i >> 2] |= str.charCodeAt(i) << ((i % 4) << 3)
  }
  tail[i >> 2] |= 0x80 << ((i % 4) << 3)
  if (i > 55) {
    md5cycle(state, tail)
    for (i = 0; i < 16; i += 1) {
      tail[i] = 0
    }
  }
  tail[14] = n * 8
  md5cycle(state, tail)
  return state
}

function md5blk(str) {
  const blocks = []
  for (let i = 0; i < 64; i += 4) {
    blocks[i >> 2] =
      str.charCodeAt(i) +
      (str.charCodeAt(i + 1) << 8) +
      (str.charCodeAt(i + 2) << 16) +
      (str.charCodeAt(i + 3) << 24)
  }
  return blocks
}

function md5cycle(x, k) {
  let [a, b, c, d] = x

  a = ff(a, b, c, d, k[0], 7, -680876936)
  d = ff(d, a, b, c, k[1], 12, -389564586)
  c = ff(c, d, a, b, k[2], 17, 606105819)
  b = ff(b, c, d, a, k[3], 22, -1044525330)
  a = ff(a, b, c, d, k[4], 7, -176418897)
  d = ff(d, a, b, c, k[5], 12, 1200080426)
  c = ff(c, d, a, b, k[6], 17, -1473231341)
  b = ff(b, c, d, a, k[7], 22, -45705983)
  a = ff(a, b, c, d, k[8], 7, 1770035416)
  d = ff(d, a, b, c, k[9], 12, -1958414417)
  c = ff(c, d, a, b, k[10], 17, -42063)
  b = ff(b, c, d, a, k[11], 22, -1990404162)
  a = ff(a, b, c, d, k[12], 7, 1804603682)
  d = ff(d, a, b, c, k[13], 12, -40341101)
  c = ff(c, d, a, b, k[14], 17, -1502002290)
  b = ff(b, c, d, a, k[15], 22, 1236535329)

  a = gg(a, b, c, d, k[1], 5, -165796510)
  d = gg(d, a, b, c, k[6], 9, -1069501632)
  c = gg(c, d, a, b, k[11], 14, 643717713)
  b = gg(b, c, d, a, k[0], 20, -373897302)
  a = gg(a, b, c, d, k[5], 5, -701558691)
  d = gg(d, a, b, c, k[10], 9, 38016083)
  c = gg(c, d, a, b, k[15], 14, -660478335)
  b = gg(b, c, d, a, k[4], 20, -405537848)
  a = gg(a, b, c, d, k[9], 5, 568446438)
  d = gg(d, a, b, c, k[14], 9, -1019803690)
  c = gg(c, d, a, b, k[3], 14, -187363961)
  b = gg(b, c, d, a, k[8], 20, 1163531501)
  a = gg(a, b, c, d, k[13], 5, -1444681467)
  d = gg(d, a, b, c, k[2], 9, -51403784)
  c = gg(c, d, a, b, k[7], 14, 1735328473)
  b = gg(b, c, d, a, k[12], 20, -1926607734)

  a = hh(a, b, c, d, k[5], 4, -378558)
  d = hh(d, a, b, c, k[8], 11, -2022574463)
  c = hh(c, d, a, b, k[11], 16, 1839030562)
  b = hh(b, c, d, a, k[14], 23, -35309556)
  a = hh(a, b, c, d, k[1], 4, -1530992060)
  d = hh(d, a, b, c, k[4], 11, 1272893353)
  c = hh(c, d, a, b, k[7], 16, -155497632)
  b = hh(b, c, d, a, k[10], 23, -1094730640)
  a = hh(a, b, c, d, k[13], 4, 681279174)
  d = hh(d, a, b, c, k[0], 11, -358537222)
  c = hh(c, d, a, b, k[3], 16, -722521979)
  b = hh(b, c, d, a, k[6], 23, 76029189)
  a = hh(a, b, c, d, k[9], 4, -640364487)
  d = hh(d, a, b, c, k[12], 11, -421815835)
  c = hh(c, d, a, b, k[15], 16, 530742520)
  b = hh(b, c, d, a, k[2], 23, -995338651)

  a = ii(a, b, c, d, k[0], 6, -198630844)
  d = ii(d, a, b, c, k[7], 10, 1126891415)
  c = ii(c, d, a, b, k[14], 15, -1416354905)
  b = ii(b, c, d, a, k[5], 21, -57434055)
  a = ii(a, b, c, d, k[12], 6, 1700485571)
  d = ii(d, a, b, c, k[3], 10, -1894986606)
  c = ii(c, d, a, b, k[10], 15, -1051523)
  b = ii(b, c, d, a, k[1], 21, -2054922799)
  a = ii(a, b, c, d, k[8], 6, 1873313359)
  d = ii(d, a, b, c, k[15], 10, -30611744)
  c = ii(c, d, a, b, k[6], 15, -1560198380)
  b = ii(b, c, d, a, k[13], 21, 1309151649)
  a = ii(a, b, c, d, k[4], 6, -145523070)
  d = ii(d, a, b, c, k[11], 10, -1120210379)
  c = ii(c, d, a, b, k[2], 15, 718787259)
  b = ii(b, c, d, a, k[9], 21, -343485551)

  x[0] = add32(a, x[0])
  x[1] = add32(b, x[1])
  x[2] = add32(c, x[2])
  x[3] = add32(d, x[3])
}

function cmn(q, a, b, x, s, t) {
  return add32(((a + q + x + t) << s) | ((a + q + x + t) >>> (32 - s)), b)
}

function ff(a, b, c, d, x, s, t) {
  return cmn((b & c) | (~b & d), a, b, x, s, t)
}

function gg(a, b, c, d, x, s, t) {
  return cmn((b & d) | (c & ~d), a, b, x, s, t)
}

function hh(a, b, c, d, x, s, t) {
  return cmn(b ^ c ^ d, a, b, x, s, t)
}

function ii(a, b, c, d, x, s, t) {
  return cmn(c ^ (b | ~d), a, b, x, s, t)
}

const hexChars = '0123456789abcdef'

function rhex(n) {
  let s = ''
  for (let j = 0; j < 4; j += 1) {
    s += hexChars[(n >> (j * 8 + 4)) & 0x0f] + hexChars[(n >> (j * 8)) & 0x0f]
  }
  return s
}

function hex(x) {
  return x.map(rhex).join('')
}

function add32(a, b) {
  return (a + b) & 0xffffffff
}

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
  templates.value = []

  try {
    const authHeaders = await buildAuthHeaders()
    const payload = {
      pageNum: 1,
      pageSize: 12
    }
    if (filters.style) {
      payload.style = filters.style
    }
    if (filters.color) {
      payload.color = filters.color
    }
    if (filters.industry) {
      payload.industry = filters.industry
    }

    const res = await fetch(`${apiBaseUrl}/template/list`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...authHeaders },
      body: JSON.stringify(payload)
    })

    const result = await res.json()
    if (!result?.flag) {
      errorMsg.value = `查询失败：${result?.desc || '未知错误'}`
      return
    }

    templates.value = result?.data?.records || []
    if (templates.value.length === 0) {
      ElMessage.info('暂无匹配的主题')
    } else {
      ElMessage.success(`找到 ${templates.value.length} 个主题`)
    }
  } catch (err) {
    if (err?.message === 'missing-auth') {
      errorMsg.value = '请先填写 AppId 和 Secret'
      return
    }
    if (!errorMsg.value) {
      errorMsg.value = '查询失败，请稍后重试'
    }
  } finally {
    loading.value = false
  }
}

async function checkPptProgress(sid) {
  try {
    const authHeaders = await buildAuthHeaders()
    const res = await fetch(`${apiBaseUrl}/progress?sid=${sid}`, {
      headers: authHeaders
    })
    const text = await res.text()

    if (text.startsWith('<!DOCTYPE') || text.startsWith('<html')) {
      throw new Error('接口返回了HTML')
    }

    const data = JSON.parse(text)

    if (!data.flag) {
      pptResult.value = `<el-alert title="查询进度失败：${data.desc || '未知错误'}" type="error" show-icon />`
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
    if (e?.message === 'missing-auth') {
      pptResult.value = '<el-alert title="请先填写 AppId 和 Secret" type="error" show-icon />'
      return
    }
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
  formData.append('query', queryText.value.trim())
  if (templateId.value.trim()) {
    formData.append('templateId', templateId.value.trim())
  }
  formData.append('author', userInfo?.username || '智文')
  formData.append('isCardNote', isCardNote.value)
  formData.append('search', search.value)
  formData.append('language', 'cn')
  formData.append('isFigure', isFigure.value)
  if (isFigure.value) {
    formData.append('aiImage', aiImage.value)
  }

  try {
    const authHeaders = await buildAuthHeaders()
    const res = await fetch(`${apiBaseUrl}/create`, {
      method: 'POST',
      headers: authHeaders,
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
    if (err?.message === 'missing-auth') {
      pptResult.value = '<el-alert title="请先填写 AppId 和 Secret" type="error" show-icon />'
      return
    }
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
