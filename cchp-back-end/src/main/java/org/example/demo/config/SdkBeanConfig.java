package org.example.demo.config;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.constants.ContractConstants;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.config.model.ConfigProperty;
import org.fisco.bcos.sdk.v3.model.CryptoType;
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
        if (contractConfig.getHelloWorldAddress() == null || contractConfig.getHelloWorldAddress().isEmpty()) {
            String address = deploy(client);
            contractConfig.setHelloWorldAddress(address);
            saveContractAddresses();
        }

        if(contractConfig.getPatientAddress() == null || contractConfig.getPatientAddress().isEmpty()) {
            String address = deployContract(client, ContractConstants.PatientAbi, ContractConstants.PatientBinary);
            contractConfig.setPatientAddress(address);
            saveContractAddresses();
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
                        if ("helloWorldAddress".equals(key)) {
                            contractConfig.setHelloWorldAddress(value);
                        } else if ("patientAddress".equals(key)) {
                            contractConfig.setPatientAddress(value);
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
            content.append("helloWorldAddress=").append(contractConfig.getHelloWorldAddress()).append("\n");
            content.append("patientAddress=").append(contractConfig.getPatientAddress()).append("\n");

            try (FileWriter writer = new FileWriter(CONTRACT_ADDRESS_FILE)) {
                writer.write(content.toString());
            }
        } catch (IOException e) {
            log.error("Failed to save contract addresses to file", e);
        }
    }

    private String deploy(Client client) throws Exception {
        AssembleTransactionProcessor txProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        client, client.getCryptoSuite().getCryptoKeyPair());
        String abi = ContractConstants.HelloWorldAbi;
        String bin =
                (client.getCryptoSuite().getCryptoTypeConfig() == CryptoType.ECDSA_TYPE)
                        ? ContractConstants.HelloWorldBinary
                        : ContractConstants.HelloWorldGmBinary;
        TransactionReceipt receipt =
                txProcessor.deployAndGetResponse(abi, bin, Arrays.asList()).getTransactionReceipt();
        if (receipt.isStatusOK()) {
            return receipt.getContractAddress();
        } else {
            throw new RuntimeException("Deploy failed");
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
