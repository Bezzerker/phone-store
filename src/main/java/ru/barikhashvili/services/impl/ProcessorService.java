package ru.barikhashvili.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barikhashvili.dto.specs.ProcessorDTO;
import ru.barikhashvili.entities.specs.ProcessorEntity;
import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.exceptions.ResourceNotFoundException;
import ru.barikhashvili.repositories.ProcessorRepository;
import ru.barikhashvili.services.CrudService;
import ru.barikhashvili.services.EntityExtractor;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProcessorService implements CrudService<ProcessorDTO, Integer>,
        EntityExtractor<ProcessorDTO, ProcessorEntity, Integer> {
    ProcessorRepository processorRepository;
    ModelMapper mapper;
    ModelMapper nullableMapper;

    public ProcessorService(ProcessorRepository processorRepository,
                            @Qualifier("modelMapper") ModelMapper mapper,
                            @Qualifier("nullableModelMapper") ModelMapper nullableMapper) {
        this.processorRepository = processorRepository;
        this.mapper = mapper;
        this.nullableMapper = nullableMapper;
    }

    /**
     * Добавляет новый процессор в базу данных и возвращает информацию о добавленном процессоре.
     * Этот метод требует, чтобы полученный в аргументе processorDTO содержал полную информацию
     * о добавляемом процессоре.
     *
     * @param processorDTO объект со сведениями о добавляемом процессоре (объект должен содержать все сведения).
     * @return объект ProcessorDTO с полной информацией о добавленном процессоре из базы данных.
     * @throws InsufficientDataException если данные о добавляемом процессоре отсутствуют или указаны частично.
     */
    @Override
    @Transactional
    public ProcessorDTO saveEntity(ProcessorDTO processorDTO) {
        var transientProcessor = mapper.map(processorDTO, ProcessorEntity.class);
        var persistentProcessor = processorRepository.save(transientProcessor);

        return mapper.map(persistentProcessor, ProcessorDTO.class);
    }

    /**
     * Находит и возвращает данные о процессоре с указанным идентификатором.
     *
     * @param id уникальный идентификатор процессора, который необходимо найти
     * @return объект ProcessorDTO с полной информацией о процессоре.
     * @throws ResourceNotFoundException если процессор с указанным id не найден в базе данных
     */
    @Override
    public ProcessorDTO getEntityById(Integer id) {
        var persistentProcessor = getPersistentEntityById(id);
        return mapper.map(persistentProcessor, ProcessorDTO.class);
    }

    /**
     * Находит и возвращает список всех процессоров, отсортированный в порядке увеличения
     * технологического процесса и алфавитного порядка названия процессора.
     *
     * @return список ProcessorDTO с полными сведениями о всех процессорах из базы данных.
     */
    @Override
    public List<ProcessorDTO> getAllEntities() {
        var persistentProcessors = processorRepository.findAllByOrderByTechnologyNodeAscModelAsc();
        return persistentProcessors.stream()
                .map(processor -> mapper.map(processor, ProcessorDTO.class))
                .toList();
    }

    /**
     * Удаляет процессор с указанным id из базы данных и возвращает сведения об удаленном процессоре.
     *
     * @param id уникальный идентификатор процессора, который необходимо удалить.
     * @throws ResourceNotFoundException если процессор с указанным id не найден в базе данных.
     */
    @Override
    @Transactional
    public ProcessorDTO removeEntityById(Integer id) {
        var removedProcessor = getEntityById(id);
        processorRepository.deleteById(id);
        return removedProcessor;
    }

    /**
     * Полностью обновляет информацию о процессоре в базе данных и возвращает объект с обновленной информацией.
     * Этот метод требует, чтобы полученный в аргументе processorDTO содержал полную информацию об изменяемом процессоре.
     * Важно: Этот метод не позволяет изменять id процессора.
     *
     * @param id           уникальный идентификатор обновляемого процессора, данные которого необходимо изменить.
     * @param processorDTO объект с новыми сведениями об изменяемом процессоре (Он должен содержать все сведения о процессоре).
     * @return объект ProcessorDTO с обновленной информацией об измененном процессоре из базы данных.
     * @throws InsufficientDataException если данные об изменяемом процессоре отсутствуют.
     * @throws ResourceNotFoundException если изменяемый процессор не найден по id.
     */
    @Override
    @Transactional
    public ProcessorDTO editEntity(Integer id, ProcessorDTO processorDTO) {
        var persistentProcessor = getPersistentEntityById(id);
        mapper.map(processorDTO, persistentProcessor);
        processorRepository.save(persistentProcessor);
        return mapper.map(persistentProcessor, ProcessorDTO.class);
    }

    /**
     * Частично или полностью обновляет информацию о процессоре в базе данных и возвращает обновленную
     * информацию о процессоре. Если изменяемые данные пустые или не переданы, то изменения не происходят
     * и возвращается объект из базы данных по указанному идентификатору.
     * Важно: Этот метод не позволяет изменять id процессора.
     *
     * @param id           уникальный идентификатор обновляемого процессора, данные которого необходимо изменить.
     * @param processorDTO объект с обновленными сведениями об изменяемом процессоре.
     * @return объект ProcessorDTO с обновленной информацией об измененном процессоре из базы данных.
     * @throws ResourceNotFoundException если изменяемый процессор не найден по id.
     */
    @Override
    @Transactional
    public ProcessorDTO editPartOfEntity(Integer id, ProcessorDTO processorDTO) {
        var persistentProcessor = getPersistentEntityById(id);
        nullableMapper.map(processorDTO, persistentProcessor);
        processorRepository.save(persistentProcessor);
        return mapper.map(persistentProcessor, ProcessorDTO.class);
    }

    /**
     * Извлекает существующую hibernate сущность процессора из базы данных по его идентификатору и
     * возвращает её.
     *
     * @param id идентификатор hibernate сущности процессора, которую нужно получить.
     * @return объект ProcessorEntity, представляющий таблицу процессора из базы данных.
     * @throws ResourceNotFoundException если процессор с указанным идентификатором не найден.
     */
    @Override
    public ProcessorEntity getPersistentEntityById(Integer id) {
        return processorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Processor not found"));
    }

    /**
     * Извлекает существующую hibernate сущность процессора из базы данных по полученному DTO объекту.
     *
     * @param processorDTO объект передачи данных, содержащий сведения о процессоре.
     * @return объект ProcessorEntity, представляющий таблицу процессора из базы данных.
     * @throws ResourceNotFoundException если процессор по entityDTO не найден.
     * @throws InsufficientDataException если entityDTO равняется null или данных
     *                                   для извлечения процессора недостаточно.
     */
    @Override
    public ProcessorEntity getPersistentEntity(ProcessorDTO processorDTO) {
        if (processorDTO == null || processorDTO.getId() == null) {
            throw new InsufficientDataException("Processor data missing");
        }
        return processorRepository.findById(processorDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Processor not found"));
    }
}
