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
    private FileResponse fileResponse;

    @Builder
    public UserInformationResponse(String identifier, String nickname, Files files) {
        this.identifier = identifier;
        this.nickname = nickname;
        this.fileResponse = convertToFileResponse(Optional.ofNullable(files));
    }

    public static UserInformationResponse createByEntity(User findUser, Files files) {
        return UserInformationResponse.builder()
                .identifier(findUser.getIdentifier())
                .nickname(findUser.getNickname())
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
