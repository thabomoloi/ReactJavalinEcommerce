package com.oasisnourish;

import static io.javalin.apibuilder.ApiBuilder.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oasisnourish.config.EnvConfig;
import com.oasisnourish.config.TemplateEngineConfig;
import com.oasisnourish.controllers.*;
import com.oasisnourish.dao.*;
import com.oasisnourish.dao.impl.*;
import com.oasisnourish.db.*;
import com.oasisnourish.db.impl.*;
import com.oasisnourish.enums.Role;
import com.oasisnourish.exceptions.*;
import com.oasisnourish.seeds.*;
import com.oasisnourish.services.*;
import com.oasisnourish.services.impl.*;
import com.oasisnourish.util.EmailContentBuilder;
import com.oasisnourish.util.RoleValidator;
import com.oasisnourish.util.SessionManager;

import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    // Load env variables
    Dotenv dotenv = EnvConfig.getDotenv();
    String env = dotenv.get("ENV", "development");

    // Util
    EmailContentBuilder emailContentBuilder = new EmailContentBuilder();
    RoleValidator roleValidator = new RoleValidator();
    SessionManager sessionManager = new SessionManager();

    // Database connections
    JdbcConnection jdbcConnection = new JdbcConnectionImpl();
    RedisConnection redisConnection = new RedisConnectionImpl();

    // DAOs
    UserDao userDao = new UserDaoImpl(jdbcConnection);

    // Serivces
    UserService userService = new UserServiceImpl(userDao);
    TokenService tokenService = new TokenServiceImpl(redisConnection);
    EmailService emailService = new EmailServiceImpl(TemplateEngineConfig.getTemplateEngine());
    AuthService authService = new AuthServiceImpl(userService, emailService, tokenService, emailContentBuilder);
    JWTService jwtService = new JWTServiceImpl(redisConnection);

    // Controllers
    UserController userController = new UserController(userService);
    AuthController authController = new AuthController(userService, authService, jwtService, sessionManager,
            roleValidator);

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
            path("/auth", () -> {
                post("/signup", authController::signUpUser, Role.GUEST);
                post("/signin", authController::signInUser);
                delete("/signout", authController::signOutUser);
                post("/refresh", authController::refreshToken);
            });
        });
    }

    public void before(Javalin app) {
        app.beforeMatched(authController);
        app.before(authController::decodeJWTFromCookie);
    }

    public static void main(String[] args) {
        var application = new App();

        var app = Javalin.create(config -> {
            config.router.apiBuilder(() -> application.router());
        }).start(7070);

        application.before(app);

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
