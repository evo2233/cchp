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
            <span class="value">{{ inpatientRecord.admissionNumber }}</span>
          </div>
          <div class="header-item">
            <span class="label">入院日期：</span>
            <span class="value">{{ formatDate(inpatientRecord.diagnosisDate) }}</span>
          </div>
          <div class="header-item">
            <span class="label">健康卡号：</span>
            <span class="value">{{ inpatientRecord.residentHealthCardID }}</span>
          </div>
          <div class="header-item">
            <span class="label">医疗机构：</span>
            <span class="value">{{ inpatientRecord.institutionCode }}</span>
          </div>
          <div class="header-item">
            <span class="label">入院病情：</span>
            <span class="value">{{ getAdmissionCondition(inpatientRecord.admissionConditionCode) }}</span>
          </div>
          <div class="header-item highlight">
            <span class="label">住院总费用：</span>
            <span class="value">¥{{ inpatientRecord.totalHospitalizationCost.toFixed(2) }}</span>
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

    <!-- 病程记录添加按钮 -->
    <div class="add-course-record">
      <button @click="showAddCourseDialog"
              class="btn-add">
        <i class="fas fa-plus"></i> 添加病程记录
      </button>
    </div>

    <!-- 添加病程记录弹窗 -->
    <el-dialog v-model="courseDialogVisible"
               title="添加病程记录"
               width="50%"
               :close-on-click-modal="false">
      <el-form :model="newCourseRecord"
               label-width="100px">
        <el-form-item label="记录时间">
          <el-date-picker v-model="newCourseRecord.recordDateTime"
                          type="datetime"
                          placeholder="选择记录时间"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          :default-value="new Date()"
                          style="width: 100%" />
        </el-form-item>
        <el-form-item label="记录内容"
                      required>
          <el-input v-model="newCourseRecord.recordContent"
                    type="textarea"
                    :rows="5"
                    placeholder="请输入详细的病程记录内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="courseDialogVisible = false">取消</el-button>
        <el-button type="primary"
                   @click="submitCourseRecord">提交</el-button>
      </template>
    </el-dialog>

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
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { usePatientStore, AdmissionRecord } from '@/store/patient'
import { useRoute, useRouter } from 'vue-router'
import { getInpatientRecordDetail } from '@/api/patient'
import { addCourseRecord } from '@/api/doctor'
import { logger } from '@/utils/logger'
import { showError, showSuccess } from '@/utils/message'

const patientStore = usePatientStore()
const inpatientRecord = computed(() => patientStore.currentAdmissionRecord)

const route = useRoute()
const router = useRouter()

interface CourseRecord {
  AdmissionRecordID: number
  RecordContent: string
  RecordDateTime: string
}

// 从路由参数获取记录ID
const recordId = ref(Number(route.params.id))
// 病程记录数据
const courseRecords = ref<CourseRecord[]>([])
// 病程记录弹窗控制
const courseDialogVisible = ref(false)
// 新增病程记录数据
const newCourseRecord = ref({
  admissionRecordID: recordId, // 关联住院记录ID
  recordContent: '',
  recordDateTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
})

/**
 * 获取病程信息
 * @param recordId 住院记录ID
 */
const fetchCourseRecords = async (recordId: number) => {
  try {
    const data = await getInpatientRecordDetail(recordId)
    courseRecords.value = data.map((item: any) => ({
      AdmissionRecordID: item.admissionRecordID,
      RecordContent: item.recordContent,
      RecordDateTime: item.recordDateTime,
    }))
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

// 显示添加病程记录弹窗
const showAddCourseDialog = () => {
  // 重置表单数据
  newCourseRecord.value = {
    admissionRecordID: recordId,
    recordContent: '',
    recordDateTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
  }
  courseDialogVisible.value = true
}

// 提交病程记录
const submitCourseRecord = async () => {
  if (!newCourseRecord.value.recordContent) {
    showError('请填写病程记录内容')
    return
  }

  try {
    const data = await addCourseRecord(newCourseRecord.value)

    showSuccess('病程记录添加成功')
    courseDialogVisible.value = false
    fetchCourseRecords(recordId.value)
  } catch (error) {
    showError('添加病程记录失败: ' + (error.message || '未知错误'))
  }
}

// 返回列表
const goBack = () => {
  router.go(-1)
}

// 组件挂载时获取数据
onMounted(() => {
  console.log(inpatientRecord)
  fetchCourseRecords(recordId.value)
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

/* 添加按钮样式 */
.add-course-record {
  margin: 30px 0;
  text-align: center;
}

.btn-add {
  padding: 12px 30px;
  background-color: #67c23a;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-add:hover {
  background-color: #85ce61;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(103, 194, 58, 0.3);
}

.btn-add i {
  margin-right: 8px;
}

/* 弹窗内容样式 */
:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-form-item__label) {
  font-weight: bold;
}
</style>