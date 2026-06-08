# Azure Service Bus Reference Application

## Overview

This project demonstrates how to integrate Azure Service Bus with a Spring Boot application for asynchronous message publishing and consumption.

The application includes:

* Message Producer (Publisher)
* Message Consumer (Listener)
* Azure Service Bus Queue Integration
* Manual Message Completion using PEEK_LOCK mode
* Configurable Concurrent Message Processing
* Conditional Listener Enablement

---

## Technology Stack

* Java 21
* Spring Boot 3.x
* Azure Service Bus SDK
* Maven
* Lombok
* Jackson

---

## Project Structure

```text
src/main/java
├── config
│   └── ServiceBusConfig.java
├── controller
│   └── TestMessagingController.java
├── listener
│   └── QueueListener.java
├── dto
│   └── AiQueueMessageDto.java
└── AzureServiceBusReferenceApplication.java
```

---

## Features

### Message Publishing

The application exposes a REST endpoint that publishes messages to an Azure Service Bus queue.

**Endpoint**

```http
GET /api/perform-message
```

**Sample Payload Sent**

```json
{
  "message": "job-1"
}
```

---

### Message Consumption

A listener is configured using `ServiceBusProcessorClient`.

Features:

* Queue-based message consumption
* Manual message acknowledgement
* Concurrent message processing
* Error handling support

---

## Configuration

### application.yml

```yaml
servicebus:
  connection-string: ${SERVICEBUS_CONNECTION_STRING}

  queues:
    ai-organization: ai-organization-queue

  listener:
    enabled: true
    max-concurrent-calls: 5
```

---

## Azure Service Bus Setup

### Create Azure Service Bus Namespace

1. Navigate to Azure Portal.
2. Create a Service Bus Namespace.
3. Create a Queue named:

```text
ai-organization-queue
```

4. Copy the connection string from:

```text
Shared Access Policies
→ RootManageSharedAccessKey
→ Primary Connection String
```

---

## Environment Variable

Set the Service Bus connection string as an environment variable.

### Windows

```powershell
$env:SERVICEBUS_CONNECTION_STRING="<connection-string>"
```

### Linux / Mac

```bash
export SERVICEBUS_CONNECTION_STRING="<connection-string>"
```

---

## Running the Application

### Clone Repository

```bash
git clone <repository-url>
cd azure-servicebus-reference-app
```

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

---

## Message Flow

```text
REST API
    |
    v
ServiceBusSenderClient
    |
    v
Azure Service Bus Queue
    |
    v
ServiceBusProcessorClient
    |
    v
QueueListener
```

---

## Listener Configuration

The listener can be enabled or disabled using:

```yaml
servicebus:
  listener:
    enabled: true
```

Disable listener:

```yaml
servicebus:
  listener:
    enabled: false
```

This is useful when running only the publisher or only the consumer.

---

## Logging

Example publisher log:

```text
Message sent to Service Bus queue for jobId: job-1
```

Example consumer log:

```text
Received Job job-1 from ai service
```

To reduce Azure SDK logs:

```yaml
logging:
  level:
    com.azure: WARN
```

---

## Error Handling

Any exception during message processing is logged and propagated to the Azure Service Bus processor.

```java
catch (Exception e) {
        log.error("Error processing Service Bus message", e);
}
```

Messages remain available for retry based on Azure Service Bus queue configuration.

---

## Future Enhancements

* Dead Letter Queue (DLQ) handling
* Topic and Subscription support
* Retry policies
* Distributed tracing
* Metrics and monitoring
* Message scheduling
* Session-enabled queues
* Generic publisher framework

---

## Reference

Azure Service Bus Documentation:

https://learn.microsoft.com/en-us/azure/service-bus-messaging/

Spring Boot Documentation:

https://spring.io/projects/spring-boot
