package com.projectManagement.taskflow.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
//import org.springframework.boot.orm.jpa.hibernate.SpringHibernatePropertiesCustomizer;
import com.projectManagement.taskflow.tenant.MultiTenantConnectionProviderImpl;
import com.projectManagement.taskflow.tenant.TenantIdentifierResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import java.util.HashMap;
import java.util.Map;

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
            System.out.println("🔥 HIBERNATE CONFIGURING MULTI-TENANCY");

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

            System.out.println("ConnectionProvider = " + connectionProvider);
            System.out.println("TenantResolver = " + tenantResolver);

            System.out.println("MULTI TENANT PROVIDER PROPERTY = "
                    + props.get("hibernate.multi_tenant_connection_provider"));

            System.out.println("TENANT RESOLVER PROPERTY = "
                    + props.get("hibernate.tenant_identifier_resolver"));
        };
    }
}