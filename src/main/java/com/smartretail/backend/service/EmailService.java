package com.smartretail.backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRegistrationEmail(String toEmail, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Smart Retail <noreply@smartretail.com>");
        message.setTo(toEmail);
        message.setSubject("Chào mừng bạn đến với Smart Retail!");
        message.setText("Chào " + fullName + ",\n\n" +
                "Chúc mừng bạn đã đăng ký tài khoản thành công tại hệ thống Smart Retail.\n" +
                "Bây giờ bạn có thể đăng nhập và sử dụng các tính năng của chúng tôi.\n\n" +
                "Trân trọng,\nĐội ngũ Smart Retail.");

        mailSender.send(message);
    }
}