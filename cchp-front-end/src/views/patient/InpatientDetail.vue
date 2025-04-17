<template>
  <div class="inpatient-detail-container">
    <!-- 返回按钮 -->
    <div class="back-button">
      <button @click="goBack"
              class="btn-back">
        <i class="fas fa-arrow-left"></i> 返回列表
      </button>
    </div>

    <!-- 住院记录头信息卡片 -->
    <div class="info-card header-card">
      <div class="card-header">
        <i class="fas fa-procedures"></i>
        <h3>住院记录详情</h3>
      </div>
      <div class="card-content">
        <div class="header-grid">
          <div class="header-item">
            <span class="label">住院号：</span>
            <span class="value">{{ inpatientRecord.AdmissionNumber }}</span>
          </div>
          <div class="header-item">
            <span class="label">入院日期：</span>
            <span class="value">{{ formatDate(inpatientRecord.DiagnosisDate) }}</span>
          </div>
          <div class="header-item">
            <span class="label">健康卡号：</span>
            <span class="value">{{ inpatientRecord.ResidentHealthCardID }}</span>
          </div>
          <div class="header-item">
            <span class="label">医疗机构：</span>
            <span class="value">{{ inpatientRecord.InstitutionCode }}</span>
          </div>
          <div class="header-item">
            <span class="label">入院病情：</span>
            <span class="value">{{ getAdmissionCondition(inpatientRecord.AdmissionConditionCode) }}</span>
          </div>
          <div class="header-item highlight">
            <span class="label">住院总费用：</span>
            <span class="value">¥{{ inpatientRecord.TotalHospitalizationCost.toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 病程记录卡片列表 -->
    <div class="course-records-section">
      <h3 class="section-title">
        <i class="fas fa-clipboard-list"></i>
        病程记录 ({{ courseRecords.length }}条)
      </h3>

      <!-- 单个病程记录卡片 -->
      <div class="info-card course-card"
           v-for="(record, index) in courseRecords"
           :key="index">
        <div class="card-header">
          <div class="record-time">
            <i class="far fa-clock"></i>
            {{ formatDateTime(record.RecordDateTime) }}
          </div>
          <div class="record-number">
            记录 #{{ index + 1 }}
          </div>
        </div>
        <div class="card-content">
          <div class="record-content">
            {{ record.RecordContent }}
          </div>
        </div>
      </div>

      <!-- 无病程记录提示 -->
      <div class="info-card empty-card"
           v-if="courseRecords.length === 0">
        <div class="card-content">
          <i class="far fa-folder-open"></i>
          <p>暂无病程记录</p>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <button @click="printRecord"
              class="btn-action btn-print">
        <i class="fas fa-print"></i> 打印记录
      </button>
      <button @click="downloadRecord"
              class="btn-action btn-download">
        <i class="fas fa-download"></i> 下载PDF
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

interface InpatientRecord {
  AdmissionRecordID: number
  ResidentHealthCardID: string
  InstitutionCode: string
  AdmissionNumber: string
  AdmissionConditionCode: string
  TotalHospitalizationCost: number
  DiagnosisDate: string
}

interface CourseRecord {
  AdmissionRecordID: number
  RecordContent: string
  RecordDateTime: string
}

const route = useRoute()
const router = useRouter()

// 住院记录数据
const inpatientRecord = ref<InpatientRecord>({
  AdmissionRecordID: 0,
  ResidentHealthCardID: '',
  InstitutionCode: '',
  AdmissionNumber: '',
  AdmissionConditionCode: '',
  TotalHospitalizationCost: 0,
  DiagnosisDate: '',
})

// 病程记录数据
const courseRecords = ref<CourseRecord[]>([])

// 从路由参数获取记录ID
const recordId = ref(Number(route.params.id))

// 模拟API获取住院记录详情
const fetchInpatientRecord = async (id: number) => {
  try {
    // 模拟数据
    const mockRecord: InpatientRecord = {
      AdmissionRecordID: id,
      ResidentHealthCardID: '123456789012345678',
      InstitutionCode: 'B987654321',
      AdmissionNumber: 'ZY2023052001',
      AdmissionConditionCode: '2',
      TotalHospitalizationCost: 12568.5,
      DiagnosisDate: '2023-05-20',
    }

    inpatientRecord.value = mockRecord

    // 获取病程记录
    await fetchCourseRecords(id)
  } catch (error) {
    console.error('获取住院记录失败:', error)
  }
}

// 模拟API获取病程记录
const fetchCourseRecords = async (recordId: number) => {
  try {
    // 模拟数据
    const mockRecords: CourseRecord[] = [
      {
        AdmissionRecordID: recordId,
        RecordContent:
          '患者因"反复头晕、头痛3年，加重1周"入院。查体：BP 160/100mmHg，神清，双肺呼吸音清，心率80次/分，律齐。初步诊断：高血压3级，极高危组。给予降压、改善循环等治疗。',
        RecordDateTime: '2023-05-20T10:30:00',
      },
      {
        AdmissionRecordID: recordId,
        RecordContent:
          '今日查房，患者诉头晕症状较前减轻。血压140/90mmHg。调整降压药物剂量，继续观察病情变化。',
        RecordDateTime: '2023-05-21T09:15:00',
      },
      {
        AdmissionRecordID: recordId,
        RecordContent:
          '患者病情稳定，血压控制在130/85mmHg左右。各项检查结果回报基本正常。拟明日安排出院。',
        RecordDateTime: '2023-05-25T16:45:00',
      },
    ]

    courseRecords.value = mockRecords
  } catch (error) {
    console.error('获取病程记录失败:', error)
  }
}

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return '未知日期'
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  }
  return new Date(dateString).toLocaleDateString('zh-CN', options)
}

