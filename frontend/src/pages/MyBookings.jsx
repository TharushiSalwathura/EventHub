import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Ticket, Calendar, CheckCircle2, Clock } from 'lucide-react';

export default function MyBookings({ user }) {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadBookings();
  }, []);

  const loadBookings = async () => {
    setLoading(true);
    try {
      const data = await api.getMyBookings(user ? user.userId : 1);
      setBookings(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '1000px', margin: '40px auto', padding: '0 20px', width: '100%' }}>
      <div style={{ marginBottom: '32px' }}>
        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, marginBottom: '8px' }}>My Event Bookings</h1>
        <p style={{ color: 'var(--text-muted)' }}>Managed by Member 3 Booking Service (`/bookings/user/{'{userId}'}`)</p>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>Loading bookings...</div>
      ) : bookings.length === 0 ? (
        <div className="glass-panel" style={{ padding: '40px', textAlign: 'center', color: 'var(--text-muted)' }}>No bookings found yet.</div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          {bookings.map((b) => (
            <div key={b.id} className="glass-panel" style={{ padding: '24px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '16px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                <div style={{ background: 'rgba(99, 102, 241, 0.15)', padding: '14px', borderRadius: '12px', color: 'var(--accent-primary)' }}>
                  <Ticket size={28} />
                </div>
                <div>
                  <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '4px' }}>{b.eventTitle}</h3>
                  <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Booking #{b.id} • {b.tickets} Tickets • Total: Rs. {b.totalAmount}</p>
                </div>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                <span className={`badge ${b.status === 'CONFIRMED' ? 'badge-success' : 'badge-warning'}`}>
                  {b.status === 'CONFIRMED' ? <CheckCircle2 size={14} /> : <Clock size={14} />} {b.status}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
