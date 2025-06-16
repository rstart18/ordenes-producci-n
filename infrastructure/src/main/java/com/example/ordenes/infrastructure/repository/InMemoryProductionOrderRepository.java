package com.example.ordenes.infrastructure.repository;

import com.example.ordenes.domain.model.ProductionOrder;
import com.example.ordenes.domain.repository.ProductionOrderRepository;

import java.util.*;

/**
 * Implementación en memoria del repositorio.
 */
public class InMemoryProductionOrderRepository implements ProductionOrderRepository {

    private final Map<Long, ProductionOrder> storage = new HashMap<>();
    private long sequence = 0L;

    @Override
    public ProductionOrder save(ProductionOrder order) {
        if (order.getId() == null) {
            order.setId(++sequence);
        }
        storage.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<ProductionOrder> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<ProductionOrder> findAll() {
        return new ArrayList<>(storage.values());
    }
}
