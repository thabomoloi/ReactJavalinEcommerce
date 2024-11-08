package com.oasisnourish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.oasisnourish.exceptions.EmailExistsException;
import com.oasisnourish.exceptions.InvalidTokenException;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.exceptions.TooManyRequestsException;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.TooManyRequestsResponse;
import io.javalin.http.UnauthorizedResponse;

public class AppErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppErrorHandler.class);

    public void configureErrorHandling(Javalin app) {
        app.exception(TooManyRequestsException.class, (e, ctx) -> {
            LOGGER.error("Too many requests: {}, IP: {}, Endpoint: {}", e.getMessage(), ctx.ip(), ctx.fullUrl(), e);
            throw new TooManyRequestsResponse(e.getMessage());
        });

        app.exception(InvalidTokenException.class, (e, ctx) -> {
            LOGGER.error("Invalid token: {}, IP: {}, Endpoint: {}", e.getMessage(), ctx.ip(), ctx.fullUrl(), e);
            throw new UnauthorizedResponse(e.getMessage());
        });

        app.exception(EmailExistsException.class, (e, ctx) -> {
            LOGGER.error("Email exists conflict: {}, Email: {}, IP: {}, Endpoint: {}", e.getMessage(),
                    ctx.formParam("email"), ctx.ip(), ctx.fullUrl(), e);
            throw new BadRequestResponse(e.getMessage());
        });

        app.exception(NotFoundException.class, (e, ctx) -> {
            LOGGER.error("Resource not found: {}, IP: {}, Endpoint: {}", e.getMessage(), ctx.ip(), ctx.fullUrl(), e);
            throw new NotFoundResponse(e.getMessage());
        });

        app.exception(Exception.class, (e, ctx) -> {
            LOGGER.error("Unexpected error: {}, IP: {}, Endpoint: {}", e.getMessage(), ctx.ip(), ctx.fullUrl(), e);
            throw new InternalServerErrorResponse("An unexpected error occurred. Please try again later.");
        });
    }
}
