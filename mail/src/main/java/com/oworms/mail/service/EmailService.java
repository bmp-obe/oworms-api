package com.oworms.mail.service;

import com.oworms.mail.config.MailContentBuilder;
import com.oworms.mail.config.MailProperties;
import com.oworms.mail.dto.BucketOverflowDTO;
import com.oworms.mail.dto.UpdatedWordEmailDTO;
import com.oworms.common.error.OWormException;
import com.oworms.common.error.OWormExceptionType;
import com.oworms.mail.dto.NewBnaDTO;
import com.oworms.mail.dto.EmailWordDTO;
import com.oworms.mail.dto.NewWordEmailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final MailContentBuilder mailContentBuilder;
    private final MailProperties properties;
    private static final String BOT = "bot@oworms.com";
    private static final String ENCODING = "UTF-8";

    @Autowired
    public EmailService(final JavaMailSender mailSender,
                        final MailContentBuilder mailContentBuilder,
                        final MailProperties properties) {
        this.mailSender = mailSender;
        this.mailContentBuilder = mailContentBuilder;
        this.properties = properties;
    }

    public void sendNewBna(NewBnaDTO newBan) {
        if (properties.isDisabled()) {
             return;
        }

        String[] recipients = properties.getRecipients().split(",");

        MimeMessagePreparator messagePrep = (MimeMessage mimeMessage) -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, ENCODING);

            messageHelper.setFrom(BOT);
            messageHelper.setTo(recipients[0]);
            messageHelper.setSubject(newBan.getTitle());
            messageHelper.setBcc(recipients);

            String messageContent = mailContentBuilder.build(newBan, NewBnaDTO.TEMPLATE);

            messageHelper.setText(messageContent, true);
        };

        try {
            mailSender.send(messagePrep);
        } catch (MailException e) {
            throw new OWormException(OWormExceptionType.EMAIL_SEND_FAILURE, "Failed to send report email");
        }
    }

    public void sendBucketOverflow(BucketOverflowDTO bucketOverflowDTO) {
        if (properties.isDisabled()) {
            return;
        }

        String[] recipients = properties.getRecipients().split(",");

        MimeMessagePreparator messagePrep = (MimeMessage mimeMessage) -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, ENCODING);

            messageHelper.setFrom(BOT);
            messageHelper.setTo(recipients[0]);
            messageHelper.setSubject(bucketOverflowDTO.getTitle());
            messageHelper.setBcc(recipients);

            String messageContent = mailContentBuilder.build(bucketOverflowDTO, BucketOverflowDTO.TEMPLATE);

            messageHelper.setText(messageContent, true);
        };

        try {
            mailSender.send(messagePrep);
        } catch (MailException e) {
            throw new OWormException(OWormExceptionType.EMAIL_SEND_FAILURE, "Failed to send report email");
        }
    }

    public void sendNewWordEmail(String title, EmailWordDTO wordDTO) {
        if (properties.isDisabled()) {
            return;
        }

        NewWordEmailDTO newWordEmailDTO = getNewWordEmailDTO(title, wordDTO);

        String[] recipients = properties.getRecipients().split(",");

        MimeMessagePreparator messagePrep = (MimeMessage mimeMessage) -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, ENCODING);

            messageHelper.setFrom(BOT);
            messageHelper.setTo(recipients[0]);
            messageHelper.setSubject(title);
            messageHelper.setBcc(recipients);

            String messageContent = mailContentBuilder.build(newWordEmailDTO, NewWordEmailDTO.TEMPLATE);

            messageHelper.setText(messageContent, true);
        };

        try {
            mailSender.send(messagePrep);
        } catch (MailException e) {
            throw new OWormException(OWormExceptionType.EMAIL_SEND_FAILURE, "Failed to send new word email");
        }
    }

    private NewWordEmailDTO getNewWordEmailDTO(String title, EmailWordDTO wordDTO) {
        String retrievalLink = properties.getRetrievalLink().replace("{uuid}", String.valueOf(wordDTO.getUuid()));

        return new NewWordEmailDTO(title, wordDTO, retrievalLink);
    }

    public void sendUpdateWordEmail(String title, EmailWordDTO oldWord, EmailWordDTO updatedWord) {
        if (properties.isDisabled()) {
            return;
        }

        String[] recipients = properties.getRecipients().split(",");

        MimeMessagePreparator messagePrep = (MimeMessage mimeMessage) -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, ENCODING);

            messageHelper.setFrom(BOT);
            messageHelper.setTo(recipients[0]);
            messageHelper.setSubject(title);
            messageHelper.setBcc(recipients);

            String messageContent = mailContentBuilder.build(
                    getUpdateWordEmailDTO(title, oldWord, updatedWord),
                    UpdatedWordEmailDTO.TEMPLATE
            );

            messageHelper.setText(messageContent, true);
        };

        try {
            mailSender.send(messagePrep);
        } catch (MailException e) {
            throw new OWormException(OWormExceptionType.EMAIL_SEND_FAILURE, "Failed to send update word email");
        }
    }

    private UpdatedWordEmailDTO getUpdateWordEmailDTO(String title, EmailWordDTO oldWord, EmailWordDTO updatedWord) {
        String retrievalLink = properties.getRetrievalLink().replace("{uuid}", String.valueOf(oldWord.getUuid()));

        return new UpdatedWordEmailDTO(title, oldWord, updatedWord, retrievalLink);
    }
}