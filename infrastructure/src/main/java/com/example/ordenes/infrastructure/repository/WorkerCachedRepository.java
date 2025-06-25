package com.example.ordenes.infrastructure.repository;

import com.example.ordenes.domain.model.Worker;
import com.example.ordenes.domain.model.CreatedWorker;
import com.example.ordenes.domain.model.UpdatedWorker;
import com.example.ordenes.domain.repository.WorkerRepository;
import com.example.ordenes.infrastructure.api.WorkerApiRepository;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.*;

/**
 * Repository that caches workers in DynamoDB to reduce calls to Reqres API.
 */
public class WorkerCachedRepository implements WorkerRepository {

    private static final String TABLE = "user-cache";
    private final DynamoDbClient dynamoDb;
    private final WorkerApiRepository api;

    public WorkerCachedRepository() {
        DynamoDbClientBuilder builder = DynamoDbClient.builder();
        String endpoint = System.getenv("DYNAMODB_ENDPOINT");
        if (endpoint != null && !endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
            builder.region(Region.US_EAST_1);
        }
        this.dynamoDb = builder.build();
        this.api = new WorkerApiRepository();
    }

    private static Map<String, AttributeValue> key(String pk) {
        return Collections.singletonMap("PK", AttributeValue.fromS(pk));
    }

    @Override
    public Optional<Worker> findById(Long id) {
        String pk = "user#" + id;
        long now = System.currentTimeMillis() / 1000;
        try {
            GetItemResponse resp = dynamoDb.getItem(GetItemRequest.builder()
                    .tableName(TABLE)
                    .key(key(pk))
                    .build());
            Map<String, AttributeValue> item = resp.item();
            if (item != null && !item.isEmpty()) {
                long ttl = Long.parseLong(item.get("ttl").n());
                if (ttl > now) {
                    Worker w = new Worker();
                    w.setId(Long.parseLong(item.get("id").n()));
                    w.setEmail(item.get("email").s());
                    w.setFirstName(item.get("firstName").s());
                    w.setLastName(item.get("lastName").s());
                    return Optional.of(w);
                }
            }
        } catch (DynamoDbException ignored) {
        }

        Optional<Worker> worker = api.findById(id);
        worker.ifPresent(w -> {
            Map<String, AttributeValue> put = new HashMap<>();
            put.put("PK", AttributeValue.fromS(pk));
            put.put("id", AttributeValue.fromN(String.valueOf(w.getId())));
            put.put("email", AttributeValue.fromS(w.getEmail()));
            put.put("firstName", AttributeValue.fromS(w.getFirstName()));
            put.put("lastName", AttributeValue.fromS(w.getLastName()));
            put.put("ttl", AttributeValue.fromN(String.valueOf(now + 300)));
            try {
                dynamoDb.putItem(PutItemRequest.builder()
                        .tableName(TABLE)
                        .item(put)
                        .build());
            } catch (DynamoDbException ignored) {
            }
        });
        return worker;
    }

    @Override
    public Optional<CreatedWorker> create(String name, String job) {
        return api.create(name, job);
    }

    @Override
    public Optional<UpdatedWorker> update(Long id, String name, String job) {
        Optional<UpdatedWorker> result = api.update(id, name, job);
        try {
            dynamoDb.deleteItem(DeleteItemRequest.builder()
                    .tableName(TABLE)
                    .key(key("user#" + id))
                    .build());
        } catch (DynamoDbException ignored) {
        }
        return result;
    }

    @Override
    public List<Worker> list(int page) {
        String pk = "all-users#" + page;
        long now = System.currentTimeMillis() / 1000;
        try {
            GetItemResponse resp = dynamoDb.getItem(GetItemRequest.builder()
                    .tableName(TABLE)
                    .key(key(pk))
                    .build());
            Map<String, AttributeValue> item = resp.item();
            if (item != null && !item.isEmpty()) {
                long ttl = Long.parseLong(item.get("ttl").n());
                if (ttl > now) {
                    String data = item.get("data").s();
                    JSONArray arr = new JSONArray(data);
                    List<Worker> workers = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        Worker w = new Worker();
                        w.setId(obj.getLong("id"));
                        w.setEmail(obj.getString("email"));
                        w.setFirstName(obj.getString("firstName"));
                        w.setLastName(obj.getString("lastName"));
                        workers.add(w);
                    }
                    return workers;
                }
            }
        } catch (DynamoDbException ignored) {
        }

        List<Worker> workers = api.list(page);
        try {
            JSONArray arr = new JSONArray();
            for (Worker w : workers) {
                arr.put(new JSONObject()
                        .put("id", w.getId())
                        .put("email", w.getEmail())
                        .put("firstName", w.getFirstName())
                        .put("lastName", w.getLastName()));
            }
            Map<String, AttributeValue> put = new HashMap<>();
            put.put("PK", AttributeValue.fromS(pk));
            put.put("data", AttributeValue.fromS(arr.toString()));
            put.put("ttl", AttributeValue.fromN(String.valueOf(now + 120)));
            dynamoDb.putItem(PutItemRequest.builder()
                    .tableName(TABLE)
                    .item(put)
                    .build());
        } catch (DynamoDbException ignored) {
        }
        return workers;
    }
}
