package com.badminton.repository.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * Find by ID and not deleted
     */
    Optional<T> findByIdAndDeletedAtIsNull(ID id);

    /**
     * Find all not deleted
     */
    List<T> findAllByDeletedAtIsNull();

    /**
     * Check if exists by ID and not deleted
     */
    boolean existsByIdAndDeletedAtIsNull(ID id);
}
