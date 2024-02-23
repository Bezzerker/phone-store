package ru.barikhashvili.dto.specs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.barikhashvili.entities.specs.enums.ChargerType;
import ru.barikhashvili.entities.specs.enums.MaterialType;
import ru.barikhashvili.entities.specs.enums.NetworkType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhoneSpecificationDTO {
    Long id;

    NetworkType networkType;
    Integer simCount;
    Boolean hasWifi;
    Boolean hasNfc;
    Boolean hasBluetooth;
    BigDecimal height;
    BigDecimal width;
    BigDecimal thickness;
    BigDecimal weight;
    MaterialType material;
    ChargerType chargerType;

    OperatingSystemDTO operatingSystem;
    DisplayDTO display;
    ProcessorDTO processor;
    BatteryDTO battery;
    @Builder.Default
    List<CameraDTO> cameras = new ArrayList<>();
}
