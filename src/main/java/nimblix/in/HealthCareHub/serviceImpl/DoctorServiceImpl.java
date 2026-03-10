package nimblix.in.HealthCareHub.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.constants.HealthCareConstants;
import nimblix.in.HealthCareHub.exception.DoctorNotFoundException;
import nimblix.in.HealthCareHub.exception.UserNotFoundException;
import nimblix.in.HealthCareHub.model.*;
import nimblix.in.HealthCareHub.repository.*;
import nimblix.in.HealthCareHub.request.DoctorRegistrationRequest;
import nimblix.in.HealthCareHub.request.DoctorScheduleRequest;
import nimblix.in.HealthCareHub.response.*;
import nimblix.in.HealthCareHub.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final SpecializationRepository specializationRepository;
    private final ReviewRepository reviewRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final PatientRepository patientRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;

    @Override
    public String registerDoctor(DoctorRegistrationRequest request) {
        try {
            // Check if email already exists
            if (doctorRepository.findByEmailId(request.getDoctorEmail()).isPresent()) {
                return "Doctor already exists with this email";
            }

            // Fetch Hospital
            Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new RuntimeException("Hospital not found"));

            // Fetch Specialization
            Specialization specialization = specializationRepository.findByName(request.getSpecializationName())
                    .orElseThrow(() -> new RuntimeException("Specialization not found"));

            // Create Doctor
            Doctor doctor = new Doctor();

            doctor.setName(request.getDoctorName());
            doctor.setEmailId(request.getDoctorEmail());
            doctor.setPassword(request.getPassword());
            doctor.setPhone(request.getPhoneNo());
            doctor.setQualification(request.getQualification());
            doctor.setExperienceYears(request.getExperience());
            doctor.setDescription(request.getDescription());
            doctor.setHospital(hospital);

            doctor.setHospital(hospital);
            doctor.setSpecialization(specialization);

            doctorRepository.save(doctor);

            return "Doctor Registered Successfully";
        } catch (UserNotFoundException e) {
            return "User not found";
        }
    }

    @Override
    public ResponseEntity<?> getDoctorDetails(Long doctorId, Long hospitalId) {

        Doctor doctor = doctorRepository
                .findByIdAndHospitalId(doctorId, hospitalId)
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found in this hospital"));

        return ResponseEntity.status(HttpStatus.OK).body(doctor);
    }

    @Override
    public String updateDoctorDetails(DoctorRegistrationRequest request) {

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctorRepository.findByEmailId(request.getDoctorEmail())
                .filter(existingDoctor -> !existingDoctor.getId().equals(doctor.getId()))
                .ifPresent(existingDoctor -> {
                    throw new RuntimeException("Email already used by another doctor");
                });

        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        Specialization specialization = specializationRepository
                .findByName(request.getSpecializationName())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        doctor.setName(request.getDoctorName());
        doctor.setEmailId(request.getDoctorEmail());
        doctor.setPassword(request.getPassword());
        doctor.setPhone(request.getPhoneNo());
        doctor.setQualification(request.getQualification());
        doctor.setExperienceYears(request.getExperience());
        doctor.setDescription(request.getDescription());

        doctor.setHospital(hospital);
        doctor.setSpecialization(specialization);

        doctorRepository.save(doctor);

        return "Doctor Updated Successfully";
    }

    @Override
    public String deleteDoctorDetails(Long doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        doctor.setIsActive(HealthCareConstants.IN_ACTIVE);
        doctorRepository.save(doctor);

        return "Doctor deleted successfully (Hard Delete)";
    }

    @Override
    public DoctorProfileResponse getDoctorProfile(Long doctorId) {

        return doctorRepository.findDoctorProfileById(doctorId)
                .orElseThrow(() ->
                        new DoctorNotFoundException("Doctor not found with id: " + doctorId)
                );
    }

    @Override
    public DoctorProfileResponse getDoctorById(Long doctorId) {

        // Edge case 1: null or negative ID
        if (doctorId == null || doctorId <= 0) {
            throw new IllegalArgumentException("Doctor ID cannot be 0 or Negative");
        }

        // Edge case 2: Doctor not found
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + doctorId));

        // Edge case 3: Doctor is inactive / soft deleted
        if (HealthCareConstants.IN_ACTIVE.equals(doctor.getIsActive())) {
            throw new DoctorNotFoundException("Doctor with ID " + doctorId + " is no longer active.");
        }

        // Map entity → DTO (password excluded for security)
        return DoctorProfileResponse.builder()
                .doctorId(doctor.getId())
                .name(doctor.getName())
                .experienceYears(doctor.getExperienceYears())
                .phone(doctor.getPhone())
                .email(doctor.getEmailId())
                .qualification(doctor.getQualification())

                // specialization details
                .specializationId(doctor.getSpecialization().getId())
                .specializationName(doctor.getSpecialization().getName())

                // hospital details
                .hospitalId(doctor.getHospital().getId())
                .hospitalName(doctor.getHospital().getName())
                .hospitalAddress(doctor.getHospital().getAddress())
                .hospitalCity(doctor.getHospital().getCity())
                .hospitalState(doctor.getHospital().getState())
                .hospitalPhone(doctor.getHospital().getPhone())
                .hospitalEmail(doctor.getHospital().getEmail())
                .hospitalTotalBeds(doctor.getHospital().getTotalBeds())
                .build();
    }

    @Override
    public DoctorReviewResponse getDoctorReviews(Long doctorId) {

        doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new DoctorNotFoundException(
                                HealthCareConstants.DOCTORNOTFOUND + doctorId));
        return reviewRepository.findReviewStatsByDoctorId(doctorId);
    }

    @Override
    public List<String> getAllRoles() {
        return Arrays.stream(Role.values())
                .map(Enum::name)
                .toList();
    }

    @Override
    public List<Doctor> searchDoctorByName(String name) {

        if (name == null) {
            return List.of();
        }
System.out.println(doctorRepository.searchDoctorByName(name));
        return doctorRepository.searchDoctorByName(name);
    }

    @Override
    public DoctorListResponse getDoctorsByHospitalId(Long hospitalId) {
        // Edge Case 1: hospitalId validation
        if (hospitalId == null || hospitalId <= 0) {
            throw new IllegalArgumentException(HealthCareConstants.INVALID_HOSPITAL_ID);
        }

        // Query used to fetch doctors for the given hospital
        List<Doctor> doctors = doctorRepository.findDoctorsByHospitalId(hospitalId);

        // Edge Case 2: No doctors found
        if (doctors.isEmpty()) {
            return new DoctorListResponse(
                    HealthCareConstants.SUCCESS,
                    HealthCareConstants.NO_DOCTORS_FOUND,
                    List.of()
            );
        }

        List<DoctorSummaryResponse> doctorResponses = doctors.stream()
                .map(doctor -> new DoctorSummaryResponse(
                        doctor.getId(),
                        doctor.getName(),
                        doctor.getSpecialization() != null
                                ? doctor.getSpecialization().getName()
                                : null
                ))
                .toList();

        return new DoctorListResponse(
                HealthCareConstants.SUCCESS,
                HealthCareConstants.DOCTORS_FETCHED_SUCCESS,
                doctorResponses
        );
    }

    @Override
    public List<Doctor> filterDoctorsBySpecialization(String specialization) {

        if (specialization == null || specialization.trim().isEmpty()) {
            return null;
        }

        return doctorRepository
                .findBySpecialization_NameIgnoreCase(specialization.trim());
    }

    // ✅ GET /api/doctors/availability
    @Override
    public List<DoctorAvailabilityResponse> getAllAvailableDoctors() {

        List<DoctorAvailability> availabilityList =
                availabilityRepository.findByIsAvailableTrue();

        return availabilityList.stream()
                .map(availability -> DoctorAvailabilityResponse.builder()
                        .id(availability.getId())
                        .doctorId(availability.getDoctor().getId())
                        .doctorName(availability.getDoctor().getName())
                        .availableDate(availability.getAvailableDate())
                        .startTime(availability.getStartTime())
                        .endTime(availability.getEndTime())
                        .available(availability.isAvailable())
                        .build())
                .collect(Collectors.toList());
    }

