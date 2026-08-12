package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.tenant.TenantContext;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@Service
public class TenantService {

    private final DataSource sharedDataSource;

    @Autowired
    public TenantService(DataSource sharedDataSource) {
        this.sharedDataSource = sharedDataSource;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
            liquibase.setLiquibaseSchema(tenantName); // 🔥 Create seprate Dbchanelog sum , to avoid CheckSums Issue
            liquibase.afterPropertiesSet();
            TenantContext.setTenant(tenantName);
            return "Tenant schema created: " + tenantName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error creating tenant schema: " + e.getMessage();
        }
    }

}
