package com.oasisnourish;

import io.javalin.Javalin;

public class App {
    private final AppConfig CONFIG;

    public App(AppConfig config) {
        CONFIG = config;
    }

    public void start() {

        var app = Javalin.create(config -> {
            config.router.apiBuilder(() -> new AppRouter(CONFIG).initializeRoutes());
            // Serve files from 'src/main/resources/public'
            config.staticFiles.add("/public");
            // wait 5 seconds for existing requests to finish
            config.jetty.modifyServer(server -> server.setStopTimeout(5_000));
        }).start(7070);

        configureMiddleware(app);
        new AppErrorHandler().configureErrorHandling(app);
    }

    private void configureMiddleware(Javalin app) {
        app.beforeMatched(CONFIG.AUTH_CONTROLLER);
        app.before(CONFIG.AUTH_CONTROLLER::decodeJWTFromCookie);
        app.after(CONFIG.AUTH_CONTROLLER::updateSessionUserIfChanged);
    }

    public static void main(String[] args) {
        AppConfig config = new AppConfig();
        var app = new App(config);
        app.start();
    }
}