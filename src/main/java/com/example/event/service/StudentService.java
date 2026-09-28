package com.example.event.service;

import com.example.event.entity.Registration;
import com.example.event.entity.Student;
import com.example.event.repository.RegistrationRepository;
import com.example.event.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final RegistrationRepository registrationRepository;

    public StudentService(StudentRepository studentRepository, RegistrationRepository registrationRepository) {
        this.studentRepository = studentRepository;
        this.registrationRepository = registrationRepository;
    }

    public Student createStudent(Student student) {
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student name is required.");
        }
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Student email is required.");
        }
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }
        if (student.getDepartment() == null || student.getDepartment().trim().isEmpty()) {
            throw new IllegalArgumentException("Department is required.");
        }
        if (student.getYear() == null || student.getYear() <= 0) {
            throw new IllegalArgumentException("Year must be a positive number.");
        }
        if (student.getPassword() == null || student.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        return studentRepository.save(student);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));
    }

    public List<Map<String, Object>> getStudentRegistrations(Long studentId) {
        getStudentById(studentId); // Verify student exists
        List<Registration> registrations = registrationRepository.findByStudentId(studentId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Registration reg : registrations) {
            Map<String, Object> map = new HashMap<>();
            map.put("registrationId", reg.getId());
            map.put("registrationDate", reg.getRegistrationDate());
            map.put("status", reg.getStatus());
            map.put("eventId", reg.getEvent().getId());
            map.put("eventTitle", reg.getEvent().getTitle());
            map.put("category", reg.getEvent().getCategory());
            map.put("eventDate", reg.getEvent().getEventDate());
            map.put("venue", reg.getEvent().getVenue());
            map.put("organizerName", reg.getEvent().getOrganizer().getName());
            result.add(map);
        }
        return result;
    }
}
