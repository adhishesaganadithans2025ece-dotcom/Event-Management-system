package com.example.event.controller;

import com.example.event.entity.Organizer;
import com.example.event.service.EventService;
import com.example.event.service.OrganizerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organizers")
@CrossOrigin(origins = "*")
public class OrganizerController {

    private final OrganizerService organizerService;
    private final EventService eventService;

    public OrganizerController(OrganizerService organizerService, EventService eventService) {
        this.organizerService = organizerService;
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<?> createOrganizer(@RequestBody Organizer organizer) {
        try {
            Organizer saved = organizerService.createOrganizer(organizer);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<?> getOrganizerEvents(@PathVariable Long id) {
        try {
            List<Map<String, Object>> events = eventService.getEventsByOrganizerId(id);
            return ResponseEntity.ok(events);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
