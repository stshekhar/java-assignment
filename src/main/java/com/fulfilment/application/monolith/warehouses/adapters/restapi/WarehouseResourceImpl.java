package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;
import java.time.LocalDateTime;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  @Inject private WarehouseRepository warehouseRepository;

  @Inject private CreateWarehouseUseCase createWarehouseUseCase;

  @Inject private ArchiveWarehouseUseCase archiveWarehouseUseCase;

  @Inject private ReplaceWarehouseUseCase replaceWarehouseUseCase;

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @Override
  @Transactional
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    if (data == null) {
      throw new WebApplicationException("Warehouse payload is required.", 400);
    }

    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
            toDomainWarehouse(data);
    createWarehouseUseCase.create(warehouse);
    return toWarehouseResponse(warehouse);
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    if (id == null || id.isBlank()) {
      throw new WebApplicationException("Warehouse identifier is required.", 400);
    }

    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
            resolveDomainEntity(id);
    if (warehouse == null) {
      throw new WebApplicationException("Warehouse with id of " + id + " does not exist.", 404);
    }
    return toWarehouseResponse(warehouse);
  }

  @Override
  public void archiveAWarehouseUnitByID(String id) {
    if (id == null || id.isBlank()) {
      throw new WebApplicationException("Warehouse identifier is required.", 400);
    }

    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
            resolveDomainEntity(id);
    if (warehouse == null) {
      throw new WebApplicationException("Warehouse with id of " + id + " does not exist.", 404);
    }

    archiveWarehouseUseCase.archive(warehouse);
  }

  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(
          String businessUnitCode, @NotNull Warehouse data) {
    if (data == null) {
      throw new WebApplicationException("Warehouse payload is required.", 400);
    }
    if (businessUnitCode == null || businessUnitCode.isBlank()) {
      throw new WebApplicationException("Business unit code is required.", 400);
    }

    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
            toDomainWarehouse(data);
    warehouse.businessUnitCode = businessUnitCode;
    replaceWarehouseUseCase.replace(warehouse);
    return toWarehouseResponse(warehouse);
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse resolveDomainEntity(
          String id) {
    try {
      Long numericId = Long.parseLong(id);
      var entity = warehouseRepository.findEntityById(numericId);
      return entity == null ? null : entity.toWarehouse();
    } catch (NumberFormatException ignored) {
      return warehouseRepository.findByBusinessUnitCode(id);
    }
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toDomainWarehouse(
          Warehouse data) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
            new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    warehouse.businessUnitCode = data.getBusinessUnitCode();
    warehouse.location = data.getLocation();
    warehouse.capacity = data.getCapacity();
    warehouse.stock = data.getStock();
    warehouse.createdAt = LocalDateTime.now();
    warehouse.archivedAt = null;
    return warehouse;
  }

  private Warehouse toWarehouseResponse(
          com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);

    return response;
  }
}
