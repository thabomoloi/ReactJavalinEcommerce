package com.oasisnourish.dao.mappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface EntityRowMapper<T> {
    /**
     * Maps a {@link ResultSet} to a {@link T} object.
     *
     * @param resultSet the {@link ResultSet} containing entity data.
     * @return a {@link T} object populated with data from the {@link ResultSet}.
     * @throws SQLException if there is an error accessing the {@link ResultSet}.
     */
    T mapToEntity(ResultSet resultSet) throws SQLException;

    /**
     * Maps a {@link T} object to a {@link PreparedStatement} for database
     * operations.
     *
     * @param statement the {@link PreparedStatement} to populate with entity data.
     * @param entity    the {@link T} object containing data to set in the
     *                  {@link PreparedStatement}.
     * @param includeId whether to include the entity ID in the
     *                  {@link PreparedStatement}. If true, the entity ID will be
     *                  included for update operations; otherwise, it is used for
     *                  insert operations.
     * @throws SQLException if there is an error setting values in the
     *                      {@link PreparedStatement}.
     */
    void mapToRow(PreparedStatement preparedStatement, T entity, boolean includeId) throws SQLException;
}
