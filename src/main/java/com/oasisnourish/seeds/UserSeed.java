package com.oasisnourish.seeds;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.oasisnourish.dao.users.UserDao;
import com.oasisnourish.enums.Role;
import com.oasisnourish.models.users.User;

public class UserSeed implements DatabaseSeed {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserSeed(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void seed() {
        String[][] users = {
            {"Alice", "alice@test.com", "Password123!", "UNVERIFIED_USER"},
            {"Bob", "bob@test.com", "Password456!", "USER"},
            {"Charlie", "charlie@test.com", "Password789!", "ADMIN"},
            {"David", "david@test.com", "Password135!", "USER"},
            {"Emily", "emily@test.com", "Password246!", "USER"}
        };

        for (String[] userData : users) {
            String name = userData[0];
            String email = userData[1];
            String password = passwordEncoder.encode(userData[2]);
            Role role = Role.valueOf(userData[3]);

            // Check if user already exists
            if (userDao.findByEmail(email).isEmpty()) {
                User user = new User(0, name, email, password, role);
                userDao.save(user);

            }
        }
    }

}
