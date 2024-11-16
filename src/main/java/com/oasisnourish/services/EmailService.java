package com.oasisnourish.services;

import org.thymeleaf.context.IContext;

/**
 * Interface for email services responsible for sending emails.
 */
public interface EmailService {

    /**
     * Sends an email to the specified recipient using the provided subject,
     * template name, and context for rendering the email content.
     *
     * @param to           The email address of the recipient.
     * @param subject      The subject of the email.
     * @param templateName The name of the email template to use for rendering the
     *                     content.
     * @param context      The context containing variables for the email template.
     */
    void sendEmail(String to, String subject, String templateName, IContext context);

}
