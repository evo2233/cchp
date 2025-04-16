/**
 * 核心身份基础信息（所有角色共用）
 */
interface IdentityBase {
  id: string; // 用户唯一标识
  name: string; // 姓名
  idCardNumber: string; // 身份证号
  phone: string; // 注册手机号
  avatar?: string; // 头像URL
}

/**
 * 患者专属身份信息
 */
export interface PatientIdentity extends IdentityBase {
  role: "patient";
}

/**
 * 医生专属身份信息
 */
export interface DoctorIdentity extends IdentityBase {
  role: "doctor";
  licenseNumber: string; // 医师执业证书编号
  department: string; // 科室代码
  professionalTitle: "junior" | "senior" | "chief"; // 职称
}

/**
 * 系统身份类型联合
 */
export type MedicalUserIdentity = PatientIdentity | DoctorIdentity;
