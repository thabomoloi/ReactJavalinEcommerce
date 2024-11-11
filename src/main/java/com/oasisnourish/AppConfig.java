package com.oasisnourish;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.oasisnourish.config.*;
import com.oasisnourish.controllers.*;
import com.oasisnourish.dao.*;
import com.oasisnourish.dao.impl.*;
import com.oasisnourish.dao.mappers.*;
import com.oasisnourish.db.*;
import com.oasisnourish.db.impl.*;
import com.oasisnourish.seeds.UserSeed;
import com.oasisnourish.services.*;
import com.oasisnourish.services.impl.*;
import com.oasisnourish.util.*;

import io.github.cdimascio.dotenv.Dotenv;

public class AppConfig {
    // // Utilities
    // public final PasswordEncoder PASSWORD_ENCODER;
    // public final EmailContentBuilder EMAIL_CONTENT_BUILDER;
    // public final RoleValidator ROLE_VALIDATOR;
    // public final SessionManager SESSION_MANAGER;
    // public final Dotenv DOTENV;

    // // DAOs
    // public final UserDao USER_DAO;
    // // Services
    // public final AuthService AUTH_SERVICE;
    // public final JWTService JWT_SERVICE;
    // public final EmailService EMAIL_SERVICE;
    // public final UserService USER_SERVICE;
    // public final TokenService TOKEN_SERVICE;
    // public final ExecutorService EMAIL_EXECUTOR_SERVICE;
    // // Contorllers
    // public final UserController USER_CONTROLLER;
    // public final AuthController AUTH_CONTROLLER;
    // public AppConfig() {
    //     // Database Connections
    //     JdbcConnection jdbcConnection = new JdbcConnectionImpl();
    //     RedisConnection redisConnection = new RedisConnectionImpl();
    //     // Utilities
    //     PASSWORD_ENCODER = new BCryptPasswordEncoder();
    //     EMAIL_CONTENT_BUILDER = new EmailContentBuilder();
    //     ROLE_VALIDATOR = new RoleValidator();
    //     SESSION_MANAGER = new SessionManager();
    //     DOTENV = EnvConfig.getDotenv();
    //     // DAOs
    //     USER_DAO = new UserDaoImpl(jdbcConnection, new UserRowMapper());
    //     // Services
    //     USER_SERVICE = new UserServiceImpl(USER_DAO, PASSWORD_ENCODER);
    //     TOKEN_SERVICE = new TokenServiceImpl(redisConnection);
    //     EMAIL_EXECUTOR_SERVICE = Executors.newFixedThreadPool(10);
    //     EMAIL_SERVICE = new EmailServiceImpl(TemplateEngineConfig.getTemplateEngine(), EMAIL_EXECUTOR_SERVICE, DOTENV);
    //     JWT_SERVICE = new JWTServiceImpl(redisConnection, DOTENV);
    //     AUTH_SERVICE = new AuthServiceImpl(USER_SERVICE, EMAIL_SERVICE, TOKEN_SERVICE, JWT_SERVICE, PASSWORD_ENCODER,
    //             EMAIL_CONTENT_BUILDER);
    //     // Controllers
    //     USER_CONTROLLER = new UserController(USER_SERVICE);
    //     AUTH_CONTROLLER = new AuthController(USER_SERVICE, AUTH_SERVICE, JWT_SERVICE, SESSION_MANAGER, ROLE_VALIDATOR);
    //     // Seed data for development environment
    //     if ("development".equals(DOTENV.get("ENV", "development"))) {
    //         new UserSeed(USER_DAO, PASSWORD_ENCODER).seed();
    //     }
    // }
}
