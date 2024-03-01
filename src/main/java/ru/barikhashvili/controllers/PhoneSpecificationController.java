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
import ru.barikhashvili.dto.specs.PhoneSpecificationDTO;
import ru.barikhashvili.services.CrudService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/specifications/")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PhoneSpecificationController {
    CrudService<PhoneSpecificationDTO, Long> phoneSpecificationService;

    @PostMapping
    public ResponseEntity<PhoneSpecificationDTO> handleAddPhoneSpecification(@RequestBody PhoneSpecificationDTO phoneSpecificationDTO) {
        var persistentPhoneSpecification = phoneSpecificationService.saveEntity(phoneSpecificationDTO);
        var newPhoneSpecificationURI = UriComponentsBuilder
                .fromPath("/api/v1/specifications/{id}")
                .build(java.util.Map.of("id", persistentPhoneSpecification.getId()));

        log.info("Adding a phone specifications with ID: {}", persistentPhoneSpecification.getId());

        return ResponseEntity
                .created(newPhoneSpecificationURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(persistentPhoneSpecification);
    }

    @GetMapping("{id}")
    @Transactional
    public ResponseEntity<PhoneSpecificationDTO> handleGetPhoneSpecification(@PathVariable Long id) {
        var phoneSpecificationDTO = phoneSpecificationService.getEntityById(id);

        log.info("Received phone specification with ID: {}", phoneSpecificationDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(phoneSpecificationDTO);
    }

    @GetMapping
    public ResponseEntity<List<PhoneSpecificationDTO>> handleGetAllPhoneSpecifications() {
        var allPhoneSpecificationsDTO = phoneSpecificationService.getAllEntities();

        log.info("Received a complete list of all phone specifications");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allPhoneSpecificationsDTO);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<PhoneSpecificationDTO> handleDeletePhoneSpecification(@PathVariable Long id) {
        var removedPhoneSpecificationDTO = phoneSpecificationService.removeEntityById(id);

        log.info("Removed phone specifications with ID: {}", removedPhoneSpecificationDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(removedPhoneSpecificationDTO);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<PhoneSpecificationDTO> handleEditPhoneSpecification(
            @PathVariable Long id,
            @RequestBody PhoneSpecificationDTO phoneSpecificationDTO) {
        var editedPhoneSpecificationDTO = phoneSpecificationService.editEntity(id, phoneSpecificationDTO);

        log.info("Specification details have been completely changed. Specification ID: {}", editedPhoneSpecificationDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedPhoneSpecificationDTO);
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<PhoneSpecificationDTO> handleEditPartOfPhoneSpecification(
            @PathVariable Long id,
            @RequestBody PhoneSpecificationDTO phoneSpecificationDTO) {
        var editedPhoneSpecificationDTO = phoneSpecificationService.editPartOfEntity(id, phoneSpecificationDTO);

        log.info("Partially changed specification data. Specification ID: {}", editedPhoneSpecificationDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedPhoneSpecificationDTO);
    }
}
