package com.jfd.worker.mgmt.repository;

import com.jfd.worker.mgmt.model.registration.WorkerInformation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerInformationRepository extends MongoRepository<WorkerInformation,Integer> {
    // Custom query methods can be added here if needed
}
