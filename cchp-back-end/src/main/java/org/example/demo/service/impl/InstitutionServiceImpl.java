package org.example.demo.service.impl;

import org.example.demo.common.JwtUtils;
import org.example.demo.common.Utils;
import org.example.demo.config.ContractConfig;
import org.example.demo.constants.ContractConstants;
import org.example.demo.model.dto.InstitutionDTO;
import org.example.demo.service.InstitutionService;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InstitutionServiceImpl implements InstitutionService {

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
        this.address = contractConfig.getInstitutionContractAddress();
    }

    // 注册机构 暨 授权机构
    @Override
    public boolean authorizeInstitution(InstitutionDTO input, String adminAddress) {
        try {
            // 生成新的密钥对
            CryptoKeyPair KeyPair = new ECDSAKeyPair().generateKeyPair();
            String institutionAddress = KeyPair.getAddress();

            // 保存私钥到文件
            Utils.savePrivateKeyToFile(KeyPair.getHexPrivateKey(), input.getInstitutionCode());

            List<Object> params = new ArrayList<>();
            params.add(input.getInstitutionCode());
            params.add(input.getInstitutionName());
            params.add(institutionAddress);
            params.add(adminAddress);

            TransactionResponse response = this.txProcessor.sendTransactionAndGetResponse(
                    this.address,
                    ContractConstants.InstitutionAbi,
                    "authorizeInstitution",
                    params
            );

            return response.getTransactionReceipt().isStatusOK();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean revokeInstitution(String institutionCode, String adminAddress) {
        try {
            List<Object> params = new ArrayList<>();
            params.add(institutionCode);
            params.add(adminAddress);

            TransactionResponse response = this.txProcessor.sendTransactionAndGetResponse(
                    this.address,
                    ContractConstants.InstitutionAbi,
                    "revokeInstitution",
                    params
            );

            return response.getTransactionReceipt().isStatusOK();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<InstitutionDTO> getAllAuthorizedInstitutions() {
        try {
            List<Object> params = new ArrayList<>();
            
            CallResponse response = this.txProcessor.sendCall(
                    this.client.getCryptoSuite().getCryptoKeyPair().getAddress(),
                    this.address,
                    ContractConstants.InstitutionAbi,
                    "getAllAuthorizedInstitutions",
                    params
            );

            if (response.getResults() == null || response.getResults().isEmpty()) {
                return new ArrayList<>();
            }

            List<InstitutionDTO> result = getInstitutionDTOS(response);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static List<InstitutionDTO> getInstitutionDTOS(CallResponse response) {
        List<Type> results = response.getResults();
        
        // 获取机构代码数组
        DynamicArray codeTypes = (DynamicArray) results.get(0);
        List<String> codes = new ArrayList<>();
        for (Object codeType : codeTypes.getValue()) {
            codes.add((String) ((Type) codeType).getValue());
        }

        // 获取机构名称数组
        DynamicArray nameTypes = (DynamicArray) results.get(1);
        List<String> names = new ArrayList<>();
        for (Object nameType : nameTypes.getValue()) {
            names.add((String) ((Type) nameType).getValue());
        }

        List<InstitutionDTO> result = new ArrayList<>();
        for (int i = 0; i < codes.size(); i++) {
            InstitutionDTO dto = new InstitutionDTO();
            dto.setInstitutionCode(codes.get(i));
            dto.setInstitutionName(names.get(i));
            result.add(dto);
        }
        return result;
    }

    @Override
    public boolean isAuthorized(String institutionCode) {
        try {
            List<Object> params = new ArrayList<>();
            params.add(institutionCode);

            CallResponse response = this.txProcessor.sendCall(
                    this.client.getCryptoSuite().getCryptoKeyPair().getAddress(),
                    this.address,
                    ContractConstants.InstitutionAbi,
                    "isAuthorized",
                    params
            );

            if (response.getResults() == null || response.getResults().isEmpty()) {
                return false;
            }

            return (boolean) (response.getResults().get(0)).getValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getToken(InstitutionDTO dto) throws Exception {
        if(!isAuthorized(dto.getInstitutionCode())) {
            throw new Exception("Error: not authorized");
        }
        // generate token
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", dto.getInstitutionCode());
        String token = JwtUtils.generateToken(claims);
        if(token == null) {
            throw new Exception("Error: token is null");
        }
        return token;
    }

    private String getInstitutionAddress(String identityFile) throws IOException {
        // 从私钥文件中读取私钥
        String privateKey = Utils.readPrivateKeyFromFile(identityFile);
        if (privateKey == null) {
            throw new RuntimeException("用户私钥不存在");
        }

        // 使用私钥创建用户密钥对
        ECDSAKeyPair ecdsaKeyPair = new ECDSAKeyPair();
        CryptoKeyPair KeyPair = ecdsaKeyPair.createKeyPair(privateKey);
        String institutionAddress = KeyPair.getAddress();

        return institutionAddress;
    }
}
