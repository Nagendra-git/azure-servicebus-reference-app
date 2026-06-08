package com.example.azure_servicebus_reference_app.config;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import com.example.azure_servicebus_reference_app.listener.QueueListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Azure Service Bus.
 *
 * <p>This configuration creates and manages the Service Bus sender and
 * processor clients used for publishing and consuming messages from
 * Azure Service Bus queues.</p>
 *
 * <p>The processor client is started automatically during application
 * initialization when the listener is enabled.</p>
 *  @author Nagendra
 *  @since 1.0
 */
@Configuration
@Log4j2
public class ServiceBusConfig {

    @Value("${servicebus.connection-string}")
    private String connectionString;

    @Value("${servicebus.queues.ai-organization}")
    private String aiOrganizationQueueName;

    @Value("${servicebus.listener.max-concurrent-calls}")
    private Integer maxConcurrentCalls;

    /**
     * Creates a singleton {@link ServiceBusSenderClient} bean for sending
     * messages to the configured Azure Service Bus queue.
     *
     * @return configured {@link ServiceBusSenderClient}
     */
    @Bean
    public ServiceBusSenderClient serviceBusSenderClient() {
        return new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .queueName(aiOrganizationQueueName)
                .buildClient();
    }

    /**
     * Creates and starts a {@link ServiceBusProcessorClient} for consuming
     * messages from the configured Azure Service Bus queue.
     *
     * <p>The processor operates in {@code PEEK_LOCK} mode with manual
     * message settlement. Messages are completed only after successful
     * processing.</p>
     *
     * @param queueListener listener responsible for processing received messages
     * @return configured and started {@link ServiceBusProcessorClient}
     */
    @Bean
    @ConditionalOnProperty(name = "servicebus.listener.enabled", havingValue = "true")
    public ServiceBusProcessorClient serviceBusProcessorClient(QueueListener queueListener) {

        ServiceBusProcessorClient processor = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .processor()
                .queueName(aiOrganizationQueueName)
                .receiveMode(ServiceBusReceiveMode.PEEK_LOCK)
                .maxConcurrentCalls(maxConcurrentCalls)
                .disableAutoComplete()
                .processMessage(context -> {
                    queueListener.prepareMessageFromAi(context.getMessage());
                    context.complete();
                })
                .processError(this::handleError)
                .buildProcessorClient();

        processor.start();

        log.info("ServiceBus processor started for queue: {}", aiOrganizationQueueName);

        return processor;
    }

    /**
     * Handles exceptions raised during message processing or communication
     * with Azure Service Bus.
     *
     * @param context error context containing entity and exception details
     */
    private void handleError(ServiceBusErrorContext context) {
        log.error("Error on entity {}: {}", context.getEntityPath(),
                context.getException().getMessage());
    }
}