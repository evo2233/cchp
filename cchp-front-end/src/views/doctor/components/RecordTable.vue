<template>
  <div class="doctors-appointment">
    <!-- 标签页放在顶部 -->
    <div class="record-tabs">
      <el-tabs v-model="activeTab"
               class="custom-tabs">
        <el-tab-pane label="门诊记录"
                     name="outpatient">
          <!-- 内容会在下面统一处理 -->
        </el-tab-pane>
        <el-tab-pane label="医疗影像记录"
                     name="medical-images">
          <!-- 内容会在下面统一处理 -->
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 搜索部分放在标签下面 -->
    <div class="search-bar">
      <div class="search-inputs">
        <input type="text"
               v-model="residentCard"
               placeholder="居民健康卡号" />
        <input type="text"
               v-model="institutionCode"
               placeholder="医疗机构组织机构代码" />
        <!-- 单日选择 -->
        <input type="date"
               v-model="searchDate"
               :max="maxDate"
               class="date-input" />
        <div class="button-group">
          <button @click="searchRecords"
                  class="search-btn">搜索</button>
          <button @click="addRecordBbtn"
                  class="add-btn">新增</button>
        </div>
      </div>
    </div>

    <!-- 表格内容区域 -->
    <div class="table-content">
      <!-- 门诊记录表格 -->
      <div v-if="activeTab === 'outpatient'"
           class="doctors-list">
        <div class="table-outer">
          <table class="doctors-table">
            <thead class="table-header">
              <tr>
                <th>记录类型</th>
                <th>诊断日期</th>
                <th>居民健康卡号</th>
                <th>医疗机构代码</th>
                <th>住院号</th>
                <th>总费用</th>
                <th>&nbsp;</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="record in records"
                  :key="record.record_id">
                <td>
                  <div class="name-box">
                    <figure class="image">
                      <img :src="record.record_type === '住院' ? '/icons/icons8-病床-100.png' : '/icons/icons8-门诊部-100.png'"
                           :alt="record.record_type" />
                    </figure>
                    <h5>{{ record.record_type }}记录</h5>
                    <span class="designation">
                      {{ record.record_type === '住院' ? 'Inpatient' : 'Outpatient' }}
                    </span>
                  </div>
                </td>
                <td>
                  <p>{{ record.DiagnosisDate }}</p>
                  <span class="time">{{ record.DiagnosisDate }}</span>
                </td>
                <td>
                  <p>{{ record.ResidentHealthCardID }}</p>
                </td>
                <td>
                  <p>{{ record.InstitutionCode }}</p>
                </td>
                <td>
                  <p>{{ record.admissionNumber }}</p>
                </td>
                <td>
                  <p>{{ record.total_hospitalization_cost }}</p>
                </td>
                <td>
                  <span class="view"
                        @click="viewRecordDetail(record.record_id, record.record_type, record)">
                    <i class="fas fa-eye"></i>查看详情
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 医疗影像记录表格 -->
      <div v-if="activeTab === 'medical-images'"
           class="doctors-list">
        <div class="table-outer">
          <table class="doctors-table">
            <thead class="table-header">
              <tr>
                <th>医疗影像类型</th>
                <th>诊断日期</th>
                <th>居民健康卡号</th>
                <th>医疗机构代码</th>
                <th>&nbsp;</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="image in medicalImages"
                  :key="image.id">
                <td>
                  <div class="name-box">
                    <figure class="image">
                      <img :src="'/icons/6-医疗-影像.png'"
                           :alt="image.examType" />
                    </figure>
                    <h5>{{ image.examType }}记录</h5>
                    <span class="designation">
                      {{ image.examType === '住院' ? 'Inpatient' : 'Outpatient' }}
                    </span>
                  </div>
                </td>
                <td>
                  <p>{{ image.examDate }}</p>
                  <span class="time">{{ image.examDate }}</span>
                </td>
                <td>
                  <p>{{ image.residentID }}</p>
                </td>
                <td>
                  <p>{{ image.instCode }}</p>
                </td>
                <td>
                  <span class="view"
                        @click="openImageDetailDialog(image.id)">
                    <i class="fas fa-eye"></i>查看详情
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

    </div>

    <!-- 记录弹窗 -->
    <el-dialog v-model="showAddDialog"
               title="新增住院记录"
               width="50%">
      <el-form :model="newRecord"
               label-width="150px">
        <el-form-item label="住院记录ID">
          <el-input v-model="newRecord.admissionRecordID"
                    disabled />
        </el-form-item>
        <el-form-item label="居民健康卡号"
                      required>
          <el-input v-model="newRecord.residentHealthCardID" />
        </el-form-item>
        <el-form-item label="医疗机构代码"
                      required>
          <el-input v-model="newRecord.institutionCode"
                    disabled />
        </el-form-item>
        <el-form-item label="住院号">
          <el-input v-model="newRecord.admissionNumber" />
        </el-form-item>
        <el-form-item label="入院情况代码">
          <el-select v-model="newRecord.admissionConditionCode">
            <el-option label="危"
                       value="1" />
            <el-option label="急"
                       value="2" />
            <el-option label="一般"
                       value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="住院总费用">
          <el-input-number v-model="newRecord.totalHospitalizationCost"
                           :min="0" />
        </el-form-item>
        <el-form-item label="诊断日期">
          <el-date-picker v-model="newRecord.diagnosisDate"
                          type="date"
                          value-format="yyyy-MM-dd" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary"
                   @click="confirmAddRecord">确认</el-button>
      </template>
    </el-dialog>

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
            <label>居民健康卡号</label>
            <input type="text"
                   v-model="uploadForm.residentHealthCardID"
                   placeholder="请输入健康卡号"
                   class="form-input">
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { onMounted } from 'vue'
import { logger } from '@/utils/logger'
import { useRouter } from 'vue-router'
import { usePatientStore, AdmissionRecord } from '@/store/patient'
import { useAuthStore } from '@/store/auth'
import {
  getInpatientRecordList,
  addInpatientRecord,
  addCourseRecord,
} from '@/api/doctor'
import {
  GetMedicalImagesRequest,
  GetMedicalImageDetailRequest,
  UploadMedicalImageRequest,
} from '@/api/media'
import { showError } from '@/utils/message'

