package nimblix.in.HealthCareHub.serviceImpl;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nimblix.in.HealthCareHub.constants.HealthCareConstants;
import nimblix.in.HealthCareHub.exception.DuplicateHospitalException;
import nimblix.in.HealthCareHub.exception.HospitalNotFoundException;
import nimblix.in.HealthCareHub.exception.InvalidCredentialsException;
import nimblix.in.HealthCareHub.model.Hospital;
import nimblix.in.HealthCareHub.model.Medicine;
import nimblix.in.HealthCareHub.repository.*;
import nimblix.in.HealthCareHub.request.HospitalLoginRequest;
import nimblix.in.HealthCareHub.request.HospitalRegistrationRequest;
import nimblix.in.HealthCareHub.request.MedicineAddRequest;
import nimblix.in.HealthCareHub.response.*;
import nimblix.in.HealthCareHub.security.JwtUtil;
import nimblix.in.HealthCareHub.service.HospitalService;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;
    private final MedicineRepository medicineRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppointmentRepository appointmentRepository;

    private final JwtUtil jwtUtil;


    @Override
    public Long registerHospital(HospitalRegistrationRequest request) {
        if (request == null) {
            throw new ValidationException(HealthCareConstants.REQUEST_BODY_NULL);
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ValidationException(HealthCareConstants.HOSPITAL_NAME_REQUIRED);
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new ValidationException(HealthCareConstants.EMAIL_REQUIRED);
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ValidationException(HealthCareConstants.PASSWORD_REQUIRED);
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException(HealthCareConstants.PASSWORD_MISMATCH);
        }
        if (request.getTotalBeds() <= 0) {
            throw new ValidationException(HealthCareConstants.INVALID_TOTAL_BEDS);
        }

        if (hospitalRepository.findByNameAndCityAndState(
                request.getName(), request.getCity(), request.getState()).isPresent()) {
            throw new DuplicateHospitalException(HealthCareConstants.HOSPITAL_ALREADY_EXISTS);
        }

        Hospital hospital = Hospital.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .phone(request.getPhone())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .establishedYear(request.getEstablishedYear())
                .totalBeds(request.getTotalBeds())
                .specializations(request.getSpecializations())
                .doctors(request.getDoctors())
                .aboutHospital(request.getAboutHospital())
                .build();

        Hospital savedHospital = hospitalRepository.save(hospital);
        return savedHospital.getId();
    }


