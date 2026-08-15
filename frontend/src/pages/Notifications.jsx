import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Bell, CheckCircle2 } from 'lucide-react';

export default function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadNotifications();
  }, []);

  const loadNotifications = async () => {
    setLoading(true);
    try {
      const data = await api.getNotifications();
      setNotifications(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '900px', margin: '40px auto', padding: '0 20px', width: '100%' }}>
      <div style={{ marginBottom: '32px' }}>
        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, marginBottom: '8px' }}>Notifications & Alerts</h1>
        <p style={{ color: 'var(--text-muted)' }}>Managed by Member 4 Notification Service (`/notifications`)</p>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>Loading notifications...</div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {notifications.map((n) => (
            <div key={n.id} className="glass-panel" style={{ padding: '20px', display: 'flex', alignItems: 'flex-start', gap: '16px' }}>
              <div style={{ background: 'rgba(6, 182, 212, 0.15)', padding: '10px', borderRadius: '10px', color: 'var(--secondary-accent)', marginTop: '2px' }}>
                <Bell size={20} />
              </div>
              <div style={{ flex: 1 }}>
                <p style={{ fontSize: '0.95rem', lineHeight: '1.5', color: 'var(--text-main)' }}>{n.message}</p>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-dim)', marginTop: '6px', display: 'block' }}>{n.timestamp}</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
