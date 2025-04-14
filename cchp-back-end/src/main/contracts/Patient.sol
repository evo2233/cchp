// SPDX-License-Identifier: MIT
pragma solidity >=0.6.10 <0.8.20;

contract Patient {
    struct Patient {
        string identity;
        string realname;
        string gendercode;
        string birthdate;
        bool isRegistered;
    }

    mapping(address => Patient) public patients;

    // 注册患者
    function registerPatient(address userAddr, string memory identity, string memory realname, string memory gendercode, string memory birthdate) public {
        require(!patients[userAddr].isRegistered, "Patient already registered");
        patients[userAddr] = Patient(identity, realname, gendercode, birthdate, true);
    }

    // 获取患者信息
    function getPatientInfo(address userAddr) public view returns (string memory, string memory, string memory, string memory) {
        require(patients[userAddr].isRegistered, "Patient not found");
        Patient memory patient = patients[userAddr];
        return (patient.identity, patient.realname, patient.gendercode, patient.birthdate);
    }

    // 修改患者信息（除identity外）
    function updatePatientInfo(address userAddr, string memory realname, string memory gendercode, string memory birthdate) public {
        require(patients[userAddr].isRegistered, "Patient not found");
        Patient storage patient = patients[userAddr];
        patient.realname = realname;
        patient.gendercode = gendercode;
        patient.birthdate = birthdate;
    }

    // 注销患者
    function deletePatient(address userAddr) public {
        require(patients[userAddr].isRegistered, "Patient not found");
        delete patients[userAddr];
    }
}
