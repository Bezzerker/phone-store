package ru.barikhashvili.entities.specs;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.barikhashvili.entities.specs.enums.BatteryType;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "batteries")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = "phoneSpecifications")
public class BatteryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    int capacity;

    @Enumerated(EnumType.ORDINAL)
    BatteryType batteryType;

    @Builder.Default
    @OneToMany(mappedBy = "battery")
    List<PhoneSpecificationEntity> phoneSpecifications = new ArrayList<>();

    public void addSpecification(PhoneSpecificationEntity spec) {
        this.phoneSpecifications.add(spec);
        spec.setBattery(this);
    }
}
