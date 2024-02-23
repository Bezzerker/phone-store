package ru.barikhashvili.entities.specs;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.barikhashvili.entities.ResolutionEntity;
import ru.barikhashvili.entities.specs.enums.DisplayType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "displays")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = "phoneSpecifications")
public class DisplayEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    int refreshRate;

    BigDecimal diagonal;

    @Enumerated(EnumType.ORDINAL)
    DisplayType displayType;

    @ManyToOne(fetch = FetchType.LAZY)
    ResolutionEntity resolution;

    @Builder.Default
    @OneToMany(mappedBy = "display")
    List<PhoneSpecificationEntity> phoneSpecifications = new ArrayList<>();

    public void addSpecification(PhoneSpecificationEntity spec) {
        this.phoneSpecifications.add(spec);
        spec.setDisplay(this);
    }
}
