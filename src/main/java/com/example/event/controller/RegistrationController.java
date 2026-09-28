package com.example.event.controller;

import com.example.event.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/{eventId}/register/{studentId}")
    public ResponseEntity<?> registerStudent(@PathVariable Long eventId, @PathVariable Long studentId) {
        try {
            Map<String, Object> result = registrationService.registerStudentForEvent(studentId, eventId);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

    @GetMapping("/{eventId}/registrations")
    public ResponseEntity<?> getParticipants(@PathVariable Long eventId) {
        try {
            List<Map<String, Object>> participants = registrationService.getParticipantsForEvent(eventId);
            return ResponseEntity.ok(participants);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @DeleteMapping("/{eventId}/register/{studentId}")
    public ResponseEntity<?> cancelRegistration(@PathVariable Long eventId, @PathVariable Long studentId) {
        try {
            Map<String, Object> result = registrationService.cancelRegistration(studentId, eventId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
