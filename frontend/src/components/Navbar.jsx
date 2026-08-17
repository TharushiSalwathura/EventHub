import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Calendar, User, LogOut, Bell, Ticket, Sparkles, CheckCircle2, X } from 'lucide-react';

export default function Navbar({ user, onLogout, notifications = [] }) {
  const navigate = useNavigate();
  const [showNotificationDropdown, setShowNotificationDropdown] = useState(false);

  const unreadCount = notifications.length;

  return (
    <nav className="glass-panel" style={{ borderRadius: 0, borderTop: 0, borderLeft: 0, borderRight: 0, padding: '16px 32px', position: 'sticky', top: 0, zIndex: 100 }}>
      <div style={{ maxWidth: '1200px', margin: '0 auto', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        
        {/* Brand Logo */}
        <Link to="/events" style={{ display: 'flex', alignItems: 'center', gap: '10px', textDecoration: 'none' }}>
          <div style={{ background: 'linear-gradient(135deg, #6366f1, #06b6d4)', width: '38px', height: '38px', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 4px 12px rgba(99, 102, 241, 0.4)' }}>
            <Calendar size={22} color="white" />
          </div>
          <span style={{ fontSize: '1.4rem', fontWeight: 800, background: 'linear-gradient(90deg, #ffffff, #9ca3af)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            EventHub
          </span>
        </Link>

        {/* Navigation Links */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '24px' }}>
          <Link to="/events" style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.95rem', fontWeight: 600, color: 'var(--text-main)', textDecoration: 'none' }}>
            <Calendar size={18} color="var(--accent-primary)" /> Events
          </Link>

          {user ? (
            <>
              <Link to="/my-bookings" style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.95rem', fontWeight: 600, color: 'var(--text-main)', textDecoration: 'none' }}>
                <Ticket size={18} color="var(--secondary-accent)" /> My Bookings
              </Link>

              {/* Notification Bell Dropdown */}
              <div style={{ position: 'relative' }}>
                <button
                  onClick={() => setShowNotificationDropdown(!showNotificationDropdown)}
                  style={{ background: 'transparent', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '6px', position: 'relative', color: 'var(--text-main)' }}
                >
                  <Bell size={20} color={unreadCount > 0 ? "var(--warning-color)" : "#9ca3af"} />
                  <span style={{ fontSize: '0.95rem', fontWeight: 600 }}>Notifications</span>
                  {unreadCount > 0 && (
                    <span style={{ background: '#ef4444', color: 'white', fontSize: '0.7rem', fontWeight: 800, padding: '2px 6px', borderRadius: '10px', marginLeft: '-2px' }}>
                      {unreadCount}
                    </span>
                  )}
                </button>

                {/* Notifications Popup Drawer */}
                {showNotificationDropdown && (
                  <div className="glass-panel" style={{ position: 'absolute', right: 0, top: '40px', width: '360px', padding: '16px', background: 'rgba(15, 23, 42, 0.95)', border: '1px solid rgba(255,255,255,0.15)', boxShadow: '0 20px 40px rgba(0,0,0,0.6)', zIndex: 200, borderRadius: '16px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px', borderBottom: '1px solid rgba(255,255,255,0.1)', paddingBottom: '8px' }}>
                      <span style={{ fontWeight: 700, fontSize: '0.95rem' }}>Recent Notifications</span>
                      <button onClick={() => setShowNotificationDropdown(false)} style={{ background: 'transparent', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}><X size={16} /></button>
                    </div>

                    {notifications.length === 0 ? (
                      <div style={{ textAlign: 'center', padding: '20px 0', color: 'var(--text-muted)', fontSize: '0.85rem' }}>
                        No notifications yet. Book an event to receive payment alerts!
                      </div>
                    ) : (
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', maxHeight: '280px', overflowY: 'auto' }}>
                        {notifications.slice(0, 5).map((n, idx) => (
                          <div key={idx} style={{ background: 'rgba(255,255,255,0.04)', padding: '10px 12px', borderRadius: '8px', borderLeft: '3px solid var(--success-color)' }}>
                            <div style={{ fontSize: '0.85rem', fontWeight: 600, color: 'white', marginBottom: '2px' }}>{n.title || 'Payment Successful'}</div>
                            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{n.message}</div>
                            <span style={{ fontSize: '0.7rem', color: 'var(--text-dim)', marginTop: '4px', display: 'block' }}>{n.timestamp || 'Just now'}</span>
                          </div>
                        ))}
                      </div>
                    )}

                    <div style={{ marginTop: '12px', textCenter: 'center', borderTop: '1px solid rgba(255,255,255,0.08)', paddingTop: '8px' }}>
                      <Link to="/notifications" onClick={() => setShowNotificationDropdown(false)} style={{ fontSize: '0.8rem', color: 'var(--accent-primary)', fontWeight: 600, textDecoration: 'none', display: 'block', textAlign: 'center' }}>
                        View All Notifications →
                      </Link>
                    </div>
                  </div>
                )}
              </div>

              <Link to="/dashboard" style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.95rem', fontWeight: 600, color: 'var(--text-main)', textDecoration: 'none' }}>
                <User size={18} color="var(--accent-primary)" /> Profile
              </Link>

              <div style={{ display: 'flex', alignItems: 'center', gap: '12px', paddingLeft: '12px', borderLeft: '1px solid var(--border-color)' }}>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: '0.9rem', fontWeight: 700 }}>{user.name}</div>
                  <span className="badge badge-success" style={{ fontSize: '0.65rem' }}>{user.role}</span>
                </div>
                <button onClick={() => { onLogout(); navigate('/login'); }} className="btn btn-secondary" style={{ padding: '8px 12px' }}>
                  <LogOut size={16} />
                </button>
              </div>
            </>
          ) : (
            <div style={{ display: 'flex', gap: '12px' }}>
              <Link to="/login" className="btn btn-secondary">Login</Link>
              <Link to="/register" className="btn btn-primary">Register</Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
}
