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
import ru.barikhashvili.dto.specs.ProcessorDTO;
import ru.barikhashvili.services.CrudService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/processors/")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProcessorController {
    CrudService<ProcessorDTO, Integer> processorService;

    @PostMapping
    public ResponseEntity<ProcessorDTO> handleAddProcessor(@RequestBody ProcessorDTO processorDTO) {
        var persistentProcessor = processorService.saveEntity(processorDTO);
        var newProcessorURI = UriComponentsBuilder
                .fromPath("/api/v1/processors/{id}")
                .build(java.util.Map.of("id", persistentProcessor.getId()));

        log.info("Adding a phone processor with ID: {}", persistentProcessor.getId());

        return ResponseEntity
                .created(newProcessorURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(persistentProcessor);
    }

    @GetMapping("{id}")
    @Transactional
    public ResponseEntity<ProcessorDTO> handleGetProcessor(@PathVariable Integer id) {
        var processorDTO = processorService.getEntityById(id);

        log.info("Received phone processor with ID: {}", processorDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(processorDTO);
    }

    @GetMapping
    public ResponseEntity<List<ProcessorDTO>> handleGetAllProcessors() {
        var allProcessorsDTO = processorService.getAllEntities();

        log.info("Received a complete list of all phone processors");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allProcessorsDTO);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<ProcessorDTO> handleDeleteProcessor(@PathVariable Integer id) {
        var removedProcessorDTO = processorService.removeEntityById(id);

        log.info("Removed phone processor with ID: {}", removedProcessorDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(removedProcessorDTO);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<ProcessorDTO> handleEditProcessor(
            @PathVariable Integer id,
            @RequestBody ProcessorDTO processorDTO) {
        var editedProcessorDTO = processorService.editEntity(id, processorDTO);

        log.info("Processor details have been completely changed. Processor ID: {}", editedProcessorDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedProcessorDTO);
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<ProcessorDTO> handleEditPartOfProcessor(
            @PathVariable Integer id,
            @RequestBody ProcessorDTO processorDTO) {
        var editedProcessorDTO = processorService.editPartOfEntity(id, processorDTO);

        log.info("Partially changed processor data. Processor ID: {}", editedProcessorDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedProcessorDTO);
    }
}
