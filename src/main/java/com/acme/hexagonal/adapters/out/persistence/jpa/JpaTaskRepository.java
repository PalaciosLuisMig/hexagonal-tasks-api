package com.acme.hexagonal.adapters.out.persistence.jpa;

import com.acme.hexagonal.application.port.out.TaskRepository;
import com.acme.hexagonal.domain.model.Task;
import com.acme.hexagonal.domain.model.TaskId;

import io.quarkus.arc.properties.IfBuildProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Side adapter that implements the {@link TaskRepository} port on top of JPA (Panache + H2).
 *
 * <p>It is only active when {@code app.persistence=jpa}. The code translates between the
 * domain model ({@link Task}) and the infrastructure model ({@link TaskJpaEntity}), which
 * keeps the core free of JPA annotations.
 */
@ApplicationScoped
@IfBuildProperty(name = "app.persistence", stringValue = "jpa")
public class JpaTaskRepository implements TaskRepository {

    private final TaskJpaRepository repository;

    public JpaTaskRepository(TaskJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Task> findById(TaskId id) {
        return repository.findByUuid(id.value().toString()).map(this::toDomain);
    }

    @Override
    public List<Task> findAll() {
        return repository.listAll().stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public Task save(Task task) {
        Optional<TaskJpaEntity> existing = repository.findByUuid(task.id().value().toString());
        TaskJpaEntity entity = existing.orElseGet(TaskJpaEntity::new);
        entity.uuid = task.id().value().toString();
        entity.title = task.title();
        entity.description = task.description();
        entity.status = task.status();
        entity.createdAt = task.createdAt();
        entity.dueDate = task.dueDate();
        if (existing.isEmpty()) {
            repository.persist(entity);
        }
        return toDomain(entity);
    }

    @Override
    @Transactional
    public void deleteById(TaskId id) {
        repository.delete("uuid", id.value().toString());
    }

    private Task toDomain(TaskJpaEntity entity) {
        return Task.restore(
                TaskId.of(entity.uuid),
                entity.title,
                entity.description,
                entity.dueDate,
                entity.status,
                entity.createdAt
        );
    }
}