package com.projectManagement.taskflow.tenant;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class MultiTenantConnectionProviderImpl
        extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String> {

    private final DataSource dataSource;

    public MultiTenantConnectionProviderImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return dataSource;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return dataSource;
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        System.out.println("TENANT RECEIVED: " + tenantIdentifier);
//        TODO: remove this manual patching so it does not disturb the flow of data
//        if(TenantContext.getTenant()!= null){
//            tenantIdentifier = TenantContext.getTenant();
//            System.out.println("Tenant Identifier takes value of "+ tenantIdentifier);
//        }
        Connection connection = super.getConnection(tenantIdentifier);
        System.out.println("BEFORE SCHEMA: " + connection.getSchema());

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET SCHEMA " + tenantIdentifier);
        }
        System.out.println("AFTER SCHEMA: " + connection.getSchema());

        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET SCHEMA PUBLIC");
        }
        super.releaseConnection(tenantIdentifier, connection);
    }
}