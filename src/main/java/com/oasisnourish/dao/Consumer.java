package com.oasisnourish.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Consumer {

    @FunctionalInterface
    public interface PreparedStatementConsumer {
        void accept(PreparedStatement ps) throws SQLException;
    }

    @FunctionalInterface
    public interface ResultSetConsumer {
        void accept(ResultSet rs) throws SQLException;
    }

}