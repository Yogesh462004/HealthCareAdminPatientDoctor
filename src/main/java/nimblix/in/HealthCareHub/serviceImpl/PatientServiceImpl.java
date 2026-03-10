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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import nimblix.in.HealthCareHub.response.ApiResponse;

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
    public ApiResponse forgotPassword(String phoneNumber, String email) {

        ApiResponse response = new ApiResponse();

        if(phoneNumber == null && email == null){
            response.setStatus("200");
            response.setMessage("Phone number or email required");
            return response;
        }

        Optional<User> userOptional;

        if(phoneNumber != null){
            userOptional = userRepository.findByPhoneNumber(phoneNumber);
        } else {
            userOptional = userRepository.findByEmail(email);
        }

        if(!userOptional.isPresent()){
            response.setStatus("404");
            response.setMessage("User not found");
            return response;
        }

        response.setStatus("200");
        response.setMessage("User verified. You can reset password.");

        return response;
    }

    @Override
    public ApiResponse resetPassword(String phoneNumber, String email, String newPassword) {

        ApiResponse response = new ApiResponse();

        if(newPassword == null || newPassword.isEmpty()){
            response.setStatus("200");
            response.setMessage("New password required");
            return response;
        }

        Optional<User> userOptional;

        if(phoneNumber != null){
            userOptional = userRepository.findByPhoneNumber(phoneNumber);
        } else {
            userOptional = userRepository.findByEmail(email);
        }

        if(!userOptional.isPresent()){
            response.setStatus("404");
            response.setMessage("User not found");
            return response;
        }

        User user = userOptional.get();
        user.setPassword(newPassword);

        userRepository.save(user);

        response.setStatus("200");
        response.setMessage("Password reset successfully");

        return response;
    }

}