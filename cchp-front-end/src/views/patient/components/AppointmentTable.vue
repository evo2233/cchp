<template>
  <div class="doctors-appointment">
    <!-- Page Title -->
    <div class="title-box">
      <h3>我的就诊记录</h3>
      <p>Check your appointment status and details.</p>
    </div>
    <!-- Table -->
    <div class="doctors-list">
      <div class="table-outer">
        <table class="doctors-table">
          <thead class="table-header">
            <tr>
              <th>就诊医生</th>
              <th>就诊时间</th>
              <th>预约时间</th>
              <th>支付金额</th>
              <th>状态</th>
              <th>&nbsp;</th>
              <th>&nbsp;</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="appointment in appointments"
                :key="appointment.id">
              <td>
                <div class="name-box">
                  <figure class="image">
                    <img :src=appointment.image
                         :alt="appointment.doctor" />
                  </figure>
                  <h5>{{ appointment.doctor }}</h5>
                  <span class="designation">{{ appointment.specialty }}</span>
                </div>
              </td>
              <td>
                <p>{{ appointment.apptDate }}</p>
                <span class="time">{{ appointment.apptTime }}</span>
              </td>
              <td>
                <p>{{ appointment.bookingDate }}</p>
              </td>
              <td>
                <p>{{ appointment.amount }}</p>
              </td>
              <td>
                <span :class="['status', appointment.statusClass]">{{ appointment.status }}</span>
              </td>
              <td>
                <span class="print"
                      @click="printAppointment(appointment.id)">
                  <i class="fas fa-print"></i>打印
                </span>
              </td>
              <td>
                <span class="view"
                      @click="viewAppointment(appointment.id)">
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

<script>
export default {
  name: 'DoctorsAppointments',
  data () {
    return {
      appointments: [
        {
          id: 1,
          doctor: 'Dr. Amelia Anna',
          specialty: 'Cardiology',
          image: '/images/resource/dashboard-doc-1.png',
          apptDate: '20 Oct 2020',
          apptTime: '10:30AM',
          bookingDate: '18 Oct, 2020',
          amount: '$150.00',
          status: '完成',
          statusClass: ''
        },
        {
          id: 2,
          doctor: 'Dr. Paolo Dybala',
          specialty: 'Dermatology',
          image: '/images/resource/dashboard-doc-2.png',
          apptDate: '20 Oct 2020',
          apptTime: '10:30AM',
          bookingDate: '18 Oct, 2020',
          amount: '$150.00',
          status: '完成',
          statusClass: ''
        },
        {
          id: 3,
          doctor: 'Dr. Samuel Daniels',
          specialty: 'ACL Surgeon',
          image: '/images/resource/dashboard-doc-3.png',
          apptDate: '20 Oct 2020',
          apptTime: '10:30AM',
          bookingDate: '18 Oct, 2020',
          amount: '$150.00',
          status: '取消',
          statusClass: 'cancel'
        },
        {
          id: 4,
          doctor: 'Dr. Julia Jhones',
          specialty: 'Dentist',
          image: '/images/resource/dashboard-doc-4.png',
          apptDate: '20 Oct 2020',
          apptTime: '10:30AM',
          bookingDate: '18 Oct, 2020',
          amount: '$150.00',
          status: '完成',
          statusClass: ''
        },
        {
          id: 5,
          doctor: 'Dr. Mary Astor',
          specialty: 'Orthopedic',
          image: '/images/resource/dashboard-doc-5.png',
          apptDate: '20 Oct 2020',
          apptTime: '10:30AM',
          bookingDate: '18 Oct, 2020',
          amount: '$150.00',
          status: '未开始',
          statusClass: 'pending'
        }
      ]
    }
  },
  methods: {
    printAppointment (id) {
      // Handle print functionality
      console.log('Print appointment:', id)
    },
    viewAppointment (id) {
      // Handle view functionality
      console.log('View appointment:', id)
    }
  }
}
</script>
