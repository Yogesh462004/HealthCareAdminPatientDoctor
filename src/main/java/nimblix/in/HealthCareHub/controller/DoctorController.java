package nimblix.in.HealthCareHub.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.constants.HealthCareConstants;
import nimblix.in.HealthCareHub.model.Doctor;
import nimblix.in.HealthCareHub.request.DoctorAddRequest;
import nimblix.in.HealthCareHub.request.DoctorRegistrationRequest;
import nimblix.in.HealthCareHub.request.DoctorScheduleRequest;
import nimblix.in.HealthCareHub.response.DoctorSearchResponse;
import nimblix.in.HealthCareHub.response.*;
import nimblix.in.HealthCareHub.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping("/register")
    public String registerDoctor(@RequestBody DoctorRegistrationRequest request) {
        return doctorService.registerDoctor(request);
    }

    @GetMapping("/hospitals/{hospitalId}/doctors")
    public ResponseEntity<ApiResponse<DoctorListResponse>> getDoctorsByHospital(@PathVariable Long hospitalId) {
        DoctorListResponse response = doctorService.getDoctorsByHospitalId(hospitalId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Doctors fetched successfully", response));

    // Add a new doctor under hospital
    @PostMapping("/addDoctor")
    public ResponseEntity<ApiResponse<DoctorProfileResponse>> addDoctor(
            @RequestBody DoctorAddRequest request){

        ApiResponse<DoctorProfileResponse> response =
                doctorService.addDoctor(request);

        return ResponseEntity.ok(response);
    }



    @GetMapping("/getDoctorDetails/{doctorId}/{hospitalId}")
    public ResponseEntity<?> getDoctorDetails(@PathVariable Long doctorId,
                                              @PathVariable Long hospitalId) {
        return doctorService.getDoctorDetails(doctorId, hospitalId);
    }

    @PutMapping("/updateDoctorDetails")
    public String updateDoctorDetails(@RequestBody DoctorRegistrationRequest request) {
        return doctorService.updateDoctorDetails(request);
    }

    @GetMapping("/{doctorId}/profile")
    public ResponseEntity<ApiResponse<DoctorProfileResponse>> getDoctorProfile(
            @PathVariable Long doctorId) {
        DoctorProfileResponse response = doctorService.getDoctorProfile(doctorId);
        return ResponseEntity.ok(new ApiResponse<>("200", HealthCareConstants.DOCTOR_PROFILE_FETCHED_SUCCESSFULLY, response));
    }

    @DeleteMapping("/deleteDoctorDetails")
    public String deleteDoctorDetails(@RequestParam Long doctorId) {
        return doctorService.deleteDoctorDetails(doctorId);
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<Object>> getDoctorById(@PathVariable Long doctorId) {
        Object doctor = doctorService.getDoctorById(doctorId);
        return ResponseEntity.ok(new ApiResponse<>("200", HealthCareConstants.DOCTOR_FETCHED_SUCCESSFULLY, doctor));
    }


    @GetMapping("/{doctorId}/reviews")
    public ResponseEntity<ApiResponse<DoctorReviewResponse>> getDoctorReviews(
            @PathVariable Long doctorId) {
        if (doctorId <= 0) {
            throw new IllegalArgumentException("Doctor id cannot be 0 or negative");
        }
        DoctorReviewResponse result = doctorService.getDoctorReviews(doctorId);
        return ResponseEntity.ok(new ApiResponse<>("200", HealthCareConstants.SUCCESSFULLY, result));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        return ResponseEntity.ok(doctorService.getAllRoles());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchDoctor(@RequestParam String name) {
   /* @GetMapping("/search")
    public ResponseEntity<?> searchDoctor(@RequestParam String name){
        return ResponseEntity.ok(doctorService.searchDoctorByName(name));
    } */

    // Filter doctors by specialization
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Object>> filterDoctorsBySpecialization(
            @RequestParam(required = false) String specialization) {

        if (specialization == null || specialization.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("400", "Specialization parameter is required", null));
        }

        List<Doctor> doctors = doctorService.filterDoctorsBySpecialization(specialization);

        if (doctors == null || doctors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("404", "No doctors found with specialization: " + specialization, null));
        }

        return ResponseEntity.ok(new ApiResponse<>("200", HealthCareConstants.DOCTORS_FEATCHED_SUCCESSFULLY, doctors));
    public ResponseEntity<ApiResponse<List<DoctorFilterResponse>>> filterDoctorsBySpecialization(
            @RequestParam String specialization){

        ApiResponse<List<DoctorFilterResponse>> response =
                doctorService.filterDoctorsBySpecialization(specialization);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/availability")
    public ResponseEntity<ApiResponse<Object>> setDoctorAvailability(
            @RequestBody DoctorRegistrationRequest request) {
        String message = doctorService.setDoctorAvailability(request);
        return ResponseEntity.ok(new ApiResponse<>("200", message, null));
    }
    @GetMapping("/availability")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability() {

        List<DoctorAvailabilityResponse> availabilityList =
                doctorService.getAllAvailableDoctors();

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("status", HttpStatus.OK.value());
        response.put("message", "Doctor availability fetched successfully");
        response.put("count", availabilityList.size());
        response.put("data", availabilityList);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{doctorId}/availability")
    public ResponseEntity<ApiResponse<Object>> getDoctorAvailability(
            @PathVariable Long doctorId) {
        List<Map<String, Object>> slots = doctorService.getDoctorAvailability(doctorId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Doctor availability fetched successfully", slots));
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<ApiResponse<DoctorListResponse>> getDoctorsByHospitalId(
            @PathVariable Long hospitalId) {
        DoctorListResponse doctors = doctorService.getDoctorsByHospitalId(hospitalId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Doctors fetched successfully", doctors));
    }

    // Retrieve doctors by hospital
    // Get doctors by hospital
    @GetMapping("/hospitals/{id}/doctors")
    public ResponseEntity<ApiResponse<List<DoctorSummaryResponse>>>
    getDoctorsByHospital(@PathVariable Long id) {

        ApiResponse<List<DoctorSummaryResponse>> response =
                doctorService.getDoctorsByHospitalId(id);

        return ResponseEntity.ok(response);
    }


    // setting that doctor status put/api/doctors/{doctorId}/status?status=IN_OPERATION
    @PutMapping("/{doctorId}/status")
    public ResponseEntity<ApiResponse<Object>> updateDoctorStatus(
            @PathVariable Long doctorId,
            @RequestParam String status) {
        String message = doctorService.updateDoctorStatus(doctorId, status);
        return ResponseEntity.ok(new ApiResponse<>("200", message, null));
    }

    @GetMapping("/{doctorId}/status")
    public ResponseEntity<ApiResponse<DoctorStatusResponse>> getDoctorStatus(
            @PathVariable Long doctorId) {
        DoctorStatusResponse response = doctorService.getDoctorStatus(doctorId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Doctor status fetched successfully", response));
    }

    @PostMapping("/{doctorId}/schedule")
    public ResponseEntity<ApiResponse<DoctorScheduleResponse>> createSchedule(
            @PathVariable Long doctorId,
            @RequestBody DoctorScheduleRequest request) {
        if (doctorId <= 0) {
            throw new IllegalArgumentException("Doctor id cannot be 0 or negative");
        }
        if (request.getPatientId() == null || request.getPatientId() <= 0) {
            throw new IllegalArgumentException("Patient id cannot be null, 0 or negative");
        }
        DoctorScheduleResponse response = doctorService.createDoctorSchedule(doctorId, request);
        return ResponseEntity.ok(new ApiResponse<>("200", HealthCareConstants.DOCTORSCHEDULESUCCESSFULLY, response));
    }

    @GetMapping("/{doctorId}/schedule")
    public ResponseEntity<ApiResponse<List<DoctorScheduleResponse>>> getSchedules(@PathVariable Long doctorId) {
        if (doctorId <= 0) {
            throw new IllegalArgumentException("Doctor id cannot be 0 or negative");
        }
        List<DoctorScheduleResponse> schedules = doctorService.getDoctorSchedules(doctorId);
        return ResponseEntity.ok(new ApiResponse<>("200", HealthCareConstants.DOCTORSCHEDULESUCCESSFULLY, schedules));
    }

    @PutMapping("/schedule/{scheduleId}/status")
    public ResponseEntity<ApiResponse<DoctorScheduleResponse>> updateScheduleStatus(
            @PathVariable Long scheduleId,
            @RequestParam String status) {
        if (scheduleId <= 0) {
            throw new IllegalArgumentException("Schedule id cannot be 0 or negative");
        }
        DoctorScheduleResponse response = doctorService.updateDoctorScheduleStatus(scheduleId, status);
        return ResponseEntity.ok(new ApiResponse<>("200", HealthCareConstants.SCHEDULESTATUSUPDATED, response));
    }

    @PatchMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<Object>> updateDoctor(
            @PathVariable Long doctorId,
            @RequestBody Map<String, Object> updates) {
        if (doctorId <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("400", "Doctor ID must be a positive number", null));
        }
        Object updatedDoctor = doctorService.updateDoctor(doctorId, updates);
        return ResponseEntity.ok(new ApiResponse<>("200", "Doctor updated successfully", updatedDoctor));
    }
    // Search Doctors by Name
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DoctorSearchResponse>>> searchDoctors(
            @RequestParam String name){

        ApiResponse<List<DoctorSearchResponse>> response =
                doctorService.searchDoctors(name);

        return ResponseEntity.ok(response);
    }

}