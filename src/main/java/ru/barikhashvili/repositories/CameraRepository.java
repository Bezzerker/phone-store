package ru.barikhashvili.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.barikhashvili.entities.specs.CameraEntity;

import java.util.List;

public interface CameraRepository extends JpaRepository<CameraEntity, Long> {
    List<CameraEntity> findAllByOrderByIdAsc();
}
