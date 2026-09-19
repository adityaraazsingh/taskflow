package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.service.TenantService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/tenant")
@RestController
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String createTenant(@RequestParam String tenantName) {
        return tenantService.createTenant(tenantName);
    }
}
