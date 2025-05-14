package org.example.demo.service.impl;

import org.example.demo.common.JwtUtils;
import org.example.demo.common.Utils;
import org.example.demo.config.ContractConfig;
import org.example.demo.constants.ContractConstants;
import org.example.demo.mapper.PatientMapper;
import org.example.demo.model.bo.PatientRegisterInputBO;
import org.example.demo.model.dto.PatientLoginDTO;
import org.example.demo.model.dto.PatientRegistrationDTO;
import org.example.demo.model.entity.Patient;
import org.example.demo.model.dto.PatientInfoVO;
import org.example.demo.service.PatientService;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientMapper patientMapper;

    @Override
    public void addPatient(PatientRegistrationDTO patientdto) throws Exception {
        Patient patient = patientMapper.getPatient(
                patientdto.getIdentity(),
                patientdto.getRealname(),
                patientdto.getPassword()
        );
        if(patient != null) {
            throw new Exception("Error: patient already exists");
        }

        Patient _patient = new Patient();
        _patient.setIdentity(patientdto.getIdentity());
        _patient.setRealname(patientdto.getRealname());
        _patient.setPassword(patientdto.getPassword());
        patientMapper.insertPatient(_patient);

        registerPatient(patientdto);
    }

    @Override
    public String getToken(PatientLoginDTO patientdto) throws Exception {
        Patient patient = patientMapper.getPatient(
                patientdto.getIdentity(),
                patientdto.getRealname(),
                patientdto.getPassword()
        );
        if(patient == null) {
            throw new Exception("Error: no such patient");
        }
        // generate token
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", patientdto.getIdentity());
        String token = JwtUtils.generateToken(claims);
        if(token == null) {
            throw new Exception("Error: token is null");
        }
        return token;
    }

    @Override
    public PatientInfoVO getInfo(String identity) throws Exception {
        Patient patient = patientMapper.getPatientById(identity);
        if (patient == null) {
            throw new Exception("Error: patient not found");
        }
        return getPatientInfo(identity);
    }

    /*
     * 后端与区块链交互部分
     * */

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
        this.address = contractConfig.getPatientContractAddress();
    }

    public void registerPatient(PatientRegistrationDTO dto) throws Exception {
        // 生成新的密钥对
        CryptoKeyPair userKeyPair = new ECDSAKeyPair().generateKeyPair();
        String userAddress = userKeyPair.getAddress();
        
        // 保存私钥到文件
        Utils.savePrivateKeyToFile(userKeyPair.getHexPrivateKey(), dto.getIdentity());
        
        // 将LocalDate转换为字符串格式
        String birthdateStr = dto.getBirthdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // 调用合约注册用户
        PatientRegisterInputBO input = new PatientRegisterInputBO(
                userAddress,
                dto.getIdentity(),
                dto.getRealname(),
                dto.getGendercode(),
                birthdateStr
        );
        
        TransactionResponse response = this.txProcessor.sendTransactionAndGetResponse(
                this.address, 
                ContractConstants.PatientAbi, 
                "registerPatient", 
                input.toArgs()
        );
        
        if (!response.getTransactionReceipt().isStatusOK()) {
            throw new RuntimeException("患者注册失败: " + response.getTransactionReceipt().getMessage());
        }
    }

    public PatientInfoVO getPatientInfo(String identityFile) throws Exception {
        // 从私钥文件中读取私钥
        String privateKey = Utils.readPrivateKeyFromFile(identityFile);
        if (privateKey == null) {
            throw new RuntimeException("用户私钥不存在");
        }

        // 使用私钥创建用户密钥对
        ECDSAKeyPair ecdsaKeyPair = new ECDSAKeyPair();
        CryptoKeyPair userKeyPair = ecdsaKeyPair.createKeyPair(privateKey);
        String userAddress = userKeyPair.getAddress();

        // 调用合约获取用户信息
        List<Object> params = new ArrayList<Object>();
        params.add(userAddress);
        
        CallResponse response = this.txProcessor.sendCall(
                userAddress,
                this.address,
                ContractConstants.PatientAbi,
                "getPatientInfo",
                params
        );

        if (response.getResults() == null || response.getResults().isEmpty()) {
            throw new RuntimeException("获取用户信息失败");
        }

        // 解析返回结果
        List<Type> results = response.getResults();
        String identity = ((Utf8String) results.get(0)).getValue();
        String realName = ((Utf8String) results.get(1)).getValue();
        String genderCode = ((Utf8String) results.get(2)).getValue();
        String birthdate = ((Utf8String) results.get(3)).getValue();

        return new PatientInfoVO(identity, realName, genderCode, birthdate);
    }
}
