package nimblix.in.HealthCareHub.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorAddRequest {

    @NotBlank
    private String doctorName;

    @Email
    private String doctorEmail;

    @NotBlank
    private String password;
    private String qualification;
    private Long experience;
    private String description;

    @NotBlank
    private String phoneNo;
    private Long doctorId;
    private Long hospitalId;
    private Long specializationId;
    private String specializationName;
    private Double consultationFee;
}
