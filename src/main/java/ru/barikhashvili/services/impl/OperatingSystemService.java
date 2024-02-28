package ru.barikhashvili.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barikhashvili.dto.specs.OperatingSystemDTO;
import ru.barikhashvili.entities.specs.OperatingSystemEntity;
import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.exceptions.ResourceNotFoundException;
import ru.barikhashvili.repositories.OperatingSystemRepository;
import ru.barikhashvili.services.CrudService;
import ru.barikhashvili.services.EntityExtractor;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OperatingSystemService implements CrudService<OperatingSystemDTO, Integer>,
        EntityExtractor<OperatingSystemDTO, OperatingSystemEntity, Integer> {
    OperatingSystemRepository operatingSystemRepository;
    ModelMapper mapper;
    ModelMapper nullableMapper;

    public OperatingSystemService(OperatingSystemRepository operatingSystemRepository,
                                  @Qualifier("modelMapper") ModelMapper mapper,
                                  @Qualifier("nullableModelMapper") ModelMapper nullableMapper) {
        this.operatingSystemRepository = operatingSystemRepository;
        this.mapper = mapper;
        this.nullableMapper = nullableMapper;
    }

    /**
     * Добавляет новую операционную систему в базу данных и возвращает DTO с информацией
     * о добавленной операционной системе. Этот метод требует, чтобы полученный в аргументе operatingSystemDTO
     * содержал полную информацию об операционной системе.
     *
     * @param operatingSystemDTO объект DTO со сведениями о добавляемой операционной системе (DTO должен содержать полные сведения).
     * @return объект OperatingSystemDTO с полной информацией о добавленной операционной системе из базы данных.
     * @throws InsufficientDataException если данные об операционной системе отсутствуют.
     */
    @Override
    @Transactional
    public OperatingSystemDTO saveEntity(OperatingSystemDTO operatingSystemDTO) {
        var transientOperatingSystem = mapper.map(operatingSystemDTO, OperatingSystemEntity.class);
        var persistentOperatingSystem = operatingSystemRepository.save(transientOperatingSystem);

        return mapper.map(persistentOperatingSystem, OperatingSystemDTO.class);
    }

    /**
     * Находит и возвращает данные об операционной системе с указанным идентификатором в виде DTO.
     *
     * @param operatingSystemId уникальный идентификатор операционной системы, которую необходимо найти
     * @return объект OperatingSystemDTO с полными сведениями об операционной системе.
     * @throws ResourceNotFoundException если операционная система с указанным id не найдена в базе данных
     */
    @Override
    public OperatingSystemDTO getEntityById(Integer operatingSystemId) {
        var persistentOperatingSystem = getPersistentEntityById(operatingSystemId);
        return mapper.map(persistentOperatingSystem, OperatingSystemDTO.class);
    }

    /**
     * Находит и возвращает список всех операционных систем, отсортированный в алфавитном порядке согласно
     * названиям операционных систем и номеру версий в порядке возрастания.
     *
     * @return список OperatingSystemDTO с полными сведениями о всех операционных системах из базы данных.
     */
    @Override
    public List<OperatingSystemDTO> getAllEntities() {
        var persistentOperatingSystems = operatingSystemRepository.findAllByOrderByNameAscVersionAsc();
        return persistentOperatingSystems.stream()
                .map(operatingSystem -> mapper.map(operatingSystem, OperatingSystemDTO.class))
                .toList();
    }

    /**
     * Удаляет операционную систему с указанным id из базы данных и возвращает DTO с данными
     * об удаленной операционной системе.
     *
     * @param operatingSystemId уникальный идентификатор операционной системы, которую необходимо удалить.
     * @throws ResourceNotFoundException если операционная система с указанным id не найдена в базе данных.
     */
    @Override
    @Transactional
    public OperatingSystemDTO removeEntityById(Integer operatingSystemId) {
        var removedOperatingSystem = getEntityById(operatingSystemId);
        operatingSystemRepository.deleteById(operatingSystemId);
        return removedOperatingSystem;
    }

    /**
     * Полностью обновляет информацию об операционной системе в базе данных и возвращает DTO объект с обновленной информацией.
     * Этот метод требует, чтобы полученный в аргументе operatingSystemDTO содержал полную информацию об операционной системе.
     * Важно: Этот метод не позволяет изменять id операционной системы.
     *
     * @param operatingSystemId  уникальный идентификатор операционной системы, данные которой необходимо изменить.
     * @param operatingSystemDTO объект DTO с новыми сведениями об операционной системе (DTO должен содержать
     *                           полные сведения об операционной системе).
     * @return объект OperatingSystemDTO с обновленной информацией об измененной операционной системе из базы данных.
     * @throws InsufficientDataException если изменяемые данные об операционной системе отсутствуют.
     * @throws ResourceNotFoundException если изменяемая операционная система не найдена по operatingSystemId
     */
    @Override
    @Transactional
    public OperatingSystemDTO editEntity(Integer operatingSystemId, OperatingSystemDTO operatingSystemDTO) {
        var persistentOperatingSystem = getPersistentEntityById(operatingSystemId);
        mapper.map(operatingSystemDTO, persistentOperatingSystem);
        operatingSystemRepository.save(persistentOperatingSystem);
        return mapper.map(persistentOperatingSystem, OperatingSystemDTO.class);
    }

    /**
     * Частично или полностью обновляет информацию об операционной системе в базе данных и возвращает DTO
     * с обновленной информацией об операционной системе. Если изменяемые данные пустые или не переданы, то
     * изменения не происходят и возвращается объект из базы данных по указанному идентификатору.
     * Важно: Этот метод не позволяет изменять id операционной системы.
     *
     * @param operatingSystemId  уникальный идентификатор операционной системы, данные которой необходимо изменить.
     * @param operatingSystemDTO объект DTO с обновленными сведениями об операционной системе.
     * @return объект OperatingSystemDTO с обновленной информацией об операционной системе из базы данных.
     * @throws ResourceNotFoundException если изменяемая операционная система не найдена по operatingSystemId
     */
    @Override
    @Transactional
    public OperatingSystemDTO editPartOfEntity(Integer operatingSystemId, OperatingSystemDTO operatingSystemDTO) {
        var persistentOperatingSystem = getPersistentEntityById(operatingSystemId);
        nullableMapper.map(operatingSystemDTO, persistentOperatingSystem);
        operatingSystemRepository.save(persistentOperatingSystem);
        return mapper.map(persistentOperatingSystem, OperatingSystemDTO.class);
    }

    /**
     * Находит существующую в базе данных операционную систему по указанному идентификатору.
     * Если операционная система не найдена, то выбрасывается исключение ResourceNotFoundException.
     *
     * @param id уникальный идентификатор операционной системы
     * @return сущность операционной системы, найденной в базе данных
     * @throws ResourceNotFoundException если операционная система с указанным id не найдена в базе данных
     */
    @Override
    public OperatingSystemEntity getPersistentEntityById(Integer id) {
        return operatingSystemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operating system not found"));
    }

    /**
     * Извлекает существующую операционную систему из базы данных по полученному DTO объекту.
     *
     * @param operatingSystemDTO объект передачи данных, содержащий сведения об операционной системе.
     * @return объект OperatingSystemEntity, представляющий таблицу операционной системы из базы данных.
     * @throws ResourceNotFoundException если операционная система по operatingSystemDTO не найдена.
     * @throws InsufficientDataException если operatingSystemDTO равняется null или данных
     *                                   для извлечения операционной системы не достаточно.
     */
    @Override
    public OperatingSystemEntity getPersistentEntity(OperatingSystemDTO operatingSystemDTO) {
        if (operatingSystemDTO == null || operatingSystemDTO.getId() == null) {
            throw new InsufficientDataException("Operating system data missing");
        }
        return operatingSystemRepository.findById(operatingSystemDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Operating system not found"));
    }
}