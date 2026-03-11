package nimblix.in.HealthCareHub.repository;

import nimblix.in.HealthCareHub.model.DoctorAvailability;
import nimblix.in.HealthCareHub.response.DoctorAvailabilityResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    // For ADD
    boolean existsByDoctor_IdAndAvailableDateAndStartTime(
            Long doctorId,
            String availableDate,
            String startTime
    );
    @Query("""
    SELECT COUNT(d) > 0 FROM DoctorAvailability d
        WHERE d.doctor.id = :doctorId
        AND d.availableDate = :availableDate
        AND (
                (:startTime < d.endTime AND :endTime > d.startTime)
    ) """)
    boolean existsOverlappingSlot(
            @Param("doctorId") Long doctorId,
            @Param("availableDate") String availableDate,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );
    // For UPDATE
    boolean existsByDoctor_IdAndAvailableDateAndStartTimeAndIdNot(
            Long doctorId,
            String availableDate,
            String startTime,
            Long id
    );
    @Query("""
        SELECT COUNT(d) > 0 FROM DoctorAvailability d
        WHERE d.doctor.id = :doctorId
        AND d.availableDate = :availableDate
        AND d.id <> :slotId
        AND (
                (:startTime < d.endTime AND :endTime > d.startTime)
        )
    """)
        //overlapping
    boolean existsOverlappingSlotForUpdate(
            @Param("doctorId") Long doctorId,
            @Param("availableDate") String availableDate,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("slotId") Long slotId
    );
    @Query("""
       SELECT new nimblix.in.HealthCareHub.response.DoctorAvailabilityResponse(
           d.id,
           d.doctor.id,
           d.doctor.name,
           d.availableDate,
           d.startTime,
           d.endTime,
           d.isAvailable
       )
       FROM DoctorAvailability d
       WHERE d.id = :slotId
    """)
    DoctorAvailabilityResponse getSlotResponseById(@Param("slotId") Long slotId);

    List<DoctorAvailability> findByIsAvailableTrue();
}
