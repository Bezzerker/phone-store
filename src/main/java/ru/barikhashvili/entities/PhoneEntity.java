package ru.barikhashvili.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.barikhashvili.entities.specs.ManufacturerEntity;
import ru.barikhashvili.entities.specs.PhoneSpecificationEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "phones")
@ToString(exclude = {"phoneVariants"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhoneEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    ManufacturerEntity manufacturer;

    String model;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "specification_id")
    PhoneSpecificationEntity specification;

    @Basic
    LocalDateTime releaseDate;

    public void setSpecification(PhoneSpecificationEntity specification) {
        specification.setPhone(this);
        this.specification = specification;
    }

    @Builder.Default
    @OneToMany(mappedBy = "phone", cascade = CascadeType.ALL)
    List<PhoneVariantEntity> phoneVariants = new ArrayList<>();
}
