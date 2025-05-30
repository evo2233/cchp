package org.example.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {
    private String residentID;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date examDate;
    private String examType;
    private String identity;  // 传入时为空，从token中提取
    private String fileName;  // 从文件名提取
    private String fileType;  // 从文件扩展提取
    private byte[] imgData;
    private String description;
}