// 格式化日期时间
const formatDateTime = (dateTimeString: string) => {
  if (!dateTimeString) return '未知时间'
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }
  return new Date(dateTimeString).toLocaleDateString('zh-CN', options)
}

// 获取入院病情描述
const getAdmissionCondition = (code: string) => {
  const conditions: Record<string, string> = {
    '1': '危',
    '2': '急',
    '3': '一般',
    '4': '未特指',
  }
  return conditions[code] || '未知病情'
}

// 打印记录
const printRecord = () => {
  window.print()
}

// 下载PDF (模拟)
const downloadRecord = () => {
  alert('PDF下载功能将在实际应用中实现')
}

// 返回列表
const goBack = () => {
  router.go(-1)
}

// 组件挂载时获取数据
onMounted(() => {
  fetchInpatientRecord(recordId.value)
})
</script>

<style scoped>
.inpatient-detail-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 返回按钮 */
.back-button {
  margin-bottom: 20px;
}

.btn-back {
  padding: 8px 16px;
  background-color: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  color: #606266;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-back:hover {
  background-color: #e4e7ed;
}

/* 通用卡片样式 */
.info-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  overflow: hidden;
}

.card-header {
  padding: 16px 20px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-header i {
  color: #409eff;
}

.card-content {
  padding: 20px;
}

/* 头部信息卡片 */
.header-card .card-header {
  background-color: #ecf5ff;
}

.header-card .card-header i {
  color: #409eff;
}

.header-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
}

.header-item {
  display: flex;
  min-height: 40px;
  align-items: center;
}

.header-item.highlight {
  background-color: #f0f9eb;
  padding: 0 10px;
  border-radius: 4px;
}

.label {
  font-weight: 600;
  color: #606266;
  min-width: 80px;
}

.value {
  color: #303133;
}

.highlight .value {
  color: #67c23a;
  font-weight: bold;
}

/* 病程记录部分 */
.section-title {
  font-size: 18px;
  color: #303133;
  margin: 30px 0 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  gap: 10px;
}

.course-records-section {
  margin-top: 30px;
}

/* 单个病程记录卡片 */
.course-card {
  margin-bottom: 15px;
  border-left: 4px solid #409eff;
}

.course-card .card-header {
  background-color: #f5f7fa;
  padding: 12px 20px;
}

.record-time {
  color: #606266;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.record-time i {
  color: #909399;
}

.record-number {
  background-color: #409eff;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.record-content {
  line-height: 1.8;
  color: #303133;
  white-space: pre-line;
}

/* 空状态卡片 */
.empty-card {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.empty-card i {
  font-size: 24px;
  margin-bottom: 10px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
}

.btn-action {
  padding: 10px 24px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-print {
  background-color: #409eff;
  color: white;
}

.btn-print:hover {
  background-color: #66b1ff;
}

.btn-download {
  background-color: #67c23a;
  color: white;
}

.btn-download:hover {
  background-color: #85ce61;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-grid {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
    gap: 12px;
  }

  .btn-action {
    width: 100%;
    justify-content: center;
  }
}

@media print {
  .back-button,
  .action-buttons {
    display: none;
  }

  .info-card {
    box-shadow: none;
    border: 1px solid #ddd;
    page-break-inside: avoid;
  }
}
</style>