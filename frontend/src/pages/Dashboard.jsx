import React from 'react';
import { Link } from 'react-router-dom';
import { User, Calendar, Ticket, Bell, Shield, Sparkles } from 'lucide-react';

export default function Dashboard({ user }) {
  if (!user) return null;

  return (
    <div style={{ maxWidth: '1100px', margin: '40px auto', padding: '0 20px', width: '100%' }}>
      {/* Welcome Banner */}
      <div className="glass-panel" style={{ padding: '32px', marginBottom: '32px', background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.15), rgba(6, 182, 212, 0.15))', border: '1px solid var(--border-accent)' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '20px', flexWrap: 'wrap' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '8px' }}>
              <h1 style={{ fontSize: '2rem', fontWeight: 800 }}>Welcome back, {user.name}!</h1>
              <span className="badge badge-success">{user.role}</span>
            </div>
            <p style={{ color: 'var(--text-muted)' }}>Manage your event bookings, ticket status, and profile preferences.</p>
          </div>
          <Link to="/events" className="btn btn-primary" style={{ padding: '12px 24px' }}>Browse Events Catalog</Link>
        </div>
      </div>

      {/* Account Shortcuts */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '24px', marginBottom: '40px' }}>
        
        <Link to="/events" style={{ textDecoration: 'none', color: 'inherit' }}>
          <div className="glass-panel glass-panel-hover" style={{ padding: '28px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '14px', marginBottom: '14px' }}>
              <div style={{ background: 'rgba(99, 102, 241, 0.2)', padding: '12px', borderRadius: '12px', color: 'var(--accent-primary)' }}>
                <Calendar size={24} />
              </div>
              <div>
                <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>Upcoming Events</h3>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Explore Catalog</p>
              </div>
            </div>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)', lineHeight: '1.5' }}>
              Discover tech conferences, music shows, workshops, and reserve your seats with instant confirmation.
            </p>
          </div>
        </Link>

        <Link to="/my-bookings" style={{ textDecoration: 'none', color: 'inherit' }}>
          <div className="glass-panel glass-panel-hover" style={{ padding: '28px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '14px', marginBottom: '14px' }}>
              <div style={{ background: 'rgba(6, 182, 212, 0.2)', padding: '12px', borderRadius: '12px', color: 'var(--secondary-accent)' }}>
                <Ticket size={24} />
              </div>
              <div>
                <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>My Bookings</h3>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Ticket Management</p>
              </div>
            </div>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)', lineHeight: '1.5' }}>
              View all your active bookings, payment receipts, and manage confirmed event reservations.
            </p>
          </div>
        </Link>

        <Link to="/notifications" style={{ textDecoration: 'none', color: 'inherit' }}>
          <div className="glass-panel glass-panel-hover" style={{ padding: '28px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '14px', marginBottom: '14px' }}>
              <div style={{ background: 'rgba(16, 185, 129, 0.2)', padding: '12px', borderRadius: '12px', color: 'var(--success-color)' }}>
                <Bell size={24} />
              </div>
              <div>
                <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>Notifications</h3>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Payment Alerts</p>
              </div>
            </div>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)', lineHeight: '1.5' }}>
              Check your real-time payment receipts, booking status updates, and event reminder alerts.
            </p>
          </div>
        </Link>

      </div>
    </div>
  );
}
