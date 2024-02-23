package ru.barikhashvili.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.barikhashvili.entities.specs.DisplayEntity;

import java.util.List;

public interface DisplayRepository extends JpaRepository<DisplayEntity, Integer> {
    List<DisplayEntity> findAllByOrderByDiagonalAscRefreshRateAsc();
}
