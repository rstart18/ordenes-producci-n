package com.example.ordenes.domain.repository;

import com.example.ordenes.domain.model.Worker;
import com.example.ordenes.domain.model.CreatedWorker;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar la información de trabajadores.
 */
public interface WorkerRepository {

    Optional<Worker> findById(Long id);

    /**
     * Crea un nuevo trabajador.
     */
    Optional<CreatedWorker> create(String name, String job);

    /**
     * Obtiene la lista de trabajadores de la página indicada.
     */
    List<Worker> list(int page);
}
