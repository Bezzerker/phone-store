package ru.barikhashvili.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.barikhashvili.entities.specs.VariantEntity;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "phones_variants")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhoneVariantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    PhoneEntity phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    VariantEntity variant;

    int quantity;

    BigDecimal price;

    public void setPhone(PhoneEntity phone) {
        phone.getPhoneVariants().add(this);
        this.phone = phone;
    }
}
