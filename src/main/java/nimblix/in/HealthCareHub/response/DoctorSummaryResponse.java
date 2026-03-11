package nimblix.in.HealthCareHub.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorSummaryResponse {

    private Long doctorId;
    private String doctorName;
    private String specialization;
    private Long experienceYears;

}