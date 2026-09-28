package com.example.event.service;

import com.example.event.entity.Event;
import com.example.event.entity.Organizer;
import com.example.event.entity.Registration;
import com.example.event.entity.Student;
import com.example.event.repository.EventRepository;
import com.example.event.repository.RegistrationRepository;
import com.example.event.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private RegistrationService registrationService;

    private Student student;
    private Event event;

    @BeforeEach
    void setUp() {
        student = new Student(1L, "Arun", "arun@example.com", "CSE", 3, "password");
        Organizer organizer = new Organizer(1L, "CSE Club", "cse@example.com", "CSE", "password");
        event = new Event(1L, "Hackathon", "24-hr coding", "Technical", LocalDate.now().plusDays(5), "Auditorium", 3, organizer);
    }

    @Test
    @DisplayName("1. Registration succeeds when valid student and seats available")
    void testRegistrationSuccess() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(registrationRepository.findByStudentIdAndEventId(1L, 1L)).thenReturn(Optional.empty());
        when(registrationRepository.countByEventIdAndStatus(1L, "REGISTERED")).thenReturn(1L);

        Registration savedReg = new Registration(10L, LocalDateTime.now(), "REGISTERED", student, event);
        when(registrationRepository.save(any(Registration.class))).thenReturn(savedReg);

        Map<String, Object> result = registrationService.registerStudentForEvent(1L, 1L);

        assertNotNull(result);
        assertEquals("Registration successful!", result.get("message"));
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    @Test
    @DisplayName("2. Registration fails when event capacity is full")
    void testRegistrationFailsWhenFull() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event)); // maxSeats = 3
        when(registrationRepository.findByStudentIdAndEventId(1L, 1L)).thenReturn(Optional.empty());
        when(registrationRepository.countByEventIdAndStatus(1L, "REGISTERED")).thenReturn(3L); // full

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
            registrationService.registerStudentForEvent(1L, 1L)
        );

        assertEquals("Registration failed. This event is full.", ex.getMessage());
    }

    @Test
    @DisplayName("3. Duplicate registration fails when student already registered")
    void testDuplicateRegistrationFails() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Registration activeReg = new Registration(10L, LocalDateTime.now(), "REGISTERED", student, event);
        when(registrationRepository.findByStudentIdAndEventId(1L, 1L)).thenReturn(Optional.of(activeReg));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
            registrationService.registerStudentForEvent(1L, 1L)
        );

        assertEquals("You are already registered for this event.", ex.getMessage());
    }

    @Test
    @DisplayName("4. Cancellation succeeds and changes status to CANCELLED")
    void testCancellationSuccess() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Registration activeReg = new Registration(10L, LocalDateTime.now(), "REGISTERED", student, event);
        when(registrationRepository.findByStudentIdAndEventIdAndStatus(1L, 1L, "REGISTERED"))
                .thenReturn(Optional.of(activeReg));

        Map<String, Object> result = registrationService.cancelRegistration(1L, 1L);

        assertEquals("Registration cancelled successfully.", result.get("message"));
        assertEquals("CANCELLED", activeReg.getStatus());
        verify(registrationRepository, times(1)).save(activeReg);
    }

    @Test
    @DisplayName("5. Cancelled seat becomes available for re-registration")
    void testCancelledSeatBecomesAvailable() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Registration cancelledReg = new Registration(10L, LocalDateTime.now().minusDays(1), "CANCELLED", student, event);
        when(registrationRepository.findByStudentIdAndEventId(1L, 1L)).thenReturn(Optional.of(cancelledReg));
        when(registrationRepository.countByEventIdAndStatus(1L, "REGISTERED")).thenReturn(2L); // 2 active out of 3

        when(registrationRepository.save(any(Registration.class))).thenAnswer(i -> i.getArgument(0));

        Map<String, Object> result = registrationService.registerStudentForEvent(1L, 1L);

        assertEquals("Registration successful!", result.get("message"));
        assertEquals("REGISTERED", cancelledReg.getStatus());
    }

    @Test
    @DisplayName("6. Registration for past event fails")
    void testPastEventRegistrationFails() {
        Event pastEvent = new Event(2L, "Old Event", "Past", "General", LocalDate.now().minusDays(2), "Hall", 50, event.getOrganizer());

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(eventRepository.findById(2L)).thenReturn(Optional.of(pastEvent));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            registrationService.registerStudentForEvent(1L, 2L)
        );

        assertEquals("Registration is closed because the event has already taken place.", ex.getMessage());
    }
}
