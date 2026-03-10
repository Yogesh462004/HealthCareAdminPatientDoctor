package nimblix.in.HealthCareHub.repository;

import nimblix.in.HealthCareHub.model.Doctor;
import nimblix.in.HealthCareHub.model.DoctorAvailability;
import nimblix.in.HealthCareHub.response.DoctorProfileResponse;
import nimblix.in.HealthCareHub.response.SpecializationDistributionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    List<Doctor> findByHospitalIdAndNameContainingIgnoreCase(Long hospitalId, String name);
    Optional<Doctor> findByEmailId(String emailId);
    Optional<Doctor> findByIdAndHospitalId(Long doctorId, Long hospitalId);



    @Query("""
            SELECT new nimblix.in.HealthCareHub.response.DoctorProfileResponse(
                d.id,
                d.name,
                d.emailId,
                d.phone,
                d.qualification,
                d.experienceYears,
                s.id,
                s.name,
                h.id,
                h.name,
                h.address,
                h.city,
                h.state,
                h.phone,
                h.email,
                h.totalBeds
            )
            FROM Doctor d
            LEFT JOIN d.specialization s
            LEFT JOIN d.hospital h
            WHERE d.id = :doctorId
            """)
    Optional<DoctorProfileResponse> findDoctorProfileById(@Param("doctorId") Long doctorId);
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Doctor> searchDoctorByName(@Param("name") String name);
    List<Doctor> findDoctorsByHospitalId(@Param("hospitalId") Long hospitalId);
    List<Doctor> findBySpecialization_NameIgnoreCase(String name);

    @Query("""
        SELECT COUNT(DISTINCT d.specialization)
        FROM Doctor d
        WHERE d.hospital.id = :hospitalId
        """)
    Long countSpecializationsByHospitalId(@Param("hospitalId") Long hospitalId);

    @Query("""
        SELECT COUNT(d)
        FROM Doctor d
        WHERE d.hospital.id = :hospitalId
        """)
    Long countDoctorsByHospitalId(@Param("hospitalId") Long hospitalId);

    // Query to fetch active doctors
    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.isActive='Y'")
    Long countActiveDoctors();

    /* Query used to fetch specialization distribution for dashboard pie chart
     It counts number of doctors in each specialization */
    @Query("""
    SELECT new nimblix.in.HealthCareHub.response.SpecializationDistributionResponse(
    s.name,
    COUNT(d.id)
    )
    FROM Doctor d
    JOIN d.specialization s
    GROUP BY s.name
    ORDER BY COUNT(d.id) DESC
    """)
    List<SpecializationDistributionResponse> getSpecializationDistribution();

    Optional<Doctor> findByPhone(String phone);

    // Query used to map doctor entity to response DTO
    @Query("""
        SELECT new nimblix.in.HealthCareHub.response.DoctorProfileResponse(
        d.id,
        d.name,
        d.emailId,
        d.phone,
        d.qualification,
        d.experienceYears,
        s.id,
        s.name,
        h.id,
        h.name,
        h.address,
        h.city,
        h.state,
        h.phone,
        h.email,
        h.totalBeds
        )
        FROM Doctor d
        JOIN d.specialization s
        JOIN d.hospital h
        WHERE d.id = :doctorId
    """)
    DoctorProfileResponse getDoctorProfile(Long doctorId);

    // insert availability
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO doctor_availability (doctor_id, available_date, start_time, end_time, is_available) " +
            "VALUES (:doctorId, :availableDate, :startTime, :endTime, :isAvailable)",
            nativeQuery = true)
    void insertAvailability(@Param("doctorId") Long doctorId,
                            @Param("availableDate") String availableDate,
                            @Param("startTime") String startTime,
                            @Param("endTime") String endTime,
                            @Param("isAvailable") boolean isAvailable);

    // get all slots for a doctor
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor.id = :doctorId")
    List<DoctorAvailability> findAvailabilityByDoctorId(@Param("doctorId") Long doctorId);

    // check if doctor is available today
    @Query("SELECT COUNT(da) > 0 FROM DoctorAvailability da " +
            "WHERE da.doctor.id = :doctorId AND da.availableDate = :today AND da.isAvailable = true")
    boolean isDoctorAvailableToday(@Param("doctorId") Long doctorId,
                                   @Param("today") String today);
}