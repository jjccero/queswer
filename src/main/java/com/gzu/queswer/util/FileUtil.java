package com.gzu.queswer.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class FileUtil {
    private FileUtil() {
    }

    private static String filePath;

    public static void setFilePath(String filePath) {
        FileUtil.filePath = filePath;
    }

    public static boolean uploadFile(MultipartFile file, Long userId) {
        try (FileOutputStream outputStream = new FileOutputStream(filePath + userId + ".png")) {
            outputStream.write(file.getBytes());
            outputStream.flush();
            return true;
        } catch (IOException e) {
            log.info(e.toString());
            return false;
        }
    }
}
