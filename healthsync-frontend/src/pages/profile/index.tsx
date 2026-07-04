import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { Card, Badge, Button, Input } from '../../components/common';
import { 
  User, 
  Mail, 
  ShieldCheck, 
  Building2, 
  MapPin, 
  Phone,
  FileCheck2,
  CheckCircle
} from 'lucide-react';

export const UserProfile: React.FC = () => {
  const { currentUser } = useApp();
  const [isSaved, setIsSaved] = useState(false);
  const [userName, setUserName] = useState(currentUser?.name || '');
  const [contact, setContact] = useState('+91 98765 43210');

  if (!currentUser) return null;

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    setIsSaved(true);
    setTimeout(() => setIsSaved(false), 2000);
  };

  return (
    <div className="space-y-6">
      {/* Title */}
      <div className="flex flex-col gap-2">
        <h2 className="text-xl font-bold tracking-tight text-foreground">
          User Account Directory
        </h2>
        <p className="text-xs text-muted-foreground uppercase font-bold tracking-wider leading-none mt-0.5">
          Access group licensing and user profile details
        </p>
      </div>

      {/* Main Split */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Left Side: Avatar Card */}
        <Card className="p-6 flex flex-col items-center text-center">
          <img
            src={currentUser.avatar || 'https://api.dicebear.com/7.x/adventurer/svg?seed=user'}
            alt="Profile Avatar"
            className="h-28 w-28 rounded-full border border-border bg-muted object-cover mb-4"
          />
          <h3 className="text-base font-bold text-foreground truncate max-w-[200px]">
            {userName}
          </h3>
          <p className="text-xs text-muted-foreground mt-0.5">{currentUser.email}</p>
          
          <Badge variant="default" className="mt-3 capitalize">
            {currentUser.role.toLowerCase().replace('_', ' ')}
          </Badge>

          <div className="w-full border-t border-border/80 pt-4 mt-4 space-y-2.5 text-xs text-left">
            <div className="flex justify-between items-center">
              <span className="text-muted-foreground font-medium flex items-center gap-1.5">
                <ShieldCheck className="h-4 w-4 text-primary" /> Authority:
              </span>
              <span className="font-bold text-foreground">Active Roster</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-muted-foreground font-medium flex items-center gap-1.5">
                <Building2 className="h-4 w-4 text-primary" /> Tenant Node:
              </span>
              <span className="font-bold text-foreground truncate max-w-[140px]">
                {currentUser.facilityName || 'District HQ'}
              </span>
            </div>
          </div>
        </Card>

        {/* Right Side: Account Editor */}
        <Card className="p-6 md:col-span-2">
          <div className="mb-4">
            <h3 className="text-sm font-bold text-foreground">
              Modify Officer Credentials
            </h3>
            <p className="text-xs text-muted-foreground mt-0.5">
              Update contact directories for administrative escalation grids.
            </p>
          </div>

          <form onSubmit={handleSave} className="space-y-4">
            {isSaved && (
              <div className="rounded-md bg-success/10 border border-success/20 p-3 text-xs text-success font-semibold flex items-center gap-1.5">
                <CheckCircle className="h-4 w-4" /> Account credentials synced successfully.
              </div>
            )}

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input
                label="Full Legal Name"
                value={userName}
                onChange={(e) => setUserName(e.target.value)}
                required
              />
              <Input
                label="Clinical Phone Directory"
                value={contact}
                onChange={(e) => setContact(e.target.value)}
                required
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input
                label="Registered Health Domain Email"
                value={currentUser.email}
                disabled
                helperText="Domain authentication locked by department security."
              />
              <Input
                label="District License / Registry Index"
                value="SEC-HS-2026-99"
                disabled
              />
            </div>

            <div className="flex justify-end pt-2">
              <Button type="submit" className="text-xs font-bold shadow-md">
                Sync Profile Credentials
              </Button>
            </div>
          </form>
        </Card>
      </div>
    </div>
  );
};
export default UserProfile;
