package nimblix.in.HealthCareHub.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorRegistrationResponse {

    private Long doctorId;
    private String doctorName;
    private String doctorEmail;
    private Double consultationFee;
    private String specialization;
    private String hospitalName;
    private Long hospitalId;
    private String qualification;
    private Long experienceYears;
    private String message;

}
