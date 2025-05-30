package org.example.demo.service.impl;

import com.mongodb.client.result.DeleteResult;
import org.bson.types.ObjectId;
import org.example.demo.model.dto.ImageDTO;
import org.example.demo.model.dto.ImageVO;
import org.example.demo.model.entity.MedicalImaging;
import org.example.demo.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import com.mongodb.client.result.UpdateResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaServiceImpl implements MediaService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MediaServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean insertImaging(ImageDTO imageDTO, String identity, MultipartFile file) throws Exception {
        imageDTO.setIdentity(identity);
        imageDTO.setFileName(file.getOriginalFilename());
        imageDTO.setFileType(file.getContentType());
        imageDTO.setImgData(file.getBytes());

        return doInsert(imageDTO) != null;
    }

    @Override
    public List<ImageVO> queryImages(String residentID, String instCode, String examType, String examDate) {
        Query query = new Query();
        Criteria criteria = new Criteria(); // 创建一个空的 Criteria

        // 动态拼接条件，使用 and() 方法链式调用
        if (residentID != null && !residentID.isEmpty()) {
            criteria.and("residentHealthCardID").is(residentID);
        }
        if (instCode != null && !instCode.isEmpty()) {
            criteria.and("institutionCode").is(instCode);
        }
        if (examType != null && !examType.isEmpty()) {
            criteria.and("examinationType").is(examType);
        }
        if (examDate != null && !examDate.isEmpty()) {
            criteria.and("examinationDate").is(examDate);
        }

        // 只有当 criteria 包含实际条件时才添加到 query
        // getCriteriaObject() 会返回一个 BSONObject，检查其是否为空
        if (!criteria.getCriteriaObject().isEmpty()) {
            query.addCriteria(criteria);
        }

        return mapVOList(mongoTemplate.find(query, MedicalImaging.class));
    }

    @Override
    public MedicalImaging queryImageDetail(String id) {
        return mongoTemplate.findById(id, MedicalImaging.class);
    }

    @Override
    public boolean updateMedicalImaging(String id, ImageDTO imageDTO, String identity, MultipartFile file) throws Exception{
        if(noPermission(id, identity)){
            return false;
        }

        imageDTO.setFileName(file.getOriginalFilename());
        imageDTO.setFileType(file.getContentType());
        imageDTO.setImgData(file.getBytes());

        Query query = new Query(Criteria.where("_id").is(id));
        Update update = constructUpdate(imageDTO);

        // 检查是否有字段需要更新
        if (update.getUpdateObject().isEmpty()) {
            return false;
        }
        // 执行更新操作
        update.set("uploadDate", new Date());
        UpdateResult result = mongoTemplate.updateFirst(query, update, MedicalImaging.class);

        // 检查更新是否成功
        return result.getMatchedCount() > 0 && result.getModifiedCount() > 0;
    }

    @Override
    public boolean deleteImageById(String id, String identity) {
        if(noPermission(id, identity)){
            return false;
        }

        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        // 删除匹配查询条件的文档
        DeleteResult result = mongoTemplate.remove(query, MedicalImaging.class);
        // 返回被删除的文档数量。
        return result.getDeletedCount() > 0;
    }

    private String doInsert(ImageDTO imageDTO) {
        MedicalImaging record = new MedicalImaging();
        record.setResidentHealthCardID(imageDTO.getResidentID());
        record.setInstitutionCode(imageDTO.getIdentity());
        record.setExaminationDate(imageDTO.getExamDate());
        record.setExaminationType(imageDTO.getExamType());
        record.setImageFileName(imageDTO.getFileName());
        record.setImageContentType(imageDTO.getFileType());
        record.setImageData(imageDTO.getImgData());
        record.setDescription(imageDTO.getDescription());
        record.setUploadDate(new Date());

        return mongoTemplate.insert(record).getId();
    }

    private List<ImageVO> mapVOList(List<MedicalImaging> medicalImagingList) {
        return medicalImagingList.stream()
                .map(a -> {
                    ImageVO b = new ImageVO();
                    b.setId(a.getId());
                    b.setResidentID(a.getResidentHealthCardID());
                    b.setInstCode(a.getInstitutionCode());
                    b.setExamType(a.getExaminationType());
                    b.setExamDate(a.getExaminationDate());
                    return b;
                })
                .collect(Collectors.toList());
    }

    private boolean noPermission(String id, String instCode){
        String originCode = queryImageDetail(id).getInstitutionCode();
        return !originCode.equals(instCode);
    }

    private Update constructUpdate(ImageDTO imageDTO) {
        Update update = new Update();

        // 动态构建更新操作，只更新 ImageDTO 中非空的字段
        if (imageDTO.getResidentID() != null && !imageDTO.getResidentID().isEmpty()) {
            update.set("residentHealthCardID", imageDTO.getResidentID());
        }
        if (imageDTO.getExamDate() != null) {
            update.set("examinationDate", imageDTO.getExamDate());
        }
        if (imageDTO.getExamType() != null && !imageDTO.getExamType().isEmpty()) {
            update.set("examinationType", imageDTO.getExamType());
        }
        if (imageDTO.getFileName() != null && !imageDTO.getFileName().isEmpty()) {
            update.set("imageFileName", imageDTO.getFileName());
        }
        if (imageDTO.getFileType() != null && !imageDTO.getFileType().isEmpty()) {
            update.set("imageContentType", imageDTO.getFileType());
        }
        if (imageDTO.getImgData() != null) {
            update.set("imageData", imageDTO.getImgData());
        }
        if (imageDTO.getDescription() != null && !imageDTO.getDescription().isEmpty()) {
            update.set("description", imageDTO.getDescription());
        }

        return update;
    }
}