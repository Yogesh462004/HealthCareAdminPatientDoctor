package nimblix.in.HealthCareHub.model;

import jakarta.persistence.*;
import lombok.*;
import nimblix.in.HealthCareHub.constants.HealthCareConstants;
import nimblix.in.HealthCareHub.utility.HealthCareUtil;

@Entity
@Table(name = "doctor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Long experienceYears;

    private String phone;

    @Column(unique = true)
    private String emailId;

    private String description;

    private String password;

    private String qualification;
    private Double consultationFee;

    // ✅ Doctor login account
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // ✅ Many Doctors → One Hospital
    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Column(name = "is_active")
    private  String isActive;

    // ✅ Many Doctors → One Specialization
    @ManyToOne
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;

    //  Real-time status — uses HealthCareConstants (AVAILABLE, IN_OPERATION, ON_BREAK ...)
    @Column(name = "doctor_status", nullable = false)
    private String doctorStatus = HealthCareConstants.DOCTOR_STATUS_AVAILABLE;

    private String createdTime;
    private String updatedTime;

    @PrePersist
    protected void onCreate(){
        createdTime = HealthCareUtil.changeCurrentTimeToLocalDateFromGmtToISTInString();
        updatedTime = createdTime;
        if (doctorStatus == null) {
            doctorStatus = HealthCareConstants.DOCTOR_STATUS_AVAILABLE;
        }
    }

    @PreUpdate
    protected void onUpdate(){
        updatedTime = HealthCareUtil.changeCurrentTimeToLocalDateFromGmtToISTInString();
    }
}