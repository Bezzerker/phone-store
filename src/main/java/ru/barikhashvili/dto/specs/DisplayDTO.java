package ru.barikhashvili.dto.specs;

import lombok.Data;
import ru.barikhashvili.entities.specs.enums.DisplayType;

import java.math.BigDecimal;

@Data
public class DisplayDTO {
    Integer id;
    Integer refreshRate;
    BigDecimal diagonal;
    DisplayType displayType;
    ResolutionDTO resolution;
}
