package ru.barikhashvili.entities.specs;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.barikhashvili.entities.specs.enums.CameraType;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "cameras")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = "specifications")
public class CameraEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.ORDINAL)
    CameraType cameraType;

    boolean hasOpticalStabilization;

    @ManyToOne(fetch = FetchType.LAZY)
    CameraSensorEntity sensor;

    @Builder.Default
    @ManyToMany(mappedBy = "cameras")
    List<PhoneSpecificationEntity> specifications = new ArrayList<>();
}
