package com.example.ordenes.infrastructure.server;

import com.example.ordenes.application.usecase.ManageWorkerUseCase;
import com.example.ordenes.domain.model.Worker;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * Servidor HTTP sencillo para exponer el endpoint de trabajadores.
 */
public class WorkerHttpServer {

    private final ManageWorkerUseCase manageWorkerUseCase;
    private HttpServer server;

    public WorkerHttpServer(ManageWorkerUseCase manageWorkerUseCase) {
        this.manageWorkerUseCase = manageWorkerUseCase;
    }

    /**
     * Inicia el servidor en el puerto indicado.
     */
    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/workers", this::handleGetWorker);
        server.setExecutor(null);
        server.start();
    }

    private void handleGetWorker(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        if (parts.length != 3) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }
        try {
            long id = Long.parseLong(parts[2]);
            Optional<Worker> workerOpt = manageWorkerUseCase.get(id);
            if (workerOpt.isPresent()) {
                Worker worker = workerOpt.get();
                JSONObject json = new JSONObject()
                        .put("id", worker.getId())
                        .put("email", worker.getEmail())
                        .put("firstName", worker.getFirstName())
                        .put("lastName", worker.getLastName());
                byte[] response = json.toString().getBytes();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        } catch (NumberFormatException e) {
            exchange.sendResponseHeaders(400, -1);
        } finally {
            exchange.close();
        }
    }
}
