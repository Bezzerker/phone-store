package ru.barikhashvili.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.barikhashvili.entities.PhoneEntity;

import java.util.List;

public interface PhoneRepository extends JpaRepository<PhoneEntity, Long> {
    List<PhoneEntity> findAllByOrderByModelAsc();
}
