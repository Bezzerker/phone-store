package ru.barikhashvili.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barikhashvili.dto.specs.CountryDTO;
import ru.barikhashvili.entities.specs.CountryEntity;
import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.exceptions.ResourceNotFoundException;
import ru.barikhashvili.repositories.CountryRepository;
import ru.barikhashvili.services.CrudService;
import ru.barikhashvili.services.EntityExtractor;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CountryService implements CrudService<CountryDTO, Integer>,
        EntityExtractor<CountryDTO, CountryEntity, Integer> {
    CountryRepository countryRepository;
    ModelMapper mapper;
    ModelMapper nullableMapper;

    public CountryService(CountryRepository countryRepository,
                          @Qualifier("modelMapper") ModelMapper mapper,
                          @Qualifier("nullableModelMapper") ModelMapper nullableMapper) {
        this.countryRepository = countryRepository;
        this.mapper = mapper;
        this.nullableMapper = nullableMapper;
    }

    /**
     * Сохраняет страну в базу данных и возвращает DTO с информацией о добавленной стране.
     *
     * @param countryDTO объект DTO с названием о добавляемой стране (название обязательно должно присутствовать).
     * @return объект CountryDTO с полной информацией о добавленной стране из базы данных.
     */
    @Override
    @Transactional
    public CountryDTO saveEntity(CountryDTO countryDTO) {
        var transientCountry = mapper.map(countryDTO, CountryEntity.class);
        var persistentCountry = countryRepository.save(transientCountry);
        return mapper.map(persistentCountry, CountryDTO.class);
    }

    /**
     * Находит из возвращает полную информацию о стране с указанным идентификатором в виде DTO.
     *
     * @param countryId уникальный идентификатор страны, которую необходимо найти.
     * @return объект CountryDTO c идентификатором и названием страны.
     * @throws ResourceNotFoundException если страна с указанным идентификатором не найдена.
     */
    @Override
    public CountryDTO getEntityById(Integer countryId) {
        var persistentCountry = getPersistentEntityById(countryId);
        return mapper.map(persistentCountry, CountryDTO.class);
    }

    /**
     * Находит и возвращает список всех стран, отсортированный в алфавитном порядке по названию стран.
     *
     * @return список CountryDTO с полными сведениями о всех странах из базы данных.
     */
    @Override
    public List<CountryDTO> getAllEntities() {
        var persistentCountries = countryRepository.findAllByOrderByNameAsc();
        return persistentCountries.stream()
                .map(countryEntity -> mapper.map(countryEntity, CountryDTO.class))
                .toList();
    }

    /**
     * Удаляет страну с указанным идентификатором из базы данных и возвращает DTO со сведениями
     * об удаленной стране.
     *
     * @param countryId уникальный идентификатор страны, которую надо удалить.
     * @throws ResourceNotFoundException если страна с указанным идентификатором не найдена.
     */
    @Override
    @Transactional
    public CountryDTO removeEntityById(Integer countryId) {
        var removedCountry = getEntityById(countryId);
        countryRepository.deleteById(countryId);
        return removedCountry;
    }

    /**
     * Обновляет информацию о стране в базе данных и возвращает DTO с обновленной информацией.
     *
     * @param countryId  уникальный идентификатор страны, данные которой необходимо изменить.
     * @param countryDTO объект DTO с новыми сведениями о стране (обязательно должно
     *                   присутствовать название страны).
     * @return объект CountryDTO с обновленной информацией о стране из базы данных.
     * @throws ResourceNotFoundException если страна с указанным идентификатором не найдена.
     */
    @Override
    @Transactional
    public CountryDTO editEntity(Integer countryId, CountryDTO countryDTO) {
        var persistentCountry = getPersistentEntityById(countryId);
        persistentCountry.setName(countryDTO.getName());
        return mapper.map(persistentCountry, CountryDTO.class);
    }

    /**
     * Частично или полностью обновляет информацию о стране в базе данных и возвращает обновлённую
     * информацию о стране. Если изменяемые данные пустые или не переданы, то изменения не происходят
     * и возвращается объект из базы данных по указанному идентификатору.
     * Важно: Этот метод не позволяет изменять id страны.
     *
     * @param countryId уникальный идентификатор обновляемой страны, данные которой необходимо изменить.
     * @param countryDTO объект с обновленными сведениями об изменяемой стране.
     * @return объект CountryDTO с обновленной информацией об изменяемой стране из базы данных.
     * @throws ResourceNotFoundException если изменяемая страна не найдена по id.
     */
    @Override
    @Transactional
    public CountryDTO editPartOfEntity(Integer countryId, CountryDTO countryDTO) {
        var persistentCountry = getPersistentEntityById(countryId);
        nullableMapper.map(countryDTO, persistentCountry);
        countryRepository.save(persistentCountry);
        return mapper.map(persistentCountry, CountryDTO.class);
    }

    /**
     * Извлекает страну из базы данных по ее идентификатору для внутреннего использования.
     *
     * @param id идентификатор страны, которую нужно извлечь.
     * @return объект CountryEntity, представляющий извлеченную страну в базе данных.
     * @throws ResourceNotFoundException если страна с указанным идентификатором не найдена.
     */
    @Override
    public CountryEntity getPersistentEntityById(Integer id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found"));
    }

    /**
     * Извлекает страну из базы данных по идентификатору CountryDTO для внутреннего использования.
     *
     * @param countryDTO объект передачи данных, содержащий сведения о стране.
     * @return объект CountryEntity, представляющий извлеченную страну в базе данных.
     * @throws ResourceNotFoundException если страна с идентификатором из CountryDTO не найдена.
     * @throws InsufficientDataException если countryDTO равняется null или в countryDTO отсутствует id страны.
     */
    @Override
    public CountryEntity getPersistentEntity(CountryDTO countryDTO) {
        if (countryDTO == null || countryDTO.getId() == null) {
            throw new InsufficientDataException("Country data missing");
        }
        return countryRepository.findById(countryDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found"));
    }
}
