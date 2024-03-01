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
import ru.barikhashvili.dto.VariantDTO;
import ru.barikhashvili.services.CrudService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/variants/")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class VariantController {
    CrudService<VariantDTO, Integer> variantService;

    @PostMapping
    public ResponseEntity<VariantDTO> handleAddVariant(@RequestBody VariantDTO variantDTO) {
        var persistentVariant = variantService.saveEntity(variantDTO);
        var newVariantURI = UriComponentsBuilder
                .fromPath("/api/v1/variants/{id}")
                .build(java.util.Map.of("id", persistentVariant.getId()));

        log.info("Adding a phone variant with ID: {}", persistentVariant.getId());

        return ResponseEntity
                .created(newVariantURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(persistentVariant);
    }

    @GetMapping("{id}")
    @Transactional
    public ResponseEntity<VariantDTO> handleGetVariant(@PathVariable Integer id) {
        var variantDTO = variantService.getEntityById(id);

        log.info("Received phone variant with ID: {}", variantDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(variantDTO);
    }

    @GetMapping
    public ResponseEntity<List<VariantDTO>> handleGetAllVariants() {
        var allVariantsDTO = variantService.getAllEntities();

        log.info("Received a complete list of all phone variants");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allVariantsDTO);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<VariantDTO> handleDeleteVariant(@PathVariable Integer id) {
        var removedVariantDTO = variantService.removeEntityById(id);

        log.info("Removed phone variant with ID: {}", removedVariantDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(removedVariantDTO);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<VariantDTO> handleEditVariant(
            @PathVariable Integer id,
            @RequestBody VariantDTO variantDTO) {
        var editedVariantDTO = variantService.editEntity(id, variantDTO);

        log.info("Phone variant details have been completely changed. Variant ID: {}", editedVariantDTO.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedVariantDTO);
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<VariantDTO> handleEditPartOfVariant(
            @PathVariable Integer id,
            @RequestBody VariantDTO variantDTO) {
        var editedVariantDTO = variantService.editPartOfEntity(id, variantDTO);

        log.info("Partially changed phone variant data. Variant ID: {}", editedVariantDTO.getId());


        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(editedVariantDTO);
    }
}
