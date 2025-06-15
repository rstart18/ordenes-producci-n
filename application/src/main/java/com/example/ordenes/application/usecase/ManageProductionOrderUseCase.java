package com.example.ordenes.application.usecase;

import com.example.ordenes.domain.model.ProductionOrder;

import java.util.List;
import java.util.Optional;

/**
 * Caso de uso para gestionar las ordenes de producción.
 */
public interface ManageProductionOrderUseCase {

    ProductionOrder create(ProductionOrder order);

    Optional<ProductionOrder> get(Long id);

    List<ProductionOrder> list();
}
