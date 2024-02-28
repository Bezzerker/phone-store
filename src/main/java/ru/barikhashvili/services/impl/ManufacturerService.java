package ru.barikhashvili.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barikhashvili.dto.specs.CountryDTO;
import ru.barikhashvili.dto.specs.ManufacturerDTO;
import ru.barikhashvili.entities.specs.CountryEntity;
import ru.barikhashvili.entities.specs.ManufacturerEntity;
import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.exceptions.ResourceNotFoundException;
import ru.barikhashvili.repositories.ManufacturerRepository;
import ru.barikhashvili.services.CrudService;
import ru.barikhashvili.services.EntityExtractor;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ManufacturerService implements CrudService<ManufacturerDTO, Integer>,
        EntityExtractor<ManufacturerDTO, ManufacturerEntity, Integer> {
    EntityExtractor<CountryDTO, CountryEntity, Integer> countryExtractor;
    ManufacturerRepository manufacturerRepository;
    ModelMapper mapper;
    ModelMapper nullableMapper;

    public ManufacturerService(
            EntityExtractor<CountryDTO, CountryEntity, Integer> countryExtractor,
            ManufacturerRepository manufacturerRepository,
            @Qualifier("modelMapper") ModelMapper mapper,
            @Qualifier("nullableModelMapper") ModelMapper nullableMapper) {
        this.countryExtractor = countryExtractor;
        this.manufacturerRepository = manufacturerRepository;
        this.mapper = mapper;
        this.nullableMapper = nullableMapper;
    }

    /**
     * Добавляет нового указанного производителя в базу данных и возвращает DTO с информацией
     * о добавленном производителе. Этот метод требует полного объекта ManufacturerDTO, содержащего
     * информацию о производителе, включая информацию о стране.
     *
     * @param manufacturerDTO объект DTO со сведениями о добавляемом производителе (DTO обязательно
     *                        должен включать название производителя и id страны производителя).
     * @return объект ManufacturerDTO с полной информацией о добавленном производителе из базы данных.
     * @throws InsufficientDataException если данные о стране или производителе отсутствуют.
     * @throws ResourceNotFoundException новая страна из manufacturerDTO не найдена по id.
     */
    @Override
    @Transactional
    public ManufacturerDTO saveEntity(ManufacturerDTO manufacturerDTO) {
        var transientManufacturer = mapper.map(manufacturerDTO, ManufacturerEntity.class);

        var countryDTO = manufacturerDTO.getCountry();
        var persistentCountry = countryExtractor.getPersistentEntity(countryDTO);
        transientManufacturer.setCountry(persistentCountry);

        var persistentManufacturer = manufacturerRepository.save(transientManufacturer);

        return mapper.map(persistentManufacturer, ManufacturerDTO.class);
    }

    /**
     * Находит из возвращает данные о производителе с указанным идентификатором в виде DTO.
     *
     * @param manufacturerId уникальный идентификатор производителя, которого необходимо найти
     * @return объект ManufacturerDTO с полными сведениями о производителе.
     * @throws ResourceNotFoundException если производитель с указанным id не найден в базе данных
     */
    @Override
    public ManufacturerDTO getEntityById(Integer manufacturerId) {
        var persistentManufacturer = getPersistentEntityById(manufacturerId);
        return mapper.map(persistentManufacturer, ManufacturerDTO.class);
    }

    /**
     * Находит и возвращает список всех производителей, отсортированный в алфавитном порядке по названию имён.
     *
     * @return список ManufacturerDTO с полными сведениями о всех производителях из базы данных.
     */
    @Override
    @Transactional
    public List<ManufacturerDTO> getAllEntities() {
        var persistentManufacturers = manufacturerRepository.findAllByOrderByNameAsc();
        return persistentManufacturers.stream()
                .map(manufacturer -> mapper.map(manufacturer, ManufacturerDTO.class))
                .toList();
    }

    /**
     * Удаляет производителя с указанным id из базы данных и возвращает DTO с данными
     * об удаленном производителе.
     *
     * @param manufacturerId уникальный идентификатор производителя, которого надо удалить.
     * @throws ResourceNotFoundException если производитель с указанным id не найден в базе данных.
     */
    @Override
    @Transactional
    public ManufacturerDTO removeEntityById(Integer manufacturerId) {
        var removedManufacturer = getEntityById(manufacturerId);
        manufacturerRepository.deleteById(manufacturerId);
        return removedManufacturer;
    }

    /**
     * Полностью обновляет информацию о производителе в базе данных и возвращает DTO с обновленной информацией.
     * Этот метод требует полного объекта ManufacturerDTO, содержащего информацию о производителе,
     * включая информацию о стране.
     * Важно: Этот метод не позволяет изменять id производителя и название страны производителя.
     *
     * @param manufacturerId  уникальный идентификатор производителя, данные которого необходимо изменить.
     * @param manufacturerDTO объект DTO с новыми сведениями о производителе (DTO обязательно должен
     *                        включать название производителя и id страны производителя).
     * @return объект ManufacturerDTO с обновленной информацией о производителе из базы данных.
     * @throws InsufficientDataException если данные о стране или производителе отсутствуют.
     * @throws ResourceNotFoundException если производитель не найден по manufacturerId или новая страна
     *                                   из manufacturerDTO не найдена по id.
     */
    @Override
    @Transactional
    public ManufacturerDTO editEntity(Integer manufacturerId, ManufacturerDTO manufacturerDTO) {
        var persistentManufacturer = getPersistentEntityById(manufacturerId);

        mapper.map(manufacturerDTO, persistentManufacturer);

        var countryDTO = manufacturerDTO.getCountry();
        var persistentCountry = countryExtractor.getPersistentEntity(countryDTO);
        persistentManufacturer.setCountry(persistentCountry);

        return mapper.map(persistentManufacturer, ManufacturerDTO.class);
    }

    /**
     * Частично или полностью обновляет информацию о производителе в базе данных и возвращает DTO
     * с обновленной информацией о производителе. Если изменяемые данные пустые или не переданы, то
     * изменения не происходят и возвращается объект из базы данных по указанному идентификатору.
     * Важно: Этот метод не позволяет изменять id производителя и название страны производителя.
     *
     * @param manufacturerId  уникальный идентификатор производителя, данные которого необходимо изменить.
     * @param manufacturerDTO объект DTO с обновленными данными о производителе. Может содержать как полностью
     *                        все данные о производителе, так и часть данных.
     * @return объект ManufacturerDTO с обновленной информацией о производителе из базы данных.
     * @throws ResourceNotFoundException если производитель не найден по manufacturerId или новая страна
     *                                   из manufacturerDTO не найдена по id.
     */
    @Override
    @Transactional
    public ManufacturerDTO editPartOfEntity(Integer manufacturerId, ManufacturerDTO manufacturerDTO) {
        var persistentManufacturer = getPersistentEntityById(manufacturerId);

        nullableMapper.map(manufacturerDTO, persistentManufacturer);

        var countryDTO = manufacturerDTO.getCountry();
        if (countryDTO != null && countryDTO.getId() != null) {
            var newCountryId = countryDTO.getId();
            var persistentCountryEntity = countryExtractor.getPersistentEntityById(newCountryId);
            persistentManufacturer.setCountry(persistentCountryEntity);
        }

        return mapper.map(persistentManufacturer, ManufacturerDTO.class);
    }

    /**
     * Находит существующего в базе данных производителя по указанному идентификатору.
     * Если производитель не найден, то выбрасывается исключение ResourceNotFoundException.
     *
     * @param id уникальный идентификатор производителя
     * @return сущность производителя, найденного в базе данных
     * @throws ResourceNotFoundException если производитель с указанным id не найден в базе данных
     */
    @Override
    public ManufacturerEntity getPersistentEntityById(Integer id) {
        return manufacturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer not found"));
    }

    /**
     * Извлекает существующую hibernate сущность производителя из базы данных по
     * полученному DTO объекту производителя.
     *
     * @param manufacturerDTO объект передачи данных, содержащий сведения о производителе.
     * @return объект E, представляющий таблицу производителя из базы данных.
     * @throws ResourceNotFoundException если производитель по manufacturerDTO не найден.
     * @throws InsufficientDataException если manufacturerDTO равняется null или данных
     *                                   для извлечения сущности не достаточно.
     */
    @Override
    public ManufacturerEntity getPersistentEntity(ManufacturerDTO manufacturerDTO) {
        if (manufacturerDTO == null || manufacturerDTO.getId() == null) {
            throw new InsufficientDataException("Manufacturer data missing");
        }
        return manufacturerRepository.findById(manufacturerDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer not found"));
    }
}