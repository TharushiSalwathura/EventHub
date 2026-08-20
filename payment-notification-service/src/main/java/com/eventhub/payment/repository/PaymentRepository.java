package com.eventhub.payment.repository;

import com.eventhub.payment.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByTransactionReference(String transactionReference);

    List<Payment> findByUserId(Long userId);

    List<Payment> findAllByOrderByCreatedAtDesc();
}
