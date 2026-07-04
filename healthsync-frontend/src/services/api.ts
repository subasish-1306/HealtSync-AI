import axios from 'axios';
import { Hospital, InventoryItem, Bed, Doctor, LabTest, RedistributionRequest } from '../types';

// Spring Boot API Integration Base URL.
// Easily toggle between local mock data and real backend endpoints.
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Delay helper to simulate network latency for realistic clinical UI loadings
const simulateLatency = <T>(data: T, delay = 500): Promise<T> => {
  return new Promise(resolve => {
    setTimeout(() => resolve(data), delay);
  });
};

// API Ready Service Adapters
export const HospitalService = {
  getAll: async (districtId?: string): Promise<Hospital[]> => {
    if (API_BASE_URL) {
      const response = await api.get<Hospital[]>(`/hospitals`, { params: { districtId } });
      return response.data;
    }
    // Fallback to local mock handled by TanStack wrapper using context database
    return simulateLatency([] as Hospital[]); 
  },
  
  getById: async (id: string): Promise<Hospital> => {
    if (API_BASE_URL) {
      const response = await api.get<Hospital>(`/hospitals/${id}`);
      return response.data;
    }
    return simulateLatency({} as Hospital);
  }
};

export const InventoryService = {
  getAll: async (hospitalId?: string): Promise<InventoryItem[]> => {
    if (API_BASE_URL) {
      const response = await api.get<InventoryItem[]>(`/inventory`, { params: { hospitalId } });
      return response.data;
    }
    return simulateLatency([] as InventoryItem[]);
  },

  updateStock: async (itemId: string, newStock: number): Promise<boolean> => {
    if (API_BASE_URL) {
      const response = await api.put<{ success: boolean }>(`/inventory/${itemId}/stock`, { stockLevel: newStock });
      return response.data.success;
    }
    return simulateLatency(true);
  }
};

export const BedService = {
  getAll: async (hospitalId?: string): Promise<Bed[]> => {
    if (API_BASE_URL) {
      const response = await api.get<Bed[]>(`/beds`, { params: { hospitalId } });
      return response.data;
    }
    return simulateLatency([] as Bed[]);
  },

  update: async (bedId: string, updates: Partial<Bed>): Promise<boolean> => {
    if (API_BASE_URL) {
      const response = await api.put<{ success: boolean }>(`/beds/${bedId}`, updates);
      return response.data.success;
    }
    return simulateLatency(true);
  }
};

export const DoctorService = {
  getAll: async (hospitalId?: string): Promise<Doctor[]> => {
    if (API_BASE_URL) {
      const response = await api.get<Doctor[]>(`/doctors`, { params: { hospitalId } });
      return response.data;
    }
    return simulateLatency([] as Doctor[]);
  },

  updateStatus: async (doctorId: string, status: Doctor['status']): Promise<boolean> => {
    if (API_BASE_URL) {
      const response = await api.put<{ success: boolean }>(`/doctors/${doctorId}/status`, { status });
      return response.data.success;
    }
    return simulateLatency(true);
  }
};

export const LabService = {
  getAll: async (hospitalId?: string): Promise<LabTest[]> => {
    if (API_BASE_URL) {
      const response = await api.get<LabTest[]>(`/lab-tests`, { params: { hospitalId } });
      return response.data;
    }
    return simulateLatency([] as LabTest[]);
  },

  create: async (test: Partial<LabTest>): Promise<LabTest> => {
    if (API_BASE_URL) {
      const response = await api.post<LabTest>(`/lab-tests`, test);
      return response.data;
    }
    return simulateLatency(test as LabTest);
  },

  complete: async (testId: string, results: { value: string, isCritical: boolean, reagentLevelStatus: string }): Promise<boolean> => {
    if (API_BASE_URL) {
      const response = await api.put<{ success: boolean }>(`/lab-tests/${testId}/complete`, results);
      return response.data.success;
    }
    return simulateLatency(true);
  }
};

export const RedistributionService = {
  getAll: async (): Promise<RedistributionRequest[]> => {
    if (API_BASE_URL) {
      const response = await api.get<RedistributionRequest[]>(`/redistribution`);
      return response.data;
    }
    return simulateLatency([] as RedistributionRequest[]);
  },

  create: async (request: Partial<RedistributionRequest>): Promise<RedistributionRequest> => {
    if (API_BASE_URL) {
      const response = await api.post<RedistributionRequest>(`/redistribution`, request);
      return response.data;
    }
    return simulateLatency(request as RedistributionRequest);
  },

  approve: async (id: string): Promise<boolean> => {
    if (API_BASE_URL) {
      const response = await api.post<{ success: boolean }>(`/redistribution/${id}/approve`);
      return response.data.success;
    }
    return simulateLatency(true);
  }
};
