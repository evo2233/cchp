package org.example.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageVO {
    public String id;
    public String residentID;
    private String instCode;
    public String examType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date examDate;
}
