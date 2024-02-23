package ru.barikhashvili.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.barikhashvili.entities.specs.PhoneSpecificationEntity;

import java.util.List;

public interface PhoneSpecificationRepository extends JpaRepository<PhoneSpecificationEntity, Long> {
    List<PhoneSpecificationEntity> findAllByOrderByIdAsc();
}
