# Pharmacy Store Management System - Requirements Traceability Matrix (RTM)

**Date**: 2026-10-12  
**Status**: Fully Implemented & Verified  
**Stack**: Java 21, Spring Boot 3.3.4, Spring Security 6 (Stateless JWT), React 18 (JavaScript), Tailwind CSS 3, Vite, Vitest, MySQL 8 / H2 In-Memory Profile.

---

## 1. Overview and Scope Traceability

This matrix links business requirements, architectural components, implementation files, API endpoints, error handling specifications, and test verification outcomes.

| Module | Purpose | Status |
| :--- | :--- | :--- |
| `pharmacy-core` | Core business domain models, FEFO inventory allocation engine, custom exceptions, and core validation logic. | Completed |
| `pharmacy-web` | Spring Boot 3 REST API layer, JWT security, `@RestControllerAdvice`, role-protected dashboard endpoints, and atomic transactional POS services. | Completed |
| `pharmacy-frontend` | React 18 SPA (JavaScript only), role-based dashboards (`ADMIN`, `PHARMACIST`, `CASHIER`), POS modal, resilient menu navigation, and centralized error handling. | Completed |

---

## 2. Role-Based Dashboards Traceability

### 2.1 Admin Dashboard (`ROLE_ADMIN`)
* **Endpoint**: `GET /api/dashboard/admin`
* **Authorization**: Secured to `ROLE_ADMIN` only.
* **Component**: `Project/pharmacy-frontend/src/pages/AdminDashboard.jsx`
* **Features Implemented**:
  * Total active medicines from database catalog.
  * Total registered suppliers.
  * Total available stock with explicit unit measure (`Units (Tablets / Bottles)`).
  * Batches expiring within 30 days.
  * Today's sales total (in INR `₹`) and today's invoice count.
  * Recent purchase transactions list.
  * Recent invoice transactions list.
  * Quick action shortcuts to Manage Medicines, Manage Suppliers, Record Purchase, and New POS Bill.
* **Verification**: Verified in `SecurityAndExceptionHandlingTest#testAdminDashboardEndpoint_RequiresAdminRole` and Vitest `DashboardAndRules.test.jsx#1`.

### 2.2 Pharmacist Dashboard (`ROLE_PHARMACIST`, `ROLE_ADMIN`)
* **Endpoint**: `GET /api/dashboard/pharmacist?threshold={qty}`
* **Authorization**: Accessible to `ROLE_PHARMACIST` and `ROLE_ADMIN`.
* **Component**: `Project/pharmacy-frontend/src/pages/PharmacistDashboard.jsx`
* **Features Implemented**:
  * Total catalog medicines available.
  * Low-stock medicines based on user-configurable threshold parameter (live query param).
  * Batches expiring within the next 30 days.
  * Expired batches quarantined and flagged for attention (strictly excluded from saleable stock).
  * Recent purchase orders.
  * Quick action: "Record Purchase Order" (with modal).
  * Quick action: "Batch-wise Inventory Inspection" (with status badges: Active, Expiring Soon, Expired Quarantined).
* **Verification**: Verified in `SecurityAndExceptionHandlingTest#testCashier_CannotAccessPharmacistDashboard` and Vitest `DashboardAndRules.test.jsx#2`.

### 2.3 Cashier Dashboard (`ROLE_CASHIER`, `ROLE_PHARMACIST`, `ROLE_ADMIN`)
* **Endpoint**: `GET /api/dashboard/cashier`
* **Authorization**: Accessible to `ROLE_CASHIER`, `ROLE_PHARMACIST`, and `ROLE_ADMIN`.
* **Component**: `Project/pharmacy-frontend/src/pages/CashierDashboard.jsx`
* **Features Implemented**:
  * Today's total sales revenue (`₹`) and invoice count.
  * Recent invoices generated today with status indicators.
  * Medicine search and catalog view.
  * POS Billing Modal: Live medicine search, batch selection, prescription reference input, quantity counter, and clear inline business-rule error alerts.
  * Role restriction: Cashier cannot see staff management or unrestricted supplier/inventory editing controls.
* **Verification**: Verified in `SecurityAndExceptionHandlingTest` and Vitest `DashboardAndRules.test.jsx#3`.

---

## 3. Spring Boot Exception Package Traceability

