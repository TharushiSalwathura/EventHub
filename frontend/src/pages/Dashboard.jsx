import React from 'react';
import { Link } from 'react-router-dom';
import { User, Shield, Key, Calendar, Ticket, CheckCircle2, Server } from 'lucide-react';

export default function Dashboard({ user }) {
  if (!user) return null;

  return (
    <div style={{ maxWidth: '1100px', margin: '40px auto', padding: '0 20px', width: '100%' }}>
      {/* Welcome Banner */}
      <div className="glass-panel" style={{ padding: '32px', marginBottom: '32px', background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.15), rgba(6, 182, 212, 0.15))', border: '1px solid var(--border-accent)' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyBetween: 'space-between', gap: '20px', flexWrap: 'wrap' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '8px' }}>
              <h1 style={{ fontSize: '2rem', fontWeight: 800 }}>Welcome back, {user.name}!</h1>
              <span className="badge badge-success">{user.role}</span>
            </div>
            <p style={{ color: 'var(--text-muted)' }}>You are logged in through <b>API Gateway (:8080)</b> with a valid <b>JWT Access Token</b>.</p>
          </div>
          <Link to="/events" className="btn btn-primary">Browse Events Catalog</Link>
        </div>
      </div>

      {/* Grid Status Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '24px', marginBottom: '40px' }}>
        <div className="glass-panel glass-panel-hover" style={{ padding: '24px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '14px', marginBottom: '16px' }}>
            <div style={{ background: 'rgba(99, 102, 241, 0.2)', padding: '10px', borderRadius: '12px', color: 'var(--accent-primary)' }}>
              <Key size={24} />
            </div>
            <div>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>OAuth2 / JWT Token</h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Security Architecture</p>
            </div>
          </div>
          <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)', lineHeight: '1.5' }}>
            Your session is secured using HMAC SHA-256 JWT tokens. Token details are stored in client memory and forwarded to internal services via HTTP Bearer header.
          </p>
        </div>

        <div className="glass-panel glass-panel-hover" style={{ padding: '24px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '14px', marginBottom: '16px' }}>
            <div style={{ background: 'rgba(6, 182, 212, 0.2)', padding: '10px', borderRadius: '12px', color: 'var(--secondary-accent)' }}>
              <Server size={24} />
            </div>
            <div>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>Internal API Key</h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Gateway Microservice Protection</p>
            </div>
          </div>
          <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)', lineHeight: '1.5' }}>
            Downstream services enforce <code style={{ background: 'rgba(255,255,255,0.1)', padding: '2px 6px', borderRadius: '4px' }}>X-API-KEY</code> headers forwarded exclusively by the API Gateway.
          </p>
        </div>

        <div className="glass-panel glass-panel-hover" style={{ padding: '24px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '14px', marginBottom: '16px' }}>
            <div style={{ background: 'rgba(16, 185, 129, 0.2)', padding: '10px', borderRadius: '12px', color: 'var(--success-color)' }}>
              <Ticket size={24} />
            </div>
            <div>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>Event Booking</h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Member 2 & 3 Services</p>
            </div>
          </div>
          <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)', lineHeight: '1.5' }}>
            Select events, reserve tickets, and execute mock payment processing with instant notification generation.
          </p>
        </div>
      </div>
    </div>
  );
}
