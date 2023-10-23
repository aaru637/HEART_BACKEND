package com.heart.heart.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {
    @Autowired
    private JavaMailSender jMailSender;

    private SimpleMailMessage mailMessage = new SimpleMailMessage();

    public boolean sendEmail(String toEmail, String subject, String body) {
        try {
            mailMessage.setFrom("dhandapanisakthi123@gmail.com");
            mailMessage.setTo(toEmail);
            mailMessage.setText(body);
            mailMessage.setSubject(subject);

            jMailSender.send(mailMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
