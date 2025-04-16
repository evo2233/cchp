package org.example.demo.service.impl;

import org.example.demo.model.entity.InpatientRecord;
import org.example.demo.model.entity.CourseRecordDetail;
import org.example.demo.mapper.InpatientMapper;
import org.example.demo.service.InpatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class InpatientServiceImpl implements InpatientService {

    @Autowired
    private InpatientMapper inpatientMapper;

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    @Override
    public int insertInpatientRecord(InpatientRecord record) {
        return inpatientMapper.insertInpatientRecord(record);
    }

    @Override
    @Transactional
    public int insertCourseRecordDetail(CourseRecordDetail detail) {
        int result = inpatientMapper.insertCourseRecordDetail(detail);
        if (result > 0) {
            inpatientMapper.updateDiagnosisDate(detail.getAdmissionRecordID(), getCurrentDate());
        }
        return result;
    }

    @Override
    @Transactional
    public int deleteInpatientRecord(Integer admissionRecordID) {
        // 先删除关联的病程记录
        List<CourseRecordDetail> details = inpatientMapper.selectCourseRecordDetails(admissionRecordID);
        for (CourseRecordDetail detail : details) {
            inpatientMapper.deleteCourseRecordDetail(admissionRecordID, detail.getRecordDateTime().toString());
        }
        // 再删除住院记录
        return inpatientMapper.deleteInpatientRecord(admissionRecordID);
    }

    @Override
    @Transactional
    public int deleteCourseRecordDetail(Integer admissionRecordID, String recordDateTime) {
        int result = inpatientMapper.deleteCourseRecordDetail(admissionRecordID, recordDateTime);
        if (result > 0) {
            inpatientMapper.updateDiagnosisDate(admissionRecordID, getCurrentDate());
        }
        return result;
    }

    @Override
    @Transactional
    public int updateInpatientRecord(InpatientRecord record) {
        record.setDiagnosisDate(getCurrentDate());
        return inpatientMapper.updateInpatientRecord(record);
    }

    @Override
    @Transactional
    public int updateCourseRecordDetail(CourseRecordDetail detail) {
        int result = inpatientMapper.updateCourseRecordDetail(detail);
        if (result > 0) {
            inpatientMapper.updateDiagnosisDate(detail.getAdmissionRecordID(), getCurrentDate());
        }
        return result;
    }

    @Override
    public List<InpatientRecord> selectInpatientRecords(String institutionCode, String residentHealthCardID, String diagnosisDate) {
        return inpatientMapper.selectInpatientRecords(institutionCode, residentHealthCardID, diagnosisDate);
    }

    @Override
    public List<InpatientRecord> getInpatientRecords(String identity) {
        return inpatientMapper.selectInpatientRecords(null, identity, null);
    }

    @Override
    public List<CourseRecordDetail> getCourseRecordDetails(Integer admissionRecordID) {
        return inpatientMapper.selectCourseRecordDetails(admissionRecordID);
    }
} 