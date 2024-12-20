package com.oasisnourish.services.impl;

import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;

import com.oasisnourish.config.EmailConfig;
import com.oasisnourish.services.EmailService;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Implementation of the {@link EmailService} interface for sending emails.
 * Utilizes JavaMail for email transmission and Thymeleaf for HTML content
 * rendering.
 */
public class EmailServiceImpl implements EmailService {

    private final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final TemplateEngine templateEngine;
    private final EmailConfig emailConfig;
    private final ExecutorService executorService;

    /**
     * Constructs an instance of {@link EmailServiceImplTest} with the specified
     * Thymeleaf {@link TemplateEngine}.
     *
     * @param templateEngine the {@link TemplateEngine} used for rendering email
     * content.
     * @throws IllegalStateException if the mail environment variables are not
     * set.
     */
    public EmailServiceImpl(TemplateEngine templateEngine, EmailConfig emailConfig, ExecutorService executorService) {
        this.templateEngine = templateEngine;
        this.executorService = executorService;
        this.emailConfig = emailConfig;
    }

    /**
     * Sends an email to a specified recipient using a provided subject and
     * Thymeleaf template.
     *
     * @param to the email address of the recipient.
     * @param subject the subject of the email.
     * @param templateName the name of the Thymeleaf template to be used for the
     * email content.
     * @param context the context containing variables for the Thymeleaf
     * template.
     * @throws MessagingException if there is an error sending the email.
     */
    @Override
    public void sendEmail(String to, String subject, String templateName, IContext context) {
        executorService.submit(() -> {
            // Set up mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props);

            try {
                // Create a MimeMessage
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(emailConfig.getMailUsername()));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                message.setSubject(subject);

                // Process the Thymeleaf template
                String htmlContent = templateEngine.process(templateName, context);

                // Set the content of the email
                message.setContent(htmlContent, "text/html");

                // Send the message
                Transport.send(message, emailConfig.getMailUsername(), emailConfig.getMailPassword());

            } catch (MessagingException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
    }
}
