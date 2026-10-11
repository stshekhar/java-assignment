package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
    return this.list("archivedAt is null").stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    DbWarehouse entity = new DbWarehouse();
    copyFromDomain(entity, warehouse);
    persist(entity);
  }

  @Override
  public void update(Warehouse warehouse) {
    DbWarehouse entity = findEntityByBusinessUnitCode(warehouse.businessUnitCode);
    if (entity == null) {
      entity = new DbWarehouse();
    }
    copyFromDomain(entity, warehouse);
    persist(entity);
  }

  @Override
  public void remove(Warehouse warehouse) {
    DbWarehouse entity = findEntityByBusinessUnitCode(warehouse.businessUnitCode);
    if (entity != null) {
      delete(entity);
    }
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse entity = findEntityByBusinessUnitCode(buCode);
    return entity == null ? null : entity.toWarehouse();
  }

  public DbWarehouse findEntityByBusinessUnitCode(String buCode) {
    if (buCode == null || buCode.isBlank()) {
      return null;
    }

    String normalized = buCode.trim();
    for (DbWarehouse candidate : listAll()) {
      if (candidate.businessUnitCode != null
              && candidate.businessUnitCode.equalsIgnoreCase(normalized)
              && candidate.archivedAt == null) {
        return candidate;
      }
    }
    for (DbWarehouse candidate : listAll()) {
      if (candidate.businessUnitCode != null
              && candidate.businessUnitCode.equalsIgnoreCase(normalized)) {
        return candidate;
      }
    }
    return null;
  }

  public DbWarehouse findEntityById(Long id) {
    if (id == null) {
      return null;
    }
    return findById(id);
  }

  private void copyFromDomain(DbWarehouse entity, Warehouse warehouse) {
    entity.businessUnitCode = warehouse.businessUnitCode;
    entity.location = warehouse.location;
    entity.capacity = warehouse.capacity;
    entity.stock = warehouse.stock;
    entity.createdAt = warehouse.createdAt == null ? LocalDateTime.now() : warehouse.createdAt;
    entity.archivedAt = warehouse.archivedAt;
  }
}
