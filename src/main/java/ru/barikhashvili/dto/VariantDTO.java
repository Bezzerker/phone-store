package ru.barikhashvili.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.barikhashvili.entities.specs.enums.Color;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariantDTO {
    Integer id;
    Integer romSize;
    Integer ramSize;
    Color color;
}