* **Base Package**: `com.pharmacy.core.exception` & `com.pharmacy.web.exception`
* **Global Handler**: `GlobalExceptionHandler.java` (`@RestControllerAdvice`)

| Exception Class | HTTP Status | Error Code | Client Handling & Business Meaning |
| :--- | :---: | :---: | :--- |
| `ResourceNotFoundException` | `404 NOT_FOUND` | `RESOURCE_NOT_FOUND` | Target medicine, batch, supplier, or invoice does not exist. |
| `BusinessRuleException` | `409 CONFLICT` | `BUSINESS_RULE_VIOLATION` | General business rule constraint violated. |
| `InsufficientStockException` | `409 CONFLICT` | `INSUFFICIENT_STOCK` | Requested quantity exceeds eligible saleable non-expired stock. |
| `ExpiredBatchException` | `409 CONFLICT` | `EXPIRED_BATCH` | Attempted sale of batch past its expiry date or no non-expired batch found. |
| `InvalidPrescriptionException`| `400 BAD_REQUEST`| `PRESCRIPTION_REFERENCE_REQUIRED` | Scheduled prescription medicine sold without prescription reference. |
| `MethodArgumentNotValidException` | `400 BAD_REQUEST` | `VALIDATION_FAILED` | Bean validation failure with field-level error mappings (`fieldErrors`). |
| `BadCredentialsException` | `401 UNAUTHORIZED` | `AUTHENTICATION_FAILED` | Invalid credentials or expired JWT token. |
| `AccessDeniedException` | `403 FORBIDDEN` | `ACCESS_DENIED` | Authenticated principal lacks necessary role permission. |
| `Exception` (catch-all) | `500 INTERNAL_SERVER_ERROR` | `INTERNAL_SERVER_ERROR` | Server exception; sanitizes stack trace while logging correlation ID. |

### Error Response Contract
```json
{
  "timestamp": "2026-10-12T19:30:00",
  "status": 409,
  "error": "Conflict",
  "errorCode": "EXPIRED_BATCH",
  "message": "Batch BATCH-EXP-01 has expired on 2026-03-31 and cannot be sold",
  "path": "/api/invoices",
  "correlationId": "corr-uuid-1234",
  "fieldErrors": []
}
```

---

## 4. Business Rules Traceability

### Business Rule 1: Expired Medicine Cannot Be Sold (BR-1)
* **Policy Statement**: A medicine batch is valid and saleable up to and including its stated expiry date through 23:59:59. It becomes strictly non-saleable on `expiryDate.isBefore(LocalDate.now())`.
* **Backend Enforcement**:
  * Direct batch request: `PharmacyWebService.java` checks `batch.getExpiryDate().isBefore(LocalDate.now())`. If expired, throws `ExpiredBatchException("EXPIRED_BATCH")`.
  * Automatic FEFO allocation: `allocateFEFOEligibleBatches()` sorts eligible batches by `expiryDate ASC` and automatically filters out any batch where `isBefore(LocalDate.now())`. If cumulative non-expired stock is insufficient, throws `ExpiredBatchException("EXPIRED_BATCH")`.
  * Atomic Rollback: `@Transactional` ensures inventory is never decremented and no invoice is created.
* **Frontend Enforcement**:
  * Highlights batch status as Expired in red badge.
  * Traps API error `EXPIRED_BATCH` and displays an alert in the billing screen without navigating away or clearing the cart.
* **Traceability Tests**:
  * Backend: `BusinessRuleBillingTest#testBusinessRule1_ExpiredBatchSaleIsRejected_ByDirectBatch`
  * Backend: `BusinessRuleBillingTest#testBusinessRule1_ExpiredStockIsNotDecremented`
  * Backend: `BusinessRuleBillingTest#testBusinessRule1_NoSuccessfulInvoicePersistedOnExpiredSale`
  * Frontend: `DashboardAndRules.test.jsx#4`

### Business Rule 2: Scheduled Medicines Require a Prescription Reference (BR-2)
* **Policy Statement**: Any medicine marked as scheduled (`requiresPrescription = true` or `scheduleType = SCHEDULE_H / SCHEDULE_X`) requires a non-empty prescription reference string before an invoice can be finalized.
* **Backend Enforcement**:
  * Evaluated directly in `PharmacyWebService.java`: Checks `medicine.isRequiresPrescription()`. If prescription reference is null, blank, or whitespace, immediately throws `InvalidPrescriptionException("PRESCRIPTION_REFERENCE_REQUIRED")`.
  * Atomic Rollback: Transaction rolls back; no stock deducted, no invoice saved.
