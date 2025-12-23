package com.badminton.repository.product;

import com.badminton.entity.product.Service;
import com.badminton.enums.ServiceStatus;
import com.badminton.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    Optional<Service> findBySlug(String slug);

    List<Service> findByStatus(ServiceStatus status);

    List<Service> findByServiceType(ServiceType serviceType);

    @Query("SELECT s FROM Service s WHERE s.status = 'ACTIVE' ORDER BY s.displayOrder")
    List<Service> findAllActiveServices();

    @Query("SELECT s FROM Service s WHERE s.isFeatured = true AND s.status = 'ACTIVE' ORDER BY s.displayOrder")
    List<Service> findFeaturedServices();
}
