package ru.barikhashvili.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.barikhashvili.entities.specs.OperatingSystemEntity;

import java.util.List;

public interface OperatingSystemRepository extends JpaRepository<OperatingSystemEntity, Integer> {
    List<OperatingSystemEntity> findAllByOrderByNameAscVersionAsc();
}
