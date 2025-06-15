package com.example.ordenes.infrastructure;

import com.example.ordenes.domain.model.ProductionOrder;
import com.example.ordenes.application.usecase.ManageProductionOrderUseCase;

/**
 * Punto de entrada de la aplicación.
 */
public class MainApplication {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        ManageProductionOrderUseCase useCase = bootstrap.manageProductionOrderUseCase();

        ProductionOrder order = new ProductionOrder();
        order.setDescription("Orden inicial");
        useCase.create(order);

        System.out.println("Ordenes registradas: " + useCase.list().size());
    }
}
