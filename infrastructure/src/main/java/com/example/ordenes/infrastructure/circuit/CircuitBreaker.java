package com.example.ordenes.infrastructure.circuit;

import java.util.function.Supplier;

/**
 * Implementación simple de Circuit Breaker.
 */
public class CircuitBreaker {

    private enum State { CLOSED, OPEN, HALF_OPEN }

    private final int failureThreshold;
    private final long retryTimePeriodMillis;

    private int failureCount = 0;
    private long lastFailureTime = 0L;
    private State state = State.CLOSED;

    public CircuitBreaker(int failureThreshold, long retryTimePeriodMillis) {
        this.failureThreshold = failureThreshold;
        this.retryTimePeriodMillis = retryTimePeriodMillis;
    }

    public synchronized <T> T execute(Supplier<T> supplier) {
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime > retryTimePeriodMillis) {
                state = State.HALF_OPEN;
            } else {
                throw new IllegalStateException("Circuit is open");
            }
        }

        try {
            T result = supplier.get();
            reset();
            return result;
        } catch (RuntimeException e) {
            recordFailure();
            throw e;
        }
    }

    private void recordFailure() {
        failureCount++;
        lastFailureTime = System.currentTimeMillis();
        if (failureCount >= failureThreshold) {
            state = State.OPEN;
        }
    }

    private void reset() {
        failureCount = 0;
        state = State.CLOSED;
    }
}
