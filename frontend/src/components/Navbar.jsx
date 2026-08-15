import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Calendar, User, LogOut, Bell, Ticket, Shield } from 'lucide-react';

export default function Navbar({ user, onLogout }) {
  const navigate = useNavigate();

  return (
    <nav className="glass-panel" style={{ borderRadius: 0, borderTop: 0, borderLeft: 0, borderRight: 0, padding: '16px 32px', position: 'sticky', top: 0, zIndex: 100 }}>
      <div style={{ maxWidth: '1200px', margin: '0 auto', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div style={{ background: 'linear-gradient(135deg, #6366f1, #06b6d4)', width: '36px', height: '36px', borderRadius: '10px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Calendar size={22} color="white" />
          </div>
          <span style={{ fontSize: '1.4rem', fontWeight: 800, background: 'linear-gradient(90deg, #f9fafb, #9ca3af)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            EventHub
          </span>
          <span className="badge badge-info" style={{ marginLeft: '4px' }}>Gateway :8080</span>
        </Link>

        <div style={{ display: 'flex', alignItems: 'center', gap: '24px' }}>
          <Link to="/events" style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.95rem', fontWeight: 500 }}>
            <Calendar size={18} color="#9ca3af" /> Events
          </Link>

          {user ? (
            <>
              <Link to="/my-bookings" style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.95rem', fontWeight: 500 }}>
                <Ticket size={18} color="#9ca3af" /> My Bookings
              </Link>
              <Link to="/notifications" style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.95rem', fontWeight: 500 }}>
                <Bell size={18} color="#9ca3af" /> Notifications
              </Link>
              <Link to="/dashboard" style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.95rem', fontWeight: 500 }}>
                <Shield size={18} color="#9ca3af" /> Profile
              </Link>
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px', paddingLeft: '12px', borderLeft: '1px solid var(--border-color)' }}>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: '0.9rem', fontWeight: 600 }}>{user.name}</div>
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
