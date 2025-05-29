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

    <!-- 新增：医疗影像记录卡片列表 -->
    <div class="medical-images-section">
      <h3 class="section-title">
        <i class="fas fa-x-ray"></i>
        医疗影像记录 ({{ medicalImages.length }}张)
      </h3>

      <!-- 影像卡片网格 -->
      <div class="images-grid">
        <!-- 单个影像卡片 -->
        <div class="image-card"
             v-for="(image, index) in medicalImages"
             :key="image.id">
          <div class="image-preview"
               @click="openImageDialog(image)">
            <img :src="getImageUrl(image)"
                 :alt="image.description || '医疗影像'">
            <div class="image-overlay">
              <i class="fas fa-search-plus"></i>
            </div>
          </div>
          <div class="image-meta">
            <div class="meta-item">
              <span class="label">类型：</span>
              <span class="value">{{ image.examinationType }}</span>
            </div>
            <div class="meta-item">
              <span class="label">日期：</span>
              <span class="value">{{ formatDate(image.examinationDate) }}</span>
            </div>
            <div class="meta-item"
                 v-if="image.description">
              <span class="label">描述：</span>
              <span class="value">{{ image.description }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 无影像记录提示 -->
      <div class="info-card empty-card"
           v-if="medicalImages.length === 0">
        <div class="card-content">
          <i class="far fa-image"></i>
          <p>暂无医疗影像记录</p>
        </div>
      </div>
    </div>

    <!-- 图片预览对话框 -->
    <div class="image-dialog"
         v-if="selectedImage"
         @click.self="selectedImage = null">
      <div class="dialog-content">
        <button class="close-btn"
                @click="selectedImage = null">
          <i class="fas fa-times"></i>
        </button>
        <img :src="getImageUrl(selectedImage)"
             :alt="selectedImage.description">
        <div class="image-info">
          <h4>{{ selectedImage.examinationType }}</h4>
          <p>检查日期：{{ formatDateTime(selectedImage.examinationDate) }}</p>
          <p v-if="selectedImage.description">描述：{{ selectedImage.description }}</p>
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
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { usePatientStore, AdmissionRecord } from '@/store/patient'
import { useRoute, useRouter } from 'vue-router'
import { getInpatientRecordDetail } from '@/api/patient'
import {
  GetMedicalImageDetailRequest,
  GetMedicalImagesRequest,
} from '@/api/media'
import { logger } from '@/utils/logger'

const patientStore = usePatientStore()
const inpatientRecord = computed(() => patientStore.currentAdmissionRecord)

const route = useRoute()
const router = useRouter()

interface CourseRecord {
  AdmissionRecordID: number
  RecordContent: string
  RecordDateTime: string
}

// 病程记录数据
const courseRecords = ref<CourseRecord[]>([])

// 从路由参数获取记录ID
const recordId = ref(Number(route.params.id))

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

interface MedicalImage {
  id: string
  residentHealthCardID: string
  institutionCode: string
  examinationDate: string
  examinationType: string
  imageFileName: string
  imageContentType: string
  imageData: string // base64编码
  description: string
  uploadDate: string
}

/**
 * 获取医疗影像信息
 */
const fetchMedicalImages = async (recordId: number) => {
  try {
    const data = await GetMedicalImagesRequest()
    medicalImages.value = data.map((item: any) => ({
      id: item.id,
      residentHealthCardID: item.residentHealthCardID,
      institutionCode: item.institutionCode,
      examinationDate: item.examinationDate,
      examinationType: item.examinationType,
      imageFileName: item.imageFileName,
      imageContentType: item.imageContentType,
      imageData: item.imageData,
      description: item.description,
      uploadDate: item.uploadDate,
    }))
  } catch (error) {
    console.error('获取医疗影像失败:', error)
  }
}

/**
 * 获取图片URL
 */
const getImageUrl = (image: MedicalImage) => {
  return `data:${image.imageContentType};base64,${image.imageData}`
}

/**
 * 打开图片对话框
 */
const openImageDialog = (image: MedicalImage) => {
  selectedImage.value = image
}

// 新增医疗影像数据
const medicalImages = ref<MedicalImage[]>([])
const selectedImage = ref<MedicalImage | null>(null)

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
  fetchCourseRecords(recordId.value)
  fetchMedicalImages(recordId.value)
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

/* 医疗影像部分 */
.medical-images-section {
  margin-top: 30px;
}

.images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 15px;
}

.image-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s;
}

.image-card:hover {
  transform: translateY(-5px);
}

.image-preview {
  position: relative;
  height: 200px;
  cursor: pointer;
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.image-preview:hover img {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-preview:hover .image-overlay {
  opacity: 1;
}

.image-overlay i {
  color: white;
  font-size: 2rem;
}

.image-meta {
  padding: 15px;
}

.meta-item {
  margin-bottom: 8px;
  display: flex;
}

.meta-item .label {
  font-weight: 600;
  color: #606266;
  min-width: 50px;
}

.meta-item .value {
  color: #303133;
  word-break: break-word;
}

/* 图片对话框 */
.image-dialog {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dialog-content {
  position: relative;
  max-width: 90%;
  max-height: 90%;
  background: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.dialog-content img {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
}

.close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #606266;
}

.image-info {
  margin-top: 15px;
  padding: 0 10px;
}

.image-info h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.image-info p {
  margin: 5px 0;
  color: #606266;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .images-grid {
    grid-template-columns: 1fr;
  }

  .dialog-content {
    width: 95%;
    padding: 10px;
  }
}
</style>