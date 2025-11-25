import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

const patientTableBody = document.getElementById("patientTableBody");
const todayISODate = () => new Date().toISOString().split("T")[0];

let selectedDate = todayISODate();
const token = localStorage.getItem("token");
let patientName = "null";

const normalizedAppointments = (payload) => {
  if (Array.isArray(payload)) {
    return payload;
  }
  if (payload && Array.isArray(payload.appointments)) {
    return payload.appointments;
  }
  return [];
};

const showTableMessage = (message) => {
  if (!patientTableBody) {
    console.warn("Patient table body not found");
    return;
  }
  const messageRow = document.createElement("tr");
  messageRow.innerHTML = `<td colspan="5" style="text-align:center;">${message}</td>`;
  patientTableBody.appendChild(messageRow);
};

const loadAppointments = async () => {
  if (!patientTableBody) {
    console.warn("Patient table body not found");
    return;
  }

  patientTableBody.innerHTML = "";

  if (!token) {
    showTableMessage("Please login to view appointments.");
    return;
  }

  try {
    const data = await getAllAppointments(selectedDate, patientName, token);
    const appointments = normalizedAppointments(data);

    if (!appointments.length) {
      showTableMessage("No Appointments found for today.");
      return;
    }

    appointments.forEach((appointment) => {
      const patient = {
        id:
          appointment?.patient?.id ??
          appointment?.patientId ??
          appointment?.patient?.patientId ??
          "N/A",
        name:
          appointment?.patient?.name ??
          appointment?.patientName ??
          "Unknown Patient",
        phone:
          appointment?.patient?.phone ??
          appointment?.patientPhone ??
          "Not Provided",
        email:
          appointment?.patient?.email ??
          appointment?.patientEmail ??
          "Not Provided",
      };

      const doctorId =
        appointment?.doctor?.id ?? appointment?.doctorId ?? "unknown";

      const row = createPatientRow(patient, appointment?.id, doctorId);
      patientTableBody.appendChild(row);
    });
  } catch (error) {
    console.error("loadAppointments :: error", error);
    showTableMessage("Error loading appointments. Try again later.");
  }
};

const bindSearch = () => {
  const searchBar = document.getElementById("searchBar");
  if (!searchBar) {
    return;
  }

  searchBar.addEventListener("input", (event) => {
    const value = event.target.value.trim();
    patientName = value.length ? value : "null";
    loadAppointments();
  });
};

const bindTodayButton = () => {
  const todayButton = document.getElementById("todayButton");
  const datePicker = document.getElementById("datePicker");

  if (todayButton) {
    todayButton.addEventListener("click", () => {
      selectedDate = todayISODate();
      if (datePicker) {
        datePicker.value = selectedDate;
      }
      loadAppointments();
    });
  }
};

const bindDatePicker = () => {
  const datePicker = document.getElementById("datePicker");
  if (!datePicker) {
    return;
  }

  datePicker.value = selectedDate;
  datePicker.addEventListener("change", (event) => {
    selectedDate = event.target.value || todayISODate();
    loadAppointments();
  });
};

document.addEventListener("DOMContentLoaded", () => {
  if (typeof renderContent === "function") {
    renderContent();
  }

  bindSearch();
  bindTodayButton();
  bindDatePicker();
  loadAppointments();
});
