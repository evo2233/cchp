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



## 常见问题

### 新合约所需配置

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

### 跨域访问CROS配置

```
\cchp-back-end\src\main\java\org.example.demo
|	\config
	|	\WenConfig
	\common
	|	\JwtFilter
```

### 从开发环境到生产环境

1. 合约地址

​	当部署智能合约到链上时，系统会通过算法生成一个**唯一的合约地址**，它是智能合约在链上的唯一标识，专门用于管理智能合约的代码和数据。即使是同一份合约在多次部署时也会产生不同的地址，因此为了避免在后端重启后丢失持久化存储的链上数据，需要在部署合约时将地址存储到文件中。

​	系统在初次部署时，会在根目录生成一个`contract_addresses.properties`文件用于存储合约地址。在从开发环境移动到生产环境时，如果要保留测试数据，要在启动前复制此文件到生产环境，以便后台可以在初始化时读取到合约的地址，而不是重新部署合约。



2. 区块链账户私钥

​	每个患者、医生在注册时都会产生一个账户私钥。当用户发起交易（如调用智能合约）时，需用私钥对交易进行**数字签名**，证明该操作确实由私钥持有者发起。节点收到交易后，会通过签名验证确认交易的合法性，确保操作者拥有对应私钥（**验证身份**）且有进行此交易的权限（**鉴权**），同时也会作为交易发起者记录进日志（**可追踪**）。

​	本系统账户私钥存储在根目录下的`keys`文件夹里，文件名是患者ID和医生ID。如果想在生产环境使用，也记得都复制过去。
