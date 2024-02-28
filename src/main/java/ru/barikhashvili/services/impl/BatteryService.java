package ru.barikhashvili.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barikhashvili.dto.specs.BatteryDTO;
import ru.barikhashvili.entities.specs.BatteryEntity;
import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.exceptions.ResourceNotFoundException;
import ru.barikhashvili.repositories.BatteryRepository;
import ru.barikhashvili.services.CrudService;
import ru.barikhashvili.services.EntityExtractor;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BatteryService implements CrudService<BatteryDTO, Integer>,
        EntityExtractor<BatteryDTO, BatteryEntity, Integer> {
    BatteryRepository batteryRepository;
    ModelMapper mapper;
    ModelMapper nullableMapper;

    public BatteryService(BatteryRepository batteryRepository,
                          @Qualifier("modelMapper") ModelMapper mapper,
                          @Qualifier("nullableModelMapper") ModelMapper nullableMapper) {
        this.batteryRepository = batteryRepository;
        this.mapper = mapper;
        this.nullableMapper = nullableMapper;
    }

    /**
     * Добавляет новый аккумулятор телефона в базу данных и возвращает DTO с информацией
     * о добавленном аккумулятора. Этот метод требует, чтобы полученный в аргументе batteryDTO
     * содержал полную информацию об аккумуляторе.
     *
     * @param batteryDTO объект DTO со сведениями о добавляемом аккумуляторе (DTO должен содержать полные сведения).
     * @return объект BatteryDTO с полной информацией о добавленном аккумуляторе из базы данных.
     * @throws InsufficientDataException если данные об аккумуляторе отсутствуют.
     */
    @Override
    @Transactional
    public BatteryDTO saveEntity(BatteryDTO batteryDTO) {
        var transientBattery = mapper.map(batteryDTO, BatteryEntity.class);
        var persistentBattery = batteryRepository.save(transientBattery);

        return mapper.map(persistentBattery, BatteryDTO.class);
    }

    /**
     * Находит и возвращает данные об аккумуляторе с указанным идентификатором в виде DTO.
     *
     * @param batteryId уникальный идентификатор аккумулятора, который необходимо найти
     * @return объект BatteryDTO с полными сведениями об аккумуляторе.
     * @throws ResourceNotFoundException если аккумулятор с указанным id не найден в базе данных
     */
    @Override
    public BatteryDTO getEntityById(Integer batteryId) {
        var persistentBattery = getPersistentEntityById(batteryId);
        return mapper.map(persistentBattery, BatteryDTO.class);
    }

    /**
     * Находит и возвращает список всех аккумуляторов, отсортированный в порядке возрастания ёмкости аккумулятора.
     *
     * @return список BatteryDTO с полными сведениями о всех аккумуляторах из базы данных.
     */
    @Override
    public List<BatteryDTO> getAllEntities() {
        var persistentBatteries = batteryRepository.findAllByOrderByCapacity();
        return persistentBatteries.stream()
                .map(battery -> mapper.map(battery, BatteryDTO.class))
                .toList();
    }

    /**
     * Удаляет аккумулятор с указанным id из базы данных и возвращает DTO с данными
     * об удаленном аккумуляторе.
     *
     * @param batteryId уникальный идентификатор аккумулятора, который необходимо удалить.
     * @throws ResourceNotFoundException если аккумулятор с указанным id не найден в базе данных.
     */
    @Override
    @Transactional
    public BatteryDTO removeEntityById(Integer batteryId) {
        var removedBattery = getEntityById(batteryId);
        batteryRepository.deleteById(batteryId);
        return removedBattery;
    }

    /**
     * Полностью обновляет информацию об аккумуляторе в базе данных и возвращает DTO объект с обновленной информацией.
     * Этот метод требует, чтобы полученный в аргументе batteryDTO содержал полную информацию об аккумуляторе.
     * Важно: Этот метод не позволяет изменять id аккумулятора.
     *
     * @param batteryId  уникальный идентификатор аккумулятора, данные которого необходимо изменить.
     * @param batteryDTO объект DTO с новыми сведениями об аккумуляторе (DTO должен содержать полные сведения об аккумуляторе).
     * @return объект BatteryDTO с обновленной информацией об измененном аккумуляторе из базы данных.
     * @throws InsufficientDataException если данные об аккумуляторе отсутствуют.
     * @throws ResourceNotFoundException если изменяемый аккумулятор не найден по batteryId
     */
    @Override
    @Transactional
    public BatteryDTO editEntity(Integer batteryId, BatteryDTO batteryDTO) {
        var persistentBattery = getPersistentEntityById(batteryId);
        mapper.map(batteryDTO, persistentBattery);
        batteryRepository.save(persistentBattery);
        return mapper.map(persistentBattery, BatteryDTO.class);
    }

    /**
     * Частично или полностью обновляет информацию об аккумуляторе в базе данных и возвращает DTO
     * с обновленной информацией об аккумуляторе. Если изменяемые данные пустые или не переданы, то
     * изменения не происходят и возвращается объект из базы данных по указанному идентификатору.
     * Важно: Этот метод не позволяет изменять id аккумулятора.
     *
     * @param batteryId  уникальный идентификатор аккумулятора, данные которого необходимо изменить.
     * @param batteryDTO объект DTO с обновленными сведениями об аккумуляторе.
     * @return объект BatteryDTO с обновленной информацией об аккумуляторе из базы данных.
     * @throws ResourceNotFoundException если изменяемый аккумулятор не найден по batteryId
     */
    @Override
    @Transactional
    public BatteryDTO editPartOfEntity(Integer batteryId, BatteryDTO batteryDTO) {
        var persistentBattery = getPersistentEntityById(batteryId);
        nullableMapper.map(batteryDTO, persistentBattery);
        batteryRepository.save(persistentBattery);
        return mapper.map(persistentBattery, BatteryDTO.class);
    }

    /**
     * Находит существующий в базе данных аккумулятор по указанному идентификатору.
     * Если аккумулятор не найден, то выбрасывается исключение ResourceNotFoundException.
     *
     * @param id уникальный идентификатор аккумулятора
     * @return сущность аккумулятора, найденного в базе данных
     * @throws ResourceNotFoundException если аккумулятор с указанным id не найден в базе данных
     */
    @Override
    public BatteryEntity getPersistentEntityById(Integer id) {
        return batteryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Battery not found"));
    }

    /**
     * Извлекает существующий объект аккумулятора из базы данных по полученному DTO объекту.
     *
     * @param batteryDTO объект передачи данных, содержащий информацию об аккумуляторе.
     * @return объект BatteryEntity, представляющий запись аккумулятора из базы данных.
     * @throws ResourceNotFoundException если аккумулятор по batteryDTO не найден.
     * @throws InsufficientDataException если batteryDTO равен null или данных
     * для извлечения аккумулятора недостаточно.
     */
    @Override
    public BatteryEntity getPersistentEntity(BatteryDTO batteryDTO) {
        if (batteryDTO == null || batteryDTO.getId() == null) {
            throw new InsufficientDataException("Battery data missing");
        }
        return batteryRepository.findById(batteryDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Battery not found"));
    }
}
