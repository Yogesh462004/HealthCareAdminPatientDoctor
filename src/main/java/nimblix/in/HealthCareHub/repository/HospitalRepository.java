package nimblix.in.HealthCareHub.repository;

import nimblix.in.HealthCareHub.model.Hospital;
import nimblix.in.HealthCareHub.response.DropdownResponse;
import nimblix.in.HealthCareHub.response.HospitalOverviewResponse;
import nimblix.in.HealthCareHub.response.HospitalResponse;
import nimblix.in.HealthCareHub.response.HospitalSpecializationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital,Long> {
    Optional<Hospital> findByName(String name);
    List<Hospital> findByNameContainingIgnoreCase(String name);

    Optional<Hospital> findByEmail(String email);

    Optional<Hospital> findByNameAndCityAndState(String name, String city, String state);

    // Query to fetch total beds
    @Query("SELECT COALESCE(SUM(h.totalBeds),0) FROM Hospital h")
    Long getTotalBeds();

    // Query to fetch average hospital rating
    @Query("SELECT COALESCE(AVG(h.rating),0) FROM Hospital h")
    Double getAverageRating();

   /* Query used to fetch hospital dropdown list for dashboard filter
   ORDER BY used to return hospitals in sequential order */

    @Query("""
SELECT new nimblix.in.HealthCareHub.response.DropdownResponse(
h.id,
h.name
)
FROM Hospital h
ORDER BY h.id ASC
""")
    List<DropdownResponse> getHospitalDropdownList();


    /*Query used to fetch hospital overview details for dashboard table
 We join hospital and doctor tables to calculate doctor count */
    @Query("""
        SELECT new nimblix.in.HealthCareHub.response.HospitalOverviewResponse(
            h.id,
            h.name,
            h.city,
            h.rating,
            COUNT(d.id),
            h.totalBeds,
            h.status
        )
        FROM Hospital h
        LEFT JOIN Doctor d ON d.hospital.id = h.id
        GROUP BY h.id
    """)
    List<HospitalOverviewResponse> getHospitalOverview();

    // Query used to fetch hospitals based on specialization
    @Query("""
        SELECT new nimblix.in.HealthCareHub.response.HospitalSpecializationResponse(
            h.id,
            h.name,
            CONCAT(h.city, ', ', h.state),
            s.name
        )
        FROM Hospital h
        JOIN Doctor d ON d.hospital.id = h.id
        JOIN Specialization s ON d.specialization.id = s.id
        WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :specialization, '%'))
    """)
    List<HospitalSpecializationResponse>
    findHospitalsBySpecialization(@Param("specialization") String specialization);


    @Query("""
    SELECT new nimblix.in.HealthCareHub.response.HospitalResponse(
    h.id,
    h.name,
    h.address,
    h.city,
    h.state,
    h.phone,
    h.email,
    h.totalBeds
    )
    FROM Hospital h
    """)
    List<HospitalResponse> getAllHospitals();

}
