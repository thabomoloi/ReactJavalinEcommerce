package com.oasisnourish;

import com.oasisnourish.controllers.UserController;
import com.oasisnourish.dao.UserDao;
import com.oasisnourish.dao.impl.UserDaoImpl;
import com.oasisnourish.db.JdbcConnection;
import com.oasisnourish.db.impl.JdbcConnectionImpl;
import com.oasisnourish.seeds.DatabaseSeed;
import com.oasisnourish.seeds.UserSeed;
import com.oasisnourish.services.UserService;
import com.oasisnourish.services.impl.UserServiceImpl;

import io.javalin.Javalin;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        var app = Javalin.create(/* config */).start(7070);

        try {
            JdbcConnection jdbcConnection = new JdbcConnectionImpl();

            // DAOs
            UserDao userDao = new UserDaoImpl(jdbcConnection);

            // Serivces
            UserService userService = new UserServiceImpl(userDao);

            // Seeds
            DatabaseSeed userSeed = new UserSeed(userDao);
            userSeed.seed();

            // Controllers
            UserController userController = new UserController(userService);
            userController.registerRoutes(app);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
