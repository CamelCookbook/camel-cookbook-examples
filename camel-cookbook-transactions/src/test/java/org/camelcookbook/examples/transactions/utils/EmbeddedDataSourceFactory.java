package org.camelcookbook.examples.transactions.utils;

import javax.sql.DataSource;
import org.apache.commons.lang.Validate;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Utility class that centralises the wiring of a DataSource to the embedded database.
 */
public class EmbeddedDataSourceFactory {
    private static Logger LOG = LoggerFactory.getLogger(EmbeddedDataSourceFactory.class);

    public static DataSource getDataSource(String initScriptLocation) {
        Validate.notEmpty(initScriptLocation, "initScriptLocation is empty");

        String mavenRelativePath = "src/main/resources/" + initScriptLocation;
        String mavenRootRelativePath = "camel-cookbook-transactions/" + mavenRelativePath;

        StringBuilder jdbcUrl = new StringBuilder("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        StringBuilder errorMessage = new StringBuilder();

        // check that we can load the init script, and if so append its location to the jdbcUrl
        if (!(appendExisting(initScriptLocation, jdbcUrl, errorMessage) ||
                appendExisting(mavenRelativePath, jdbcUrl, errorMessage) ||
                appendExisting(mavenRootRelativePath, jdbcUrl, errorMessage))) {
            throw new IllegalArgumentException(errorMessage.toString());
        }

        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(jdbcUrl.toString());
        dataSource.setUser("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    private static boolean appendExisting(String location, StringBuilder jdbcUrl, StringBuilder errorMessage) {
        File file = new File(location);
        String path;
        try {
            path = file.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (file.exists()) {
            LOG.info("Init script found in {}", path);
            jdbcUrl.append(";INIT=RUNSCRIPT FROM '" + path + "'");
            return true;
        } else {
            if (errorMessage.length() == 0) {
                errorMessage.append("File not found in ");
            } else {
                errorMessage.append(" or in ");
            }
            errorMessage.append("'" + path + "'");
            return false;
        }
    }

    private EmbeddedDataSourceFactory() {}
}
