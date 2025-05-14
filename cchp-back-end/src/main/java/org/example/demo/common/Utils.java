package org.example.demo.common;

import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    public static void savePrivateKeyToFile(String privateKey, String identity) throws IOException {
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

    public static String readPrivateKeyFromFile(String identityFile) throws IOException {
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

    public static Bytes32 letRecordHashed(String record) {
        try{
            // 使用 SHA-256 哈希算法
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(record.getBytes(StandardCharsets.UTF_8));

            System.out.println("Hashed: " + new String(hashBytes));

            return new Bytes32(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash计算失败", e);
        }
    }

    // 将 Bytes32 对象转为 byte[]，再格式化为十六进制字符串
    public static String bytes32ToHex(Bytes32 bytes32) {
        byte[] bytes = bytes32.getValue(); // 获取底层 byte[]
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
