package com.eventhub.event;

import com.eventhub.event.model.Event;
import com.eventhub.event.repository.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootApplication
public class EventServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner initSampleEvents(EventRepository eventRepository) {
        return args -> {
            if (eventRepository.count() == 0) {
                eventRepository.save(Event.builder()
                        .title("Global Tech Summit 2026")
                        .description("Premier annual technology and software engineering conference with international keynote speakers.")
                        .location("Colombo Exhibition & Convention Centre")
                        .eventDate(LocalDateTime.now().plusDays(30))
                        .capacity(200)
                        .availableSeats(185)
                        .price(new BigDecimal("4500.00"))
                        .build());

                eventRepository.save(Event.builder()
                        .title("Sri Lanka Music & Arts Fest")
                        .description("Live outdoor music performance featuring top national bands, cultural dancers, and food stalls.")
                        .location("Galle Face Green, Colombo")
                        .eventDate(LocalDateTime.now().plusDays(45))
                        .capacity(500)
                        .availableSeats(420)
                        .price(new BigDecimal("2500.00"))
                        .build());

                eventRepository.save(Event.builder()
                        .title("AI & Cloud Microservices Expo")
                        .description("Hands-on workshop exploring Spring Boot 3, Spring Cloud Gateway, Docker, and GenAI integrations.")
                        .location("BMICH, Colombo")
                        .eventDate(LocalDateTime.now().plusDays(60))
                        .capacity(150)
                        .availableSeats(110)
                        .price(new BigDecimal("6000.00"))
                        .build());
            }
        };
    }
}
