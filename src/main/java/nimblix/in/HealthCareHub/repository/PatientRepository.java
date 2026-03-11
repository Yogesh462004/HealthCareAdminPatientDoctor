package nimblix.in.HealthCareHub.repository;

import nimblix.in.HealthCareHub.model.Patient;
import nimblix.in.HealthCareHub.response.AdmissionDischargeActivityResponse;
import nimblix.in.HealthCareHub.response.SurgeryEmergencyActivityResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient,Long> {
    // Get only active patients (isDeleted = false)
    // List<Patient> findByIsDeletedFalse();

    @Query("""
            SELECT COUNT(p) FROM Patient p
            WHERE p.hospital.id = :hospitalId
            """)
    Long countPatientsByHospitalId(@Param("hospitalId") Long hospitalId);

    // We use JPQL constructor projection to directly map results
    // to AdmissionDischargeActivityResponse DTO.
    @Query("""
                SELECT new nimblix.in.HealthCareHub.response.AdmissionDischargeActivityResponse(
                p.admissionDate,
                COUNT(p.id),
                SUM(CASE WHEN p.dischargeDate IS NOT NULL THEN 1 ELSE 0 END)
                )
                FROM Patient p
                WHERE p.admissionDate >= :startDate
                GROUP BY p.admissionDate
                ORDER BY p.admissionDate
            """)
    List<AdmissionDischargeActivityResponse> getAdmissionsDischargesLast14Days(
            @Param("startDate") LocalDate startDate);
/*
     Query used to fetch admissions and discharges activity for the last 14 days
     This query aggregates patient data to generate dashboard chart data
     We calculate:
     - Number of admissions per day
     - Number of discharges per day
     - Only for last 14 days */

    /*Query used to fetch surgeries and emergency cases for dashboard bar chart
    Aggregates patient records for the last 14 days */
    @Query("""
                SELECT new nimblix.in.HealthCareHub.response.SurgeryEmergencyActivityResponse(
                p.admissionDate,
                SUM(CASE WHEN p.surgeryRequired = true THEN 1 ELSE 0 END),
                SUM(CASE WHEN p.emergencyCase = true THEN 1 ELSE 0 END)
                )
                FROM Patient p
                WHERE p.admissionDate >= :startDate
                GROUP BY p.admissionDate
                ORDER BY p.admissionDate
            """)
    List<SurgeryEmergencyActivityResponse> getSurgeriesEmergenciesLast14Days(
            @Param("startDate") LocalDate startDate);

    // Patient Filter (Day / Month / Year)

    @Query(value = "SELECT * FROM patient WHERE DAY(admission_date) = :day", nativeQuery = true)
    List<Patient> findPatientsByDay(@Param("day") int day);

    @Query(value = "SELECT * FROM patient WHERE MONTH(admission_date) = :month", nativeQuery = true)
    List<Patient> findPatientsByMonth(@Param("month") int month);

    @Query(value = "SELECT * FROM patient WHERE YEAR(admission_date) = :year", nativeQuery = true)
    List<Patient> findPatientsByYear(@Param("year") int year);
}
