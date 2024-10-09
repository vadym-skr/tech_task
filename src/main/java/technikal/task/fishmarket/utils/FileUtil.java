package technikal.task.fishmarket.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Component
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    @Value("${image.upload.dir}")
    private String uploadDir;

    public String createUniqueFileName(Date catchDate, String originalFilename) {
        return catchDate.getTime() + "_" + originalFilename;
    }

    public void saveFile(MultipartFile file, String storageFileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void removeFileFromStorage(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir, fileName);
            Files.deleteIfExists(filePath);
            logger.info("Deleted file: {}", filePath);
        } catch (IOException ex) {
            logger.error("Failed to delete file: {}. Error: {}", fileName, ex.getMessage());
        }
    }

    public void removeSavedFiles(List<String> storedFileNames) {
        for (String fileName : storedFileNames) {
            removeFileFromStorage(fileName);
        }
    }
}
