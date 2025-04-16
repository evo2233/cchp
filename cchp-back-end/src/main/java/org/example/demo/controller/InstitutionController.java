package org.example.demo.controller;

import org.example.demo.service.InstitutionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/institution")
public class InstitutionController {
    private InstitutionService institutionService;

}
