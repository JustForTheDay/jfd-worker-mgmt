package com.jfd.worker.mgmt.service;

import com.jfd.worker.mgmt.model.config.DatabaseSequence;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.FindAndModifyOptions;

import java.util.Objects;

/**
 * Service to generate auto-incrementing integer sequences for MongoDB documents.
 */
@Service
@AllArgsConstructor
public class SequenceGeneratorService {

    private final MongoOperations mongoOperations;

    /**
     * Generates the next sequence number for a given sequence name.
     * This operation is atomic (thread-safe).
     *
     * @param seqName The name of the sequence (e.g., "workers_sequence").
     * @return The next sequence number.
     */
    public int generateSequence(String seqName) {
        Query query = new Query(Criteria.where("id").is(seqName));
        Update update = new Update().inc("seq", 1); // Increment the 'seq' field by 1

        // Find the document and increment the sequence. If not found, insert a new one.
        DatabaseSequence counter = mongoOperations.findAndModify(query,
                update, FindAndModifyOptions.options().returnNew(true).upsert(true),
                DatabaseSequence.class);

        // Return the sequence value, defaulting to 1 if it was just created
        return Objects.requireNonNull(counter).getSeq() > 0 ? (int) counter.getSeq() : 1;
    }
}

