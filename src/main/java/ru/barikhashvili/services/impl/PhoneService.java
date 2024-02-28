package ru.barikhashvili.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barikhashvili.dto.PhoneDTO;
import ru.barikhashvili.dto.specs.ManufacturerDTO;
import ru.barikhashvili.dto.specs.PhoneSpecificationDTO;
import ru.barikhashvili.entities.PhoneEntity;
import ru.barikhashvili.entities.specs.ManufacturerEntity;
import ru.barikhashvili.entities.specs.PhoneSpecificationEntity;
import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.exceptions.ResourceNotFoundException;
import ru.barikhashvili.repositories.PhoneRepository;
import ru.barikhashvili.services.CrudService;
import ru.barikhashvili.services.EntityExtractor;
import ru.barikhashvili.services.utils.DtoToEntityConverter;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PhoneService implements CrudService<PhoneDTO, Long>,
        EntityExtractor<PhoneDTO, PhoneEntity, Long> {
    PhoneRepository phoneRepository;
    EntityExtractor<ManufacturerDTO, ManufacturerEntity, Integer> manufacturerExtractor;
    EntityExtractor<PhoneSpecificationDTO, PhoneSpecificationEntity, Long> specificationExtractor;
    DtoToEntityConverter converter;
    ModelMapper mapper;
    ModelMapper nullableMapper;

    public PhoneService(PhoneRepository phoneRepository,
                        EntityExtractor<ManufacturerDTO, ManufacturerEntity, Integer> manufacturerExtractor,
                        EntityExtractor<PhoneSpecificationDTO, PhoneSpecificationEntity, Long> specificationExtractor,
                        DtoToEntityConverter converter,
                        @Qualifier("modelMapper") ModelMapper mapper,
                        @Qualifier("nullableModelMapper") ModelMapper nullableMapper) {
        this.phoneRepository = phoneRepository;
        this.manufacturerExtractor = manufacturerExtractor;
        this.specificationExtractor = specificationExtractor;
        this.mapper = mapper;
        this.nullableMapper = nullableMapper;

        this.converter = converter;
    }

    /**
     * Обновляет сущность PhoneSpecificationEntity новыми данными из объекта PhoneSpecificationDTO.
     * Этот метод извлекает идентификаторы компонентов (операционная система, дисплей, процессор, аккумулятор)
     * из DTO, находит сущности Hibernate, используя соответствующие "extractor"-ы, а затем заменяет ими
     * связанные с PhoneSpecificationEntity компоненты их новыми версиями из DTO.
     *
     * @param entity сущность, которую необходимо обновить.
     * @param dto    объект DTO, содержащий id новых данных.
     * @throws InsufficientDataException если новая сущность, полученная через dto, равна null.
     * @throws ResourceNotFoundException если сущность не найдена по id из dto.
     */
    private void getNewDataAndReplaceCurrent(PhoneEntity entity, PhoneDTO dto) {
        var manufacturerDTO = dto.getManufacturer();
        var persistentManufacturer = manufacturerExtractor.getPersistentEntity(manufacturerDTO);
        entity.setManufacturer(persistentManufacturer);

        var specificationDTO = dto.getSpecification();
        var persistentSpecification = specificationExtractor.getPersistentEntity(specificationDTO);
        entity.setSpecification(persistentSpecification);
    }

    /**
     * Добавляет новый телефон в базу данных и возвращает информацию о добавленном телефоне.
     * Также метод добавляет записи о всех указанных вариантах телефона в таблицу phones_variants.
     * Этот метод требует, чтобы полученный в аргументе phoneDTO содержал полную информацию
     * о добавляемом телефоне.
     *
     * @param phoneDTO объект со сведениями о добавляемом телефоне (объект должен содержать все сведения).
     * @return объект PhoneDTO с полной информацией о добавленном телефоне из базы данных.
     * @throws InsufficientDataException если данные о добавляемом телефоне отсутствуют или указаны частично.
     */
    @Override
    @Transactional
    public PhoneDTO saveEntity(PhoneDTO phoneDTO) {
        var transientPhone = mapper.map(phoneDTO, PhoneEntity.class);

        getNewDataAndReplaceCurrent(transientPhone, phoneDTO);

        var phoneVariants = phoneDTO.getPhoneVariants();
        var variants = converter.phoneVariantDtoListToEntities(phoneVariants, transientPhone);
        transientPhone.setPhoneVariants(variants);

        var persistentPhone = phoneRepository.save(transientPhone);

        return mapper.map(persistentPhone, PhoneDTO.class);
    }

    /**
     * Находит и возвращает данные о телефоне с указанным идентификатором.
     *
     * @param id уникальный идентификатор телефона, который необходимо найти
     * @return объект PhoneDTO с полной информацией о телефоне.
     * @throws ResourceNotFoundException если телефон с указанным id не найден в базе данных
     */
    @Override
    public PhoneDTO getEntityById(Long id) {
        var persistentPhone = getPersistentEntityById(id);
        return mapper.map(persistentPhone, PhoneDTO.class);
    }

    /**
     * Находит и возвращает список всех телефонов, отсортированный в алфавитном
     * порядке согласно названию модели телефона.
     *
     * @return список PhoneDTO с полными сведениями о всех телефонах из базы данных.
     */
    @Override
    @Transactional
    public List<PhoneDTO> getAllEntities() {
        var persistentPhones = phoneRepository.findAllByOrderByModelAsc();
        return persistentPhones.stream()
                .map(phone -> mapper.map(phone, PhoneDTO.class))
                .toList();
    }

    /**
     * Удаляет телефон с указанным id из базы данных и возвращает сведения об удаленном телефоне.
     *
     * @param id уникальный идентификатор телефона, который необходимо удалить.
     * @throws ResourceNotFoundException если телефон с указанным id не найден в базе данных.
     */
    @Override
    @Transactional
    public PhoneDTO removeEntityById(Long id) {
        var removedPhone = getEntityById(id);
        phoneRepository.deleteById(id);
        return removedPhone;
    }

    /**
     * Полностью обновляет информацию о телефоне в базе данных и возвращает объект с обновленной информацией.
     * Этот метод требует, чтобы полученный в аргументе phoneDTO содержал полную информацию об изменяемом телефоне.
     * Важно: Этот метод не позволяет изменять id телефона.
     *
     * @param id       уникальный идентификатор обновляемого телефона, данные которого необходимо изменить.
     * @param phoneDTO объект с новыми сведениями об изменяемом телефоне (Он должен содержать все сведения о телефоне).
     * @return объект PhoneDTO с обновленной информацией об измененном телефоне из базы данных.
     * @throws InsufficientDataException если данные об изменяемом телефоне отсутствуют.
     * @throws ResourceNotFoundException если изменяемый телефон не найден по id.
     */
    @Override
    @Transactional
    public PhoneDTO editEntity(Long id, PhoneDTO phoneDTO) {
        var persistentPhone = getPersistentEntityById(id);
        mapper.map(phoneDTO, persistentPhone);
        getNewDataAndReplaceCurrent(persistentPhone, phoneDTO);

        var entities = converter.phoneVariantDtoListToEntities(phoneDTO.getPhoneVariants(), persistentPhone);

        persistentPhone.setPhoneVariants(entities);

        return mapper.map(persistentPhone, PhoneDTO.class);
    }

    /**
     * Частично или полностью обновляет информацию о телефоне в базе данных и возвращает обновленную
     * информацию о телефоне. Если изменяемые данные пустые или не переданы, то изменения не происходят
     * и возвращается объект из базы данных по указанному идентификатору.
     * Важно: Этот метод не позволяет изменять id телефона.
     *
     * @param id       уникальный идентификатор обновляемого телефона, данные которого необходимо изменить.
     * @param phoneDTO объект с обновленными сведениями об изменяемом телефоне.
     * @return объект PhoneDTO с обновленной информацией об измененном телефоне из базы данных.
     * @throws ResourceNotFoundException если изменяемый телефон не найден по id.
     */
    @Override
    @Transactional
    public PhoneDTO editPartOfEntity(Long id, PhoneDTO phoneDTO) {
        var persistentPhone = getPersistentEntityById(id);
        nullableMapper.map(phoneDTO, persistentPhone);

        var manufacturerDTO = phoneDTO.getManufacturer();
        if (manufacturerDTO != null && manufacturerDTO.getId() != null) {
            var newManufacturerId = manufacturerDTO.getId();
            var persistentManufacturer = manufacturerExtractor.getPersistentEntityById(newManufacturerId);
            persistentPhone.setManufacturer(persistentManufacturer);
        }

        var specificationDTO = phoneDTO.getSpecification();
        if (specificationDTO != null && specificationDTO.getId() != null) {
            var newSpecificationId = specificationDTO.getId();
            var persistentSpecification = specificationExtractor.getPersistentEntityById(newSpecificationId);
            persistentPhone.setSpecification(persistentSpecification);
        }

        if (!phoneDTO.getPhoneVariants().isEmpty()) {
            var entities = converter.phoneVariantDtoListToEntities(phoneDTO.getPhoneVariants(), persistentPhone);
            persistentPhone.setPhoneVariants(entities);
        }

        return mapper.map(persistentPhone, PhoneDTO.class);
    }

    /**
     * Извлекает существующую hibernate сущность телефона из базы данных по его идентификатору и
     * возвращает её.
     *
     * @param id идентификатор hibernate сущности телефона, которую нужно получить.
     * @return объект PhoneEntity, представляющий таблицу телефона из базы данных.
     * @throws ResourceNotFoundException если телефон с указанным идентификатором не найден.
     */
    @Override
    public PhoneEntity getPersistentEntityById(Long id) {
        return phoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phone not found"));
    }

    /**
     * Извлекает существующую hibernate сущность телефона из базы данных по полученному DTO объекту.
     *
     * @param phoneDTO объект передачи данных, содержащий сведения о телефоне.
     * @return объект PhoneEntity, представляющий таблицу телефона из базы данных.
     * @throws ResourceNotFoundException если телефон по entityDTO не найден.
     * @throws InsufficientDataException если entityDTO равняется null или данных
     *                                   для извлечения телефона недостаточно.
     */
    @Override
    public PhoneEntity getPersistentEntity(PhoneDTO phoneDTO) {
        if (phoneDTO == null || phoneDTO.getId() == null) {
            throw new InsufficientDataException("Phone data missing");
        }
        return phoneRepository.findById(phoneDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Phone not found"));
    }
}
