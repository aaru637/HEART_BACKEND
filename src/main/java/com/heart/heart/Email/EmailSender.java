package com.heart.heart.Email;

import java.io.IOException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSender {
    @Autowired
    private JavaMailSender jMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public boolean sendResetPasswordHTMLEmail(String toEmail, String subject, String id, String role)
            throws MessagingException, IOException {
        try {
            MimeMessage message = jMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            Context context = new Context();
            context.setVariable("id", id);
            context.setVariable("role", role);
            helper.setText(templateEngine.process("forgot-password", context), true);
            jMailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean sendConfirmationHTMLEmail(String toEmail, String subject, String role, String id, String name) {
        try {
            MimeMessage message = jMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            Context context = new Context();
            context.setVariables(new HashMap<>() {
                {
                    put("id", id);
                    put("name", name);
                    put("role", role);
                }
            });
            helper.setText(templateEngine.process("confirmation-link", context), true);
            jMailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean sendRequestHTMLEmail(String toEmail, String subject, String aId, String sId, String aName,
            String sName) {
        try {
            MimeMessage message = jMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            Context context = new Context();
            context.setVariables(new HashMap<>() {
                {
                    put("aId", aId);
                    put("sId", sId);
                    put("aName", aName);
                    put("sName", sName);
                }
            });
            helper.setText(templateEngine.process("request-link", context), true);
            jMailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
