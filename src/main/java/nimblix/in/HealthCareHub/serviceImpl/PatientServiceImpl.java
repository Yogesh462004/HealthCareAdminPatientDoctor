package nimblix.in.HealthCareHub.serviceImpl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import nimblix.in.HealthCareHub.model.Hospital;
import nimblix.in.HealthCareHub.model.Patient;
import nimblix.in.HealthCareHub.model.Role;
import nimblix.in.HealthCareHub.model.User;
import nimblix.in.HealthCareHub.repository.HospitalRepository;
import nimblix.in.HealthCareHub.repository.UserRepository;
import nimblix.in.HealthCareHub.request.PatientRegistrationRequest;
import nimblix.in.HealthCareHub.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Patient registerPatient(PatientRegistrationRequest request) {

        // 1. Check email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 2. Check password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and Confirm Password do not match");
        }

        // 3. Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.PATIENT);
        user.setEnabled(true);
        userRepository.save(user);

        // 4. Create Patient entity
        Patient patient = Patient.builder()
                .name(request.getFirstName() + " " + request.getLastName())
                .age(request.getAge())
                .gender(request.getGender())
                .phone(request.getPhone())
                .disease(request.getDisease())
                .admissionDate(request.getAdmissionDate() != null ? LocalDate.parse(request.getAdmissionDate()) : null)
                .dischargeDate(request.getDischargeDate() != null ? LocalDate.parse(request.getDischargeDate()) : null)
                .surgeryRequired(request.getSurgeryRequired())
                .emergencyCase(request.getEmergencyCase())
                .user(user)
                .hospital(request.getHospitalId() != null
                        ? hospitalRepository.findById(request.getHospitalId())
                        .orElseThrow(() -> new RuntimeException("Hospital not found"))
                        : null)
                .build();

        entityManager.persist(patient);
        return new PatientRegistrationResponse(true, "Registration successful");
    }

    @Override
    public PrescriptionResponse<Prescription> getPrescription(Long id) {
        Optional<Prescription> op = prescriptionRepository.findById(id);
        Prescription pr = op.get();
        if (op.isPresent()) {
            PrescriptionResponse<Prescription> response = new PrescriptionResponse<Prescription>(HealthCareConstants.STATUS_SUCCESS, HealthCareConstants.FETCHED_SUCCESSFULY, pr);
            return response;
        } else {
            PrescriptionResponse<Prescription> response = new PrescriptionResponse<Prescription>(HealthCareConstants.STATUS_FAILURE, HealthCareConstants.FETCH_FAILED, null);
            return response;
        }
    }

    @Override
    public PrescriptionMedicineResponse<PrescriptionMedicines> getPrescriptionMedicines(Long prescription_id) {
        List<PrescriptionMedicines> prescriptions = prescriptionMedicinesRepository.findByPrescriptionId(prescription_id);

        if (!prescriptions.isEmpty()) {
            PrescriptionMedicineResponse<PrescriptionMedicines> response = new PrescriptionMedicineResponse<>(HealthCareConstants.STATUS_SUCCESS, HealthCareConstants.FETCHED_SUCCESSFULY, prescriptions);
            return response;
        } else {
            PrescriptionMedicineResponse<PrescriptionMedicines> response = new PrescriptionMedicineResponse<>(HealthCareConstants.STATUS_FAILURE, HealthCareConstants.FETCH_FAILED, null);
            return response;
        }
    }

//    @Override
//    public String softDeletePatient(Long id) {
//        Patient patient = patientRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Patient not found"));
//
////        patient.setDeleted();   //  Mark as deleted
//        patientRepository.save(patient);
//
//        return "Patient soft deleted successfully";
//    }

public boolean softDeletePatient(Long id) {

    Optional<Patient> optionalPatient = patientRepository.findById(id);

    if(optionalPatient.isPresent()) {

        Patient patient = optionalPatient.get();
        patient.setDeleted(true);
        patientRepository.save(patient);

        return true;

    } else {
        return false;
    }
}

        return patient;
    }

    @Override
    public Review addDoctorReview(Long patientId, Long doctorId, String comment, int rating) {
//        Patient patient = patientRepository.findById(patientId)
//                .orElseThrow(() -> new RuntimeException("Patient not found"));
//        Doctor doctor = doctorRepository.findById(doctorId)
//                .orElseThrow(() -> new RuntimeException("Doctor not found"));
//        Review review = Review.builder()
//                .patient(patient)
//                .doctor(doctor)
//                .comment(comment)
//                .rating(rating)
//                .build();
//        // Add review to doctor's review list
//        if (doctor.getReviews() == null) {
//            doctor.setReviews(new ArrayList<>());
//        }
//        doctor.getReviews().add(review);
//        // Saving the doctor will also save the review due to cascade
//        doctorRepository.save(doctor);
//        return review;
        return null;
    }

    @Override
    public List<Review> getDoctorReviews(Long doctorId) {
//        Doctor doctor = doctorRepository.findById(doctorId)
//                .orElseThrow(() -> new RuntimeException("Doctor not found"));
//        if (doctor.getReviews() == null) {
//            return new ArrayList<>();
//        }
//        return doctor.getReviews();
        return null;
    }

    @Override
    public Review addPatientReview(Long doctorId, Long patientId, String comment, int rating) {
//        Doctor doctor = doctorRepository.findById(doctorId)
//                .orElseThrow(() -> new RuntimeException("Doctor not found"));
//        Patient patient = patientRepository.findById(patientId)
//                .orElseThrow(() -> new RuntimeException("Patient not found"));
//        Review review = Review.builder()
//                .doctor(doctor)
//                .patient(patient)
//                .comment(comment)
//                .rating(rating)
//                .build();
//
//        // Add review to patient's review list
//        if (patient.getReviews() == null) {
//            patient.setReviews(new ArrayList<>());
//        }

//        patient.getReviews().add(review);
//        // Saving the patient will also save the review due to cascade
//        patientRepository.save(patient);
//        return review;
        return null;
    }

    @Override
    public List<Review> getPatientReviews(Long patientId) {
//        Patient patient = patientRepository.findById(patientId)
//                                .orElseThrow(() -> new RuntimeException("Patient not found"));
//        if (patient.getReviews() == null) {
//            return new ArrayList<>();
//        }
//        return patient.getReviews();
        return null;
    }

    @Override
    public List<Patient> filterPatientsByDay(int day) {
        return patientRepository.findPatientsByDay(day);
    }

    @Override
    public List<Patient> filterPatientsByMonth(int month) {
        return patientRepository.findPatientsByMonth(month);
    }

    @Override
    public List<Patient> filterPatientsByYear(int year) {
        return patientRepository.findPatientsByYear(year);
    }


}