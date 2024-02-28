package ru.barikhashvili.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barikhashvili.dto.specs.CameraSensorDTO;
import ru.barikhashvili.entities.specs.CameraSensorEntity;
import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.exceptions.ResourceNotFoundException;
import ru.barikhashvili.repositories.CameraSensorRepository;
import ru.barikhashvili.services.CrudService;
import ru.barikhashvili.services.EntityExtractor;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CameraSensorService implements CrudService<CameraSensorDTO, Integer>,
        EntityExtractor<CameraSensorDTO, CameraSensorEntity, Integer> {
    CameraSensorRepository cameraSensorRepository;
    ModelMapper mapper;
    ModelMapper nullableMapper;

    public CameraSensorService(CameraSensorRepository cameraSensorRepository,
                            @Qualifier("modelMapper") ModelMapper mapper,
                            @Qualifier("nullableModelMapper") ModelMapper nullableMapper) {
        this.cameraSensorRepository = cameraSensorRepository;
        this.mapper = mapper;
        this.nullableMapper = nullableMapper;
    }

    /**
     * Добавляет новый сенсор камеры в базу данных и возвращает информацию о добавленном сенсоре камеры.
     * Этот метод требует, чтобы полученный в аргументе cameraSensorDTO содержал полную информацию
     * о добавляемом сенсоре камеры.
     *
     * @param cameraSensorDTO объект со сведениями о добавляемом сенсоре камеры (объект должен содержать все сведения).
     * @return объект CameraSensorDTO с полной информацией о добавленном сенсоре камеры из базы данных.
     * @throws InsufficientDataException если данные о добавляемом сенсоре камеры отсутствуют или указаны частично.
     */
    @Override
    @Transactional
    public CameraSensorDTO saveEntity(CameraSensorDTO cameraSensorDTO) {
        var transientCameraSensor = mapper.map(cameraSensorDTO, CameraSensorEntity.class);
        var persistentCameraSensor = cameraSensorRepository.save(transientCameraSensor);

        return mapper.map(persistentCameraSensor, CameraSensorDTO.class);
    }

    /**
     * Находит и возвращает данные о сенсоре камеры с указанным идентификатором.
     *
     * @param id уникальный идентификатор сенсора камеры, который необходимо найти
     * @return объект CameraSensorDTO с полной информацией о сенсоре камеры.
     * @throws ResourceNotFoundException если сенсор камеры с указанным id не найден в базе данных
     */
    @Override
    public CameraSensorDTO getEntityById(Integer id) {
        var persistentCameraSensor = getPersistentEntityById(id);
        return mapper.map(persistentCameraSensor, CameraSensorDTO.class);
    }

    /**
     * Находит и возвращает список всех сенсоров камеры, отсортированный в порядке
     * увеличения количества мегапикселей.
     *
     * @return список CameraSensorDTO с полными сведениями о всех сенсорах камеры из базы данных.
     */
    @Override
    public List<CameraSensorDTO> getAllEntities() {
        var persistentCameraSensors = cameraSensorRepository.findAllByOrderByMegapixelsAsc();
        return persistentCameraSensors.stream()
                .map(cameraSensor -> mapper.map(cameraSensor, CameraSensorDTO.class))
                .toList();
    }

    /**
     * Удаляет сенсор камеры с указанным id из базы данных и возвращает сведения об удаленном сенсоре камеры.
     *
     * @param id уникальный идентификатор сенсора камеры, который необходимо удалить.
     * @throws ResourceNotFoundException если сенсор камеры с указанным id не найден в базе данных.
     */
    @Override
    @Transactional
    public CameraSensorDTO removeEntityById(Integer id) {
        var removedCameraSensor = getEntityById(id);
        cameraSensorRepository.deleteById(id);
        return removedCameraSensor;
    }

    /**
     * Полностью обновляет информацию о сенсоре камеры в базе данных и возвращает объект с обновленной информацией.
     * Этот метод требует, чтобы полученный в аргументе cameraSensorDTO содержал полную информацию об изменяемом сенсоре камеры.
     * Важно: Этот метод не позволяет изменять id сенсора камеры.
     *
     * @param id           уникальный идентификатор обновляемого сенсора камеры, данные которого необходимо изменить.
     * @param cameraSensorDTO объект с новыми сведениями об изменяемом сенсоре камеры (Он должен содержать все сведения о сенсоре камеры).
     * @return объект CameraSensorDTO с обновленной информацией об измененном сенсоре камеры из базы данных.
     * @throws InsufficientDataException если данные об изменяемом сенсоре камеры отсутствуют.
     * @throws ResourceNotFoundException если изменяемый сенсор камеры не найден по id.
     */
    @Override
    @Transactional
    public CameraSensorDTO editEntity(Integer id, CameraSensorDTO cameraSensorDTO) {
        var persistentCameraSensor = getPersistentEntityById(id);
        mapper.map(cameraSensorDTO, persistentCameraSensor);
        cameraSensorRepository.save(persistentCameraSensor);
        return mapper.map(persistentCameraSensor, CameraSensorDTO.class);
    }

    /**
     * Частично или полностью обновляет информацию о сенсоре камеры в базе данных и возвращает обновленную
     * информацию о сенсоре камеры. Если изменяемые данные пустые или не переданы, то изменения не происходят
     * и возвращается объект из базы данных по указанному идентификатору.
     * Важно: Этот метод не позволяет изменять id сенсора камеры.
     *
     * @param id           уникальный идентификатор обновляемого сенсора камеры, данные которого необходимо изменить.
     * @param cameraSensorDTO объект с обновленными сведениями об изменяемом сенсоре камеры.
     * @return объект CameraSensorDTO с обновленной информацией об измененном сенсоре камеры из базы данных.
     * @throws ResourceNotFoundException если изменяемый сенсор камеры не найден по id.
     */
    @Override
    @Transactional
    public CameraSensorDTO editPartOfEntity(Integer id, CameraSensorDTO cameraSensorDTO) {
        var persistentCameraSensor = getPersistentEntityById(id);
        nullableMapper.map(cameraSensorDTO, persistentCameraSensor);
        cameraSensorRepository.save(persistentCameraSensor);
        return mapper.map(persistentCameraSensor, CameraSensorDTO.class);
    }

    /**
     * Извлекает существующую hibernate сущность сенсора камеры из базы данных по его идентификатору и
     * возвращает её.
     *
     * @param id идентификатор hibernate сущности сенсора камеры, которую нужно получить.
     * @return объект CameraSensorEntity, представляющий таблицу сенсора камеры из базы данных.
     * @throws ResourceNotFoundException если сенсор камеры с указанным идентификатором не найден.
     */
    @Override
    public CameraSensorEntity getPersistentEntityById(Integer id) {
        return cameraSensorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CameraSensor not found"));
    }

    /**
     * Извлекает существующую hibernate сущность сенсора камеры из базы данных по полученному DTO объекту.
     *
     * @param cameraSensorDTO объект передачи данных, содержащий сведения о сенсоре камеры.
     * @return объект CameraSensorEntity, представляющий таблицу сенсора камеры из базы данных.
     * @throws ResourceNotFoundException если сенсор камеры по entityDTO не найден.
     * @throws InsufficientDataException если entityDTO равняется null или данных
     *                                   для извлечения сенсора камеры недостаточно.
     */
    @Override
    public CameraSensorEntity getPersistentEntity(CameraSensorDTO cameraSensorDTO) {
        if (cameraSensorDTO == null || cameraSensorDTO.getId() == null) {
            throw new InsufficientDataException("Camera sensor data missing");
        }
        return cameraSensorRepository.findById(cameraSensorDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Camera sensor not found"));
    }
}
