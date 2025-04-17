<template>
  <div class="doctors-appointment">
    <!-- Page Title -->
    <div class="title-box">
      <h3>患者就诊记录</h3>
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
        <!-- 单日选择 -->
        <input type="date"
               v-model="searchDate"
               :max="maxDate"
               class="date-input" />
        <div class="button-group">
          <button @click="searchRecords"
                  class="search-btn">搜索</button>
          <button @click="addRecord"
                  class="add-btn">新增</button>
        </div>
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

  <!-- 新增记录弹窗 -->
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
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
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
import { showError } from '@/utils/message'

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
const searchDate = ref('')
const maxDate = ref(new Date().toISOString().split('T')[0]) // 设置最大日期为今天

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
    门诊: '/doctor/patient/OutpatientDetail',
    住院: '/doctor/patient/InpatientDetail',
    急诊: '/doctor/patient/EmergencyDetail',
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
  try {
    logger.info('开始搜索住院记录')
    const data = await getInpatientRecordList(
      institutionCode.value,
      residentCard.value,
      searchDate.value
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

const emit = defineEmits(['add-record'])
const showAddDialog = ref(false)

// 计算新ID：现有记录最大ID + 1
const newRecordId = computed(() => {
  if (records.value.length === 0) return 1
  const maxId = Math.max(...records.value.map((r) => r.record_id))
  return maxId + 1
})

// 初始化新记录
const newRecord = ref({
  admissionRecordID: newRecordId.value,
  residentHealthCardID: '',
  institutionCode: '',
  admissionNumber: '',
  admissionConditionCode: '1',
  totalHospitalizationCost: 0,
  diagnosisDate: new Date().toISOString().split('T')[0],
})

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
</script>

<style>
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
</style>
