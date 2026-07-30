package com.projectManagement.taskflow.tenant;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class TenantProvisioningService {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public TenantProvisioningService(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void provisionTenant(String tenantId) {
        validateTenantId(tenantId); // see note below - important

        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + tenantId);

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(tenantId)
                .locations("classpath:db/migration/tenant")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
    }

    private void validateTenantId(String tenantId) {
        // tenantId ends up concatenated into DDL/schema-switching SQL, so
        // whitelist it strictly to prevent SQL injection via a crafted tenant name
        if (!tenantId.matches("[a-zA-Z0-9_]{1,50}")) {
            throw new IllegalArgumentException("Invalid tenant identifier: " + tenantId);
        }
    }
}