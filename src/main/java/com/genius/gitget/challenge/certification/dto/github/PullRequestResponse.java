package com.genius.gitget.challenge.certification.dto.github;

import com.genius.gitget.global.util.exception.BusinessException;
import java.io.IOException;
import java.util.Date;
import lombok.Builder;
import org.kohsuke.github.GHPullRequest;

@Builder
public record PullRequestResponse(
        String prTitle,
        String prLink,
        String state,
        Date createdAt,
        Date closedAt
) {

    public static PullRequestResponse create(GHPullRequest ghPullRequest) {
        try {
            return PullRequestResponse.builder()
                    .prTitle(ghPullRequest.getTitle())
                    .prLink(String.valueOf(ghPullRequest.getHtmlUrl()))
                    .state(ghPullRequest.getState().name())
                    .createdAt(ghPullRequest.getCreatedAt())
                    .closedAt(ghPullRequest.getClosedAt())
                    .build();
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }
}
