package com.genius.gitget.global.util.exception.handler;

public interface MessageSender {
    /**
     * 예외가 발생했을 때 Slack에 예외 발생에 대한 메세지를 보내는 메서드
     * <p>
     * 주의 사항!!
     * 활성화 된 profile이 "prod"일 때에만 작동하도록 해야 합니다.
     * Environment의 matchProfiles()를 통해 특정 프로파일이 활성화되어 있는지 확인 가능
     * if(!environment.matchesProfiles("prod")) return;
     *
     * @param exception 발생한 예외
     */
    void sendSlackMessage(Exception exception);
}
