package ru.barikhashvili.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barikhashvili.dto.specs.CameraDTO;
import ru.barikhashvili.dto.specs.CameraSensorDTO;
import ru.barikhashvili.entities.specs.CameraEntity;
import ru.barikhashvili.entities.specs.CameraSensorEntity;
import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.exceptions.ResourceNotFoundException;
import ru.barikhashvili.repositories.CameraRepository;
import ru.barikhashvili.services.CrudService;
import ru.barikhashvili.services.EntityExtractor;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CameraService implements CrudService<CameraDTO, Long>,
        EntityExtractor<CameraDTO, CameraEntity, Long> {
    CameraRepository cameraRepository;
    EntityExtractor<CameraSensorDTO, CameraSensorEntity, Integer> sensorExtractor;
    ModelMapper mapper;
    ModelMapper nullableMapper;

    public CameraService(CameraRepository cameraRepository,
                         EntityExtractor<CameraSensorDTO, CameraSensorEntity, Integer> sensorExtractor,
                         @Qualifier("modelMapper") ModelMapper mapper,
                         @Qualifier("nullableModelMapper") ModelMapper nullableMapper) {
        this.cameraRepository = cameraRepository;
        this.sensorExtractor = sensorExtractor;
        this.mapper = mapper;
        this.nullableMapper = nullableMapper;
    }

    /**
     * Добавляет новую камеру в базу данных и возвращает информацию о добавленной камере.
     * Этот метод требует, чтобы полученный в аргументе cameraDTO содержал полную информацию
     * о добавляемой камере.
     *
     * @param cameraDTO объект со сведениями о добавляемой камере (объект должен содержать все сведения).
     * @return объект CameraDTO с полной информацией о добавленной камере из базы данных.
     * @throws InsufficientDataException если данные о добавляемой камере отсутствуют или указаны частично.
     */
    @Override
    @Transactional
    public CameraDTO saveEntity(CameraDTO cameraDTO) {
        var transientCamera = mapper.map(cameraDTO, CameraEntity.class);

        var sensorDTO = cameraDTO.getSensor();
        var persistentSensor = sensorExtractor.getPersistentEntity(sensorDTO);
        transientCamera.setSensor(persistentSensor);

        var persistentCamera = cameraRepository.save(transientCamera);

        return mapper.map(persistentCamera, CameraDTO.class);
    }

    /**
     * Находит и возвращает данные о камере с указанным идентификатором.
     *
     * @param id уникальный идентификатор камеры, который необходимо найти
     * @return объект CameraDTO с полной информацией о камере.
     * @throws ResourceNotFoundException если камера с указанным id не найдена в базе данных
     */
    @Override
    public CameraDTO getEntityById(Long id) {
        var persistentCamera = getPersistentEntityById(id);
        return mapper.map(persistentCamera, CameraDTO.class);
    }

    /**
     * Находит и возвращает список всех камер, отсортированный по возрастанию идентификаторов.
     *
     * @return список CameraDTO с полными сведениями о всех камерах из базы данных.
     */
    @Override
    @Transactional
    public List<CameraDTO> getAllEntities() {
        var persistentCameras = cameraRepository.findAllByOrderByIdAsc();
        return persistentCameras.stream()
                .map(camera -> mapper.map(camera, CameraDTO.class))
                .toList();
    }

    /**
     * Удаляет камеру с указанным id из базы данных и возвращает сведения об удаленной камере.
     *
     * @param id уникальный идентификатор камеры, которую необходимо удалить.
     * @throws ResourceNotFoundException если камера с указанным id не найдена в базе данных.
     */
    @Override
    @Transactional
    public CameraDTO removeEntityById(Long id) {
        var removedCamera = getEntityById(id);
        cameraRepository.deleteById(id);
        return removedCamera;
    }

    /**
     * Полностью обновляет информацию о камере в базе данных и возвращает объект с обновленной информацией.
     * Этот метод требует, чтобы полученный в аргументе cameraDTO содержал полную информацию об изменяемой камере.
     * Важно: Этот метод не позволяет изменять id камеры.
     *
     * @param id        уникальный идентификатор обновляемой камеры, данные которой необходимо изменить.
     * @param cameraDTO объект с новыми сведениями об изменяемой камере (Он должен содержать все сведения о камере).
     * @return объект CameraDTO с обновленной информацией об измененной камере из базы данных.
     * @throws InsufficientDataException если данные об изменяемой камере отсутствуют.
     * @throws ResourceNotFoundException если изменяемая камера не найдена по id.
     */
    @Override
    @Transactional
    public CameraDTO editEntity(Long id, CameraDTO cameraDTO) {
        var persistentCamera = getPersistentEntityById(id);
        mapper.map(cameraDTO, persistentCamera);

        var sensorDTO = cameraDTO.getSensor();
        var persistentSensor = sensorExtractor.getPersistentEntity(sensorDTO);
        persistentCamera.setSensor(persistentSensor);

        return mapper.map(persistentCamera, CameraDTO.class);
    }

    /**
     * Частично или полностью обновляет информацию о камере в базе данных и возвращает обновленную
     * информацию о камере. Если изменяемые данные пустые или не переданы, то изменения не происходят
     * и возвращается объект из базы данных по указанному идентификатору.
     * Важно: Этот метод не позволяет изменять id камеры.
     *
     * @param id        уникальный идентификатор обновляемой камеры, данные которой необходимо изменить.
     * @param cameraDTO объект с обновленными сведениями об изменяемой камере.
     * @return объект CameraDTO с обновленной информацией об измененной камере из базы данных.
     * @throws ResourceNotFoundException если изменяемая камера не найдена по id.
     */
    @Override
    @Transactional
    public CameraDTO editPartOfEntity(Long id, CameraDTO cameraDTO) {
        var persistentCamera = getPersistentEntityById(id);
        nullableMapper.map(cameraDTO, persistentCamera);

        var sensorDTO = cameraDTO.getSensor();
        if (sensorDTO != null && sensorDTO.getId() != null) {
            var newSensorId = sensorDTO.getId();
            var persistentSensorEntity = sensorExtractor.getPersistentEntityById(newSensorId);
            persistentCamera.setSensor(persistentSensorEntity);
        }

        cameraRepository.save(persistentCamera);
        return mapper.map(persistentCamera, CameraDTO.class);
    }

    /**
     * Извлекает существующую hibernate сущность камеры из базы данных по его идентификатору и
     * возвращает её.
     *
     * @param id идентификатор hibernate сущности камеры, которую нужно получить.
     * @return объект CameraEntity, представляющий таблицу камеры из базы данных.
     * @throws ResourceNotFoundException если камера с указанным идентификатором не найдена.
     */
    @Override
    public CameraEntity getPersistentEntityById(Long id) {
        return cameraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Camera not found"));
    }

    /**
     * Извлекает существующую hibernate сущность камеры из базы данных по полученному DTO объекту.
     *
     * @param cameraDTO объект передачи данных, содержащий сведения о камере.
     * @return объект CameraEntity, представляющий таблицу камеры из базы данных.
     * @throws ResourceNotFoundException если камера по entityDTO не найдена.
     * @throws InsufficientDataException если entityDTO равняется null или данных
     *                                   для извлечения камеры недостаточно.
     */
    @Override
    public CameraEntity getPersistentEntity(CameraDTO cameraDTO) {
        if (cameraDTO == null || cameraDTO.getId() == null) {
            throw new InsufficientDataException("Camera data missing");
        }
        return cameraRepository.findById(cameraDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Camera not found"));
    }
}
