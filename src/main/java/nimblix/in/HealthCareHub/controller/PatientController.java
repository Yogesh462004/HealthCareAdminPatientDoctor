package nimblix.in.HealthCareHub.controller;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.constants.HealthCareConstants;
import nimblix.in.HealthCareHub.model.Patient;
import nimblix.in.HealthCareHub.model.Prescription;
import nimblix.in.HealthCareHub.model.PrescriptionMedicines;
import nimblix.in.HealthCareHub.model.Review;
import nimblix.in.HealthCareHub.request.AdmitPatientRequest;
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

    @PostMapping("/{patientId}/doctors/{doctorId}/review")
    public ResponseEntity<Map<String,Object>> addDoctorReview(
           @PathVariable Long patientId,
           @PathVariable Long doctorId,
           @RequestParam String comment,
           @RequestParam int rating
    ) {
       Review data = patientService.addDoctorReview(patientId, doctorId, comment, rating);
       Map<String,Object> response = new HashMap<>();
       response.put("status", HttpStatus.CREATED.value());
       response.put("message", "Patient review added successfully");
       response.put("data", data);
       return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/doctors/{doctorId}/reviews")
    public ResponseEntity<Map<String,Object>> getDoctorReviews(@PathVariable Long doctorId) {
       List<Review> data = patientService.getDoctorReviews(doctorId);
       Map<String,Object> response = new HashMap<>();
       response.put("status", HttpStatus.OK.value());
       response.put("message", "Doctor reviews fetched successfully");
       response.put("count", data.size());
       response.put("data", data);
       return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{patientId}/review-by-doctor/{doctorId}")
    public ResponseEntity<Map<String,Object>> addPatientReview(
           @PathVariable Long patientId,
           @PathVariable Long doctorId,
           @RequestParam String comment,
           @RequestParam int rating
    ) {
       Review data = patientService.addPatientReview(doctorId, patientId, comment, rating);
       Map<String,Object> response = new HashMap<>();
       response.put("status", HttpStatus.CREATED.value());
       response.put("message", "Doctor review added successfully");
       response.put("data", data);
       return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{patientId}/reviews")
    public ResponseEntity<Map<String,Object>> getPatientReviews(@PathVariable Long patientId) {
       List<Review> data = patientService.getPatientReviews(patientId);
       Map<String,Object> response = new HashMap<>();
       response.put("status", HttpStatus.OK.value());
       response.put("message", "Patient reviews fetched successfully");
       response.put("count", data.size());
       response.put("data", data);
       return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<Patient>>> filterPatients(
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        List<Patient> patients;

        if (day != null) {
            patients = patientService.filterPatientsByDay(day);
        }
        else if (month != null) {
            patients = patientService.filterPatientsByMonth(month);
        }
        else if (year != null) {
            patients = patientService.filterPatientsByYear(year);
        }
        else {
            patients = List.of();
        }

        ApiResponse<List<Patient>> response =
                new ApiResponse<>(
                        "200",
                        "Patient records fetched successfully",
                        patients
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
