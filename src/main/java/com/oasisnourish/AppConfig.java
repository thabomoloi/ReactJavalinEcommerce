package com.oasisnourish;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.oasisnourish.config.AuthTokenConfig;
import com.oasisnourish.config.EmailConfig;
import com.oasisnourish.config.EnvConfig;
import com.oasisnourish.config.JWTConfig;
import com.oasisnourish.config.TemplateEngineConfig;
import com.oasisnourish.controllers.AuthController;
import com.oasisnourish.controllers.UserController;
import com.oasisnourish.dao.impl.tokens.TokenDaoImpl;
import com.oasisnourish.dao.impl.tokens.TokenRateLimitDaoImpl;
import com.oasisnourish.dao.impl.tokens.TokenVersionDaoImpl;
import com.oasisnourish.dao.impl.users.UserDaoImpl;
import com.oasisnourish.dao.mappers.users.UserRowMapper;
import com.oasisnourish.db.JdbcConnection;
import com.oasisnourish.db.RedisConnection;
import com.oasisnourish.db.impl.JdbcConnectionImpl;
import com.oasisnourish.db.impl.RedisConnectionImpl;
import com.oasisnourish.models.tokens.AuthToken;
import com.oasisnourish.models.tokens.JsonWebToken;
import com.oasisnourish.seeds.UserSeed;
import com.oasisnourish.services.impl.EmailServiceImpl;
import com.oasisnourish.services.impl.tokens.AuthTokenServiceImpl;
import com.oasisnourish.services.impl.tokens.JWTServiceImpl;
import com.oasisnourish.services.impl.users.AuthServiceImpl;
import com.oasisnourish.services.impl.users.UserServiceImpl;
import com.oasisnourish.util.EmailContentBuilder;
import com.oasisnourish.util.RoleValidator;
import com.oasisnourish.util.SessionManager;
import com.oasisnourish.util.jwt.JWTGenerator;
import com.oasisnourish.util.jwt.JWTProvider;

import io.github.cdimascio.dotenv.Dotenv;

public class AppConfig {

    public final ExecutorService EMAIL_EXECUTOR_SERVICE;
    public final UserController USER_CONTROLLER;
    public final AuthController AUTH_CONTROLLER;

    public AppConfig() {
        // Database Connections
        JdbcConnection jdbcConnection = new JdbcConnectionImpl();
        RedisConnection redisConnection = new RedisConnectionImpl();

        // Utilities
        Dotenv dotenv = EnvConfig.getDotenv();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        EmailContentBuilder emailContentBuilder = new EmailContentBuilder(dotenv);

        RoleValidator roleValidator = new RoleValidator();

        SessionManager sessionManager = new SessionManager(dotenv);

        // DAOs
        UserDaoImpl userDao = new UserDaoImpl(jdbcConnection, new UserRowMapper());
        TokenVersionDaoImpl tokenVersionDao = new TokenVersionDaoImpl(redisConnection);

        // Services
        EMAIL_EXECUTOR_SERVICE = Executors.newFixedThreadPool(10);

        UserServiceImpl userService = new UserServiceImpl(userDao, passwordEncoder);

        EmailServiceImpl emailService = new EmailServiceImpl(
                TemplateEngineConfig.getTemplateEngine(),
                new EmailConfig(dotenv),
                EMAIL_EXECUTOR_SERVICE);

        JWTServiceImpl jwtService = new JWTServiceImpl(
                new TokenDaoImpl<>(redisConnection, JsonWebToken.class),
                tokenVersionDao,
                new JWTProvider(new JWTGenerator(), new JWTConfig(dotenv)));

        AuthTokenServiceImpl authTokenService = new AuthTokenServiceImpl(
                new TokenDaoImpl<>(redisConnection, AuthToken.class),
                tokenVersionDao,
                new TokenRateLimitDaoImpl(redisConnection),
                new AuthTokenConfig(dotenv));

        AuthServiceImpl authService = new AuthServiceImpl(
                userService,
                emailService,
                authTokenService,
                jwtService,
                passwordEncoder,
                emailContentBuilder);

        // Controllers
        USER_CONTROLLER = new UserController(userService);

        AUTH_CONTROLLER = new AuthController(
                userService,
                authService,
                jwtService,
                sessionManager,
                roleValidator);

        // Seed data for development environment
        if ("development".equals(dotenv.get("ENV", "development"))) {
            new UserSeed(userDao, passwordEncoder).seed();
        }
    }
}
