package nimblix.in.HealthCareHub.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryResponse {

    private Long totalBeds;
    private Long activeDoctors;
    private Long patientsServed;
    private Double averageRating;
}