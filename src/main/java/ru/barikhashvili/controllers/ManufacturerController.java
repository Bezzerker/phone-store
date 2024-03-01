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
import ru.barikhashvili.dto.specs.ManufacturerDTO;
import ru.barikhashvili.services.CrudService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manufacturers/")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class ManufacturerController {
    CrudService<ManufacturerDTO, Integer> manufacturerService;

    @PostMapping
    public ResponseEntity<ManufacturerDTO> handleAddManufacturer(@RequestBody ManufacturerDTO manufacturerDTO) {
        var persistentManufacturer = manufacturerService.saveEntity(manufacturerDTO);
        var newManufacturerURI = UriComponentsBuilder
                .fromPath("/api/v1/manufacturers/{id}")
                .build(java.util.Map.of("id", persistentManufacturer.getId()));

        log.info("Adding a phone manufacturer with ID: {}", manufacturerDTO.getId());

        return ResponseEntity
                .created(newManufacturerURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(persistentManufacturer);
    }

    @GetMapping("{id}")
    @Transactional
    public ResponseEntity<ManufacturerDTO> handleGetManufacturer(@PathVariable Integer id) {
        var manufacturerDTO = manufacturerService.getEntityById(id);

        log.info("Received phone manufacturer with ID: {}", manufacturerDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(manufacturerDTO);
    }

    @GetMapping
    public ResponseEntity<List<ManufacturerDTO>> handleGetAllManufacturers() {
        var allManufacturersDTO = manufacturerService.getAllEntities();

        log.info("Received a complete list of all phone manufacturers");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allManufacturersDTO);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<ManufacturerDTO> handleDeleteManufacturer(@PathVariable Integer id) {
        var removedManufacturerDTO = manufacturerService.removeEntityById(id);

        log.info("Removed phone manufacturers with ID: {}", removedManufacturerDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(removedManufacturerDTO);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<ManufacturerDTO> handleEditManufacturer(
            @PathVariable Integer id,
            @RequestBody ManufacturerDTO manufacturerDTO) {
        var editedManufacturerDTO = manufacturerService.editEntity(id, manufacturerDTO);

        log.info("Manufacturer details have been completely changed. Manufacturer ID: {}", manufacturerDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedManufacturerDTO);
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<ManufacturerDTO> handleEditPartOfManufacturer(
            @PathVariable Integer id,
            @RequestBody ManufacturerDTO manufacturerDTO) {
        var editedManufacturerDTO = manufacturerService.editPartOfEntity(id, manufacturerDTO);

        log.info("Partially changed manufacturer data. Manufacturer ID: {}", editedManufacturerDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedManufacturerDTO);
    }
}
