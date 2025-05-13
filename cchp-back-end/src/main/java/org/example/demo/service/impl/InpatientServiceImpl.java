package org.example.demo.service.impl;

import org.example.demo.common.Utils;
import org.example.demo.config.ContractConfig;
import org.example.demo.constants.ContractConstants;
import org.example.demo.model.entity.InpatientRecord;
import org.example.demo.model.entity.CourseRecordDetail;
import org.example.demo.mapper.InpatientMapper;
import org.example.demo.service.InpatientService;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InpatientServiceImpl implements InpatientService {

    @Autowired
    private InpatientMapper inpatientMapper;

    private String address;

    @Autowired
    private Client client;

    @Autowired
    private ContractConfig contractConfig;

    private AssembleTransactionProcessor txProcessor;

    @PostConstruct
    public void init() throws Exception {
        this.txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(
                this.client, this.client.getCryptoSuite().getCryptoKeyPair());
        this.address = contractConfig.getInpatientContractAddress();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    private String letRecordStringed(InpatientRecord record) {
        // 拼接字段形成字符串（字段顺序需固定）
        return record.getAdmissionRecordID() +
                record.getResidentHealthCardID() +
                record.getInstitutionCode() +
                record.getAdmissionNumber() +
                record.getAdmissionConditionCode() +
                record.getTotalHospitalizationCost() +
                (record.getDiagnosisDate() != null ? record.getDiagnosisDate().getTime() : "");
    }

    @Override
    public int insertInpatientRecord(InpatientRecord record, String identity) throws Exception {
        record.setDiagnosisDate(new Date());
        record.setInstitutionCode(identity);  // 机构自动完成签名

        int rowcount = inpatientMapper.insertInpatientRecord(record);  // 尝试插入数据库
        // hash上链
        if(rowcount == 1) {
            // 从文件读取机构私钥
            String privateKey = Utils.readPrivateKeyFromFile(identity);
            if (privateKey == null) {
                throw new RuntimeException("机构私钥不存在");
            }

            // 使用私钥创建机构密钥对
            ECDSAKeyPair ecdsaKeyPair = new ECDSAKeyPair();
            CryptoKeyPair keyPair = ecdsaKeyPair.createKeyPair(privateKey);
            // 不使用默认交易处理器
            AssembleTransactionProcessor institutionTxProcessor =
                    TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, keyPair);

            List<Object> params = new ArrayList<>();
            params.add(record.getAdmissionRecordID());
            params.add(Utils.letRecordHashed(letRecordStringed(record)));
            TransactionResponse response = institutionTxProcessor.sendTransactionAndGetResponse(
                    this.address,
                    ContractConstants.InpatientAbi,
                    "createRecord",
                    params
            );
            if (!response.getTransactionReceipt().isStatusOK()) {
                throw new RuntimeException("hash上链失败: " + response.getTransactionReceipt().getMessage());
            } else{
                System.out.println("Hash 上链成功！");
                System.out.println("交易哈希: " + response.getTransactionReceipt().getTransactionHash());
                System.out.println("记录ID: " + record.getAdmissionRecordID());
            }
        }

        return rowcount;
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
    public int updateInpatientRecord(InpatientRecord record, String identity) {
        record.setDiagnosisDate(new Date());
        record.setInstitutionCode(identity);  // 机构自动完成签名
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