package com.example.ordenes.infrastructure.api;

import com.example.ordenes.domain.model.Worker;
import com.example.ordenes.domain.model.CreatedWorker;
import com.example.ordenes.domain.repository.WorkerRepository;
import com.example.ordenes.infrastructure.circuit.CircuitBreaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONObject;

/**
 * Repositorio que obtiene trabajadores de la API externa.
 */
public class WorkerApiRepository implements WorkerRepository {

    private static final String API_URL = "https://reqres.in/api/users/";
    private static final String API_KEY = "reqres-free-v1";

    private final CircuitBreaker circuitBreaker;

    public WorkerApiRepository() {
        // 3 fallos permiten abrir el circuito, reintento tras 5 segundos
        this.circuitBreaker = new CircuitBreaker(3, 5000);
    }

    @Override
    public Optional<Worker> findById(Long id) {
        try {
            return circuitBreaker.execute(() -> Optional.ofNullable(callApi(id)));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CreatedWorker> create(String name, String job) {
        try {
            return circuitBreaker.execute(() -> Optional.ofNullable(callCreateApi(name, job)));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private Worker callApi(Long id) {
        try {
            URL url = new URL(API_URL + id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("x-api-key", API_KEY);

            int status = con.getResponseCode();
            if (status >= 200 && status < 300) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String body = in.lines().collect(Collectors.joining());
                    JSONObject json = new JSONObject(body).getJSONObject("data");
                    Worker worker = new Worker();
                    worker.setId(json.getLong("id"));
                    worker.setEmail(json.getString("email"));
                    worker.setFirstName(json.getString("first_name"));
                    worker.setLastName(json.getString("last_name"));
                    return worker;
                }
            } else {
                throw new IOException("Unexpected status: " + status);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error calling worker API", e);
        }
    }

    private CreatedWorker callCreateApi(String name, String job) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("x-api-key", API_KEY);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            JSONObject payload = new JSONObject()
                    .put("name", name)
                    .put("job", job);
            byte[] out = payload.toString().getBytes();
            try (OutputStream os = con.getOutputStream()) {
                os.write(out);
            }

            int status = con.getResponseCode();
            if (status >= 200 && status < 300) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String body = in.lines().collect(Collectors.joining());
                    JSONObject json = new JSONObject(body);
                    CreatedWorker cw = new CreatedWorker();
                    cw.setId(Long.parseLong(json.getString("id")));
                    cw.setName(json.optString("name"));
                    cw.setJob(json.optString("job"));
                    cw.setCreatedAt(json.optString("createdAt"));
                    return cw;
                }
            } else {
                throw new IOException("Unexpected status: " + status);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error calling worker API", e);
        }
    }
}
