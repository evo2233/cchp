// SPDX-License-Identifier: MIT
pragma solidity >=0.6.10 <0.8.20;

contract User {
    struct Patient {
        string realName;
        string idCard;
        bool isRegistered;
    }

    mapping(address => Patient) public patients;

    function registerUser(address userAddr, string memory realName, string memory idCard) public {
        require(!patients[userAddr].isRegistered, "User already registered");
        patients[userAddr] = Patient(realName, idCard, true);
    }

    function getPatientInfo(address userAddr) public view returns (string memory, string memory) {
        require(patients[userAddr].isRegistered, "User not found");
        return (patients[userAddr].realName, patients[userAddr].idCard);
    }
}
