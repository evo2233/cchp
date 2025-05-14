package org.example.demo.service.impl;

import org.example.demo.common.Utils;
import org.example.demo.config.ContractConfig;
import org.example.demo.constants.ContractConstants;
import org.example.demo.model.entity.InpatientRecord;
import org.example.demo.model.entity.CourseRecordDetail;
import org.example.demo.mapper.InpatientMapper;
import org.example.demo.service.InpatientService;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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

    @PostConstruct
    public void init() throws Exception {
        this.address = contractConfig.getInpatientContractAddress();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    private String letRecordStringed(InpatientRecord record) {
        return record.getAdmissionRecordID() +
                       record.getResidentHealthCardID() +
                       record.getInstitutionCode() +
                       record.getAdmissionNumber() +
                       record.getAdmissionConditionCode() +
                       record.getTotalHospitalizationCost();
    }

    /**
     * 创建机构交易处理器
     * @param institutionCode 机构代码
     * @return 交易处理器
     * @throws Exception 如果私钥不存在或创建失败
     */
    private AssembleTransactionProcessor createInstitutionTxProcessor(String institutionCode) throws Exception {
        // 从文件读取机构私钥
        String privateKey = Utils.readPrivateKeyFromFile(institutionCode);
        if (privateKey == null) {
            throw new RuntimeException("机构私钥不存在");
        }

        // 使用私钥创建机构密钥对
        ECDSAKeyPair ecdsaKeyPair = new ECDSAKeyPair();
        CryptoKeyPair keyPair = ecdsaKeyPair.createKeyPair(privateKey);
        
        // 使用机构密钥对创建新的交易处理器
        return TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, keyPair);
    }

    /**
     * 执行区块链交易
     * @param txProcessor 交易处理器
     * @param functionName 函数名
     * @param params 参数列表
     * @throws Exception 如果交易失败
     */
    private void executeBlockchainTransaction(AssembleTransactionProcessor txProcessor, 
                                           String functionName, 
                                           List<Object> params) throws Exception {
        TransactionResponse response = txProcessor.sendTransactionAndGetResponse(
                this.address,
                ContractConstants.InpatientAbi,
                functionName,
                params
        );

        if (!response.getTransactionReceipt().isStatusOK()) {
            throw new RuntimeException(functionName + "失败: " + response.getTransactionReceipt().getMessage());
        }
    }

    @Override
    public int insertInpatientRecord(InpatientRecord record, String identity) throws Exception {
        record.setDiagnosisDate(new Date());
        record.setInstitutionCode(identity);

        int rowcount = inpatientMapper.insertInpatientRecord(record);
        if(rowcount == 1) {
            AssembleTransactionProcessor institutionTxProcessor = createInstitutionTxProcessor(identity);

            List<Object> params = new ArrayList<>();
            params.add(record.getAdmissionRecordID());
            params.add(Utils.letRecordHashed(letRecordStringed(record)));

            executeBlockchainTransaction(institutionTxProcessor, "createRecord", params);
            
            System.out.println("Hash 上链成功！记录ID: " + record.getAdmissionRecordID());
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

    private void InvalidateRecord(Integer admissionRecordID, String identity) throws Exception {
        // 获取要删除的记录信息
        InpatientRecord record = inpatientMapper.selectInpatientRecordById(admissionRecordID);
        if (record == null) {
            throw new RuntimeException("记录不存在");
        }

        // 使记录在链上失效
        AssembleTransactionProcessor institutionTxProcessor = createInstitutionTxProcessor(identity);
        List<Object> params = new ArrayList<>();
        params.add(admissionRecordID);
        executeBlockchainTransaction(institutionTxProcessor, "invalidateRecord", params);
    }

    @Override
    @Transactional
    public int deleteInpatientRecord(Integer admissionRecordID, String identity) throws Exception {
        InvalidateRecord(admissionRecordID, identity);

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
    public int updateInpatientRecord(InpatientRecord record, String identity) throws Exception {
        record.setDiagnosisDate(new Date());
        record.setInstitutionCode(identity);  // 机构自动完成签名

        int rowcount = inpatientMapper.updateInpatientRecord(record);
        if (rowcount > 0) {
            AssembleTransactionProcessor institutionTxProcessor = createInstitutionTxProcessor(identity);

            List<Object> params = new ArrayList<>();
            params.add(record.getAdmissionRecordID());
            params.add(Utils.letRecordHashed(letRecordStringed(record)));

            executeBlockchainTransaction(institutionTxProcessor, "updateRecord", params);
        }
        return rowcount;
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

    private boolean VerifyHash(InpatientRecord record) throws Exception {
        String rawString = letRecordStringed(record);
        Bytes32 localHash = Utils.letRecordHashed(rawString);

        AssembleTransactionProcessor institutionTxProcessor = createInstitutionTxProcessor(record.getInstitutionCode());
        List<Object> params = new ArrayList<>();
        params.add(record.getAdmissionRecordID());
        CallResponse response = institutionTxProcessor.sendCall(
                null,
                this.address,
                ContractConstants.InpatientAbi,
                "getRecord",
                params
        );

        List<Type> results = response.getResults();
        Bytes32 chainHash = (Bytes32) results.get(0);
        String localHashString = Utils.bytes32ToHex(localHash);
        String chainHashString = Utils.bytes32ToHex(chainHash);
        System.out.println("==数据库记录==");
        System.out.println("记录ID: " + record.getAdmissionRecordID());
        System.out.println("拼接内容: " + rawString);
        System.out.println("本地计算哈希: " + localHashString);

        System.out.println("==链上记录==");
        System.out.println("链上哈希: " + chainHashString);
        System.out.println("时间戳: " + results.get(1).getValue());
        System.out.println("机构地址: " + results.get(2).getValue());
        System.out.println("是否有效: " + results.get(3).getValue());

        return localHashString.equals(chainHashString);
    }

    @Override
    public List<InpatientRecord> selectInpatientRecords(String institutionCode, String residentHealthCardID, String diagnosisDate) throws Exception {
        List<InpatientRecord> records = inpatientMapper.selectInpatientRecords(institutionCode, residentHealthCardID, diagnosisDate);

        // 验证每条记录的完整性
        Iterator<InpatientRecord> iterator = records.iterator();
        while (iterator.hasNext()) {
            InpatientRecord record = iterator.next();
            if (!VerifyHash(record)) {
                iterator.remove();
            }
        }
        return records;
    }

    @Override
    public List<CourseRecordDetail> getCourseRecordDetails(Integer admissionRecordID) {
        return inpatientMapper.selectCourseRecordDetails(admissionRecordID);
    }
} 