//    @Override
//    public List<DoctorAvailabilityResponse> getDoctorAvailability() {
//
//        List<DoctorAvailability> slots =
//                availabilityRepository.findByIsAvailableTrue();
//
//        return slots.stream().map(slot -> DoctorAvailabilityResponse.builder()
//                .id(slot.getId())
//                .doctorId(slot.getDoctor().getId())
//                .doctorName(slot.getDoctor().getName())
//                .availableDate(slot.getAvailableDate())
//                .startTime(slot.getStartTime())
//                .endTime(slot.getEndTime())
//                .available(slot.isAvailable())
//                .build()
//        ).collect(Collectors.toList());
//    }

    @Override
    public List<Map<String, Object>> getDoctorAvailability(Long doctorId) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        List<DoctorAvailability> slots = doctorRepository.findAvailabilityByDoctorId(doctorId);
        return slots.stream().map(slot -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("doctorId",      slot.getDoctor().getId());
            map.put("name",          slot.getDoctor().getName());
            map.put("availableNow",  slot.isAvailable());
            map.put("availableDate", slot.getAvailableDate());
            map.put("startTime",     slot.getStartTime());
            map.put("endTime",       slot.getEndTime());
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public DoctorProfileResponse addDoctor(DoctorRegistrationRequest request) {

        // Edge Case 1
        if (request == null) {
            throw new RuntimeException(HealthCareConstants.INVALID_REQUEST);
        }

        // Edge Case 2
        if (doctorRepository.findByPhone(request.getPhoneNo()).isPresent()) {
            throw new RuntimeException(HealthCareConstants.DOCTOR_PHONE_EXISTS);
        }

        // Edge Case 3
        if (doctorRepository.findByEmailId(request.getDoctorEmail()).isPresent()) {
            throw new RuntimeException(HealthCareConstants.DOCTOR_EMAIL_EXISTS);
        }

        // Fetch hospital
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() ->
                        new RuntimeException(HealthCareConstants.HOSPITAL_NOT_FOUND));

        // Fetch specialization
        Specialization specialization = specializationRepository
                .findByName(request.getSpecializationName())
                .orElseThrow(() ->
                        new RuntimeException(HealthCareConstants.SPECIALIZATION_NOT_FOUND));

        // Save doctor
        Doctor doctor = Doctor.builder()
                .name(request.getDoctorName())
                .emailId(request.getDoctorEmail())
                .password(request.getPassword())
                .qualification(request.getQualification())
                .experienceYears(request.getExperience())
                .description(request.getDescription())
                .phone(request.getPhoneNo())
                .hospital(hospital)
                .specialization(specialization)
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        // Fetch response using SQL projection
        return doctorRepository.getDoctorProfile(savedDoctor.getId());
    }

    @Override
    public String setDoctorAvailability(DoctorRegistrationRequest request) {
        doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + request.getDoctorId()));
        doctorRepository.insertAvailability(
                request.getDoctorId(),
                request.getAvailableDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.isAvailable()
        );
        return "Doctor availability set successfully";
    }

    // Returns String — same pattern as registerDoctor
    @Override
    public String updateDoctorStatus(Long doctorId, String status) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        doctor.setDoctorStatus(status);
        doctorRepository.save(doctor);
        return "Doctor status updated to: " + resolveStatusLabel(status);
    }

    //  Returns DoctorStatusResponse — same pattern as getDoctorProfile
    @Override
    public DoctorStatusResponse getDoctorStatus(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        String status = doctor.getDoctorStatus() != null
                ? doctor.getDoctorStatus()
                : HealthCareConstants.DOCTOR_STATUS_AVAILABLE;
        return new DoctorStatusResponse(
                doctor.getId(),
                doctor.getName(),
                status,
                resolveStatusLabel(status)
        );
    }

    private String resolveStatusLabel(String status) {
        return switch (status) {
            case HealthCareConstants.DOCTOR_STATUS_AVAILABLE    -> " Available";
            case HealthCareConstants.DOCTOR_STATUS_IN_OPERATION -> " In Operation";
            case HealthCareConstants.DOCTOR_STATUS_ON_BREAK     -> " On Break";
            case HealthCareConstants.DOCTOR_STATUS_ON_LEAVE     -> " On Leave";
            case HealthCareConstants.DOCTOR_STATUS_BUSY         -> " Busy with Patient";
            case HealthCareConstants.DOCTOR_STATUS_OFF_DUTY     -> " Off Duty";
            default -> " NOt Available";
        };
    }

