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
import ru.barikhashvili.dto.PhoneDTO;
import ru.barikhashvili.services.CrudService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/phones/")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PhoneController {
    CrudService<PhoneDTO, Long> phoneService;

    @PostMapping
    public ResponseEntity<PhoneDTO> handleAddPhone(@RequestBody PhoneDTO phoneDTO) {
        var persistentPhone = phoneService.saveEntity(phoneDTO);
        var newPhoneURI = UriComponentsBuilder
                .fromPath("/api/v1/phones/{id}")
                .build(java.util.Map.of("id", persistentPhone.getId()));
        log.info("Adding a phone data with ID: {}", phoneDTO.getId());
        return ResponseEntity
                .created(newPhoneURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(persistentPhone);
    }

    @GetMapping("{id}")
    @Transactional
    public ResponseEntity<PhoneDTO> handleGetPhone(@PathVariable Long id) {
        var phoneDTO = phoneService.getEntityById(id);

        log.info("Received phone data with ID: {}", phoneDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(phoneDTO);
    }

    @GetMapping
    public ResponseEntity<List<PhoneDTO>> handleGetAllPhones() {
        var allPhonesDTO = phoneService.getAllEntities();

        log.info("Received a complete list of all phones");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allPhonesDTO);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<PhoneDTO> handleDeletePhone(@PathVariable Long id) {
        var removedPhoneDTO = phoneService.removeEntityById(id);

        log.info("Removed phone data with ID: {}", removedPhoneDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(removedPhoneDTO);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<PhoneDTO> handleEditPhone(
            @PathVariable Long id,
            @RequestBody PhoneDTO phoneDTO) {
        var editedPhoneDTO = phoneService.editEntity(id, phoneDTO);

        log.info("Phone details have been completely changed. Phone ID: {}", editedPhoneDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedPhoneDTO);
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<PhoneDTO> handleEditPartOfPhone(
            @PathVariable Long id,
            @RequestBody PhoneDTO phoneDTO) {
        var editedPhoneDTO = phoneService.editPartOfEntity(id, phoneDTO);

        log.info("Partially changed phone data. Phone ID: {}", editedPhoneDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedPhoneDTO);
    }
}
