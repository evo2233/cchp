<template>
  <div class="outpatient-detail-container">
    <!-- 返回按钮 -->
    <div class="back-button">
      <button @click="goBack"
              class="btn-back">
        <i class="fas fa-arrow-left"></i> 返回列表
      </button>
    </div>

    <!-- 病历头信息 -->
    <div class="record-header-card">
      <div class="header-icon">
        <img src="/icons/icons8-门诊部-100.png"
             alt="门诊记录" />
      </div>
      <div class="header-content">
        <h2>门诊记录详情</h2>
        <div class="header-meta">
          <div class="meta-item">
            <span class="meta-label">病历编号：</span>
            <span class="meta-value">{{ outpatientRecord.OutpatientRecordID }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">就诊日期：</span>
            <span class="meta-value">{{ formatDate(outpatientRecord.DiagnosisDate) }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">健康卡号：</span>
            <span class="meta-value">{{ outpatientRecord.ResidentHealthCardID }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">医疗机构：</span>
            <span class="meta-value">{{ outpatientRecord.InstitutionCode }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 主诉和现病史卡片 -->
    <div class="info-card complaint-card">
      <div class="card-header">
        <i class="fas fa-comment-medical"></i>
        <h3>主诉与现病史</h3>
      </div>
      <div class="card-content">
        <div class="complaint-section">
          <h4>主诉</h4>
          <p>{{ outpatientRecord.ChiefComplaint || '暂无主诉信息' }}</p>
        </div>
        <div class="illness-history-section">
          <h4>现病史</h4>
          <p>{{ outpatientRecord.PresentIllnessHistory || '暂无现病史信息' }}</p>
        </div>
      </div>
    </div>

    <!-- 诊断信息卡片 -->
    <div class="info-card diagnosis-card">
      <div class="card-header">
        <i class="fas fa-diagnoses"></i>
        <h3>诊断信息</h3>
      </div>
      <div class="card-content">
        <div class="diagnosis-item">
          <span class="diagnosis-label">西医诊断：</span>
          <span class="diagnosis-value">{{ outpatientRecord.InitialWesternDiagnosis || '暂无诊断信息' }}</span>
        </div>
      </div>
    </div>

    <!-- 处方信息卡片 -->
    <div class="info-card prescription-card"
         v-for="prescription in prescriptions"
         :key="prescription.PrescriptionID">
      <div class="card-header">
        <i class="fas fa-prescription-bottle-alt"></i>
        <h3>处方信息</h3>
        <span class="prescription-id">No.{{ prescription.PrescriptionID }}</span>
      </div>
      <div class="card-content">
        <div class="medicine-list">
          <div class="list-header">
            <span>药品名称</span>
          </div>
          <div class="medicine-item"
               v-for="(medicine, index) in prescription.medicines"
               :key="index">
            <i class="fas fa-pills"></i>
            <span>{{ medicine.MedicineName }}</span>
          </div>
          <div class="no-medicine"
               v-if="prescription.medicines.length === 0">
            暂无药品信息
          </div>
        </div>
      </div>
    </div>

    <!-- 无处方时的提示卡片 -->
    <div class="info-card empty-prescription-card"
         v-if="prescriptions.length === 0">
      <div class="card-header">
        <i class="fas fa-prescription-bottle-alt"></i>
        <h3>处方信息</h3>
      </div>
      <div class="card-content">
        <div class="no-prescription">
          <i class="far fa-frown"></i>
          <p>暂无处方信息</p>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <button @click="printRecord"
              class="btn-print">
        <i class="fas fa-print"></i> 打印病历
      </button>
      <button @click="downloadRecord"
              class="btn-download">
        <i class="fas fa-download"></i> 下载PDF
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
// 脚本部分与之前相同，保持不变
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

interface OutpatientRecord {
  OutpatientRecordID: number
  ResidentHealthCardID: string
  InstitutionCode: string
  ChiefComplaint: string
  PresentIllnessHistory: string
  InitialWesternDiagnosis: string
  DiagnosisDate: string
}

interface Prescription {
  PrescriptionID: string
  OutpatientRecordID: number
  PrescriptionDate: string
  medicines: Medicine[]
}

interface Medicine {
  MedicineName: string
}

const route = useRoute()
const router = useRouter()

const outpatientRecord = ref<OutpatientRecord>({
  OutpatientRecordID: 0,
  ResidentHealthCardID: '',
  InstitutionCode: '',
  ChiefComplaint: '',
  PresentIllnessHistory: '',
  InitialWesternDiagnosis: '',
  DiagnosisDate: '',
})

const prescriptions = ref<Prescription[]>([])
const recordId = ref(Number(route.params.id))

// 模拟API获取门诊记录详情
const fetchOutpatientRecord = async (id: number) => {
  // 模拟API调用
  try {
    // 这里应该是实际的API调用，例如：
    // const response = await axios.get(`/api/outpatient-records/${id}`)
    // outpatientRecord.value = response.data

    // 模拟数据
    const mockRecord: OutpatientRecord = {
      OutpatientRecordID: id,
      ResidentHealthCardID: '123456789012345678',
      InstitutionCode: 'A123456789',
      ChiefComplaint: '咳嗽、咳痰3天，伴发热1天',
      PresentIllnessHistory:
        '患者3天前受凉后出现咳嗽、咳痰，痰为白色粘痰，量中等。1天前出现发热，体温最高38.5℃，无寒战、抽搐。自服"感冒药"后症状无缓解。',
      InitialWesternDiagnosis: '急性上呼吸道感染',
      DiagnosisDate: '2023-05-15',
    }

    outpatientRecord.value = mockRecord

    // 获取处方数据
    await fetchPrescriptions(id)
  } catch (error) {
    console.error('获取门诊记录失败:', error)
    // 可以在这里处理错误，比如跳转到错误页面
  }
}

// 模拟API获取处方信息
const fetchPrescriptions = async (recordId: number) => {
  try {
    // 这里应该是实际的API调用，例如：
    // const response = await axios.get(`/api/prescriptions?recordId=${recordId}`)
    // prescriptions.value = response.data

    // 模拟数据
    const mockPrescriptions: Prescription[] = [
      {
        PrescriptionID: 'RX20230515001',
        OutpatientRecordID: recordId,
        PrescriptionDate: '2023-05-15',
        medicines: [
          { MedicineName: '阿莫西林胶囊 0.25g×24粒' },
          { MedicineName: '复方甘草片 100片' },
          { MedicineName: '布洛芬缓释胶囊 0.3g×10粒' },
        ],
      },
      {
        PrescriptionID: 'RX20230515002',
        OutpatientRecordID: recordId,
        PrescriptionDate: '2023-05-15',
        medicines: [{ MedicineName: '维生素C片 0.1g×100片' }],
      },
    ]

    prescriptions.value = mockPrescriptions
  } catch (error) {
    console.error('获取处方信息失败:', error)
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

// 打印病历
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
  fetchOutpatientRecord(recordId.value)
})
</script>

<style scoped>
.outpatient-detail-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', 'PingFang SC', sans-serif;
  color: #333;
}

/* 返回按钮样式 */
.back-button {
  margin-bottom: 25px;
}

.btn-back {
  padding: 8px 16px;
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  color: #495057;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.btn-back:hover {
  background-color: #e9ecef;
  border-color: #ced4da;
}

/* 病历头卡片 */
.record-header-card {
  display: flex;
  background: white;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
  margin-bottom: 25px;
  align-items: center;
}

.header-icon img {
  width: 70px;
  height: 70px;
  margin-right: 25px;
}

.header-content {
  flex: 1;
}

.header-content h2 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 24px;
}

.header-meta {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.meta-item {
  display: flex;
}

.meta-label {
  font-weight: 600;
  color: #6c757d;
  margin-right: 5px;
}

.meta-value {
  color: #495057;
}

/* 通用信息卡片样式 */
.info-card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 25px;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 1px solid #eee;
}

.card-header i {
  margin-right: 12px;
  color: #3498db;
  font-size: 18px;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #2c3e50;
  flex: 1;
}

.card-content {
  padding: 20px;
}

/* 主诉卡片特殊样式 */
.complaint-card .card-header {
  background: linear-gradient(135deg, #f0f8ff 0%, #d0e3ff 100%);
}

.complaint-card .card-header i {
  color: #4169e1;
}

.complaint-section,
.illness-history-section {
  margin-bottom: 15px;
}

.complaint-section h4,
.illness-history-section h4 {
  margin: 0 0 10px 0;
  color: #495057;
  font-size: 16px;
}

.complaint-section p,
.illness-history-section p {
  margin: 0;
  color: #6c757d;
  line-height: 1.6;
  white-space: pre-line;
}

/* 诊断卡片特殊样式 */
.diagnosis-card .card-header {
  background: linear-gradient(135deg, #fff0f5 0%, #ffd6e7 100%);
}

.diagnosis-card .card-header i {
  color: #e83e8c;
}

.diagnosis-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.diagnosis-label {
  font-weight: 600;
  color: #495057;
  min-width: 80px;
}

.diagnosis-value {
  color: #212529;
  flex: 1;
}

/* 处方卡片特殊样式 */
.prescription-card .card-header {
  background: linear-gradient(135deg, #f0fff4 0%, #d0ffdf 100%);
  position: relative;
}

.prescription-card .card-header i {
  color: #28a745;
}

.prescription-date {
  margin-left: 20px;
  font-size: 14px;
  color: #6c757d;
}

.prescription-id {
  position: absolute;
  right: 20px;
  font-size: 14px;
  color: #6c757d;
}

.medicine-list {
  border: 1px solid #e9ecef;
  border-radius: 6px;
  overflow: hidden;
}

.list-header {
  padding: 10px 15px;
  background-color: #f8f9fa;
  font-weight: 600;
  color: #495057;
  border-bottom: 1px solid #e9ecef;
}

.medicine-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  border-bottom: 1px solid #e9ecef;
}

.medicine-item:last-child {
  border-bottom: none;
}

.medicine-item i {
  margin-right: 10px;
  color: #6c757d;
}

.no-medicine,
.no-prescription {
  padding: 30px;
  text-align: center;
  color: #adb5bd;
}

.no-prescription i {
  font-size: 24px;
  margin-bottom: 10px;
  display: block;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

.btn-print,
.btn-download {
  padding: 10px 25px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-print {
  background-color: #17a2b8;
  color: white;
}

.btn-print:hover {
  background-color: #138496;
}

.btn-download {
  background-color: #6c757d;
  color: white;
}

.btn-download:hover {
  background-color: #5a6268;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .record-header-card {
    flex-direction: column;
    text-align: center;
  }

  .header-icon img {
    margin-right: 0;
    margin-bottom: 15px;
  }

  .header-meta {
    grid-template-columns: 1fr;
  }

  .prescription-id {
    position: static;
    display: block;
    margin-top: 5px;
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