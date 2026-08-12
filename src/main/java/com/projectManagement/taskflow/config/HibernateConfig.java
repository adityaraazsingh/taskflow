package com.projectManagement.taskflow.config;

import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import com.projectManagement.taskflow.tenant.MultiTenantConnectionProviderImpl;
import com.projectManagement.taskflow.tenant.TenantIdentifierResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    private final MultiTenantConnectionProviderImpl connectionProvider;
    private final TenantIdentifierResolver tenantResolver;

    public HibernateConfig(
            MultiTenantConnectionProviderImpl connectionProvider,
            TenantIdentifierResolver tenantResolver) {

        this.connectionProvider = connectionProvider;
        this.tenantResolver = tenantResolver;
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return props -> {
            props.put(
                    "hibernate.multiTenancy" ,"SCHEMA"
            );
            props.put(
                    "hibernate.multi_tenant_connection_provider",
                    connectionProvider
            );
            props.put(
                    "hibernate.tenant_identifier_resolver",
                    tenantResolver
            );
        };
    }
}