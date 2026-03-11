package nimblix.in.HealthCareHub.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewResponse {

    private Long id;

    private String patientName;

    private Integer rating;

    private String comment;

    private String createdTime;
}