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
import ru.barikhashvili.dto.specs.CameraDTO;
import ru.barikhashvili.services.CrudService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cameras/")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CameraController {
    CrudService<CameraDTO, Long> cameraService;

    @PostMapping
    public ResponseEntity<CameraDTO> handleAddCamera(@RequestBody CameraDTO cameraDTO) {
        var persistentCamera = cameraService.saveEntity(cameraDTO);
        var newCameraURI = UriComponentsBuilder
                .fromPath("/api/v1/cameras/{id}")
                .build(java.util.Map.of("id", persistentCamera.getId()));

        log.info("Adding a phone camera with ID: {}", persistentCamera.getId());

        return ResponseEntity
                .created(newCameraURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(persistentCamera);
    }

    @GetMapping("{id}")
    @Transactional
    public ResponseEntity<CameraDTO> handleGetCamera(@PathVariable Long id) {
        var cameraDTO = cameraService.getEntityById(id);

        log.info("Received phone camera with ID: {}", cameraDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(cameraDTO);
    }

    @GetMapping
    public ResponseEntity<List<CameraDTO>> handleGetAllCameras() {
        var allCamerasDTO = cameraService.getAllEntities();

        log.info("Received a complete list of all phone cameras");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allCamerasDTO);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<CameraDTO> handleDeleteCamera(@PathVariable Long id) {
        var removedCameraDTO = cameraService.removeEntityById(id);

        log.info("Removed phone camera with ID: {}", removedCameraDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(removedCameraDTO);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<CameraDTO> handleEditCamera(
            @PathVariable Long id,
            @RequestBody CameraDTO cameraDTO) {
        var editedCameraDTO = cameraService.editEntity(id, cameraDTO);

        log.info("Camera details have been completely changed. Camera ID: {}", editedCameraDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedCameraDTO);
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<CameraDTO> handleEditPartOfCamera(
            @PathVariable Long id,
            @RequestBody CameraDTO cameraDTO) {
        var editedCameraDTO = cameraService.editPartOfEntity(id, cameraDTO);

        log.info("Partially changed camera data. Camera ID: {}", editedCameraDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedCameraDTO);
    }
}
