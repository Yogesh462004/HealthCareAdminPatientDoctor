package nimblix.in.HealthCareHub.repository;


import nimblix.in.HealthCareHub.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query(value = """
        SELECT DATE(appointment_date_time), COUNT(*)
        FROM appointments
        WHERE DATE(appointment_date_time) BETWEEN :startDate AND :endDate
        GROUP BY DATE(appointment_date_time)
        """, nativeQuery = true)
    List<Object[]> getWeeklyAdmissions(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}