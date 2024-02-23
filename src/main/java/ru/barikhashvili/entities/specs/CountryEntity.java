package ru.barikhashvili.entities.specs;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "countries")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = "manufacturers")
public class CountryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;

    @Builder.Default
    @OneToMany(mappedBy = "country")
    List<ManufacturerEntity> manufacturers = new ArrayList<>();

    public void addManufacturer(ManufacturerEntity manufacturer) {
        this.manufacturers.add(manufacturer);
        manufacturer.setCountry(this);
    }
}
