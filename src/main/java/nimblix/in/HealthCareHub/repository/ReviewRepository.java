package nimblix.in.HealthCareHub.repository;

import nimblix.in.HealthCareHub.model.Review;
import nimblix.in.HealthCareHub.response.DoctorReviewResponse;
import nimblix.in.HealthCareHub.response.ReviewResponse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Get doctor rating statistics (average rating + total reviews)
    @Query("""
        SELECT new nimblix.in.HealthCareHub.response.DoctorReviewResponse(
            COALESCE(AVG(r.rating),0),
            COUNT(r)
        )
        FROM Review r
        WHERE r.doctor.id = :doctorId
    """)
    DoctorReviewResponse findReviewStatsByDoctorId(@Param("doctorId") Long doctorId);


    // Get patient reviews by hospital
    @Query("""
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

}
