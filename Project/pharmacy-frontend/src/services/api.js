/**
 * Centralized API Service for Pharmacy Store Management System.
 * Enforces resilient error handling, session preservation on business/validation errors,
 * and correlation ID tracking.
 */

const API_BASE_URL = '/api';

export class ApiError extends Error {
  constructor(status, errorCode, message, fieldErrors = [], correlationId = null) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.errorCode = errorCode;
    this.fieldErrors = fieldErrors;
    this.correlationId = correlationId;
  }
}

let onUnauthorizedCallback = null;

export const setOnUnauthorizedCallback = (callback) => {
  onUnauthorizedCallback = callback;
};

export async function request(endpoint, options = {}) {
  const token = localStorage.getItem('pharmacy_token');
  const correlationId = 'req-' + Math.random().toString(36).substring(2, 11);

  const headers = {
    'Content-Type': 'application/json',
    'X-Correlation-Id': correlationId,
    ...(options.headers || {}),
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const config = {
    ...options,
    headers,
  };

  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

    // Parse JSON safely
    let data;
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      data = await response.json();
    } else {
      data = await response.text();
    }

    if (!response.ok) {
      // 401 Unauthorized handling: only clear session if genuinely expired on an authenticated route
      if (response.status === 401 && endpoint !== '/auth/login') {
        if (typeof onUnauthorizedCallback === 'function') {
          onUnauthorizedCallback();
        }
      }

      // Extract structured error details
      const errorCode = data && data.errorCode ? data.errorCode : `HTTP_${response.status}`;
      const message = data && data.message ? data.message : `Request failed with status ${response.status}`;
      const fieldErrors = data && data.fieldErrors ? data.fieldErrors : [];
      const corrId = data && data.correlationId ? data.correlationId : correlationId;

      throw new ApiError(response.status, errorCode, message, fieldErrors, corrId);
    }

    return data;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    // Network / connection errors
    throw new ApiError(
      0,
      'NETWORK_ERROR',
      'Unable to connect to the backend server. Please check your network or try again.',
      [],
      correlationId
    );
  }
}

export const api = {
  // Auth
  login: (username, password) => request('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  }),
  getMe: () => request('/auth/me', { method: 'GET' }),

  // Dashboards
  getAdminDashboard: () => request('/dashboard/admin', { method: 'GET' }),
  getPharmacistDashboard: (threshold = 20) => request(`/dashboard/pharmacist?threshold=${threshold}`, { method: 'GET' }),
  getCashierDashboard: () => request('/dashboard/cashier', { method: 'GET' }),

  // Medicines
  getMedicines: () => request('/medicines', { method: 'GET' }),
  getMedicineById: (id) => request(`/medicines/${id}`, { method: 'GET' }),
  addMedicine: (data) => request('/medicines', { method: 'POST', body: JSON.stringify(data) }),

  // Suppliers
  getSuppliers: () => request('/suppliers', { method: 'GET' }),
  getSupplierById: (id) => request(`/suppliers/${id}`, { method: 'GET' }),
  addSupplier: (data) => request('/suppliers', { method: 'POST', body: JSON.stringify(data) }),

  // Batches & Inventory
  getBatches: () => request('/batches', { method: 'GET' }),
  getBatchesForMedicine: (medicineId) => request(`/batches/medicine/${medicineId}`, { method: 'GET' }),
  recordPurchase: (data) => request('/batches/purchase', { method: 'POST', body: JSON.stringify(data) }),

  // Invoices & Billing
  getInvoices: () => request('/invoices', { method: 'GET' }),
  createInvoice: (data) => request('/invoices', { method: 'POST', body: JSON.stringify(data) }),
};
