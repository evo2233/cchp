package org.example.demo.controller;

import org.example.demo.common.ArgumentResolver;
import org.example.demo.model.entity.InpatientRecord;
import org.example.demo.model.entity.CourseRecordDetail;
import org.example.demo.service.InpatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/inpatient")
public class InpatientController {

    private final InpatientService inpatientService;
    @Autowired
    public InpatientController(InpatientService service) { this.inpatientService = service; }

    // 插入住院记录
    @PostMapping("/record")
    public ResponseEntity<?> insertInpatientRecord(@RequestBody InpatientRecord record,
                                                   @ArgumentResolver.PatientIdentity String identity) {
        try{
            int result = inpatientService.insertInpatientRecord(record, identity);
            if (result > 0) {
                return ResponseEntity.ok().body("住院记录添加成功");
            }
            return ResponseEntity.badRequest().body("住院记录添加失败");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("住院记录添加失败："+e);
        }
    }

    // 插入病程记录
    @PostMapping("/course-record")
    public ResponseEntity<?> insertCourseRecordDetail(@RequestBody CourseRecordDetail detail) {
        int result = inpatientService.insertCourseRecordDetail(detail);
        if (result > 0) {
            return ResponseEntity.ok().body("病程记录添加成功");
        }
        return ResponseEntity.badRequest().body("病程记录添加失败");
    }

    // 删除住院记录
    @DeleteMapping("/record/{admissionRecordID}")
    public ResponseEntity<?> deleteInpatientRecord(@PathVariable Integer admissionRecordID,
                                                   @ArgumentResolver.PatientIdentity String identity) {
        try{
            int result = inpatientService.deleteInpatientRecord(admissionRecordID, identity);
            if (result > 0) {
                return ResponseEntity.ok().body("住院记录删除成功");
            }
            return ResponseEntity.badRequest().body("住院记录删除失败");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("住院记录删除失败"+e);
        }
    }

    // 删除病程记录
    @DeleteMapping("/course-record/{admissionRecordID}/{recordDateTime}")
    public ResponseEntity<?> deleteCourseRecordDetail(
            @PathVariable Integer admissionRecordID,
            @PathVariable String recordDateTime) {
        int result = inpatientService.deleteCourseRecordDetail(admissionRecordID, recordDateTime);
        if (result > 0) {
            return ResponseEntity.ok().body("病程记录删除成功");
        }
        return ResponseEntity.badRequest().body("病程记录删除失败");
    }

    // 修改住院记录
    @PutMapping("/record")
    public ResponseEntity<?> updateInpatientRecord(@RequestBody InpatientRecord record,
                                                   @ArgumentResolver.PatientIdentity String identity) {
        try{
            int result = inpatientService.updateInpatientRecord(record, identity);
            if (result > 0) {
                return ResponseEntity.ok().body("住院记录更新成功");
            }
            return ResponseEntity.badRequest().body("住院记录更新失败");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("住院记录更新失败"+e);
        }
    }

    // 修改病程记录
    @PutMapping("/course-record")
    public ResponseEntity<?> updateCourseRecordDetail(@RequestBody CourseRecordDetail detail) {
        int result = inpatientService.updateCourseRecordDetail(detail);
        if (result > 0) {
            return ResponseEntity.ok().body("病程记录更新成功");
        }
        return ResponseEntity.badRequest().body("病程记录更新失败");
    }

    // 根据条件查询住院记录
    @GetMapping("/records")
    public ResponseEntity<List<InpatientRecord>> selectInpatientRecords(
            @RequestParam(required = false) String institutionCode,
            @RequestParam(required = false) String residentHealthCardID,
            @RequestParam(required = false) String diagnosisDate) {
        try{
            List<InpatientRecord> records = inpatientService.selectInpatientRecords(
                    institutionCode, residentHealthCardID, diagnosisDate);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 根据住院记录ID查询病程记录
    @GetMapping("/course-records/{admissionRecordID}")
    public ResponseEntity<List<CourseRecordDetail>> getCourseRecordDetails(
            @PathVariable Integer admissionRecordID) {
        List<CourseRecordDetail> details = inpatientService.getCourseRecordDetails(admissionRecordID);
        return ResponseEntity.ok(details);
    }
} 