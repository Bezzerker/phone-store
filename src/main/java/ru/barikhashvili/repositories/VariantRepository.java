package ru.barikhashvili.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.barikhashvili.entities.specs.VariantEntity;

import java.util.List;

public interface VariantRepository extends JpaRepository<VariantEntity, Integer> {
    List<VariantEntity> findAllByOrderByRamSizeAscRomSizeAsc();
}
