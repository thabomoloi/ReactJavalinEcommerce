package com.oasisnourish.seeds;

import java.time.LocalDateTime;

import com.oasisnourish.dao.UserDao;
import com.oasisnourish.enums.Role;
import com.oasisnourish.models.User;
import com.oasisnourish.util.PasswordUtil;

public class UserSeed implements DatabaseSeed {

    private final UserDao userDao;

    public UserSeed(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void seed() {
        String[][] users = {
                { "Alice", "alice@test.com", "Password123!", "UNVERIFIED_USER" },
                { "Bob", "bob@test.com", "Password456!", "USER" },
                { "Charlie", "charlie@test.com", "Password789!", "ADMIN" },
                { "David", "david@test.com", "Password135!", "USER" },
                { "Emily", "emily@test.com", "Password246!", "USER" }
        };

        for (String[] userData : users) {
            String name = userData[0];
            String email = userData[1];
            String password = PasswordUtil.hashPassword(userData[2]);
            Role role = Role.valueOf(userData[3]);

            // Check if user already exists
            if (userDao.findByEmail(email).isEmpty()) {
                User user = new User(name, email, password, role);
                if (user.getRole() != Role.ANYONE || user.getRole() != Role.UNVERIFIED_USER) {
                    user.setEmailVerified(LocalDateTime.now());
                }
                userDao.save(user);

            }
        }
    }

}
