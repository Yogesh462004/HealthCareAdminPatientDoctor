package nimblix.in.HealthCareHub.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.constants.HealthCareConstants;
import nimblix.in.HealthCareHub.model.Doctor;
import nimblix.in.HealthCareHub.request.DoctorRegistrationRequest;
import nimblix.in.HealthCareHub.request.DoctorScheduleRequest;
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

//    // Add a new doctor under hospital
//    @PostMapping("/addDoctor")
//    public ResponseEntity<DoctorProfileResponse> addDoctor(
//            @Valid @RequestBody DoctorRegistrationRequest request) {
//
//        DoctorProfileResponse response = doctorService.addDoctor(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

    //Retrieve the list of doctors associated with a specific hospital.
    @GetMapping("/hospitals/{hospitalId}/doctors")
    public ResponseEntity<DoctorListResponse> getDoctorsByHospital(@PathVariable Long hospitalId) {

        DoctorListResponse response = doctorService.getDoctorsByHospitalId(hospitalId);
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

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Doctor>>> searchDoctor(@RequestParam String name) {

        List<Doctor> doctors = doctorService.searchDoctorByName(name);

        if (doctors == null || doctors.isEmpty()) {

            ApiResponse<List<Doctor>> response =
                    new ApiResponse<>("FAILURE", "No doctors found", null);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        else {

            ApiResponse<List<Doctor>> response =
                    new ApiResponse<>("SUCCESS", "Doctors fetched successfully", doctors);

            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterDoctorsBySpecialization(
            @RequestParam(required = false) String specialization) {

        if (specialization == null || specialization.trim().isEmpty()) {

            Map<String, Object> error = new HashMap<>();
            error.put(HealthCareConstants.STATUS, HttpStatus.BAD_REQUEST.value());
            error.put(HealthCareConstants.MESSAGE, "Specialization parameter is required");

            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        List<Doctor> doctors =
                doctorService.filterDoctorsBySpecialization(specialization);

        if (doctors == null || doctors.isEmpty()) {

            Map<String, Object> error = new HashMap<>();
            error.put(HealthCareConstants.STATUS, HttpStatus.NOT_FOUND.value());
            error.put(HealthCareConstants.MESSAGE, "No doctors found with specialization: " + specialization);

            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        Map<String, Object> response = new HashMap<>();
        response.put(HealthCareConstants.STATUS, HttpStatus.OK.value());
        response.put(HealthCareConstants.MESSAGE, HealthCareConstants.DOCTORS_FEATCHED_SUCCESSFULLY);
        response.put(HealthCareConstants.COUNT, doctors.size());
        response.put(HealthCareConstants.DATA, doctors);

        return new ResponseEntity<>(response, HttpStatus.OK);
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

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<Map<String, Object>> getDoctorsByHospitalId(
            @PathVariable Long hospitalId) {
        DoctorListResponse doctors = doctorService.getDoctorsByHospitalId(hospitalId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(HealthCareConstants.STATUS, HttpStatus.OK.value());
        result.put(HealthCareConstants.MESSAGE, "Doctors fetched successfully");
        result.put(HealthCareConstants.DATA, doctors);
        return ResponseEntity.ok(result);
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

//    @PostMapping("/{doctorId}/schedule")
//    public ResponseEntity<Map<String,Object>> createSchedule(
//            @PathVariable Long doctorId,
//            @RequestBody DoctorScheduleRequest request) {
//        if (doctorId <= 0) {
//            throw new IllegalArgumentException("Doctor id cannot be 0 or negative");
//        }
//        if (request.getPatientId() == null || request.getPatientId() <= 0) {
//            throw new IllegalArgumentException("Patient id cannot be null, 0 or negative");
//        }
//
//        DoctorScheduleResponse response = doctorService.createDoctorSchedule(doctorId, request);
//
//        Map<String,Object> result = new LinkedHashMap<>();
//        result.put(HealthCareConstants.STATUS, HttpStatus.OK.value());
//        result.put(HealthCareConstants.MESSAGE, HealthCareConstants.DOCTORSCHEDULESUCCESSFULLY);
//        result.put(HealthCareConstants.DATA, response);
//        return ResponseEntity.ok(result);
//    }


    @PostMapping("/{doctorId}/schedule")
    public ResponseEntity<ApiResponse<DoctorScheduleResponse>> createDoctorSchedule(
            @PathVariable Long doctorId,
            @RequestBody DoctorScheduleRequest request) {

        ApiResponse<DoctorScheduleResponse> response = new ApiResponse<>();

        if (doctorId <= 0) {
            response.setStatus("FAILURE");
            response.setMessage("Doctor id cannot be 0 or negative");
            response.setData(null);
            return ResponseEntity.badRequest().body(response);
        }

        DoctorScheduleResponse schedule = doctorService.createDoctorSchedule(doctorId, request);

        if (schedule == null) {
            response.setStatus("FAILURE");
            response.setMessage("Schedule could not be created");
            response.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setStatus("SUCCESS");
        response.setMessage("Doctor schedule created successfully");
        response.setData(schedule);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{doctorId}/schedules")
    public ResponseEntity<ApiResponse<List<DoctorScheduleResponse>>> getSchedules(@PathVariable Long doctorId) {

        ApiResponse<List<DoctorScheduleResponse>> response = new ApiResponse<>();

        if (doctorId <= 0) {
            response.setStatus("FAILURE");
            response.setMessage("Doctor id cannot be 0 or negative");
            response.setData(null);
            return ResponseEntity.badRequest().body(response);
        }

        List<DoctorScheduleResponse> schedules = doctorService.getDoctorSchedules(doctorId);

        if (schedules == null || schedules.isEmpty()) {
            response.setStatus("FAILURE");
            response.setMessage("No schedules found");
            response.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setStatus("SUCCESS");
        response.setMessage("Doctor schedules fetched successfully");
        response.setData(schedules);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/schedule/{scheduleId}/status")
    public ResponseEntity<ApiResponse<DoctorScheduleResponse>> updateScheduleStatus(
            @PathVariable Long scheduleId,
            @RequestParam String status) {

        ApiResponse<DoctorScheduleResponse> response = new ApiResponse<>();

        if (scheduleId <= 0) {
            response.setStatus("FAILURE");
            response.setMessage("Schedule id cannot be 0 or negative");
            response.setData(null);
            return ResponseEntity.badRequest().body(response);
        }

        DoctorScheduleResponse schedule = doctorService.updateDoctorScheduleStatus(scheduleId, status);

        if (schedule == null) {
            response.setStatus("FAILURE");
            response.setMessage("Schedule not found or update failed");
            response.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setStatus("SUCCESS");
        response.setMessage("Schedule status updated successfully");
        response.setData(schedule);

        return ResponseEntity.ok(response);
    }

}