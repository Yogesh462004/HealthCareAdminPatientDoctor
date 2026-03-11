package nimblix.in.HealthCareHub.controller;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.constants.HealthCareConstants;
import nimblix.in.HealthCareHub.request.HospitalLoginRequest;
import nimblix.in.HealthCareHub.request.HospitalRegistrationRequest;
import nimblix.in.HealthCareHub.request.MedicineAddRequest;
import nimblix.in.HealthCareHub.response.*;
import nimblix.in.HealthCareHub.service.HospitalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hospital")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Long>> registerHospital(@RequestBody HospitalRegistrationRequest request) {
        Long hospitalId = hospitalService.registerHospital(request);

        ApiResponse<Long> response = ApiResponse.<Long>builder()
                .status(HealthCareConstants.STATUS_SUCCESS)
                .message(HealthCareConstants.REGISTER_SUCCESS)
                .data(hospitalId)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Authenticate hospital admin using login credentials.
    @PostMapping("/login")
    public ResponseEntity<HospitalLoginResponse> loginHospital(@RequestBody HospitalLoginRequest request) {
        String token = hospitalService.loginHospital(request);

        HospitalLoginResponse response = HospitalLoginResponse  .builder()
                .status(HealthCareConstants.STATUS_SUCCESS)
                .message("Login successful")
                .token(token)
                .build();

        return ResponseEntity.ok(response);
    }



    @PostMapping("/medicine/add")
    public String addMedicine(@RequestBody MedicineAddRequest request){

        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
        if (request.getHospitalId() == null) {
            throw new IllegalArgumentException("Hospital Id is required");
        }
        if (request.getMedicineName()==null || request.getMedicineName().trim().isEmpty()){
            throw new IllegalArgumentException("medicine name is required");
        }
        return hospitalService.addMedicine(request);
    }

    @PostMapping("/{hospitalId}/rooms")
    public String addRooms(
            @PathVariable Long hospitalId,
            @RequestBody List<HospitalRegistrationRequest.Room> rooms) {

        hospitalService.addRooms(hospitalId, rooms);
        return "Rooms added successfully";
    }

    @GetMapping("/{hospitalId}/available-rooms")
    public List<RoomResponse> getAvailableRooms(
            @PathVariable Long hospitalId) {

        return hospitalService.getAvailableRooms(hospitalId);
    }



    // Fetch detailed information about a specific hospital
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HospitalDetailResponse>> getHospitalById(
            @PathVariable Long id) {

        ApiResponse<HospitalDetailResponse> response =
                hospitalService.getHospitalById(id);

        return ResponseEntity.ok(response);
    }

    // Search hospitals based on hospital name
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<HospitalSearchResponse>>> searchHospital(
            @RequestParam("name") String name) {

        ApiResponse<List<HospitalSearchResponse>> response =
                hospitalService.searchHospitalByName(name);

        return ResponseEntity.ok(response);
    }

    // Retrieve hospital statistics
    @GetMapping("/{hospitalId}/stats")
    public ResponseEntity<ApiResponse<HospitalStatsResponse>> getHospitalStats(
            @PathVariable Long hospitalId) {

        ApiResponse<HospitalStatsResponse> response =
                hospitalService.getHospitalStats(hospitalId);

        return ResponseEntity.ok(response);
    }


    // Sort hospitals based on rating, name, or doctor count
    @GetMapping("/sort")
    public ResponseEntity<ApiResponse<List<HospitalSummaryResponse>>> sortHospitals(
            @RequestParam String sortBy) {

        ApiResponse<List<HospitalSummaryResponse>> response =
                hospitalService.sortHospitals(sortBy);

        return ResponseEntity.ok(response);
    }

    // Fetch hospital list for dashboard filter dropdown
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<DropdownResponse>>> getHospitalList() {

        ApiResponse<List<DropdownResponse>> response =
                hospitalService.getHospitalList();

        return ResponseEntity.ok(response);
    }

    // Filter hospitals based on specialization
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<HospitalSpecializationResponse>>> filterHospitals(
            @RequestParam String specialization) {

        ApiResponse<List<HospitalSpecializationResponse>> response =
                hospitalService.filterHospitalsBySpecialization(specialization);

        return ResponseEntity.ok(response);
    }



    //Retrieve weekly activity data of a hospital including admissions,
    // discharges, and surgeries for chart visualization.


    @GetMapping("/{id}/weekly-activity")
    public ResponseEntity<ApiResponse<List<WeeklyActivityResponse>>> getWeeklyActivity(
            @PathVariable Long id) {

        ApiResponse<List<WeeklyActivityResponse>> response =
                hospitalService.getWeeklyActivity(id);

        return ResponseEntity.ok(response);
    }


    //Retrieve the list of all hospitals available in the system.
    @GetMapping
    public ResponseEntity<ApiResponse<List<HospitalResponse>>> getAllHospitals(){

        ApiResponse<List<HospitalResponse>> response = hospitalService.getAllHospitals();
        return ResponseEntity.ok(response);

    }


    //Retrieve all patient reviews and ratings associated with a specific hospital.

    @GetMapping("/{hospitalId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getHospitalReviews(@PathVariable Long hospitalId){

        ApiResponse<List<ReviewResponse>> response = hospitalService.getHospitalReviews(hospitalId);
        return ResponseEntity.ok(response);
    }
}