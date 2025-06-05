package com.jfd.worker.mgmt.controller;

import com.jfd.worker.mgmt.model.registration.WorkerInformation;
import com.jfd.worker.mgmt.service.WorkerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for handling worker registration requests.
 */
@Tag(description = "worker-mgmt", name = "Worker Management")
@Slf4j
@RestController // Marks this class as a REST controller
@RequestMapping("/api/worker") // Base path for all endpoints in this controller
@AllArgsConstructor
public class WorkerController {
    private final WorkerService workerService;

    /**
     * Handles POST requests for worker registration.
     *
     * @param workerInformation The WorkerInformation object received in the request body.
     * @return A ResponseEntity containing the saved WorkerInformation and HTTP status 201 Created.
     */
    @PutMapping("/registration") // Maps POST requests to /api/registration
    public ResponseEntity<WorkerInformation> registerWorker(@Valid @RequestBody WorkerInformation workerInformation) {
        log.info("Received worker registration request for ID: {}", workerInformation.getId());
        WorkerInformation savedWorker = workerService.registerWorker(workerInformation);
        return new ResponseEntity<>(savedWorker, HttpStatus.CREATED); // Return 201 Created status
    }

    /**
     * method to get worker information by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkerInformation> getWorkerById(@PathVariable("id") Integer id) {
        log.info("Fetching worker information for ID: {}", id);
        WorkerInformation worker = workerService.getWorkerById(id);
        if (worker != null) {
            return new ResponseEntity<>(worker, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * method to get all worker information.
     */
    @GetMapping("/all") // Maps GET requests to /api/worker/all
    public ResponseEntity<List<WorkerInformation>> getAllWorkers() {
        log.info("Fetching all worker information.");
        List<WorkerInformation> workers = workerService.getAllWorkers();
        return new ResponseEntity<>(workers, HttpStatus.OK);
    }

    /**
     * update worker information by ID.
     * @param id The ID of the worker to update.
     * @param workerInformation The updated WorkerInformation object.
     * @return A ResponseEntity containing the updated WorkerInformation and HTTP status 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WorkerInformation> updateWorkerById(
            @PathVariable("id") Integer id,
            @Valid @RequestBody WorkerInformation workerInformation) {
        log.info("Updating worker information for ID: {}", id);
        WorkerInformation updatedWorker = workerService.updateWorkerById(id, workerInformation);
        if (updatedWorker != null) {
            return new ResponseEntity<>(updatedWorker, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    /**
     * Exception handler for validation errors (e.g., @Valid annotations).
     * Returns a map of field errors with HTTP status 400 Bad Request.
     * @param ex The MethodArgumentNotValidException thrown during validation.
     * @return A map containing field names and their error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        log.error("Validation errors: {}", errors);
        return errors;
    }

    /**
     * General exception handler for any unhandled runtime exceptions.
     * Returns a generic error message with HTTP status 500 Internal Server Error.
     * @param ex The RuntimeException that occurred.
     * @return A map containing a generic error message.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "An internal server error occurred: " + ex.getMessage());
        log.error("Internal server error: {}", ex.getMessage(), ex);
        return error;
    }
}
