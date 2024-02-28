package ru.barikhashvili.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barikhashvili.dto.specs.DisplayDTO;
import ru.barikhashvili.dto.specs.ResolutionDTO;
import ru.barikhashvili.entities.specs.DisplayEntity;
import ru.barikhashvili.entities.ResolutionEntity;
import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.exceptions.ResourceNotFoundException;
import ru.barikhashvili.repositories.DisplayRepository;
import ru.barikhashvili.services.CrudService;
import ru.barikhashvili.services.EntityExtractor;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DisplayService implements CrudService<DisplayDTO, Integer>,
        EntityExtractor<DisplayDTO, DisplayEntity, Integer> {
    EntityExtractor<ResolutionDTO, ResolutionEntity, Integer> resolutionExtractor;
    DisplayRepository displayRepository;
    ModelMapper mapper;
    ModelMapper nullableMapper;

    public DisplayService(DisplayRepository displayRepository,
                          EntityExtractor<ResolutionDTO, ResolutionEntity, Integer> resolutionExtractor,
                          @Qualifier("modelMapper") ModelMapper mapper,
                          @Qualifier("nullableModelMapper") ModelMapper nullableMapper) {
        this.displayRepository = displayRepository;
        this.resolutionExtractor = resolutionExtractor;
        this.mapper = mapper;
        this.nullableMapper = nullableMapper;
    }

    /**
     * Добавляет новый дисплей в базу данных и возвращает информацию о добавленном дисплее.
     * Этот метод требует, чтобы полученный в аргументе displayDTO содержал полную информацию
     * о добавляемом дисплее.
     *
     * @param displayDTO объект со сведениями о добавляемом дисплее (объект должен содержать все сведения).
     * @return объект DisplayDTO с полной информацией о добавленном дисплее из базы данных.
     * @throws InsufficientDataException если данные о добавляемом дисплее отсутствуют или указаны частично.
     */
    @Override
    @Transactional
    public DisplayDTO saveEntity(DisplayDTO displayDTO) {
        var transientDisplay = mapper.map(displayDTO, DisplayEntity.class);

        var resolutionDTO = displayDTO.getResolution();
        var persistentResolution = resolutionExtractor.getPersistentEntity(resolutionDTO);
        transientDisplay.setResolution(persistentResolution);

        var persistentDisplay = displayRepository.save(transientDisplay);

        return mapper.map(persistentDisplay, DisplayDTO.class);
    }

    /**
     * Находит и возвращает данные о дисплее с указанным идентификатором.
     *
     * @param id уникальный идентификатор дисплея, который необходимо найти
     * @return объект DisplayDTO с полной информацией о дисплее.
     * @throws ResourceNotFoundException если дисплей с указанным id не найден в базе данных
     */
    @Override
    public DisplayDTO getEntityById(Integer id) {
        var persistentDisplay = getPersistentEntityById(id);
        return mapper.map(persistentDisplay, DisplayDTO.class);
    }

    /**
     * Находит и возвращает список всех дисплеев, отсортированный в порядке возрастания
     * диагонали экранов и частоты обновления.
     *
     * @return список DisplayDTO с полными сведениями о всех дисплеях из базы данных.
     */
    @Override
    @Transactional
    public List<DisplayDTO> getAllEntities() {
        var persistentDisplays = displayRepository.findAllByOrderByDiagonalAscRefreshRateAsc();
        return persistentDisplays.stream()
                .map(display -> mapper.map(display, DisplayDTO.class))
                .toList();
    }

    /**
     * Удаляет дисплей с указанным id из базы данных и возвращает сведения об удаленном дисплее.
     *
     * @param id уникальный идентификатор дисплея, который необходимо удалить.
     * @throws ResourceNotFoundException если дисплей с указанным id не найден в базе данных.
     */
    @Override
    @Transactional
    public DisplayDTO removeEntityById(Integer id) {
        var removedDisplay = getEntityById(id);
        displayRepository.deleteById(id);
        return removedDisplay;
    }

    /**
     * Полностью обновляет информацию о дисплее в базе данных и возвращает объект с обновленной информацией.
     * Этот метод требует, чтобы полученный в аргументе displayDTO содержал полную информацию об изменяемом дисплее.
     * Важно: Этот метод не позволяет изменять id дисплея.
     *
     * @param id         уникальный идентификатор обновляемого дисплея, данные которого необходимо изменить.
     * @param displayDTO объект с новыми сведениями об изменяемом дисплее (Он должен содержать все сведения о дисплее).
     * @return объект DisplayDTO с обновленной информацией об измененном дисплее из базы данных.
     * @throws InsufficientDataException если данные об изменяемом дисплее отсутствуют.
     * @throws ResourceNotFoundException если изменяемый дисплей не найден по id.
     */
    @Override
    @Transactional
    public DisplayDTO editEntity(Integer id, DisplayDTO displayDTO) {
        var persistentDisplay = getPersistentEntityById(id);
        mapper.map(displayDTO, persistentDisplay);

        var resolutionDTO = displayDTO.getResolution();
        var persistentResolution = resolutionExtractor.getPersistentEntity(resolutionDTO);
        persistentDisplay.setResolution(persistentResolution);

        return mapper.map(persistentDisplay, DisplayDTO.class);
    }

    /**
     * Частично или полностью обновляет информацию о дисплее в базе данных и возвращает обновленную
     * информацию о дисплее. Если изменяемые данные пустые или не переданы, то изменения не происходят
     * и возвращается объект из базы данных по указанному идентификатору.
     * Важно: Этот метод не позволяет изменять id дисплея.
     *
     * @param id         уникальный идентификатор обновляемого дисплея, данные которого необходимо изменить.
     * @param displayDTO объект с обновленными сведениями об изменяемом дисплее.
     * @return объект DisplayDTO с обновленной информацией об измененном дисплее из базы данных.
     * @throws ResourceNotFoundException если изменяемый дисплей не найден по id или объект изменяемого
     * разрешения не найден в базе данных.
     */
    @Override
    @Transactional
    public DisplayDTO editPartOfEntity(Integer id, DisplayDTO displayDTO) {
        var persistentDisplay = getPersistentEntityById(id);
        nullableMapper.map(displayDTO, persistentDisplay);

        var resolutionDTO = displayDTO.getResolution();
        if (resolutionDTO != null && resolutionDTO.getId() != null) {
            var newResolutionId = resolutionDTO.getId();
            var persistentResolutionEntity = resolutionExtractor.getPersistentEntityById(newResolutionId);
            persistentDisplay.setResolution(persistentResolutionEntity);
        }

        displayRepository.save(persistentDisplay);
        return mapper.map(persistentDisplay, DisplayDTO.class);
    }

    /**
     * Извлекает существующую hibernate сущность дисплея из базы данных по его идентификатору и
     * возвращает её.
     *
     * @param id идентификатор hibernate сущности дисплея, которую нужно получить.
     * @return объект DisplayEntity, представляющий таблицу дисплея из базы данных.
     * @throws ResourceNotFoundException если дисплей с указанным идентификатором не найден.
     */
    @Override
    public DisplayEntity getPersistentEntityById(Integer id) {
        return displayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Display not found"));
    }

    /**
     * Извлекает существующую hibernate сущность дисплея из базы данных по полученному DTO объекту.
     *
     * @param displayDTO объект передачи данных, содержащий сведения о дисплее.
     * @return объект DisplayEntity, представляющий таблицу дисплея из базы данных.
     * @throws ResourceNotFoundException если дисплей по entityDTO не найден.
     * @throws InsufficientDataException если entityDTO равняется null или данных
     *                                   для извлечения дисплея недостаточно.
     */
    @Override
    public DisplayEntity getPersistentEntity(DisplayDTO displayDTO) {
        if (displayDTO == null || displayDTO.getId() == null) {
            throw new InsufficientDataException("Display data missing");
        }
        return displayRepository.findById(displayDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Display not found"));
    }
}
