package com.badminton.repository.payment;

import com.badminton.entity.payment.PaymentMethod;
import com.badminton.enums.PaymentProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    Optional<PaymentMethod> findByCode(String code);

    List<PaymentMethod> findByIsActiveTrue();

    List<PaymentMethod> findByProvider(PaymentProvider provider);

    List<PaymentMethod> findByIsActiveTrueOrderByDisplayOrder();

    List<PaymentMethod> findByIsOnlineTrue();
}
