package com.genius.gitget.slack.service;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.Attachment;
import com.slack.api.model.block.LayoutBlock;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SlackServiceImpl implements SlackService {
    private static final String PROD_ERROR_MESSAGE_TITLE = "*Exception 발생*";
    private static final String ATTACHMENTS_ERROR_COLOR = "#eb4034";
    @Value("${slack.token}")
    private String token;
    @Value("${slack.channel}")
    private String channel;

    @Override
    public void sendMessage(String message) {
        try {
            MethodsClient methods = Slack.getInstance().methods(token);
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(channel)
                    .text(message)
                    .build();

            methods.chatPostMessage(request);

        } catch (SlackApiException | IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void sendMessage(Exception exception) {
        try {
            List<LayoutBlock> layoutBlocks = SlackMessageUtil.createProdErrorMessage(exception);
            List<Attachment> attachments = SlackMessageUtil.createAttachments(ATTACHMENTS_ERROR_COLOR,
                    layoutBlocks);

            MethodsClient methods = Slack.getInstance().methods(token);
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(channel)
                    .attachments(attachments)
                    .text(PROD_ERROR_MESSAGE_TITLE)
                    .build();

            methods.chatPostMessage(request);
            log.info("slack 메세지 전송 성공");

        } catch (SlackApiException | IOException e) {
            log.error(e.getMessage());
        }
    }
}
