/*
Import the overlay function for booking appointments from loggedPatient.js

  Import the deleteDoctor API function to remove doctors (admin role) from docotrServices.js

  Import function to fetch patient details (used during booking) from patientServices.js

  Function to create and return a DOM element for a single doctor card
    Create the main container for the doctor card
    Retrieve the current user role from localStorage
    Create a div to hold doctor information
    Create and set the doctor’s name
    Create and set the doctor's specialization
    Create and set the doctor's email
    Create and list available appointment times
    Append all info elements to the doctor info container
    Create a container for card action buttons
    === ADMIN ROLE ACTIONS ===
      Create a delete button
      Add click handler for delete button
     Get the admin token from localStorage
        Call API to delete the doctor
        Show result and remove card if successful
      Add delete button to actions container
   
    === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
      Create a book now button
      Alert patient to log in before booking
      Add button to actions container
  
    === LOGGED-IN PATIENT ROLE ACTIONS === 
      Create a book now button
      Handle booking logic for logged-in patient   
        Redirect if token not available
        Fetch patient data with token
        Show booking overlay UI with doctor and patient info
      Add button to actions container
   
  Append doctor info and action buttons to the car
  Return the complete doctor card element
*/

import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";
import { showBookingOverlay } from "../loggedPatient.js";

export function createDoctorCard(doctor) {
  // Main card container
  const card = document.createElement("div");
  card.classList.add("doctor-card");
  card.dataset.id = doctor.id;

  // Fetch user role
  const role = localStorage.getItem("userRole");

  // Doctor info section
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name || "Dr. Unknown";

  const specialization = document.createElement("p");
  specialization.classList.add("specialty");
  specialization.textContent = doctor.specialty || "General";

  const email = document.createElement("p");
  email.classList.add("email");
  email.textContent = doctor.email || "";

  const availability = document.createElement("p");
  availability.classList.add("availability");
  const times = Array.isArray(doctor.availableTimes) ? doctor.availableTimes : (doctor.availableTimes ? [doctor.availableTimes] : []);
  availability.textContent = times.length > 0 ? `Available: ${times.join(", ")}` : "No availability listed";

  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  if (doctor.email) infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  // Actions container
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // ADMIN actions: delete
  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.classList.add("danger-btn");
    removeBtn.textContent = "Delete";

    removeBtn.addEventListener("click", async () => {
      const confirmed = confirm(`Are you sure you want to delete Dr. ${doctor.name}?`);
      if (!confirmed) return;

      const token = localStorage.getItem("token");
      if (!token) {
        alert("You must be logged in as admin to perform this action.");
        return;
      }

      try {
        const res = await deleteDoctor(doctor.id, token);
        if (res && res.success) {
          alert("Doctor deleted successfully.");
          // remove DOM card
          card.remove();
        } else {
          alert("Failed to delete doctor: " + (res.message || "Unknown error"));
        }
      } catch (err) {
        console.error("Error deleting doctor:", err);
        alert("An error occurred while deleting the doctor.");
      }
    });

    actionsDiv.appendChild(removeBtn);
  }

  // PATIENT not logged in: show Book Now but prompt to login
  else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.classList.add("primary-btn");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", () => {
      alert("Please log in to book an appointment.");
    });
    actionsDiv.appendChild(bookNow);
  }

  // LOGGED-IN PATIENT: allow booking
  else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.classList.add("primary-btn");
    bookNow.textContent = "Book Now";

    bookNow.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Session expired. Please log in again.");
        localStorage.removeItem("userRole");
        window.location.href = "/";
        return;
      }

      // fetch patient data then show overlay
      const patient = await getPatientData(token);
      if (!patient) {
        alert("Unable to fetch patient details. Please try again.");
        return;
      }

      // show booking overlay (from loggedPatient.js)
      try {
        if (typeof showBookingOverlay === 'function') {
          showBookingOverlay(e, doctor, patient);
        } else if (window.showBookingOverlay) {
          window.showBookingOverlay(e, doctor, patient);
        } else {
          console.warn('Booking overlay not available');
        }
      } catch (err) {
        console.error('Error showing booking overlay:', err);
      }
    });

    actionsDiv.appendChild(bookNow);
  }

  // Default: show Book Now as login prompt
  else {
    const bookNow = document.createElement("button");
    bookNow.classList.add("primary-btn");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", () => {
      alert("Please log in to book an appointment.");
    });
    actionsDiv.appendChild(bookNow);
  }

  // Final assembly
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}
