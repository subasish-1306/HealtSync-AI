import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useApp } from '../../context/AppContext';
import { ThemeToggle, Badge, Button } from '../common';
import { 
  Bell, 
  MessageSquareCode, 
  Search, 
  Menu, 
  User, 
  LogOut, 
  Sparkles,
  ShieldCheck
} from 'lucide-react';

interface NavbarProps {
  sidebarOpen: boolean;
  setSidebarOpen: (open: boolean) => void;
}

export const Navbar: React.FC<NavbarProps> = ({ sidebarOpen, setSidebarOpen }) => {
  const { currentUser, alerts, acknowledgeAlert, isChatOpen, setChatOpen, logout } = useApp();
  const [showNotifications, setShowNotifications] = useState(false);
  const [showProfile, setShowProfile] = useState(false);
  const notificationRef = useRef<HTMLDivElement>(null);
  const profileRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  const activeAlerts = alerts.filter(a => !a.acknowledged);

  useEffect(() => {
    const handleOutsideClick = (e: MouseEvent) => {
      if (notificationRef.current && !notificationRef.current.contains(e.target as Node)) {
        setShowNotifications(false);
      }
      if (profileRef.current && !profileRef.current.contains(e.target as Node)) {
        setShowProfile(false);
      }
    };
    document.addEventListener('mousedown', handleOutsideClick);
    return () => document.removeEventListener('mousedown', handleOutsideClick);
  }, []);

  const handleAcknowledgeAlert = (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    acknowledgeAlert(id);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="sticky top-0 z-10 flex h-14 w-full items-center justify-between border-b border-border bg-card/85 backdrop-blur-md px-4 shadow-fluentSm">
      {/* Left side: Hamburger & Navigation breadcrumbs */}
      <div className="flex items-center gap-3">
        <button
          onClick={() => setSidebarOpen(!sidebarOpen)}
          className="flex h-9 w-9 items-center justify-center rounded-md text-muted-foreground hover:bg-muted/40 hover:text-foreground active:scale-95 md:hidden"
        >
          <Menu className="h-5 w-5" />
        </button>
        
        {/* Dynamic Context Header */}
        <div className="hidden items-center gap-1.5 text-sm md:flex">
          <span className="font-semibold text-foreground/80">Operations Command</span>
          <span className="text-muted-foreground">/</span>
          <span className="text-muted-foreground capitalize">
            {currentUser?.role.toLowerCase().replace('_', ' ')}
          </span>
        </div>
      </div>

      {/* Right side: Operations controls */}
      <div className="flex items-center gap-2">
        {/* AI Assistant Quick Toggle */}
        <Button
          variant={isChatOpen ? 'primary' : 'outline'}
          size="sm"
          onClick={() => setChatOpen(!isChatOpen)}
          className="h-9 px-3 gap-1.5 hover:fluent-shadow-md text-xs font-semibold"
        >
          <Sparkles className="h-3.5 w-3.5" />
          <span className="hidden sm:inline">AI Operations Chat</span>
        </Button>

        {/* Global Search Bar (Visual Placeholder) */}
        <div className="relative hidden w-48 lg:block xl:w-64">
          <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground/60" />
          <input
            type="search"
            placeholder="Search facility databases..."
            className="h-9 w-full rounded-md border border-input bg-muted/20 pl-8 pr-3 text-xs focus:bg-background focus:outline-none focus:ring-1 focus:ring-ring"
          />
        </div>

        {/* Theme Toggle */}
        <ThemeToggle />

        {/* Alert Notifications Center */}
        <div className="relative" ref={notificationRef}>
          <Button
            variant="ghost"
            size="icon"
            onClick={() => setShowNotifications(!showNotifications)}
            className="relative h-9 w-9 text-muted-foreground hover:text-foreground"
            title="System alerts ledger"
          >
            <Bell className="h-5 w-5" />
            {activeAlerts.length > 0 && (
              <span className="absolute right-1 top-1 flex h-4 w-4 items-center justify-center rounded-full bg-destructive text-[9px] font-bold text-destructive-foreground">
                {activeAlerts.length}
              </span>
            )}
          </Button>

          {/* Notifications Dropdown Panel */}
          {showNotifications && (
            <div className="absolute right-0 mt-2 w-80 rounded-lg border border-border bg-card shadow-fluentLg p-1 z-30 animate-fade-in max-h-[400px] flex flex-col">
              <div className="flex items-center justify-between border-b border-border px-3.5 py-2">
                <span className="text-xs font-bold uppercase tracking-wider text-muted-foreground">
                  System Alerts ({activeAlerts.length})
                </span>
                {activeAlerts.length > 0 && (
                  <button
                    onClick={() => navigate('/alerts')}
                    className="text-[10px] font-semibold text-primary hover:underline"
                  >
                    View All
                  </button>
                )}
              </div>
              
              <div className="flex-1 overflow-y-auto py-1 divide-y divide-border/60">
                {activeAlerts.length === 0 ? (
                  <div className="flex flex-col items-center justify-center py-6 text-center text-muted-foreground">
                    <ShieldCheck className="h-8 w-8 text-success mb-1" />
                    <p className="text-xs font-semibold text-foreground/80">All systems secure</p>
                    <p className="text-[10px] text-muted-foreground">No active operation alerts</p>
                  </div>
                ) : (
                  activeAlerts.slice(0, 5).map((alert) => (
                    <div
                      key={alert.id}
                      onClick={() => {
                        setShowNotifications(false);
                        navigate('/alerts');
                      }}
                      className="flex flex-col items-start gap-1 p-3 hover:bg-muted/30 cursor-pointer transition-colors"
                    >
                      <div className="flex w-full items-center justify-between">
                        <Badge
                          variant={
                            alert.type === 'Critical' ? 'destructive' : 'warning'
                          }
                          className="text-[9px] scale-90 -translate-x-1"
                        >
                          {alert.type}
                        </Badge>
                        <span className="text-[9px] text-muted-foreground">{alert.timestamp}</span>
                      </div>
                      <p className="text-xs font-medium text-foreground/90 line-clamp-2 leading-tight">
                        {alert.message}
                      </p>
                      <div className="mt-1 flex w-full justify-between items-center text-[10px] text-muted-foreground">
                        <span className="truncate max-w-[140px]">{alert.targetFacilityName}</span>
                        <button
                          onClick={(e) => handleAcknowledgeAlert(alert.id, e)}
                          className="text-primary font-semibold hover:underline"
                        >
                          Dismiss
                        </button>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}
        </div>

        {/* Profile Controls Dropdown */}
        <div className="relative border-l border-border/80 pl-2 ml-1" ref={profileRef}>
          <button
            onClick={() => setShowProfile(!showProfile)}
            className="flex items-center gap-2 hover:opacity-90 active:scale-[0.98]"
          >
            <img
              src={currentUser?.avatar || 'https://api.dicebear.com/7.x/adventurer/svg?seed=admin'}
              alt="Profile"
              className="h-8 w-8 rounded-full border border-border bg-muted object-cover"
            />
            <div className="hidden flex-col items-start text-left xl:flex">
              <span className="text-xs font-semibold text-foreground/90 max-w-[120px] truncate leading-none">
                {currentUser?.name}
              </span>
              <span className="text-[10px] text-muted-foreground leading-none mt-0.5 capitalize">
                {currentUser?.role.toLowerCase().replace('_', ' ')}
              </span>
            </div>
          </button>

          {/* Profile Dropdown Panel */}
          {showProfile && (
            <div className="absolute right-0 mt-2 w-48 rounded-lg border border-border bg-card shadow-fluentLg p-1 z-30 animate-fade-in">
              <div className="px-3.5 py-2 border-b border-border text-xs">
                <p className="font-semibold text-foreground truncate">{currentUser?.name}</p>
                <p className="text-muted-foreground truncate">{currentUser?.email}</p>
              </div>
              <div className="py-1">
                <button
                  onClick={() => {
                    setShowProfile(false);
                    navigate('/profile');
                  }}
                  className="flex w-full items-center gap-2 px-3 py-2 text-xs font-medium text-foreground hover:bg-muted/40 transition-colors"
                >
                  <User className="h-4 w-4 text-muted-foreground" />
                  My Profile
                </button>
                <button
                  onClick={handleLogout}
                  className="flex w-full items-center gap-2 px-3 py-2 text-xs font-medium text-destructive hover:bg-destructive/10 transition-colors"
                >
                  <LogOut className="h-4 w-4 text-destructive" />
                  Logout
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
