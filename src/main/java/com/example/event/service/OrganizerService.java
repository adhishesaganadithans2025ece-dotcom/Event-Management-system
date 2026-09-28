package com.example.event.service;

import com.example.event.entity.Organizer;
import com.example.event.repository.OrganizerRepository;
import org.springframework.stereotype.Service;

@Service
public class OrganizerService {

    private final OrganizerRepository organizerRepository;

    public OrganizerService(OrganizerRepository organizerRepository) {
        this.organizerRepository = organizerRepository;
    }

    public Organizer createOrganizer(Organizer organizer) {
        if (organizer.getName() == null || organizer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Organizer name is required.");
        }
        if (organizer.getEmail() == null || organizer.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Organizer email is required.");
        }
        if (organizerRepository.existsByEmail(organizer.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }
        if (organizer.getDepartment() == null || organizer.getDepartment().trim().isEmpty()) {
            throw new IllegalArgumentException("Department is required.");
        }
        if (organizer.getPassword() == null || organizer.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        return organizerRepository.save(organizer);
    }

    public Organizer getOrganizerById(Long id) {
        return organizerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found."));
    }
}
