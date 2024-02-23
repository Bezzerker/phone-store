package ru.barikhashvili.dto.specs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.barikhashvili.entities.specs.enums.CameraType;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CameraDTO {
    Long id;
    CameraType cameraType;
    Boolean hasOpticalStabilization;
    CameraSensorDTO sensor;
}
