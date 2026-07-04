import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { Card, Badge, Button, Input, Select } from '../../components/common';
import { 
  Settings as SettingsIcon, 
  Bell, 
  ShieldCheck, 
  Volume2, 
  Moon, 
  Sliders,
  CheckCircle
} from 'lucide-react';

export const Settings: React.FC = () => {
  const { theme, toggleTheme } = useApp();
  const [isSaved, setIsSaved] = useState(false);
  
  // Alert thresholds
  const [stockThreshold, setStockThreshold] = useState(15);
  const [bedThreshold, setBedThreshold] = useState(85);
  const [reagentThreshold, setReagentThreshold] = useState(20);

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
          System Configuration & Settings
        </h2>
        <p className="text-xs text-muted-foreground uppercase font-bold tracking-wider leading-none mt-0.5">
          Calibrate safety alerts thresholds, themes, and notification triggers
        </p>
      </div>

      {/* Main Split */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Settings categories */}
        <Card className="p-4 md:col-span-1 h-fit">
          <span className="text-[10px] font-bold text-muted-foreground uppercase tracking-widest leading-none mb-2 block">
            System Parameters
          </span>
          <div className="space-y-1 text-xs">
            <button className="flex w-full items-center gap-2 rounded-md bg-primary/10 text-primary font-bold px-2.5 py-2">
              <Sliders className="h-4 w-4" /> Threshold Calibration
            </button>
            <button className="flex w-full items-center gap-2 rounded-md text-muted-foreground hover:bg-muted/40 hover:text-foreground px-2.5 py-2">
              <Bell className="h-4 w-4" /> Push Notifications
            </button>
            <button className="flex w-full items-center gap-2 rounded-md text-muted-foreground hover:bg-muted/40 hover:text-foreground px-2.5 py-2">
              <ShieldCheck className="h-4 w-4" /> Security Certificates
            </button>
          </div>
        </Card>

        {/* Configurations form */}
        <Card className="p-6 md:col-span-2">
          <div className="mb-4">
            <h3 className="text-sm font-bold text-foreground">
              Threshold Alarm Configurations
            </h3>
            <p className="text-xs text-muted-foreground mt-0.5">
              Set the values at which AI safety incidents are dispatched to the central database.
            </p>
          </div>

          <form onSubmit={handleSave} className="space-y-4">
            {isSaved && (
              <div className="rounded-md bg-success/10 border border-success/20 p-3 text-xs text-success font-semibold flex items-center gap-1.5">
                <CheckCircle className="h-4 w-4" /> Calibration profiles saved successfully.
              </div>
            )}

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input
                label="Medicine Stockout Refill Warning (%)"
                type="number"
                value={stockThreshold}
                onChange={(e) => setStockThreshold(Number(e.target.value))}
                min={5}
                max={50}
                helperText="Triggers warning when stock drops below this % of monthly demand."
              />
              <Input
                label="Bed Occupancy Alarm Threshold (%)"
                type="number"
                value={bedThreshold}
                onChange={(e) => setBedThreshold(Number(e.target.value))}
                min={50}
                max={98}
                helperText="Dispatches warning when capacity breaches this percentage."
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
              <Input
                label="Reagent Kit Outage Limit (%)"
                type="number"
                value={reagentThreshold}
                onChange={(e) => setReagentThreshold(Number(e.target.value))}
                min={5}
                max={40}
                helperText="Triggers lab warning when reagent level falls below safety margin."
              />
              
              <div className="flex flex-col justify-end pb-1.5">
                <span className="mb-1.5 block text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                  Interface Theme Mode
                </span>
                <Button
                  type="button"
                  variant="outline"
                  onClick={toggleTheme}
                  className="h-10 text-xs font-semibold gap-2 border-border/80 w-full"
                >
                  <Moon className="h-4 w-4" /> Toggle System Dark/Light Mode
                </Button>
              </div>
            </div>

            <div className="flex justify-end pt-4 border-t border-border/80 mt-4">
              <Button type="submit" className="text-xs font-bold shadow-md">
                Save Parameter Calibrations
              </Button>
            </div>
          </form>
        </Card>
      </div>
    </div>
  );
};
export default Settings;
