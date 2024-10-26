package com.oasisnourish.enums;

import io.javalin.security.RouteRole;

public enum Role implements RouteRole {
    ANYONE, UNVERIFIED_USER, USER, ADMIN
}