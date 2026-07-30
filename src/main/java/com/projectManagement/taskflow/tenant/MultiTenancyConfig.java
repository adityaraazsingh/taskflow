package com.projectManagement.taskflow.tenant;

import com.projectManagement.taskflow.tenant.SchemaMultiTenantConnectionProvider;
import com.projectManagement.taskflow.tenant.TenantIdentifierResolver;
import org.hibernate.cfg.Environment;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MultiTenancyConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(DataSource dataSource) {
        SchemaMultiTenantConnectionProvider connectionProvider =
                new SchemaMultiTenantConnectionProvider(dataSource);
        TenantIdentifierResolver identifierResolver = new TenantIdentifierResolver();

        return hibernateProperties -> {
            hibernateProperties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
            hibernateProperties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, identifierResolver);
        };
    }
}