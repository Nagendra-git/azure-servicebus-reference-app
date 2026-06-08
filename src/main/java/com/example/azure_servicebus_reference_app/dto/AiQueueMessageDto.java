package com.example.azure_servicebus_reference_app.dto;

import lombok.Data;
/**
 * Data Transfer Object (DTO) representing a message exchanged through
 * Azure Service Bus.
 *
 * <p>This DTO is used by both the publisher and consumer components
 * to serialize and deserialize message payloads transmitted via
 * Azure Service Bus queues.</p>
 * @author Nagendra
 *  @since 1.0
 */
@Data
public class AiQueueMessageDto {

    /**
     * Message content or job identifier carried in the queue message.
     */
    private String message;
}