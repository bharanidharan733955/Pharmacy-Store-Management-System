# Day 1: Agile & Scrum Guide + Project Backlog

## 1. Overview of Agile & Scrum Framework

### What is Scrum?
Scrum is an iterative, incremental Agile framework used to manage complex software development. Work is delivered in fixed-length iterations called **Sprints** (typically 1 to 4 weeks long).

### Key Scrum Ceremonies
1. **Sprint Planning**: Team aligns on the sprint goal and selects user stories from the Product Backlog into the Sprint Backlog based on team velocity.
2. **Daily Standup**: A 15-minute daily synchronization meeting where team members answer 3 questions:
   - What did I complete yesterday?
   - What will I work on today?
   - Are there any blockers/impediments in my way?
3. **Sprint Review**: Demonstration of the potentially releasable product increment to stakeholders for feedback.
4. **Sprint Retrospective**: Internal team meeting to reflect on what went well, what didn't go well, and actionable improvements for the next sprint.

---

## 2. Definition of Done (DoD)

A User Story or Product Backlog item is marked **DONE** only when all of the following criteria are met:

- [x] **Requirement Implemented**: All functional requirements described in the user story are fully built.
- [x] **Code Follows Standards**: Code adheres to Java naming conventions, formatting rules, and eliminates magic numbers using constants.
- [x] **Code Compiles Successfully**: Code compiles cleanly via `javac` with zero compilation errors or warnings.
- [x] **Unit Tests Completed**: Business logic and calculations (e.g., tax, discount slabs, stock threshold checks) pass unit tests where applicable.
- [x] **Functional Testing Completed**: Manual execution via `java` terminal output confirms expected behaviors across positive and edge cases.
- [x] **No Critical Defects**: Zero open critical or high-severity bugs exist.
- [x] **Documentation Updated**: In-code JavaDoc comments and markdown documentation (`README.md`, `AGILE_SCRUM.md`) are accurate and complete.
- [x] **Git Changes Committed**: Clean git commits made with descriptive messages and proper `.gitignore` enforcement.
- [x] **Code Reviewed**: Peer review completed and approved.
- [x] **Feature Accepted**: Product Owner reviews and accepts the feature according to the story's Acceptance Criteria.

---

## 3. Product Backlog: 8 User Stories (Pharmacy Store Management System)

Below are the 8 Functional Requirements (FRs) for the **Pharmacy Store Management System** converted into formal User Stories with Story Point estimates using the Fibonacci sequence (1, 2, 3, 5, 8, 13) based on implementation complexity and risk.

---

### 🟢 User Story US-01: User Authentication & Role Management
- **User Story ID**: US-01
- **Title**: Role-Based User Authentication
- **User Story**:
  > **As a** Pharmacy Manager / Cashier,  
  > **I want to** log into the system using credentials with role-based permissions,  
  > **so that** unauthorized personnel cannot access sensitive inventory or financial records.
- **Acceptance Criteria**:
  1. System prompts for username and password.
  2. Users are classified as `ADMIN`, `PHARMACIST`, or `CASHIER`.
  3. `CASHIER` can access billing but cannot edit stock levels or view supplier cost details.
  4. Failed login attempts display an appropriate error message without exposing detailed system internals.
- **Story Point Estimate**: **3 Points** (Medium complexity - security & session handling)
- **Priority**: High

---

### 🟢 User Story US-02: Medicine Inventory Management
- **User Story ID**: US-02
- **Title**: Medicine Catalogue & Stock Control
- **User Story**:
  > **As a** Pharmacist,  
  > **I want to** add, edit, view, and search medicine details (Name, Batch Number, Unit Price, Stock Quantity),  
  > **so that** the pharmacy catalogue reflects accurate stock levels in real time.
- **Acceptance Criteria**:
  1. Pharmacist can add new medicines with name, batch ID, manufacturer, price, and initial stock.
  2. Search function allows filtering by medicine name or batch number.
  3. Updating stock quantity automatically adjusts available balance.
  4. Price inputs validate that values are strictly positive floating-point numbers.
- **Story Point Estimate**: **5 Points** (High complexity - core CRUD operations & validation)
- **Priority**: High

---

### 🟢 User Story US-03: Low-Stock Reorder Notifications
- **User Story ID**: US-03
- **Title**: Automatic Low-Stock Reorder Alerts
- **User Story**:
  > **As an** Inventory Supervisor,  
  > **I want to** receive automatic alerts when medicine stock falls below predefined threshold levels,  
  > **so that** essential life-saving medicines never run out of stock.
- **Acceptance Criteria**:
  1. System compares stock quantity against `REORDER_THRESHOLD` (e.g., 50 units).
  2. Medicines with stock $\le 15$ units are flagged as `CRITICAL REORDER`.
  3. Medicines with stock between 16 and 50 units are flagged as `LOW STOCK ALERT`.
  4. Supervisor can export/display a filtered list of all low-stock items.
- **Story Point Estimate**: **3 Points** (Medium complexity - automated threshold evaluation)
- **Priority**: High

---

### 🟢 User Story US-04: Expiry Date Tracking & Warnings
- **User Story ID**: US-04
- **Title**: Medicine Expiration Monitoring
- **User Story**:
  > **As a** Pharmacist,  
  > **I want to** filter and track medicines nearing their expiration date (e.g., within 30 days),  
  > **so that** expired or near-expiry drugs are safely removed from active sales.
