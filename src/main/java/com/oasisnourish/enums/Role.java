package com.oasisnourish.enums;

import io.javalin.security.RouteRole;

public enum Role implements RouteRole {
    GUEST, UNVERIFIED_USER, USER, ADMIN
}