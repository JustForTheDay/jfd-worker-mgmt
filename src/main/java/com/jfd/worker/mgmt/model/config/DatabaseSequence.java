package com.jfd.worker.mgmt.model.config;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model to represent a sequence counter in MongoDB for auto-generating IDs.
 */
@Data
@Document(collection = "database_sequences")
public class DatabaseSequence {

    @Id // This ID will be the name of the sequence (e.g., "workers_sequence")
    private String id;
    private long seq; // The actual sequence value

}
