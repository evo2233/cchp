import request from "@/utils/request";
import { logger } from "@/utils/logger";
import { useAuthStore } from "@/store/auth";

/**
 * 获取住院记录列表
 */
export const getInpatientRecordList = async (
  institutionCode?: string,
  diagnosisDate?: string
) => {
  try {
    const params: Record<string, string> = {};
    if (institutionCode) params.institutionCode = institutionCode;
    if (diagnosisDate) params.diagnosisDate = diagnosisDate;

    const { data } = await request.get("/patient/inpatient-records", {
      params,
    });

    logger.info("获取住院记录列表成功", data);
    return data;
  } catch (error) {
    logger.error("获取住院记录失败:", error);
    return null;
  }
};
