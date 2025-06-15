package com.example.ordenes.domain.repository;

import com.example.ordenes.domain.model.ProductionOrder;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar las ordenes de producción.
 */
public interface ProductionOrderRepository {

    ProductionOrder save(ProductionOrder order);

    Optional<ProductionOrder> findById(Long id);

    List<ProductionOrder> findAll();
}
