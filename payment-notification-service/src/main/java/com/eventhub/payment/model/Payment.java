package com.eventhub.payment.model;

import com.eventhub.payment.model.enums.PaymentMethod;
import com.eventhub.payment.model.enums.PaymentStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    private Long id;

    private Long bookingId;

    private Long userId;

    private Double amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus status;

    @Indexed(unique = true)
    private String transactionReference;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
