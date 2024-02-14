package com.genius.gitget.util.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileTestUtil {
    public static MultipartFile getMultipartFile(String filename) {
        return new MultipartFile() {
            @Override
            public String getName() {
                return filename;
            }

            @Override
            public String getOriginalFilename() {
                return filename + ".png";
            }

            @Override
            public String getContentType() {
                return "png";
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
