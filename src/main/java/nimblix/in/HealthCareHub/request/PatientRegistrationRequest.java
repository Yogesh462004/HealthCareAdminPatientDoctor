package nimblix.in.HealthCareHub.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientRegistrationRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Gender is required")
    private String gender;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    // Optional patient fields
    private Integer age;
    private String phone;
    private String disease;
    private String admissionDate; // format: YYYY-MM-DD
    private String dischargeDate; // format: YYYY-MM-DD
    private Boolean surgeryRequired;
    private Boolean emergencyCase;
    private Long hospitalId;
}