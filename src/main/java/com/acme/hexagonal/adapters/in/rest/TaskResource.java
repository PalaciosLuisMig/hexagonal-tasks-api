package com.acme.hexagonal.adapters.in.rest;

import com.acme.hexagonal.adapters.in.rest.dto.CreateTaskRequest;
import com.acme.hexagonal.adapters.in.rest.dto.TaskResponse;
import com.acme.hexagonal.application.port.in.CompleteTaskUseCase;
import com.acme.hexagonal.application.port.in.CreateTaskUseCase;
import com.acme.hexagonal.application.port.in.DeleteTaskUseCase;
import com.acme.hexagonal.application.port.in.ListTasksUseCase;
import com.acme.hexagonal.domain.model.Task;
import com.acme.hexagonal.domain.model.TaskId;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Inbound (driving) adapter: exposes the use-cases over HTTP.
 *
 * <p>Everything HTTP-related (DTOs, status codes, serialization, path variables) happens
 * here. The class only talks to the application ports ({@code *UseCase}) so the core is
 * completely unaware that a REST API exists.
 */
@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {

    private final CreateTaskUseCase createTask;
    private final ListTasksUseCase listTasks;
    private final CompleteTaskUseCase completeTask;
    private final DeleteTaskUseCase deleteTask;

    public TaskResource(CreateTaskUseCase createTask,
                        ListTasksUseCase listTasks,
                        CompleteTaskUseCase completeTask,
                        DeleteTaskUseCase deleteTask) {
        this.createTask = createTask;
        this.listTasks = listTasks;
        this.completeTask = completeTask;
        this.deleteTask = deleteTask;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(CreateTaskRequest request) {
        Task created = createTask.create(request.toCommand());
        return Response.status(Response.Status.CREATED).entity(TaskResponse.from(created)).build();
    }

    @GET
    public List<TaskResponse> list() {
        return listTasks.listAll().stream().map(TaskResponse::from).toList();
    }

    @PUT
    @Path("/{id}/complete")
    public TaskResponse complete(@PathParam("id") String id) {
        return TaskResponse.from(completeTask.complete(TaskId.of(id)));
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        deleteTask.delete(TaskId.of(id));
        return Response.noContent().build();
    }
}