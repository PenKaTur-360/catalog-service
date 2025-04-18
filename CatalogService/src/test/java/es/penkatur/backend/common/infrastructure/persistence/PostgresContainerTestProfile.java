package es.penkatur.backend.common.infrastructure.persistence;

import io.quarkus.test.junit.QuarkusTestProfile;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;

public class PostgresContainerTestProfile implements QuarkusTestProfile {

    private static final PostgreSQLContainer<?> POSTGRESQL = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("pkt_test_db")
            .withUsername("test")
            .withPassword("test");

    static {
        POSTGRESQL.start();
    }

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "quarkus.datasource.reactive.url", "vertx-reactive:postgresql://" + POSTGRESQL.getHost() + ":" + POSTGRESQL.getFirstMappedPort() + "/" + POSTGRESQL.getDatabaseName(),
                "quarkus.datasource.jdbc.url", "jdbc:postgresql://" + POSTGRESQL.getHost() + ":" + POSTGRESQL.getFirstMappedPort() + "/" + POSTGRESQL.getDatabaseName(),
                "quarkus.datasource.username", POSTGRESQL.getUsername(),
                "quarkus.datasource.password", POSTGRESQL.getPassword(),
                "quarkus.hibernate-orm.database.generation", "drop-and-create"
        );
    }
}