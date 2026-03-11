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

import java.util.HashMap;
import java.util.LinkedHashMap;
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
    public ResponseEntity<Map<String, Object>> getDoctorProfile(
            @PathVariable Long doctorId) {

        DoctorProfileResponse response = doctorService.getDoctorProfile(doctorId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put(HealthCareConstants.STATUS, HttpStatus.OK.value());
        result.put(HealthCareConstants.DOCTOR_PROFILE_FETCHED_SUCCESSFULLY, HealthCareConstants.DOCTOR_PROFILE_FETCHED_SUCCESSFULLY);
        result.put(HealthCareConstants.DATA_KEY, response);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/deleteDoctorDetails")
    public String deleteDoctorDetails(@RequestParam Long doctorId) {
        return doctorService.deleteDoctorDetails(doctorId);
    }

    //  - Get Doctor by ID with edge cases
    @GetMapping("/{doctorId}")
    public ResponseEntity<Map<String, Object>> getDoctorById(@PathVariable Long doctorId) {

        DoctorProfileResponse doctor = doctorService.getDoctorById(doctorId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put(HealthCareConstants.STATUS, HttpStatus.OK.value());
        result.put(HealthCareConstants.MESSAGE, HealthCareConstants.DOCTOR_FETCHED_SUCCESSFULLY);
        result.put(HealthCareConstants.DATA_KEY, doctor);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{doctorId}/reviews")
    public ResponseEntity<Map<String, Object>> getDoctorReviews(
            @PathVariable Long doctorId) {

        if (doctorId <= 0) {
            throw new IllegalArgumentException("Doctor id cannot be 0 or negative");
        }

        Map<String, Object> response = new LinkedHashMap<>();

        DoctorReviewResponse result = doctorService.getDoctorReviews(doctorId);

        response.put(HealthCareConstants.STATUS, HttpStatus.OK.value());
        response.put(HealthCareConstants.MESSAGE, HealthCareConstants.SUCCESSFULLY);
        response.put(HealthCareConstants.AVERAGERATING, result.getAverageRating());
        response.put(HealthCareConstants.TOTALREVIEWS, result.getTotalReviews());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {

        return ResponseEntity.ok(doctorService.getAllRoles());
    }

   /* @GetMapping("/search")
    public ResponseEntity<?> searchDoctor(@RequestParam String name){
        return ResponseEntity.ok(doctorService.searchDoctorByName(name));
    } */

    // Filter doctors by specialization
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<DoctorFilterResponse>>> filterDoctorsBySpecialization(
            @RequestParam String specialization){

        ApiResponse<List<DoctorFilterResponse>> response =
                doctorService.filterDoctorsBySpecialization(specialization);

        return ResponseEntity.ok(response);
    }

//    @GetMapping("/availability")
//    public ResponseEntity<List<DoctorAvailabilityResponse>> getDoctorAvailability() {
//
//        List<DoctorAvailabilityResponse> availabilityList =
//                doctorService.getDoctorAvailability();
//
//        return ResponseEntity.ok(availabilityList);
//    }

    @PostMapping("/availability")
    public ResponseEntity<Map<String, Object>> setDoctorAvailability(
            @RequestBody DoctorRegistrationRequest request) {
        String message = doctorService.setDoctorAvailability(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(HealthCareConstants.STATUS, HttpStatus.OK.value());
        result.put(HealthCareConstants.MESSAGE, message);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{doctorId}/availability")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable Long doctorId) {
        List<Map<String, Object>> slots = doctorService.getDoctorAvailability(doctorId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(HealthCareConstants.STATUS, HttpStatus.OK.value());
        result.put(HealthCareConstants.MESSAGE, "Doctor availability fetched successfully");
        result.put(HealthCareConstants.DATA, slots);
        return ResponseEntity.ok(result);
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
    public ResponseEntity<Map<String, Object>> updateDoctorStatus(
            @PathVariable Long doctorId,
            @RequestParam String status) {
        String message = doctorService.updateDoctorStatus(doctorId, status);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(HealthCareConstants.STATUS, HttpStatus.OK.value());
        result.put(HealthCareConstants.MESSAGE, message);
        return ResponseEntity.ok(result);
    }

    /*  getting Doctor Status GET /api/doctors/{doctorId}/status */
    @GetMapping("/{doctorId}/status")
    public ResponseEntity<Map<String, Object>> getDoctorStatus(
            @PathVariable Long doctorId) {
        DoctorStatusResponse response = doctorService.getDoctorStatus(doctorId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(HealthCareConstants.STATUS, HttpStatus.OK.value());
        result.put(HealthCareConstants.MESSAGE, "Doctor status fetched successfully");
        result.put(HealthCareConstants.DATA, response);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{doctorId}/schedule")
    public ResponseEntity<Map<String,Object>> createSchedule(
            @PathVariable Long doctorId,
            @RequestBody DoctorScheduleRequest request) {
        if (doctorId <= 0) {
            throw new IllegalArgumentException("Doctor id cannot be 0 or negative");
        }
        if (request.getPatientId() == null || request.getPatientId() <= 0) {
            throw new IllegalArgumentException("Patient id cannot be null, 0 or negative");
        }

        DoctorScheduleResponse response = doctorService.createDoctorSchedule(doctorId, request);

        Map<String,Object> result = new LinkedHashMap<>();
        result.put(HealthCareConstants.STATUS, HttpStatus.OK.value());
        result.put(HealthCareConstants.MESSAGE, HealthCareConstants.DOCTORSCHEDULESUCCESSFULLY);
        result.put(HealthCareConstants.DATA, response);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{doctorId}/schedule")
    public ResponseEntity<Map<String,Object>> getSchedules(@PathVariable Long doctorId) {
        if (doctorId <= 0) {
            throw new IllegalArgumentException("Doctor id cannot be 0 or negative");
        }

        List<DoctorScheduleResponse> schedules =
                doctorService.getDoctorSchedules(doctorId);

        Map<String,Object> result = new LinkedHashMap<>();
        result.put(HealthCareConstants.STATUS, HttpStatus.OK.value());
        result.put(HealthCareConstants.MESSAGE, HealthCareConstants.DOCTORSCHEDULESUCCESSFULLY);
        result.put(HealthCareConstants.DATA, schedules);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/schedule/{scheduleId}/status")
    public ResponseEntity<Map<String,Object>> updateScheduleStatus(
            @PathVariable Long scheduleId,
            @RequestParam String status) {
        if (scheduleId <= 0) {
            throw new IllegalArgumentException("Schedule id cannot be 0 or negative");
        }

        DoctorScheduleResponse response = doctorService.updateDoctorScheduleStatus(scheduleId, status);

        Map<String,Object> result = new LinkedHashMap<>();
        result.put(HealthCareConstants.STATUS, HttpStatus.OK.value());
        result.put(HealthCareConstants.MESSAGE, HealthCareConstants.SCHEDULESTATUSUPDATED);
        result.put(HealthCareConstants.DATA, response);
        return ResponseEntity.ok(result);
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