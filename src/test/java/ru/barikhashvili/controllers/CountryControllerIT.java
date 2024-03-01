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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;
import ru.barikhashvili.dto.specs.CountryDTO;
import ru.barikhashvili.entities.specs.CountryEntity;
import ru.barikhashvili.repositories.CountryRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Country controller integration tests")
class CountryControllerIT {
    @Autowired
    CountryRepository countryRepository;

    @Autowired
    private MockMvc mvc;

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.6-alpine"));
    public static final String NOT_FOUND_MESSAGE = "{\"error\": \"Not found\"}";
    public static final String FOUND_NOT_UNIQUE_VALUES_MESSAGE = "{\"error\": \"Non-unique values found in fields\"}";
    public static final String INSUFFICIENT_DATA_MESSAGE = "{\"error\": \"Insufficient data\"}";
    public static final String REQUEST_BODY_IS_MISSING_MESSAGE = "{\"error\": \"Request body is missing\"}";

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

    @SneakyThrows
    public List<CountryDTO> getAllCountriesSortedByName() {
        return countryRepository.findAll().stream()
                .map(country -> CountryDTO.of(country.getId(), country.getName()))
                .sorted(Comparator.comparing(CountryDTO::getName))
                .toList();
    }

    @Test
    @DisplayName("GET /api/v1/countries/ должен вернуть 200 OK и список стран в порядке, отсортированном по именам")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    void handleGetAllCountries_shouldReturnAllCountriesSortedByName() {
        var countriesInDatabase = getAllCountriesSortedByName();
        var correctJsonResponseBody = objectMapper.writeValueAsString(countriesInDatabase);

        mvc.perform(get("/api/v1/countries/"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(correctJsonResponseBody)
                );
    }

    @Test
    @DisplayName("GET /api/v1/countries/ должен вернуть 200 OK и пустое тело ответа, когда страны не добавлены")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    void handleGetAllCountries_shouldReturnEmptyResponseBody_whenNoCountriesAreAdded() {
        var correctJsonResponseBody = objectMapper.writeValueAsString(Collections.emptyList());

        mvc.perform(get("/api/v1/countries/"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(correctJsonResponseBody)
                );
    }

    @Test
    @DisplayName("POST /api/v1/countries/ должен вернуть 201 CREATED и новую страну, когда поля страны корректны")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    void handleAddCountry_shouldAddAndReturnNewCountryEntity_whenCountryIsValid() {
        var nonExistentId = 1000;
        var newCountryName = "Poland";
        var requestCountryDTO = CountryDTO.of(nonExistentId, newCountryName);
        var requestJsonBody = objectMapper.writeValueAsString(requestCountryDTO);

        mvc.perform(post("/api/v1/countries/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonBody))
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        header().stringValues("Location", "/api/v1/countries/1"),
                        content().json(
                                objectMapper.writeValueAsString(
                                        countryRepository.findByName(newCountryName)
                                                .map(countryEntity -> CountryDTO.of(countryEntity.getId(), countryEntity.getName()))
                                                .orElseThrow()
                                )
                        )
                );
    }

    @SneakyThrows
    public CountryDTO getCountryDTOById(Integer existingCountryId) {
        return countryRepository.findById(existingCountryId)
                .map(countryEntity -> CountryDTO.of(countryEntity.getId(), countryEntity.getName()))
                .orElseThrow();
    }

    @Test
    @DisplayName("GET /api/v1/countries/{id} должен вернуть 200 OK и страну с указанным id, когда страна с указанным id существует")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    void handleGetCountry_shouldReturnValidCountryEntityById_whenCountryExists() {
        var receivedCountryId = 2;
        var responseCountryDTO = getCountryDTOById(receivedCountryId);
        var correctJsonResponseBody = objectMapper.writeValueAsString(responseCountryDTO);

        mvc.perform(get("/api/v1/countries/" + receivedCountryId))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(correctJsonResponseBody)
                );
    }

    @Test
    @DisplayName("DELETE /api/v1/countries/{id} должен вернуть 200 OK и страну, которая была удалена, когда страна с указанным id существует")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    void handleDeleteCountry_shouldDeleteCountryByIdAndReturnValidDeletedCountyEntity_whenCountyExists() {
        var idOfRemovedCountry = 3;
        var removedCountryDTO = getCountryDTOById(idOfRemovedCountry);
        var correctJsonResponseBody = objectMapper.writeValueAsString(removedCountryDTO);
        var numberOfCountriesAfterDeleting = countryRepository.count() - 1;

        mvc.perform(delete("/api/v1/countries/" + idOfRemovedCountry))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(correctJsonResponseBody));

        assertThat(getAllCountriesSortedByName()).doesNotContain(removedCountryDTO);
        assertThat(numberOfCountriesAfterDeleting).isEqualTo(countryRepository.count());
    }

    @Test
    @DisplayName("PUT /api/v1/countries/{id} должен вернуть 200 ОК и страну с измененными данными, когда страна существует")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    void handleEditCountry_shouldEditCountryByIdAndReturnValidEditedCountry_whenCountryExists() {
        var idOfEditedCountry = 2;
        var newCountryName = "Estonia";
        var countryDTOAfterEditing = CountryDTO.of(1000, newCountryName);
        var jsonRequestBody = objectMapper.writeValueAsString(countryDTOAfterEditing);

        var correctCountryDTOAfterEditing = CountryDTO.of(idOfEditedCountry, newCountryName);
        var correctJsonResponseBody = objectMapper.writeValueAsString(correctCountryDTOAfterEditing);

        mvc.perform(put("/api/v1/countries/" + idOfEditedCountry)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(correctJsonResponseBody)
                );

        var persistedCountry = countryRepository.findById(idOfEditedCountry)
                .orElseThrow();

        assertThat(persistedCountry.getId()).isEqualTo(idOfEditedCountry);
        assertThat(persistedCountry.getName()).isEqualTo(newCountryName);
    }

    @Test
    @DisplayName("PUT /api/v1/countries/{id} должен вернуть 404 NOT FOUND и сообщение об ошибке, когда страна не существует")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    void handleEditCountry_shouldReturnStatusCodeNotFoundAndErrorMessage_whenCountryNotFoundById() {
        var idOfEditedCountry = 2;
        var newCountryName = "Bangladesh";
        var countryDTOAfterEditing = CountryDTO.of(1000, newCountryName);
        var jsonRequestBody = objectMapper.writeValueAsString(countryDTOAfterEditing);

        mvc.perform(put("/api/v1/countries/" + idOfEditedCountry)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(NOT_FOUND_MESSAGE)
                );

        assertThat(countryRepository.count()).isZero();
    }

    @Test
    @DisplayName("PUT /api/v1/countries/{id} должен вернуть 400 BAD REQUEST и сообщение об ошибке, когда тело запроса отсутствует")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    void handleEditCountry_shouldReturnBadRequestStatusCodeAndReturnErrorMessage_whenRequestBodyIsMissing() {
        var idOfEditedCountry = 2;

        mvc.perform(put("/api/v1/countries/" + idOfEditedCountry)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(REQUEST_BODY_IS_MISSING_MESSAGE)
                );

        assertThat(countryRepository.count()).isZero();
    }

    @Test
    @DisplayName("PUT /api/v1/countries/{id} должен вернуть 400 BAD REQUEST и сообщение об ошибке, когда изменяемые параметры отсутствуют или равны нулю")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    void handleEditCountry_shouldReturnBadRequestStatusCodeAndReturnInsufficientDataErrorMessage_whenRequestBodyContainsNoData() {
        var idOfEditedCountry = 2;
        var emptyCountryDTO = new CountryDTO();
        var jsonRequestBody = objectMapper.writeValueAsString(emptyCountryDTO);
        var numberOfCountriesInDatabaseBeforeRequest = countryRepository.count();

        mvc.perform(put("/api/v1/countries/" + idOfEditedCountry)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(INSUFFICIENT_DATA_MESSAGE)
                );

        assertThat(countryRepository.count()).isEqualTo(numberOfCountriesInDatabaseBeforeRequest);
    }

    @Test
    @DisplayName("PUT /api/v1/countries/{id} должен вернуть 422 UNPROCESSABLE ENTITY и сообщение об ошибке, когда название страны не уникально")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    void handleEditCountry_shouldReturnUnprocessableEntityStatusCodeAndReturnErrorMessage_whenRequestBodyContainsNotUniqueCountryName() {
        var existingCountryId = 1;
        var existingCountryName = countryRepository.findById(existingCountryId)
                .map(CountryEntity::getName)
                .orElseThrow();
        var idOfEditedCountry = 2;
        var countryDTOAfterEditing = new CountryDTO();
        countryDTOAfterEditing.setName(existingCountryName);
        var jsonRequestBody = objectMapper.writeValueAsString(countryDTOAfterEditing);
        var numberOfCountriesInDatabaseBeforeRequest = countryRepository.count();

        mvc.perform(put("/api/v1/countries/" + idOfEditedCountry)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(
                        status().isUnprocessableEntity(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(FOUND_NOT_UNIQUE_VALUES_MESSAGE)
                );

        assertThat(countryRepository.count()).isEqualTo(numberOfCountriesInDatabaseBeforeRequest);
    }

    @Test
    @DisplayName("POST /api/v1/countries/ должен вернуть 400 BAD REQUEST и сообщение об ошибке, когда тело запроса отсутствует")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    void handleAddCountry_shouldReturnBadRequestStatusCodeAndReturnErrorMessage_whenRequestBodyIsMissing() {
        mvc.perform(post("/api/v1/countries/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(REQUEST_BODY_IS_MISSING_MESSAGE)
                );

        assertThat(countryRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST /api/v1/countries/ должен вернуть 400 BAD REQUEST и сообщение об ошибке, когда информация о добавляемой стране отсутствует или не указана")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    void handleAddCountry_shouldReturnBadRequestStatusCodeAndReturnInsufficientDataErrorMessage_whenAddedCountryIsNotValid() {
        var emptyCountryDTO = new CountryDTO();
        var jsonRequestBody = objectMapper.writeValueAsString(emptyCountryDTO);

        mvc.perform(post("/api/v1/countries/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(INSUFFICIENT_DATA_MESSAGE)
                );

        assertThat(countryRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST /api/v1/countries/ должен вернуть 422 UNPROCESSABLE ENTITY и сообщение об ошибке, когда название страны не уникально")
    @Sql(scripts = "/clear-tables.sql")
    @Sql(scripts = "/test-data.sql")
    @SneakyThrows
    void handleAddCountry_shouldReturnUnprocessableEntityStatusCodeAndReturnErrorMessage_whenRequestBodyContainsNotUniqueCountryName() {
        var existingCountryId = 1;
        var existingCountryName = countryRepository.findById(existingCountryId)
                .map(CountryEntity::getName)
                .orElseThrow();
        var notUniqueCountryDTO = new CountryDTO();
        notUniqueCountryDTO.setName(existingCountryName);
        var jsonRequestBody = objectMapper.writeValueAsString(notUniqueCountryDTO);
        var numberOfCountriesInDatabaseBeforeRequest = countryRepository.count();

        mvc.perform(post("/api/v1/countries/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpectAll(
                        status().isUnprocessableEntity(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(FOUND_NOT_UNIQUE_VALUES_MESSAGE)
                );

        assertThat(countryRepository.count()).isEqualTo(numberOfCountriesInDatabaseBeforeRequest);
    }

    @Test
    @DisplayName("GET /api/v1/countries/{id} должен вернуть 404 NOT FOUND и сообщение об ошибке, когда страна не существует")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    void handleGetCountry_shouldReturnStatusCodeNotFoundAndErrorMessage_whenCountryNotFoundById() {
        var idOfReceivingCountry = 1;
        mvc.perform(get("/api/v1/countries/" + idOfReceivingCountry))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(NOT_FOUND_MESSAGE)
                );
    }

    @Test
    @DisplayName("DELETE /api/v1/countries/{id} должен вернуть 404 NOT FOUND и сообщение об ошибке, когда страна не существует")
    @Sql(scripts = "/clear-tables.sql")
    @SneakyThrows
    void handleDeleteCountry_shouldReturnStatusCodeNotFoundAndErrorMessage_whenCountryNotFoundById() {
        var idOfDeletingCountry = 1;
        mvc.perform(delete("/api/v1/countries/" + idOfDeletingCountry))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(NOT_FOUND_MESSAGE)
                );
    }
}