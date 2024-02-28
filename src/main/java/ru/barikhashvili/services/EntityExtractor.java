package ru.barikhashvili.services;

import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.exceptions.ResourceNotFoundException;

/**
 * Интерфейс для получения указанной hibernate сущности из базы данных по
 * уникальному идентификатору или по Data Transfer Object
 *
 * @param <D> Data Transfer Object изменяемой сущности.
 * @param <E> Класс возвращаемой hibernate сущности
 * @param <I> Класс уникального идентификатора сущности.
 */
public interface EntityExtractor<D, E, I> {
    /**
     * Извлекает существующую hibernate сущность из базы данных по ее идентификатору и
     * возвращает её.
     *
     * @param id идентификатор hibernate сущности, которую нужно получить.
     * @return объект E, представляющий таблицу сущности из базы данных.
     * @throws ResourceNotFoundException если сущность с указанным идентификатором не найдена.
     */
    E getPersistentEntityById(I id);

    /**
     * Извлекает существующую hibernate сущность из базы данных по полученному DTO объекту.
     *
     * @param entityDTO объект передачи данных, содержащий сведения о сущности.
     * @return объект E, представляющий таблицу сущности из базы данных.
     * @throws ResourceNotFoundException если сущность по entityDTO не найдена.
     * @throws InsufficientDataException если entityDTO равняется null или данных
     * для извлечения сущности не достаточно.
     */
    E getPersistentEntity(D entityDTO);
}
