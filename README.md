# ordenes-producci-n

Base de proyecto en Java 8 siguiendo la arquitectura Hexagonal para gestionar 
ordenes de producción. La lógica de negocio aún no ha sido implementada.

## Estructura

- **domain**: contiene las entidades y repositorios del dominio.
- **application**: define los casos de uso y servicios de aplicación.
- **infrastructure**: implementación de repositorios y punto de entrada.

## Compilación

Utilice Gradle para compilar el proyecto:

```bash
gradle build
```

Esto genera los artefactos de cada módulo. El módulo `infrastructure` contiene
una clase `MainApplication` con un ejemplo básico de ejecución.

El registro de trabajadores se obtiene de la API pública de ReqRes.
Cada petición envía la cabecera `x-api-key: reqres-free-v1` y está
protegida por un Circuit Breaker básico en el módulo `infrastructure`.
