package com.example.ordenes.application.usecase;

import com.example.ordenes.domain.model.Worker;
import com.example.ordenes.domain.model.CreatedWorker;
import com.example.ordenes.domain.model.UpdatedWorker;

import java.util.List;
import java.util.Optional;

/**
 * Caso de uso para obtener información de trabajadores.
 */
public interface ManageWorkerUseCase {

    Optional<Worker> get(Long id);

    Optional<CreatedWorker> create(String name, String job);

    Optional<UpdatedWorker> update(Long id, String name, String job);

    List<Worker> list(int page);
}
