package ru.barikhashvili.entities.specs;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.barikhashvili.entities.PhoneEntity;
import ru.barikhashvili.entities.specs.enums.ChargerType;
import ru.barikhashvili.entities.specs.enums.MaterialType;
import ru.barikhashvili.entities.specs.enums.NetworkType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "phone_specs")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = {"cameras", "phone"})
public class PhoneSpecificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Enumerated(EnumType.ORDINAL)
    NetworkType networkType;
    Integer simCount;
    Boolean hasWifi;
    Boolean hasNfc;
    Boolean hasBluetooth;
    BigDecimal height;
    BigDecimal width;
    BigDecimal thickness;
    BigDecimal weight;

    @Enumerated(EnumType.ORDINAL)
    MaterialType material;
    @Enumerated(EnumType.ORDINAL)
    ChargerType chargerType;

    @ManyToOne(fetch = FetchType.LAZY)
    OperatingSystemEntity operatingSystem;

    @ManyToOne(fetch = FetchType.LAZY)
    DisplayEntity display;

    @ManyToOne(fetch = FetchType.LAZY)
    ProcessorEntity processor;

    @ManyToOne(fetch = FetchType.LAZY)
    BatteryEntity battery;

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "specifications_cameras",
            joinColumns = @JoinColumn(name = "specification_id"),
            inverseJoinColumns = @JoinColumn(name = "camera_id"))
    List<CameraEntity> cameras = new ArrayList<>();

    @OneToOne(mappedBy = "specification")
    PhoneEntity phone;
}
