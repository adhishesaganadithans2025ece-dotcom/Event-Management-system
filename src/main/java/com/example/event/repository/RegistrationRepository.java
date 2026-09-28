package com.example.event.repository;

import com.example.event.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByStudentId(Long studentId);
    List<Registration> findByEventId(Long eventId);
    Optional<Registration> findByStudentIdAndEventId(Long studentId, Long eventId);
    Optional<Registration> findByStudentIdAndEventIdAndStatus(Long studentId, Long eventId, String status);
    long countByEventIdAndStatus(Long eventId, String status);
}
