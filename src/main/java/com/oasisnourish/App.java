package com.oasisnourish;

import io.javalin.Javalin;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        Javalin.create(/* config */)
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);
    }
}
