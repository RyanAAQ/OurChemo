# ourChemo — Pharmacy Inventory Manager

A full-stack pharmacy management system built with **Spring Boot 3 + MongoDB** on the backend and **React 18 + TypeScript + Vite** on the frontend.

---

## Features

| Area | What it does |
|---|---|
| **Auth** | Register / login / logout chemist accounts |
| **Inventory** | View all drugs, search by name / generic / brand, add new stock batches, edit price/brand, delete |
| **Dispense** | Pick drugs from a batch-aware selector, build a cart, confirm dispensing — stock is decremented automatically |
| **History** | View all past dispensing transactions with itemised breakdown and total revenue |

---

## Project structure

```
ourChemo/
├── src/ng/ourChemo/          # Spring Boot source
│   ├── config/               # CORS filter
│   ├── controllers/          # AuthControllers, DrugInventoryControllers
│   ├── data/models/          # User, Drug, Batch, DispensedDrug, DispensedDrugs
│   ├── data/repositories/    # Spring Data MongoDB repositories
│   ├── dtos/                 # Request / response DTOs
│   ├── exceptions/           # Custom exceptions + GlobalExceptionHandler
│   ├── services/             # AuthService, DrugInventoryServices (+ impls)
│   └── utils/                # Mapper
├── resources/
│   └── application.properties
├── frontend/                 # Vite + React + TypeScript
│   └── src/
│       ├── api/              # axios wrappers (auth.ts, drugs.ts)
│       ├── components/       # Layout, Modal, ProtectedRoute
│       ├── context/          # AuthContext (login state + localStorage)
│       ├── pages/            # LoginPage, RegisterPage, InventoryPage,
│       │                     #   DispensePage, HistoryPage
│       └── types/            # Shared TypeScript types
└── pom.xml
```

---

## Prerequisites

| Tool | Version |
|---|---|
| Java | 21+ |
| Maven | 3.9+ |
| MongoDB | 6+ (running locally on port 27017) |
| Node.js | 18+ |
| npm | 9+ |

---

## Running locally

### 1 — Start MongoDB

```bash
mongod --dbpath /data/db
# or via systemd
sudo systemctl start mongod
```

### 2 — Start the backend

```bash
# from the project root
mvn spring-boot:run
```

The API starts on **http://localhost:8081**.

### 3 — Start the frontend

```bash
cd frontend
npm install   # only needed once
npm run dev
```

The UI starts on **http://localhost:5173**.  
All `/api/*` requests are proxied to `http://localhost:8081` automatically.

---

## API endpoints

### Auth  `/auth`

| Method | Path | Body |
|---|---|---|
| POST | `/auth/register` | `{ username, password, fullName }` |
| POST | `/auth/login` | `{ username, password }` |
| POST | `/auth/logout` | `{ username }` |

### Drugs  `/drugs`

| Method | Path | Notes |
|---|---|---|
| GET | `/drugs/all` | List all drugs (sorted A→Z) |
| GET | `/drugs/search?word=` | Search by name / generic / brand |
| POST | `/drugs/add` | Add drug / restock batch |
| PUT | `/drugs/update` | Update brand or price |
| DELETE | `/drugs/delete` | Delete drug + all its batches |
| POST | `/drugs/dispense` | Dispense items (requires logged-in user) |
| GET | `/drugs/history` | All dispensing records (newest first) |

All responses are wrapped:
```json
{ "successful": true, "data": { ... } }
{ "successful": false, "data": "Error message here" }
```

---

## Environment / configuration

Edit `resources/application.properties` to change the MongoDB URI or server port:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/test
server.port=8081
```

---

## Known limitations

- Passwords are stored as **plain text** — add BCrypt hashing before any production use.
- Authentication uses a simple `isLoggedIn` boolean flag on the User document (no JWT/sessions).
- Spring Data derived queries use exact-match, not substring search, so the search feature works best with full words.
# OurChemo
