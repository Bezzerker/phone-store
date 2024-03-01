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
import ru.barikhashvili.dto.specs.CameraSensorDTO;
import ru.barikhashvili.services.CrudService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/camera-sensors/")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CameraSensorController {
    CrudService<CameraSensorDTO, Integer> cameraSensorService;

    @PostMapping
    public ResponseEntity<CameraSensorDTO> handleAddCameraSensor(@RequestBody CameraSensorDTO cameraSensorDTO) {
        var persistentCameraSensor = cameraSensorService.saveEntity(cameraSensorDTO);
        var newCameraSensorURI = UriComponentsBuilder
                .fromPath("/api/v1/camera-sensors/{id}")
                .build(java.util.Map.of("id", persistentCameraSensor.getId()));

        log.info("Adding a camera sensor with ID: {}", persistentCameraSensor.getId());

        return ResponseEntity
                .created(newCameraSensorURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(persistentCameraSensor);
    }

    @GetMapping("{id}")
    @Transactional
    public ResponseEntity<CameraSensorDTO> handleGetCameraSensor(@PathVariable Integer id) {
        var cameraSensorDTO = cameraSensorService.getEntityById(id);

        log.info("Received phone camera sensor with ID: {}", cameraSensorDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(cameraSensorDTO);
    }

    @GetMapping
    public ResponseEntity<List<CameraSensorDTO>> handleGetAllCameraSensors() {
        var allCameraSensorsDTO = cameraSensorService.getAllEntities();

        log.info("Received a complete list of all phone camera sensors");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allCameraSensorsDTO);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<CameraSensorDTO> handleDeleteCameraSensor(@PathVariable Integer id) {
        var removedCameraSensorDTO = cameraSensorService.removeEntityById(id);

        log.info("Removed phone camera sensor with ID: {}", removedCameraSensorDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(removedCameraSensorDTO);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<CameraSensorDTO> handleEditCameraSensor(
            @PathVariable Integer id,
            @RequestBody CameraSensorDTO cameraSensorDTO) {
        var editedCameraSensorDTO = cameraSensorService.editEntity(id, cameraSensorDTO);

        log.info("Camera sensor details have been completely changed. Camera sensor ID: {}", editedCameraSensorDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedCameraSensorDTO);
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<CameraSensorDTO> handleEditPartOfCameraSensor(
            @PathVariable Integer id,
            @RequestBody CameraSensorDTO cameraSensorDTO) {
        var editedCameraSensorDTO = cameraSensorService.editPartOfEntity(id, cameraSensorDTO);

        log.info("Partially changed camera sensor data. Camera sensor ID: {}", editedCameraSensorDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedCameraSensorDTO);
    }
}
