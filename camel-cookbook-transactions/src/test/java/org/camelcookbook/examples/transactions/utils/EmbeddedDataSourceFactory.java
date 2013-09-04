package org.camelcookbook.examples.transactions.utils;

import javax.sql.DataSource;
import org.apache.commons.lang.Validate;
import org.camelcookbook.examples.transactions.util.DataSourceInitializer;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

/**
 * Utility class that centralises the wiring of a DataSource to the embedded database.
 */
public class EmbeddedDataSourceFactory {

    public static DataSource getDataSource(String initScriptLocation) {
        Validate.notEmpty(initScriptLocation, "initScriptLocation is empty");

        String mavenRelativePath = "src/main/resources/" + initScriptLocation;
        String mavenRootRelativePath = "camel-cookbook-transactions/" + mavenRelativePath;

        // check that we can load the init script
        FileLocator locator = new FileLocator().with(initScriptLocation).with(mavenRelativePath).with(mavenRootRelativePath);
        File file = locator.find();
        Validate.notNull(file, locator.getErrorMessage());
        FileSystemResource script = new FileSystemResource(file);

        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        return DataSourceInitializer.initializeDataSource(dataSource, script);
    }

    private EmbeddedDataSourceFactory() {}
}
