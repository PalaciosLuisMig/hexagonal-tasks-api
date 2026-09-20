# Plan: `PUT /tasks/{id}/due-date` — Cambiar fecha límite de una tarea

## Objetivo

Agregar el caso de uso "cambiar la fecha límite de una tarea" respetando la arquitectura hexagonal existente. Regla de negocio: una tarea en estado `DONE` **no puede ser reprogramada** → responde `409 conflict`. Tarea inexistente → `404 not_found`.

## Decisiones tomadas

| Decisión | Valor | Justificación |
|----------|-------|---------------|
| Excepción de aplicación nueva | `TaskCannotBeRescheduledException` | Regla de estado ≠ body inválido; se mapea a `409` |
| Mapper | Nuevo `@Provider` propio, uno por excepción | Patrón ya implementado; no se toca ningún mapper existente |
| Puerto out | **Sin cambios** | `TaskRepository.findById + save` ya cubre la mutación |

## Arquitectura de archivos resultante

```
domain/model/Task                 (+ método reschedule)                     MODIFICAR
application/service/              (+ UpdateTaskDueDateService)              CREAR
application/port/in/              (+ UpdateTaskDueDateUseCase,
                                   + UpdateTaskDueDateCommand)              CREAR
application/exception/            (+ TaskCannotBeRescheduledException)      CREAR
adapters/in/rest/                 (+ UpdateTaskDueDateRequest,
                                   + TaskCannotBeRescheduledExceptionMapper,
                                   * modificar TaskResource)                CREAR + MODIFICAR
adapters/out/**                   (sin cambios)                             NO TOCAR
```

## Paso 1 — Dominio: `Task.reschedule(LocalDate)`

**Modificar** `domain/model/Task.java` — junto a `complete()` (línea 67):

```java
public void reschedule(LocalDate newDueDate) {
    if (status == TaskStatus.DONE) {
        throw new BusinessRuleException("a completed task cannot be rescheduled");
    }
    this.dueDate = newDueDate;
}
```

## Paso 2 — Puerto de entrada

**Crear** `application/port/in/UpdateTaskDueDateCommand.java`:

```java
package com.acme.hexagonal.application.port.in;

import com.acme.hexagonal.domain.model.TaskId;

import java.time.LocalDate;

public record UpdateTaskDueDateCommand(TaskId id, LocalDate newDueDate) {
}
```

**Crear** `application/port/in/UpdateTaskDueDateUseCase.java`:

```java
package com.acme.hexagonal.application.port.in;

import com.acme.hexagonal.domain.model.Task;

public interface UpdateTaskDueDateUseCase {

    Task updateDueDate(UpdateTaskDueDateCommand command);
}
```

## Paso 3 — Excepción de aplicación + use case

**Crear** `application/exception/TaskCannotBeRescheduledException.java`:

```java
package com.acme.hexagonal.application.exception;

import com.acme.hexagonal.domain.model.TaskId;

public class TaskCannotBeRescheduledException extends RuntimeException {

    public TaskCannotBeRescheduledException(TaskId id) {
        super("Task %s cannot be rescheduled because it is completed".formatted(id.value()));
    }
}
```

**Crear** `application/service/UpdateTaskDueDateService.java` (`@ApplicationScoped`, patrón de `CompleteTaskService.java`):

```java
package com.acme.hexagonal.application.service;

import com.acme.hexagonal.application.exception.TaskCannotBeRescheduledException;
import com.acme.hexagonal.application.exception.TaskNotFoundException;
import com.acme.hexagonal.application.port.in.UpdateTaskDueDateCommand;
import com.acme.hexagonal.application.port.in.UpdateTaskDueDateUseCase;
import com.acme.hexagonal.application.port.out.TaskRepository;
import com.acme.hexagonal.domain.model.Task;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UpdateTaskDueDateService implements UpdateTaskDueDateUseCase {

    private final TaskRepository taskRepository;

    public UpdateTaskDueDateService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task updateDueDate(UpdateTaskDueDateCommand command) {
        Task task = taskRepository.findById(command.id())
                .orElseThrow(() -> new TaskNotFoundException(command.id()));
        try {
            task.reschedule(command.newDueDate());
        } catch (RuntimeException e) {
            throw new TaskCannotBeRescheduledException(command.id());
        }
        return taskRepository.save(task);
    }
}
```

## Paso 4 — Adapter de entrada (REST)

**Crear** `adapters/in/rest/dto/UpdateTaskDueDateRequest.java`:

```java
package com.acme.hexagonal.adapters.in.rest.dto;

import com.acme.hexagonal.application.port.in.UpdateTaskDueDateCommand;
import com.acme.hexagonal.domain.model.TaskId;

import java.time.LocalDate;

public record UpdateTaskDueDateRequest(String dueDate) {

    public UpdateTaskDueDateCommand toCommand(TaskId id) {
        return new UpdateTaskDueDateCommand(id, LocalDate.parse(dueDate));
    }
}
```

**Crear** `adapters/in/rest/TaskCannotBeRescheduledExceptionMapper.java` (patrón uno-mapper-por-excepción):

```java
package com.acme.hexagonal.adapters.in.rest;

import com.acme.hexagonal.application.exception.TaskCannotBeRescheduledException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TaskCannotBeRescheduledExceptionMapper
        implements ExceptionMapper<TaskCannotBeRescheduledException> {

    @Override
    public Response toResponse(TaskCannotBeRescheduledException exception) {
        return ExceptionMapperSupport.error(Response.Status.CONFLICT, "conflict", exception.getMessage());
    }
}
```

**Modificar** `adapters/in/rest/TaskResource.java`:
- Constructor: agregar `UpdateTaskDueDateUseCase updateDueDate` (`TaskResource.java:41-49`)
- Método nuevo:

```java
@PUT
@Path("/{id}/due-date")
@Consumes(MediaType.APPLICATION_JSON)
public TaskResponse updateDueDate(@PathParam("id") String id, UpdateTaskDueDateRequest request) {
    return TaskResponse.from(updateDueDate.updateDueDate(request.toCommand(TaskId.of(id))));
}
```

## Paso 5 — Adapters de salida

Sin cambios (`JpaTaskRepository` / `InMemoryTaskRepository` ya implementan el puerto).

## Paso 6 — Tests

| Archivo | Casos |
|---------|-------|
| `TaskTest.java` | `reschedule()` cambia fecha; sobre `DONE` lanza `BusinessRuleException` |
| `TaskServiceTest.java` | id inexistente → `TaskNotFoundException`; tarea `DONE` → `TaskCannotBeRescheduledException` |
| `TaskResourceTest.java` | `PUT /tasks/{id}/due-date` → 200 con nuevo `dueDate`; sobre tarea completada → **409** `"conflict"`; id inválido/inexistente → `404` |

## Verificación

```bash
./mvnw test   # esperado: BUILD SUCCESS, 19 tests existentes + nuevos
```