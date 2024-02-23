package ru.barikhashvili.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.barikhashvili.entities.specs.CameraSensorEntity;

import java.util.List;

public interface CameraSensorRepository extends JpaRepository<CameraSensorEntity, Integer> {
    List<CameraSensorEntity> findAllByOrderByMegapixelsAsc();
}
