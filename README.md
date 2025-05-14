# 云链医道（CCHP）

本项目基于Java SDK + Gradle + SpringBoot方式来调用智能合约。后端使用 FISCO 官方提供的 [spring-boot-starter](https://github.com/FISCO-BCOS/spring-boot-starter) 主分支为基础开发。



## 前置条件

搭建FISCO BCOS 单群组区块链（Air版本），具体步骤[参考这里](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/tutorial/air/build_chain.html) 。

阅读 spring-boot-starter 的 [README](https://github.com/FISCO-BCOS/spring-boot-starter/blob/master/README.md)，并理解其项目架构。



## 相关链接

- FISCO BCOS： [FISCO BCOS文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/introduction.html)。
- Java Sdk： [JavaSdk文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)。
- SpringBoot文档： [Spring Boot](https://spring.io/guides/gs/spring-boot/)。
- Maven工程示例：[maven示例](https://github.com/FISCO-BCOS/spring-boot-crud)。



## 新合约所需配置

```
\cchp-back-end\src\main\java\org.example.demo
|	\config
	|	\ContractConfig一处
	|	\SdkBeanConfig三处
		|public Client client()
		|private void loadContractAddresses()
		|private void saveContractAddresses()
|	\constants
	|	\ContractConstants两处
```

