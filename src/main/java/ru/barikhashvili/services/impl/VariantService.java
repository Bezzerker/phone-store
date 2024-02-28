package ru.barikhashvili.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barikhashvili.dto.VariantDTO;
import ru.barikhashvili.entities.specs.VariantEntity;
import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.exceptions.ResourceNotFoundException;
import ru.barikhashvili.repositories.VariantRepository;
import ru.barikhashvili.services.CrudService;
import ru.barikhashvili.services.EntityExtractor;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class VariantService implements CrudService<VariantDTO, Integer>,
        EntityExtractor<VariantDTO, VariantEntity, Integer> {
    VariantRepository variantRepository;
    ModelMapper mapper;
    ModelMapper nullableMapper;

    public VariantService(VariantRepository variantRepository,
                            @Qualifier("modelMapper") ModelMapper mapper,
                            @Qualifier("nullableModelMapper") ModelMapper nullableMapper) {
        this.variantRepository = variantRepository;
        this.mapper = mapper;
        this.nullableMapper = nullableMapper;
    }

    /**
     * Добавляет новый вариант спецификации телефона в базу данных и возвращает информацию о добавленном варианте.
     * Этот метод требует, чтобы полученный в аргументе variantDTO содержал полную информацию
     * о добавляемом варианте.
     *
     * @param variantDTO объект со сведениями о добавляемом варианте (объект должен содержать все сведения).
     * @return объект VariantDTO с полной информацией о добавленном варианте из базы данных.
     * @throws InsufficientDataException если данные о добавляемом варианте отсутствуют или указаны частично.
     */
    @Override
    @Transactional
    public VariantDTO saveEntity(VariantDTO variantDTO) {
        var transientVariant = mapper.map(variantDTO, VariantEntity.class);
        var persistentVariant = variantRepository.save(transientVariant);

        return mapper.map(persistentVariant, VariantDTO.class);
    }

    /**
     * Находит и возвращает данные о варианте спецификации телефона с указанным идентификатором.
     *
     * @param id уникальный идентификатор варианта спецификации телефона, который необходимо найти
     * @return объект VariantDTO с полной информацией о варианте спецификации телефона.
     * @throws ResourceNotFoundException если вариант спецификации телефона с указанным id не найден в базе данных
     */
    @Override
    public VariantDTO getEntityById(Integer id) {
        var persistentVariant = getPersistentEntityById(id);
        return mapper.map(persistentVariant, VariantDTO.class);
    }

    /**
     * Находит и возвращает список всех вариантов спецификации телефонов, отсортированный в порядке увеличения
     * ёмкости оперативной памяти и постоянной памяти.
     *
     * @return список VariantDTO с полными сведениями о всех вариантах спецификации телефонов из базы данных.
     */
    @Override
    public List<VariantDTO> getAllEntities() {
        var persistentVariants = variantRepository.findAllByOrderByRamSizeAscRomSizeAsc();
        return persistentVariants.stream()
                .map(variant -> mapper.map(variant, VariantDTO.class))
                .toList();
    }

    /**
     * Удаляет вариант спецификации телефона с указанным id из базы данных и возвращает сведения об удаленном варианте.
     *
     * @param id уникальный идентификатор варианта спецификации телефона, который необходимо удалить.
     * @throws ResourceNotFoundException если вариант спецификации телефона с указанным id не найден в базе данных.
     */
    @Override
    @Transactional
    public VariantDTO removeEntityById(Integer id) {
        var removedVariant = getEntityById(id);
        variantRepository.deleteById(id);
        return removedVariant;
    }

    /**
     * Полностью обновляет информацию о варианте спецификации телефона в базе данных и возвращает объект с обновленной информацией.
     * Этот метод требует, чтобы полученный в аргументе variantDTO содержал полную информацию об изменяемом варианте.
     * Важно: Этот метод не позволяет изменять id варианта спецификации телефона.
     *
     * @param id           уникальный идентификатор обновляемого варианта спецификации телефона, данные которого необходимо изменить.
     * @param variantDTO объект с новыми сведениями об изменяемом варианте спецификации телефона (Он должен содержать все сведения о варианте).
     * @return объект VariantDTO с обновленной информацией об измененном варианте спецификации телефона из базы данных.
     * @throws InsufficientDataException если данные об изменяемом варианте спецификации телефона отсутствуют.
     * @throws ResourceNotFoundException если изменяемый вариант спецификации телефона не найден по id.
     */
    @Override
    @Transactional
    public VariantDTO editEntity(Integer id, VariantDTO variantDTO) {
        var persistentVariant = getPersistentEntityById(id);
        mapper.map(variantDTO, persistentVariant);
        variantRepository.save(persistentVariant);
        return mapper.map(persistentVariant, VariantDTO.class);
    }

    /**
     * Частично или полностью обновляет информацию о варианте спецификации телефона в базе данных и возвращает обновленную
     * информацию о варианте спецификации телефона. Если изменяемые данные пустые или не переданы, то изменения не происходят
     * и возвращается объект из базы данных по указанному идентификатору.
     * Важно: Этот метод не позволяет изменять id варианта спецификации телефона.
     *
     * @param id           уникальный идентификатор обновляемого варианта спецификации телефона, данные которого необходимо изменить.
     * @param variantDTO объект с обновленными сведениями об изменяемом варианте спецификации телефона.
     * @return объект VariantDTO с обновленной информацией об измененном варианте спецификации телефона из базы данных.
     * @throws ResourceNotFoundException если изменяемый вариант спецификации телефона не найден по id.
     */
    @Override
    @Transactional
    public VariantDTO editPartOfEntity(Integer id, VariantDTO variantDTO) {
        var persistentVariant = getPersistentEntityById(id);
        nullableMapper.map(variantDTO, persistentVariant);
        variantRepository.save(persistentVariant);
        return mapper.map(persistentVariant, VariantDTO.class);
    }

    /**
     * Извлекает существующую hibernate сущность варианта спецификации телефона из базы данных по его идентификатору и
     * возвращает её.
     *
     * @param id идентификатор hibernate сущности варианта спецификации телефона, которую нужно получить.
     * @return объект VariantEntity, представляющий таблицу варианта спецификации телефона из базы данных.
     * @throws ResourceNotFoundException если вариант спецификации телефона с указанным идентификатором не найден.
     */
    @Override
    public VariantEntity getPersistentEntityById(Integer id) {
        return variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));
    }

    /**
     * Извлекает существующую hibernate сущность варианта спецификации телефона из базы данных по полученному DTO объекту.
     *
     * @param variantDTO объект передачи данных, содержащий сведения о варианте спецификации телефона.
     * @return объект VariantEntity, представляющий таблицу варианта спецификации телефона из базы данных.
     * @throws ResourceNotFoundException если вариант спецификации телефона по entityDTO не найден.
     * @throws InsufficientDataException если entityDTO равняется null или данных
     *                                   для извлечения варианта спецификации телефона недостаточно.
     */
    @Override
    public VariantEntity getPersistentEntity(VariantDTO variantDTO) {
        if (variantDTO == null || variantDTO.getId() == null) {
            throw new InsufficientDataException("Variant data missing");
        }
        return variantRepository.findById(variantDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));
    }
}
