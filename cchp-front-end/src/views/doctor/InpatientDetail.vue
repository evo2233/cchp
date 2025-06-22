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

    <!-- 新增：医疗影像记录卡片列表 -->
    <div class="medical-images-section">

      <div class="section-header">
        <h3 class="section-title">
          <i class="fas fa-x-ray"></i>
          医疗影像记录 ({{ medicalImages.length }}张)
        </h3>

        <!-- 上传按钮 - 仅医生可见 -->
        <button class="btn-upload"
                @click="openUploadDialog">
          <i class="fas fa-upload"></i> 上传影像
        </button>
      </div>

      <!-- 影像列表表格 -->
      <div class="image-list-table">
        <table>
          <thead>
            <tr>
              <th>检查类型</th>
              <th>检查日期</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="image in medicalImages"
                :key="image.id">
              <td>{{ image.examType || '未知类型' }}</td>
              <td>{{ formatDate(image.examDate) }}</td>
              <td>
                <button class="btn-view"
                        @click="openImageDetailDialog(image.id)">
                  查看详情
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 无影像记录提示 -->
        <div class="empty-table"
             v-if="medicalImages.length === 0">
          <i class="far fa-image"></i>
          <p>暂无医疗影像记录</p>
        </div>
      </div>
    </div>

    <!-- 影像详情对话框 -->
    <el-dialog v-model="imageDetailDialogVisible"
               title="医疗影像详情"
               width="60%"
               :close-on-click-modal="false">
      <div class="image-detail-content"
           v-if="currentImageDetail">
        <!-- 图片展示区域 -->
        <div class="image-preview-area">
          <img :src="getImageUrl(currentImageDetail)"
               :alt="currentImageDetail.description">
        </div>

        <!-- 详细信息区域 -->
        <div class="image-info-area">
          <div class="info-item">
            <span class="label">健康卡号：</span>
            <span class="value">{{ currentImageDetail.residentHealthCardID }}</span>
          </div>
          <div class="info-item">
            <span class="label">机构代码：</span>
            <span class="value">{{ currentImageDetail.institutionCode }}</span>
          </div>
          <div class="info-item">
            <span class="label">检查类型：</span>
            <span class="value">{{ currentImageDetail.examinationType }}</span>
          </div>
          <div class="info-item">
            <span class="label">检查日期：</span>
            <span class="value">{{ formatDateTime(currentImageDetail.examinationDate) }}</span>
          </div>
          <div class="info-item">
            <span class="label">上传日期：</span>
            <span class="value">{{ formatDateTime(currentImageDetail.uploadDate) }}</span>
          </div>
          <div class="info-item full-width">
            <span class="label">影像描述：</span>
            <span class="value">{{ currentImageDetail.description || '无描述' }}</span>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="imageDetailDialogVisible = false">关闭</el-button>
        <el-button type="primary"
                   @click="downloadImage(currentImageDetail)"
                   v-if="currentImageDetail">
          下载影像
        </el-button>
      </template>
    </el-dialog>

    <!-- 新增：上传影像对话框 -->
    <div class="upload-dialog"
         v-if="showUploadDialog"
         @click.self="closeUploadDialog">
      <div class="dialog-content">
        <button class="close-btn"
                @click="closeUploadDialog">
          <i class="fas fa-times"></i>
        </button>
        <h3>上传医疗影像</h3>

        <form @submit.prevent="uploadMedicalImage">
          <div class="form-group">
            <label>影像类型 *</label>
            <select v-model="uploadForm.examinationType"
                    required>
              <option value="">请选择</option>
              <option value="X光">X光</option>
              <option value="CT">CT</option>
              <option value="MRI">MRI</option>
              <option value="超声">超声</option>
              <option value="内镜">内镜</option>
            </select>
          </div>

          <div class="form-group">
            <label>检查日期 *</label>
            <input type="datetime-local"
                   v-model="uploadForm.examinationDate"
                   required>
          </div>

          <div class="form-group">
            <label>影像描述</label>
            <textarea v-model="uploadForm.description"
                      placeholder="请输入影像描述..."
                      rows="3"></textarea>
          </div>

          <div class="form-group">
            <label>选择影像文件 *</label>
            <div class="file-upload">
              <input type="file"
                     ref="fileInput"
                     @change="handleFileChange"
                     accept="image/*,.dcm"
                     required>
              <div class="file-info"
                   v-if="uploadForm.file">
                {{ uploadForm.file.name }}
                <span>({{ formatFileSize(uploadForm.file.size) }})</span>
              </div>
              <div class="file-placeholder"
                   v-else>
                <i class="fas fa-cloud-upload-alt"></i>
                <p>点击选择或拖拽文件到此处</p>
              </div>
            </div>
          </div>

          <div class="form-actions">
            <button type="button"
                    class="btn-cancel"
                    @click="closeUploadDialog">
              取消
            </button>
            <button type="submit"
                    class="btn-submit"
                    :disabled="isUploading">
              <span v-if="!isUploading">确认上传</span>
              <span v-else>上传中...</span>
            </button>
          </div>
        </form>
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
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { usePatientStore, AdmissionRecord } from '@/store/patient'
import { useRoute, useRouter } from 'vue-router'
import { getInpatientRecordDetail } from '@/api/patient'
import { addCourseRecord } from '@/api/doctor'
import {
  UploadMedicalImageRequest,
  GetMedicalImageDetailRequest,
  GetMedicalImagesRequest,
} from '@/api/media'
import { logger } from '@/utils/logger'
import { showError, showSuccess } from '@/utils/message'
import { id } from 'element-plus/es/locale'

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

