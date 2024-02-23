package ru.barikhashvili.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.barikhashvili.entities.ResolutionEntity;

import java.util.List;

public interface ScreenResolutionRepository extends JpaRepository<ResolutionEntity, Integer> {
    List<ResolutionEntity> findAllByOrderByHorizontalPixelsAscVerticalPixelsAsc();
}
