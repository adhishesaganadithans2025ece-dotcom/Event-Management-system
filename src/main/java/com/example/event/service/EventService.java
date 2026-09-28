package com.example.event.service;

import com.example.event.entity.Event;
import com.example.event.entity.Organizer;
import com.example.event.repository.EventRepository;
import com.example.event.repository.OrganizerRepository;
import com.example.event.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;
    private final RegistrationRepository registrationRepository;

    public EventService(EventRepository eventRepository,
                        OrganizerRepository organizerRepository,
                        RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.organizerRepository = organizerRepository;
        this.registrationRepository = registrationRepository;
    }

    public Event createEvent(Event event) {
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (event.getDescription() == null || event.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required.");
        }
        if (event.getCategory() == null || event.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required.");
        }
        if (event.getEventDate() == null) {
            throw new IllegalArgumentException("Event date is required.");
        }
        if (event.getEventDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("New events cannot have a past date.");
        }
        if (event.getVenue() == null || event.getVenue().trim().isEmpty()) {
            throw new IllegalArgumentException("Venue is required.");
        }
        if (event.getMaxSeats() == null || event.getMaxSeats() <= 0) {
            throw new IllegalArgumentException("Max seats must be greater than 0.");
        }
        if (event.getOrganizer() == null || event.getOrganizer().getId() == null) {
            throw new IllegalArgumentException("Organizer must exist.");
        }
        Organizer organizer = organizerRepository.findById(event.getOrganizer().getId())
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found."));
        event.setOrganizer(organizer);

        return eventRepository.save(event);
    }

    public List<Map<String, Object>> getAllEvents() {
        List<Event> events = eventRepository.findAllByOrderByEventDateAsc();
        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Event event : events) {
            responseList.add(mapToEventResponse(event));
        }
        return responseList;
    }

    public Map<String, Object> getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));
        return mapToEventResponse(event);
    }

    public List<Map<String, Object>> getEventsByOrganizerId(Long organizerId) {
        organizerRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found."));
        List<Event> events = eventRepository.findByOrganizerId(organizerId);
        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Event event : events) {
            responseList.add(mapToEventResponse(event));
        }
        return responseList;
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));

        if (updatedEvent.getTitle() != null && !updatedEvent.getTitle().trim().isEmpty()) {
            existing.setTitle(updatedEvent.getTitle());
        }
        if (updatedEvent.getDescription() != null && !updatedEvent.getDescription().trim().isEmpty()) {
            existing.setDescription(updatedEvent.getDescription());
        }
        if (updatedEvent.getCategory() != null && !updatedEvent.getCategory().trim().isEmpty()) {
            existing.setCategory(updatedEvent.getCategory());
        }
        if (updatedEvent.getEventDate() != null) {
            if (updatedEvent.getEventDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Event date cannot be set to a past date.");
            }
            existing.setEventDate(updatedEvent.getEventDate());
        }
        if (updatedEvent.getVenue() != null && !updatedEvent.getVenue().trim().isEmpty()) {
            existing.setVenue(updatedEvent.getVenue());
        }
        if (updatedEvent.getMaxSeats() != null) {
            if (updatedEvent.getMaxSeats() <= 0) {
                throw new IllegalArgumentException("Max seats must be greater than 0.");
            }
            existing.setMaxSeats(updatedEvent.getMaxSeats());
        }
        return eventRepository.save(existing);
    }

    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));
        eventRepository.delete(event);
    }

    public Map<String, Object> mapToEventResponse(Event event) {
        long registeredSeatsCount = registrationRepository.countByEventIdAndStatus(event.getId(), "REGISTERED");
        int registeredSeats = (int) registeredSeatsCount;
        int availableSeats = Math.max(0, event.getMaxSeats() - registeredSeats);

        String status;
        if (event.getEventDate().isBefore(LocalDate.now())) {
            status = "COMPLETED";
        } else if (availableSeats <= 0) {
            status = "FULL";
        } else {
            status = "OPEN";
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", event.getId());
        map.put("title", event.getTitle());
        map.put("description", event.getDescription());
        map.put("category", event.getCategory());
        map.put("eventDate", event.getEventDate());
        map.put("venue", event.getVenue());
        map.put("maxSeats", event.getMaxSeats());
        map.put("registeredSeats", registeredSeats);
        map.put("availableSeats", availableSeats);
        map.put("status", status);
        map.put("organizerId", event.getOrganizer().getId());
        map.put("organizerName", event.getOrganizer().getName());

        return map;
    }
}
