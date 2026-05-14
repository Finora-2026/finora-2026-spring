package com.bellamyphan.finora_2026_spring.postgres.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;

import java.util.Properties;

@Configuration
public class MailConfig {

    private static final Logger logger = LoggerFactory.getLogger(MailConfig.class);

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.ports}")
    private String ports; // comma-separated ports

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean starttlsEnable;

    @Value("${spring.mail.properties.mail.smtp.starttls.required}")
    private boolean starttlsRequired;

    @Value("${spring.mail.properties.mail.debug}")
    private boolean debug;

    private JavaMailSenderImpl mailSender;

    @PostConstruct
    public void init() {

        mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();

        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        props.put("mail.smtp.starttls.required", starttlsRequired);
        props.put("mail.debug", debug);

        // Default to first port immediately
        String firstPort = ports.split(",")[0].trim();
        mailSender.setPort(Integer.parseInt(firstPort));

        logger.info("Mail sender initialized with default port {}", firstPort);

        // Async background verification
        verifyConnectionsAsync();
    }

    @Async
    public void verifyConnectionsAsync() {
        for (String portStr : ports.split(",")) {
            try {
                int port = Integer.parseInt(portStr.trim());
                JavaMailSenderImpl testSender = new JavaMailSenderImpl();

                testSender.setHost(host);
                testSender.setUsername(username);
                testSender.setPassword(password);
                testSender.setPort(port);

                Properties props = testSender.getJavaMailProperties();

                props.put("mail.smtp.auth", smtpAuth);
                props.put("mail.debug", debug);

                if (port == 465) {
                    // Port 465 requires "Implicit SSL"
                    props.put("mail.smtp.ssl.enable", "true");
                    props.put("mail.smtp.starttls.enable", "false");
                    props.put("mail.smtp.starttls.required", "false");
                } else {
                    // Ports 587 and 2525 use "Explicit SSL" (STARTTLS)
                    props.put("mail.smtp.ssl.enable", "false");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.starttls.required", "true");
                }

                testSender.testConnection();
                logger.info("Successfully connected to Mailgun on port {}", port);

                // Switch main sender to working port
                mailSender.setPort(port);
                logger.info("Mail sender switched to working port {}", port);

                break;

            } catch (Exception e) {

                logger.warn("Failed on port {}: {}", portStr, e.getMessage());
            }
        }
    }

    @Bean
    public JavaMailSenderImpl javaMailSender() {
        return mailSender;
    }
}
