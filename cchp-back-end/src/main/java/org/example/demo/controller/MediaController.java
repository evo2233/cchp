package org.example.demo.controller;

import org.example.demo.common.ArgumentResolver;
import org.example.demo.model.CommonResponse;
import org.example.demo.model.dto.ImageDTO;
import org.example.demo.model.dto.ImageVO;
import org.example.demo.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final MediaService mediaService;

    @Autowired
    public MediaController(MediaService mediaService) { this.mediaService = mediaService; }

    @PostMapping("/imaging")
    public ResponseEntity<CommonResponse> uploadImage(
            @RequestParam(value = "residentID", required = false) String residentID,
            @RequestParam(value = "examDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date examDate,
            @RequestParam(value = "examType", required = false) String examType,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file,
            @ArgumentResolver.PatientIdentity String identity) {
        try{
            ImageDTO imageDTO = new ImageDTO();
            imageDTO.setResidentID(residentID);
            imageDTO.setExamDate(examDate);
            imageDTO.setExamType(examType);
            imageDTO.setDescription(description);
            boolean result = mediaService.insertImaging(imageDTO, identity, file);
            if(result){
                return ResponseEntity.ok(CommonResponse.ok("success"));
            }else{
                return ResponseEntity.badRequest().body(CommonResponse.fail("400", new Exception("插入图像失败")));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CommonResponse.fail("400", e));
        }
    }

    @GetMapping("/imaging")
    public ResponseEntity<?> getImages(
            @RequestParam(required = false) String residentID,
            @RequestParam(required = false) String instCode,
            @RequestParam(required = false) String examType,
            @RequestParam(required = false) String examDate
    ) {
        try {
            List<ImageVO> records = mediaService.queryImages(residentID, instCode, examType, examDate);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CommonResponse.fail("400", e));
        }
    }

    @GetMapping("/imaging/{id}")
    public ResponseEntity<?> getImage(@PathVariable String id) {
        try{
            return ResponseEntity.ok(mediaService.queryImageDetail(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CommonResponse.fail("400", e));
        }
    }

    @PatchMapping("/imaging/{id}")
    public ResponseEntity<CommonResponse> updateImage(
            @PathVariable String id,
            @RequestParam(value = "residentID", required = false) String residentID,
            @RequestParam(value = "examDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date examDate,
            @RequestParam(value = "examType", required = false) String examType,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file,
            @ArgumentResolver.PatientIdentity String identity
    ) {
        try{
            ImageDTO imageDTO = new ImageDTO();
            imageDTO.setResidentID(residentID);
            imageDTO.setExamDate(examDate);
            imageDTO.setExamType(examType);
            imageDTO.setDescription(description);
            return ResponseEntity.ok(CommonResponse.ok(mediaService.updateMedicalImaging(id, imageDTO, identity, file)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CommonResponse.fail("400", e));
        }
    }

    @DeleteMapping("/imaging/{id}")
    public ResponseEntity<CommonResponse> deleteImage(
            @PathVariable String id,
            @ArgumentResolver.PatientIdentity String identity
    ) {
        try{
            return ResponseEntity.ok(CommonResponse.ok(mediaService.deleteImageById(id, identity)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CommonResponse.fail("400", e));
        }
    }

}
