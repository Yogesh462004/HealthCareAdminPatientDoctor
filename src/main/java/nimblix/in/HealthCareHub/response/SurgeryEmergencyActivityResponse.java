package nimblix.in.HealthCareHub.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurgeryEmergencyActivityResponse {

    private String date;
    private Long surgeries;
    private Long emergencies;

}