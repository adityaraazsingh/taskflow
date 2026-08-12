package com.projectManagement.taskflow.controller;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/tenant")
@RestController
public class TenantController {

    private final DataSource sharedDataSource;

    @Autowired
    public TenantController(DataSource sharedDataSource) {
        this.sharedDataSource = sharedDataSource;
    }

    @PostMapping
    public String createTenant(@RequestParam String tenantName) {
        try (Connection conn = sharedDataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Create schema dynamically
            stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + tenantName);

            // 2. Run Liquibase for this schema
            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setDataSource(sharedDataSource);
            liquibase.setChangeLog("classpath:dbchangelog.h2.sql");
            Map<String, String> params = new HashMap<>();
            params.put("schemaName", tenantName);
            liquibase.setChangeLogParameters(params);
            liquibase.setLiquibaseSchema(tenantName); // 🔥 Create sepearte Dbchanelog sum , to avoid CheckSums Issue
            liquibase.afterPropertiesSet();

            return "Tenant schema created: " + tenantName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error creating tenant schema: " + e.getMessage();
        }
    }
}
