package com.oasisnourish.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.oasisnourish.dao.mappers.EntityRowMapper;
import com.oasisnourish.db.JdbcConnection;

@ExtendWith(MockitoExtension.class)
public abstract class DaoTestHelper<T> {

    @Mock
    private JdbcConnection jdbcConnection;

    @Mock
    protected Connection connection;

    @Mock
    protected PreparedStatement ps;

    @Mock
    protected ResultSet rs;

    @Mock
    protected EntityRowMapper<T> entityRowMapper;

    protected void setUp() throws SQLException {
        when(jdbcConnection.getConnection()).thenReturn(connection);
    }

    protected void mockEntityExists(String sql, T expected) throws SQLException {
        when(connection.prepareStatement(sql)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(entityRowMapper.mapToEntity(rs)).thenReturn(expected);

    }

    protected void assertEntityExists(T expected, Optional<T> result) {
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
    }

    protected void mockEntityDoesNotExist(String sql) throws SQLException {
        when(connection.prepareStatement(sql)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
    }

    protected void assertEntityDoesNotExist(Optional<T> result) {
        assertTrue(result.isEmpty());
    }

    protected void mockEntityList(String sql, List<T> expected) throws SQLException {
        when(connection.prepareStatement(sql)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenAnswer(new Answer<Boolean>() {
            private int count = 0;

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return count++ < expected.size();
            }

        });
        when(entityRowMapper.mapToEntity(rs)).thenAnswer(AdditionalAnswers.returnsElementsOf(expected));
    }

    protected void mockEntityEmpyList(String sql) throws SQLException {
        when(connection.prepareStatement(sql)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
    }

    protected void assertEntityListEquals(List<T> expected, List<T> actual) {
        assertEquals(expected, actual);
    }

    protected void mockEntitySave(String sql, int nextId) throws SQLException {
        when(connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)).thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(ps.executeUpdate()).thenReturn(1); // affected rows
        when(rs.getInt(1)).thenReturn(nextId); // generated id

    }

    protected void assertEntitySaved(int expectedId, int actualId, T entity) throws SQLException {
        verify(entityRowMapper).mapToRow(ps, entity, false);
        assertEquals(expectedId, actualId);
    }

    protected void mockEntityUpdate(String sql) throws SQLException {
        when(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1); // affected rows
    }

    protected void assertEntityUpdated(T entity) throws SQLException {
        verify(entityRowMapper).mapToRow(ps, entity, true);
        verify(ps).executeUpdate();
    }

    protected void mockEntityDelete(String sql) throws SQLException {
        when(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1); // affected rows
    }

    protected void assertEntityDeleted(int id) throws SQLException {
        verify(ps).setInt(1, id);
        verify(ps).executeUpdate();
    }
}
