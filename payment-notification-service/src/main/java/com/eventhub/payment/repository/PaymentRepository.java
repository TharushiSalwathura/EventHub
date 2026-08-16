package com.eventhub.payment.repository;

import com.eventhub.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByTransactionReference(String transactionReference);

    List<Payment> findByUserId(Long userId);

    List<Payment> findAllByOrderByCreatedAtDesc();
}
