package org.camelcookbook.examples.transactions.utils;

import javax.sql.DataSource;
import org.apache.commons.lang.Validate;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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

        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        // here we use the same classes that Spring does under the covers to run the schema into the database
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new FileSystemResource(file));
        DatabasePopulatorUtils.execute(populator, dataSource);

        return dataSource;
    }

    private static class FileLocator {
        private List<String> locations = new LinkedList<String>();
        private boolean used = false;
        private StringBuilder errorMessage = new StringBuilder();

        public FileLocator() {}

        public FileLocator with(String location) {
            Validate.isTrue(!used);
            locations.add(location);
            return this;
        }

        public File find() {
            used = true; // this builder can't be used again
            File foundFile = null;
            for (String location : locations) {
                File file = new File(location);
                String path;
                try {
                    path = file.getCanonicalPath();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (file.exists()) {
                    foundFile = file;
                    break;
                } else {
                    if (errorMessage.length() == 0) {
                        errorMessage.append("File not found in ");
                    } else {
                        errorMessage.append(" or in ");
                    }
                    errorMessage.append("'" + path + "'");
                }
            }
            return foundFile;
        }

        public String getErrorMessage() {
            Validate.isTrue(used, "The find() method hasn't been called on this builder");
            return errorMessage.toString();
        }
    }

    private EmbeddedDataSourceFactory() {}
}
