package com.example.ordenes.infrastructure;

import com.example.ordenes.domain.model.ProductionOrder;
import com.example.ordenes.application.usecase.ManageProductionOrderUseCase;
import com.example.ordenes.application.usecase.ManageWorkerUseCase;

/**
 * Punto de entrada de la aplicación.
 */
public class MainApplication {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        ManageProductionOrderUseCase useCase = bootstrap.manageProductionOrderUseCase();
        ManageWorkerUseCase workerUseCase = bootstrap.manageWorkerUseCase();

        ProductionOrder order = new ProductionOrder();
        order.setDescription("Orden inicial");
        useCase.create(order);

        System.out.println("Ordenes registradas: " + useCase.list().size());

        workerUseCase.get(1L).ifPresent(worker ->
                System.out.println("Trabajador obtenido: " + worker.getFirstName() + " " + worker.getLastName()));
    }
}
