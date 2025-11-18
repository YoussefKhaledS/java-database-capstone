Smart Clinic Management System – Architecture and Schema Overview
Section 1: Architecture Summary

This Spring Boot application follows a hybrid architecture that combines MVC and RESTful design patterns to support both web-based dashboards and API-driven interactions.
Thymeleaf templates are used to render dynamic pages for Admin and Doctor dashboards, enabling them to manage appointments, patients, and clinic operations through an intuitive web interface.
At the same time, REST APIs serve as the communication layer for all other modules, including Patient and Appointment services, facilitating secure and scalable data exchange between frontend components and the backend.

The system integrates with two databases to manage structured and unstructured data:

MySQL stores relational data such as Patients, Doctors, Admins, and Appointments, leveraging JPA entities with appropriate relationships and constraints.

MongoDB manages unstructured data like Prescriptions, enabling flexible document storage for diverse prescription details.

All HTTP requests are handled through well-defined controllers, which route operations to a service layer responsible for encapsulating business logic. The service layer then interacts with respective repositories, ensuring separation of concerns and maintainable code organization.
The application also implements JWT-based authentication to secure endpoints and enforce role-based access control for Admin, Doctor, and Patient roles.

Section 2: Numbered Flow of Data and Control

1. User Interaction:
A user (Admin, Doctor, or Patient) accesses the system through either a web dashboard (Thymeleaf templates) or REST API endpoints.

2. Routing to Controllers:
Requests are routed to the appropriate MVC controller (for web views) or REST controller (for API operations).

3. Authentication & Authorization:
Before accessing protected resources, users are authenticated using JWT tokens, and role-based permissions determine the actions they can perform.

4. Service Layer Invocation:
The controller delegates the request to a service layer, which contains the business logic for processing data or executing transactions.

5. Repository Access:
The service layer communicates with JPA repositories (for MySQL) or MongoDB repositories to fetch, insert, update, or delete data.

6. Data Persistence and Retrieval:
MySQL handles structured, relational data like user profiles and appointments, while MongoDB manages flexible data structures such as prescriptions.

7. Response Handling:
The processed data or operation results are returned from the service layer to the controller, which then sends either a rendered HTML page (via Thymeleaf) or a JSON response (via REST API) back to the client.
