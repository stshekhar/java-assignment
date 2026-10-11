package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

class ReplaceWarehouseUseCaseTest {

  @Test
  void archivesCurrentWarehouseAndCreatesReplacement() {
    var store = new InMemoryWarehouseStore();
    var current = warehouse("MWH.001", "AMSTERDAM-001", 60, 30);
    store.create(current);
    var replacement = warehouse("MWH.001", "AMSTERDAM-002", 80, 30);
    var useCase =
        new ReplaceWarehouseUseCase(
            store, identifier -> new Location(identifier, 5, 100));

    useCase.replace(replacement);

    assertNotNull(current.archivedAt);
    assertNotNull(replacement.createdAt);
    assertEquals(1, store.getAll().size());
    assertEquals(replacement, store.getAll().get(0));
  }

  @Test
  void rejectsReplacementWhenWarehouseDoesNotExist() {
    var useCase =
        new ReplaceWarehouseUseCase(
            new InMemoryWarehouseStore(), identifier -> new Location(identifier, 5, 100));

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> useCase.replace(warehouse("MWH.001", "AMSTERDAM-001", 80, 30)));

    assertEquals(404, exception.getResponse().getStatus());
  }

  @Test
  void rejectsNegativeReplacementValues() {
    var store = new InMemoryWarehouseStore();
    store.create(warehouse("MWH.001", "AMSTERDAM-001", 60, 30));
    var useCase =
        new ReplaceWarehouseUseCase(
            store, identifier -> new Location(identifier, 5, 100));

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> useCase.replace(warehouse("MWH.001", "AMSTERDAM-001", -1, 30)));

    assertEquals(400, exception.getResponse().getStatus());
    assertEquals(1, store.getAll().size());
  }

  @Test
  void rejectsReplacementWithDifferentStock() {
    var store = new InMemoryWarehouseStore();
    store.create(warehouse("MWH.001", "AMSTERDAM-001", 60, 30));
    var useCase =
        new ReplaceWarehouseUseCase(
            store, identifier -> new Location(identifier, 5, 100));

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> useCase.replace(warehouse("MWH.001", "AMSTERDAM-001", 80, 29)));

    assertEquals(400, exception.getResponse().getStatus());
    assertEquals(
        "New warehouse stock must match the stock of the warehouse being replaced.",
        exception.getMessage());
  }

  @Test
  void rejectsReplacementCapacityAboveLocationLimit() {
    var store = new InMemoryWarehouseStore();
    store.create(warehouse("MWH.001", "AMSTERDAM-001", 60, 30));
    var useCase =
        new ReplaceWarehouseUseCase(
            store, identifier -> new Location(identifier, 5, 50));

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> useCase.replace(warehouse("MWH.001", "AMSTERDAM-001", 60, 30)));

    assertEquals(400, exception.getResponse().getStatus());
    assertEquals(
        "Warehouse capacity exceeds the maximum allowed for the location.",
        exception.getMessage());
    assertNull(store.getAll().get(0).archivedAt);
  }

  @Test
  void allowsReplacementToReuseItsCurrentLocationSlotAndCapacity() {
    var store = new InMemoryWarehouseStore();
    var current = warehouse("MWH.001", "AMSTERDAM-001", 60, 30);
    store.create(current);
    var replacement = warehouse("MWH.001", "AMSTERDAM-001", 60, 30);
    var useCase =
        new ReplaceWarehouseUseCase(
            store, identifier -> new Location(identifier, 1, 60));

    useCase.replace(replacement);

    assertNotNull(current.archivedAt);
    assertEquals(1, store.getAll().size());
    assertEquals(replacement, store.getAll().get(0));
  }

  @Test
  void rejectsReplacementWhenDestinationLocationHasNoAvailableSlot() {
    var store = new InMemoryWarehouseStore();
    var current = warehouse("MWH.001", "AMSTERDAM-001", 60, 30);
    store.create(current);
    store.create(warehouse("MWH.002", "AMSTERDAM-002", 40, 10));
    var useCase =
        new ReplaceWarehouseUseCase(
            store, identifier -> new Location(identifier, 1, 100));

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> useCase.replace(warehouse("MWH.001", "AMSTERDAM-002", 60, 30)));

    assertEquals(400, exception.getResponse().getStatus());
    assertEquals(
        "Maximum number of warehouses reached for this location.", exception.getMessage());
    assertNull(current.archivedAt);
  }

  @Test
  void rejectsReplacementWhenCombinedLocationCapacityIsTooLarge() {
    var store = new InMemoryWarehouseStore();
    var current = warehouse("MWH.001", "AMSTERDAM-001", 60, 30);
    store.create(current);
    store.create(warehouse("MWH.002", "AMSTERDAM-002", 80, 10));
    var useCase =
        new ReplaceWarehouseUseCase(
            store, identifier -> new Location(identifier, 5, 100));

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> useCase.replace(warehouse("MWH.001", "AMSTERDAM-002", 30, 30)));

    assertEquals(400, exception.getResponse().getStatus());
    assertEquals(
        "Warehouse capacity exceeds the maximum allowed for the location.",
        exception.getMessage());
    assertNull(current.archivedAt);
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
