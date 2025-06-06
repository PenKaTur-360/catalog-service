package es.penkatur.backend.catalogservice.catalog.infrastructure.persistence;

import es.penkatur.backend.sharedkernel.infrastructure.persistence.PostgresContainerTestProfile;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusIntegrationTest
@TestProfile(PostgresContainerTestProfile.class)
class CatalogRepositoryTestIT extends CatalogRepositoryTest {
}
