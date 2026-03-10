package nimblix.in.HealthCareHub.controller;

import jakarta.validation.Valid;
import nimblix.in.HealthCareHub.model.Patient;
import nimblix.in.HealthCareHub.request.PatientRegistrationRequest;
import nimblix.in.HealthCareHub.response.*;
import nimblix.in.HealthCareHub.service.AdmissionService;
import nimblix.in.HealthCareHub.service.LabResultService;
import nimblix.in.HealthCareHub.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/register")
    public ApiResponse<Patient> registerPatient(@Valid @RequestBody PatientRegistrationRequest request) {

        // Call service to create Patient
        Patient savedPatient = patientService.registerPatient(request);

        // Return ApiResponse with patient data and message
        return new ApiResponse<>("SUCCESS", "Patient registered successfully", savedPatient);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable Long id) {

        boolean isDeleted = patientService.softDeletePatient(id);

        ApiResponse<Void> response = new ApiResponse<>();

        if (isDeleted) {

            response.setStatus("SUCCESS");
            response.setMessage("Patient deleted successfully");
            response.setData(null);

            return ResponseEntity.ok(response);

        } else {

            response.setStatus("FAILURE");
            response.setMessage("Patient not found with id: " + id);
            response.setData(null);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
