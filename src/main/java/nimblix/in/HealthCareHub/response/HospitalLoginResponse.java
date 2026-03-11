package nimblix.in.HealthCareHub.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HospitalLoginResponse {

    private String status;
    private String message;
    private String token;
}