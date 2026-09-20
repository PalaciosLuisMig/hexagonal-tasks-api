package com.acme.hexagonal.domain.model;

import com.acme.hexagonal.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pure JUnit tests of the domain model. No Quarkus, CDI or database involved:
 * the domain is the layer with most business value and it must be testable in isolation.
 */
class TaskTest {

    @Test
    void create_setsIdStatusAndTitle() {
        Task task = Task.create("Buy milk", "1L, whole", LocalDate.of(2026, 10, 1));

        assertNotNull(task.id());
        assertEquals("Buy milk", task.title());
        assertEquals(TaskStatus.OPEN, task.status());
    }

    @Test
    void create_rejectsBlankTitle() {
        assertThrows(BusinessRuleException.class, () -> Task.create("   ", null, null));
        assertThrows(BusinessRuleException.class, () -> Task.create(null, null, null));
    }

    @Test
    void create_trimsTitle() {
        Task task = Task.create("  Write the report  ", null, null);
        assertEquals("Write the report", task.title());
    }

    @Test
    void complete_movesStatusToDone() {
        Task task = Task.create("Review PR", null, null);
        task.complete();
        assertEquals(TaskStatus.DONE, task.status());
    }

    @Test
    void complete_alreadyCompleted_throwsBusinessRule() {
        Task task = Task.create("Review PR", null, null);
        task.complete();
        assertThrows(BusinessRuleException.class, task::complete);
    }

    @Test
    void start_afterDone_throwsBusinessRule() {
        Task task = Task.create("Review PR", null, null);
        task.complete();
        assertThrows(BusinessRuleException.class, task::start);
    }

    @Test
    void start_movesStatusToInProgress() {
        Task task = Task.create("Write docs", null, null);
        task.start();
        assertEquals(TaskStatus.IN_PROGRESS, task.status());
    }
}