package com.practice.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailSendingService {
    public boolean sendEmail(String subject, String body, String from, String to) throws NoSuchProviderException {
        boolean isEmailSend = false;

        String host = "smtp.gmail.com";
        String port = "587";

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host); // specify your SMTP server host
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // if required
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.port", port); // default port for TLS

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("", "");
            }
        });

        session.setDebug(true);
        session.getTransport();

        try {
            MimeMessage m = new MimeMessage(session);
            m.setFrom(from);
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            m.setSubject(subject);
            m.setText(body);

            Transport.send(m);

            System.out.println("Email sent successfully!");
            isEmailSend = true;

        } catch (MessagingException e) {
            System.out.println("Error message is " + e.getMessage());
            e.getMessage();
        }

        return isEmailSend;
    }
}
