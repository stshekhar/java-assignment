package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import java.time.LocalDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Inject
  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore) {
    this(warehouseStore, new LocationGateway());
  }

  ReplaceWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  @Transactional
  public void replace(Warehouse newWarehouse) {
    WarehouseUseCaseValidation.validateRequiredFields(newWarehouse);

    Warehouse currentWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
    if (currentWarehouse == null) {
      throw new WebApplicationException("Warehouse to replace does not exist.", 404);
    }
    if (newWarehouse.stock > newWarehouse.capacity) {
      throw new WebApplicationException("Warehouse stock cannot exceed capacity.", 400);
    }
    if (newWarehouse.capacity < currentWarehouse.stock) {
      throw new WebApplicationException("New warehouse capacity is too small for the current stock.", 400);
    }
    if (!newWarehouse.stock.equals(currentWarehouse.stock)) {
      throw new WebApplicationException("New warehouse stock must match the stock of the warehouse being replaced.", 400);
    }

    Location location = locationResolver.resolveByIdentifier(newWarehouse.location);
    if (location == null) {
      throw new WebApplicationException("Warehouse location is not valid.", 400);
    }
    WarehouseUseCaseValidation.validateLocationConstraints(
        warehouseStore.getAll(),
        newWarehouse,
        currentWarehouse,
        location.maxNumberOfWarehouses,
        location.maxCapacity);

    currentWarehouse.archivedAt = LocalDateTime.now();
    warehouseStore.update(currentWarehouse);

    newWarehouse.createdAt = LocalDateTime.now();
    newWarehouse.archivedAt = null;
    warehouseStore.create(newWarehouse);
  }
}
