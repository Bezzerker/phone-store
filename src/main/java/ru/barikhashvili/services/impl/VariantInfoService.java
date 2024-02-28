package ru.barikhashvili.services.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.barikhashvili.dto.PhoneVariantDTO;
import ru.barikhashvili.entities.PhoneVariantEntity;
import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.exceptions.ResourceNotFoundException;
import ru.barikhashvili.repositories.PhoneVariantRepository;
import ru.barikhashvili.services.EntityExtractor;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class VariantInfoService implements EntityExtractor<PhoneVariantDTO, PhoneVariantEntity, Long> {
    PhoneVariantRepository phoneVariantRepository;

    /**
     * Извлекает существующую hibernate сущность варианта телефона из базы данных по его идентификатору и
     * возвращает её.
     *
     * @param id идентификатор hibernate сущности варианта телефона, которую нужно получить.
     * @return объект PhoneVariantEntity, представляющий таблицу варианта телефона из базы данных.
     * @throws ResourceNotFoundException если вариант телефона с указанным идентификатором не найден.
     */
    @Override
    public PhoneVariantEntity getPersistentEntityById(Long id) {
        return phoneVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phone variant not found"));
    }

    /**
     * Извлекает существующую hibernate сущность варианта телефона из базы данных по полученному DTO объекту.
     *
     * @param phoneVariantDTO объект передачи данных, содержащий сведения о варианте телефона.
     * @return объект PhoneVariantEntity, представляющий таблицу варианта телефона из базы данных.
     * @throws ResourceNotFoundException если вариант телефона по entityDTO не найден.
     * @throws InsufficientDataException если entityDTO равняется null или данных
     *                                   для извлечения варианта телефона недостаточно.
     */
    @Override
    public PhoneVariantEntity getPersistentEntity(PhoneVariantDTO phoneVariantDTO) {
        if (phoneVariantDTO == null || phoneVariantDTO.getId() == null) {
            throw new InsufficientDataException("Phone variant data missing");
        }
        return phoneVariantRepository.findById(phoneVariantDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Phone variant not found"));
    }
}
