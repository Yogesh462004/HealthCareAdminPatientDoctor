package nimblix.in.HealthCareHub.service;

import nimblix.in.HealthCareHub.model.Doctor;
import nimblix.in.HealthCareHub.request.DoctorAvailabilityRequest;
import nimblix.in.HealthCareHub.request.DoctorRegistrationRequest;
import nimblix.in.HealthCareHub.request.DoctorScheduleRequest;
import nimblix.in.HealthCareHub.response.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface DoctorService {
    String registerDoctor(DoctorRegistrationRequest request);
    DoctorProfileResponse getDoctorProfile(Long doctorId);
    ResponseEntity<?> getDoctorDetails(Long doctorId, Long hospitalId);
    DoctorReviewResponse getDoctorReviews(Long doctorId);
    String updateDoctorDetails(DoctorRegistrationRequest request);
    String deleteDoctorDetails(Long doctorId);
    DoctorProfileResponse getDoctorById(Long doctorId);
    Doctor getDoctorById(Long doctorId);

    List<String> getAllRoles();
    List<Doctor> searchDoctorByName(String name);
    DoctorListResponse getDoctorsByHospitalId(Long hospitalId);
    List<Doctor> filterDoctorsBySpecialization(String specialization);
    List<DoctorAvailabilityResponse> getAllAvailableDoctors();
    List<Map<String, Object>> getDoctorAvailability(Long doctorId);
    DoctorProfileResponse addDoctor(DoctorRegistrationRequest request);

    DoctorAvailabilityResponse setDoctorAvailability(DoctorAvailabilityRequest request);
    DoctorStatusResponse updateDoctorStatus(Long doctorId, String status);

    DoctorStatusResponse getDoctorStatus(Long doctorId);
    DoctorScheduleResponse createDoctorSchedule(Long doctorId, DoctorScheduleRequest request);
    List<DoctorScheduleResponse> getDoctorSchedules(Long doctorId);
    DoctorScheduleResponse updateDoctorScheduleStatus(Long scheduleId, String status);
}