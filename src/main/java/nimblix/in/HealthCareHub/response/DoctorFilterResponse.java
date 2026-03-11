package nimblix.in.HealthCareHub.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorFilterResponse {

    private Long doctorId;
    private String doctorName;
    private String specialization;
    private String hospitalName;
    private Long experienceYears;

}