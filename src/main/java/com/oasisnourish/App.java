package com.oasisnourish;

import static io.javalin.apibuilder.ApiBuilder.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oasisnourish.config.EnvConfig;
import com.oasisnourish.controllers.*;
import com.oasisnourish.dao.*;
import com.oasisnourish.dao.impl.*;
import com.oasisnourish.db.*;
import com.oasisnourish.db.impl.*;
import com.oasisnourish.exceptions.*;
import com.oasisnourish.seeds.*;
import com.oasisnourish.services.*;
import com.oasisnourish.services.impl.*;

import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;

/**
 * Hello world!
 *
 */
public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    // Load env variables
    Dotenv dotenv = EnvConfig.getDotenv();
    String env = dotenv.get("ENV", "development");

    // DAOs and DB connections
    JdbcConnection jdbcConnection = new JdbcConnectionImpl();
    UserDao userDao = new UserDaoImpl(jdbcConnection);

    // Serivces
    UserService userService = new UserServiceImpl(userDao);

    // Controllers
    UserController userController = new UserController(userService);

    public App() {
        if ("development".equals(env)) {
            DatabaseSeed userSeed = new UserSeed(userDao);
            userSeed.seed();
        }
    }

    public void router() {
        path("/api", () -> {
            path("/users", () -> {
                get(userController::findAllUsers);
                post(userController::createUser);
                path("/{userId}", () -> {
                    get(userController::findUserById);
                    patch(userController::updateUser);
                    delete(userController::deleteUser);
                });
            });
        });
    }

    public static void main(String[] args) {
        var application = new App();

        var app = Javalin.create(config -> {
            config.router.apiBuilder(() -> application.router());
        }).start(7070);

        app.exception(EmailExistsException.class, (e, ctx) -> {
            LOGGER.error(e.getMessage(), e);
            throw new BadRequestResponse(e.getMessage());
        });

        app.exception(NotFoundException.class, (e, ctx) -> {
            LOGGER.error(e.getMessage(), e);
            throw new NotFoundResponse(e.getMessage());
        });

        app.exception(Exception.class, (e, ctx) -> {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorResponse(
                    "We're currently experiencing issues with our server. Please try again later.");
        });
    }
}
