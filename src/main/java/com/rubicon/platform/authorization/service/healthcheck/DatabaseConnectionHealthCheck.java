package com.rubicon.platform.authorization.service.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.rubicon.platform.authorization.metrics.HealthCheckRegistryHolder;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnectionHealthCheck extends HealthCheck
{
    private HealthCheckRegistryHolder healthCheckRegistryHolder;
    private ComboPooledDataSource dataSourceWrapped;

    public DatabaseConnectionHealthCheck(
            HealthCheckRegistryHolder healthCheckRegistryHolder,
            ComboPooledDataSource dataSourceWrapped)
    {
        this.healthCheckRegistryHolder = healthCheckRegistryHolder;
        this.dataSourceWrapped = dataSourceWrapped;
    }

    @Override
    protected Result check() throws Exception
    {
        int results = 0;

        // Connect to the Database, allowing the Framework to handle the errors
        Connection connection = DriverManager
                .getConnection(dataSourceWrapped.getJdbcUrl(), dataSourceWrapped.getUser(),
                        dataSourceWrapped.getPassword());
        Statement statement = connection.createStatement();

        // execute the query, and get a java resultset
        ResultSet resultSet = statement.executeQuery("SELECT 1");

        while (resultSet.next())
        {
            results++;
            break;
        }

        if (results == 1)
        {
            return HealthCheck.Result.healthy();
        }
        else
        {
            // if a checked exception is thrown, this message is not used
            // instead the actual error message is used, along with a stack trace, which is more informative
            return HealthCheck.Result.unhealthy("Unable to successfully run a query");
        }
    }

    @PostConstruct
    public void addToRegistry()
    {
        healthCheckRegistryHolder.getInstance().getRegistry().register(this.getClass().getSimpleName(), this);
    }
}
