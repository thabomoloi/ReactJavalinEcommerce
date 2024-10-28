package com.oasisnourish.config;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Configuration class for setting up the Thymeleaf template engine.
 * This class provides a static method to create and configure a
 * Thymeleaf {@link TemplateEngine} instance with a specific template resolver.
 */
public class TemplateEngineConfig {

    /**
     * Creates and configures a {@link TemplateEngine} instance.
     *
     * @return a configured {@link TemplateEngine} instance ready for use.
     *         The template engine will use a {@link ClassLoaderTemplateResolver}
     *         that looks for templates in the "templates/" directory with a ".html"
     *         suffix.
     *         <p>
     *         The template mode is set to "HTML" and the character encoding is set
     *         to "UTF-8".
     *         </p>
     */
    public static TemplateEngine getTemplateEngine() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        return templateEngine;
    }
}
