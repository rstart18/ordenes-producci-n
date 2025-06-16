package com.example.ordenes.infrastructure;

import com.example.ordenes.application.usecase.ManageWorkerUseCase;
import com.example.ordenes.infrastructure.server.WorkerHttpServer;

/**
 * Punto de entrada de la aplicación.
 */
public class MainApplication {

    public static void main(String[] args) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        ManageWorkerUseCase workerUseCase = bootstrap.manageWorkerUseCase();

        WorkerHttpServer server = new WorkerHttpServer(workerUseCase);
        server.start(8080);
        System.out.println("Servidor iniciado en http://localhost:8080");
    }
}
