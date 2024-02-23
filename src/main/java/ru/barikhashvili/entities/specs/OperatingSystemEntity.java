package ru.barikhashvili.entities.specs;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "operating_systems")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = "phoneSpecifications")
public class OperatingSystemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;

    String version;

    @Builder.Default
    @OneToMany(mappedBy = "operatingSystem")
    List<PhoneSpecificationEntity> phoneSpecifications = new LinkedList<>();

    public void addSpecification(PhoneSpecificationEntity spec) {
        this.phoneSpecifications.add(spec);
        spec.setOperatingSystem(this);
    }
}
