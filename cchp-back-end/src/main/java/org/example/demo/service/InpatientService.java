package org.example.demo.service;

import org.example.demo.model.entity.InpatientRecord;
import org.example.demo.model.entity.CourseRecordDetail;

import java.io.IOException;
import java.util.List;

public interface InpatientService {
    // 插入住院记录
    int insertInpatientRecord(InpatientRecord record, String identity) throws Exception;

    // 插入病程记录
    int insertCourseRecordDetail(CourseRecordDetail detail);

    // 删除住院记录
    int deleteInpatientRecord(Integer admissionRecordID);

    // 删除病程记录
    int deleteCourseRecordDetail(Integer admissionRecordID, String recordDateTime);

    // 修改住院记录
    int updateInpatientRecord(InpatientRecord record, String identity);

    // 修改病程记录
    int updateCourseRecordDetail(CourseRecordDetail detail);

    // 根据条件查询住院记录
    List<InpatientRecord> selectInpatientRecords(String institutionCode, String residentHealthCardID, String diagnosisDate);

    // 根据residentHealthCardID查询住院记录
    List<InpatientRecord> getInpatientRecords(String identity);

    // 根据admissionRecordID查询病程记录
    List<CourseRecordDetail> getCourseRecordDetails(Integer admissionRecordID);
}
