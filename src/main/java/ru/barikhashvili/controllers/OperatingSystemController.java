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
import ru.barikhashvili.dto.specs.OperatingSystemDTO;
import ru.barikhashvili.services.CrudService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operating-systems/")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class OperatingSystemController {
    CrudService<OperatingSystemDTO, Integer> operatingSystemService;

    @PostMapping
    public ResponseEntity<OperatingSystemDTO> handlerAddOperatingSystem(@RequestBody OperatingSystemDTO operatingSystemDTO) {
        var persistentOperatingSystem = operatingSystemService.saveEntity(operatingSystemDTO);
        var newOperatingSystemURI = UriComponentsBuilder
                .fromPath("/api/v1/operating-systems/{id}")
                .build(java.util.Map.of("id", persistentOperatingSystem.getId()));

        log.info("Adding a phone operating system with ID: {}", operatingSystemDTO.getId());

        return ResponseEntity
                .created(newOperatingSystemURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(persistentOperatingSystem);
    }

    @GetMapping("{id}")
    @Transactional
    public ResponseEntity<OperatingSystemDTO> handleGetOperatingSystem(@PathVariable Integer id) {
        var operatingSystemDTO = operatingSystemService.getEntityById(id);

        log.info("Received phone operating system with ID: {}", operatingSystemDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(operatingSystemDTO);
    }

    @GetMapping
    public ResponseEntity<List<OperatingSystemDTO>> handleGetAllOperatingSystems() {
        var allOperatingSystemsDTO = operatingSystemService.getAllEntities();

        log.info("Received a complete list of all phone operating systems");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allOperatingSystemsDTO);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<OperatingSystemDTO> handleDeleteOperatingSystem(@PathVariable Integer id) {
        var removedOperatingSystemDTO = operatingSystemService.removeEntityById(id);

        log.info("Removed phone operating system with ID: {}", removedOperatingSystemDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(removedOperatingSystemDTO);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<OperatingSystemDTO> handleEditOperatingSystem(
            @PathVariable Integer id,
            @RequestBody OperatingSystemDTO operatingSystemDTO) {
        var editedOperatingSystemDTO = operatingSystemService.editEntity(id, operatingSystemDTO);

        log.info("Operating system details have been completely changed. Operating system ID: {}", operatingSystemDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedOperatingSystemDTO);
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<OperatingSystemDTO> handleEditPartOfOperatingSystem(
            @PathVariable Integer id,
            @RequestBody OperatingSystemDTO operatingSystemDTO) {
        var editedOperatingSystemDTO = operatingSystemService.editPartOfEntity(id, operatingSystemDTO);

        log.info("Partially changed operating system data. Operating system ID: {}", operatingSystemDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedOperatingSystemDTO);
    }
}
