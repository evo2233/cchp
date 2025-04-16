// SPDX-License-Identifier: MIT
pragma solidity >=0.6.10 <0.8.20;

contract Institution {
    // 管理员地址
    address public admin;

    // 授权医疗机构结构体
    struct InstitutionData {
        string institutionCode;
        string institutionName;
        bool isAuthorized;
    }

    // 存储结构
    mapping(string => InstitutionData) private institutions;        // 机构代码 => 机构信息
    mapping(address => string) private addressToCode;          // 机构地址 => 机构代码
    string[] private authorizedCodes;                         // 所有授权的机构代码列表

    // 事件
    event InstitutionAuthorized(string institutionCode, string institutionName, address institutionAddress);
    event InstitutionRevoked(string institutionCode);

    // 构造函数
    constructor() {
        admin = msg.sender;
    }

    // 修饰符：只有管理员可以调用
    modifier onlyAdmin(address _adminAddress) {
        require(_adminAddress == admin, "Only admin can call this function");
        _;
    }

    // 授权医疗机构
    function authorizeInstitution(
        string memory institutionCode,
        string memory institutionName,
        address institutionAddress,
        address _adminAddress
    ) public onlyAdmin(_adminAddress) {
        require(!institutions[institutionCode].isAuthorized, "Institution already authorized");
        require(bytes(addressToCode[institutionAddress]).length == 0, "Address already associated with an institution");

        institutions[institutionCode] = InstitutionData(
            institutionCode,
            institutionName,
            true
        );
        
        addressToCode[institutionAddress] = institutionCode;
        authorizedCodes.push(institutionCode);

        emit InstitutionAuthorized(institutionCode, institutionName, institutionAddress);
    }

    // 撤销医疗机构授权
    function revokeInstitution(
        string memory institutionCode,
        address _adminAddress
    ) public onlyAdmin(_adminAddress) {
        require(institutions[institutionCode].isAuthorized, "Institution not found or already revoked");
        
        institutions[institutionCode].isAuthorized = false;

        // 从授权列表中移除
        for(uint i = 0; i < authorizedCodes.length; i++) {
            if(keccak256(bytes(authorizedCodes[i])) == keccak256(bytes(institutionCode))) {
                // 将最后一个元素移到当前位置，然后删除最后一个元素
                authorizedCodes[i] = authorizedCodes[authorizedCodes.length - 1];
                authorizedCodes.pop();
                break;
            }
        }

        emit InstitutionRevoked(institutionCode);
    }

    // 获取所有授权的医疗机构
    function getAllAuthorizedInstitutions() public view returns (
        string[] memory codes,
        string[] memory names
    ) {
        codes = new string[](authorizedCodes.length);
        names = new string[](authorizedCodes.length);

        for(uint i = 0; i < authorizedCodes.length; i++) {
            codes[i] = authorizedCodes[i];
            names[i] = institutions[authorizedCodes[i]].institutionName;
        }

        return (codes, names);
    }

    // 根据机构代码判断是否有授权
    function isAuthorized(string memory institutionCode) public view returns (bool) {
        return institutions[institutionCode].isAuthorized;
    }

    // 根据机构地址判断是否有授权
    function isAuthorizedAddress(address institutionAddress) public view returns (bool) {
        string memory code = addressToCode[institutionAddress];
        return institutions[code].isAuthorized;
    }

    // 根据机构地址获取机构代码
    function getInstitutionCode(address institutionAddress) public view returns (string memory) {
        string memory code = addressToCode[institutionAddress];
        require(institutions[code].isAuthorized, "Institution not found or not authorized");
        return code;
    }
}
