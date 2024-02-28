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
    private String information;
    private Long point;
    private FileResponse fileResponse;

    @Builder
    public UserInformationResponse(String identifier, String nickname, String information, Long point, Files files) {
        this.identifier = identifier;
        this.nickname = nickname;
        this.information = information;
        this.point = point;
        this.fileResponse = convertToFileResponse(Optional.ofNullable(files));
    }

    public static UserInformationResponse entityToDto(User findUser, Files files) {
        return UserInformationResponse.builder()
                .identifier(findUser.getIdentifier())
                .nickname(findUser.getNickname())
                .information(findUser.getInformation())
                .point(findUser.getPoint())
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
