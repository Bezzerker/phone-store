package ru.barikhashvili.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.barikhashvili.entities.specs.CountryEntity;

import java.util.List;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<CountryEntity, Integer> {
    List<CountryEntity> findAllByOrderByNameAsc();
    Optional<CountryEntity> findByName(String name);
}
