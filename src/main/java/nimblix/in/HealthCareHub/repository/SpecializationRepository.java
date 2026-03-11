package nimblix.in.HealthCareHub.repository;

import nimblix.in.HealthCareHub.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    Optional<Specialization> findByName(String name);

    boolean existsByNameIgnoreCase(String name);



}
