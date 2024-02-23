package ru.barikhashvili.entities.specs;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.barikhashvili.entities.PhoneEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "manufacturers")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = "phones")
public class ManufacturerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    CountryEntity country;

    @Builder.Default
    @OneToMany(mappedBy = "manufacturer")
    List<PhoneEntity> phones = new ArrayList<>();

    public void addPhone(PhoneEntity phone) {
        this.phones.add(phone);
        phone.setManufacturer(this);
    }
}