//    @Override
//    public DoctorScheduleResponse createDoctorSchedule(Long doctorId, DoctorScheduleRequest request) {
//        if (request.getOperationName() == null || request.getOperationName().trim().isEmpty()) {
//            throw new IllegalArgumentException("Operation name cannot be null or empty");
//        }
//
//        if (request.getOperationDate() == null || request.getOperationDate().trim().isEmpty()) {
//            throw new IllegalArgumentException("Operation date cannot be null or empty");
//        }
//        try {
//            LocalDate.parse(request.getOperationDate());
//        } catch (DateTimeParseException e) {
//            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
//        }
//
//        Doctor doctor = doctorRepository.findById(doctorId)
//                .orElseThrow(() ->
//                        new DoctorNotFoundException(HealthCareConstants.DOCTORNOTFOUND+ " " + doctorId));
//
//        Patient patient = patientRepository.findById(request.getPatientId())
//                .orElseThrow(() ->
//                        new DoctorNotFoundException(HealthCareConstants.PATIENTNOTFOUND + request.getPatientId()));
//
//        DoctorSchedule schedule = new DoctorSchedule();
//
//        schedule.setDoctor(doctor);
//        schedule.setPatient(patient);
//        schedule.setOperationName(request.getOperationName());
//        schedule.setOperationDate(request.getOperationDate());
//        schedule.setStatus(HealthCareConstants.SCHEDULED);
//        boolean exists = doctorScheduleRepository.existsByDoctor_IdAndPatient_IdAndOperationDate(
//                        doctorId,
//                        request.getPatientId(),
//                        request.getOperationDate());
//
//        if (exists) {
//            throw new IllegalArgumentException("Schedule already exists for this doctor, patient and date");
//        }
//
//        doctorScheduleRepository.save(schedule);
//
//        return doctorScheduleRepository.findSchedulesByDoctorId(doctorId)
//                .stream()
//                .filter(s -> s.getScheduleId().equals(schedule.getId()))
//                .findFirst()
//                .orElseThrow(() ->
//                        new RuntimeException("Schedule creation failed"));
//    }
@Override
public DoctorScheduleResponse createDoctorSchedule(Long doctorId, DoctorScheduleRequest request) {

    if (request.getOperationName() == null || request.getOperationName().trim().isEmpty()) {
        return null;
    }

    if (request.getOperationDate() == null || request.getOperationDate().trim().isEmpty()) {
        return null;
    }

    try {
        LocalDate.parse(request.getOperationDate());
    } catch (DateTimeParseException e) {
        return null;
    }

    Doctor doctor = doctorRepository.findById(doctorId).orElse(null);

    if (doctor == null) {
        return null;
    }

    Patient patient = patientRepository.findById(request.getPatientId()).orElse(null);

    if (patient == null) {
        return null;
    }

    boolean exists = doctorScheduleRepository
            .existsByDoctor_IdAndPatient_IdAndOperationDate(
                    doctorId,
                    request.getPatientId(),
                    request.getOperationDate());

    if (exists) {
        return null;
    }

    DoctorSchedule schedule = new DoctorSchedule();

    schedule.setDoctor(doctor);
    schedule.setPatient(patient);
    schedule.setOperationName(request.getOperationName());
    schedule.setOperationDate(request.getOperationDate());
    schedule.setStatus("SCHEDULED");

    doctorScheduleRepository.save(schedule);

    return doctorScheduleRepository.findSchedulesByDoctorId(doctorId)
            .stream()
            .filter(s -> s.getScheduleId().equals(schedule.getId()))
            .findFirst()
            .orElse(null);
}
    @Override
    public List<DoctorScheduleResponse> getDoctorSchedules(Long doctorId) {

        doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new DoctorNotFoundException(HealthCareConstants.DOCTORNOTFOUND
                                + " " + doctorId)
                );

        return doctorScheduleRepository.findSchedulesByDoctorId(doctorId);
    }
    @Override
    public DoctorScheduleResponse updateDoctorScheduleStatus(Long scheduleId, String status) {

        DoctorSchedule schedule = doctorScheduleRepository.findById(scheduleId)
                .orElseThrow(() ->
                        new DoctorNotFoundException("Doctor Schedule not found with id: " + scheduleId));
        if(!status.equalsIgnoreCase(HealthCareConstants.SCHEDULED) &&
                !status.equalsIgnoreCase(HealthCareConstants.ONGOING) &&
                !status.equalsIgnoreCase(HealthCareConstants.COMPLETED) &&
                !status.equalsIgnoreCase(HealthCareConstants.FAILED)) {

            throw new IllegalArgumentException("Invalid schedule status");
        }

        schedule.setStatus(status);

        doctorScheduleRepository.save(schedule);

        return doctorScheduleRepository.findSchedulesByDoctorId(schedule.getDoctor().getId())
                .stream()
                .filter(s -> s.getScheduleId().equals(scheduleId))
                .findFirst()
                .orElseThrow(() ->
                        new UserNotFoundException("Schedule update failed"));
    }

}