- **Acceptance Criteria**:
  1. System checks expiration dates against current system date.
  2. Flag items expiring within 30 days as `EXPIRING SOON`.
  3. Block sales of items whose expiration date has passed (`EXPIRED`).
  4. Generate a monthly batch expiration report.
- **Story Point Estimate**: **5 Points** (High complexity - date calculations & sales blocking logic)
- **Priority**: High

---

### 🟢 User Story US-05: Customer Billing & POS Receipts
- **User Story ID**: US-05
- **Title**: Point-of-Sale Billing & Invoice Generation
- **User Story**:
  > **As a** Cashier,  
  > **I want to** select medicines, apply tax/discounts, and generate itemized receipts,  
  > **so that** customer transactions are processed quickly and accurately.
- **Acceptance Criteria**:
  1. Cashier can select multiple medicines and quantities for a customer cart.
  2. System automatically calculates subtotal, standard tax rate (8%), and discounts (e.g., bulk discount over $200).
  3. Inventory stock is decremented immediately upon transaction completion.
  4. System prints an itemized bill showing medicine names, batch IDs, unit prices, tax, and total price.
- **Story Point Estimate**: **8 Points** (Very High complexity - multi-item cart, stock sync, tax/discount calculation)
- **Priority**: Critical

---

### 🟢 User Story US-06: Customer Management & Prescription History
- **User Story ID**: US-06
- **Title**: Customer Profile & Prescription History
- **User Story**:
  > **As a** Pharmacist,  
  > **I want to** record customer details and view their past purchase/prescription history,  
  > **so that** repeat orders and restricted medications are handled safely.
- **Acceptance Criteria**:
  1. Store customer contact details (Name, Phone, Allergies/Notes).
  2. Search customer by phone number or ID.
  3. Display past purchase history including dates and prescribed items.
- **Story Point Estimate**: **3 Points** (Medium complexity - customer lookup & relationship mapping)
- **Priority**: Medium

---

### 🟢 User Story US-07: Supplier & Purchase Order Management
- **User Story ID**: US-07
- **Title**: Supplier Records & Purchase Order Generation
- **User Story**:
  > **As a** Store Manager,  
  > **I want to** manage supplier records and generate Purchase Orders (POs),  
  > **so that** inventory restocking from vendors is streamlined.
- **Acceptance Criteria**:
  1. Maintain supplier directory (Supplier Name, Contact Person, Drug Licenses, Phone).
  2. Generate PO draft automatically populated with low-stock medicines.
  3. Mark PO status as `DRAFT`, `SENT`, or `RECEIVED`.
- **Story Point Estimate**: **5 Points** (High complexity - order workflow & status tracking)
- **Priority**: Medium

---

### 🟢 User Story US-08: Sales Analytics & Multi-Branch Financial Reporting
- **User Story ID**: US-08
- **Title**: Sales Dashboard & Multi-Branch Analytics
- **User Story**:
  > **As a** Pharmacy Store Owner,  
  > **I want to** view daily/monthly revenue reports and multi-branch performance analytics,  
  > **so that** I can monitor financial health and make data-driven expansion decisions.
- **Acceptance Criteria**:
  1. Display total daily, monthly, and annual gross revenue.
  2. Calculate branch performance grades ('A', 'B', 'C') based on revenue slabs.
  3. Export report summary containing average daily sales, tax collected, and peak sales days.
- **Story Point Estimate**: **8 Points** (Very High complexity - multi-branch aggregation, data arrays, financial math)
- **Priority**: High

---

## 📈 Story Point Summary & Velocity Estimation

| Story ID | Requirement | Story Points | Priority | Sprint Assigned |
| :--- | :--- | :---: | :---: | :---: |
| **US-01** | User Authentication & Roles | 3 | High | Sprint 1 |
| **US-02** | Medicine Inventory Management | 5 | High | Sprint 1 |
| **US-03** | Low-Stock Reorder Alerts | 3 | High | Sprint 1 |
| **US-04** | Expiry Date Tracking | 5 | High | Sprint 2 |
| **US-05** | Billing & POS Invoice | 8 | Critical | Sprint 2 |
| **US-06** | Customer History | 3 | Medium | Sprint 2 |
| **US-07** | Supplier PO Management | 5 | Medium | Sprint 3 |
| **US-08** | Multi-Branch Sales Analytics | 8 | High | Sprint 3 |
| **TOTAL** | **8 User Stories** | **40 Points** | — | **3 Sprints** |

---

## 🔄 Example Sprint Workflow & Templates

### 1. Daily Standup Format (15-Minute Sync)
```text
Daily Standup Meeting Agenda:
--------------------------------------------------
1. What did you complete yesterday?
   - Example: "Completed US-03 low-stock alert logic using constant threshold checks."
2. What are you working on today?
   - Example: "Starting US-02 unit test coverage for medicine inventory updates."
3. Are there any impediments / blockers?
   - Example: "Waiting for clarification on tax calculation rules for prescription vs OTC items."
```

### 2. Sprint Retrospective Format (Start / Stop / Continue)
```text
Sprint Retrospective Notes:
--------------------------------------------------
- START: Adding automated check scripts for terminal compilation before PR reviews.
- STOP: Hardcoding threshold numbers in method bodies (use Constants class).
- CONTINUE: Maintaining comprehensive AGILE_SCRUM.md backlog documentation.
```