@Override
public String loginHospital(HospitalLoginRequest request) {
    if (request == null) {
        throw new ValidationException(HealthCareConstants.REQUEST_BODY_NULL);
    }
    if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
        throw new ValidationException(HealthCareConstants.EMAIL_REQUIRED);
    }
    if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
        throw new ValidationException(HealthCareConstants.PASSWORD_REQUIRED);
    }

    Hospital hospital = hospitalRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new HospitalNotFoundException(HealthCareConstants.HOSPITAL_NOT_FOUND));

    if (!passwordEncoder.matches(request.getPassword(), hospital.getPassword())) {
        throw new InvalidCredentialsException(HealthCareConstants.INVALID_CREDENTIALS);
    }

    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
            hospital.getEmail(),
            hospital.getPassword(),
            new ArrayList<>()
    );

    return jwtUtil.generateToken(userDetails);
}





    @Override
    public String addMedicine(MedicineAddRequest request){

        //-edge cases---
        //--Check Hospital Exists--
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new IllegalArgumentException("Hospital Not Found"));

        if (request.getPrice()==null || request.getPrice()<=0){
            throw new IllegalArgumentException("price must be greater than 0");
        }

        if (request.getStockQuantity()==null || request.getStockQuantity()<0){
            throw new IllegalArgumentException("StockQuantity cannot be negative ");
        }

        Optional<Medicine> existing = medicineRepository.findByMedicineNameAndHospital(
                request.getMedicineName(), hospital);
        if (existing.isPresent()){
            throw new IllegalArgumentException("Medicine already exists in this hospital");
        }

        //--Create Medicine--
        Medicine medicine = Medicine.builder()
                .medicineName(request.getMedicineName())
                .manufacturer(request.getManufacturer())
                .description(request.getDescription())
                .dosage(request.getDosage())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .isActive("ACTIVE")
                .hospital(hospital)
                .build();

        //--Save medicine
        medicineRepository.save(medicine);
        return "Medicine Added Successfully";
    }

    @Override
    public void addRooms(Long hospitalId,
                         List<HospitalRegistrationRequest.Room> rooms) {

        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found"));

        if (hospital.getRooms().size() + rooms.size() > hospital.getTotalBeds()) {
            throw new IllegalArgumentException("Exceeds total bed capacity");
        }

        for (HospitalRegistrationRequest.Room roomReq : rooms) {
            Hospital.Room room = new Hospital.Room();
            room.setRoomNumber(roomReq.getRoomNumber());
            room.setRoomType(roomReq.getRoomType());
            room.setAvailable(roomReq.isAvailable());

            hospital.getRooms().add(room);
        }

        hospitalRepository.save(hospital);
    }

    @Override
    public List<RoomResponse> getAvailableRooms(Long hospitalId) {

        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found"));

        List<RoomResponse> response = new ArrayList<>();

        for (Hospital.Room room : hospital.getRooms()) {
            if (room.isAvailable()) {
                RoomResponse roomResponse = RoomResponse.builder()
                        .roomNumber(room.getRoomNumber())
                        .roomType(room.getRoomType())
                        .available(room.isAvailable())
                        .build();

                response.add(roomResponse);
            }
        }

        return response;
    }

    @Override
    public ApiResponse<List<HospitalResponse>> getAllHospitals() {

        List<HospitalResponse> hospitals = hospitalRepository.getAllHospitals();
        if (hospitals == null || hospitals.isEmpty()) {
            throw new IllegalArgumentException(HealthCareConstants.NO_HOSPITALS_FOUND);
        }

        return new ApiResponse<>(
                HealthCareConstants.STATUS_SUCCESS,
                HealthCareConstants.HOSPITAL_FETCHED_SUCCESS,
                hospitals
        );

    }

    @Override
    public ApiResponse<HospitalDetailResponse> getHospitalById(Long id) {

        log.info("Fetching hospital with id: {}", id);

        // Edge Case 1: Invalid ID
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    HealthCareConstants.INVALID_HOSPITAL_ID);
        }

        // Fetch hospital
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                HealthCareConstants.HOSPITAL_NOT_FOUND + id));

        log.info("Hospital fetched successfully with id: {}", id);

        HospitalDetailResponse data = HospitalDetailResponse.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .address(hospital.getAddress())
                .city(hospital.getCity())
                .state(hospital.getState())
                .phone(hospital.getPhone())
                .email(hospital.getEmail())
                .totalBeds(hospital.getTotalBeds())
                .createdTime(hospital.getCreatedTime())
                .updatedTime(hospital.getUpdatedTime())
                .build();

        return ApiResponse.<HospitalDetailResponse>builder()
                .status(HealthCareConstants.SUCCESS)
                .message(HealthCareConstants.HOSPITAL_FETCHED_SUCCESSFULLY)
                .data(data)
                .build();
    }


    @Override
    public ApiResponse<List<HospitalSearchResponse>> searchHospitalByName(String name){

        log.info("Searching hospitals with name: {}", name);

        // Edge Case 1: Empty name
        if (name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException(
                    HealthCareConstants.HOSPITAL_NAME_EMPTY);
        }

        List<Hospital> hospitals =
                hospitalRepository.findByNameContainingIgnoreCase(name);

        // Edge Case 2: No hospitals found
        if(hospitals.isEmpty()){
            return ApiResponse.<List<HospitalSearchResponse>>builder()
                    .status(HealthCareConstants.SUCCESS)
                    .message(HealthCareConstants.NO_HOSPITAL_FOUND)
                    .data(List.of())
                    .build();
        }

        List<HospitalSearchResponse> hospitalList =
                hospitals.stream()
                        .map(h -> HospitalSearchResponse.builder()
                                .id(h.getId())
                                .hospitalName(h.getName())
                                .city(h.getCity())
                                .state(h.getState())
                                .build())
                        .toList();

        return ApiResponse.<List<HospitalSearchResponse>>builder()
                .status(HealthCareConstants.SUCCESS)
                .message(HealthCareConstants.HOSPITAL_FETCHED_SUCCESSFULLY)
                .data(hospitalList)
                .build();
    }

    @Override
    public ApiResponse<HospitalStatsResponse> getHospitalStats(Long hospitalId) {

        log.info("Fetching hospital stats for hospitalId: {}", hospitalId);

        // Edge Case 1: Hospital ID validation
        if (hospitalId == null || hospitalId <= 0) {
            throw new IllegalArgumentException(
                    HealthCareConstants.INVALID_HOSPITAL_ID);
        }

        // Edge Case 2: Hospital existence
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() ->
                        new RuntimeException(
                                HealthCareConstants.HOSPITAL_NOT_FOUND + hospitalId));

        // Fetch statistics
        Long totalDoctors =
                doctorRepository.countDoctorsByHospitalId(hospitalId);

        Long totalPatients =
                patientRepository.countPatientsByHospitalId(hospitalId);

        Long totalSpecializations =
                doctorRepository.countSpecializationsByHospitalId(hospitalId);

        Long totalBeds =
                hospital.getTotalBeds() == null
                        ? 0L
                        : hospital.getTotalBeds().longValue();

        Long patientsServed =
                totalPatients == null ? 0L : totalPatients;

        HospitalStatsResponse stats = HospitalStatsResponse.builder()
                .hospitalId(hospitalId)
                .totalBeds(totalBeds)
                .totalDoctors(totalDoctors == null ? 0L : totalDoctors)
                .totalPatients(totalPatients == null ? 0L : totalPatients)
                .patientsServed(patientsServed)
                .totalSpecializations(totalSpecializations == null ? 0L : totalSpecializations)
                .build();

        return ApiResponse.<HospitalStatsResponse>builder()
                .status(HealthCareConstants.SUCCESS)
                .message(HealthCareConstants.HOSPITAL_STATS_FETCHED_SUCCESS)
                .data(stats)
                .build();
    }

    @Override
    public ApiResponse<List<HospitalSummaryResponse>> sortHospitals(String sortBy) {

        log.info("Sorting hospitals by parameter: {}", sortBy);

        // Edge Case 1: sortBy null or empty
        if (sortBy == null || sortBy.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    HealthCareConstants.INVALID_SORT_PARAMETER
            );
        }

        List<Hospital> hospitals;

        switch (sortBy.toLowerCase()) {

            case "rating":
                hospitals = hospitalRepository
                        .findAll(Sort.by(Sort.Direction.DESC, "rating"));
                break;

            case "name":
                hospitals = hospitalRepository
                        .findAll(Sort.by(Sort.Direction.ASC, "name"));
                break;

            case "doctorcount":
                hospitals = hospitalRepository
                        .findAll(Sort.by(Sort.Direction.DESC, "doctorCount"));
                break;

            // Edge Case 2: invalid parameter
            default:
                throw new IllegalArgumentException(
                        HealthCareConstants.INVALID_SORT_PARAMETER
                );
        }

        // Edge Case 3: no hospitals found
        if (hospitals.isEmpty()) {
            return ApiResponse.<List<HospitalSummaryResponse>>builder()
                    .status(HealthCareConstants.SUCCESS)
                    .message(HealthCareConstants.NO_HOSPITALS_FOUND)
                    .data(List.of())
                    .build();
        }

        List<HospitalSummaryResponse> hospitalResponses =
                hospitals.stream()
                        .map(hospital -> new HospitalSummaryResponse(
                                hospital.getId(),
                                hospital.getName(),
                                hospital.getCity(),
                                hospital.getRating(),
                                hospital.getDoctorCount()
                        ))
                        .toList();

        return ApiResponse.<List<HospitalSummaryResponse>>builder()
                .status(HealthCareConstants.SUCCESS)
                .message(HealthCareConstants.HOSPITALS_SORTED_SUCCESS)
                .data(hospitalResponses)
                .build();
    }

    @Override
    public ApiResponse<List<DropdownResponse>> getHospitalList() {

        // Query used to fetch hospital dropdown list
        List<DropdownResponse> hospitals =
                hospitalRepository.getHospitalDropdownList();

        // Edge Case: No hospitals found
        if (hospitals.isEmpty()) {

            return new ApiResponse<>(
                    HealthCareConstants.SUCCESS,
                    HealthCareConstants.NO_HOSPITAL_FOUND,
                    List.of()
            );
        }

        return new ApiResponse<>(
                HealthCareConstants.SUCCESS,
                HealthCareConstants.HOSPITAL_FETCHED_SUCCESS,
                hospitals
        );
    }

    @Override
    public ApiResponse<List<HospitalSpecializationResponse>>
    filterHospitalsBySpecialization(String specialization) {

        log.info("Filtering hospitals for specialization: {}", specialization);

        // Edge Case 1: specialization validation
        if (specialization == null || specialization.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    HealthCareConstants.INVALID_SPECIALIZATION);
        }

        // Fetch hospitals from repository
        List<HospitalSpecializationResponse> hospitals =
                hospitalRepository.findHospitalsBySpecialization(specialization);

        // Edge Case 2: no hospitals found
        if (hospitals.isEmpty()) {

            return ApiResponse.<List<HospitalSpecializationResponse>>builder()
                    .status(HealthCareConstants.SUCCESS)
                    .message(HealthCareConstants.NO_HOSPITAL_FOUND)
                    .data(List.of())
                    .build();
        }

        return ApiResponse.<List<HospitalSpecializationResponse>>builder()
                .status(HealthCareConstants.SUCCESS)
                .message(HealthCareConstants.HOSPITAL_FETCHED_SUCCESS)
                .data(hospitals)
                .build();
    }



    @Override
    public ApiResponse<List<ReviewResponse>> getHospitalReviews(Long hospitalId) {

        if (hospitalId == null || hospitalId <= 0){
            throw new IllegalArgumentException(HealthCareConstants.INVALID_HOSPITAL_ID);
        }

        if (!hospitalRepository.existsById(hospitalId)){
            throw new IllegalArgumentException(HealthCareConstants.HOSPITAL_NOT_FOUND + hospitalId);
        }

        List<ReviewResponse> reviews = reviewRepository.findPatientReviewsByHospitalId(hospitalId);
        return new ApiResponse<>(
                HealthCareConstants.STATUS_SUCCESS,
                HealthCareConstants.REVIEWS_FETCHED_SUCCESS,
                reviews
        );
    }


    @Override
    public ApiResponse<List<WeeklyActivityResponse>> getWeeklyActivity(Long hospitalId) {

        // Edge Case 1: Invalid hospital id
        if (hospitalId == null || hospitalId <= 0) {
            throw new IllegalArgumentException(
                    HealthCareConstants.INVALID_HOSPITAL_ID);
        }

        // Edge Case 2: Hospital existence check
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new IllegalArgumentException(
                    HealthCareConstants.HOSPITAL_NOT_FOUND + hospitalId);
        }

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Fetch weekly admissions
        List<Object[]> admissions =
                appointmentRepository.getWeeklyAdmissions(startOfWeek, endOfWeek);

        Map<LocalDate, Long> admissionMap = new HashMap<>();

        for (Object[] row : admissions) {

            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();

            admissionMap.put(
                    date,
                    ((Number) row[1]).longValue()
            );
        }

        List<WeeklyActivityResponse> weekActivity = new ArrayList<>();

        // Build 7 days activity
        for (int i = 0; i < 7; i++) {

            LocalDate date = startOfWeek.plusDays(i);

            weekActivity.add(
                    new WeeklyActivityResponse(
                            date.getDayOfWeek().toString(),
                            admissionMap.getOrDefault(date, 0L),
                            0L,
                            0L
                    )
            );
        }

        return ApiResponse.<List<WeeklyActivityResponse>>builder()
                .status(HealthCareConstants.SUCCESS)
                .message(HealthCareConstants.WEEKLY_ACTIVITY_FETCHED)
                .data(weekActivity)
                .build();
    }
}