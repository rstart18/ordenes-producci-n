package com.example.ordenes.application.usecase;

import com.example.ordenes.domain.model.Worker;

import java.util.Optional;

/**
 * Caso de uso para obtener información de trabajadores.
 */
public interface ManageWorkerUseCase {

    Optional<Worker> get(Long id);
}
