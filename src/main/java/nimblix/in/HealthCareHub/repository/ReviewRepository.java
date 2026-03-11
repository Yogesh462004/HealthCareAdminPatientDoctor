package nimblix.in.HealthCareHub.repository;

import nimblix.in.HealthCareHub.model.Review;
import nimblix.in.HealthCareHub.response.DoctorReviewResponse;
import nimblix.in.HealthCareHub.response.ReviewResponse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("""
        SELECT new nimblix.in.HealthCareHub.response.DoctorReviewResponse(
            COALESCE(AVG(r.rating), 0),
            COUNT(r)
        )
        FROM Review r
        WHERE r.doctor.id = :doctorId
    """)
    DoctorReviewResponse findReviewStatsByDoctorId(@Param("doctorId") Long doctorId);


    @Query("""
        SELECT new nimblix.in.HealthCareHub.response.ReviewResponse(
            SELECT new nimblix.in.HealthCareHub.response.ReviewResponse(
            r.id,
            r.patient.name,
            r.rating,
            r.comment,
            r.createdTime
        )
        FROM Review r
        WHERE r.patient.hospital.id = :hospitalId
    """)
    List<ReviewResponse> findPatientReviewsByHospitalId(@Param("hospitalId") Long hospitalId);
           )
            FROM Review r
            WHERE r.patient.hospital.id = :hospitalId
           """)
    List<ReviewResponse>findPatientReviewsByHospitalId( @Param("hospitalId") Long hospitalId);


}