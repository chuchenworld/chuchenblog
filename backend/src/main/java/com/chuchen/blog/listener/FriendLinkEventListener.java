package com.chuchen.blog.listener;

import com.chuchen.blog.event.FriendLinkCreatedEvent;
import com.chuchen.blog.entity.FriendLink;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 友链申请事件监听器 - 发送邮件通知管理员
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FriendLinkEventListener {

    private final JavaMailSender mailSender;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFriendLinkCreated(FriendLinkCreatedEvent event) {
        log.info("【FriendLinkEventListener】收到友链申请事件，开始发送邮件通知");

        String fromEmail = "3349410592@qq.com";
        String recipientEmail = "3349410592@qq.com";

        try {
            FriendLink friendLink = event.getFriendLink();

            log.info("【FriendLinkEventListener】邮件详情 - 发件人: {}, 收件人: {}", fromEmail, recipientEmail);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "初尘博客");
            helper.setTo(recipientEmail);
            helper.setSubject("【友链申请】新的友链申请待审核");

            String htmlContent = String.format(
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<h2 style='color: #333; border-left: 4px solid #409eff; padding-left: 10px;'>新友链申请提醒</h2>" +
                "<p style='color: #666;'>您收到了新的友链申请，请及时审核：</p>" +
                "<div style='background-color: #f5f7fa; padding: 15px; border-radius: 5px; margin: 10px 0;'>" +
                "<p><strong>站点名称：</strong> %s</p>" +
                "<p><strong>站点描述：</strong> %s</p>" +
                "<p><strong>站点链接：</strong> <a href='%s' target='_blank'>%s</a></p>" +
                "<p><strong>头像链接：</strong> %s</p>" +
                "</div>" +
                "<p style='color: #999; font-size: 12px;'>此邮件由系统自动发送，请勿回复</p>" +
                "</div>",
                friendLink.getSiteName(),
                friendLink.getSiteDescript() != null ? friendLink.getSiteDescript() : "",
                friendLink.getSiteUrl(),
                friendLink.getSiteUrl(),
                friendLink.getAvatarUrl() != null ? friendLink.getAvatarUrl() : "未提供"
            );

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("【FriendLinkEventListener】邮件发送成功，收件人: {}", recipientEmail);
        } catch (MessagingException e) {
            log.error("【FriendLinkEventListener】邮件发送失败 - 错误类型: {}, 错误信息: {}",
                e.getClass().getName(), e.getMessage(), e);
            log.error("【FriendLinkEventListener】邮件详情 - 发件人: {}, 收件人: {}", fromEmail, recipientEmail);
        } catch (Exception e) {
            log.error("【FriendLinkEventListener】发送邮件时发生未知错误 - 错误类型: {}, 错误信息: {}",
                e.getClass().getName(), e.getMessage(), e);
            log.error("【FriendLinkEventListener】邮件详情 - 发件人: {}, 收件人: {}", fromEmail, recipientEmail);
        }
    }
}