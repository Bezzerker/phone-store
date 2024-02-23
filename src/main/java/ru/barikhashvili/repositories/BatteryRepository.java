package ru.barikhashvili.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.barikhashvili.entities.specs.BatteryEntity;

import java.util.List;

public interface BatteryRepository extends JpaRepository<BatteryEntity, Integer> {
    List<BatteryEntity> findAllByOrderByCapacity();
}
