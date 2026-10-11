package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

class CreateWarehouseUseCaseTest {

  @Test
  void createsWarehouseAfterValidatingLocationAndCapacity() {
    var store = new InMemoryWarehouseStore();
    var useCase = new CreateWarehouseUseCase(store);
    var warehouse = warehouse("MWH.001", "AMSTERDAM-001", 80, 30);

    useCase.create(warehouse);

    assertEquals(1, store.getAll().size());
    assertNotNull(warehouse.createdAt);
  }

  @Test
  void rejectsWarehouseWithStockAboveCapacity() {
    var useCase =
        new CreateWarehouseUseCase(new InMemoryWarehouseStore());
    var warehouse = warehouse("MWH.001", "AMSTERDAM-001", 20, 30);

    WebApplicationException exception =
        assertThrows(WebApplicationException.class, () -> useCase.create(warehouse));

    assertEquals(400, exception.getResponse().getStatus());
    assertEquals("Warehouse stock cannot exceed capacity.", exception.getMessage());
  }

  @Test
  void rejectsUnknownLocation() {
    var useCase = new CreateWarehouseUseCase(new InMemoryWarehouseStore());

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> useCase.create(warehouse("MWH.001", "UNKNOWN", 20, 10)));

    assertEquals(400, exception.getResponse().getStatus());
    assertEquals("Warehouse location is not valid.", exception.getMessage());
  }

  @Test
  void rejectsCapacityAboveLocationLimit() {
    var useCase =
        new CreateWarehouseUseCase(new InMemoryWarehouseStore());

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> useCase.create(warehouse("MWH.001", "ZWOLLE-001", 50, 30)));

    assertEquals(400, exception.getResponse().getStatus());
    assertEquals(
        "Warehouse capacity exceeds the maximum allowed for the location.",
        exception.getMessage());
  }

  @Test
  void rejectsWhenLocationHasNoRemainingWarehouseSlots() {
    var store = new InMemoryWarehouseStore();
    store.create(warehouse("MWH.000", "ZWOLLE-001", 20, 10));
    var useCase = new CreateWarehouseUseCase(store);

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> useCase.create(warehouse("MWH.001", "ZWOLLE-001", 20, 10)));

    assertEquals(400, exception.getResponse().getStatus());
    assertEquals(
        "Maximum number of warehouses reached for this location.", exception.getMessage());
  }

  @Test
  void rejectsNegativeCapacityBeforeCallingStore() {
    var store = new InMemoryWarehouseStore();
    var useCase = new CreateWarehouseUseCase(store);

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> useCase.create(warehouse("MWH.001", "AMSTERDAM-001", -1, 0)));

    assertEquals(400, exception.getResponse().getStatus());
    assertEquals(0, store.getAll().size());
  }

  private static Warehouse warehouse(String code, String location, int capacity, int stock) {
    var warehouse = new Warehouse();
    warehouse.businessUnitCode = code;
    warehouse.location = location;
    warehouse.capacity = capacity;
    warehouse.stock = stock;
    return warehouse;
  }
}
