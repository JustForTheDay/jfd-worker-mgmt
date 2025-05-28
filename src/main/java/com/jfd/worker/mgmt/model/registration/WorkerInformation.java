package com.jfd.worker.mgmt.model.registration;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represents the WorkerInformation entity, mapped to a MongoDB document.
 * Includes validation annotations for incoming REST requests.
 */
@Data
@ToString
@Document(collection = "workers") // Specifies the MongoDB collection name
public class WorkerInformation {

    @Id // Marks this field as the primary key in MongoDB
    //@NotNull(message = "ID cannot be null")
    //@Min(value = 1, message = "ID must be a positive integer")
    private Integer id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Min(value = 18, message = "Age must be at least 18")
    private int age;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull(message = "EKYC ID type cannot be null")
    private EkycId ekycId;

    @NotBlank(message = "EKYC data cannot be blank")
    private String ekycData;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotNull(message = "Latitude cannot be null")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    @NotNull(message = "Status cannot be null")
    private WorkerStatus status;

    //@NotNull(message = "Timestamp cannot be null")
    //@ReadOnlyProperty
    private final LocalDateTime timestamp = LocalDateTime.now(); // Automatically set to current time if not provided

}
