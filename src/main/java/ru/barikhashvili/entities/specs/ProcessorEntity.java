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
@Entity(name = "processors")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = "phoneSpecifications")
public class ProcessorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String model;

    int technologyNode;

    int cores;

    BigDecimal maxFrequency;

    @Builder.Default
    @OneToMany(mappedBy = "processor")
    List<PhoneSpecificationEntity> phoneSpecifications = new ArrayList<>();
}
