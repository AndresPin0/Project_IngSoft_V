package com.example.demo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api")
public class CurriculumController {

    private static final Logger logger = LoggerFactory.getLogger(CurriculumController.class);

    private final CursoRepository cursoRepository;
    private final Counter requestCounter;
    private final Counter creationCounter;
    private final Counter errorCounter;
    private final Timer transactionTimer;
    private final Timer dbAccessTimer;

    @Autowired
    public CurriculumController(CursoRepository cursoRepository, MeterRegistry meterRegistry) {
        this.cursoRepository = cursoRepository;

        // Métrica para el número total de solicitudes
        this.requestCounter = Counter.builder("api_requests_total")
                .description("Total de solicitudes a la API")
                .register(meterRegistry);

        // Métrica para el número total de elementos creados
        this.creationCounter = Counter.builder("elements_created_total")
                .description("Total de elementos creados")
                .register(meterRegistry);

        // Métrica para el número total de errores
        this.errorCounter = Counter.builder("api_errors_total")
                .description("Total de errores en la API")
                .register(meterRegistry);

        // Métrica para la duración de las transacciones
        this.transactionTimer = Timer.builder("transactions_duration_seconds")
                .description("Duración de las transacciones")
                .register(meterRegistry);

        // Métrica para el tiempo de acceso a la base de datos
        this.dbAccessTimer = Timer.builder("database_access_duration_seconds")
                .description("Tiempo de acceso a la base de datos")
                .register(meterRegistry);
    }

    @GetMapping(value = "/add-course", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> addCourseForm() throws IOException {
        Resource resource = new ClassPathResource("static/addCourse.html");
        String body = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(body);
    }

    @GetMapping("/cursos")
    public ResponseEntity<List<Curso>> obtenerCursos() {
        String operationId = UUID.randomUUID().toString();

        requestCounter.increment();
        logger.info(Markers.append("operationId", operationId)
                        .and(Markers.append("event", "obtenerCursos")),
                "Inicio de obtenerCursos");

        List<Curso> cursos = transactionTimer.record(() ->
                dbAccessTimer.record(() -> cursoRepository.findAll())
        );

        logger.info(Markers.append("operationId", operationId)
                        .and(Markers.append("event", "obtenerCursos")),
                "Cursos obtenidos exitosamente");

        return ResponseEntity.ok(cursos);
    }

    @PostMapping("/cursos")
    public ResponseEntity<Curso> crearCurso(@ModelAttribute Curso curso) {
        String operationId = UUID.randomUUID().toString();

        requestCounter.increment();
        logger.info(Markers.append("operationId", operationId)
                        .and(Markers.append("event", "crearCurso")),
                "Inicio de crearCurso");

        try {
            Curso nuevoCurso = transactionTimer.record(() ->
                    dbAccessTimer.record(() -> cursoRepository.save(curso))
            );

            creationCounter.increment();
            logger.info(Markers.append("operationId", operationId)
                            .and(Markers.append("event", "crearCurso")),
                    "Curso creado exitosamente");

            return ResponseEntity.ok(nuevoCurso);
        } catch (Exception e) {
            errorCounter.increment();
            logger.error(Markers.append("operationId", operationId)
                            .and(Markers.append("event", "crearCurso"))
                            .and(Markers.append("error", e.getMessage())),
                    "Error al crear el curso", e);
            throw e;
        }
    }

    @GetMapping("/cursos/{id}")
    public ResponseEntity<Curso> obtenerCurso(@PathVariable Long id) {
        String operationId = UUID.randomUUID().toString();

        requestCounter.increment();
        logger.info(Markers.append("operationId", operationId)
                        .and(Markers.append("entityId", id))
                        .and(Markers.append("event", "obtenerCurso")),
                "Inicio de obtenerCurso");

        try {
            Curso curso = transactionTimer.record(() ->
                    dbAccessTimer.record(() -> cursoRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Curso no encontrado")))
            );

            logger.info(Markers.append("operationId", operationId)
                            .and(Markers.append("entityId", id))
                            .and(Markers.append("event", "obtenerCurso")),
                    "Curso obtenido exitosamente");

            return ResponseEntity.ok(curso);
        } catch (Exception e) {
            errorCounter.increment();
            logger.error(Markers.append("operationId", operationId)
                            .and(Markers.append("entityId", id))
                            .and(Markers.append("event", "obtenerCurso"))
                            .and(Markers.append("error", e.getMessage())),
                    "Error al obtener el curso", e);
            throw e;
        }
    }
}
