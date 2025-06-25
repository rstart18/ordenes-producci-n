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
una clase `MainApplication` que inicia un pequeño servidor HTTP para consultar
trabajadores.

Para arrancarlo ejecute:

```bash
gradle :infrastructure:run
```

Luego podrá acceder con Postman a `http://localhost:8080/workers/{id}` para
obtener la información de un trabajador o realizar un `POST` a
`http://localhost:8080/workers` para dar de alta uno nuevo. El cuerpo de la
petición debe incluir `name` y `job`:

```json
{
  "name": "morpheus",
  "job": "leader"
}
```

La respuesta de la API incluirá el identificador asignado y la fecha de creación:

```json
{
  "name": "morpheus",
  "job": "leader",
  "id": "358",
  "createdAt": "2025-06-17T02:55:31.927Z"
}
```
`http://localhost:8080/workers` para dar de alta uno nuevo.

Si desea modificar un trabajador existente puede realizar un `PUT` a
`http://localhost:8080/workers/{id}` con los nuevos datos:

```json
{
  "name": "morpheus",
  "job": "zion resident"
}
```

La respuesta será similar a:

```json
{
  "name": "morpheus",
  "job": "zion resident",
  "updatedAt": "2025-06-18T02:50:58.315Z"
}
```

El registro de trabajadores se obtiene de la API pública de ReqRes.
Cada petición envía la cabecera `x-api-key: reqres-free-v1` y está
protegida por un Circuit Breaker básico en el módulo `infrastructure`.

## DynamoDB local con LocalStack

Para probar el proyecto con una base de datos DynamoDB local puede utilizar [LocalStack](https://docs.localstack.cloud/). Se necesita tener Docker y Python instalados.

1. Instale LocalStack:

```bash
pip install localstack
```

2. Inicie los servicios (en segundo plano) desde este directorio:

```bash
docker-compose up -d
```

Esto levantará LocalStack escuchando en `http://localhost:4566` únicamente con el servicio de DynamoDB habilitado. Puede verificar que está funcionando ejecutando:

```bash
aws --endpoint-url=http://localhost:4566 dynamodb list-tables
```

3. Si desea crear una tabla de ejemplo:

```bash
aws --endpoint-url=http://localhost:4566 dynamodb create-table \
  --table-name ProductionOrders \
  --attribute-definitions AttributeName=id,AttributeType=N \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST
```

Con esto dispondrá de un entorno local de DynamoDB para sus pruebas.