* **Frontend Enforcement**:
  * Marks scheduled medicines with a blue `Rx Required` badge.
  * Provides prescription input box in billing modal.
  * Displays inline field-level validation error if left blank.
* **Traceability Tests**:
  * Backend: `BusinessRuleBillingTest#testBusinessRule2_ScheduledMedicineWithoutPrescriptionReferenceIsRejected`
  * Backend: `BusinessRuleBillingTest#testBusinessRule2_StockIsUnchangedWhenPrescriptionValidationFails`
  * Frontend: `DashboardAndRules.test.jsx#5`

---

## 5. UI and Menu Recovery Traceability

| Requirement | Implementation Component | Verification Test |
| :--- | :--- | :--- |
| Centralized API Error Handling | `services/api.js` (`ApiError` handling) | Unit tests in `DashboardAndRules.test.jsx` |
| Session Preservation on 400/404/409 | `services/api.js` & `AuthContext.jsx` (clears token ONLY on genuine 401) | `DashboardAndRules.test.jsx#4, #5` |
| Sidebar & Topbar Resilience | `Sidebar.jsx`, `Navbar.jsx`, `AppLayout.jsx` | `DashboardAndRules.test.jsx#4, #5, #6` |
| Widget-Level Failure Containment | `ErrorBoundary.jsx`, `StatCard.jsx` error state | `DashboardAndRules.test.jsx#7` |
| Loading Cleanup on Error | `finally { setLoading(false); }` pattern | `DashboardAndRules.test.jsx#6` |
| Responsive Layout | Tailwind CSS breakpoints at 360px, 768px, 1280px | `AppLayout.jsx` |

---

## 6. Comprehensive Test Verification Matrix

### Backend Tests (41/41 Passing)
* **`pharmacy-core`** (28 passing tests in `mvn test`):
  * FEFO batch queue sorting by expiry date.
  * Unit deduction and stock threshold checks.
  * Pharmacy service domain business operations.
* **`pharmacy-web`** (13 passing tests in `mvn test`):
  1. `testBusinessRule1_ExpiredBatchSaleIsRejected_ByDirectBatch` -> Passed
  2. `testBusinessRule1_ExpiredStockIsNotDecremented` -> Passed
  3. `testBusinessRule1_NoSuccessfulInvoicePersistedOnExpiredSale` -> Passed
  4. `testBusinessRule2_ScheduledMedicineWithoutPrescriptionReferenceIsRejected` -> Passed
  5. `testBusinessRule2_StockIsUnchangedWhenPrescriptionValidationFails` -> Passed
  6. `testValidSale_UsingEligibleBatch_Succeeds` -> Passed
  7. `testFEFO_ChoosesEarliestExpiringEligibleBatch` -> Passed
  8. `testValidationFailures_ReturnExpectedStructuredError` -> Passed
  9. `testAdminDashboardEndpoint_RequiresAdminRole` -> Passed
  10. `testCashier_CannotAccessPharmacistDashboard` -> Passed
  11. `testUnauthenticatedAccess_ReturnsStructured401` -> Passed
  12. `testAuthentication_WithValidCredentials_ReturnsJwtToken` -> Passed
  13. `contextLoads` -> Passed

### Frontend Tests (7/7 Passing)
* **`pharmacy-frontend`** (7 passing tests in Vitest `src/test/DashboardAndRules.test.jsx`):
  1. `ADMIN sees Admin dashboard and permitted menu items` -> Passed
  2. `PHARMACIST sees Pharmacist dashboard and permitted menu items` -> Passed
  3. `CASHIER sees Cashier dashboard, has no access to staff/purchase controls` -> Passed
  4. `Expired-stock error is shown without hiding navigation menu` -> Passed
  5. `Missing-prescription error is shown without hiding navigation menu` -> Passed
  6. `Failed API request clears loading state and allows retry` -> Passed
  7. `Dashboard widget error does not crash the complete dashboard or sidebar` -> Passed
