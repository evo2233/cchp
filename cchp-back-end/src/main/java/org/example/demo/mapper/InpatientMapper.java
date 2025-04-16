package org.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo.model.entity.InpatientRecord;
import org.example.demo.model.entity.CourseRecordDetail;

import java.util.List;

@Mapper
public interface InpatientMapper {
    // 插入住院记录
    int insertInpatientRecord(InpatientRecord record);

    // 插入病程记录
    int insertCourseRecordDetail(CourseRecordDetail detail);

    // 删除住院记录
    int deleteInpatientRecord(@Param("admissionRecordID") Integer admissionRecordID);

    // 删除病程记录
    int deleteCourseRecordDetail(@Param("admissionRecordID") Integer admissionRecordID, 
                               @Param("recordDateTime") String recordDateTime);

    // 修改住院记录
    int updateInpatientRecord(InpatientRecord record);

    // 修改病程记录
    int updateCourseRecordDetail(CourseRecordDetail detail);

    // 根据条件查询住院记录
    List<InpatientRecord> selectInpatientRecords(
            @Param("institutionCode") String institutionCode,
            @Param("residentHealthCardID") String residentHealthCardID,
            @Param("diagnosisDate") String diagnosisDate);

    // 根据AdmissionRecordID查找所有病程记录
    List<CourseRecordDetail> selectCourseRecordDetails(@Param("admissionRecordID") Integer admissionRecordID);

    // 更新住院记录的DiagnosisDate
    int updateDiagnosisDate(@Param("admissionRecordID") Integer admissionRecordID, @Param("diagnosisDate") String diagnosisDate);
}
