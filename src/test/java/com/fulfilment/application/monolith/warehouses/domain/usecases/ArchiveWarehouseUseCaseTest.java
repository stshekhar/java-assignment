package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

class ArchiveWarehouseUseCaseTest {

  @Test
  void archivesExistingWarehouse() {
    var store = new InMemoryWarehouseStore();
    var warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.001";
    store.create(warehouse);

    new ArchiveWarehouseUseCase(store).archive(warehouse);

    assertNotNull(warehouse.archivedAt);
    assertEquals(0, store.getAll().size());
  }

  @Test
  void rejectsWarehouseWithoutBusinessUnitCode() {
    var useCase = new ArchiveWarehouseUseCase(new InMemoryWarehouseStore());

    WebApplicationException exception =
        assertThrows(WebApplicationException.class, () -> useCase.archive(new Warehouse()));

    assertEquals(400, exception.getResponse().getStatus());
  }

  @Test
  void returnsNotFoundWhenWarehouseDoesNotExist() {
    var warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.001";
    var useCase = new ArchiveWarehouseUseCase(new InMemoryWarehouseStore());

    WebApplicationException exception =
        assertThrows(WebApplicationException.class, () -> useCase.archive(warehouse));

    assertEquals(404, exception.getResponse().getStatus());
  }
}
