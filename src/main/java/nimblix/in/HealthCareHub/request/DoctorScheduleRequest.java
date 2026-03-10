package nimblix.in.HealthCareHub.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DoctorScheduleRequest {

    private Long patientId;
    private String operationName;
    private String operationDate;
    private String status;

}
