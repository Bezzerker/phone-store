package ru.barikhashvili.controllers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;
import ru.barikhashvili.dto.specs.CountryDTO;
import ru.barikhashvili.dto.specs.ManufacturerDTO;
import ru.barikhashvili.entities.specs.ManufacturerEntity;
import ru.barikhashvili.repositories.CountryRepository;
import ru.barikhashvili.repositories.ManufacturerRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Manufacturer controller integration tests")
class ManufacturerControllerIT {
    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private MockMvc mvc;

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.6-alpine"));
    private static final String NOT_FOUND_MESSAGE = "{\"error\": \"Not found\"}";
    private static final String FOUND_NOT_UNIQUE_VALUES_MESSAGE = "{\"error\": \"Non-unique values found in fields\"}";
    private static final String REQUEST_BODY_IS_MISSING_MESSAGE = "{\"error\": \"Request body is missing\"}";
    private static final String INSUFFICIENT_DATA_MESSAGE = "{\"error\": \"Insufficient data\"}";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void beforeAll() {
        postgres.withInitScript("schema.sql");
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    public ManufacturerDTO convertManufacturerEntityToDTO(ManufacturerEntity manufacturer) {
        var countryEntity = manufacturer.getCountry();
        var countryDTO = CountryDTO.of(countryEntity.getId(), countryEntity.getName());
        return ManufacturerDTO.builder()
                .id(manufacturer.getId())
                .name(manufacturer.getName())
                .country(countryDTO)
                .build();
    }

    @SneakyThrows
    public List<ManufacturerDTO> getAllManufacturersSortedByName() {
        return manufacturerRepository.findAll().stream()
                .map(this::convertManufacturerEntityToDTO)
                .sorted(Comparator.comparing(ManufacturerDTO::getName))
                .toList();
    }

    @Test
    @DisplayName("POST /api/v1/manufacturers/ должен вернуть 201 CREATED и нового производителя, когда отправленные данные о производителе корректны")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    @Transactional
    void handleAddManufacturer_shouldAddAndReturnNewManufacturerEntity_whenManufacturerDataIsValid() {
        var nonExistentManufacturerId = 1000;
        var nonExistentCountryName = "USSR";

        var nameOfAddedManufacturer = "Amazon";
        var addedManufacturerCountry = "USA";

        var persistentCountry = countryRepository.findByName(addedManufacturerCountry).orElseThrow();

        var countryAssociatedWithManufacturer = CountryDTO.builder()
                .id(persistentCountry.getId())
                .name(nonExistentCountryName)
                .build();

        var addedManufacturerWithIncorrectIdAndCountryName = ManufacturerDTO.builder()
                .id(nonExistentManufacturerId)
                .name(nameOfAddedManufacturer)
                .country(countryAssociatedWithManufacturer)
                .build();
        var requestJsonBody = objectMapper.writeValueAsString(addedManufacturerWithIncorrectIdAndCountryName);
        var manufacturersNumberBeforeAdding = manufacturerRepository.count();

        var responseBody = mvc.perform(post("/api/v1/manufacturers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonBody))
                .andExpectAll(
                        status().isCreated(),
                        header().stringValues("Location", "/api/v1/manufacturers/" + (manufacturersNumberBeforeAdding + 1)),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse().getContentAsString();
        var manufacturersNumberAfterAdding = manufacturerRepository.count();
        var receivedManufacturerDTO = objectMapper.readValue(responseBody, ManufacturerDTO.class);
        var persistentManufacturer = manufacturerRepository.findById(receivedManufacturerDTO.getId())
                .orElseThrow();

        assertThat(persistentManufacturer.getId()).isEqualTo(receivedManufacturerDTO.getId());
        assertThat(persistentManufacturer.getName()).isEqualTo(receivedManufacturerDTO.getName());
        assertThat(persistentManufacturer.getCountry().getId()).isEqualTo(persistentCountry.getId());
        assertThat(persistentManufacturer.getCountry().getName()).isEqualTo(persistentCountry.getName());
        assertThat(manufacturersNumberAfterAdding).isGreaterThan(manufacturersNumberBeforeAdding);
    }

    @Test
    @DisplayName("POST /api/v1/manufacturers/ должен вернуть 404 NOT FOUND и сообщение об ошибке, когда страна не существует")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    void handleAddManufacturer_shouldReturnStatusCodeNotFoundAndNotFoundErrorMessage_whenNewCountryNotFoundById() {
        var nonExistentCountryId = 1;
        var nonExistentCountryName = "Israel";
        var notExistingCountryAssociatedWithManufacturer = CountryDTO.builder()
                .id(nonExistentCountryId)
                .name(nonExistentCountryName)
                .build();

        var nonExistentManufacturerId = 1000;
        var nonExistentManufacturerName = "USSR";
        var addedManufacturerWithIncorrectIdAndCountryName = ManufacturerDTO.builder()
                .id(nonExistentManufacturerId)
                .name(nonExistentManufacturerName)
                .country(notExistingCountryAssociatedWithManufacturer)
                .build();
        var requestJsonBody = objectMapper.writeValueAsString(addedManufacturerWithIncorrectIdAndCountryName);

        mvc.perform(post("/api/v1/manufacturers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonBody))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(NOT_FOUND_MESSAGE)
                );
        var manufacturersNumberAfterAdding = manufacturerRepository.count();

        assertThat(manufacturersNumberAfterAdding).isZero();
    }

    @Test
    @DisplayName("POST /api/v1/manufacturers/ должен вернуть 400 BAD REQUEST и сообщение об ошибке, когда тело запроса отсутствует")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    void handleAddManufacturer_shouldReturnBadRequestStatusCodeAndMissingRequestBodyErrorMessage_whenRequestBodyIsMissing() {
        mvc.perform(post("/api/v1/manufacturers/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(REQUEST_BODY_IS_MISSING_MESSAGE)
                );
        var manufacturersNumberAfterAdding = manufacturerRepository.count();

        assertThat(manufacturersNumberAfterAdding).isZero();
    }

    @Test
    @DisplayName("POST /api/v1/manufacturers/ должен вернуть 400 BAD REQUEST и сообщение об ошибке, когда отсутствует часть данных или все данные полностью")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    void handleAddManufacturer_shouldReturnBadRequestStatusCodeAndInsufficientDataErrorMessage_whenNotAllDataToChangeWasSpecified() {
        var manufacturersNumberBeforeEditing = manufacturerRepository.count();
        var emptyManufacturerDTO = new ManufacturerDTO();
        var jsonRequestBody = objectMapper.writeValueAsString(emptyManufacturerDTO);

        mvc.perform(post("/api/v1/manufacturers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(INSUFFICIENT_DATA_MESSAGE)
                );
        var manufacturersNumberAfterEditing = manufacturerRepository.count();

        assertThat(manufacturersNumberAfterEditing).isEqualTo(manufacturersNumberBeforeEditing);
    }

    @Test
    @DisplayName("POST /api/v1/manufacturers/ должен вернуть 422 UNPROCESSABLE ENTITY и сообщение об ошибке, когда добавляемый производитель не уникален")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    void handleAddManufacturer_shouldReturnUnprocessableEntityStatusCodeAndNotUniqueValuesErrorMessage_whenRequestBodyContainsNotUniqueCountryManufacturer() {
        var idOfExistingManufacturer = 2;
        var existingManufacturer = transactionTemplate.execute(status -> manufacturerRepository
                .findById(idOfExistingManufacturer)
                .map(this::convertManufacturerEntityToDTO)
                .orElseThrow());
        var jsonRequestBody = objectMapper.writeValueAsString(existingManufacturer);
        var manufacturersNumberBeforeEditing = manufacturerRepository.count();

        mvc.perform(post("/api/v1/manufacturers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(
                        status().isUnprocessableEntity(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(FOUND_NOT_UNIQUE_VALUES_MESSAGE)
                );
        var manufacturersNumberAfterEditing = manufacturerRepository.count();

        assertThat(manufacturersNumberAfterEditing).isEqualTo(manufacturersNumberBeforeEditing);
    }

    @Test
    @DisplayName("GET /api/v1/manufacturers/ должен вернуть 200 OK и список производителей в порядке, отсортированном по именам")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    @Transactional
    void handleGetAllManufacturers_shouldReturnAllManufacturersSortedByName() {
        var manufacturersInDatabase = getAllManufacturersSortedByName();
        var correctJsonResponseBody = objectMapper.writeValueAsString(manufacturersInDatabase);

        mvc.perform(get("/api/v1/manufacturers/"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(correctJsonResponseBody)
                );
    }

    @Test
    @DisplayName("GET /api/v1/manufacturers/ должен вернуть 200 OK и пустое тело ответа, когда производители не добавлены")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    void handleGetAllManufacturers_shouldReturnEmptyResponseBody_whenNoManufacturersAreAdded() {
        var correctJsonResponseBody = objectMapper.writeValueAsString(Collections.emptyList());

        mvc.perform(get("/api/v1/manufacturers/"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(correctJsonResponseBody)
                );
    }

    @Test
    @DisplayName("GET /api/v1/manufacturers/{id} должен вернуть 200 OK и производителя с указанным id, когда производитель с id существует")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    @Transactional
    void handleGetManufacturer_shouldReturnValidManufacturerEntityById_whenManufacturerExists() {
        var receivedManufacturerId = 4;
        var responseCountryDTO = manufacturerRepository.findById(receivedManufacturerId)
                .map(this::convertManufacturerEntityToDTO)
                .orElseThrow();
        var correctJsonResponseBody = objectMapper.writeValueAsString(responseCountryDTO);

        mvc.perform(get("/api/v1/manufacturers/" + receivedManufacturerId))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(correctJsonResponseBody)
                );
    }

    @Test
    @DisplayName("GET /api/v1/manufacturers/{id} должен вернуть 404 NOT FOUND и сообщение с ошибкой, когда производитель с id не существует")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    @Transactional
    void handleGetManufacturer_shouldReturnNotFoundStatusCodeAndNotFoundMessage_whenCountyNotExists() {
        var receivedManufacturerId = 1;

        mvc.perform(get("/api/v1/manufacturers/" + receivedManufacturerId))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(NOT_FOUND_MESSAGE)
                );
    }

    @Test
    @DisplayName("DELETE /api/v1/manufacturers/{id} должен вернуть 200 OK и удаленного производителя, когда производитель с указанным id существует")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    @Transactional
    void handleDeleteManufacturer_shouldDeleteManufacturerByIdAndReturnValidDeletedManufacturerEntity_whenCountyExists() {
        var numberOfManufacturersBeforeDeleting = manufacturerRepository.count();

        var removedManufacturerId = 3;
        var removedManufacturerDTO = manufacturerRepository.findById(removedManufacturerId)
                .map(this::convertManufacturerEntityToDTO)
                .orElseThrow();
        var correctJsonResponseBody = objectMapper.writeValueAsString(removedManufacturerDTO);


        mvc.perform(delete("/api/v1/manufacturers/" + removedManufacturerId))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(correctJsonResponseBody));
        var numberOfManufacturersAfterDeleting = manufacturerRepository.count();


        assertThat(manufacturerRepository.findById(removedManufacturerId)).isEmpty();
        assertThat(manufacturerRepository.findByName(removedManufacturerDTO.getName())).isEmpty();
        assertThat(numberOfManufacturersAfterDeleting).isEqualTo(numberOfManufacturersBeforeDeleting - 1);
    }

    @Test
    @DisplayName("DELETE /api/v1/manufacturers/{id} должен вернуть 404 NOT FOUND, когда производитель с указанным id не существует")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    @Transactional
    void handleDeleteManufacturer_shouldReturnNotFoundStatusCodeAndNotFoundMessage_whenCountyNotExists() {
        var removedManufacturerId = 1;

        mvc.perform(delete("/api/v1/manufacturers/" + removedManufacturerId))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(NOT_FOUND_MESSAGE));
        var numberOfManufacturersAfterDeleting = manufacturerRepository.count();

        assertThat(manufacturerRepository.findById(removedManufacturerId)).isEmpty();
        assertThat(numberOfManufacturersAfterDeleting).isZero();
    }

    @Test
    @DisplayName("PUT /api/v1/manufacturers/{id} должен вернуть 200 ОК и производителя с измененными данными, когда производитель существует")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    @Transactional
    void handleEditManufacturer_shouldEditManufacturerByIdAndReturnValidEditedManufacturerEntity_whenManufacturerExists() {
        var manufacturersNumberBeforeEditing = manufacturerRepository.count();

        var nonExistingManufacturerId = 100;
        var nonExistingCountryName = "South Ossetia";
        var idOfEditableManufacturer = 2;
        var newManufacturerName = "Meizu";
        var newManufacturerCountryId = 3;
        var editedManufacturerDTO = ManufacturerDTO.builder()
                .id(nonExistingManufacturerId)
                .name(newManufacturerName)
                .country(CountryDTO.of(newManufacturerCountryId, nonExistingCountryName))
                .build();
        var jsonRequestBody = objectMapper.writeValueAsString(editedManufacturerDTO);

        var manufacturerAfterEditing = ManufacturerDTO.builder()
                .id(idOfEditableManufacturer)
                .name(newManufacturerName)
                .country(CountryDTO.builder()
                        .id(newManufacturerCountryId)
                        .name(countryRepository.findById(newManufacturerCountryId).orElseThrow().getName())
                        .build())
                .build();
        var correctJsonResponseBody = objectMapper.writeValueAsString(manufacturerAfterEditing);

        mvc.perform(put("/api/v1/manufacturers/" + idOfEditableManufacturer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(correctJsonResponseBody)
                );
        var manufacturersNumberAfterEditing = manufacturerRepository.count();

        assertThat(manufacturersNumberAfterEditing).isEqualTo(manufacturersNumberBeforeEditing);
    }

    @Test
    @DisplayName("PUT /api/v1/manufacturers/{id} должен вернуть 404 NOT FOUND и сообщение об ошибке, когда производитель не существует")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    void handleEditManufacturer_shouldReturnStatusCodeNotFoundAndNotFoundErrorMessage_whenManufacturerNotFoundById() {
        var manufacturersNumberBeforeEditing = manufacturerRepository.count();

        var idOfEditableManufacturer = 10;
        var editedManufacturerDTO = ManufacturerDTO.builder()
                .id(idOfEditableManufacturer)
                .name("Some country")
                .country(CountryDTO.of(1, "Random country name"))
                .build();
        var jsonRequestBody = objectMapper.writeValueAsString(editedManufacturerDTO);

        mvc.perform(put("/api/v1/manufacturers/" + idOfEditableManufacturer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(NOT_FOUND_MESSAGE)
                );
        var manufacturersNumberAfterEditing = manufacturerRepository.count();

        assertThat(manufacturersNumberAfterEditing).isEqualTo(manufacturersNumberBeforeEditing);
    }

    @Test
    @DisplayName("PUT /api/v1/manufacturers/{id} должен вернуть 404 NOT FOUND и сообщение об ошибке, когда страна не существует")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    void handleEditManufacturer_shouldReturnStatusCodeNotFoundAndNotFoundErrorMessage_whenNewCountryNotFoundById() {
        var manufacturersNumberBeforeEditing = manufacturerRepository.count();

        var idOfEditableManufacturer = 1;
        var idOfNotExistingCountry = 10;
        var editedManufacturerDTO = ManufacturerDTO.builder()
                .id(idOfEditableManufacturer)
                .name("Some country")
                .country(CountryDTO.of(idOfNotExistingCountry, "Random country name"))
                .build();
        var jsonRequestBody = objectMapper.writeValueAsString(editedManufacturerDTO);

        mvc.perform(put("/api/v1/manufacturers/" + idOfEditableManufacturer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(NOT_FOUND_MESSAGE)
                );
        var manufacturersNumberAfterEditing = manufacturerRepository.count();

        assertThat(manufacturersNumberAfterEditing).isEqualTo(manufacturersNumberBeforeEditing);
    }

    @Test
    @DisplayName("PUT /api/v1/manufacturers/{id} должен вернуть 400 BAD REQUEST и сообщение об ошибке, когда тело запроса отсутствует")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    void handleEditManufacturer_shouldReturnBadRequestStatusCodeAndMissingRequestBodyErrorMessage_whenRequestBodyIsMissing() {
        var manufacturersNumberBeforeEditing = manufacturerRepository.count();
        var idOfEditableManufacturer = 2;

        mvc.perform(put("/api/v1/manufacturers/" + idOfEditableManufacturer)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(REQUEST_BODY_IS_MISSING_MESSAGE)
                );
        var manufacturersNumberAfterEditing = manufacturerRepository.count();

        assertThat(manufacturersNumberAfterEditing).isEqualTo(manufacturersNumberBeforeEditing);
    }

    @Test
    @DisplayName("PUT /api/v1/manufacturers/{id} должен вернуть 400 BAD REQUEST и сообщение об ошибке, когда отсутствует часть данных")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    void handleEditManufacturer_shouldReturnBadRequestStatusCodeAndInsufficientDataErrorMessage_whenPartOfDataIsMissing() {
        var manufacturersNumberBeforeEditing = manufacturerRepository.count();

        assertAll(
                () -> {
                    var idOfEditableManufacturer = 2;
                    var emptyManufacturerDTO = ManufacturerDTO.builder()
                            .id(idOfEditableManufacturer)
                            .name("Amazon")
                            .country(new CountryDTO())
                            .build();
                    var jsonRequestBody = objectMapper.writeValueAsString(emptyManufacturerDTO);

                    mvc.perform(put("/api/v1/manufacturers/" + idOfEditableManufacturer)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(jsonRequestBody))
                            .andExpectAll(
                                    status().isBadRequest(),
                                    content().contentType(MediaType.APPLICATION_JSON),
                                    content().json(INSUFFICIENT_DATA_MESSAGE)
                            );
                },
                () -> {
                    var idOfEditableManufacturer = 2;
                    var existingCountryId = 1;
                    var emptyManufacturerDTO = ManufacturerDTO.builder()
                            .country(CountryDTO.of(existingCountryId, null))
                            .build();
                    var jsonRequestBody = objectMapper.writeValueAsString(emptyManufacturerDTO);

                    mvc.perform(put("/api/v1/manufacturers/" + idOfEditableManufacturer)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(jsonRequestBody))
                            .andExpectAll(
                                    status().isBadRequest(),
                                    content().contentType(MediaType.APPLICATION_JSON),
                                    content().json(INSUFFICIENT_DATA_MESSAGE)
                            );
                }
        );

        var manufacturersNumberAfterEditing = manufacturerRepository.count();

        assertThat(manufacturersNumberAfterEditing).isEqualTo(manufacturersNumberBeforeEditing);
    }

    @Test
    @DisplayName("PUT /api/v1/manufacturers/{id} должен вернуть 400 BAD REQUEST и сообщение об ошибке, когда отсутствуют все данные полностью")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    void handleEditManufacturer_shouldReturnBadRequestStatusCodeAndInsufficientDataErrorMessage_whenAllDataIsMissing() {
        var manufacturersNumberBeforeEditing = manufacturerRepository.count();
        var idOfEditableManufacturer = 2;
        var emptyManufacturerDTO = new ManufacturerDTO();
        var jsonRequestBody = objectMapper.writeValueAsString(emptyManufacturerDTO);

        mvc.perform(put("/api/v1/manufacturers/" + idOfEditableManufacturer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(INSUFFICIENT_DATA_MESSAGE)
                );
        var manufacturersNumberAfterEditing = manufacturerRepository.count();

        assertThat(manufacturersNumberAfterEditing).isEqualTo(manufacturersNumberBeforeEditing);
    }

    @Test
    @DisplayName("PUT /api/v1/manufacturers/{id} должен вернуть 422 UNPROCESSABLE ENTITY и сообщение об ошибке, когда изменяемые данные не уникальны")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    void handleEditManufacturer_shouldReturnUnprocessableEntityStatusCodeAndNotUniqueValuesErrorMessage_whenRequestBodyContainsNotUniqueCountryManufacturer() {
        var idOfEditableManufacturer = 1;
        var idOfExistingManufacturer = 2;

        var existingManufacturer = transactionTemplate.execute(status -> manufacturerRepository
                .findById(idOfExistingManufacturer)
                .map(this::convertManufacturerEntityToDTO)
                .orElseThrow());

        var jsonRequestBody = objectMapper.writeValueAsString(existingManufacturer);
        var manufacturersNumberBeforeEditing = manufacturerRepository.count();

        mvc.perform(put("/api/v1/manufacturers/" + idOfEditableManufacturer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(
                        status().isUnprocessableEntity(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(FOUND_NOT_UNIQUE_VALUES_MESSAGE)
                );
        var manufacturersNumberAfterEditing = manufacturerRepository.count();

        assertThat(manufacturersNumberAfterEditing).isEqualTo(manufacturersNumberBeforeEditing);
    }

    @Test
    @DisplayName("PATCH /api/v1/manufacturers/{id} должен вернуть 200 ОК и производителя с измененными данными, когда производитель существует и изменяемые данные указаны частично")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    @Transactional
    void handleEditPartOfManufacturer_shouldEditManufacturerByIdAndReturnValidEditedManufacturerEntity_whenRequestContainsPartOfManufacturer() {
        assertAll(
                () -> {
                    var idOfEditableManufacturer = 1;
                    var nonExistedManufacturerId = 1000;
                    var newManufacturerName = "Dell";

                    var manufacturersNumberBeforePartiallyEditing = manufacturerRepository.count();
                    var partiallyEditedManufacturer = ManufacturerDTO.builder()
                            .id(nonExistedManufacturerId)
                            .name(newManufacturerName)
                            .build();
                    var jsonRequestBody = objectMapper.writeValueAsString(partiallyEditedManufacturer);

                    var responseBody = mvc.perform(patch("/api/v1/manufacturers/" + idOfEditableManufacturer)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(jsonRequestBody))
                            .andExpectAll(
                                    status().isOk(),
                                    content().contentType(MediaType.APPLICATION_JSON)
                            ).andReturn().getResponse().getContentAsString();

                    var manufacturersNumberAfterPartiallyEditing = manufacturerRepository.count();
                    var manufacturerFromDatabaseAfterEditing = manufacturerRepository.findById(idOfEditableManufacturer)
                            .map(this::convertManufacturerEntityToDTO);
                    var receivedManufacturerFromResponse = objectMapper.readValue(responseBody, ManufacturerDTO.class);

                    assertThat(manufacturerFromDatabaseAfterEditing).isPresent();
                    assertThat(receivedManufacturerFromResponse).isEqualTo(manufacturerFromDatabaseAfterEditing.get());
                    assertThat(manufacturersNumberAfterPartiallyEditing).isEqualTo(manufacturersNumberBeforePartiallyEditing);
                },
                () -> {
                    var idOfEditableManufacturer = 2;
                    var nonExistedManufacturerId = 1000;
                    var newCountryId = 3;

                    var manufacturersNumberBeforePartiallyEditing = manufacturerRepository.count();
                    var partiallyEditedManufacturer = ManufacturerDTO.builder()
                            .id(nonExistedManufacturerId)
                            .country(CountryDTO.builder()
                                    .id(newCountryId)
                                    .build())
                            .build();
                    var jsonRequestBody = objectMapper.writeValueAsString(partiallyEditedManufacturer);

                    var responseBody = mvc.perform(patch("/api/v1/manufacturers/" + idOfEditableManufacturer)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(jsonRequestBody))
                            .andExpectAll(
                                    status().isOk(),
                                    content().contentType(MediaType.APPLICATION_JSON)
                            ).andReturn().getResponse().getContentAsString();

                    var manufacturersNumberAfterPartiallyEditing = manufacturerRepository.count();
                    var manufacturerFromDatabaseAfterEditing = manufacturerRepository.findById(idOfEditableManufacturer)
                            .map(this::convertManufacturerEntityToDTO);
                    var receivedManufacturerFromResponse = objectMapper.readValue(responseBody, ManufacturerDTO.class);

                    assertThat(manufacturerFromDatabaseAfterEditing).isPresent();
                    assertThat(receivedManufacturerFromResponse).isEqualTo(manufacturerFromDatabaseAfterEditing.get());
                    assertThat(manufacturersNumberAfterPartiallyEditing).isEqualTo(manufacturersNumberBeforePartiallyEditing);
                }
        );
    }

    @Test
    @DisplayName("PATCH /api/v1/manufacturers/{id} должен вернуть 200 ОК и данные производителя по индексу, когда изменяемые данные не указаны")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    @Transactional
    void handleEditPartOfManufacturer_shouldReturnValidManufacturerEntityById_whenEditableDataIsNullable() {
        var idOfEditableManufacturer = 1;

        var manufacturersNumberBeforePartiallyEditing = manufacturerRepository.count();
        var manufacturerWithNullableData = ManufacturerDTO.builder()
                .country(new CountryDTO())
                .build();
        var jsonRequestBody = objectMapper.writeValueAsString(manufacturerWithNullableData);

        var responseBody = mvc.perform(patch("/api/v1/manufacturers/" + idOfEditableManufacturer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse().getContentAsString();

        var manufacturersNumberAfterPartiallyEditing = manufacturerRepository.count();
        var manufacturerFromDatabaseAfterEditing = manufacturerRepository.findById(idOfEditableManufacturer)
                .map(this::convertManufacturerEntityToDTO);
        var receivedManufacturerFromResponse = objectMapper.readValue(responseBody, ManufacturerDTO.class);

        assertThat(manufacturerFromDatabaseAfterEditing).isPresent();
        assertThat(receivedManufacturerFromResponse).isEqualTo(manufacturerFromDatabaseAfterEditing.get());
        assertThat(manufacturersNumberAfterPartiallyEditing).isEqualTo(manufacturersNumberBeforePartiallyEditing);
    }

    @Test
    @DisplayName("PATCH /api/v1/manufacturers/{id} должен вернуть 422 UNPROCESSABLE ENTITY и сообщение об ошибке, когда отправлены не уникальные данные")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    void handleEditPartOfManufacturer_shouldReturnUnprocessableEntityStatusCodeAndReturnNotUniqueErrorMessage_whenRequestBodyContainsNotUniqueFields() {
        var idOfEditableManufacturer = 1;
        var idOfExistingManufacturer = 2;

        var manufacturersNumberBeforePartiallyEditing = manufacturerRepository.count();
        var existingManufacturer = transactionTemplate.execute(status ->
                manufacturerRepository.findById(idOfExistingManufacturer)
                        .map(this::convertManufacturerEntityToDTO)
                        .orElseThrow());
        var jsonRequestBody = objectMapper.writeValueAsString(existingManufacturer);

        mvc.perform(patch("/api/v1/manufacturers/" + idOfEditableManufacturer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(status().isUnprocessableEntity(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(FOUND_NOT_UNIQUE_VALUES_MESSAGE));
        var manufacturersNumberAfterPartiallyEditing = manufacturerRepository.count();

        assertThat(manufacturersNumberAfterPartiallyEditing).isEqualTo(manufacturersNumberBeforePartiallyEditing);
    }

    @Test
    @DisplayName("PATCH /api/v1/manufacturers/{id} должен вернуть 404 NOT FOUND и сообщение об ошибке, когда производитель не найден по id")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    void handleEditPartOfManufacturer_shouldReturnNotFoundStatusCodeAndReturnNotFoundErrorMessage_whenManufacturerIdIsNotExist() {
        var idOfEditableManufacturer = 1;

        var newManufacturerData = new ManufacturerEntity();
        var jsonRequestBody = objectMapper.writeValueAsString(newManufacturerData);

        mvc.perform(patch("/api/v1/manufacturers/" + idOfEditableManufacturer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(NOT_FOUND_MESSAGE));
        var manufacturersNumberAfterPartiallyEditing = manufacturerRepository.count();

        assertThat(manufacturersNumberAfterPartiallyEditing).isZero();
    }

    @Test
    @DisplayName("PATCH /api/v1/manufacturers/{id} должен вернуть 404 NOT FOUND и сообщение об ошибке, когда страна не найдена по id")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    void handleEditPartOfManufacturer_shouldReturnNotFoundStatusCodeAndReturnNotFoundErrorMessage_whenNewCountryIdIsNotExist() {
        var manufacturersNumberBeforePartiallyEditing = manufacturerRepository.count();
        var idOfEditableManufacturer = 1;
        var nonExistingCountryId = 10;

        var newManufacturerData = ManufacturerDTO.builder()
                .country(CountryDTO.of(nonExistingCountryId, null))
                .build();
        var jsonRequestBody = objectMapper.writeValueAsString(newManufacturerData);

        mvc.perform(patch("/api/v1/manufacturers/" + idOfEditableManufacturer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(NOT_FOUND_MESSAGE));
        var manufacturersNumberAfterPartiallyEditing = manufacturerRepository.count();

        assertThat(manufacturersNumberAfterPartiallyEditing).isEqualTo(manufacturersNumberBeforePartiallyEditing);
    }

    @Test
    @DisplayName("PUT /api/v1/manufacturers/{id} должен вернуть 400 BAD REQUEST и сообщение об ошибке, когда тело запроса отсутствует")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    void handleEditPartOfManufacturer_shouldReturnBadRequestStatusCodeAndMissingRequestBodyErrorMessage_whenRequestBodyIsMissing() {
        var manufacturersNumberBeforeEditing = manufacturerRepository.count();
        var idOfEditableManufacturer = 2;

        mvc.perform(patch("/api/v1/manufacturers/" + idOfEditableManufacturer)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(REQUEST_BODY_IS_MISSING_MESSAGE)
                );

        var manufacturersNumberAfterEditing = manufacturerRepository.count();
        assertThat(manufacturersNumberAfterEditing).isEqualTo(manufacturersNumberBeforeEditing);
    }
}