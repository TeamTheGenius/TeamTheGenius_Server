package com.genius.gitget.slack.service;

public interface SlackService {
    void sendMessage(String message);

    void sendMessage(Exception exception);
}
