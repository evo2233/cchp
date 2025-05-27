package org.example.demo.service;

import org.example.demo.model.dto.ImageDTO;
import org.example.demo.model.dto.ImageVO;
import org.example.demo.model.entity.MedicalImaging;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaService {

    boolean insertImaging(ImageDTO imageDTO, String identity, MultipartFile file) throws Exception;

    List<ImageVO> queryImages(String residentID, String instCode, String examType, String examDate);

    MedicalImaging queryImageDetail(String id);

    boolean updateMedicalImaging(String id, ImageDTO imageDTO, String identity, MultipartFile file) throws Exception;

    boolean deleteImageById(String id, String identity);

}
