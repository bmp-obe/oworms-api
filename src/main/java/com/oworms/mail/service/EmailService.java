package com.oworms.mail.service;

import com.oworms.common.error.OWormException;
import com.oworms.common.error.OWormExceptionType;
import com.oworms.mail.config.MailContentBuilder;
import com.oworms.mail.dto.BucketOverflowDTO;
import com.oworms.mail.dto.EmailWordDTO;
import com.oworms.mail.dto.NewBnaDTO;
import com.oworms.mail.dto.NewWordEmailDTO;
import com.oworms.mail.dto.UpdatedWordEmailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private static final String BOT = "bot@oworms.com";
    private static final String ENCODING = "UTF-8";
    private static final String UUID = "{uuid}";

    @Value("${mail.getURL}")
    private String getURL;

    @Value("${mail.putURL}")
    private String putURL;

    @Value("${mail.admin.emailAddress}")
    private String adminEmailAddress;

    private final boolean mailDisabled;

    @Autowired
    public EmailService(final JavaMailSender mailSender,
                        final MailContentBuilder mailContentBuilder,
                        @Value("${mail.disabled}") final String disabled) {
        this.mailSender = mailSender;
        this.mailContentBuilder = mailContentBuilder;
        this.mailDisabled = "true".equals(disabled);
    }

    public void sendNewBna(final NewBnaDTO newBna, final String[] recipients) {
        if (mailDisabled) {
            return;
        }

        MimeMessagePreparator messagePrep = (MimeMessage mimeMessage) -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, ENCODING);

            messageHelper.setFrom(BOT);
            messageHelper.setTo(recipients[0]);
            messageHelper.setSubject(newBna.getTitle());
            messageHelper.setBcc(recipients);

            String messageContent = mailContentBuilder.build(newBna, NewBnaDTO.TEMPLATE);

            messageHelper.setText(messageContent, true);
        };

        try {
            mailSender.send(messagePrep);
        } catch (MailException e) {
            throw new OWormException(OWormExceptionType.EMAIL_SEND_FAILURE, "Failed to send report email");
        }
    }

    public void sendNewWordEmail(final String title, final EmailWordDTO wordDTO, final String[] recipients) {
        if (mailDisabled) {
            return;
        }

        final NewWordEmailDTO newWordEmailDTO = getNewWordEmailDTO(title, wordDTO);

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

    public void sendUpdateWordEmail(final UpdatedWordEmailDTO updatedWordEmail, final String[] recipients) {
        if (mailDisabled) {
            return;
        }

        final String retrievalLink = getURL.replace(UUID, updatedWordEmail.getOld().getUuid());
        updatedWordEmail.setRetrievalLink(retrievalLink);

        final String editLink = putURL.replace(UUID, updatedWordEmail.getOld().getUuid());
        updatedWordEmail.setEditLink(editLink);

        MimeMessagePreparator messagePrep = (MimeMessage mimeMessage) -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, ENCODING);

            messageHelper.setFrom(BOT);
            messageHelper.setTo(recipients[0]);
            messageHelper.setSubject(updatedWordEmail.getTitle());
            messageHelper.setBcc(recipients);

            String messageContent = mailContentBuilder.build(updatedWordEmail, UpdatedWordEmailDTO.TEMPLATE);

            messageHelper.setText(messageContent, true);
        };

        try {
            mailSender.send(messagePrep);
        } catch (MailException e) {
            throw new OWormException(OWormExceptionType.EMAIL_SEND_FAILURE, "Failed to send update word email");
        }
    }

    public void sendBucketOverflow(BucketOverflowDTO bucketOverflowDTO) {
        if (mailDisabled) {
            return;
        }

        String[] recipients = new String[1];
        recipients[0] = adminEmailAddress;

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

    private NewWordEmailDTO getNewWordEmailDTO(String title, EmailWordDTO wordDTO) {
        String retrievalLink = getURL.replace(UUID, wordDTO.getUuid());
        String editLink = putURL.replace(UUID, wordDTO.getUuid());

        NewWordEmailDTO newWordEmailDTO = new NewWordEmailDTO(title, wordDTO, retrievalLink);
        newWordEmailDTO.setEditLink(editLink);

        return newWordEmailDTO;
    }
}