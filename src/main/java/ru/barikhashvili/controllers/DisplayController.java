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
import ru.barikhashvili.dto.specs.DisplayDTO;
import ru.barikhashvili.services.CrudService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/displays/")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DisplayController {
    CrudService<DisplayDTO, Integer> displayService;

    @PostMapping
    public ResponseEntity<DisplayDTO> handleAddDisplay(@RequestBody DisplayDTO displayDTO) {
        var persistentDisplay = displayService.saveEntity(displayDTO);
        var newDisplayURI = UriComponentsBuilder
                .fromPath("/api/v1/displays/{id}")
                .build(java.util.Map.of("id", persistentDisplay.getId()));

        log.info("Adding a phone display with ID: {}", displayDTO.getId());

        return ResponseEntity
                .created(newDisplayURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(persistentDisplay);
    }

    @GetMapping("{id}")
    @Transactional
    public ResponseEntity<DisplayDTO> handleGetDisplay(@PathVariable Integer id) {
        var displayDTO = displayService.getEntityById(id);

        log.info("Received phone display with ID: {}", displayDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(displayDTO);
    }

    @GetMapping
    public ResponseEntity<List<DisplayDTO>> handleGetAllDisplays() {
        var allDisplaysDTO = displayService.getAllEntities();

        log.info("Received a complete list of all phone displays");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allDisplaysDTO);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<DisplayDTO> handleDeleteDisplay(@PathVariable Integer id) {
        var removedDisplayDTO = displayService.removeEntityById(id);

        log.info("Removed phone display with ID: {}", removedDisplayDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(removedDisplayDTO);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<DisplayDTO> handleEditDisplay(
            @PathVariable Integer id,
            @RequestBody DisplayDTO displayDTO) {
        var editedDisplayDTO = displayService.editEntity(id, displayDTO);

        log.info("Display details have been completely changed. Display ID: {}", editedDisplayDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedDisplayDTO);
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<DisplayDTO> handleEditPartOfDisplay(
            @PathVariable Integer id,
            @RequestBody DisplayDTO displayDTO) {
        var editedDisplayDTO = displayService.editPartOfEntity(id, displayDTO);

        log.info("Partially changed display data. Display ID: {}", editedDisplayDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedDisplayDTO);
    }
}
