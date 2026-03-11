package nimblix.in.HealthCareHub.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyActivityResponse {

    private String day;
    private Long admissions;
    private Long discharges;
    private Long surgeries;

}