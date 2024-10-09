package technikal.task.fishmarket.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishFile;
import technikal.task.fishmarket.repository.FishFileRepository;
import technikal.task.fishmarket.utils.exception.CustomUncheckedException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FishFileService {

    private static final Logger logger = LoggerFactory.getLogger(FishFileService.class);

    private final FishFileRepository fishFileRepository;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Autowired
    public FishFileService(FishFileRepository fishFileRepository) {
        this.fishFileRepository = fishFileRepository;
    }

    @Transactional
    public void saveFishFiles(Fish fish, List<MultipartFile> files, Date catchDate) {
        List<String> storedFileNames = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                String storageFileName = createUniqueFileName(catchDate, file.getOriginalFilename());
                saveFile(file, storageFileName);

                FishFile fishFile = new FishFile();
                fishFile.setFish(fish);
                fishFile.setFileName(storageFileName);
                fishFileRepository.save(fishFile);

                storedFileNames.add(storageFileName);
            }
        } catch (IOException ex) {
            removeSavedFiles(storedFileNames);
            String errorText = "Error saving fish files: " + ex.getMessage();
            logger.error(errorText, ex);
            throw new CustomUncheckedException(errorText);
        }
    }

    @Transactional
    public void removeFilesByFish(Fish fish) {
        List<FishFile> fishFiles = fishFileRepository.findByFish(fish);
        fishFileRepository.deleteAll(fishFiles);

        for (FishFile fishFile : fishFiles) {
            removeFileFromStorage(fishFile.getFileName());
        }
    }

    private String createUniqueFileName(Date catchDate, String originalFilename) {
        return catchDate.getTime() + "_" + originalFilename;
    }

    private void saveFile(MultipartFile file, String storageFileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void removeFileFromStorage(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir, fileName);
            Files.deleteIfExists(filePath);
            logger.info("Deleted file: {}", filePath);
        } catch (IOException ex) {
            logger.error("Failed to delete file: {}. Error: {}", fileName, ex.getMessage());
        }
    }

    private void removeSavedFiles(List<String> storedFileNames) {
        for (String fileName : storedFileNames) {
            removeFileFromStorage(fileName);
        }
    }
}
