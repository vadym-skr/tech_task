package technikal.task.fishmarket.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishFile;
import technikal.task.fishmarket.repository.FishFileRepository;
import technikal.task.fishmarket.utils.FileUtil;
import technikal.task.fishmarket.utils.exception.CustomUncheckedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class FishFileService {

    private static final Logger logger = LoggerFactory.getLogger(FishService.class);

    private final FishFileRepository fishFileRepository;
    private final FileUtil fileUtil;

    @Autowired
    public FishFileService(FishFileRepository fishFileRepository, FileUtil fileUtil) {
        this.fishFileRepository = fishFileRepository;
        this.fileUtil = fileUtil;
    }

    @Transactional
    public void saveFishFiles(Fish fish, List<MultipartFile> files, Date catchDate) {
        List<String> storedFileNames = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                String storageFileName = fileUtil.createUniqueFileName(catchDate, file.getOriginalFilename());
                fileUtil.saveFile(file, storageFileName);

                FishFile fishFile = new FishFile();
                fishFile.setFish(fish);
                fishFile.setFileName(storageFileName);
                fishFileRepository.save(fishFile);

                storedFileNames.add(storageFileName);
            }
        } catch (IOException ex) {
            fileUtil.removeSavedFiles(storedFileNames);
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
            fileUtil.removeFileFromStorage(fishFile.getFileName());
        }
    }
}
