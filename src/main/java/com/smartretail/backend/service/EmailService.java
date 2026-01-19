package com.smartretail.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.base.url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendRegistrationEmail(String toEmail, String fullName, String token) {
        String verificationLink = baseUrl + "/api/auth/verify?token=" + token;

        Context context = new Context();
        context.setVariable("fullName", fullName);
        context.setVariable("verificationLink", verificationLink);

        String htmlContent = templateEngine.process("email/email-verification", context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("Smart Retail Support <smartretail.contact@gmail.com>");
            helper.setTo(toEmail);
            helper.setSubject("Xác thực tài khoản Smart Retail");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi gửi email xác thực: " + e.getMessage());
        }
    }
}