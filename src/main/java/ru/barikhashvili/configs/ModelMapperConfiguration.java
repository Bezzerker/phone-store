package ru.barikhashvili.configs;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.barikhashvili.dto.PhoneDTO;
import ru.barikhashvili.dto.PhoneVariantDTO;
import ru.barikhashvili.dto.VariantDTO;
import ru.barikhashvili.dto.specs.*;
import ru.barikhashvili.entities.PhoneEntity;
import ru.barikhashvili.entities.PhoneVariantEntity;
import ru.barikhashvili.entities.ResolutionEntity;
import ru.barikhashvili.entities.specs.*;
import ru.barikhashvili.exceptions.InsufficientDataException;

@Configuration
public class ModelMapperConfiguration {
    @Bean
    @Primary
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setPropertyCondition(context -> {
            if (context.getSource() == null) {
                throw new InsufficientDataException("Not all data is provided");
            }
            return context.getSource() != null;
        });

        addAllMappings(modelMapper);

        return modelMapper;
    }

    @Bean
    public ModelMapper nullableModelMapper() {
        var nullableModelMapper = new ModelMapper();

        nullableModelMapper.getConfiguration()
                .setPropertyCondition(Conditions.isNotNull())
                .setSkipNullEnabled(true);

        addAllMappings(nullableModelMapper);

        return nullableModelMapper;
    }

    private void addAllMappings(ModelMapper modelMapper) {
        modelMapper.addMappings(new PropertyMap<CountryDTO, CountryEntity>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<ManufacturerDTO, ManufacturerEntity>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getCountry());
            }
        });

        modelMapper.addMappings(new PropertyMap<BatteryDTO, BatteryEntity>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<OperatingSystemDTO, OperatingSystemEntity>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<ProcessorDTO, ProcessorEntity>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<ResolutionDTO, ResolutionEntity>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<DisplayDTO, DisplayEntity>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getResolution());
            }
        });

        modelMapper.addMappings(new PropertyMap<CameraSensorDTO, CameraSensorEntity>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<CameraDTO, CameraEntity>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getSensor());
            }
        });

        modelMapper.addMappings(new PropertyMap<PhoneSpecificationDTO, PhoneSpecificationEntity>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getOperatingSystem());
                skip(destination.getDisplay());
                skip(destination.getProcessor());
                skip(destination.getBattery());
                skip(destination.getCameras());
            }
        });

        modelMapper.addMappings(new PropertyMap<VariantDTO, VariantEntity>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<PhoneVariantEntity, PhoneVariantDTO>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<PhoneDTO, PhoneEntity>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getSpecification());
                skip(destination.getManufacturer());
                skip(destination.getPhoneVariants());
            }
        });
    }
}
