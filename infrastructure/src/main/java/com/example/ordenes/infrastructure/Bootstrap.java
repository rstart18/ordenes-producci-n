package com.example.ordenes.infrastructure;

import com.example.ordenes.application.usecase.ManageProductionOrderService;
import com.example.ordenes.application.usecase.ManageProductionOrderUseCase;
import com.example.ordenes.infrastructure.repository.InMemoryProductionOrderRepository;

/**
 * Configuración manual de dependencias para fines de demostración.
 */
public class Bootstrap {

    public ManageProductionOrderUseCase manageProductionOrderUseCase() {
        return new ManageProductionOrderService(new InMemoryProductionOrderRepository());
    }
}
