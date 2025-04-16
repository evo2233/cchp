package org.example.demo.service.impl;

import org.example.demo.mapper.AdminMapper;
import org.example.demo.model.entity.Admin;
import org.example.demo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    private static String admin_name = "admin1";
    private static String admin_password = "123";

    @Autowired
    private AdminMapper adminMapper;
    
    @Override
    public String login(String username, String password) throws Exception {

        Admin admin = adminMapper.getAdmin(username, password);

        if (admin != null) {
            return admin.getAddress();
        } else {
            throw new Exception("Error: Invalid username or password");
        }
    }

    @Override
    public void initializeAdmin(String address) {
        // 检查是否已存在管理员
        Admin existingAdmin = adminMapper.getAdmin(admin_name, admin_password);
        if (existingAdmin == null) {
            // 创建默认管理员
            Admin admin = new Admin();
            admin.setUsername(admin_name);
            admin.setPassword(admin_password);
            admin.setAddress(address);
            adminMapper.addAdmin(admin);
        }
    }
} 