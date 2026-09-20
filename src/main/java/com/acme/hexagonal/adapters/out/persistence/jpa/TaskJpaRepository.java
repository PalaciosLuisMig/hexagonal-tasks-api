package com.acme.hexagonal.adapters.out.persistence.jpa;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

/**
 * Panache repository bound to the {@link TaskJpaEntity} table.
 *
 * <p>This is an infrastructure concern. The core only sees the {@code TaskRepository} port;
 * the fact that we query Hibernate here is invisible to it.
 */
@ApplicationScoped
public class TaskJpaRepository implements PanacheRepository<TaskJpaEntity> {

    public Optional<TaskJpaEntity> findByUuid(String uuid) {
        return find("uuid", uuid).firstResultOptional();
    }
}