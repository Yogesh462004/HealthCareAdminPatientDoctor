package nimblix.in.HealthCareHub.service;

import nimblix.in.HealthCareHub.model.Patient;
import nimblix.in.HealthCareHub.response.ApiResponse;
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

    boolean softDeletePatient(Long id);
    PrescriptionMedicineResponse<PrescriptionMedicines> getPrescriptionMedicines(Long prescription_id);
    Patient savePatient(Patient patient);

    String softDeletePatient(Long id);


    Review addDoctorReview(Long patientId, Long doctorId, String comment, int rating);

    List<Review> getDoctorReviews(Long doctorId);


    Review addPatientReview(Long doctorId, Long patientId, String comment, int rating);

    List<Review> getPatientReviews(Long patientId);


    Review addHospitalReview(Long patientId, Long hospitalId, String comment, int rating);


    List<Patient> filterPatientsByDay(int day);

    List<Patient> filterPatientsByMonth(int month);
    List<Patient> filterPatientsByYear(int year);
    ApiResponse forgotPassword(String phoneNumber, String email);

    ApiResponse resetPassword(String phoneNumber, String email, String newPassword);
}

    List<Patient> filterPatientsByYear(int year);
}