import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useApp } from '../context/AppContext';
import { 
  HospitalService, InventoryService, BedService, 
  DoctorService, LabService, RedistributionService 
} from '../services/api';
import { Bed, Doctor, LabTest, RedistributionRequest } from '../types';

export const useHospitalsQuery = (districtId?: string) => {
  const { hospitals } = useApp();
  return useQuery({
    queryKey: ['hospitals', districtId, hospitals],
    queryFn: async () => {
      // If base URL exists, use API, otherwise fallback to context reactive state
      if (import.meta.env.VITE_API_BASE_URL) {
        return HospitalService.getAll(districtId);
      }
      return districtId 
        ? hospitals.filter(h => h.district === districtId)
        : hospitals;
    },
    staleTime: 5 * 60 * 1000,
  });
};

export const useInventoryQuery = (hospitalId?: string) => {
  const { inventory } = useApp();
  return useQuery({
    queryKey: ['inventory', hospitalId, inventory],
    queryFn: async () => {
      if (import.meta.env.VITE_API_BASE_URL) {
        return InventoryService.getAll(hospitalId);
      }
      return hospitalId
        ? inventory.filter(item => item.hospitalId === hospitalId)
        : inventory;
    },
  });
};

export const useBedsQuery = (hospitalId?: string) => {
  const { beds } = useApp();
  return useQuery({
    queryKey: ['beds', hospitalId, beds],
    queryFn: async () => {
      if (import.meta.env.VITE_API_BASE_URL) {
        return BedService.getAll(hospitalId);
      }
      return hospitalId
        ? beds.filter(b => b.hospitalId === hospitalId)
        : beds;
    },
  });
};

export const useDoctorsQuery = (hospitalId?: string) => {
  const { doctors } = useApp();
  return useQuery({
    queryKey: ['doctors', hospitalId, doctors],
    queryFn: async () => {
      if (import.meta.env.VITE_API_BASE_URL) {
        return DoctorService.getAll(hospitalId);
      }
      return hospitalId
        ? doctors.filter(d => d.hospitalId === hospitalId)
        : doctors;
    },
  });
};

export const useRedistributionsQuery = () => {
  const { redistributionRequests } = useApp();
  return useQuery({
    queryKey: ['redistributions', redistributionRequests],
    queryFn: async () => {
      if (import.meta.env.VITE_API_BASE_URL) {
        return RedistributionService.getAll();
      }
      return redistributionRequests;
    },
  });
};

export const useLabTestsQuery = (hospitalId?: string) => {
  const { laboratoryTests } = useApp();
  return useQuery({
    queryKey: ['labTests', hospitalId, laboratoryTests],
    queryFn: async () => {
      if (import.meta.env.VITE_API_BASE_URL) {
        return LabService.getAll(hospitalId);
      }
      return hospitalId
        ? laboratoryTests.filter(t => t.hospitalId === hospitalId)
        : laboratoryTests;
    },
  });
};

export const useAlertsQuery = (hospitalId?: string) => {
  const { alerts } = useApp();
  return useQuery({
    queryKey: ['alerts', hospitalId, alerts],
    queryFn: async () => {
      return hospitalId
        ? alerts.filter(a => a.targetFacilityId === hospitalId)
        : alerts;
    },
  });
};

// Mutations
export const useUpdateStockMutation = () => {
  const { updateInventoryStock } = useApp();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ itemId, newStock }: { itemId: string, newStock: number }) => {
      await InventoryService.updateStock(itemId, newStock);
      return { itemId, newStock };
    },
    onSuccess: (data) => {
      updateInventoryStock(data.itemId, data.newStock);
      queryClient.invalidateQueries({ queryKey: ['inventory'] });
      queryClient.invalidateQueries({ queryKey: ['alerts'] });
    },
  });
};

export const useTransferBedMutation = () => {
  const { transferBed } = useApp();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ bedId, updates }: { bedId: string, updates: Partial<Bed> }) => {
      await BedService.update(bedId, updates);
      return { bedId, updates };
    },
    onSuccess: (data) => {
      transferBed(data.bedId, data.updates);
      queryClient.invalidateQueries({ queryKey: ['beds'] });
      queryClient.invalidateQueries({ queryKey: ['hospitals'] });
      queryClient.invalidateQueries({ queryKey: ['alerts'] });
    },
  });
};

export const useUpdateDoctorMutation = () => {
  const { updateDoctorStatus } = useApp();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ doctorId, status }: { doctorId: string, status: Doctor['status'] }) => {
      await DoctorService.updateStatus(doctorId, status);
      return { doctorId, status };
    },
    onSuccess: (data) => {
      updateDoctorStatus(data.doctorId, data.status);
      queryClient.invalidateQueries({ queryKey: ['doctors'] });
      queryClient.invalidateQueries({ queryKey: ['hospitals'] });
    },
  });
};

export const useRedistributionMutation = () => {
  const { createRedistributionRequest } = useApp();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (request: { medicineName: string, quantity: number, fromHospitalId: string, toHospitalId: string, urgency: 'High' | 'Medium' | 'Low' }) => {
      await RedistributionService.create(request);
      return request;
    },
    onSuccess: (data) => {
      createRedistributionRequest(data.medicineName, data.quantity, data.fromHospitalId, data.toHospitalId, data.urgency);
      queryClient.invalidateQueries({ queryKey: ['redistributions'] });
    },
  });
};

export const useApproveRedistributionMutation = () => {
  const { approveRedistribution } = useApp();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: string) => {
      await RedistributionService.approve(id);
      return id;
    },
    onSuccess: (id) => {
      approveRedistribution(id);
      queryClient.invalidateQueries({ queryKey: ['redistributions'] });
    },
  });
};
