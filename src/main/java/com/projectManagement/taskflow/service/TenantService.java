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
import java.util.regex.Pattern;

@Service
public class TenantService {

    /**
     * Tenant names become SQL schema names and are interpolated into DDL, so only allow plain
     * identifiers: a letter followed by letters, digits or underscores (max 63 chars).
     */
    private static final Pattern VALID_TENANT_NAME = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{0,62}$");

    private final DataSource sharedDataSource;

    @Autowired
    public TenantService(DataSource sharedDataSource) {
        this.sharedDataSource = sharedDataSource;
    }

    public static boolean isValidTenantName(String tenantName) {
        return tenantName != null && VALID_TENANT_NAME.matcher(tenantName).matches();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String createTenant(@RequestParam String tenantName) {
        if (!isValidTenantName(tenantName)) {
            throw new IllegalArgumentException(
                    "Invalid tenant name '" + tenantName + "': use a letter followed by letters, digits or underscores");
        }
        try (Connection conn = sharedDataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Create schema dynamically
            stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + tenantName);

            // 2. Run Liquibase for this schema
            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setDataSource(sharedDataSource);
            liquibase.setChangeLog("classpath:db.changelog-master.xml");
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
