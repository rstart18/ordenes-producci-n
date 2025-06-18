package com.example.ordenes.infrastructure.server;

import com.example.ordenes.application.usecase.ManageWorkerUseCase;
import com.example.ordenes.domain.model.Worker;
import com.example.ordenes.domain.model.CreatedWorker;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
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
        server.createContext("/workers", this::handleWorkers);
        server.setExecutor(null);
        server.start();
    }

    private void handleWorkers(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                String path = exchange.getRequestURI().getPath();
                if ("/workers".equals(path) || "/workers/".equals(path)) {
                    handleListWorkers(exchange);
                } else {
                    handleGetWorker(exchange);
                }
                break;
            case "POST":
                handleCreateWorker(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
        }
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

    private void handleCreateWorker(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String requestBody;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            requestBody = reader.lines().collect(Collectors.joining());
        }
        JSONObject body = new JSONObject(requestBody);
        String name = body.optString("name", null);
        String job = body.optString("job", null);
        if (name == null || job == null) {
            exchange.sendResponseHeaders(400, -1);
            exchange.close();
            return;
        }

        Optional<CreatedWorker> created = manageWorkerUseCase.create(name, job);
        if (created.isPresent()) {
            CreatedWorker cw = created.get();
            JSONObject responseJson = new JSONObject()
                    .put("name", cw.getName())
                    .put("job", cw.getJob())
                    .put("id", cw.getId())
                    .put("createdAt", cw.getCreatedAt());
            byte[] response = responseJson.toString().getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        } else {
            exchange.sendResponseHeaders(502, -1);
        }
        exchange.close();
    }

    private void handleListWorkers(HttpExchange exchange) throws IOException {
        int page = 1;
        String query = exchange.getRequestURI().getQuery();
        if (query != null) {
            for (String part : query.split("&")) {
                String[] kv = part.split("=");
                if (kv.length == 2 && "page".equals(kv[0])) {
                    try {
                        page = Integer.parseInt(kv[1]);
                    } catch (NumberFormatException ignore) {
                        // default
                    }
                }
            }
        }

        List<Worker> workers = manageWorkerUseCase.list(page);
        JSONArray array = new JSONArray();
        for (Worker w : workers) {
            array.put(new JSONObject()
                    .put("id", w.getId())
                    .put("email", w.getEmail())
                    .put("firstName", w.getFirstName())
                    .put("lastName", w.getLastName()));
        }
        JSONObject responseJson = new JSONObject().put("data", array);
        byte[] response = responseJson.toString().getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
        exchange.close();
    }
}
