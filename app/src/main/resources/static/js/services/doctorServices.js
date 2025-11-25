import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = `${API_BASE_URL}/doctor`;

const parseDoctors = (payload) => {
  if (Array.isArray(payload)) {
    return payload;
  }
  if (payload && Array.isArray(payload.doctors)) {
    return payload.doctors;
  }
  return [];
};

const buildFilterParam = (value) =>
  value && value.length > 0 ? encodeURIComponent(value) : "null";

export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);
    if (!response.ok) {
      throw new Error(`Failed to fetch doctors: ${response.status}`);
    }
    const data = await response.json();
    return parseDoctors(data);
  } catch (error) {
    console.error("getDoctors :: error", error);
    return [];
  }
}

export async function deleteDoctor(id, token) {
  if (!id || !token) {
    return {
      success: false,
      message: "Doctor id and token are required to delete a doctor.",
    };
  }

  try {
    const response = await fetch(
      `${DOCTOR_API}/${encodeURIComponent(id)}/${encodeURIComponent(token)}`,
      {
        method: "DELETE",
      }
    );
    const data = await response.json().catch(() => ({}));
    if (!response.ok) {
      return {
        success: false,
        message: data?.message || "Failed to delete doctor.",
      };
    }

    return {
      success: true,
      message: data?.message || "Doctor deleted successfully.",
    };
  } catch (error) {
    console.error("deleteDoctor :: error", error);
    return {
      success: false,
      message: "Unable to delete doctor right now. Please try again.",
    };
  }
}

export async function saveDoctor(doctor, token) {
  if (!doctor || !token) {
    return {
      success: false,
      message: "Doctor data and admin token are required.",
    };
  }

  try {
    const response = await fetch(
      `${DOCTOR_API}/${encodeURIComponent(token)}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(doctor),
      }
    );
    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      return {
        success: false,
        message: data?.message || "Failed to save doctor.",
      };
    }

    return {
      success: true,
      message: data?.message || "Doctor saved successfully.",
    };
  } catch (error) {
    console.error("saveDoctor :: error", error);
    return {
      success: false,
      message: "Unable to save doctor right now. Please try again.",
    };
  }
}

export async function filterDoctors(name, time, specialty) {
  try {
    const response = await fetch(
      `${DOCTOR_API}/filter/${buildFilterParam(name)}/${buildFilterParam(
        time
      )}/${buildFilterParam(specialty)}`
    );

    if (!response.ok) {
      throw new Error(`Failed to filter doctors: ${response.status}`);
    }

    const data = await response.json();
    return {
      doctors: parseDoctors(data),
    };
  } catch (error) {
    console.error("filterDoctors :: error", error);
    return { doctors: [] };
  }
}