interface MedicalImageSimple {
  id: string
  examType?: string
  examDate?: string
}

interface MedicalImageDetail {
  id: string
  residentHealthCardID: string
  institutionCode: string
  examinationDate: string
  examinationType: string
  imageFileName: string
  imageContentType: string
  imageData: string
  description: string
  uploadDate: string
}

// 新增医疗影像数据
const medicalImages = ref<MedicalImageSimple[]>([])
const selectedImage = ref<MedicalImageSimple | null>(null)
// 上传相关状态
const showUploadDialog = ref(false)
const isUploading = ref(false)
const fileInput = ref<HTMLInputElement | null>(null)
// 新增状态
const imageDetailDialogVisible = ref(false)
const currentImageDetail = ref<MedicalImageDetail | null>(null)
const isDoctor = ref(true) // 根据实际用户角色设置

// 上传表单数据
const uploadForm = reactive({
  examinationType: '',
  examinationDate: '',
  description: '',
  file: null as File | null,
})

/**
 * 获取医疗影像信息
 */
const fetchMedicalImages = async (residentID: string) => {
  try {
    // 调用查询接口，传入residentID参数
    const data = await GetMedicalImagesRequest({ residentID })
    console.log('获取到的医疗影像列表后端数据data:', data)
    // 映射接口返回的数据到需要的格式
    medicalImages.value = data.map((item: any) => ({
      id: item.id,
      examType: item.examType,
      examDate: item.examDate,
    }))
    console.log('获取到的医疗影像列表数据:', medicalImages)
  } catch (error) {
    console.error('获取医疗影像失败:', error)
  }
}

/**
 * 获取图片URL
 */
const getImageUrl = (image) => {
  return `data:${image.imageContentType};base64,${image.imageData}`
}

/**
 * 打开影像详情对话框
 */
const openImageDetailDialog = async (imageId: string) => {
  try {
    console.log('ImageID:', imageId)
    // 获取影像详情
    const detail = await GetMedicalImageDetailRequest(imageId)
    console.log('获取到的数据', detail)
    currentImageDetail.value = {
      id: detail.id,
      residentHealthCardID: detail.residentHealthCardID,
      institutionCode: detail.institutionCode,
      examinationDate: detail.examinationDate,
      examinationType: detail.examinationType,
      imageFileName: detail.imageFileName,
      imageContentType: detail.imageContentType,
      imageData: detail.imageData,
      description: detail.description,
      uploadDate: detail.uploadDate,
    }
    imageDetailDialogVisible.value = true
  } catch (error) {
    console.error('获取影像详情失败:', error)
    showError('获取影像详情失败')
  }
}

/**
 * 下载影像
 */
