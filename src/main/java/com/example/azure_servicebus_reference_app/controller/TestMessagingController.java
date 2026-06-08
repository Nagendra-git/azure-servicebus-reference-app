package com.example.azure_servicebus_reference_app.controller;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.example.azure_servicebus_reference_app.dto.AiQueueMessageDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

/**
 * REST controller for testing Azure Service Bus message publishing.
 *
 * <p>This controller exposes endpoints that publish sample messages to
 * the configured Azure Service Bus queue. It is primarily intended for
 * testing and validating Service Bus integration.</p>
 *  @author Nagendra
 *  @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class TestMessagingController {

    /**
     * Azure Service Bus sender client used to publish messages.
     */
    private final ServiceBusSenderClient serviceBusSenderClient;

    /**
     * Object mapper used to serialize message payloads.
     */
    private final ObjectMapper objectMapper;

    @Value("${servicebus.queues.ai-organization}")
    private String aiOrganizationQueueName;

    /**
     * Publishes a sample message to the configured Azure Service Bus queue.
     *
     * <p>This endpoint creates a test message with a predefined job ID,
     * serializes it to JSON, and sends it to Azure Service Bus.</p>
     *
     * <p>Endpoint:
     * <pre>
     * GET /api/perform-message
     * </pre>
     * </p>
     */
    @GetMapping("/perform-message")
    public void performMessage() {
        String jobId = "job-1";
        AiQueueMessageDto aiQueueMessageDto = new AiQueueMessageDto();
        aiQueueMessageDto.setMessage(jobId);

        try {
            String payload = objectMapper.writeValueAsString(aiQueueMessageDto);

            ServiceBusMessage message = new ServiceBusMessage(payload)
                    .setContentType("application/json")
                    .setMessageId(jobId);

            serviceBusSenderClient.sendMessage(message);

            log.info("Message sent to Service Bus queue for jobId: {}", jobId);

        } catch (Exception e) {
            log.error("Failed to send message to queue: {}", jobId, e);
        }
    }
}