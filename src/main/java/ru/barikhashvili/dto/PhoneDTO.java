package ru.barikhashvili.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.barikhashvili.dto.specs.ManufacturerDTO;
import ru.barikhashvili.dto.specs.PhoneSpecificationDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhoneDTO {
    Long id;
    ManufacturerDTO manufacturer;
    String model;
    PhoneSpecificationDTO specification;
    LocalDateTime releaseDate;
    @Builder.Default
    List<PhoneVariantDTO> phoneVariants = new ArrayList<>();
}
