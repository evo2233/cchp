package org.example.demo.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "medical_imaging")
public class MedicalImaging {
    @Id
    private String id;  // MongoDB自动生成的文档唯一标识

    private String residentHealthCardID; // 患者身份证号
    private String institutionCode;      // 医院机构代码
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date examinationDate;        // 检查时间
    private String examinationType;      // 检查类型（X光、CT、MRI等）
    private String imageFileName;        // 图片文件名
    private String imageContentType;     // 图片MIME类型
    private byte[] imageData;            // 图片二进制数据
    private String description;          // 图片描述
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadDate;             // 上传时间
}
