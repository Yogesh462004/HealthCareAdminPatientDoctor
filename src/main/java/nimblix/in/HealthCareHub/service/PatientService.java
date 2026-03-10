package nimblix.in.HealthCareHub.service;

import nimblix.in.HealthCareHub.model.Patient;
import nimblix.in.HealthCareHub.model.Prescription;
import nimblix.in.HealthCareHub.model.PrescriptionMedicines;
import nimblix.in.HealthCareHub.model.Review;
import nimblix.in.HealthCareHub.request.PatientRegistrationRequest;
import nimblix.in.HealthCareHub.response.PatientRegistrationResponse;
import nimblix.in.HealthCareHub.response.PrescriptionMedicineResponse;
import nimblix.in.HealthCareHub.response.PrescriptionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PatientService {
    PrescriptionResponse<Prescription> getPrescription(Long id);
    PatientRegistrationResponse registerPatient(PatientRegistrationRequest request);
    PrescriptionMedicineResponse<PrescriptionMedicines> getPrescriptionMedicines(Long prescription_id);
    Patient savePatient(Patient patient);
   // String softDeletePatient(Long id);
    Review addDoctorReview(Long patientId, Long doctorId, String comment, int rating);
    List<Review> getDoctorReviews(Long doctorId);
    Review addPatientReview(Long doctorId, Long patientId, String comment, int rating);
    List<Review> getPatientReviews(Long patientId);
    List<Patient> filterPatientsByDay(int day);
    List<Patient> filterPatientsByMonth(int month);
    List<Patient> filterPatientsByYear(int year);
}

