package nimblix.in.HealthCareHub.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HospitalRegistrationRequest {

    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String phone;
    private String email;
    private String password;
    private String confirmPassword;
    private Integer establishedYear;
    private Integer totalBeds;
    private List<String> specializations;
    private List<String> doctors;
    private String aboutHospital;


    private List<Room> rooms;

    @Getter
    @Setter
    public static class Room {
        private String roomNumber;
        private String roomType;
        private boolean available;
    }
}