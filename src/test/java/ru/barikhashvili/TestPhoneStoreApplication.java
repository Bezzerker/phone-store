package ru.barikhashvili;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestPhoneStoreApplication {
    public static void main(String[] args) {
        SpringApplication.from(PhoneStoreApplication::main).with(TestPhoneStoreApplication.class).run(args);
    }
}
