package nimblix.in.HealthCareHub.serviceImpl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import nimblix.in.HealthCareHub.constants.HealthCareConstants;
import nimblix.in.HealthCareHub.model.*;
import nimblix.in.HealthCareHub.repository.*;
import nimblix.in.HealthCareHub.request.PatientRegistrationRequest;
import nimblix.in.HealthCareHub.response.ApiResponse;
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
    private HospitalRepository hospitalRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public PatientRegistrationResponse registerPatient(PatientRegistrationRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new PatientRegistrationResponse(false, "Email already exists");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return new PatientRegistrationResponse(false, "Password and Confirm Password do not match");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(Role.PATIENT);
        user.setEnabled(true);

        userRepository.save(user);

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

        if (op.isPresent()) {
            return new PrescriptionResponse<>(
                    HealthCareConstants.STATUS_SUCCESS,
                    HealthCareConstants.FETCHED_SUCCESSFULY,
                    op.get());
        } else {
            return new PrescriptionResponse<>(
                    HealthCareConstants.STATUS_FAILURE,
                    HealthCareConstants.FETCH_FAILED,
                    null);
        }
    }

    @Override
    public PrescriptionMedicineResponse<PrescriptionMedicines> getPrescriptionMedicines(Long prescriptionId) {

        List<PrescriptionMedicines> medicines =
                prescriptionMedicinesRepository.findByPrescriptionId(prescriptionId);

        if (!medicines.isEmpty()) {
            return new PrescriptionMedicineResponse<>(
                    HealthCareConstants.STATUS_SUCCESS,
                    HealthCareConstants.FETCHED_SUCCESSFULY,
                    medicines);
        } else {
            return new PrescriptionMedicineResponse<>(
                    HealthCareConstants.STATUS_FAILURE,
                    HealthCareConstants.FETCH_FAILED,
                    null);
        }
    }

    @Override
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public boolean softDeletePatient(Long id) {

        Optional<Patient> optionalPatient = patientRepository.findById(id);

        if (optionalPatient.isPresent()) {

            Patient patient = optionalPatient.get();
            patient.setIsDeleted(true);

            patientRepository.save(patient);

            return true;
        }

        return false;
    }

    @Override
    public Review addDoctorReview(Long patientId, Long doctorId, String comment, int rating) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Review review = Review.builder()
                .patient(patient)
                .doctor(doctor)
                .comment(comment)
                .rating(rating)
                .build();

        if (doctor.getReviews() == null) {
            doctor.setReviews(new ArrayList<>());
        }

        doctor.getReviews().add(review);

        doctorRepository.save(doctor);

        return review;
    }

    @Override
    public List<Review> getDoctorReviews(Long doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (doctor.getReviews() == null) {
            return new ArrayList<>();
        }

        return doctor.getReviews();
    }

    @Override
    public Review addHospitalReview(Long patientId, Long hospitalId, String comment, int rating) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        Review review = Review.builder()
                .patient(patient)
                .hospital(hospital)
                .comment(comment)
                .rating(rating)
                .build();

        if (hospital.getReviews() == null) {
            hospital.setReviews(new ArrayList<>());
        }

        hospital.getReviews().add(review);

        hospitalRepository.save(hospital);

        return review;
    }

    @Override
    public Review addPatientReview(Long doctorId, Long patientId, String comment, int rating) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Review review = Review.builder()
                .doctor(doctor)
                .patient(patient)
                .comment(comment)
                .rating(rating)
                .build();

        if (patient.getReviews() == null) {
            patient.setReviews(new ArrayList<>());
        }

        patient.getReviews().add(review);

        patientRepository.save(patient);

        return review;
    }

    @Override
    public List<Review> getPatientReviews(Long patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (patient.getReviews() == null) {
            return new ArrayList<>();
        }

        return patient.getReviews();
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

        if (newPassword == null || newPassword.isEmpty()) {
            response.setStatus("FAILURE");
            response.setMessage("New password required");
            return response;
        }

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

        User user = userOptional.get();
        user.setPassword(newPassword);

        userRepository.save(user);

        response.setStatus("SUCCESS");
        response.setMessage("Password reset successfully");

        return response;
    }
}