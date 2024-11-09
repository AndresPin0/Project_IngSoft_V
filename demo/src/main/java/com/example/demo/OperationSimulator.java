package com.example.demo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class OperationSimulator {

    private final Counter counter;

    public OperationSimulator(MeterRegistry registry) {
        this.counter = Counter.builder("custom.operations.count")
                .description("Conteo de operaciones simuladas")
                .register(registry);
    }

    @PostConstruct
    public void simulateOperations() {
        new Thread(() -> {
            while (true) {
                counter.increment();
                System.out.println("Operación simulada realizada");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }
}
