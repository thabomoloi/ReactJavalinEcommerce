package com.oasisnourish.services.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thymeleaf.TemplateEngine;

import io.github.cdimascio.dotenv.Dotenv;

public class EmailServiceImplTest {
    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private ExecutorService executorService;

    @Mock
    private Dotenv dotenv;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(dotenv.get("MAIL_USERNAME")).thenReturn("username@gmail.com");
        when(dotenv.get("MAIL_PASSWORD")).thenReturn("password");
    }

    @Test
    void testEnvironmentVariablesInitialization() {
        assertDoesNotThrow(() -> new EmailServiceImpl(templateEngine, executorService, dotenv));
    }

    @Test
    void testEnvironmentVariablesMissing() {
        when(dotenv.get("MAIL_USERNAME")).thenReturn(null);
        when(dotenv.get("MAIL_PASSWORD")).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> new EmailServiceImpl(templateEngine, executorService, dotenv));
    }
}
