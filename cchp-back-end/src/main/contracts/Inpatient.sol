// SPDX-License-Identifier: MIT
pragma solidity >=0.6.10 <0.8.20;

import "./Institution.sol";

contract Inpatient {
    Institution private institutionContract;
    
    constructor(address institutionContractAddress) {
        institutionContract = Institution(institutionContractAddress);
    }
    
    // 修饰符：检查机构授权
    modifier onlyAuthorizedInstitution() {
        require(institutionContract.isAuthorizedAddress(msg.sender), "Institution not authorized");
        _;
    }

    struct Record {
        bytes32 recordHash;
        uint256 timestamp;
        address institution;
        bool isValid;
    }
    
    // 映射：住院记录ID => 记录信息
    mapping(uint256 => Record) public records;
    
    // 事件
    event RecordCreated(uint256 indexed recordId, bytes32 recordHash, address institution);
    event RecordUpdated(uint256 indexed recordId, bytes32 recordHash, address institution);
    event RecordInvalidated(uint256 indexed recordId, address institution);
    
    // 创建新记录
    function createRecord(uint256 recordId, bytes32 recordHash) public onlyAuthorizedInstitution {
        require(records[recordId].timestamp == 0, "Record already exists");
        
        records[recordId] = Record({
            recordHash: recordHash,
            timestamp: block.timestamp,
            institution: msg.sender,
            isValid: true
        });
        
        emit RecordCreated(recordId, recordHash, msg.sender);
    }
    
    // 更新记录
    function updateRecord(uint256 recordId, bytes32 newRecordHash) public onlyAuthorizedInstitution {
        require(records[recordId].timestamp != 0, "Record does not exist");
        require(records[recordId].isValid, "Record is invalid");
        require(records[recordId].institution == msg.sender, "Only the institution that created the record can update it");
        
        records[recordId].recordHash = newRecordHash;
        records[recordId].timestamp = block.timestamp;
        
        emit RecordUpdated(recordId, newRecordHash, msg.sender);
    }
    
    // 验证记录
    function verifyRecord(uint256 recordId, bytes32 recordHash) public view returns (bool) {
        return records[recordId].recordHash == recordHash && 
               records[recordId].isValid;
    }
    
    // 获取记录信息
    function getRecord(uint256 recordId) public view returns (
        bytes32 recordHash,
        uint256 timestamp,
        address institution,
        bool isValid
    ) {
        Record memory record = records[recordId];
        return (record.recordHash, record.timestamp, record.institution, record.isValid);
    }
    
    // 使记录失效（用于删除操作）
    function invalidateRecord(uint256 recordId) public onlyAuthorizedInstitution {
        require(records[recordId].timestamp != 0, "Record does not exist");
        require(records[recordId].isValid, "Record is already invalid");
        require(records[recordId].institution == msg.sender, "Only the institution that created the record can invalidate it");
        
        records[recordId].isValid = false;
        emit RecordInvalidated(recordId, msg.sender);
    }
}