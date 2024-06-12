package at.letto.basespringboot.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class MailService {

    static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    @Value("${spring.mail.host:}")
    private String mailServerHost;

    @Value("${spring.mail.port:}")
    private Integer mailServerPort;

    @Value("${spring.mail.username:}")
    private String mailServerUsername;

    @Value("${spring.mail.password:}")
    private String mailServerPassword;

    @Value("${spring.mail.properties.mail.smtp.auth:}")
    private String mailServerAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable:false}")
    private String mailServerStartTls;

    @Value("${spring.mail.properties.mail.smtp.ssl.enable:false}")
    private String mailServerSsl;

    @Value("${spring.mail.properties.mail.debug:false}")
    private String mailDebug;

    @Value("${spring.mail.templates.path:mail-templates}")
    private String mailTemplatesPath;

    @Value("${spring.mail.address.noreply:noreply@myschool.at}")
    private String noReplyAddress;

    @Value("${spring.mail.address.reply:reply@myschool.at}")
    private String replyAddress;

    @Value("${mail.admin.adress:}")
    private String adminMailAdress;

    private JavaMailSender javaMailSender = null;

    public JavaMailSender getJavaMailSender(){
        if (javaMailSender == null)
            javaMailSender = createJavaMailSender();
        return javaMailSender;
    }

    private JavaMailSender createJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailServerHost);
        mailSender.setPort(mailServerPort!=null?mailServerPort:465);

        mailSender.setUsername(mailServerUsername);
        mailSender.setPassword(mailServerPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", mailServerAuth);
        props.put("mail.smtp.starttls.enable", mailServerStartTls);
        props.put("mail.smtp.ssl.enable", mailServerSsl);
        props.put("mail.debug", mailDebug);

        return mailSender;
    }

    public ResourceBundleMessageSource emailMessageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("mailMessages");
        return messageSource;
    }

    public boolean sendSimpleMessage(String subject, String text) {
        if (adminMailAdress!=null && adminMailAdress.length()>0)
            return sendSimpleMessage(adminMailAdress,subject,text);
        return false;
    }

    public boolean sendSimpleMessage(String to, String subject, String text) {
        try {
            JavaMailSender javaMailSender = getJavaMailSender();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(noReplyAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
            return true;
        } catch (MailException exception) {
            LOGGER.error("cannot send email to "+to+" subject "+subject+" see stack-trace");
            exception.printStackTrace();
            return false;
        }
    }

    public boolean sendHtmlMessage(String subject, String text) {
        if (adminMailAdress!=null && adminMailAdress.length()>0)
            return sendHtmlMessage(adminMailAdress,subject,text);
        return false;
    }

    public boolean sendHtmlMessage(String to, String subject, String htmlBody) {
        try {
            JavaMailSender javaMailSender = getJavaMailSender();
            MimeMessage message = getJavaMailSender().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(noReplyAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            // helper.addInline("attachment.png", resourceFile);
            javaMailSender.send(message);
            return true;
        } catch (jakarta.mail.MessagingException ex) {
            LOGGER.error("cannot send email to "+to+" subject "+subject+" see stack-trace");
            ex.printStackTrace();
            return false;
        }
    }

/*
    public void sendSimpleMessageUsingTemplate(String to,
                                               String subject,
                                               String ...templateModel) {
        String text = String.format(template.getText(), templateModel);
        sendSimpleMessage(to, subject, text);
    }

    public void sendMessageWithAttachment(String to,
                                          String subject,
                                          String text,
                                          String pathToAttachment) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            // pass 'true' to the constructor to create a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(emailConfiguration.getNoReplyAddress());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Invoice", file);

            emailSender.send(message);
        } catch (jakarta.mail.MessagingException e) {
            e.printStackTrace();
        }
    }


    public void sendMessageUsingThymeleafTemplate(
            String to, String subject, Map<String, Object> templateModel)
            throws jakarta.mail.MessagingException {

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);

        String htmlBody = thymeleafTemplateEngine.process("template-thymeleaf.html", thymeleafContext);

        sendHtmlMessage(to, subject, htmlBody);
    }

    private void sendHtmlMessage(String to, String subject, String htmlBody) throws jakarta.mail.MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(emailConfiguration.getNoReplyAddress());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.addInline("attachment.png", resourceFile);
        emailSender.send(message);
    }*/
}
