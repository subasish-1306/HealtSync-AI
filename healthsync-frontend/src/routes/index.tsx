import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useApp } from '../context/AppContext';
import { AppLayout } from '../components/layout';
import { Login, Register, ForgotPassword } from '../pages/auth';
import { SuperAdminDashboard, DistrictAdminDashboard, HospitalAdminDashboard } from '../pages/dashboard';
import { GovernmentCommandCenter } from '../pages/district';
import { MedicineInventory } from '../pages/inventory';
import { BedManagement } from '../pages/beds';
import { DoctorAttendance } from '../pages/doctors';
import { PatientFootfall } from '../pages/patients';
import { LaboratoryManagement } from '../pages/laboratory';
import { ResourceRedistribution } from '../pages/ResourceRedistribution';
import { AIForecast } from '../pages/AIForecast';
import { AlertsCenter } from '../pages/AlertsCenter';
import { UserProfile } from '../pages/profile';
import { Settings } from '../pages/settings';
import { Reports } from '../pages/reports';

// Redirect helper based on active user authorization levels
const HomeRedirect: React.FC = () => {
  const { currentUser } = useApp();
  
  if (!currentUser) return <Navigate to="/login" replace />;
  
  if (currentUser.role === 'SUPER_ADMIN') {
    return <Navigate to="/dashboard/super" replace />;
  } else if (currentUser.role === 'DISTRICT_ADMIN') {
    return <Navigate to="/dashboard/district" replace />;
  } else {
    return <Navigate to="/dashboard/hospital" replace />;
  }
};

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      {/* Authentication */}
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />

      {/* Main command shell */}
      <Route path="/" element={<AppLayout />}>
        <Route index element={<HomeRedirect />} />
        
        {/* Dashboards */}
        <Route path="dashboard/super" element={<SuperAdminDashboard />} />
        <Route path="dashboard/district" element={<GovernmentCommandCenter />} />
        <Route path="dashboard/hospital" element={<HospitalAdminDashboard />} />

        {/* Clinical Modules */}
        <Route path="inventory" element={<MedicineInventory />} />
        <Route path="beds" element={<BedManagement />} />
        <Route path="doctors" element={<DoctorAttendance />} />
        <Route path="patients" element={<PatientFootfall />} />
        <Route path="laboratory" element={<LaboratoryManagement />} />
        
        {/* Operations */}
        <Route path="redistribution" element={<ResourceRedistribution />} />
        <Route path="forecast" element={<AIForecast />} />
        <Route path="alerts" element={<AlertsCenter />} />
        
        {/* General */}
        <Route path="profile" element={<UserProfile />} />
        <Route path="settings" element={<Settings />} />
        <Route path="reports" element={<Reports />} />

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
};
