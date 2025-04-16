package org.example.demo.service;

public interface AdminService {
    String login(String username, String password) throws Exception;

    void initializeAdmin(String address);
}
