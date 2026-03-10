package nimblix.in.HealthCareHub.controller;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.constants.HealthCareConstants;
import nimblix.in.HealthCareHub.model.Prescription;
import nimblix.in.HealthCareHub.model.PrescriptionMedicines;
import nimblix.in.HealthCareHub.model.Review;
import nimblix.in.HealthCareHub.request.AdmitPatientRequest;
import nimblix.in.HealthCareHub.request.PatientRegistrationRequest;
import nimblix.in.HealthCareHub.response.AdmitPatientResponse;
import nimblix.in.HealthCareHub.response.LabResultResponse;
import nimblix.in.HealthCareHub.response.PrescriptionMedicineResponse;
import nimblix.in.HealthCareHub.response.PrescriptionResponse;
import nimblix.in.HealthCareHub.service.AdmissionService;
import nimblix.in.HealthCareHub.service.LabResultService;
import nimblix.in.HealthCareHub.service.PatientService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final AdmissionService admissionService;
    private final LabResultService labResultService;
    private final PatientService patientService;

    // Register Patient
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

    // Get Prescription Medicines
    @GetMapping("/get/prescriptionmedicine/{prescriptionId}")
    public PrescriptionMedicineResponse<PrescriptionMedicines> getPrescriptionMedicine(
            @PathVariable Long prescriptionId){
        return patientService.getPrescriptionMedicines(prescriptionId);
    }

    // Admit Patient
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

    // Get Lab Results
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
            @RequestBody Review request
    ) {

        Review data = patientService.addDoctorReview(
                patientId,
                doctorId,
                request.getComment(),
                request.getRating()
        );

        Map<String,Object> response = new HashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", "Doctor review added successfully");
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


    @PostMapping("/{patientId}/hospitals/{hospitalId}/review")
    public ResponseEntity<Map<String,Object>> addHospitalReview(
            @PathVariable Long patientId,
            @PathVariable Long hospitalId,
            @RequestBody Review request
    ) {

        Review data = patientService.addHospitalReview(
                patientId,
                hospitalId,
                request.getComment(),
                request.getRating()
        );

        Map<String,Object> response = new HashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", "Hospital review added successfully");
        response.put("data", data);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
