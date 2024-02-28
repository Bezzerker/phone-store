package ru.barikhashvili.services.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.barikhashvili.dto.PhoneVariantDTO;
import ru.barikhashvili.dto.VariantDTO;
import ru.barikhashvili.dto.specs.PhoneSpecificationDTO;
import ru.barikhashvili.entities.PhoneEntity;
import ru.barikhashvili.entities.PhoneVariantEntity;
import ru.barikhashvili.entities.specs.PhoneSpecificationEntity;
import ru.barikhashvili.entities.specs.VariantEntity;
import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.services.EntityExtractor;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DtoToEntityConverter {
    EntityExtractor<VariantDTO, VariantEntity, Integer> variantExtractor;

    /**
     * Создает объект PhoneSpecificationEntity, заполняет его значениями из PhoneSpecificationDTO,
     * после чего возвращает созданный объект
     *
     * @param phoneSpecificationDTO - DTO класс-источник, откуда будут браться данные для заполнения
     * @return transient hibernate объект, заполненный необходимыми данными.
     * @throws InsufficientDataException Если какой-либо из обязательных параметров в phoneSpecificationDTO отсутствует.
     */
    public PhoneSpecificationEntity phoneSpecDtoToEntity(PhoneSpecificationDTO phoneSpecificationDTO) {
        var phoneSpecificationEntity = new PhoneSpecificationEntity();
        fillEntityFromDTO(phoneSpecificationDTO, phoneSpecificationEntity);
        return phoneSpecificationEntity;
    }

    /**
     * Заполняет объект hibernate PhoneSpecificationEntity данными из PhoneSpecificationDTO.
     *
     * <p>Обязательные параметры: networkType (тип сети), simCount (количество SIM-карт),
     * hasWifi (наличие Wi-Fi), hasNfc (наличие NFC), hasBluetooth (наличие Bluetooth),
     * height (высота), width (ширина), thickness (толщина), weight (вес), material (материал),
     * chargerType (тип зарядки)</p>
     *
     * @param phoneSpecificationDTO    Объект DTO, содержащий спецификации телефона.
     * @param phoneSpecificationEntity Объект сущности, в который будут записаны спецификации.
     * @throws InsufficientDataException Если какой-либо из обязательных параметров в phoneSpecificationDTO отсутствует.
     */
    public void fillEntityFromDTO(PhoneSpecificationDTO phoneSpecificationDTO, PhoneSpecificationEntity phoneSpecificationEntity) {
        if (phoneSpecificationDTO.getNetworkType() == null || phoneSpecificationDTO.getSimCount() == null
                || phoneSpecificationDTO.getHasWifi() == null || phoneSpecificationDTO.getHasNfc() == null
                || phoneSpecificationDTO.getHasBluetooth() == null || phoneSpecificationDTO.getHeight() == null
                || phoneSpecificationDTO.getWidth() == null || phoneSpecificationDTO.getThickness() == null
                || phoneSpecificationDTO.getWeight() == null || phoneSpecificationDTO.getMaterial() == null
                || phoneSpecificationDTO.getChargerType() == null) {
            throw new InsufficientDataException("Not all data is provided");
        }

        phoneSpecificationEntity.setNetworkType(phoneSpecificationDTO.getNetworkType());
        phoneSpecificationEntity.setSimCount(phoneSpecificationDTO.getSimCount());
        phoneSpecificationEntity.setHasWifi(phoneSpecificationDTO.getHasWifi());
        phoneSpecificationEntity.setHasNfc(phoneSpecificationDTO.getHasNfc());
        phoneSpecificationEntity.setHasBluetooth(phoneSpecificationDTO.getHasBluetooth());
        phoneSpecificationEntity.setHeight(phoneSpecificationDTO.getHeight());
        phoneSpecificationEntity.setWidth(phoneSpecificationDTO.getWidth());
        phoneSpecificationEntity.setThickness(phoneSpecificationDTO.getThickness());
        phoneSpecificationEntity.setWeight(phoneSpecificationDTO.getWeight());
        phoneSpecificationEntity.setMaterial(phoneSpecificationDTO.getMaterial());
        phoneSpecificationEntity.setChargerType(phoneSpecificationDTO.getChargerType());
    }

    /**
     * Создает объект PhoneVariantEntity, заполняет его значениями из PhoneVariantDTO,
     * после чего возвращает созданный объект
     *
     * @param dto объект передачи данных варианта телефона, содержащий информацию для создания сущности.
     * @return PhoneVariantEntity сущность варианта телефона, содержащая информацию из DTO.
     * @throws InsufficientDataException если цена или количество не предоставлены в DTO.
     */
    public PhoneVariantEntity phoneVariantDtoToEntity(PhoneVariantDTO dto) {
        if (dto.getPrice() == null || dto.getQuantity() == null) {
            throw new InsufficientDataException("Not all data is provided");
        }

        var variant = variantExtractor.getPersistentEntity(dto.getVariant());

        PhoneVariantEntity phoneVariantEntity = new PhoneVariantEntity();
        phoneVariantEntity.setVariant(variant);
        phoneVariantEntity.setPrice(dto.getPrice());
        phoneVariantEntity.setQuantity(dto.getQuantity());
        return phoneVariantEntity;
    }

    /**
     * Конвертирует список DTO вариантов телефона в список сущностей PhoneVariantEntity.
     *
     * @param dtoList список объектов передачи данных вариантов телефона для конвертации.
     * @param phoneEntity сущность телефона, к которой будут привязаны варианты.
     * @return <PhoneVariantEntity> список сущностей вариантов телефона, связанных с телефоном.
     * @throws InsufficientDataException если в любом из DTO цена или количество не предоставлены.
     */
    public List<PhoneVariantEntity> phoneVariantDtoListToEntities(
            List<PhoneVariantDTO> dtoList,
            PhoneEntity phoneEntity) {
            return dtoList.stream()
                .map(variantDto ->
                {
                    var variantEntity = phoneVariantDtoToEntity(variantDto);
                    variantEntity.setPhone(phoneEntity);
                    return variantEntity;
                })
                .toList();
    }
}
