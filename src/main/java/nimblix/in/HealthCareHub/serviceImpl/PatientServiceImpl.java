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
    private HospitalRepository hospitalRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public PatientRegistrationResponse registerPatient(PatientRegistrationRequest request) {

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

        User user = new User();
        user.setEmail(request.getEmail());
        user.setRole(Role.PATIENT);
        user.setEnabled(true);

        userRepository.save(user);

        Patient patient = new Patient();
        patient.setName(request.getFirstName() + " " + request.getLastName());
        patient.setGender(request.getGender());
        patient.setUser(user);

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

        List<PrescriptionMedicines> prescriptions =
                prescriptionMedicinesRepository.findByPrescriptionId(prescriptionId);

        if (!prescriptions.isEmpty()) {
            return new PrescriptionMedicineResponse<>(
                    HealthCareConstants.STATUS_SUCCESS,
                    HealthCareConstants.FETCHED_SUCCESSFULY,
                    prescriptions);
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
    public String softDeletePatient(Long id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        patient.setIsDeleted(true);


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



}