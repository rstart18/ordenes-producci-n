package com.example.ordenes.domain.repository;

import com.example.ordenes.domain.model.Worker;

import java.util.Optional;

/**
 * Repositorio para gestionar la información de trabajadores.
 */
public interface WorkerRepository {

    Optional<Worker> findById(Long id);

    /**
     * Crea un nuevo trabajador.
     */
    Optional<Long> create(String name, String job);
}
