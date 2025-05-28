package com.jfd.worker.mgmt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jfd.worker.mgmt.model.registration.WorkerInformation;
import com.jfd.worker.mgmt.repository.WorkerInformationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
/**
 * Service layer for handling WorkerInformation operations.
 * Manages persistence to MongoDB and publishing to Kafka.
 */
@Slf4j
@Service
public class WorkerService {
    private final WorkerInformationRepository workerInformationRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final SequenceGeneratorService sequenceGeneratorService; // Inject the sequence generator

    @Value("${app.kafka.topic.worker.registration}") // Injects the Kafka topic name from properties
    private String kafkaTopic;

    /**
     * Constructor for WorkerService.
     * @param workerInformationRepository Repository for MongoDB operations.
     * @param kafkaTemplate Kafka template for sending messages.
     */
    public WorkerService(WorkerInformationRepository workerInformationRepository, KafkaTemplate<String, String> kafkaTemplate,SequenceGeneratorService sequenceGeneratorService) {
        this.workerInformationRepository = workerInformationRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.sequenceGeneratorService = sequenceGeneratorService; // Initialize the sequence generator
        this.objectMapper = new ObjectMapper();
        // Register JavaTimeModule to correctly serialize LocalDateTime objects
        this.objectMapper.registerModule(new JavaTimeModule());
        // Disable WRITE_DATES_AS_TIMESTAMPS to serialize dates as ISO-8601 strings
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    /**
     * Registers a new worker by saving their information to MongoDB
     * and then publishing the saved data to Kafka.
     *
     * @param workerInformation The worker information to register.
     * @return The saved WorkerInformation object.
     * @throws RuntimeException if there's an error during MongoDB save or Kafka publishing.
     */
    public WorkerInformation registerWorker(WorkerInformation workerInformation) {
        log.info("Attempting to register worker (ID will be auto-generated as Integer).");

        // Generate the next sequence ID
        workerInformation.setId(sequenceGeneratorService.generateSequence("workers_sequence"));
        log.debug("Generated ID for worker: {}", workerInformation.getId());;

        // 1. Save to MongoDB
        WorkerInformation savedWorker = workerInformationRepository.save(workerInformation);
        log.info("Worker with ID {} saved to MongoDB successfully.", savedWorker.getId());

        // 2. Publish to Kafka
        publishWorkerRegistrationEvent(savedWorker);

        return savedWorker;
    }

    /**
     * Publishes the WorkerInformation object to the configured Kafka topic.
     * The object is serialized to a JSON string.
     *
     * @param workerInformation The worker information to publish.
     */
    @Async
    private void publishWorkerRegistrationEvent(WorkerInformation workerInformation) {
        try {
            String workerJson = objectMapper.writeValueAsString(workerInformation);
            // Send message to Kafka with worker's ID as the key
            kafkaTemplate.send(kafkaTopic, String.valueOf(workerInformation.getId()), workerJson);
            log.info("Worker with ID {} published to Kafka topic '{}' successfully.", workerInformation.getId(), kafkaTopic);
        } catch (JsonProcessingException e) {
            log.error("Error serializing WorkerInformation to JSON for Kafka: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize worker information for Kafka", e);
        } catch (Exception e) {
            log.error("Error publishing worker with ID {} to Kafka: {}", workerInformation.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish worker information to Kafka", e);
        }
    }
}
