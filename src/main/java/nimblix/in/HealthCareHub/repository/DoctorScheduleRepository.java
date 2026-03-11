package nimblix.in.HealthCareHub.repository;

import nimblix.in.HealthCareHub.model.DoctorSchedule;
import nimblix.in.HealthCareHub.response.DoctorScheduleResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    boolean existsByDoctor_IdAndPatient_IdAndOperationDate(
            Long doctorId,
            Long patientId,
            String operationDate
    );

    @Query("""
        SELECT new nimblix.in.HealthCareHub.response.DoctorScheduleResponse(
            s.id,
            s.doctor.id,
            s.patient.id,
            s.operationName,
            s.operationDate,
            s.status
        )
        FROM DoctorSchedule s
        WHERE s.doctor.id = :doctorId
    """)
    List<DoctorScheduleResponse> findSchedulesByDoctorId(@Param("doctorId") Long doctorId);

}
