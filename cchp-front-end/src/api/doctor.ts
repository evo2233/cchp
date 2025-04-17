import request from "@/utils/request";
import { logger } from "@/utils/logger";
import { useAuthStore } from "@/store/auth";

/**
 * 获取住院记录列表
 */
export const getInpatientRecordList = async (
  institutionCode?: string,
  residentCard?: string,
  searchDate?: string
) => {
  try {
    const params: Record<string, string> = {};
    if (institutionCode) params.institutionCode = institutionCode;
    if (residentCard) params.residentHealthCardID = residentCard;
    if (searchDate) params.diagnosisDate = searchDate;

    const data = await request.get("/inpatient/records", {
      params,
    });
    logger.info("获取住院记录列表成功", data);
    return data;
  } catch (error) {
    logger.error("获取住院记录失败:", error);
    return null;
  }
};

/**
 * 添加住院记录
 * @param record 住院记录数据
 */
export const addInpatientRecord = async (record: {
  admissionRecordID: number;
  residentHealthCardID: string;
  institutionCode: string;
  admissionNumber?: string;
  admissionConditionCode?: string;
  totalHospitalizationCost?: number;
  diagnosisDate: string;
}) => {
  try {
    // 构造请求体，包含所有必填和选填字段
    const requestBody = {
      admissionRecordID: record.admissionRecordID,
      residentHealthCardID: record.residentHealthCardID,
      institutionCode: record.institutionCode,
      ...(record.admissionNumber && {
        admissionNumber: record.admissionNumber,
      }),
      ...(record.admissionConditionCode && {
        admissionConditionCode: record.admissionConditionCode,
      }),
      ...(record.totalHospitalizationCost && {
        totalHospitalizationCost: record.totalHospitalizationCost,
      }),
      diagnosisDate: record.diagnosisDate,
    };

    logger.debug("请求体", requestBody);

    const response = await request.post("/inpatient/record", requestBody);

    logger.info("添加住院记录成功", {
      request: requestBody,
      response: response.data,
    });

    return response.data;
  } catch (error) {
    logger.error("添加住院记录失败:", {
      error,
      recordData: record,
    });
    throw error; // 抛出错误以便调用方处理
  }
};

/**
 * 添加病程记录
 * @param record 住院记录数据
 */
export const addCourseRecord = async (record: {
  admissionRecordID: number;
  recordContent: string;
  recordDateTime: string;
}) => {
  try {
    // 构造请求体，包含所有必填和选填字段
    const requestBody = {
      admissionRecordID: record.admissionRecordID,
      recordContent: record.recordContent,
      recordDateTime: record.recordDateTime,
    };

    logger.debug("请求体", requestBody);

    const response = await request.post(
      "/inpatient/course-record",
      requestBody
    );

    logger.info("添加住院病程成功", {
      request: requestBody,
      response: response.data,
    });

    return response.data;
  } catch (error) {
    logger.error("添加住院病程失败:", {
      error,
      recordData: record,
    });
    throw error; // 抛出错误以便调用方处理
  }
};
