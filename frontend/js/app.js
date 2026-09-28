/* EventEase - College Event Registration System - JavaScript Application */

const API_BASE_URL = "http://localhost:8080/api";

// --- Local Storage Session Management ---
function saveUserSession(user) {
    localStorage.setItem("userId", user.id);
    localStorage.setItem("userName", user.name);
    localStorage.setItem("userEmail", user.email);
    localStorage.setItem("userRole", user.role);
}

function getUserSession() {
    const id = localStorage.getItem("userId");
    const name = localStorage.getItem("userName");
    const email = localStorage.getItem("userEmail");
    const role = localStorage.getItem("userRole");

    if (!id || !role) return null;
    return { id: parseInt(id), name, email, role };
}

function clearUserSession() {
    localStorage.removeItem("userId");
    localStorage.removeItem("userName");
    localStorage.removeItem("userEmail");
    localStorage.removeItem("userRole");
}

function logout() {
    clearUserSession();
    window.location.href = "index.html";
}

function checkAuth(expectedRole) {
    const user = getUserSession();
    if (!user) {
        window.location.href = "index.html";
        return null;
    }
    if (expectedRole && user.role !== expectedRole) {
        if (user.role === "STUDENT") {
            window.location.href = "student.html";
        } else if (user.role === "ORGANIZER") {
            window.location.href = "organizer.html";
        } else {
            window.location.href = "index.html";
        }
        return null;
    }
    return user;
}

// --- Alert Notification UI ---
function showAlert(elementId, message, type = "success") {
    const alert = document.getElementById(elementId);
    if (!alert) return;
    alert.textContent = message;
    alert.className = `alert alert-${type}`;
    alert.style.display = "block";
    setTimeout(() => {
        alert.style.display = "none";
    }, 4000);
}

// --- API Calls ---

// 1. Student Login
async function loginStudent(email, password) {
    try {
        const response = await fetch(`${API_BASE_URL}/login/student`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || "Login failed");
        saveUserSession(data);
        window.location.href = "student.html";
    } catch (err) {
        showAlert("login-alert", err.message, "danger");
    }
}

// 2. Organizer Login
async function loginOrganizer(email, password) {
    try {
        const response = await fetch(`${API_BASE_URL}/login/organizer`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || "Login failed");
        saveUserSession(data);
        window.location.href = "organizer.html";
    } catch (err) {
        showAlert("login-alert", err.message, "danger");
    }
}

// 3. Register Student Account
async function registerStudentAccount(studentData) {
    try {
        const response = await fetch(`${API_BASE_URL}/students`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(studentData)
        });
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || "Student registration failed");
        showAlert("register-alert", "Student account created! You can now log in.", "success");
        setTimeout(() => closeModal('register-modal'), 1500);
    } catch (err) {
        showAlert("register-alert", err.message, "danger");
    }
}

// 4. Register Organizer Account
async function registerOrganizerAccount(organizerData) {
    try {
        const response = await fetch(`${API_BASE_URL}/organizers`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(organizerData)
        });
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || "Organizer registration failed");
        showAlert("register-alert", "Organizer account created! You can now log in.", "success");
        setTimeout(() => closeModal('register-modal'), 1500);
    } catch (err) {
        showAlert("register-alert", err.message, "danger");
    }
}

// 5. Fetch All Events
async function fetchAllEvents() {
    try {
        const response = await fetch(`${API_BASE_URL}/events`);
        if (!response.ok) throw new Error("Failed to load events");
        return await response.json();
    } catch (err) {
        console.error(err);
        return [];
    }
}

// 6. Fetch Event Details
async function fetchEventDetails(eventId) {
    try {
        const response = await fetch(`${API_BASE_URL}/events/${eventId}`);
        if (!response.ok) throw new Error("Failed to load event details");
        return await response.json();
    } catch (err) {
        console.error(err);
        return null;
    }
}

// 7. Register Student for Event
async function registerForEvent(eventId) {
    const user = getUserSession();
    if (!user || user.role !== "STUDENT") {
        alert("Please log in as a Student to register for events.");
        window.location.href = "index.html";
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/events/${eventId}/register/${user.id}`, {
            method: "POST"
        });
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || "Registration failed");

        alert("Successfully registered for event!");
        location.reload();
    } catch (err) {
        alert(err.message);
    }
}

// 8. Cancel Student Registration
async function cancelRegistration(eventId) {
    const user = getUserSession();
    if (!user || user.role !== "STUDENT") return;

    if (!confirm("Are you sure you want to cancel your registration?")) return;

    try {
        const response = await fetch(`${API_BASE_URL}/events/${eventId}/register/${user.id}`, {
            method: "DELETE"
        });
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || "Cancellation failed");

        alert("Registration cancelled successfully.");
        location.reload();
    } catch (err) {
        alert(err.message);
    }
}

// 9. Fetch Student Registrations
async function fetchStudentRegistrations(studentId) {
    try {
        const response = await fetch(`${API_BASE_URL}/students/${studentId}/registrations`);
        if (!response.ok) throw new Error("Failed to load registrations");
        return await response.json();
    } catch (err) {
        console.error(err);
        return [];
    }
}

// 10. Fetch Organizer Events
async function fetchOrganizerEvents(organizerId) {
    try {
        const response = await fetch(`${API_BASE_URL}/organizers/${organizerId}/events`);
        if (!response.ok) throw new Error("Failed to load organizer events");
        return await response.json();
    } catch (err) {
        console.error(err);
        return [];
    }
}

// 11. Create Event (Organizer)
async function createEvent(eventData) {
    try {
        const response = await fetch(`${API_BASE_URL}/events`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(eventData)
        });
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || "Failed to create event");

        showAlert("event-alert", "Event created successfully!", "success");
        setTimeout(() => location.reload(), 1200);
    } catch (err) {
        showAlert("event-alert", err.message, "danger");
    }
}

// 12. Update Event (Organizer)
async function updateEvent(eventId, eventData) {
    try {
        const response = await fetch(`${API_BASE_URL}/events/${eventId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(eventData)
        });
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || "Failed to update event");

        showAlert("edit-event-alert", "Event updated successfully!", "success");
        setTimeout(() => location.reload(), 1200);
    } catch (err) {
        showAlert("edit-event-alert", err.message, "danger");
    }
}

// 13. Delete Event (Organizer)
async function deleteEvent(eventId) {
    if (!confirm("Are you sure you want to delete this event?")) return;
    try {
        const response = await fetch(`${API_BASE_URL}/events/${eventId}`, {
            method: "DELETE"
        });
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || "Failed to delete event");

        alert("Event deleted successfully.");
        location.reload();
    } catch (err) {
        alert(err.message);
    }
}

// 14. Fetch Participant List
async function fetchEventParticipants(eventId) {
    try {
        const response = await fetch(`${API_BASE_URL}/events/${eventId}/registrations`);
        if (!response.ok) throw new Error("Failed to load participants");
        return await response.json();
    } catch (err) {
        console.error(err);
        return [];
    }
}

// --- Modal Helper Functions ---
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.style.display = "flex";
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.style.display = "none";
}
