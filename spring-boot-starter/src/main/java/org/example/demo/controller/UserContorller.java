package org.example.demo.controller;

import org.example.demo.model.UserTable;
import org.example.demo.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserContorller {

    private UserService userService = new UserService();

    @RequestMapping("/list")
    public List<UserTable> list() {
        return userService.getUserTables();
    }

}
