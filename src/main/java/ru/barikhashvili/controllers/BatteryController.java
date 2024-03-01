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
import ru.barikhashvili.dto.specs.BatteryDTO;
import ru.barikhashvili.services.CrudService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/batteries/")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BatteryController {
    CrudService<BatteryDTO, Integer> batteryService;

    @PostMapping
    public ResponseEntity<BatteryDTO> handleAddBattery(@RequestBody BatteryDTO batteryDTO) {
        var persistentBattery = batteryService.saveEntity(batteryDTO);
        var newBatteryURI = UriComponentsBuilder
                .fromPath("/api/v1/batteries/{id}")
                .build(java.util.Map.of("id", persistentBattery.getId()));

        log.info("Adding a phone battery with ID: {}", batteryDTO.getId());

        return ResponseEntity
                .created(newBatteryURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(persistentBattery);
    }

    @GetMapping("{id}")
    @Transactional
    public ResponseEntity<BatteryDTO> handleGetBattery(@PathVariable Integer id) {
        var batteryDTO = batteryService.getEntityById(id);

        log.info("Received phone battery with ID: {}", batteryDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(batteryDTO);
    }

    @GetMapping
    public ResponseEntity<List<BatteryDTO>> handleGetAllBatteries() {
        var allBatteriesDTO = batteryService.getAllEntities();

        log.info("Received a complete list of all phone batteries");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allBatteriesDTO);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<BatteryDTO> handleDeleteBattery(@PathVariable Integer id) {
        var removedBatteryDTO = batteryService.removeEntityById(id);

        log.info("Removed phone battery with ID: {}", removedBatteryDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(removedBatteryDTO);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<BatteryDTO> handleEditBattery(
            @PathVariable Integer id,
            @RequestBody BatteryDTO batteryDTO) {
        var editedBatteryDTO = batteryService.editEntity(id, batteryDTO);

        log.info("Battery details have been completely changed. Battery ID: {}", editedBatteryDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedBatteryDTO);
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<BatteryDTO> handleEditPartOfBattery(
            @PathVariable Integer id,
            @RequestBody BatteryDTO batteryDTO) {
        var editedBatteryDTO = batteryService.editPartOfEntity(id, batteryDTO);

        log.info("Partially changed battery data. Battery ID: {}", editedBatteryDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedBatteryDTO);
    }
}
