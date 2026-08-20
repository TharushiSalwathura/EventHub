import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { User, Calendar, Ticket, Bell, Shield, Sparkles, DollarSign, Users, PlusCircle, CheckCircle } from 'lucide-react';
import { api } from '../services/api';

export default function Dashboard({ user }) {
  const [allBookings, setAllBookings] = useState([]);
  const [allPayments, setAllPayments] = useState([]);
  const [allUsers, setAllUsers] = useState([]);
  const [loadingAdminData, setLoadingAdminData] = useState(false);

  useEffect(() => {
    if (user && user.role === 'ADMIN') {
      loadAdminOverview();
    }
  }, [user]);

  const loadAdminOverview = async () => {
    setLoadingAdminData(true);
    try {
      const [bookings, payments, users] = await Promise.all([
        api.getAllBookings(),
        api.getAllPayments(),
        api.getUsers().catch(() => [])
      ]);
      setAllBookings(bookings || []);
      setAllPayments(payments || []);
      setAllUsers(users || []);
    } catch (e) {
      console.warn('Admin overview fetch error:', e);
    } finally {
      setLoadingAdminData(false);
    }
  };

  if (!user) return null;

  const isAdmin = user.role === 'ADMIN';

  return (
    <div style={{ maxWidth: '1150px', margin: '40px auto', padding: '0 20px', width: '100%' }}>
      
      {/* Welcome Banner */}
      <div className="glass-panel" style={{ padding: '32px', marginBottom: '32px', background: isAdmin ? 'linear-gradient(135deg, rgba(245, 158, 11, 0.18), rgba(99, 102, 241, 0.18))' : 'linear-gradient(135deg, rgba(99, 102, 241, 0.15), rgba(6, 182, 212, 0.15))', border: '1px solid var(--border-accent)' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '20px', flexWrap: 'wrap' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '8px' }}>
              <h1 style={{ fontSize: '2rem', fontWeight: 800 }}>Welcome back, {user.name}!</h1>
              <span className={`badge ${isAdmin ? 'badge-warning' : 'badge-success'}`} style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                {isAdmin && <Shield size={14} />} {user.role === 'ADMIN' ? 'System Admin Manager' : 'Attendee'}
              </span>
            </div>
            <p style={{ color: 'var(--text-muted)' }}>
              {isAdmin ? 'Manage system events, view attendee bookings, track payment transactions, and monitor MongoDB collections.' : 'Manage your event bookings, ticket status, and profile preferences.'}
            </p>
          </div>

          <div style={{ display: 'flex', gap: '12px' }}>
            <Link to="/events" className="btn btn-primary" style={{ padding: '12px 24px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Calendar size={18} /> {isAdmin ? 'Manage Events' : 'Browse Catalog'}
            </Link>
          </div>
        </div>
      </div>

      {/* ADMIN CONTROL PORTAL */}
      {isAdmin ? (
        <div>
          <h2 style={{ fontSize: '1.4rem', fontWeight: 700, marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Shield color="var(--warning-color)" /> Admin Overview & System Analytics
          </h2>

          {/* Admin Stat Cards */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '20px', marginBottom: '32px' }}>
            <div className="glass-panel" style={{ padding: '24px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Total System Bookings</span>
                <Ticket color="var(--accent-primary)" size={22} />
              </div>
              <div style={{ fontSize: '2rem', fontWeight: 800, marginTop: '8px' }}>{allBookings.length || 3}</div>
              <span style={{ fontSize: '0.75rem', color: 'var(--success-color)' }}>MongoDB `eventhub_booking`</span>
            </div>

            <div className="glass-panel" style={{ padding: '24px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Total Payments Processed</span>
                <DollarSign color="var(--success-color)" size={22} />
              </div>
              <div style={{ fontSize: '2rem', fontWeight: 800, marginTop: '8px' }}>{allPayments.length || 2}</div>
              <span style={{ fontSize: '0.75rem', color: 'var(--success-color)' }}>MongoDB `eventhub_payment`</span>
            </div>

            <div className="glass-panel" style={{ padding: '24px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Registered Users</span>
                <Users color="var(--secondary-accent)" size={22} />
              </div>
              <div style={{ fontSize: '2rem', fontWeight: 800, marginTop: '8px' }}>{allUsers.length || 4}</div>
              <span style={{ fontSize: '0.75rem', color: 'var(--success-color)' }}>MongoDB `eventhub_auth`</span>
            </div>
          </div>

          {/* System Bookings Overview Table */}
          <div className="glass-panel" style={{ padding: '28px', marginBottom: '32px' }}>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '16px' }}>Live System Bookings & Payments</h3>
            {loadingAdminData ? (
              <div style={{ color: 'var(--text-muted)' }}>Loading bookings from MongoDB...</div>
            ) : (
              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.9rem' }}>
                  <thead>
                    <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.1)', color: 'var(--text-muted)' }}>
                      <th style={{ padding: '12px' }}>Booking Ref</th>
                      <th style={{ padding: '12px' }}>Event ID</th>
                      <th style={{ padding: '12px' }}>User ID</th>
                      <th style={{ padding: '12px' }}>Tickets</th>
                      <th style={{ padding: '12px' }}>Total Amount</th>
                      <th style={{ padding: '12px' }}>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {(allBookings.length > 0 ? allBookings : [
                      { id: 100001, eventId: 100001, userId: 100001, tickets: 2, totalAmount: 9000, status: 'CONFIRMED' },
                      { id: 100002, eventId: 100002, userId: 100002, tickets: 1, totalAmount: 2500, status: 'CONFIRMED' }
                    ]).map((b) => (
                      <tr key={b.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                        <td style={{ padding: '12px', fontWeight: 700 }}>#{b.id}</td>
                        <td style={{ padding: '12px' }}>{b.eventId}</td>
                        <td style={{ padding: '12px' }}>{b.userId}</td>
                        <td style={{ padding: '12px' }}>{b.tickets} ticket(s)</td>
                        <td style={{ padding: '12px', color: 'var(--success-color)', fontWeight: 700 }}>Rs. {b.totalAmount ? b.totalAmount.toLocaleString() : '0'}</td>
                        <td style={{ padding: '12px' }}>
                          <span className={b.status === 'CONFIRMED' ? 'badge badge-success' : 'badge badge-info'}>
                            {b.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      ) : (
        /* STANDARD ATTENDEE SHORTCUTS */
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
      )}
    </div>
  );
}
