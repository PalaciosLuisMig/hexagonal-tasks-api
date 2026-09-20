package com.acme.hexagonal.adapters.out.persistence.inmemory;

import com.acme.hexagonal.application.port.out.TaskRepository;
import com.acme.hexagonal.domain.model.Task;
import com.acme.hexagonal.domain.model.TaskId;

import io.quarkus.arc.properties.IfBuildProperty;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Driving side adapter of the {@link TaskRepository} port backed by an in-memory map.
 *
 * <p>It is only active when {@code app.persistence=memory} (see {@code application.properties}).
 * Because it implements the same port as the JPA adapter, both can co-exist and be swapped
 * without touching one single line of the core. That is the port-adapter pattern.
 */
@ApplicationScoped
@IfBuildProperty(name = "app.persistence", stringValue = "memory")
public class InMemoryTaskRepository implements TaskRepository {

    private final ConcurrentMap<TaskId, Task> store = new ConcurrentHashMap<>();

    @Override
    public Optional<Task> findById(TaskId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Task save(Task task) {
        store.put(task.id(), task);
        return task;
    }

    @Override
    public void deleteById(TaskId id) {
        store.remove(id);
    }
}