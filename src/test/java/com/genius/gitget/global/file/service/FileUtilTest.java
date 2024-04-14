package com.genius.gitget.global.file.service;

import static com.genius.gitget.global.file.domain.FileType.INSTANCE;
import static com.genius.gitget.global.file.domain.FileType.TOPIC;
import static com.genius.gitget.global.util.exception.ErrorCode.FILE_NOT_EXIST;
import static com.genius.gitget.global.util.exception.ErrorCode.NOT_SUPPORTED_EXTENSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.CopyDTO;
import com.genius.gitget.global.file.dto.UpdateDTO;
import com.genius.gitget.global.file.dto.UploadDTO;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.util.file.FileTestUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles({"file"})
class FileUtilTest {
    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private FilesService filesService;
    @Value("${file.upload.path}")
    private String UPLOAD_PATH;

    @Test
    @DisplayName("file을 전달받았을 때, originFilename가 null일 때 예외를 발생해야 한다.")
    public void should_throwException_when_originFilenameIsNull() {
        //given
        MultipartFile multipartFile = getTestMultiPartFile(null);

        //when&then
        assertThatThrownBy(() -> fileUtil.validateFile(multipartFile))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(FILE_NOT_EXIST.getMessage());
    }

    @Test
    @DisplayName("file을 전달받았을 때, originFilename이 비어있다면 예외를 발생해야 한다.")
    public void should_throwException_when_originFilenameIsBlank() {
        //given
        MultipartFile multipartFile = getTestMultiPartFile("");

        //when&then
        assertThatThrownBy(() -> fileUtil.validateFile(multipartFile))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(FILE_NOT_EXIST.getMessage());
    }

    @Test
    @DisplayName("file을 전달받았을 때, 지원하는 확장자가 아니라면 예외를 발생해야 한다.")
    public void should_throwException_when_notSupportedExtension() {
        //given
        MultipartFile multipartFile = getTestMultiPartFile("sky.pdf");

        //when&then
        assertThatThrownBy(() -> fileUtil.validateFile(multipartFile))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_SUPPORTED_EXTENSION.getMessage());
    }

    @Test
    @DisplayName("유효한 file을 전달받았을 때, 반환받은 File 객체에는 upload path가 포함되어 있어야 한다.")
    public void should_returnTargetFileInstance_when_passValidFile() {
        //given
        MultipartFile multipartFile = getTestMultiPartFile("sky.png");

        //when
        UploadDTO uploadDTO = fileUtil.getUploadInfo(multipartFile, FileType.PROFILE, UPLOAD_PATH);

        //then
        assertThat(uploadDTO.fileURI()).contains(UPLOAD_PATH);
    }

    @Test
    @DisplayName("갱신 대상인 File을 전달했을 때, 갱신해야 할 정보들을 담은 UpdateDTO를 반환받는다.")
    public void should_returnUpdateDTO_when_passUpdateTargetFile() {
        //given
        String originalFilename = "sky.png";
        MultipartFile multipartFile = getTestMultiPartFile(originalFilename);
        FileType fileType = FileType.PROFILE;

        //when
        UpdateDTO updateDTO = fileUtil.getUpdateInfo(multipartFile, fileType, UPLOAD_PATH);

        //then
        assertThat(updateDTO.originalFilename()).isEqualTo(originalFilename);
        assertThat(updateDTO.fileURI()).contains(UPLOAD_PATH);
        assertThat(updateDTO.fileURI()).contains(updateDTO.savedFilename());
    }

    @Test
    @DisplayName("기존의 파일을 복사하려고 할 때, 복사에 필요한 정보들을 추출하여 전달할 수 있다.")
    public void should_passInformation_when_tryToCopy() {
        //given
        Files files = Files.builder()
                .originalFilename("original file name.png")
                .savedFilename("saved file name")
                .fileURI("file URI")
                .fileType(TOPIC)
                .build();

        //when
        CopyDTO copyDTO = fileUtil.getCopyInfo(files, INSTANCE, UPLOAD_PATH);

        //then
        assertThat(copyDTO.fileType()).isEqualTo(INSTANCE);
        assertThat(copyDTO.fileURI()).contains(UPLOAD_PATH);
    }

    @Test
    @DisplayName("FileTestUtil을 통해 받은 MultipartFile을 통해 인코딩 파일을 받을 수 있다")
    public void should_getEncodedFiles() {
        //given
        MultipartFile multipartFile = FileTestUtil.getMultipartFile("filename");
        Files files = filesService.uploadFile(multipartFile, TOPIC);

        //when
        String encoded = fileUtil.encodedImage(files);

        //then
        log.info(encoded);
    }


    private MultipartFile getTestMultiPartFile(String originalFilename) {
        return new MultipartFile() {
            @Override
            public String getName() {
                return "image";
            }

            @Override
            public String getOriginalFilename() {
                return originalFilename;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }
        };
    }
}