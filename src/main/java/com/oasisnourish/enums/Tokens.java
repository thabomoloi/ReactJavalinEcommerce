package com.oasisnourish.enums;

public class Tokens {

    public interface Type {

        String getType();
    }

    public enum Auth implements Type {
        ACCOUNT_CONFIRMATION_TOKEN, PASSWORD_RESET_TOKEN;

        @Override
        public String getType() {
            return name().toLowerCase();
        }
    }

    public enum Jwt implements Type {
        ACCESS_TOKEN, REFRESH_TOKEN;

        @Override
        public String getType() {
            return name().toLowerCase();
        }
    }

    public enum Category {
        AUTH, JWT;

        public String getCategory() {
            return name().toLowerCase();
        }
    }
}