const downloadImage = (image: MedicalImageDetail) => {
  const link = document.createElement('a')
  link.href = `data:${image.imageContentType};base64,${image.imageData}`
  link.download = image.imageFileName || `medical_image_${image.id}`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

/**
 * 打开上传对话框
 */
const openUploadDialog = () => {
  // 重置表单
  uploadForm.examinationType = ''
  uploadForm.examinationDate = new Date().toISOString().slice(0, 16)
  uploadForm.description = ''
  uploadForm.file = null
  if (fileInput.value) fileInput.value.value = ''

  showUploadDialog.value = true
}

/**
 * 关闭上传对话框
 */
const closeUploadDialog = () => {
  if (!isUploading.value) {
    showUploadDialog.value = false
  }
}

/**
 * 处理文件选择
 */
const handleFileChange = (e: Event) => {
  console.log('加入图片文件')
  const input = e.target as HTMLInputElement
  if (input.files && input.files.length > 0) {
    uploadForm.file = input.files[0]
  }
}

/**
 * 格式化文件大小
 */
const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

/**
 * 上传医疗影像
 */
const uploadMedicalImage = async () => {
  if (!uploadForm.file || !recordId.value) return

  isUploading.value = true

  try {
    const requestData = {
      residentID: inpatientRecord.value.residentHealthCardID, // 患者身份证号
      examType: uploadForm.examinationType,
      examDate: uploadForm.examinationDate,
      description: uploadForm.description,
      file: uploadForm.file,
    }
    console.log('上传医疗影像数据:', requestData)

    const response = await UploadMedicalImageRequest(requestData)

    // 上传成功后刷新影像列表
    await fetchMedicalImages(inpatientRecord.value.residentHealthCardID)
    showUploadDialog.value = false
    logger.info('医疗影像上传成功', response)
  } catch (error) {
    logger.error('医疗影像上传失败', error)
    alert('上传失败，请重试')
  } finally {
    isUploading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  console.log(inpatientRecord)
  fetchCourseRecords(recordId.value)
  fetchMedicalImages(inpatientRecord.value.residentHealthCardID)
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

/* 医生版特有样式 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.btn-upload {
  padding: 8px 16px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background-color 0.3s;
}

.btn-upload:hover {
  background-color: #66b1ff;
}

/* 上传对话框 */
.upload-dialog {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-dialog .dialog-content {
  background: white;
  border-radius: 8px;
  width: 500px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  padding: 25px;
  position: relative;
}

.upload-dialog h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #303133;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #606266;
}

.form-group select,
.form-group input[type='datetime-local'],
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.file-upload {
  position: relative; /* 添加相对定位 */
  overflow: hidden; /* 防止溢出 */
  border: 2px dashed #dcdfe6;
  border-radius: 6px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: border-color 0.3s;
}

.file-upload:hover {
  border-color: #c0c4cc;
}

.file-upload input[type='file'] {
  position: absolute; /* 绝对定位 */
  top: 0;
  left: 0;
  width: 100%; /* 覆盖整个区域 */
  height: 100%;
  opacity: 0; /* 透明不可见 */
  cursor: pointer; /* 显示手型指针 */
}

.file-placeholder {
  color: #909399;
}

.file-placeholder i {
  font-size: 24px;
  margin-bottom: 10px;
}

.file-info {
  color: #303133;
  word-break: break-all;
}

.file-info span {
  color: #909399;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 30px;
}

.btn-cancel {
  padding: 10px 20px;
  background: #f5f7fa;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
}

.btn-submit {
  padding: 10px 20px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-submit:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}

/* 响应式调整 */
@media (max-width: 600px) {
  .upload-dialog .dialog-content {
    width: 95%;
    padding: 15px;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn-cancel,
  .btn-submit {
    width: 100%;
  }
}

/* 影像列表表格样式 */
.image-list-table {
  margin-top: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.image-list-table table {
  width: 100%;
  border-collapse: collapse;
}

.image-list-table th,
.image-list-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

.image-list-table th {
  background-color: #f5f7fa;
  color: #909399;
  font-weight: 600;
}

.image-list-table tr:hover td {
  background-color: #f5f7fa;
}

.btn-view {
  padding: 6px 12px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-view:hover {
  background-color: #66b1ff;
}

.empty-table {
  padding: 40px;
  text-align: center;
  color: #909399;
}

.empty-table i {
  font-size: 24px;
  margin-bottom: 10px;
}

/* 影像详情对话框样式 */
.image-detail-content {
  display: flex;
  gap: 20px;
}

.image-preview-area {
  flex: 1;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
}

.image-preview-area img {
  max-width: 100%;
  max-height: 400px;
  object-fit: contain;
}

.image-info-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
}

.info-item.full-width {
  flex-direction: column;
}

.info-item .label {
  min-width: 80px;
  font-weight: 600;
  color: #606266;
}

.info-item .value {
  color: #303133;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .image-detail-content {
    flex-direction: column;
  }

  .image-preview-area {
    min-height: 300px;
  }
}
</style>