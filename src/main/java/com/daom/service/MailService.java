package com.daom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public boolean sendMail(String email,String title, String content) throws MessagingException {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(msg, true, "UTF-8");
        messageHelper.setSubject(title);
        messageHelper.setText(content);
        messageHelper.setTo(email);

        msg.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(email));
        mailSender.send(msg);
    }
}
