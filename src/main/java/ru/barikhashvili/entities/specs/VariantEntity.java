package ru.barikhashvili.entities.specs;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.barikhashvili.entities.specs.enums.Color;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "variants")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VariantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer romSize;
    Integer ramSize;
    @Enumerated(EnumType.ORDINAL)
    Color color;
}
