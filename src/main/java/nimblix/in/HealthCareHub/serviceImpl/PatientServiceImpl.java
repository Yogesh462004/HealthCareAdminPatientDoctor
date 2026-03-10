package nimblix.in.HealthCareHub.serviceImpl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import nimblix.in.HealthCareHub.constants.HealthCareConstants;
import nimblix.in.HealthCareHub.response.ApiResponse;
import nimblix.in.HealthCareHub.model.*;
import nimblix.in.HealthCareHub.repository.*;
import nimblix.in.HealthCareHub.request.PatientRegistrationRequest;
import nimblix.in.HealthCareHub.response.PatientRegistrationResponse;
import nimblix.in.HealthCareHub.response.PrescriptionMedicineResponse;
import nimblix.in.HealthCareHub.response.PrescriptionResponse;
import nimblix.in.HealthCareHub.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PrescriptionMedicineRepository prescriptionMedicinesRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public PatientRegistrationResponse registerPatient(PatientRegistrationRequest request) {
        // Check if email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new PatientRegistrationResponse(false, "Email already exists");
        }

        // Check password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return new PatientRegistrationResponse(false, "Password and Confirm Password do not match");
        }

        // Create User
        User user = new User();
        user.setEmail(request.getEmail());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(nimblix.in.HealthCareHub.model.Role.PATIENT);
        user.setEnabled(true); // required for login

        userRepository.save(user);

        // Create Patient linked to User
        Patient patient = new Patient();
        patient.setName(request.getFirstName() + " " + request.getLastName());
        patient.setGender(request.getGender());
        patient.setUser(user);

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

    @Override
    public boolean softDeletePatient(Long id) {

        Optional<Patient> optionalPatient = patientRepository.findById(id);

        if(optionalPatient.isPresent()) {

            Patient patient = optionalPatient.get();

            patient.setDeleted(true);   // soft delete flag

            patientRepository.save(patient);

            return true;
        }

        return false;
    }

    @Override
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
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

    @Override
    public ApiResponse forgotPassword(String phoneNumber, String email) {

        ApiResponse response = new ApiResponse();

        Optional<User> userOptional;

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            userOptional = userRepository.findByPhoneNumber(phoneNumber);
        } else if (email != null && !email.isEmpty()) {
            userOptional = userRepository.findByEmail(email);
        } else {
            response.setStatus("FAILURE");
            response.setMessage("Phone number or email required");
            return response;
        }

        if (!userOptional.isPresent()) {
            response.setStatus("FAILURE");
            response.setMessage("User not found");
            return response;
        }

        response.setStatus("SUCCESS");
        response.setMessage("User verified. You can reset password.");

        return response;
    }
    @Override
    public ApiResponse resetPassword(String phoneNumber, String email, String newPassword) {

        ApiResponse response = new ApiResponse();

        // Check password
        if (newPassword == null || newPassword.isEmpty()) {
            response.setStatus("FAILURE");
            response.setMessage("New password required");
            return response;
        }

        Optional<User> userOptional;

        // Check user by phone or email
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            userOptional = userRepository.findByPhoneNumber(phoneNumber);
        } else if (email != null && !email.isEmpty()) {
            userOptional = userRepository.findByEmail(email);
        } else {
            response.setStatus("FAILURE");
            response.setMessage("Phone number or email required");
            return response;
        }

        // User not found
        if (!userOptional.isPresent()) {
            response.setStatus("FAILURE");
            response.setMessage("User not found");
            return response;
        }

        // Update password
        User user = userOptional.get();
        user.setPassword(newPassword);

        userRepository.save(user);

        response.setStatus("SUCCESS");
        response.setMessage("Password reset successfully");

        return response;
    }
    @Override
    public void updatePatient(Long patientId, PatientRegistrationRequest request) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        patient.setName(request.getFirstName() + " " + request.getLastName());
        patient.setGender(request.getGender());

        patientRepository.save(patient);
    }

}