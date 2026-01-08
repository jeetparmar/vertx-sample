# Vert.x Sample Project

This repository contains a **simple Vert.x-based REST API** demonstrating how to build asynchronous HTTP services using **Eclipse Vert.x** and Java.

The project follows a clean, layered structure with handlers, services, and models, making it easy to understand and extend.

---

## 🚀 Features

* Vert.x HTTP server
* RESTful APIs for user management
* Non-blocking, event-driven architecture
* Simple in-memory user handling (for demo purposes)
* Clean project structure (API, Service, Model)

---

## 🧱 Project Structure

```text
src/main/java
└── com.vertx
    ├── api
    │   └── UserHandler.java      # HTTP route handlers
    ├── model
    │   └── User.java             # User domain model
    ├── service
    │   └── UserService.java      # Business logic
    └── MainVerticle.java         # Application entry point
```

---

## 🛠️ Prerequisites

* Java 8 or higher
* Maven
* IDE (IntelliJ IDEA / Eclipse / VS Code)

---

## ▶️ Running the Application

1. Clone the repository:

   ```bash
   git clone https://github.com/jeetparmar/vertx-sample.git
   cd vertx-sample
   ```

2. Open the project in your IDE.

3. Navigate to:

   ```
   src/main/java/com/vertx/MainVerticle.java
   ```

4. **Run `MainVerticle` as a Java Application** (it contains the `main` method).

5. The server will start on:

   ```
   http://localhost:8080
   ```

---

## 📌 API Endpoints

### ➤ Get All Users

```http
GET http://localhost:8080/users
```

---

### ➤ Get User by ID

```http
GET http://localhost:8080/users/{id}
```

---

### ➤ Create User

```http
POST http://localhost:8080/users
```

**Request Body (JSON):**

```json
{
  "name": "Some Name",
  "email": "some@email.com"
}
```

---

## 🧪 Testing

You can test the APIs using:

* Postman
* curl
* Any REST client

Example using curl:

```bash
curl http://localhost:8080/users
```

---

## 📦 Build (Optional)

To build the project using Maven:

```bash
mvn clean package
```

---

## 📚 Learn More

* Vert.x Documentation: [https://vertx.io/docs/](https://vertx.io/docs/)
* Vert.x Core Concepts: Event Loop, Verticles, Handlers

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!
Feel free to fork the repository and submit a pull request.

---

## 📄 License

This project is licensed under the MIT License.

---

### ⭐ If you find this project useful, consider giving it a star!
