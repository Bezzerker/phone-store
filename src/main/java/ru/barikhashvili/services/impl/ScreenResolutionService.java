package ru.barikhashvili.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barikhashvili.dto.specs.ResolutionDTO;
import ru.barikhashvili.entities.ResolutionEntity;
import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.exceptions.ResourceNotFoundException;
import ru.barikhashvili.repositories.ScreenResolutionRepository;
import ru.barikhashvili.services.CrudService;
import ru.barikhashvili.services.EntityExtractor;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScreenResolutionService implements CrudService<ResolutionDTO, Integer>,
        EntityExtractor<ResolutionDTO, ResolutionEntity, Integer> {
    ScreenResolutionRepository screenResolutionRepository;
    ModelMapper mapper;
    ModelMapper nullableMapper;

    public ScreenResolutionService(ScreenResolutionRepository screenResolutionRepository,
                                   @Qualifier("modelMapper") ModelMapper mapper,
                                   @Qualifier("nullableModelMapper") ModelMapper nullableMapper) {
        this.screenResolutionRepository = screenResolutionRepository;
        this.mapper = mapper;
        this.nullableMapper = nullableMapper;
    }

    /**
     * Добавляет новое разрешение экрана в базу данных и возвращает информацию о добавленном разрешении.
     * Этот метод требует, чтобы полученный в аргументе resolutionDTO содержал полную информацию
     * о добавляемом разрешении экрана.
     *
     * @param resolutionDTO объект со сведениями о добавляемом разрешении экрана (объект должен содержать все сведения).
     * @return объект ResolutionDTO с полной информацией о добавленном разрешении из базы данных.
     * @throws InsufficientDataException если данные о добавляемом разрешении экрана отсутствуют или указаны частично.
     */
    @Override
    @Transactional
    public ResolutionDTO saveEntity(ResolutionDTO resolutionDTO) {
        var transientScreenResolution = mapper.map(resolutionDTO, ResolutionEntity.class);
        var persistentScreenResolution = screenResolutionRepository.save(transientScreenResolution);

        return mapper.map(persistentScreenResolution, ResolutionDTO.class);
    }

    /**
     * Находит и возвращает данные о разрешении экрана с указанным идентификатором.
     *
     * @param id уникальный идентификатор разрешения экрана, которое необходимо найти
     * @return объект ResolutionDTO с полной информацией о разрешении экрана.
     * @throws ResourceNotFoundException если разрешение экрана с указанным id не найдено в базе данных
     */
    @Override
    public ResolutionDTO getEntityById(Integer id) {
        var persistentScreenResolution = getPersistentEntityById(id);
        return mapper.map(persistentScreenResolution, ResolutionDTO.class);
    }

    /**
     * Находит и возвращает список всех разрешений экрана, отсортированный в порядке увеличения
     * разрешения по горизонтали и по вертикали
     *
     * @return список ResolutionDTO с полными сведениями о всех разрешениях экранов из базы данных.
     */
    @Override
    public List<ResolutionDTO> getAllEntities() {
        var persistentScreenResolutions = screenResolutionRepository.findAllByOrderByHorizontalPixelsAscVerticalPixelsAsc();
        return persistentScreenResolutions.stream()
                .map(screenResolution -> mapper.map(screenResolution, ResolutionDTO.class))
                .toList();
    }

    /**
     * Удаляет разрешение экрана с указанным id из базы данных и возвращает сведения об удаленном разрешении.
     *
     * @param id уникальный идентификатор разрешения экрана, которое необходимо удалить.
     * @throws ResourceNotFoundException если разрешение экрана с указанным id не найдено в базе данных.
     */
    @Override
    @Transactional
    public ResolutionDTO removeEntityById(Integer id) {
        var removedScreenResolution = getEntityById(id);
        screenResolutionRepository.deleteById(id);
        return removedScreenResolution;
    }

    /**
     * Полностью обновляет информацию о разрешении экрана в базе данных и возвращает объект с обновленной информацией.
     * Этот метод требует, чтобы полученный в аргументе resolutionDTO содержал полную информацию об изменяемом разрешении экрана.
     * Важно: Этот метод не позволяет изменять id разрешения экрана.
     *
     * @param id            уникальный идентификатор обновляемого разрешения экрана, данные которого необходимо изменить.
     * @param resolutionDTO объект с новыми сведениями об изменяемом разрешении экрана (Он должен содержать все сведения о разрешении экрана).
     * @return объект ResolutionDTO с обновленной информацией об измененном разрешении экрана из базы данных.
     * @throws InsufficientDataException если данные об изменяемом разрешении экрана отсутствуют.
     * @throws ResourceNotFoundException если изменяемое разрешение экрана не найдено по id.
     */
    @Override
    @Transactional
    public ResolutionDTO editEntity(Integer id, ResolutionDTO resolutionDTO) {
        var persistentScreenResolution = getPersistentEntityById(id);
        mapper.map(resolutionDTO, persistentScreenResolution);
        screenResolutionRepository.save(persistentScreenResolution);
        return mapper.map(persistentScreenResolution, ResolutionDTO.class);
    }

    /**
     * Частично или полностью обновляет информацию о разрешении экрана в базе данных и возвращает обновленную
     * информацию о разрешении экрана. Если изменяемые данные пустые или не переданы, то изменения не происходят
     * и возвращается объект из базы данных по указанному идентификатору.
     * Важно: Этот метод не позволяет изменять id разрешения экрана.
     *
     * @param id            уникальный идентификатор обновляемого разрешения экрана, данные которого необходимо изменить.
     * @param resolutionDTO объект с обновленными сведениями об изменяемом разрешении экрана.
     * @return объект ResolutionDTO с обновленной информацией об измененном разрешении экрана из базы данных.
     * @throws ResourceNotFoundException если изменяемое разрешение экрана не найдено по id.
     */
    @Override
    @Transactional
    public ResolutionDTO editPartOfEntity(Integer id, ResolutionDTO resolutionDTO) {
        var persistentScreenResolution = getPersistentEntityById(id);
        nullableMapper.map(resolutionDTO, persistentScreenResolution);
        screenResolutionRepository.save(persistentScreenResolution);
        return mapper.map(persistentScreenResolution, ResolutionDTO.class);
    }

    /**
     * Извлекает существующую hibernate сущность разрешения экрана из базы данных по его идентификатору и
     * возвращает её.
     *
     * @param id идентификатор hibernate сущности разрешения экрана, которую нужно получить.
     * @return объект ResolutionEntity, представляющий таблицу разрешения экрана из базы данных.
     * @throws ResourceNotFoundException если разрешение экрана с указанным идентификатором не найдено.
     */
    @Override
    public ResolutionEntity getPersistentEntityById(Integer id) {
        return screenResolutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screen resolution not found"));
    }

    /**
     * Извлекает существующую hibernate сущность разрешения экрана из базы данных по полученному DTO объекту.
     *
     * @param resolutionDTO объект передачи данных, содержащий сведения о разрешении экрана.
     * @return объект ResolutionEntity, представляющий таблицу разрешения экрана из базы данных.
     * @throws ResourceNotFoundException если разрешение экрана по entityDTO не найдено.
     * @throws InsufficientDataException если entityDTO равняется null или данных
     *                                   для извлечения разрешения экрана недостаточно.
     */
    @Override
    public ResolutionEntity getPersistentEntity(ResolutionDTO resolutionDTO) {
        if (resolutionDTO == null || resolutionDTO.getId() == null) {
            throw new InsufficientDataException("Screen resolution data missing");
        }
        return screenResolutionRepository.findById(resolutionDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen resolution not found"));
    }
}
