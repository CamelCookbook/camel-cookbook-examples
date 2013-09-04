package org.camelcookbook.examples.transactions.util;

import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Uitility class for initializing a DataSource with a SQL script.
 */
public class DataSourceInitializer {
    public static DataSource initializeDataSource(DataSource dataSource, Resource script) {
        // here we use the same classes that Spring does under the covers to run the schema into the database
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(script);
        DatabasePopulatorUtils.execute(populator, dataSource);

        return dataSource;
    }
}
