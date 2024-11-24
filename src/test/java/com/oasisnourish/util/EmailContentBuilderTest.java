package com.oasisnourish.util;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.IContext;

import com.oasisnourish.enums.Role;
import com.oasisnourish.enums.Tokens;
import com.oasisnourish.models.tokens.AuthToken;
import com.oasisnourish.models.tokens.Token;
import com.oasisnourish.models.users.User;

import io.github.cdimascio.dotenv.Dotenv;

@ExtendWith(MockitoExtension.class)
public class EmailContentBuilderTest {

    @Mock
    private Dotenv dotenv;

    @InjectMocks
    private EmailContentBuilder emailContentBuilder;

    @Test
    public void testBuildEmailTokenContext() {
        User user = new User(1, "John Doe", "john.doe@test.com", "encodedPassword", Role.ADMIN);
        Token token = new AuthToken("dummy-token", Tokens.Auth.ACCOUNT_CONFIRMATION_TOKEN, 1, Instant.now(), user.getId());

        when(dotenv.get("BASE_URL", "http://localhost:7070")).thenReturn("http://testurl.com");

        IContext context = emailContentBuilder.buildEmailTokenContext(user, token);

        assertNotNull(context);
        assertEquals(user, ((User) context.getVariable("user")));
        assertEquals(token, ((Token) context.getVariable("token")));
        assertEquals("http://testurl.com", context.getVariable("baseUrl"));
        assertNotNull(context.getVariable("timeFormatter"));
        assertNotNull(context.getVariable("authTokenConfig"));
    }
}
