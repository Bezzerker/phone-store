package ru.barikhashvili.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.barikhashvili.entities.specs.ProcessorEntity;

import java.util.List;

public interface ProcessorRepository extends JpaRepository<ProcessorEntity, Integer> {
    List<ProcessorEntity> findAllByOrderByTechnologyNodeAscModelAsc();
}
