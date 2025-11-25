import { openModal } from "./components/modals.js";
import {
  getDoctors,
  filterDoctors,
  saveDoctor,
} from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

const getContentContainer = () => document.getElementById("content");

const renderDoctorCards = (doctors = []) => {
  const contentDiv = getContentContainer();
  if (!contentDiv) {
    console.warn("Admin dashboard content container not found.");
    return;
  }

  contentDiv.innerHTML = "";
  if (!Array.isArray(doctors) || doctors.length === 0) {
    contentDiv.innerHTML = "<p>No doctors found.</p>";
    return;
  }

  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    if (card) {
      contentDiv.appendChild(card);
    }
  });
};

const loadDoctorCards = async () => {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("loadDoctorCards :: error", error);
    alert("Unable to load doctors right now. Please try again.");
  }
};

const filterDoctorsOnChange = async () => {
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (!searchBar || !filterTime || !filterSpecialty) {
    return;
  }

  const name = searchBar.value.trim() || null;
  const time = filterTime.value || null;
  const specialty = filterSpecialty.value || null;

  try {
    const response = await filterDoctors(name, time, specialty);
    const doctors = response?.doctors || [];

    if (doctors.length === 0) {
      const contentDiv = getContentContainer();
      if (contentDiv) {
        contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
      }
      return;
    }

    renderDoctorCards(doctors);
  } catch (error) {
    console.error("filterDoctorsOnChange :: error", error);
    alert("Unable to filter doctors right now. Please try again.");
  }
};

const adminAddDoctor = async () => {
  const nameInput = document.getElementById("doctorName");
  const specialtySelect = document.getElementById("specialization");
  const emailInput = document.getElementById("doctorEmail");
  const passwordInput = document.getElementById("doctorPassword");
  const phoneInput = document.getElementById("doctorPhone");
  const availabilityNodes = document.querySelectorAll(
    'input[name="availability"]:checked'
  );

  if (
    !nameInput ||
    !specialtySelect ||
    !emailInput ||
    !passwordInput ||
    !phoneInput
  ) {
    alert("Please fill out all doctor details.");
    return;
  }

  const availableTimes = Array.from(availabilityNodes).map((node) => node.value);

  const doctor = {
    name: nameInput.value.trim(),
    specialty: specialtySelect.value,
    email: emailInput.value.trim(),
    password: passwordInput.value,
    phone: phoneInput.value.trim(),
    availableTimes,
  };

  if (!doctor.name || !doctor.specialty || !doctor.email || !doctor.password) {
    alert("All fields are required to add a doctor.");
    return;
  }

  const token = localStorage.getItem("token");
  if (!token) {
    alert("You must be logged in as an admin to add doctors.");
    return;
  }

  try {
    const { success, message } = await saveDoctor(doctor, token);
    if (success) {
      alert(message || "Doctor added successfully.");
      document.getElementById("modal").style.display = "none";
      await loadDoctorCards();
    } else {
      alert(message || "Failed to add doctor.");
    }
  } catch (error) {
    console.error("adminAddDoctor :: error", error);
    alert("Unable to save doctor right now. Please try again.");
  }
};

document.addEventListener("DOMContentLoaded", () => {
  const addDoctorBtn = document.getElementById("addDocBtn");
  if (addDoctorBtn) {
    addDoctorBtn.addEventListener("click", () => openModal("addDoctor"));
  }

  loadDoctorCards();

  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (searchBar) {
    searchBar.addEventListener("input", filterDoctorsOnChange);
  }
  if (filterTime) {
    filterTime.addEventListener("change", filterDoctorsOnChange);
  }
  if (filterSpecialty) {
    filterSpecialty.addEventListener("change", filterDoctorsOnChange);
  }
});

window.adminAddDoctor = adminAddDoctor;
