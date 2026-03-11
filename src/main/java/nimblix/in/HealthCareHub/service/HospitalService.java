package nimblix.in.HealthCareHub.service;

import nimblix.in.HealthCareHub.request.HospitalLoginRequest;
import nimblix.in.HealthCareHub.request.HospitalRegistrationRequest;

import nimblix.in.HealthCareHub.request.MedicineAddRequest;
import nimblix.in.HealthCareHub.response.*;

import java.util.List;
import java.util.Map;

public interface HospitalService {


    Long registerHospital(HospitalRegistrationRequest request);
    String addMedicine(MedicineAddRequest request);

    void addRooms(Long hospitalId, List<HospitalRegistrationRequest.Room> rooms);

    List<RoomResponse> getAvailableRooms(Long hospitalId);
    ApiResponse<List<HospitalSearchResponse>> searchHospitalByName(String name);


    ApiResponse<HospitalDetailResponse> getHospitalById(Long id);
    ApiResponse<HospitalStatsResponse> getHospitalStats(Long hospitalId);
    String loginHospital(HospitalLoginRequest request);
    ApiResponse<List<HospitalSummaryResponse>> sortHospitals(String sortBy);
    ApiResponse<List<DropdownResponse>> getHospitalList();
    ApiResponse<List<HospitalSpecializationResponse>>
    filterHospitalsBySpecialization(String specialization);

    ApiResponse<List<WeeklyActivityResponse>> getWeeklyActivity(Long hospitalId);


    ApiResponse<List<HospitalResponse>> getAllHospitals();
    ApiResponse<List<ReviewResponse>> getHospitalReviews(Long hospitalId);

}
