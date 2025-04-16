package org.example.demo.controller;


import org.example.demo.model.CommonResponse;
import org.example.demo.model.dto.AdminLoginDTO;
import org.example.demo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) { this.adminService = adminService; }
    
    @PostMapping("/login")
    public ResponseEntity<CommonResponse> login(@RequestBody AdminLoginDTO admindto) {
        try {
            String address = adminService.login(admindto.getUsername(), admindto.getPassword());
            return ResponseEntity.ok(CommonResponse.ok("address："+address));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CommonResponse.fail("400", e));
        }
    }
} 