// 挂载时执行操作
onMounted(() => {
  fetchInitialRecords()
})

type RecordType = '门诊' | '住院' | '急诊' // 记录类型

// 住院记录
interface Record {
  record_id: number // 病历编号
  record_type: RecordType // 记录类型
  ResidentHealthCardID: string // 居民健康卡号
  InstitutionCode: string // 医疗机构代码
  DiagnosisDate: string // 诊断日期
  admissionNumber: string // 住院号
  total_hospitalization_cost: number // 总费用
}

// 医疗影像记录
interface MedicalImage {
  id: string
  examType?: string
  examDate?: string
  residentID?: string
  instCode?: string
}

// 医疗影像详情
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

// 搜索相关信息
const residentCard = ref('') // 居民健康卡号
const institutionCode = ref('') // 医疗机构代码
const searchDate = ref('') // 搜索日期
const maxDate = ref(new Date().toISOString().split('T')[0]) // 设置最大日期为今天

// 标签页相关信息
const activeTab = ref('outpatient') // 当前激活的标签页
const records = ref<Record[]>([]) // 住院记录
const medicalImages = ref<MedicalImage[]>([]) // 医疗影像记录
const imageDetailDialogVisible = ref(false)
const currentImageDetail = ref<MedicalImageDetail | null>(null)

// 路由实例
const router = useRouter() // 路由实例

/**
 * 获取图片URL
 */
