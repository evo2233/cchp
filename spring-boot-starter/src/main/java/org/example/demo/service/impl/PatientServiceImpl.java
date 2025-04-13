package org.example.demo.service.impl;

import org.example.demo.config.ContractConfig;
import org.example.demo.constants.ContractConstants;
import org.example.demo.model.bo.PatientRegisterInputBO;
import org.example.demo.model.dto.PatientRegistrationDTO;
import org.example.demo.model.vo.PatientInfoVO;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

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
        this.address = contractConfig.getUserAddress();
    }

    @Override
    public void registerPatient(PatientRegistrationDTO dto) throws Exception {
        // 生成新的密钥对
        CryptoKeyPair userKeyPair = new ECDSAKeyPair().generateKeyPair();
        String userAddress = userKeyPair.getAddress();
        
        // 保存私钥到文件
        savePrivateKeyToFile(userKeyPair.getHexPrivateKey(), dto.getIdentity());
        
        // 调用合约注册用户
        PatientRegisterInputBO input = new PatientRegisterInputBO(
                userAddress,
                dto.getRealname(),
                dto.getIdentity()
        );
        
        TransactionResponse response = this.txProcessor.sendTransactionAndGetResponse(
                this.address, 
                ContractConstants.UserAbi, 
                "registerUser", 
                input.toArgs()
        );
        
        if (!response.getTransactionReceipt().isStatusOK()) {
            throw new RuntimeException("用户注册失败: " + response.getTransactionReceipt().getMessage());
        }
    }

    @Override
    public PatientInfoVO getPatientInfo(String identityFile) throws Exception {
        // 从私钥文件中读取私钥
        String privateKey = readPrivateKeyFromFile(identityFile);
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
                ContractConstants.UserAbi,
                "getPatientInfo",
                params
        );

        if (response.getResults() == null || response.getResults().isEmpty()) {
            throw new RuntimeException("获取用户信息失败");
        }

        // 解析返回结果
        List<Type> results = response.getResults();
        String realName = ((Utf8String) results.get(0)).getValue();
        String identity = ((Utf8String) results.get(1)).getValue();

        return new PatientInfoVO(realName, identity, userAddress);
    }

    private void savePrivateKeyToFile(String privateKey, String identity) throws IOException {
        // 创建存储私钥的目录
        File keyDir = new File("keys");
        if (!keyDir.exists()) {
            keyDir.mkdirs();
        }
        
        // 使用身份证号作为文件名
        File keyFile = new File(keyDir, identity + ".key");
        try (FileWriter writer = new FileWriter(keyFile)) {
            writer.write(privateKey);
        }
    }

    private String readPrivateKeyFromFile(String identityFile) throws IOException {
        File keyFile = new File("keys", identityFile + ".key");
        if (!keyFile.exists()) {
            return null;
        }
        
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(keyFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }
}
