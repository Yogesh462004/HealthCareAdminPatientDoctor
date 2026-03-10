package nimblix.in.HealthCareHub.model;

import jakarta.persistence.*;
import lombok.*;
import nimblix.in.HealthCareHub.utility.HealthCareUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer age;

    private String gender;

    private String phone;

    private String disease;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @Column(name = "discharge_date")
    private LocalDate dischargeDate;

    @Column(name = "surgery_required")
    private Boolean surgeryRequired;

    @Column(name = "emergency_case")
    private Boolean emergencyCase;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews;

    private String createdTime;

    private String updatedTime;

    @PrePersist
    protected void onCreate(){
        createdTime = HealthCareUtil.changeCurrentTimeToLocalDateFromGmtToISTInString();
        updatedTime = createdTime;
    }

    @PreUpdate
    protected void onUpdate(){
        updatedTime = HealthCareUtil.changeCurrentTimeToLocalDateFromGmtToISTInString();
    }
}
