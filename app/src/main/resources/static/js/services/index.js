import { openModal } from '../components/modals.js';
import { API_BASE_URL } from '../config/config.js';

const ADMIN_API = `${API_BASE_URL}/admin/login`;
const DOCTOR_API = `${API_BASE_URL}/doctor/login`;

// Make openModal globally available
window.openModal = openModal;

window.onload = () => {
  const adminBtn = document.getElementById('adminLogin');
  const doctorBtn = document.getElementById('doctorLogin');

  if (adminBtn) {
    adminBtn.addEventListener('click', () => openModal('adminLogin'));
  }

  if (doctorBtn) {
    doctorBtn.addEventListener('click', () => openModal('doctorLogin'));
  }
  
  // Handle role selection buttons on index page
  const adminRoleBtn = document.getElementById('adminBtn');
  const doctorRoleBtn = document.getElementById('doctorBtn');

  if (adminRoleBtn) {
    adminRoleBtn.addEventListener('click', () => openModal('adminLogin'));
  }

  if (doctorRoleBtn) {
    doctorRoleBtn.addEventListener('click', () => openModal('doctorLogin'));
  }
};

const adminLoginHandler = async () => {
  const usernameField = document.getElementById('username');
  const passwordField = document.getElementById('password');

  if (!usernameField || !passwordField) {
    alert('Please enter your credentials.');
    return;
  }

  const username = usernameField.value.trim();
  const password = passwordField.value.trim();

  if (!username || !password) {
    alert('Username and password are required.');
    return;
  }

  const admin = { username, password };

  try {
    const response = await fetch(ADMIN_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(admin),
    });

    if (!response.ok) {
      alert('Invalid credentials!');
      return;
    }

    const data = await response.json();
    const token = data?.token;

    if (!token) {
      alert('Unable to login. Please try again.');
      return;
    }

    localStorage.setItem('token', token);
    selectRole('admin');
  } catch (error) {
    console.error('Admin login failed:', error);
    alert('Something went wrong. Please try again later.');
  }
};

const doctorLoginHandler = async () => {
  const emailField = document.getElementById('email');
  const passwordField = document.getElementById('password');

  if (!emailField || !passwordField) {
    alert('Please enter your credentials.');
    return;
  }

  const email = emailField.value.trim();
  const password = passwordField.value.trim();

  if (!email || !password) {
    alert('Email and password are required.');
    return;
  }

  const doctor = { email, password };

  try {
    const response = await fetch(DOCTOR_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(doctor),
    });

    if (!response.ok) {
      alert('Invalid credentials!');
      return;
    }

    const data = await response.json();
    const token = data?.token;

    if (!token) {
      alert('Unable to login. Please try again.');
      return;
    }

    localStorage.setItem('token', token);
    selectRole('doctor');
  } catch (error) {
    console.error('Doctor login failed:', error);
    alert('Something went wrong. Please try again later.');
  }
};

window.adminLoginHandler = adminLoginHandler;
window.doctorLoginHandler = doctorLoginHandler;
