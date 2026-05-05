package com.chuchen.blog.listener;

import com.chuchen.blog.event.CommentCreatedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventListener {

    private final JavaMailSender mailSender;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentCreated(CommentCreatedEvent event) {
        log.info("【CommentEventListener】收到评论创建事件，开始发送邮件通知");

        String fromEmail = "3349410592@qq.com";
        String recipientEmail = "3349410592@qq.com";

        try {
            String title;
            String subjectPrefix;

            // 动态获取标题：优先使用文章标题，否则使用照片标题
            if (event.getPost() != null) {
                title = event.getPost().getTitle();
                subjectPrefix = "文章";
            } else if (event.getPhoto() != null) {
                title = event.getPhoto().getCaption() != null && !event.getPhoto().getCaption().isEmpty()
                    ? event.getPhoto().getCaption()
                    : (event.getPhoto().getName() != null ? event.getPhoto().getName() : "照片");
                subjectPrefix = "照片";
            } else {
                log.warn("【CommentEventListener】无法获取标题信息，Post 和 Photo 都为 null");
                return;
            }

            String commentNickname = event.getComment().getNickname();
            String commentEmail = event.getComment().getEmail();
            String commentContent = event.getComment().getContent();

            log.info("【CommentEventListener】邮件详情 - 发件人: {}, 收件人: {}, {}: {}",
                fromEmail, recipientEmail, subjectPrefix, title);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "初尘博客");
            helper.setTo(recipientEmail);
            helper.setSubject(String.format("【新评论提醒】%s：《%s》", subjectPrefix, title));

            if (commentEmail != null && !commentEmail.isEmpty()) {
                helper.setReplyTo(commentEmail);
            }

            String htmlContent = String.format(
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<h2 style='color: #333; border-left: 4px solid #409eff; padding-left: 10px;'>新评论提醒</h2>" +
                "<p style='color: #666;'>%s <strong>%s</strong> 收到新评论：</p>" +
                "<div style='background-color: #f5f7fa; padding: 15px; border-radius: 5px; margin: 10px 0;'>" +
                "<p><strong>评论者：</strong> %s</p>" +
                "<p><strong>评论者邮箱：</strong> %s</p>" +
                "<p><strong>评论内容：</strong></p>" +
                "<p style='color: #666; line-height: 1.6;'>%s</p>" +
                "</div>" +
                "<p style='color: #999; font-size: 12px;'>此邮件由系统自动发送，请勿回复</p>" +
                "</div>",
                subjectPrefix, title, commentNickname, commentEmail,
                commentContent != null ? commentContent.replace("\n", "<br/>") : ""
            );

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("【CommentEventListener】邮件发送成功，收件人: {}", recipientEmail);
        } catch (MessagingException e) {
            log.error("【CommentEventListener】邮件发送失败 - 错误类型: {}, 错误信息: {}",
                e.getClass().getName(), e.getMessage(), e);
            log.error("【CommentEventListener】邮件详情 - 发件人: {}, 收件人: {}", fromEmail, recipientEmail);
        } catch (Exception e) {
            log.error("【CommentEventListener】发送邮件时发生未知错误 - 错误类型: {}, 错误信息: {}",
                e.getClass().getName(), e.getMessage(), e);
            log.error("【CommentEventListener】邮件详情 - 发件人: {}, 收件人: {}", fromEmail, recipientEmail);
        }
    }

}
