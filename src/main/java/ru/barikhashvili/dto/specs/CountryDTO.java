package ru.barikhashvili.dto.specs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CountryDTO {
    Integer id;
    String name;
}
