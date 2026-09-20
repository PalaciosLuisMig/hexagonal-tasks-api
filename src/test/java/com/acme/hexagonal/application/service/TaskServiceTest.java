package com.acme.hexagonal.application.service;

import com.acme.hexagonal.application.exception.TaskAlreadyCompletedException;
import com.acme.hexagonal.application.exception.TaskNotFoundException;
import com.acme.hexagonal.application.port.in.CreateTaskCommand;
import com.acme.hexagonal.application.port.out.TaskRepository;
import com.acme.hexagonal.domain.model.Task;
import com.acme.hexagonal.domain.model.TaskId;
import com.acme.hexagonal.domain.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests of the application layer using a <b>fake</b> implementation of the port.
 *
 * <p>The fake substitutes the real persistence adapter, proving that the use cases can be
 * exercised without Quarkus, a database or HTTP. This is the practical payoff of depending
 * on a port instead of a concrete repository.
 */
class TaskServiceTest {

    private FakeTaskRepository fakeRepository;

    @BeforeEach
    void setUp() {
        fakeRepository = new FakeTaskRepository();
    }

    @Test
    void createTask_savesAndReturnsOpenTask() {
        CreateTaskService service = new CreateTaskService(fakeRepository);

        Task created = service.create(new CreateTaskCommand("Write README", null, null));

        assertEquals("Write README", created.title());
        assertEquals(TaskStatus.OPEN, created.status());
        assertTrue(fakeRepository.findAll().contains(created));
    }

    @Test
    void completeTask_existingTask_becomesDone() {
        Task task = Task.create("Ship demo", null, null);
        fakeRepository.save(task);
        CompleteTaskService service = new CompleteTaskService(fakeRepository);

        Task completed = service.complete(task.id());

        assertEquals(TaskStatus.DONE, completed.status());
    }

    @Test
    void completeTask_missingTask_throwsNotFound() {
        CompleteTaskService service = new CompleteTaskService(fakeRepository);

        assertThrows(TaskNotFoundException.class, () -> service.complete(TaskId.generate()));
    }

    @Test
    void completeTask_alreadyDone_throwsAlreadyCompleted() {
        Task task = Task.create("Ship demo", null, null);
        task.complete();
        fakeRepository.save(task);
        CompleteTaskService service = new CompleteTaskService(fakeRepository);

        assertThrows(TaskAlreadyCompletedException.class, () -> service.complete(task.id()));
    }

    @Test
    void deleteTask_missingTask_throwsNotFound() {
        DeleteTaskService service = new DeleteTaskService(fakeRepository);

        assertThrows(TaskNotFoundException.class, () -> service.delete(TaskId.generate()));
    }

    private static final class FakeTaskRepository implements TaskRepository {

        private final ConcurrentMap<TaskId, Task> store = new ConcurrentHashMap<>();

        @Override
        public Optional<Task> findById(TaskId id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<Task> findAll() {
            return store.values().stream().toList();
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
}