package ru.barikhashvili.controllers;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.barikhashvili.dto.specs.CountryDTO;
import ru.barikhashvili.services.CrudService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/countries/")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CountryController {
    CrudService<CountryDTO, Integer> countryService;

    @PostMapping
    public ResponseEntity<CountryDTO> handleAddCountry(@RequestBody CountryDTO countryDTO) {
        var persistentCountry = countryService.saveEntity(countryDTO);
        var newCountryURI = UriComponentsBuilder
                .fromPath("/api/v1/countries/{id}")
                .build(java.util.Map.of("id", persistentCountry.getId()));

        log.info("Adding a manufacturer country with ID: {}", countryDTO.getId());

        return ResponseEntity
                .created(newCountryURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(persistentCountry);
    }

    @GetMapping("{id}")
    @Transactional
    public ResponseEntity<CountryDTO> handleGetCountry(@PathVariable Integer id) {
        var countryDTO = countryService.getEntityById(id);

        log.info("Received manufacturer country with ID: {}", countryDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(countryDTO);
    }

    @GetMapping
    public ResponseEntity<List<CountryDTO>> handleGetAllCountries() {
        var allCountriesDTO = countryService.getAllEntities();

        log.info("Received a complete list of all manufacturer countries");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allCountriesDTO);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<CountryDTO> handleDeleteCountry(@PathVariable Integer id) {
        var removedCountryDTO = countryService.removeEntityById(id);

        log.info("Removed manufacturer country with ID: {}", removedCountryDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(removedCountryDTO);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<CountryDTO> handleEditCountry(
            @PathVariable Integer id,
            @RequestBody CountryDTO countryDTO) {
        var editedCountryDTO = countryService.editEntity(id, countryDTO);

        log.info("Country details have been completely changed. Country ID: {}", countryDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedCountryDTO);
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<CountryDTO> handleEditPartOfCountry(
            @PathVariable Integer id,
            @RequestBody CountryDTO countryDTO) {
        var editedCountryDTO = countryService.editPartOfEntity(id, countryDTO);

        log.info("Partially changed country data. Country ID: {}", editedCountryDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedCountryDTO);
    }
}
