package org.example.demo.config;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collections;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.host}")
    private String host;
    @Value("${spring.data.mongodb.port}")
    private int port;
    @Value("${spring.data.mongodb.database}")
    private String database;
    // 使用 @Value 的默认值语法，如果属性未配置，username和password将默认为空字符串
    @Value("${spring.data.mongodb.username:}")
    private String username;
    @Value("${spring.data.mongodb.password:}")
    private String password;

    @Bean
    public MongoClient mongoClient() {
        com.mongodb.MongoClientSettings.Builder settingsBuilder =
                com.mongodb.MongoClientSettings.builder()
                        .applyToClusterSettings(builder ->
                                builder.hosts(Collections.singletonList(new ServerAddress(host, port))));

        if (username != null && !username.isEmpty()) {
            MongoCredential credential = MongoCredential.createCredential(
                    username,
                    database,
                    password.toCharArray()
            );
            settingsBuilder.credential(credential);
            System.out.println("MongoDB configured with authentication for database: " + database);
        } else {
            System.out.println("MongoDB configured without authentication (no username provided) for database: " + database);
        }

        return MongoClients.create(settingsBuilder.build());
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, database);
    }
}