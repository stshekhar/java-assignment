package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

@QuarkusTest
class WarehouseRepositoryTest {

  @Inject WarehouseRepository warehouseRepository;

  @Test
  @TestTransaction
  void persistsFindsUpdatesAndRemovesWarehouse() {
    Warehouse warehouse = warehouse("MWH.REPOSITORY.001");
    warehouseRepository.create(warehouse);

    DbWarehouse entity = warehouseRepository.findEntityByBusinessUnitCode("  mwh.repository.001 ");
    assertNotNull(entity);
    assertNotNull(entity.createdAt);
    assertEquals(warehouse.businessUnitCode, warehouseRepository.findByBusinessUnitCode(
        warehouse.businessUnitCode).businessUnitCode);
    assertNotNull(warehouseRepository.findEntityById(entity.id));
    assertNull(warehouseRepository.findEntityById(null));

    warehouse.location = "AMSTERDAM-002";
    warehouse.capacity = 45;
    warehouse.stock = 12;
    warehouseRepository.update(warehouse);

    Warehouse updated = warehouseRepository.findByBusinessUnitCode(warehouse.businessUnitCode);
    assertEquals("AMSTERDAM-002", updated.location);
    assertEquals(45, updated.capacity);
    assertEquals(12, updated.stock);

    warehouseRepository.remove(warehouse);
    assertNull(warehouseRepository.findByBusinessUnitCode(warehouse.businessUnitCode));
    assertTrue(
        warehouseRepository.findEntityByBusinessUnitCode(null) == null
            && warehouseRepository.findEntityByBusinessUnitCode(" ") == null);
  }

  @Test
  @TestTransaction
  void createsWarehouseWhenUpdateCannotFindAnExistingEntity() {
    Warehouse warehouse = warehouse("MWH.REPOSITORY.UPDATE");

    warehouseRepository.update(warehouse);

    assertNotNull(warehouseRepository.findByBusinessUnitCode(warehouse.businessUnitCode));
  }

  @Test
  @TestTransaction
  void findsArchivedWarehouseOnlyAsFallbackAndExcludesItFromActiveWarehouses() {
    Warehouse warehouse = warehouse("MWH.REPOSITORY.ARCHIVED");
    warehouse.archivedAt = LocalDateTime.now();
    warehouseRepository.create(warehouse);

    assertNotNull(warehouseRepository.findByBusinessUnitCode(warehouse.businessUnitCode));
    assertNull(warehouseRepository.findEntityByBusinessUnitCode("MWH.REPOSITORY.MISSING"));
    assertTrue(
        warehouseRepository.getAll().stream()
            .noneMatch(item -> item.businessUnitCode.equals(warehouse.businessUnitCode)));
  }

  private static Warehouse warehouse(String businessUnitCode) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = businessUnitCode;
    warehouse.location = "AMSTERDAM-002";
    warehouse.capacity = 45;
    warehouse.stock = 12;
    return warehouse;
  }
}
