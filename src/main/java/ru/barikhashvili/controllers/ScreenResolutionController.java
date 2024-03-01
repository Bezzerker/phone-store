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
import ru.barikhashvili.dto.specs.ResolutionDTO;
import ru.barikhashvili.services.CrudService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/screen-resolutions/")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScreenResolutionController {
    CrudService<ResolutionDTO, Integer> screenResolutionService;

    @PostMapping
    public ResponseEntity<ResolutionDTO> handleAddScreenResolution(@RequestBody ResolutionDTO screenResolutionDTO) {
        var persistentScreenResolution = screenResolutionService.saveEntity(screenResolutionDTO);
        var newScreenResolutionURI = UriComponentsBuilder
                .fromPath("/api/v1/screen-resolutions/{id}")
                .build(java.util.Map.of("id", persistentScreenResolution.getId()));

        log.info("Adding a display resolution with ID: {}", persistentScreenResolution.getId());

        return ResponseEntity
                .created(newScreenResolutionURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(persistentScreenResolution);
    }

    @GetMapping("{id}")
    @Transactional
    public ResponseEntity<ResolutionDTO> handleGetScreenResolution(@PathVariable Integer id) {
        var screenResolutionDTO = screenResolutionService.getEntityById(id);

        log.info("Received display resolution with ID: {}", screenResolutionDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(screenResolutionDTO);
    }

    @GetMapping
    public ResponseEntity<List<ResolutionDTO>> handleGetAllScreenResolutions() {
        var allScreenResolutionsDTO = screenResolutionService.getAllEntities();

        log.info("Received a complete list of all display resolutions");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allScreenResolutionsDTO);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<ResolutionDTO> handleDeleteScreenResolution(@PathVariable Integer id) {
        var removedResolutionDTO = screenResolutionService.removeEntityById(id);

        log.info("Removed display resolution with ID: {}", removedResolutionDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(removedResolutionDTO);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<ResolutionDTO> handleEditScreenResolution(
            @PathVariable Integer id,
            @RequestBody ResolutionDTO screenResolutionDTO) {
        var editedResolutionDTO = screenResolutionService.editEntity(id, screenResolutionDTO);

        log.info("Display resolution details have been completely changed. Resolution ID: {}", editedResolutionDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedResolutionDTO);
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<ResolutionDTO> handleEditPartOfScreenResolution(
            @PathVariable Integer id,
            @RequestBody ResolutionDTO screenResolutionDTO) {
        var editedResolutionDTO = screenResolutionService.editPartOfEntity(id, screenResolutionDTO);

        log.info("Partially changed display resolution data. Resolution ID: {}", editedResolutionDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedResolutionDTO);
    }
}
