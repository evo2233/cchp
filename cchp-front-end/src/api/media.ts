import request from "@/utils/request";
import { logger } from "@/utils/logger";

/**
 * 上传医疗影像服务
 * @param formData 包含患者信息和图片的表单数据
 */
function formatDateTime(date: Date): string {
  const pad = (n: number) => (n < 10 ? "0" + n : n);
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(
    date.getDate()
  )} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(
    date.getSeconds()
  )}`;
}

export const UploadMedicalImageRequest = async (form: {
  residentID: string;
  examDate: string;
  examType: string;
  description?: string;
  file: File;
}) => {
  try {
    const formData = new FormData();
    formData.append("residentID", form.residentID);
    formData.append("examDate", formatDateTime(new Date(form.examDate)));
    formData.append("examType", form.examType);
    if (form.description) formData.append("description", form.description);
    formData.append("file", form.file);

    console.log("上传医疗影像服务请求数据");
    for (const [key, value] of formData.entries()) {
      console.log(`${key}:`, value);
    }

    const { data } = await request.post("/media/imaging", formData);

    logger.info("医疗影像上传成功", data);
    return data;
  } catch (error) {
    logger.error("医疗影像上传失败", error);
    throw error;
  }
};

/**
 * 医学影像查询服务
 * @param queryParams 查询参数
 * @param queryParams.residentID 患者身份证号(可选)
 * @param queryParams.instCode 机构代码(可选)
 * @param queryParams.examType 检查类型(可选)
 * @param queryParams.examDate 检查日期(可选)
 */
export const GetMedicalImagesRequest = async (queryParams?: {
  residentID?: string;
  instCode?: string;
  examType?: string;
  examDate?: string;
}) => {
  try {
    // 构建查询参数对象，过滤掉undefined/null的值
    const params = {
      ...(queryParams?.residentID && { residentID: queryParams.residentID }),
      ...(queryParams?.instCode && { instCode: queryParams.instCode }),
      ...(queryParams?.examType && { examType: queryParams.examType }),
      ...(queryParams?.examDate && { examDate: queryParams.examDate }),
    };

    // 发送GET请求
    const data = await request.get("/media/imaging", {
      params,
    });

    logger.info("医学影像查询成功", data);
    return data;
  } catch (error) {
    logger.error("医学影像查询失败", error);
    throw error;
  }
};

/**
 * 获取医学影像详细信息服务
 * @param id 医学影像记录ID (必需)
 * @returns 返回医学影像详细信息
 */
export const GetMedicalImageDetailRequest = async (id: string) => {
  try {
    // 发送GET请求，将ID作为路径参数
    const data = await request.get(`/media/imaging/${id}`);

    logger.info(`成功获取ID为 ${id} 的医学影像详细信息`);
    return data;
  } catch (error) {
    logger.error(`获取ID为 ${id} 的医学影像详细信息失败`, error);
    throw new Error("获取医学影像详情失败，请稍后重试");
  }
};

/**
 * 更新医学影像信息服务
 * @param id 医学影像记录ID (必需)
 * @param formData 更新数据 (可选字段)
 */
export const UpdateMedicalImageRequest = async (
  id: string,
  formData: {
    residentID?: string | null;
    examDate?: string | null;
    examType?: string | null;
    description?: string | null;
    file?: File;
  }
): Promise<MedicalImageDetail> => {
  try {
    // 创建FormData对象
    const formDataToSend = new FormData();

    // 添加可选字段（明确处理null值）
    if (formData.residentID !== undefined) {
      formDataToSend.append("residentID", formData.residentID || "");
    }
    if (formData.examDate !== undefined) {
      formDataToSend.append("examDate", formData.examDate || "");
    }
    if (formData.examType !== undefined) {
      formDataToSend.append("examType", formData.examType || "");
    }
    if (formData.description !== undefined) {
      formDataToSend.append("description", formData.description || "");
    }
    if (formData.file) {
      formDataToSend.append("file", formData.file);
    }

    // 发送PUT请求
    const { data } = await request.put<MedicalImageDetail>(
      `/media/imaging/${id}`,
      formDataToSend,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );

    logger.info(`成功更新ID为 ${id} 的医学影像信息`);
    return data;
  } catch (error) {
    logger.error(`更新ID为 ${id} 的医学影像信息失败`, error);
    throw new Error("更新医学影像信息失败，请稍后重试");
  }
};

/**
 * 删除医学影像信息服务
 * @param id 要删除的医学影像记录ID (必需)
 * @returns 返回删除操作结果
 */
export const DeleteMedicalImageRequest = async (
  id: string
): Promise<{ success: boolean; message?: string }> => {
  try {
    // 发送DELETE请求
    await request.delete(`/media/imaging/${id}`);

    logger.info(`成功删除ID为 ${id} 的医学影像`);
    return { success: true, message: "医学影像删除成功" };
  } catch (error) {
    logger.error(`删除ID为 ${id} 的医学影像失败`, error);

    // 根据错误类型返回不同的错误信息
    let errorMessage = "删除医学影像失败，请稍后重试";
    if (error.response) {
      if (error.response.status === 404) {
        errorMessage = "指定的医学影像不存在";
      } else if (error.response.status === 403) {
        errorMessage = "没有权限删除此医学影像";
      }
    }

    return { success: false, message: errorMessage };
  }
};
