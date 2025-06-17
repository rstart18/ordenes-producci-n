package com.example.ordenes.application.usecase;

import com.example.ordenes.domain.model.Worker;
import com.example.ordenes.domain.model.CreatedWorker;
import com.example.ordenes.domain.repository.WorkerRepository;

import java.util.Optional;

/**
 * Implementación del caso de uso de trabajadores que delega en el repositorio.
 */
public class ManageWorkerService implements ManageWorkerUseCase {

    private final WorkerRepository repository;

    public ManageWorkerService(WorkerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Worker> get(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<CreatedWorker> create(String name, String job) {
        return repository.create(name, job);
    }
}
