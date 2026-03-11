package nimblix.in.HealthCareHub.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdmissionDischargeActivityResponse {

    private LocalDate date;
    private Long admissions;
    private Long discharges;

}