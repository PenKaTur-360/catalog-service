package es.penkatur.backend.sharedkernel.infrastructure.persistence;

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
                "quarkus.liquibase.migrate-at-start", "true",
                "quarkus.datasource.username", POSTGRESQL.getUsername(),
                "quarkus.datasource.password", POSTGRESQL.getPassword(),
                "quarkus.hibernate-orm.database.generation", "drop-and-create",
                "quarkus.datasource.jdbc.url",
                    "jdbc:postgresql://"
                            + POSTGRESQL.getHost() + ":"
                            + POSTGRESQL.getFirstMappedPort() + "/"
                            + POSTGRESQL.getDatabaseName()
        );
    }
}