const getImageUrl = (image) => {
  return `data:${image.imageContentType};base64,${image.imageData}`
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
 * 跳转到记录详情页面
 */
function viewRecordDetail(
  id: number,
  recordType: RecordType,
  recordInfo: Record
) {
  // 路由映射
  const routeMap = {
    门诊: '/doctor/patient/OutpatientDetail',
    住院: '/doctor/patient/InpatientDetail',
    急诊: '/doctor/patient/EmergencyDetail',
  }
  const patientStore = usePatientStore()

  if (routeMap[recordType]) {
    if (recordType === '住院') {
      // 构建住院记录信息
      logger.info('处理住院记录', recordInfo)
      const admissionRecordInfo: AdmissionRecord = {
        admissionRecordID: recordInfo.record_id,
        residentHealthCardID: recordInfo.ResidentHealthCardID,
        institutionCode: recordInfo.InstitutionCode,
        admissionNumber: recordInfo.admissionNumber,
        admissionConditionCode: '', // 如果原数据没有这个字段，可以留空或根据业务需求处理
        totalHospitalizationCost: recordInfo.total_hospitalization_cost,
        diagnosisDate: recordInfo.DiagnosisDate,
      }
      patientStore.setCurrentAdmissionRecord(admissionRecordInfo)
    } else if (recordType === '门诊') {
      // 处理门诊记录逻辑（如果有）
    } else if (recordType === '急诊') {
      // 处理急诊记录逻辑（如果有）
    }

    logger.info('跳转到', routeMap[recordType])
    router.push({ path: `${routeMap[recordType]}/${id}` })
  } else {
    console.error(`未配置${recordType}类型的路由`)
  }
}

/**
 * 获取初始化记录数据
 */
async function fetchInitialRecords() {
  try {
    await fetchDiagnosisRecords()
    await fetchMedicalImages()
  } catch (error) {
    logger.error('初始化数据获取失败:', error)
    showError('初始化数据获取失败，请稍后重试')
  }
}

/**
 * 获取诊断记录
 */
async function fetchDiagnosisRecords() {
  try {
    logger.info('开始获取诊断记录')
    const data = await getInpatientRecordList(
      institutionCode.value,
      residentCard.value,
      searchDate.value
    )

    if (data && Array.isArray(data)) {
      records.value = data.map((item) => ({
        ...item,
        record_type: '住院',
        record_id: item.admissionRecordID,
        diagnosis: item.diagnosis || '',
        total_hospitalization_cost: item.totalHospitalizationCost,
        ResidentHealthCardID: item.residentHealthCardID,
        InstitutionCode: item.institutionCode,
        DiagnosisDate: item.diagnosisDate,
        admissionNumber: item.admissionNumber, // 住院号
      }))
    } else {
      records.value = []
      logger.info('无诊断记录数据返回')
    }
  } catch (error) {
    logger.error('获取诊断记录失败:', error)
  }
}

/**
 * 获取医疗影像资料
 */
async function fetchMedicalImages() {
  try {
    logger.info('开始检索医疗影像资料')
    const data = await GetMedicalImagesRequest({
      instCode: institutionCode.value,
      residentID: residentCard.value,
      examDate: searchDate.value,
    })

    if (data && Array.isArray(data)) {
      medicalImages.value = data.map((item: any) => ({
        id: item.id,
        examType: item.examType,
        examDate: item.examDate,
        residentID: item.residentID,
        instCode: item.instCode,
      }))
    } else {
      medicalImages.value = []
      logger.info('无医疗影像资料数据返回')
    }
  } catch (error) {
    logger.error('检索医疗影像资料失败:', error)
    medicalImages.value = []
  }
}

/**
 * 搜索住院记录
 */
async function searchRecords() {
  try {
    if (activeTab.value === 'outpatient') {
      fetchDiagnosisRecords()
    } else if (activeTab.value === 'medical-images') {
      fetchMedicalImages()
    }
  } catch (error) {
    logger.error('搜索记录失败:', error)
    showError('搜索记录失败，请稍后重试')
  }
}

/**
 * 计算新ID：现有记录最大ID + 1
 */
const newRecordId = computed(() => {
  if (records.value.length === 0) return 1
  const maxId = Math.max(...records.value.map((r) => r.record_id))
  return maxId + 1
})

// 初始化新记录
const emit = defineEmits(['add-record'])
const showAddDialog = ref(false)
const newRecord = ref({
  admissionRecordID: newRecordId.value,
  residentHealthCardID: '',
  institutionCode: '',
  admissionNumber: '',
  admissionConditionCode: '1',
  totalHospitalizationCost: 0,
  diagnosisDate: new Date().toISOString().split('T')[0],
})

// 上传表单数据
const showUploadDialog = ref(false)
const isUploading = ref(false)
const fileInput = ref<HTMLInputElement | null>(null)
const uploadForm = reactive({
  residentHealthCardID: '', // 居民健康卡号
  examinationType: '',
  examinationDate: '',
  description: '',
  file: null as File | null,
})

/**
 * 新增记录按钮点击事件
 */
const addRecordBbtn = () => {
  try {
    if (activeTab.value === 'outpatient') {
      addRecord()
    } else if (activeTab.value === 'medical-images') {
      openUploadDialog()
    }
  } catch (error) {
    console.error('新增记录失败:', error)
  }
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
  if (!uploadForm.file) return

  isUploading.value = true

  try {
    const requestData = {
      residentID: uploadForm.residentHealthCardID,
      examType: uploadForm.examinationType,
      examDate: uploadForm.examinationDate,
      description: uploadForm.description,
      file: uploadForm.file,
    }
    console.log('上传医疗影像数据:', requestData)

    const response = await UploadMedicalImageRequest(requestData)

    // 上传成功后刷新影像列表
    await fetchMedicalImages()
    showUploadDialog.value = false
    logger.info('医疗影像上传成功', response)
  } catch (error) {
    logger.error('医疗影像上传失败', error)
    alert('上传失败，请重试')
  } finally {
    isUploading.value = false
  }
}

const addRecord = () => {
  // 新增记录逻辑
  console.log('点击了新增按钮')
  // emit('add')
  const authStore = useAuthStore()
  newRecord.value.admissionRecordID = newRecordId.value
  newRecord.value.institutionCode = authStore.institutionCode
  showAddDialog.value = true
}

// 确认添加记录
const confirmAddRecord = async () => {
  if (
    !newRecord.value.residentHealthCardID ||
    !newRecord.value.institutionCode
  ) {
    showError('请填写必填字段')
    return
  }

  emit('add-record', { ...newRecord.value })
  showAddDialog.value = false

  const patientStore = usePatientStore()

  const admissionRecordInfo: AdmissionRecord = {
    admissionRecordID: newRecord.value.admissionRecordID,
    residentHealthCardID: newRecord.value.residentHealthCardID,
    institutionCode: newRecord.value.institutionCode,
    admissionNumber: newRecord.value.admissionNumber,
    admissionConditionCode: '', // 如果原数据没有这个字段，可以留空或根据业务需求处理
    totalHospitalizationCost: newRecord.value.totalHospitalizationCost,
    diagnosisDate: newRecord.value.diagnosisDate,
  }

  patientStore.setCurrentAdmissionRecord(admissionRecordInfo)

  try {
    const result = await addInpatientRecord(newRecord.value)
    console.log('添加成功', result)
  } catch (error) {
    console.error('添加失败', error)
  }

  // 重置表单（保留ID自增）
  newRecord.value = {
    admissionRecordID: newRecordId.value + 1, // 预填充下一个ID
    residentHealthCardID: '',
    institutionCode: '',
    admissionNumber: '',
    admissionConditionCode: '1',
    totalHospitalizationCost: 0,
    diagnosisDate: new Date().toISOString().split('T')[0],
  }

  // 跳转到相应的界面
  router.push({ path: `/doctor/patient/InpatientDetail/${newRecordId.value}` })
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
</script>

<style>
/* 标签页样式 */
.record-tabs {
  margin-bottom: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  padding: 0 16px;
}

/* 标签栏容器 */
:deep(.el-tabs__header) {
  margin: 0;
}

/* 导航栏 */
:deep(.el-tabs__nav-wrap) {
  padding: 0;
  position: relative;
}

/* 单个标签项 */
:deep(.el-tabs__item) {
  font-size: 15px;
  padding: 0 24px;
  height: 48px;
  line-height: 48px;
  font-weight: 500;
  color: #606266;
  transition: all 0.3s;
  position: relative;
}

/* 激活标签 */
:deep(.el-tabs__item.is-active) {
  color: #409eff;
  font-weight: 600;
}

/* 激活条样式 */
:deep(.el-tabs__active-bar) {
  height: 3px;
  background-color: #409eff;
  border-radius: 3px 3px 0 0;
}

/* 标签悬停效果 */
:deep(.el-tabs__item:hover) {
  color: #409eff;
}

/* 标签底部线条 */
:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #e4e7ed;
}

/* 移除默认的下划线动画 */
:deep(.el-tabs__nav-wrap.is-scrollable) {
  padding: 0;
}

/* 标签间距调整 */
:deep(.el-tabs__nav) {
  margin-right: 0;
}

/* 当标签数量少时居中显示 */
:deep(.el-tabs__nav-scroll) {
  display: flex;
  justify-content: center;
}

/* 响应式调整 */
@media (max-width: 768px) {
  :deep(.el-tabs__item) {
    padding: 0 16px;
    font-size: 14px;
  }
}
/* 搜索栏样式 */
.search-bar {
  margin: 20px 0;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  width: 100%; /* 占满可用宽度 */
}

.search-inputs {
  display: flex;
  align-items: center;
  gap: 15px;
  max-width: 1400px; /* 设置最大宽度 */
  margin: 0 auto; /* 水平居中 */
}

.search-inputs input {
  flex: 3; /* 增大输入框宽度占比 */
  padding: 12px 20px;
  border: 1px solid #dcdfe6;
  border-radius: 20px;
  outline: none;
  font-size: 16px;
}

.date-input {
  height: 46px;
  line-height: 46px;
  border-radius: 20px;
  flex: 2; /* 日期选择器宽度占比 */
}

.button-group {
  display: flex;
  gap: 15px;
  flex: 1;
  min-width: 220px; /* 确保按钮组有足够宽度 */
}

.search-btn,
.add-btn {
  padding: 12px 25px; /* 加宽按钮 */
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  flex: 1;
}

.search-btn {
  background-color: #409eff;
  color: white;
}

.search-btn:hover {
  background-color: #66b1ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.add-btn {
  background-color: #67c23a;
  color: white;
}

.add-btn:hover {
  background-color: #85ce61;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
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
</style>
