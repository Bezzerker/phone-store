package ru.barikhashvili.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barikhashvili.dto.specs.*;
import ru.barikhashvili.entities.specs.*;
import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.exceptions.ResourceNotFoundException;
import ru.barikhashvili.repositories.PhoneSpecificationRepository;
import ru.barikhashvili.services.CrudService;
import ru.barikhashvili.services.EntityExtractor;
import ru.barikhashvili.services.utils.DtoToEntityConverter;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PhoneSpecificationService implements CrudService<PhoneSpecificationDTO, Long>,
        EntityExtractor<PhoneSpecificationDTO, PhoneSpecificationEntity, Long> {
    PhoneSpecificationRepository phoneSpecificationRepository;
    EntityExtractor<OperatingSystemDTO, OperatingSystemEntity, Integer> operatingSystemExtractor;
    EntityExtractor<DisplayDTO, DisplayEntity, Integer> displayExtractor;
    EntityExtractor<ProcessorDTO, ProcessorEntity, Integer> processorExtractor;
    EntityExtractor<BatteryDTO, BatteryEntity, Integer> batteryExtractor;
    EntityExtractor<CameraDTO, CameraEntity, Long> cameraExtractor;
    DtoToEntityConverter converter;
    ModelMapper mapper;
    ModelMapper nullableMapper;

    public PhoneSpecificationService(PhoneSpecificationRepository phoneSpecificationRepository,
                                     EntityExtractor<OperatingSystemDTO, OperatingSystemEntity, Integer> operatingSystemExtractor,
                                     EntityExtractor<DisplayDTO, DisplayEntity, Integer> displayExtractor,
                                     EntityExtractor<ProcessorDTO, ProcessorEntity, Integer> processorExtractor,
                                     EntityExtractor<BatteryDTO, BatteryEntity, Integer> batteryExtractor,
                                     EntityExtractor<CameraDTO, CameraEntity, Long> cameraExtractor,
                                     DtoToEntityConverter converter,
                                     @Qualifier("modelMapper") ModelMapper mapper,
                                     @Qualifier("nullableModelMapper") ModelMapper nullableMapper) {
        this.phoneSpecificationRepository = phoneSpecificationRepository;

        this.operatingSystemExtractor = operatingSystemExtractor;
        this.displayExtractor = displayExtractor;
        this.processorExtractor = processorExtractor;
        this.batteryExtractor = batteryExtractor;
        this.cameraExtractor = cameraExtractor;
        this.converter = converter;

        this.mapper = mapper;
        this.nullableMapper = nullableMapper;
    }

    /**
     * Обновляет сущность PhoneSpecificationEntity новыми данными из объекта PhoneSpecificationDTO.
     * Этот метод извлекает идентификаторы компонентов (операционная система, дисплей, процессор, аккумулятор)
     * из DTO, находит сущности Hibernate, используя соответствующие "extractor"-ы, а затем заменяет ими
     * связанные с PhoneSpecificationEntity компоненты их новыми версиями из DTO.
     *
     * @param entity сущность, которую необходимо обновить.
     * @param dto объект DTO, содержащий id новых данных.
     * @throws InsufficientDataException если новая сущность, полученная через dto, равна null.
     * @throws ResourceNotFoundException если сущность не найдена по id из dto.
     */
    private void getNewDataAndReplaceCurrent(PhoneSpecificationEntity entity, PhoneSpecificationDTO dto) {
        var operatingSystemDTO = dto.getOperatingSystem();
        var persistentOperatingSystem = operatingSystemExtractor.getPersistentEntity(operatingSystemDTO);
        entity.setOperatingSystem(persistentOperatingSystem);

        var displayDTO = dto.getDisplay();
        var persistentDisplay = displayExtractor.getPersistentEntity(displayDTO);
        entity.setDisplay(persistentDisplay);

        var processorDTO = dto.getProcessor();
        var persistentProcessor = processorExtractor.getPersistentEntity(processorDTO);
        entity.setProcessor(persistentProcessor);

        var batteryDTO = dto.getBattery();
        var persistentBattery = batteryExtractor.getPersistentEntity(batteryDTO);
        entity.setBattery(persistentBattery);
    }

    /**
     * Добавляет новую характеристику телефона в базу данных и возвращает информацию о добавленной характеристике.
     * Этот метод требует, чтобы полученный в аргументе phoneSpecificationDTO содержал полную информацию
     * о добавляемой характеристике телефона.
     *
     * @param phoneSpecificationDTO объект со сведениями о добавляемой характеристике телефона (объект должен содержать все сведения).
     * @return объект PhoneSpecificationDTO с полной информацией о добавленной характеристике телефона из базы данных.
     * @throws InsufficientDataException если данные о добавляемой характеристике телефона отсутствуют или отсутствуют сведения
     *                                   об операционной системе, дисплее, процессоре или аккумуляторной батарее.
     */
    @Override
    @Transactional
    public PhoneSpecificationDTO saveEntity(PhoneSpecificationDTO phoneSpecificationDTO) {
        var transientPhoneSpecification = converter.phoneSpecDtoToEntity(phoneSpecificationDTO);

        getNewDataAndReplaceCurrent(transientPhoneSpecification, phoneSpecificationDTO);

        var persistentCameras = phoneSpecificationDTO.getCameras()
                .stream()
                .map(cameraExtractor::getPersistentEntity)
                .toList();
        transientPhoneSpecification.setCameras(persistentCameras);

        var persistentPhoneSpecification = phoneSpecificationRepository.save(transientPhoneSpecification);

        return mapper.map(persistentPhoneSpecification, PhoneSpecificationDTO.class);
    }

    /**
     * Находит и возвращает данные о характеристике телефона с указанным идентификатором.
     *
     * @param id уникальный идентификатор характеристики телефона, которую необходимо найти
     * @return объект PhoneSpecificationDTO с полной информацией о характеристике телефона.
     * @throws ResourceNotFoundException если характеристика телефона с указанным id не найдена в базе данных
     */
    @Override
    public PhoneSpecificationDTO getEntityById(Long id) {
        var persistentPhoneSpecification = getPersistentEntityById(id);
        return mapper.map(persistentPhoneSpecification, PhoneSpecificationDTO.class);
    }

    /**
     * Находит и возвращает список всех характеристик телефонов, отсортированный по возрастанию идентификаторов.
     *
     * @return список PhoneSpecificationDTO с полными сведениями о всех характеристиках телефонов из базы данных.
     */
    @Override
    @Transactional
    public List<PhoneSpecificationDTO> getAllEntities() {
        var persistentPhoneSpecifications = phoneSpecificationRepository.findAllByOrderByIdAsc();
        return persistentPhoneSpecifications.stream()
                .map(phoneSpecification -> mapper.map(phoneSpecification, PhoneSpecificationDTO.class))
                .toList();
    }

    /**
     * Удаляет характеристику телефона с указанным id из базы данных и возвращает сведения об удаленной характеристике.
     *
     * @param id уникальный идентификатор характеристики телефона, которую необходимо удалить.
     * @throws ResourceNotFoundException если характеристика телефона с указанным id не найдена в базе данных.
     */
    @Override
    @Transactional
    public PhoneSpecificationDTO removeEntityById(Long id) {
        var removedPhoneSpecification = getEntityById(id);
        phoneSpecificationRepository.deleteById(id);
        return removedPhoneSpecification;
    }

    /**
     * Полностью обновляет информацию о характеристике телефона в базе данных и возвращает объект с обновленной информацией.
     * Этот метод требует, чтобы полученный в аргументе phoneSpecificationDTO содержал полную информацию об изменяемой характеристике телефона.
     * Важно: Этот метод не позволяет изменять id характеристики телефона.
     *
     * @param id                    уникальный идентификатор обновляемой характеристики телефона, данные которой необходимо изменить.
     * @param phoneSpecificationDTO объект с новыми сведениями об изменяемой характеристике телефона (Он должен содержать все сведения о характеристике телефона).
     * @return объект PhoneSpecificationDTO с обновленной информацией об измененной характеристике телефона из базы данных.
     * @throws ResourceNotFoundException если изменяемая характеристика телефона не найдена по id.
     * @throws InsufficientDataException если данные об изменяемой характеристике телефона отсутствуют или отсутствуют сведения
     *                                   об операционной системе, дисплее, процессоре или аккумуляторной батарее.
     */
    @Override
    @Transactional
    public PhoneSpecificationDTO editEntity(Long id, PhoneSpecificationDTO phoneSpecificationDTO) {
        var persistentPhoneSpecification = getPersistentEntityById(id);
        converter.fillEntityFromDTO(phoneSpecificationDTO, persistentPhoneSpecification);

        getNewDataAndReplaceCurrent(persistentPhoneSpecification, phoneSpecificationDTO);

        var persistentCameras = phoneSpecificationDTO.getCameras()
                .stream()
                .map(cameraExtractor::getPersistentEntity)
                .toList();
        persistentPhoneSpecification.setCameras(persistentCameras);

        return mapper.map(persistentPhoneSpecification, PhoneSpecificationDTO.class);
    }

    /**
     * Частично или полностью обновляет информацию о характеристике телефона в базе данных и возвращает обновленную
     * информацию о характеристике телефона. Если изменяемые данные пустые или не переданы, то изменения не происходят
     * и возвращается объект из базы данных по указанному идентификатору.
     * Важно: Этот метод не позволяет изменять id характеристики телефона.
     *
     * @param id                    уникальный идентификатор обновляемой характеристики телефона, данные которой необходимо изменить.
     * @param phoneSpecificationDTO объект с обновленными сведениями об изменяемой характеристике телефона.
     * @return объект PhoneSpecificationDTO с обновленной информацией об измененной характеристике телефона из базы данных.
     * @throws ResourceNotFoundException если изменяемая характеристика телефона не найдена по id.
     */
    @Override
    @Transactional
    public PhoneSpecificationDTO editPartOfEntity(Long id, PhoneSpecificationDTO phoneSpecificationDTO) {
        var persistentPhoneSpecification = getPersistentEntityById(id);
        nullableMapper.map(phoneSpecificationDTO, persistentPhoneSpecification);

        var operatingSystemDTO = phoneSpecificationDTO.getOperatingSystem();
        if (operatingSystemDTO != null && operatingSystemDTO.getId() != null) {
            var newOperatingSystemId = operatingSystemDTO.getId();
            var persistentOperatingSystem = operatingSystemExtractor.getPersistentEntityById(newOperatingSystemId);
            persistentPhoneSpecification.setOperatingSystem(persistentOperatingSystem);
        }

        var displayDTO = phoneSpecificationDTO.getDisplay();
        if (displayDTO != null && displayDTO.getId() != null) {
            var newDisplayId = displayDTO.getId();
            var persistentDisplay = displayExtractor.getPersistentEntityById(newDisplayId);
            persistentPhoneSpecification.setDisplay(persistentDisplay);
        }

        var processorDTO = phoneSpecificationDTO.getProcessor();
        if (processorDTO != null && processorDTO.getId() != null) {
            var newProcessorId = processorDTO.getId();
            var persistentProcessor = processorExtractor.getPersistentEntityById(newProcessorId);
            persistentPhoneSpecification.setProcessor(persistentProcessor);
        }

        var batteryDTO = phoneSpecificationDTO.getBattery();
        if (batteryDTO != null && batteryDTO.getId() != null) {
            var newBatteryId = batteryDTO.getId();
            var persistentBattery = batteryExtractor.getPersistentEntityById(newBatteryId);
            persistentPhoneSpecification.setBattery(persistentBattery);
        }

        if (!phoneSpecificationDTO.getCameras().isEmpty()) {
            var persistentCameras = phoneSpecificationDTO.getCameras()
                    .stream()
                    .map(cameraExtractor::getPersistentEntity)
                    .toList();
            persistentPhoneSpecification.getCameras().clear();
            persistentPhoneSpecification.getCameras().addAll(persistentCameras);
        }

        phoneSpecificationRepository.save(persistentPhoneSpecification);
        return mapper.map(persistentPhoneSpecification, PhoneSpecificationDTO.class);
    }

    /**
     * Извлекает существующую hibernate сущность характеристики телефона из базы данных по его идентификатору и
     * возвращает её.
     *
     * @param id идентификатор hibernate сущности характеристики телефона, которую нужно получить.
     * @return объект PhoneSpecificationEntity, представляющий таблицу характеристики телефона из базы данных.
     * @throws ResourceNotFoundException если характеристика телефона с указанным идентификатором не найдена.
     */
    @Override
    public PhoneSpecificationEntity getPersistentEntityById(Long id) {
        return phoneSpecificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phone specification not found"));
    }

    /**
     * Извлекает существующую hibernate сущность характеристики телефона из базы данных по полученному DTO объекту.
     *
     * @param phoneSpecificationDTO объект передачи данных, содержащий сведения о характеристике телефона.
     * @return объект PhoneSpecificationEntity, представляющий таблицу характеристики телефона из базы данных.
     * @throws ResourceNotFoundException если характеристика телефона по entityDTO не найдена.
     * @throws InsufficientDataException если entityDTO равняется null или данных
     *                                   для извлечения характеристики телефона недостаточно.
     */
    @Override
    public PhoneSpecificationEntity getPersistentEntity(PhoneSpecificationDTO phoneSpecificationDTO) {
        if (phoneSpecificationDTO == null || phoneSpecificationDTO.getId() == null) {
            throw new InsufficientDataException("Phone specification data missing");
        }
        return phoneSpecificationRepository.findById(phoneSpecificationDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Phone specification not found"));
    }
}
