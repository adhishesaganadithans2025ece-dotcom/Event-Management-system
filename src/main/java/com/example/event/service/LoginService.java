package com.example.event.service;

import com.example.event.entity.Organizer;
import com.example.event.entity.Student;
import com.example.event.repository.OrganizerRepository;
import com.example.event.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class LoginService {

    private final StudentRepository studentRepository;
    private final OrganizerRepository organizerRepository;

    public LoginService(StudentRepository studentRepository, OrganizerRepository organizerRepository) {
        this.studentRepository = studentRepository;
        this.organizerRepository = organizerRepository;
    }

    public Map<String, Object> loginStudent(String email, String password) {
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isEmpty() || !studentOpt.get().getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid student credentials.");
        }
        Student student = studentOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", student.getId());
        response.put("name", student.getName());
        response.put("email", student.getEmail());
        response.put("department", student.getDepartment());
        response.put("year", student.getYear());
        response.put("role", "STUDENT");
        return response;
    }

    public Map<String, Object> loginOrganizer(String email, String password) {
        Optional<Organizer> organizerOpt = organizerRepository.findByEmail(email);
        if (organizerOpt.isEmpty() || !organizerOpt.get().getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid organizer credentials.");
        }
        Organizer organizer = organizerOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", organizer.getId());
        response.put("name", organizer.getName());
        response.put("email", organizer.getEmail());
        response.put("department", organizer.getDepartment());
        response.put("role", "ORGANIZER");
        return response;
    }
}
