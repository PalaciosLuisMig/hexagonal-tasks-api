package com.acme.hexagonal.adapters.out.persistence.jpa;

import com.acme.hexagonal.domain.model.TaskStatus;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA entity used exclusively by the JPA adapter.
 *
 * <p>It belongs to the infrastructure layer: the domain never sees it, and its shape is
 * free to follow the database instead of the business model.
 */
@Entity
@Table(name = "tasks")
public class TaskJpaEntity extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String uuid;

    @Column(nullable = false)
    public String title;

    public String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TaskStatus status;

    public LocalDateTime createdAt;

    public LocalDate dueDate;
}