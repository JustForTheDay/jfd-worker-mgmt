package com.jfd.worker.mgmt.controller;

import com.jfd.worker.mgmt.model.registration.WorkerInformation;
import com.jfd.worker.mgmt.service.WorkerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for handling worker registration requests.
 */
@Slf4j
@RestController // Marks this class as a REST controller
@RequestMapping("/api") // Base path for all endpoints in this controller
@AllArgsConstructor
public class WorkerController {
    private final WorkerService workerService;

    /**
     * Handles POST requests for worker registration.
     *
     * @param workerInformation The WorkerInformation object received in the request body.
     * @return A ResponseEntity containing the saved WorkerInformation and HTTP status 201 Created.
     */
    @PostMapping("/registration") // Maps POST requests to /api/registration
    public ResponseEntity<WorkerInformation> registerWorker(@Valid @RequestBody WorkerInformation workerInformation) {
        log.info("Received worker registration request for ID: {}", workerInformation.getId());
        WorkerInformation savedWorker = workerService.registerWorker(workerInformation);
        return new ResponseEntity<>(savedWorker, HttpStatus.CREATED); // Return 201 Created status
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
