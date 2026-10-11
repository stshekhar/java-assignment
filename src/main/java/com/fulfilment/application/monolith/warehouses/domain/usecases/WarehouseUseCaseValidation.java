package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;

final class WarehouseUseCaseValidation {

  private WarehouseUseCaseValidation() {}

  static void validateRequiredFields(Warehouse warehouse) {
    if (warehouse == null) {
      throw new WebApplicationException("Warehouse payload is required.", 400);
    }
    if (warehouse.businessUnitCode == null || warehouse.businessUnitCode.isBlank()) {
      throw new WebApplicationException("Business unit code is required.", 400);
    }
    if (warehouse.location == null || warehouse.location.isBlank()) {
      throw new WebApplicationException("Warehouse location is required.", 400);
    }
    if (warehouse.capacity == null || warehouse.stock == null) {
      throw new WebApplicationException("Warehouse capacity and stock must be provided.", 400);
    }
    if (warehouse.capacity < 0 || warehouse.stock < 0) {
      throw new WebApplicationException("Warehouse capacity and stock cannot be negative.", 400);
    }
  }

  static void validateLocationConstraints(
      List<Warehouse> activeWarehouses,
      Warehouse warehouse,
      Warehouse replacedWarehouse,
      int maxNumberOfWarehouses,
      int maxCapacity) {
    long warehouseCount = 0;
    long totalCapacity = warehouse.capacity;
    for (Warehouse existing : activeWarehouses) {
      if (replacedWarehouse != null
          && existing.businessUnitCode != null
          && existing.businessUnitCode.equalsIgnoreCase(replacedWarehouse.businessUnitCode)) {
        continue;
      }
      if (warehouse.location.equals(existing.location)) {
        warehouseCount++;
        totalCapacity += existing.capacity;
      }
    }

    if (warehouseCount >= maxNumberOfWarehouses) {
      throw new WebApplicationException(
          "Maximum number of warehouses reached for this location.", 400);
    }
    if (totalCapacity > maxCapacity) {
      throw new WebApplicationException(
          "Warehouse capacity exceeds the maximum allowed for the location.", 400);
    }
  }
}
