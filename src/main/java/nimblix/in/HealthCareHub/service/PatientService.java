package nimblix.in.HealthCareHub.service;

import nimblix.in.HealthCareHub.model.Patient;
import nimblix.in.HealthCareHub.model.Prescription;
import nimblix.in.HealthCareHub.model.PrescriptionMedicines;
import nimblix.in.HealthCareHub.model.Review;
import nimblix.in.HealthCareHub.request.PatientRegistrationRequest;
import nimblix.in.HealthCareHub.response.PatientRegistrationResponse;
import nimblix.in.HealthCareHub.response.PrescriptionMedicineResponse;
import nimblix.in.HealthCareHub.response.PrescriptionResponse;

import java.util.List;

public interface PatientService {

    // Patient Registration
    PatientRegistrationResponse registerPatient(PatientRegistrationRequest request);

    // Prescription APIs
    PrescriptionResponse<Prescription> getPrescription(Long id);

    PrescriptionMedicineResponse<PrescriptionMedicines> getPrescriptionMedicines(Long prescriptionId);

    // Patient Management
    Patient savePatient(Patient patient);


    String softDeletePatient(Long id);

    // Doctor Review APIs

   // String softDeletePatient(Long id);

    Review addDoctorReview(Long patientId, Long doctorId, String comment, int rating);

    List<Review> getDoctorReviews(Long doctorId);

    // Patient Review by Doctor
    Review addPatientReview(Long doctorId, Long patientId, String comment, int rating);

    List<Review> getPatientReviews(Long patientId);

    List<Patient> filterPatientsByDay(int day);
    List<Patient> filterPatientsByMonth(int month);
    List<Patient> filterPatientsByYear(int year);
}


    Review addHospitalReview(Long patientId, Long hospitalId, String comment, int rating);

}
