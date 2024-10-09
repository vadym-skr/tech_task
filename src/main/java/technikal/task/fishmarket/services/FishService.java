package technikal.task.fishmarket.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technikal.task.fishmarket.dtos.FishDto;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.repository.FishRepository;

import java.util.Date;
import java.util.List;

@Service
public class FishService {

    private final FishRepository fishRepository;

    private final FishFileService fishFileService;

    @Autowired
    public FishService(FishRepository fishRepository, FishFileService fishFileService) {
        this.fishRepository = fishRepository;
        this.fishFileService = fishFileService;
    }

    public List<Fish> getAllFish() {
        return fishRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Transactional
    public void saveFish(FishDto fishDto) {
        Date catchDate = new Date();

        Fish fish = new Fish();
        fish.setCatchDate(catchDate);
        fish.setName(fishDto.getName());
        fish.setPrice(fishDto.getPrice());
        Fish savedFish = fishRepository.save(fish);

        fishFileService.saveFishFiles(savedFish, fishDto.getImageFiles(), catchDate);
    }

    @Transactional
    public void deleteFish(int id) {
        Fish fish = fishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Файл не знайдено"));
        fishFileService.removeFilesByFish(fish);
        fishRepository.delete(fish);
    }
}