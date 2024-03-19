package com.genius.gitget.profile.dto;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;

@Data
public class UserInformationResponse {
    private String identifier;
    private String nickname;
    private Long frameId;
    private FileResponse fileResponse;

    @Builder
    public UserInformationResponse(String identifier, String nickname, Long frameId, Files files) {
        this.identifier = identifier;
        this.nickname = nickname;
        this.frameId = frameId;
        this.fileResponse = convertToFileResponse(Optional.ofNullable(files));
    }

    public static UserInformationResponse createByEntity(User findUser, Long frameId, Files files) {
        return UserInformationResponse.builder()
                .identifier(findUser.getIdentifier())
                .nickname(findUser.getNickname())
                .frameId(frameId)
                .files(files)
                .build();
    }

    private static FileResponse convertToFileResponse(Optional<Files> files) {
        if (files.isEmpty()) {
            return FileResponse.createNotExistFile();
        }
        return FileResponse.createExistFile(files.get());
    }
}
