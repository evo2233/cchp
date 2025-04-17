<template>
  <div class="doctors-appointment">
    <!-- Page Title -->
    <div class="title-box">
      <h3>我的就诊记录</h3>
      <p>Check your appointment status and details.</p>
    </div>

    <!-- Search Bar -->
    <div class="search-bar">
      <div class="search-inputs">
        <input type="text"
               v-model="residentCard"
               placeholder="居民健康卡号" />
        <input type="text"
               v-model="institutionCode"
               placeholder="医疗机构组织机构代码" />
        <button @click="searchRecords"
                class="search-btn">搜索</button>
      </div>
    </div>

    <!-- Table -->
    <div class="doctors-list">
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
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onMounted } from 'vue'
import { logger } from '@/utils/logger'
import { useRouter } from 'vue-router'
import { usePatientStore, AdmissionRecord } from '@/store/patient'
import { getInpatientRecordList } from '@/api/patient'

onMounted(() => {
  fetchInitialRecords()
})

type RecordType = '门诊' | '住院' | '急诊' // 记录类型

interface Record {
  record_id: number // 病历编号
  record_type: RecordType // 记录类型
  ResidentHealthCardID: string // 居民健康卡号
  InstitutionCode: string // 医疗机构代码
  DiagnosisDate: string // 诊断日期
  admissionNumber: string // 住院号
  total_hospitalization_cost: number // 总费用
}

const residentCard = ref('')
const institutionCode = ref('')
const diagnosisDate = ref('') // 可选的诊断日期筛选字段
// 住院记录
const records = ref<Record[]>([])

function printAppointment(id: number) {
  console.log('Print appointment:', id)
}

const router = useRouter()

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

/**
 * 获取初始化记录数据
 */
async function fetchInitialRecords() {
  try {
    const data = await getInpatientRecordList()
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
    }
  } catch (error) {
    logger.error('初始化获取住院记录失败:', error)
  }
}

/**
 * 搜索住院记录
 */
async function searchRecords() {
  if (!institutionCode.value) {
    logger.warn('请输入医疗机构代码')
    return
  }

  try {
    logger.info('开始搜索住院记录')
    const data = await getInpatientRecordList(
      institutionCode.value,
      diagnosisDate.value
    )

    if (data && Array.isArray(data)) {
      records.value = data.map((item) => ({
        ...item,
        record_type: '住院', // 因为这是住院接口
        diagnosis: item.diagnosis || '',
        total_hospitalization_cost: item.totalHospitalizationCost,
        ResidentHealthCardID: item.residentHealthCardID,
        InstitutionCode: item.institutionCode,
        DiagnosisDate: item.diagnosisDate,
        condition: item.condition || '',
        record_id: item.admissionRecordID,
      }))
    } else {
      records.value = []
      logger.info('无住院记录数据返回')
    }
  } catch (error) {
    logger.error('搜索住院记录时发生错误:', error)
  }
}
</script>

<style>
/* 搜索框 */
.search-bar {
  margin-bottom: 20px;
  text-align: center;
}

.search-inputs {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.search-inputs input {
  width: 45%;
  padding: 12px 18px;
  border-radius: 20px;
  border: 1px solid #ccc;
  font-size: 16px;
  color: #333;
  transition: all 0.3s ease;
}

.search-inputs input:focus {
  outline: none;
  border-color: #4caf50;
  box-shadow: 0 0 5px rgba(76, 175, 80, 0.5);
}

.search-btn {
  padding: 12px 18px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 16px;
  white-space: nowrap; /* 禁止文本换行 */
  transition: background-color 0.3s ease;
}

.search-btn:hover {
  background-color: #45a049;
}
</style>
