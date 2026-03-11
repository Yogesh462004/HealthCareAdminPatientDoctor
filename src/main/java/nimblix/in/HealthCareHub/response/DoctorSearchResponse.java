package nimblix.in.HealthCareHub.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DoctorSearchResponse {

    private Long doctorId;
    private String doctorName;
    private String specialization;
    private Long experienceYears;
    private String hospitalName;

}