package com.boot.email.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EmailController {

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private Configuration freemarkerConfig;

    /*********************************************************************************************************
     * Sending simple text email.
     ********************************************************************************************************/
    @GetMapping(value = "/send-text-mail")
    public String sendTextMail() throws MessagingException {
        TextMail();
        return "Email has been successfully sent";
    }

    private void TextMail() throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo("eshanwp@gmail.com");
        helper.setText("How are you?");
        helper.setSubject("Hi");

        sender.send(message);
    }

    /*********************************************************************************************************
     * Sending Email with Attachments
     ********************************************************************************************************/

    @GetMapping(value = "/send-attachment-mail")
    public String sendAttachmentEmail() throws Exception {

        AttachmentEmail();
        return "Email has been successfully sent with attachment";
    }

    private void AttachmentEmail() throws Exception{
        MimeMessage message = sender.createMimeMessage();

        // Enable the multipart flag!
        MimeMessageHelper helper = new MimeMessageHelper(message,true);

        helper.setTo("eshanwp@gmail.com");
        helper.setText("How are you?");
        helper.setSubject("Hi");

        ClassPathResource file = new ClassPathResource("dog.jpg");
        helper.addAttachment("dog.jpg", file);

        sender.send(message);
    }

    /***********************************************************************************************************
     * Sending Email with Inline Resources
     *
     * The following method embeds the dog picture to the email as an inline resource.
     * The recipient will see the resource embedded in the email (not as an attachment).
     **********************************************************************************************************/

    @GetMapping(value = "/send-inline-resource-mail")
    public String sendInlineResourcesEmail() throws Exception {

        InlineResourcesEmail();
        return "Email has been successfully sent with inline resources";
    }

    private void InlineResourcesEmail() throws Exception {
        MimeMessage message = sender.createMimeMessage();

        // Enable the multipart flag!
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo("eshanwp@gmail.com");
        helper.setText("<html><body>Here is a dog picture! <img src='cid:id101'/><body></html>", true);
        helper.setSubject("Hi");

        ClassPathResource file = new ClassPathResource("dog.jpg");
        helper.addInline("id101", file);

        sender.send(message);
    }

    /*********************************************************************************************************
     * Sending Email with FreeMarker Templates
     ********************************************************************************************************/

    @GetMapping(value = "/send-freemaker-template-mail")
    public String sendFreeMarkerTemplateEmail() throws Exception {

        FreeMarkerTemplateEmail();

        return "Email has been successfully sent with freeMarker template";
    }

    private void FreeMarkerTemplateEmail() throws Exception {

        MimeMessage message = sender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message);

        Map<String, Object> model = new HashMap();
        model.put("user", "eshan");

        // set loading location to src/main/resources
        // You may want to use a subfolder such as /templates here
        freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/");

        Template t = freemarkerConfig.getTemplate("welcome.ftl");
        String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

        helper.setTo("eshanwp@gmail.com");
        helper.setText(text, true); // set to html
        helper.setSubject("Hi");

        sender.send(message);

    }

}
