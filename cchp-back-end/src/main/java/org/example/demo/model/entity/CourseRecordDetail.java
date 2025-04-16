package org.example.demo.model.entity;

import lombok.Data;
import java.util.Date;

@Data
public class CourseRecordDetail {
    private Integer admissionRecordID;    // 入院记录唯一标识，外键
    private String recordContent;         // 病程记录内容
    private Date recordDateTime;          // 记录时间
} 