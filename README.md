# Quarkus · Arquitectura Hexagonal (demo didáctica)

Demo de pila completa para aprender **arquitectura hexagonal / ports & adapters**
sobre **Quarkus 3.39 + Java 25 + Mandrel (nativo)**. Dominio de ejemplo: gestión de tareas
(crear, listar, completar, eliminar).

---

## 1. El hexágono

```
                        ┌────────────────────────────────────────────┐
                        │                 APPLICATION                │
                        │   ┌─────────────────────────────────────┐  │
   Driver (tu input)    │   │                DOMAIN               │  │     Driven (tu output)
 ┌───────────────┐      │   │  Task, TaskId, TaskStatus          │  │      ┌──────────────┐
 │  ▛ HTTP / REST ▜────▶│   │  + reglas de negocio               │  │      │ DB / JPA /    │
 │  TaskResource  │     │   └─────────────────────────────────────┘  │◀─────│  colas / ...  │
 └───────┬───────┘      │                     ▲            ▲        │      └──────▲───────┘
         │              │   ┌─────────────────┴──┐   ┌─────┴─────┐  │             │
   Driving port (in)    └──▶│                   │   │TaskRepo   │  │        Driven port
   CreateTaskUseCase ◀────┘  │   USE CASES       │   │itory      │──┘        TaskRepository
   ListTasksUseCase         │   (services)      │   │(port/out) │
   CompleteTaskUseCase      └───────────────────┘   └───────────┘
```

Principios que este proyecto demuestra:

| Principio | Dónde se ve |
|---|---|
| **Dependencias apuntan hacia dentro** | `domain` no importa nada de Quarkus/JPA/HTTP; `adapters` importa `domain` y `application`. Las reglas del negocio no conocen el framework ni la base de datos. |
| **Puertos = interfaces, no implementaciones** | `*UseCase` (puertos de entrada) y `TaskRepository` (puerto de salida) son interfaces. Los adapters dependen solo de ellas. |
| **El core es trasteable** | `TaskServiceTest` prueba los casos de uso con un *fake* del puerto: sin Quarkus, sin BD, sin HTTP. |
| **Adapters intercambiables** | `InMemoryTaskRepository` y `JpaTaskRepository` implementan el mismo puerto; se eligen en build-time con `app.persistence`. |

## 2. Estructura de paquetes

```
src/main/java/com/acme/hexagonal/
├── domain/                          ← NÚCLEO (sin framework, sin BD, sin HTTP)
│   ├── model/                       Task, TaskId, TaskStatus
│   └── exception/                   BusinessRuleException
├── application/                     ← CASOS DE USO (orquestación)
│   ├── port/in/                     ← puertos de ENTRADA (driving)
│   │     CreateTaskUseCase, ListTasksUseCase, CompleteTaskUseCase, DeleteTaskUseCase
│   ├── port/out/                    ← puerto de SALIDA (driven)
│   │     TaskRepository
│   ├── service/                     ← implementaciones de los use cases
│   └── exception/                   TaskNotFoundException, TaskAlreadyCompletedException
└── adapters/                        ← FRANJA (infraestructura)
    ├── in/rest/                     ← adapter de entrada HTTP: TaskResource, DTOs, ApiExceptionHandler
    └── out/persistence/
        ├── inmemory/                ← adapter de salida en memoria (@IfBuildProperty memory)
        └── jpa/                     ← adapter de salida JPA/Panache+H2 (@IfBuildProperty jpa)
```

## 3. Flujo de una petición (GET /tasks como ejemplo)

```
Browser/curl
   │
   ▼
TaskResource (adapters.in.rest)                     ← traduce HTTP → puerto de entrada (use case)
   │  ListTasksUseCase
   ▼
ListTasksService (application.service)              ← orquesta el caso de uso
   │  TaskRepository (port/out)
   ▼
InMemoryTaskRepository o JpaTaskRepository          ← se resuelve según app.persistence
   │
   ▼
Task (domain.model)                                 ← modelo de negocio sin infraestructura
```

El sentido de las flechas (dependency inversion): el **core** decide el contrato
(`TaskRepository`), y el **adapter** se amolda a ese contrato. Es lo opuesto al modelo
tradicional donde tu código depende de la librería de persistencia.

## 4. Dónde va cada responsabilidad

- **Domain** → reglas de negocio: no se puede completar una tarea ya completada, el título
  no puede estar vacío, estados permitidos (`OPEN → IN_PROGRESS → DONE`).
- **Application** → traducir esas reglas en errores de aplicación (`TaskNotFoundException`,
  `TaskAlreadyCompletedException`) y orquestar: buscar → operar → persistir.
