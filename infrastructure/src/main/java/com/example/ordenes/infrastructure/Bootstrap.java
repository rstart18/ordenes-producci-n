package com.example.ordenes.infrastructure;

import com.example.ordenes.application.usecase.*;
import com.example.ordenes.infrastructure.repository.InMemoryProductionOrderRepository;
import com.example.ordenes.infrastructure.api.WorkerApiRepository;
import com.example.ordenes.infrastructure.repository.WorkerCachedRepository;

/**
 * Configuración manual de dependencias para fines de demostración.
 */
public class Bootstrap {

    public ManageProductionOrderUseCase manageProductionOrderUseCase() {
        return new ManageProductionOrderService(new InMemoryProductionOrderRepository());
    }

    public ManageWorkerUseCase manageWorkerUseCase() {
        return new ManageWorkerService(new WorkerCachedRepository());
    }
}
