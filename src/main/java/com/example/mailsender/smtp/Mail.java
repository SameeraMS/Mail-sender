package com.example.mailsender.smtp;

import javafx.concurrent.Task;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.List;
import java.util.Properties;


public class Mail extends Task<Boolean> {
    private String mail;
    private String text;
    private String subject;
    private File file;
    private List<String> emails;

    public Mail(String mail, String text, String subject, File file) {
        this.mail = mail;
        this.text = text;
        this.subject = subject;
        this.file = file;
    }

    public Mail(String mail, String subject , String text) {
        this.mail = mail;
        this.text = text;
        this.subject=subject;
    }

    public Mail(List<String> emails, String text, String subject) {
        this.text = text;
        this.subject = subject;
        this.emails = emails;
    }

    @Override
    protected Boolean call() {
        String from = "sameera.royalseven@gmail.com"; //sender's email address

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", 587);
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("sameera.royalseven@gmail.com",
                        "owfj ulnc hpin ggue");    //app password
            }
        });
        updateProgress(25,100);

        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(from,"sameera"));
            mimeMessage.setSubject(this.subject);
            if (emails != null) {
                for (String email : emails) {
                    mimeMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(email));
                }
            } else {
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(this.mail));
            }
            updateProgress(50,100);
            Thread.sleep(1000);

            if (file != null) {
                BodyPart messageBodyPart1 = new MimeBodyPart();
                messageBodyPart1.setText(text);

                MimeBodyPart messageBodyPart2 = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                messageBodyPart2.setDataHandler(new DataHandler(source));
                messageBodyPart2.setFileName(file.getName());

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart1);
                multipart.addBodyPart(messageBodyPart2);

                mimeMessage.setContent(multipart);
            } else {
                mimeMessage.setContent(this.text,"text/html");
            }
            updateProgress(90,100);
            Transport.send(mimeMessage);
            updateProgress(100,100);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
