package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;

final class InMemoryWarehouseStore implements WarehouseStore {

  private final List<Warehouse> warehouses = new ArrayList<>();

  @Override
  public List<Warehouse> getAll() {
    return warehouses.stream().filter(warehouse -> warehouse.archivedAt == null).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    warehouses.add(warehouse);
  }

  @Override
  public void update(Warehouse warehouse) {
    int index = warehouses.indexOf(warehouse);
    if (index >= 0) {
      warehouses.set(index, warehouse);
    }
  }

  @Override
  public void remove(Warehouse warehouse) {
    warehouses.remove(warehouse);
  }

  @Override
  public Warehouse findByBusinessUnitCode(String businessUnitCode) {
    return warehouses.stream()
        .filter(warehouse -> warehouse.archivedAt == null)
        .filter(warehouse -> warehouse.businessUnitCode.equals(businessUnitCode))
        .findFirst()
        .orElse(null);
  }
}
