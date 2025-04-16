package org.example.demo.service;

import org.example.demo.model.dto.InstitutionDTO;

import java.util.List;

public interface InstitutionService {
    /**
     * 授权医疗机构
     * @param input 机构注册信息
     * @param adminAddress 管理员地址
     * @return 是否授权成功
     */
    boolean authorizeInstitution(InstitutionDTO input, String adminAddress);

    /**
     * 撤销医疗机构授权
     * @param institutionCode 机构代码
     * @param adminAddress 管理员地址
     * @return 是否撤销成功
     */
    boolean revokeInstitution(String institutionCode, String adminAddress);

    /**
     * 获取所有授权的医疗机构
     * @return 机构列表
     */
    List<InstitutionDTO> getAllAuthorizedInstitutions();

    /**
     * 检查机构是否被授权
     * @param institutionCode 机构代码
     * @return 是否授权
     */
    boolean isAuthorized(String institutionCode);

    /**
     * 机构登录
     */
    String getToken(InstitutionDTO dto) throws Exception;
}
