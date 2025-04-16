package org.example.demo.service.impl;

import org.example.demo.config.ContractConfig;
import org.example.demo.constants.ContractConstants;
import org.example.demo.model.bo.InstitutionRegisterInputBO;
import org.example.demo.model.dto.InstitutionDTO;
import org.example.demo.service.InstitutionService;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public boolean authorizeInstitution(InstitutionRegisterInputBO input, String adminAddress) {
        try {
            List<Object> params = new ArrayList<>();
            params.add(input.getInstitutionCode());
            params.add(input.getInstitutionName());
            params.add(adminAddress);
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
        List<Type> codeTypes = (List<Type>) results.get(0);
        List<String> codes = new ArrayList<>();
        for (Type codeType : codeTypes) {
            codes.add((String) codeType.getValue());
        }

        // 获取机构名称数组
        List<Type> nameTypes = (List<Type>) results.get(1);
        List<String> names = new ArrayList<>();
        for (Type nameType : nameTypes) {
            names.add((String) nameType.getValue());
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
    public boolean isAuthorizedAddress(String institutionAddress) {
        try {
            List<Object> params = new ArrayList<>();
            params.add(institutionAddress);

            CallResponse response = this.txProcessor.sendCall(
                    this.client.getCryptoSuite().getCryptoKeyPair().getAddress(),
                    this.address,
                    ContractConstants.InstitutionAbi,
                    "isAuthorizedAddress",
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
    public String getInstitutionCode(String institutionAddress) {
        try {
            List<Object> params = new ArrayList<>();
            params.add(institutionAddress);

            CallResponse response = this.txProcessor.sendCall(
                    this.client.getCryptoSuite().getCryptoKeyPair().getAddress(),
                    this.address,
                    ContractConstants.InstitutionAbi,
                    "getInstitutionCode",
                    params
            );

            if (response.getResults() == null || response.getResults().isEmpty()) {
                return null;
            }

            return (String) (response.getResults().get(0)).getValue();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