- **Adapters** → HTTP: status codes (400/404/409), DTOs, JSON. Persistencia: mapeo
  `Task ↔ TaskJpaEntity`, SQL/Panache.

## 5. Cómo ejecutar

Prerequisito: clonar/instalar Java 25 y Mandrel (ver `native-env.sh`), luego:

```bash
# entorno Mandrel para esta demo
source native-env.sh

# Modo dev (hot reload) → http://localhost:8080/q/dev/
./mvnw quarkus:dev

# Tests (unitarios + integración REST)
./mvnw test

# Build + ejecutable nativo (Mandrel)
./mvnw clean package -Dnative
./target/hexagonal-tasks-api-1.0.0-SNAPSHOT-runner
```

### Probar la API

```bash
# crear
curl -s -X POST http://localhost:8080/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Aprender hexágono","dueDate":"2026-10-01"}'

# listar
curl -s http://localhost:8080/tasks

# completar
curl -s -X PUT http://localhost:8080/tasks/<id>/complete

# eliminar
curl -s -X DELETE http://localhost:8080/tasks/<id>
```

### Probar la API con Insomnia

En la raíz del repo tienes la colección [`insomnia-export.json`](insomnia-export.json) con
los 4 endpoints listos (crear, listar, completar, eliminar). Para usarla:

1. Abre Insomnia → `Import/Export` → `Import Data` → `From File`.
2. Selecciona `insomnia-export.json`.
3. Ajusta los IDs de ejemplo (`be485071-...`) de *Complete task* y *Delete task* con los de
   tus tareas reales (métele el ID que devuelve `POST /tasks`).

## 6. Cambiar de adaptador de persistencia

En `application.properties`:

```properties
app.persistence=memory   # ← adapter en memoria (por defecto)
app.persistence=jpa      # ← adapter JPA (Panache + H2 en memoria)
```

> Cambia en **build-time**: reinicia `quarkus dev` (o re-empaqueta) al tocar esta propiedad.
> Como ambos adapters implementan el mismo puerto `TaskRepository`, el core no cambia nada.

## 7. Tests

- `TaskTest` (domain) — reglas de negocio puras.
- `TaskServiceTest` (application) — use cases con un *fake* del puerto.
- `TaskResourceTest` (integration) — la rebanada completa HTTP→core→adapter vía `@QuarkusTest`.

## 8. Dockerizar

Requiere Docker con el daemon corriendo (en Windows/WSL: Docker Desktop con la
integración WSL activada). El proyecto incluye dos `Dockerfile` multi-etapa **autónomos**:
construyen la app dentro del contenedor, sin necesidad de Maven/Java local.

### Imagen JVM (recomendada)

```bash
docker build -f src/main/docker/Dockerfile.jvm.multistage -t hexagonal-tasks-api:jvm .
docker run -i --rm -p 8080:8080 hexagonal-tasks-api:jvm
```

Imagen con JVM (~500MB): build más rápido y fácil de depurar.

### Imagen nativa (Mandrel)

```bash
docker build -f src/main/docker/Dockerfile.native.multistage -t hexagonal-tasks-api:native .
docker run -i --rm -p 8080:8080 hexagonal-tasks-api:native
```

Imagen sin JVM (~100MB) y arranque en milisegundos. La primera build tarda
**10-15 minutos** y necesita ~4GB de RAM.

### Docker Compose

```bash
docker compose up --build                    # API JVM    → http://localhost:8080
docker compose --profile native up --build   # API nativa → http://localhost:8081
```

### Probar la imagen

El adapter por defecto es `app.persistence=memory` (H2 embebida), así que el
contenedor **no necesita base de datos externa**:

```bash
curl -s http://localhost:8080/tasks
curl -s -X POST http://localhost:8080/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Aprender hexágono","dueDate":"2026-10-01"}'
```

> Nota: con `app.persistence=jpa` los datos viven en un H2 embebido **dentro del
> contenedor**; reiniciarlo los pierde. Para estado persistente habría que montar un
> volumen o usar una base de datos externa.

## 9. Para seguir aprendiendo (retos)

1. Añade un caso de uso nuevo, p. ej. `UpdateTaskDueDateUseCase`, sin tocar los adapters.
2. Sustituye H2 por PostgreSQL (adapter `jpa`) cambiando solo el `datasource` y probando en nativo.
3. Añade un segundo adapter de entrada: por ejemplo **CLI** (`quarkus-picocli`) que invoque
   los mismos use cases — el hexágono lo permite sin tocar el core.
4. Acopla el adapter de salida a un bus de eventos (Kafka/AMQP) para "cola de salida".
5. Convierte el proyecto en multi-módulo Maven (`domain`, `application`, `bootstrap`) para
   que las reglas de dependencia se cumplan a nivel de compilación.