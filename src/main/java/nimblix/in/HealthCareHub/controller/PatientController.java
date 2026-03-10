package nimblix.in.HealthCareHub.controller;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.constants.HealthCareConstants;
import nimblix.in.HealthCareHub.response.ApiResponse;
import nimblix.in.HealthCareHub.model.Prescription;
import nimblix.in.HealthCareHub.model.PrescriptionMedicines;
import nimblix.in.HealthCareHub.model.Review;
import nimblix.in.HealthCareHub.request.AdmitPatientRequest;
import nimblix.in.HealthCareHub.request.PatientRegistrationRequest;
import nimblix.in.HealthCareHub.response.*;
import nimblix.in.HealthCareHub.service.AdmissionService;
import nimblix.in.HealthCareHub.service.LabResultService;
import nimblix.in.HealthCareHub.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {
    private final AdmissionService admissionService;
    private final LabResultService labResultService;
    private final PatientService patientService;

    //This API is used to register a new patient in the system.It accepts patient details such as name, email, gender, and password.
    // creates a user account with the PATIENT role, and stores the patient information in the database.
    //On successful registration, it returns a 201 status with a success response.
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerPatient(
            @RequestBody PatientRegistrationRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            patientService.registerPatient(request);
            Map<String, Object> data = new HashMap<>();
            data.put("success", true);

            response.put(HealthCareConstants.STATUS, HttpStatus.CREATED.value());
            response.put(HealthCareConstants.MESSAGE,
                    HealthCareConstants.PATIENT_REGISTERED_SUCCESSFULLY);
            response.put(HealthCareConstants.DATA, data);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put(HealthCareConstants.STATUS, HttpStatus.BAD_REQUEST.value());
            response.put(HealthCareConstants.MESSAGE, "Patient registration failed");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get/prescriptions/{id}")
    public PrescriptionResponse<Prescription> getPrescription(@PathVariable Long id){
        return patientService.getPrescription(id);
    }

    @GetMapping("/get/prescriptionmedicine/{prescriptionId}")
    public PrescriptionMedicineResponse<PrescriptionMedicines> getPrescriptionMedicine(@PathVariable Long prescriptionId){
        return patientService.getPrescriptionMedicines(prescriptionId);
    }

    // Task 175 – Admission Endpoints
    // POST api/patient/admissions/admit
    @PostMapping("/admissions/admit")
    public ResponseEntity<Map<String, Object>> admitPatient(
            @RequestBody AdmitPatientRequest request) {

        AdmitPatientResponse data = admissionService.admitPatient(request);

        if (data == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", HttpStatus.NOT_FOUND.value());
            error.put("message", "Patient or Doctor not found");

            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", "Patient admitted successfully");
        response.put("data", data);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Task 186 – Lab Result Endpoint

    // GET api/patient/lab-results/patient/{patientId}
    @GetMapping("/lab-results/patient/{patientId}")
    public ResponseEntity<Map<String, Object>> getLabResultsByPatient(
            @PathVariable Long patientId) {

        List<LabResultResponse> data = labResultService.getLabResultsByPatient(patientId);

        if (data == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", HttpStatus.NOT_FOUND.value());
            error.put("message", "Patient not found with id: " + patientId);

            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Lab results fetched successfully");
        response.put("count", data.size());
        response.put("data", data);

        return new ResponseEntity<>(response, HttpStatus.OK);
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
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody PatientRegistrationRequest request) {

        ApiResponse response = patientService.forgotPassword(request.getPhoneNumber(), request.getEmail());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody PatientRegistrationRequest request) {

        ApiResponse response = patientService.resetPassword(
                request.getPhoneNumber(),
                request.getEmail(),
                request.getPassword()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
