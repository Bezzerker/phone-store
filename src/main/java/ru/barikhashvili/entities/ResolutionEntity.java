package ru.barikhashvili.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.barikhashvili.entities.specs.DisplayEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "screen_resolutions")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = "displays")
public class ResolutionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    int horizontalPixels;

    int verticalPixels;

    @Builder.Default
    @OneToMany(mappedBy = "resolution")
    List<DisplayEntity> displays = new ArrayList<>();
}
