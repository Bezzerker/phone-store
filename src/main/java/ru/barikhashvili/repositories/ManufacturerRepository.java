package ru.barikhashvili.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.barikhashvili.entities.specs.ManufacturerEntity;

import java.util.List;
import java.util.Optional;

public interface ManufacturerRepository extends JpaRepository<ManufacturerEntity, Integer> {
    List<ManufacturerEntity> findAllByOrderByNameAsc();

    Optional<ManufacturerEntity> findByName(String name);
}
