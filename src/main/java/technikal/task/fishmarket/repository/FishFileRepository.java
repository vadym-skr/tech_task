package technikal.task.fishmarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishFile;

import java.util.List;

public interface FishFileRepository extends JpaRepository<FishFile, Integer> {
    List<FishFile> findByFish(Fish fish);
}
