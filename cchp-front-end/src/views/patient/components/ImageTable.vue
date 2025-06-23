<template>
  <div class="medical-image-table">
    <!-- 标题 -->
    <div class="title-box">
      <h3>医疗影像资料</h3>
    </div>

    <!-- 检索栏 -->
    <div class="search-bar">
      <div class="search-inputs">
        <input type="text"
               v-model="institutionCode"
               placeholder="医疗机构组织机构代码" />
        <input type="date"
               v-model="searchDate"
               :max="maxDate"
               class="date-input" />
        <div class="button-group">
          <button @click="searchImages"
                 class="search-btn">搜索</button>
        </div>
      </div>
    </div>

    <!-- 影像资料表格 -->
    <div class="images-list">
      <div class="table-outer">
        <table class="images-table">
          <thead class="table-header">
            <tr>
              <th>缩略图</th>
              <th>类型</th>
              <th>检查日期</th>
              <th>描述</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="image in images" :key="image.id">
              <td>
                <img :src="getImageUrl(image)"
                     alt="影像缩略图"
                     class="thumbnail"
                     @click="openImageDialog(image)" />
              </td>
              <td>{{ image.examinationType }}</td>
              <td>{{ formatDate(image.examinationDate) }}</td>
              <td>{{ image.description || '无' }}</td>
              <td>
                <!--此方法暂不可用，需要image转Record ID-->
                <span class="view" @click="viewRecordDetail(image)">
                  <i class="fas fa-eye"></i>查看详情
                </span>
              </td>
            </tr>
            <tr v-if="images.length === 0">
              <td colspan="5" style="text-align:center; color:#999;">暂无影像资料</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 图片预览对话框 -->
    <div class="image-dialog" v-if="selectedImage" @click.self="selectedImage = null">
      <div class="dialog-content">
        <button class="close-btn" @click="selectedImage = null">
          <i class="fas fa-times"></i>
        </button>
        <img :src="getImageUrl(selectedImage)" :alt="selectedImage.description">
        <div class="image-info">
          <h4>{{ selectedImage.examinationType }}</h4>
          <p>检查日期：{{ formatDate(selectedImage.examinationDate) }}</p>
          <p v-if="selectedImage.description">描述：{{ selectedImage.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 引入Vue相关API
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

// API方法获取影像数据
import {
  GetMedicalImageDetailRequest,
  GetMedicalImagesRequest,
} from '@/api/media'

import { logger } from '@/utils/logger'
import { useAuthStore } from '@/store/auth'

// 检索条件
// 获取医保ID
const residentCard = ref(authStore.account?.residentHealthCardID || '')
const institutionCode = ref('')
const examinationType = ref('')
const searchDate = ref('')
const maxDate = ref(new Date().toISOString().split('T')[0])

// 影像数据列表
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
const images = ref<MedicalImage[]>([])

// 选中的图片用于预览
const selectedImage = ref<MedicalImage | null>(null)

const router = useRouter()

// 格式化日期
function formatDate(dateString: string) {
  if (!dateString) return '未知日期'
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }
  return new Date(dateString).toLocaleDateString('zh-CN', options)
}

// 获取图片URL
function getImageUrl(image: MedicalImage) {
  return `data:${image.imageContentType};base64,${image.imageData}`
}

// 打开图片预览对话框
function openImageDialog(image: MedicalImage) {
  selectedImage.value = image
}

// 跳转到详情页（Record详情）（TODO如何将传入Image转为id,record_type,record）
function viewRecordDetail(
  id: number,
  recordType: RecordType,
  recordInfo: Record
) {
  // 映射
  const routeMap = {
    门诊: '/patient/OutpatientDetail',
    住院: '/patient/InpatientDetail',
    急诊: '/patient/EmergencyDetail',
  }
  const patientStore = usePatientStore()

  if (routeMap[recordType]) {
    // 如果是住院记录，转换并存储数据
    if (recordType === '住院') {
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
    }

    logger.info('跳转到', routeMap[recordType])
    router.push({ path: `${routeMap[recordType]}/${id}` })
  } else {
    console.error(`未配置${recordType}类型的路由`)
  }
}

// 检索影像资料
async function searchImages() {
  try {
    logger.info('开始检索医疗影像资料')
    // 这里假设GetMedicalImagesRequest支持传参过滤
    const data = await GetMedicalImagesRequest({
      residentHealthCardID: residentCard.value,
      institutionCode: institutionCode.value,
      examinationType: examinationType.value,
      examinationDate: searchDate.value
    })
    if (data && Array.isArray(data)) {
      images.value = data
    } else {
      images.value = []
    }
  } catch (error) {
    logger.error('检索医疗影像资料失败:', error)
    images.value = []
  }
}

// 组件挂载时自动检索一次
onMounted(() => {
  searchImages()
})
</script>

<style scoped>
.medical-image-table {
  width: 100%;
}
.title-box {
  margin-bottom: 20px;
}
.search-bar {
  margin: 20px 0;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  width: 100%;
}
.search-inputs {
  display: flex;
  align-items: center;
  gap: 15px;
  max-width: 1400px;
  margin: 0 auto;
}
.search-inputs input {
  flex: 3;
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
  flex: 2;
}
.button-group {
  display: flex;
  gap: 15px;
  flex: 1;
  min-width: 120px;
}
.search-btn {
  padding: 12px 25px;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  background-color: #409eff;
  color: white;
  transition: all 0.3s ease;
  flex: 1;
}
.search-btn:hover {
  background-color: #66b1ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}
.images-list {
  margin-top: 20px;
}
.table-outer {
  overflow-x: auto;
}
.images-table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.images-table th, .images-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  text-align: center;
}
.images-table th {
  background: #f5f7fa;
  font-weight: 600;
}
.thumbnail {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s;
}
.thumbnail:hover {
  transform: scale(1.1);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}
.view {
  color: #409eff;
  cursor: pointer;
  font-weight: 500;
}
.view:hover {
  text-decoration: underline;
}
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
@media (max-width: 768px) {
  .search-inputs {
    flex-direction: column;
    gap: 10px;
  }
  .dialog-content {
    width: 95%;
    padding: 10px;
  }
}
</style>
