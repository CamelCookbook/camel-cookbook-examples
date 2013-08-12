package org.camelcookbook.examples.transactions.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Utility class for accessing the messages table used in the database examples.
 */
public class MessageDao {

    private final JdbcTemplate jdbcTemplate;

    public MessageDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int getMessageCount(String message) {
        return jdbcTemplate.queryForInt("select count(*) from messages where message = ?", message);
    }
}
