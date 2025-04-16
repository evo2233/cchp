// SPDX-License-Identifier: MIT
pragma solidity >=0.6.10 <0.8.20;
pragma experimental ABIEncoderV2;

import "./Institution.sol";
import "./Patient.sol";

contract Outpatient {
    // 引用其他合约
    Institution private institutionContract;
    Patient private patientContract;

    // 构造函数
    constructor(address institutionContractAddress, address patientContractAddress) {
        institutionContract = Institution(institutionContractAddress);
        patientContract = Patient(patientContractAddress);
    }

    // 修饰符：检查机构授权
    modifier onlyAuthorizedInstitution() {
        require(institutionContract.isAuthorizedAddress(msg.sender), "Institution not authorized");
        _;
    }

    // 数据结构定义
    struct OutpatientRecord {
        string outpatientRecordID;
        string residentHealthCardID;
        string institutionCode;
        string chiefComplaint;
        string presentIllnessHistory;
        string initialWesternDiagnosis;
        string diagnosisDate;
        address institutionAddress;
        bool isValid;
    }

    struct OutpatientPrescription {
        string prescriptionID;
        string outpatientRecordID;
        string prescriptionDate;
        address institutionAddress;
        bool isValid;
    }

    struct PrescriptionDetail {
        string prescriptionID;
        string medicineName;
        address institutionAddress;
        bool isValid;
    }

    // 存储结构
    mapping(string => OutpatientRecord) private outpatientRecords;
    mapping(string => OutpatientPrescription[]) private prescriptionsByRecord;
    mapping(string => PrescriptionDetail[]) private detailsByPrescription;
    mapping(string => string[]) private recordsByHealthCard;
    mapping(address => string[]) private recordsByInstitution;

    // 事件
    event OutpatientRecordCreated(string outpatientRecordID, string residentHealthCardID, address institutionAddress);
    event PrescriptionCreated(string prescriptionID, string outpatientRecordID, address institutionAddress);
    event PrescriptionDetailCreated(string prescriptionID, string medicineName, address institutionAddress);
    event OutpatientRecordUpdated(string outpatientRecordID, address institutionAddress);
    event OutpatientRecordDeleted(string outpatientRecordID, address institutionAddress);

    // 添加门诊记录
    function addOutpatientRecord(
        string memory outpatientRecordID,
        string memory residentHealthCardID,
        string memory chiefComplaint,
        string memory presentIllnessHistory,
        string memory initialWesternDiagnosis,
        string memory diagnosisDate
    ) public onlyAuthorizedInstitution {
        // 验证患者身份证号是否存在
        require(bytes(residentHealthCardID).length > 0, "ResidentHealthCardID cannot be empty");
        
        // 验证门诊记录是否已存在
        require(!outpatientRecords[outpatientRecordID].isValid, "OutpatientRecord already exists");
        
        // 获取机构代码
        string memory institutionCode = institutionContract.getInstitutionCode(msg.sender);
        
        OutpatientRecord memory record = OutpatientRecord(
            outpatientRecordID,
            residentHealthCardID,
            institutionCode,
            chiefComplaint,
            presentIllnessHistory,
            initialWesternDiagnosis,
            diagnosisDate,
            msg.sender,
            true
        );
        
        outpatientRecords[outpatientRecordID] = record;
        recordsByHealthCard[residentHealthCardID].push(outpatientRecordID);
        recordsByInstitution[msg.sender].push(outpatientRecordID);
        
        emit OutpatientRecordCreated(outpatientRecordID, residentHealthCardID, msg.sender);
    }

    // 添加处方
    function addPrescription(
        string memory prescriptionID,
        string memory outpatientRecordID,
        string memory prescriptionDate
    ) public onlyAuthorizedInstitution {
        require(outpatientRecords[outpatientRecordID].isValid, "OutpatientRecord not found");
        
        // 只有创建门诊记录的机构才能添加处方
        require(outpatientRecords[outpatientRecordID].institutionAddress == msg.sender, 
                "Only the institution that created the record can add prescriptions");
        
        OutpatientPrescription memory prescription = OutpatientPrescription(
            prescriptionID,
            outpatientRecordID,
            prescriptionDate,
            msg.sender,
            true
        );
        
        prescriptionsByRecord[outpatientRecordID].push(prescription);
        
        emit PrescriptionCreated(prescriptionID, outpatientRecordID, msg.sender);
    }

    // 添加处方明细
    function addPrescriptionDetail(
        string memory prescriptionID,
        string memory medicineName
    ) public onlyAuthorizedInstitution {
        // 验证处方是否存在
        bool prescriptionFound = false;
        for(uint i = 0; i < prescriptionsByRecord[outpatientRecords[prescriptionID].outpatientRecordID].length; i++) {
            if(keccak256(bytes(prescriptionsByRecord[outpatientRecords[prescriptionID].outpatientRecordID][i].prescriptionID)) == keccak256(bytes(prescriptionID))) {
                prescriptionFound = true;
                require(prescriptionsByRecord[outpatientRecords[prescriptionID].outpatientRecordID][i].institutionAddress == msg.sender,
                        "Only the institution that created the prescription can add details");
                break;
            }
        }
        require(prescriptionFound, "Prescription not found");
        
        PrescriptionDetail memory detail = PrescriptionDetail(
            prescriptionID,
            medicineName,
            msg.sender,
            true
        );
        
        detailsByPrescription[prescriptionID].push(detail);
        
        emit PrescriptionDetailCreated(prescriptionID, medicineName, msg.sender);
    }

    // 修改门诊记录
    function updateOutpatientRecord(
        string memory outpatientRecordID,
        string memory chiefComplaint,
        string memory presentIllnessHistory,
        string memory initialWesternDiagnosis,
        string memory diagnosisDate
    ) public onlyAuthorizedInstitution {
        require(outpatientRecords[outpatientRecordID].isValid, "OutpatientRecord not found");
        require(outpatientRecords[outpatientRecordID].institutionAddress == msg.sender, 
                "Only the institution that created the record can modify it");
        
        OutpatientRecord storage record = outpatientRecords[outpatientRecordID];
        record.chiefComplaint = chiefComplaint;
        record.presentIllnessHistory = presentIllnessHistory;
        record.initialWesternDiagnosis = initialWesternDiagnosis;
        record.diagnosisDate = diagnosisDate;
        
        emit OutpatientRecordUpdated(outpatientRecordID, msg.sender);
    }

    // 删除门诊记录（逻辑删除）
    function deleteOutpatientRecord(string memory outpatientRecordID) public onlyAuthorizedInstitution {
        require(outpatientRecords[outpatientRecordID].isValid, "OutpatientRecord not found");
        require(outpatientRecords[outpatientRecordID].institutionAddress == msg.sender, 
                "Only the institution that created the record can delete it");
        
        outpatientRecords[outpatientRecordID].isValid = false;
        
        emit OutpatientRecordDeleted(outpatientRecordID, msg.sender);
    }

    // 查询功能：根据居民健康卡ID查询门诊记录
    function getRecordsByHealthCard(string memory residentHealthCardID) 
        public view onlyAuthorizedInstitution returns (OutpatientRecord[] memory) {
        string[] memory recordIDs = recordsByHealthCard[residentHealthCardID];
        OutpatientRecord[] memory records = new OutpatientRecord[](recordIDs.length);
        
        uint validCount = 0;
        for(uint i = 0; i < recordIDs.length; i++) {
            if(outpatientRecords[recordIDs[i]].isValid) {
                records[validCount] = outpatientRecords[recordIDs[i]];
                validCount++;
            }
        }
        
        // 创建一个新数组，只包含有效记录
        OutpatientRecord[] memory validRecords = new OutpatientRecord[](validCount);
        for(uint i = 0; i < validCount; i++) {
            validRecords[i] = records[i];
        }
        
        return validRecords;
    }

    // 查询功能：根据门诊记录ID查询处方
    function getPrescriptionsByRecord(string memory outpatientRecordID) 
        public view onlyAuthorizedInstitution returns (OutpatientPrescription[] memory) {
        return prescriptionsByRecord[outpatientRecordID];
    }

    // 查询功能：根据处方ID查询处方明细
    function getDetailsByPrescription(string memory prescriptionID) 
        public view onlyAuthorizedInstitution returns (PrescriptionDetail[] memory) {
        return detailsByPrescription[prescriptionID];
    }

    // 查询功能：获取机构创建的所有门诊记录
    function getRecordsByInstitution() 
        public view onlyAuthorizedInstitution returns (OutpatientRecord[] memory) {
        string[] memory recordIDs = recordsByInstitution[msg.sender];
        OutpatientRecord[] memory records = new OutpatientRecord[](recordIDs.length);
        
        uint validCount = 0;
        for(uint i = 0; i < recordIDs.length; i++) {
            if(outpatientRecords[recordIDs[i]].isValid) {
                records[validCount] = outpatientRecords[recordIDs[i]];
                validCount++;
            }
        }
        
        // 创建一个新数组，只包含有效记录
        OutpatientRecord[] memory validRecords = new OutpatientRecord[](validCount);
        for(uint i = 0; i < validCount; i++) {
            validRecords[i] = records[i];
        }
        
        return validRecords;
    }
}