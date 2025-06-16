package com.example.ordenes.application.usecase;

import com.example.ordenes.domain.model.ProductionOrder;
import com.example.ordenes.domain.repository.ProductionOrderRepository;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del caso de uso que delega en el repositorio.
 */
public class ManageProductionOrderService implements ManageProductionOrderUseCase {

    private final ProductionOrderRepository repository;

    public ManageProductionOrderService(ProductionOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public ProductionOrder create(ProductionOrder order) {
        // Lógica de negocio pendiente
        return repository.save(order);
    }

    @Override
    public Optional<ProductionOrder> get(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<ProductionOrder> list() {
        return repository.findAll();
    }
}
