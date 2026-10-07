# 💊 Pharmacy Store Management System (Multi-Module Maven Project)

A production-oriented **Java Pharmacy Store Management System** designed to manage medicines, suppliers, batch-wise inventory, purchase entries, FEFO batch selection, POS billing, invoices, expiry tracking, and sales reports.

This project is structured as a **Multi-Module Maven Project** with a Parent POM ready for submodules and expansion (`pharmacy-core`, `pharmacy-cli`).

---

## 📌 Project Architecture & Modules

```text
pharmacy-store-management-system (Parent POM)
│
├── pharmacy-core/       (Domain Models, Service Layer, FR Business Logic)
│   ├── model/ (Medicine, Supplier, Batch, Invoice, UserRole)
│   └── service/ (PharmacyService handling FR-1 to FR-9)
│
└── pharmacy-cli/        (Interactive Console CLI Menu Module)
    └── com.pharmacy.cli.PharmacyConsoleApp (Console Menu with 1 option per FR)
```

---

## 📋 Functional Requirements (FR) & Console Menu Mapping

The console interface provides **one dedicated menu option per Functional Requirement (FR)**:

| Menu Option | Functional Requirement (FR) | Description |
|---|---|---|
| **Option 1** | **[FR-1] Medicine Management** | View registered medicines, add new medicines, unit prices, and Rx requirements. |
| **Option 2** | **[FR-2] Supplier Management** | View and add suppliers, contact details, and logistics partners. |
| **Option 3** | **[FR-3] Purchase Entry & Orders** | Record incoming stock purchases against suppliers and create batches. |
| **Option 4** | **[FR-4] Batch Creation & FEFO** | Track stock batches using **FEFO (First Expiry, First Out)** selection. |
| **Option 5** | **[FR-5] Inventory Management** | Monitor batch-wise stock levels, quantities, and availability across items. |
| **Option 6** | **[FR-6] POS Billing & Invoicing** | Process customer sales using FEFO stock deduction and generate invoices. |
| **Option 7** | **[FR-7] Sales & Stock Reports** | View sales history, invoice logs, and cumulative revenue statistics. |
| **Option 8** | **[FR-8] Expiry & Returns** | Detect expired batches automatically for supplier returns processing. |
| **Option 9** | **[FR-9] Role & Access Control** | Switch active user roles (**Admin**, **Pharmacist**, **Cashier**). |
| **Option 10** | **Exit System** | Cleanly terminate session. |

---

## 🛠️ How to Build and Run

### 1. Build Multi-Module Project (Parent & Submodules):
```bash
mvn clean package
```

### 2. Run Unit Tests across all modules:
```bash
mvn test
```

### 3. Launch Console Application:
```bash
java -jar pharmacy-cli/target/pharmacy-cli-1.0-SNAPSHOT.jar
```
Or via Maven:
```bash
mvn exec:java -pl pharmacy-cli
```