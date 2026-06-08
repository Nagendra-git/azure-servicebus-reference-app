package com.example.azure_servicebus_reference_app.listener;

import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.example.azure_servicebus_reference_app.dto.AiQueueMessageDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Listener responsible for processing messages received from
 * Azure Service Bus.
 *
 * <p>This component is invoked by the configured
 * {@code ServiceBusProcessorClient} whenever a new message
 * arrives on the subscribed queue.</p>
 *
 * <p>The listener deserializes the received message payload into
 * an {@link AiQueueMessageDto} and performs the required business
 * processing.</p>
 *
 * @author Nagendra
 * @since 1.0
 */
@Component
@Log4j2
@RequiredArgsConstructor
@ConditionalOnProperty(name = "servicebus.listener.enabled", havingValue = "true")
public class QueueListener {

    /**
     * Object mapper used for JSON serialization and deserialization.
     */
    private final ObjectMapper objectMapper;

    /**
     * Logs listener initialization during application startup.
     */
    @PostConstruct
    public void init() {
        log.info("QueueListener initialized and ready to process messages.");
    }

    /**
     * Processes a message received from Azure Service Bus.
     *
     * <p>The message payload is deserialized into an
     * {@link AiQueueMessageDto} and then processed according
     * to the application business logic.</p>
     *
     * @param message the Service Bus message received from the queue
     * @throws RuntimeException if message processing fails
     */
    public void prepareMessageFromAi(ServiceBusReceivedMessage message) {
        try {
            AiQueueMessageDto aiQueueMessageDto = objectMapper.readValue(
                    message.getBody().toBytes(),
                    AiQueueMessageDto.class);

            log.info("Received Job {} from ai service",
                    aiQueueMessageDto.getMessage());

            // Business processing logic

        } catch (Exception e) {
            log.error("Error processing Service Bus message: ", e);
            throw new RuntimeException(e);
        }
    }
}