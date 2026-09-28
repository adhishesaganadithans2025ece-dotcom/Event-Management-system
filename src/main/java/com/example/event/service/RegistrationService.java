package com.example.event.service;

import com.example.event.entity.Event;
import com.example.event.entity.Registration;
import com.example.event.entity.Student;
import com.example.event.repository.EventRepository;
import com.example.event.repository.RegistrationRepository;
import com.example.event.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final StudentRepository studentRepository;
    private final EventRepository eventRepository;

    public RegistrationService(RegistrationRepository registrationRepository,
                                StudentRepository studentRepository,
                                EventRepository eventRepository) {
        this.registrationRepository = registrationRepository;
        this.studentRepository = studentRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Map<String, Object> registerStudentForEvent(Long studentId, Long eventId) {
        // Step 1: Find student
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));

        // Step 2: Find event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));

        // Step 3: Check event date
        if (event.getEventDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Registration is closed because the event has already taken place.");
        }

        // Step 4: Check existing registration
        Optional<Registration> existingOpt = registrationRepository.findByStudentIdAndEventId(studentId, eventId);
        if (existingOpt.isPresent()) {
            Registration existing = existingOpt.get();
            if ("REGISTERED".equalsIgnoreCase(existing.getStatus())) {
                throw new IllegalStateException("You are already registered for this event.");
            }
        }

        // Step 5 & 6: Count active REGISTERED registrations and check capacity
        long registeredSeats = registrationRepository.countByEventIdAndStatus(eventId, "REGISTERED");
        if (registeredSeats >= event.getMaxSeats()) {
            throw new IllegalStateException("Registration failed. This event is full.");
        }

        // Step 7: Create or reactive registration
        Registration registration;
        if (existingOpt.isPresent()) {
            registration = existingOpt.get();
            registration.setStatus("REGISTERED");
            registration.setRegistrationDate(LocalDateTime.now());
        } else {
            registration = new Registration();
            registration.setStudent(student);
            registration.setEvent(event);
            registration.setStatus("REGISTERED");
            registration.setRegistrationDate(LocalDateTime.now());
        }

        Registration saved = registrationRepository.save(registration);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful!");
        response.put("id", saved.getId());
        response.put("status", saved.getStatus());
        response.put("registrationDate", saved.getRegistrationDate());
        return response;
    }

    @Transactional
    public Map<String, Object> cancelRegistration(Long studentId, Long eventId) {
        // Find student
        studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));

        // Find event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));

        // Find active registration
        Registration registration = registrationRepository.findByStudentIdAndEventIdAndStatus(studentId, eventId, "REGISTERED")
                .orElseThrow(() -> new IllegalArgumentException("Active registration not found for this student and event."));

        // Check event date
        if (event.getEventDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Registration cannot be cancelled after the event date.");
        }

        // Change status REGISTERED -> CANCELLED
        registration.setStatus("CANCELLED");
        registrationRepository.save(registration);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration cancelled successfully.");
        return response;
    }

    public List<Map<String, Object>> getParticipantsForEvent(Long eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));

        List<Registration> registrations = registrationRepository.findByEventId(eventId);
        List<Map<String, Object>> participantList = new ArrayList<>();

        for (Registration reg : registrations) {
            Map<String, Object> map = new HashMap<>();
            map.put("registrationId", reg.getId());
            map.put("studentId", reg.getStudent().getId());
            map.put("studentName", reg.getStudent().getName());
            map.put("email", reg.getStudent().getEmail());
            map.put("department", reg.getStudent().getDepartment());
            map.put("year", reg.getStudent().getYear());
            map.put("registrationDate", reg.getRegistrationDate());
            map.put("status", reg.getStatus());
            participantList.add(map);
        }
        return participantList;
    }
}
