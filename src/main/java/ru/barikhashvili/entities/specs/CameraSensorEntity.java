package ru.barikhashvili.entities.specs;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "camera_sensors")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = "cameras")
public class CameraSensorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String sensorName;

    BigDecimal megapixels;

    String matrixSize;

    String pixelSize;

    @Builder.Default
    @OneToMany(mappedBy = "sensor")
    List<CameraEntity> cameras = new ArrayList<>();

    public void addCamera(CameraEntity camera) {
        this.cameras.add(camera);
        camera.setSensor(this);
    }
}
