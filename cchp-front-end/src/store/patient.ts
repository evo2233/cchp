// store/patient.ts
import { defineStore } from "pinia";

export interface AdmissionRecord {
  admissionRecordID: number;
  residentHealthCardID: string;
  institutionCode: string;
  admissionNumber: string;
  admissionConditionCode: string;
  totalHospitalizationCost: number;
  diagnosisDate: string;
}

export const usePatientStore = defineStore("patient", {
  state: () => ({
    currentAdmissionRecord: null as AdmissionRecord | null,
  }),
  actions: {
    setCurrentAdmissionRecord(record: AdmissionRecord) {
      this.currentAdmissionRecord = record;
    },
    clearCurrentAdmissionRecord() {
      this.currentAdmissionRecord = null;
    },
  },
});
