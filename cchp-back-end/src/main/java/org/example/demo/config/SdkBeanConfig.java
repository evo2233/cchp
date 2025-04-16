package org.example.demo.config;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.constants.ContractConstants;
import org.example.demo.service.AdminService;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.config.model.ConfigProperty;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class SdkBeanConfig {

    private static final String CONTRACT_ADDRESS_FILE = "contract_addresses.properties";

    @Autowired private SystemConfig systemConfig;
    @Autowired private BcosConfig bcosConfig;
    @Autowired private ContractConfig contractConfig;

    private final AdminService adminService;
    @Autowired
    SdkBeanConfig(AdminService adminService) { this.adminService = adminService; }

    @Bean
    public Client client() throws Exception {
        ConfigProperty property = new ConfigProperty();
        configNetwork(property);
        configCryptoMaterial(property);

        ConfigOption configOption = new ConfigOption(property);
        Client client = new BcosSDK(configOption).getClient(systemConfig.getGroupName());

        BigInteger blockNumber = client.getBlockNumber().getBlockNumber();
        if (log.isInfoEnabled()) {
            log.info("Chain connect successful. Current block number {}", blockNumber);
        }

        configCryptoKeyPair(client);
        if (log.isInfoEnabled()) {
            log.info(
                    "Your account is Gm:{}, address:{}",
                    client.getCryptoSuite().cryptoTypeConfig == 1,
                    client.getCryptoSuite().getCryptoKeyPair().getAddress());
        }

        // 尝试从文件加载合约地址
        loadContractAddresses();

        // 如果合约地址为空，则部署新合约
        if(contractConfig.getPatientContractAddress() == null || contractConfig.getPatientContractAddress().isEmpty()) {
            String address = deployContract(client, ContractConstants.PatientAbi, ContractConstants.PatientBinary);
            contractConfig.setPatientContractAddress(address);
            saveContractAddresses();
        }

        if(contractConfig.getInstitutionContractAddress() == null || contractConfig.getInstitutionContractAddress().isEmpty()) {
            String address = deployContract(client, ContractConstants.InstitutionAbi, ContractConstants.InstitutionBinary);
            contractConfig.setInstitutionContractAddress(address);
            saveContractAddresses();

            // 获取部署者的地址并初始化管理员
            String deployerAddress = client.getCryptoSuite().getCryptoKeyPair().getAddress();
            adminService.initializeAdmin(deployerAddress);
        }

        return client;
    }

    private void loadContractAddresses() {
        try {
            File file = new File(CONTRACT_ADDRESS_FILE);
            if (file.exists()) {
                String content = new String(Files.readAllBytes(Paths.get(CONTRACT_ADDRESS_FILE)));
                String[] lines = content.split("\n");
                for (String line : lines) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        if ("patientAddress".equals(key)) {
                            contractConfig.setPatientContractAddress(value);
                        } else if ("institutionAddress".equals(key)) {
                            contractConfig.setInstitutionContractAddress(value);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.warn("Failed to load contract addresses from file", e);
        }
    }

    private void saveContractAddresses() {
        try {
            StringBuilder content = new StringBuilder();
            content.append("patientAddress=").append(contractConfig.getPatientContractAddress()).append("\n");
            content.append("institutionAddress=").append(contractConfig.getInstitutionContractAddress()).append("\n");

            try (FileWriter writer = new FileWriter(CONTRACT_ADDRESS_FILE)) {
                writer.write(content.toString());
            }
        } catch (IOException e) {
            log.error("Failed to save contract addresses to file", e);
        }
    }

    /*Universal contract deploy function*/
    private String deployContract(Client client, String abi, String bin) throws Exception {
        AssembleTransactionProcessor txProcessor =
            TransactionProcessorFactory.createAssembleTransactionProcessor(
                    client, client.getCryptoSuite().getCryptoKeyPair());
    
        TransactionReceipt receipt =
            txProcessor.deployAndGetResponse(abi, bin, Arrays.asList()).getTransactionReceipt();
    
        if (receipt.isStatusOK()) {
            return receipt.getContractAddress();
        } else {
            throw new RuntimeException("Contract deploy failed: " + receipt.getMessage());
        }
    }

    public void configNetwork(ConfigProperty configProperty) {
        Map peers = bcosConfig.getNetwork();
        configProperty.setNetwork(peers);
    }

    public void configCryptoMaterial(ConfigProperty configProperty) {
        Map<String, Object> cryptoMaterials = bcosConfig.getCryptoMaterial();
        configProperty.setCryptoMaterial(cryptoMaterials);
    }

    public void configCryptoKeyPair(Client client) {
        if (systemConfig.getHexPrivateKey() == null || systemConfig.getHexPrivateKey().isEmpty()) {
            return;
        }
        if (systemConfig.getHexPrivateKey().startsWith("0x")
                || systemConfig.getHexPrivateKey().startsWith("0X")) {
            systemConfig.setHexPrivateKey(systemConfig.getHexPrivateKey().substring(2));
        }
        client.getCryptoSuite().setCryptoKeyPair(client.getCryptoSuite().getCryptoKeyPair());
    }
}
