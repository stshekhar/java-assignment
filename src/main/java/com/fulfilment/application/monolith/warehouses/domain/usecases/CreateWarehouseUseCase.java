package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;

import java.time.LocalDateTime;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public CreateWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void create(Warehouse warehouse) {
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

    Location location = new LocationGateway().resolveByIdentifier(warehouse.location);
    if (location == null) {
      throw new WebApplicationException("Warehouse location is not valid.", 400);
    }
    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new WebApplicationException("Business unit code already exists.", 400);
    }
    long warehousesInLocation = warehouseStore.getAll().stream()
        .filter(
            candidate ->
                candidate.location != null && candidate.location.equals(warehouse.location))
        .count();
    if (warehousesInLocation >= location.maxNumberOfWarehouses) {
      throw new WebApplicationException("Maximum number of warehouses reached for this location.", 400);
    }
    if (warehouse.capacity > location.maxCapacity) {
      throw new WebApplicationException("Warehouse capacity exceeds the maximum allowed for the location.", 400);
    }
    if (warehouse.stock > warehouse.capacity) {
      throw new WebApplicationException("Warehouse stock cannot exceed capacity.", 400);
    }

    warehouse.createdAt = LocalDateTime.now();
    warehouse.archivedAt = null;
    warehouseStore.create(warehouse);
  }
}
