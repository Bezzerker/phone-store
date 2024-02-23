package ru.barikhashvili.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.barikhashvili.entities.PhoneVariantEntity;

public interface PhoneVariantRepository extends JpaRepository<PhoneVariantEntity, Long> {
}
