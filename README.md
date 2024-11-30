# Proyecto de Monitoreo con Grafana, Prometheus y Loki

Este proyecto implementa un entorno de monitoreo para una aplicación de Spring Boot, utilizando **Grafana**, **Prometheus** y **Loki**. El sistema permite capturar métricas y logs en tiempo real, generando visualizaciones y alertas para realizar análisis continuo de la aplicación.

[Informe Completo](https://docs.google.com/document/d/1Dq2o_ljU-3GgbdrhOpHF_EKlOOFU-BRLny-5cwpanUU/edit?usp=sharing)

## Estructura del Proyecto

- **Aplicación de Spring Boot**: Simula operaciones y expone métricas personalizadas mediante **Micrometer**.
- **Prometheus**: Captura métricas de la aplicación y de su propio servicio para análisis.
- **Loki**: Gestiona y almacena los logs generados por la aplicación.
- **Grafana**: Visualiza métricas y logs, permitiendo la creación de alertas.

## Requisitos

- **Docker** y **Docker Compose**

## Instrucciones de Configuración

1. Clona el repositorio y navega hasta el directorio del proyecto.
2. Ejecuta los servicios con Docker Compose ubicado en el directorio /docker:

   ```bash
   docker-compose up -d

3. Endpoint de la Aplicación
- /actuator/prometheus - Métricas en formato Prometheus.

## Ejemplo de Métricas
La aplicación registra un contador personalizado custom.operations.count que se incrementa con cada operación simulada.

## Configuración de Alertas
Prometheus está configurado con alertas básicas que se activan cuando ciertos umbrales de métricas son alcanzados, permitiendo una respuesta rápida ante problemas potenciales.


