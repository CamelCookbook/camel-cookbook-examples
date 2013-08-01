package org.camelcookbook.examples.transactions.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Utility class for accessing the audit_log table used in the database examples.
 */
public class AuditLogDao {

    private final JdbcTemplate jdbcTemplate;

    public AuditLogDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int getAuditCount(String message) {
        return jdbcTemplate.queryForInt("select count(*) from audit_log where message = ?", message);
    }
}
