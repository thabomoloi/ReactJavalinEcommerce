package com.oasisnourish.dao;

import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mockito.AdditionalAnswers;

import com.oasisnourish.models.User;

public class UserMockResultHelper {
    protected ResultSet mockResultSet;

    protected void mockUserResultSet(User... users) throws SQLException {
        mockUserResultSet(Arrays.asList(users));
    }

    protected void mockUserResultSet(List<User> users) throws SQLException {

        List<Integer> ids = new ArrayList<>(users.size());
        List<String> names = new ArrayList<>(users.size());
        List<String> emails = new ArrayList<>(users.size());
        List<String> passwords = new ArrayList<>(users.size());
        List<String> roles = new ArrayList<>(users.size());
        List<Timestamp> emailVerified = new ArrayList<>(users.size());

        for (User user : users) {
            ids.add(user.getId());
            names.add(user.getName());
            emails.add(user.getEmail());
            passwords.add(user.getPassword());
            roles.add(user.getRole().name());
            emailVerified.add(user.getEmailVerified() == null ? null : Timestamp.valueOf(user.getEmailVerified()));
        }

        when(mockResultSet.getInt("id")).thenAnswer(AdditionalAnswers.returnsElementsOf(ids));
        when(mockResultSet.getString("name")).thenAnswer(AdditionalAnswers.returnsElementsOf(names));
        when(mockResultSet.getString("email")).thenAnswer(AdditionalAnswers.returnsElementsOf(emails));
        when(mockResultSet.getString("password")).thenAnswer(AdditionalAnswers.returnsElementsOf(passwords));
        when(mockResultSet.getString("role")).thenAnswer(AdditionalAnswers.returnsElementsOf(roles));
        when(mockResultSet.getTimestamp("email_verified"))
                .thenAnswer(AdditionalAnswers.returnsElementsOf(emailVerified));

    }

}
