import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Calendar, MapPin, Ticket, CheckCircle2, DollarSign, X } from 'lucide-react';

export default function Events({ user }) {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [ticketCount, setTicketCount] = useState(1);
  const [bookingSuccess, setBookingSuccess] = useState(null);
  const [paymentSuccess, setPaymentSuccess] = useState(null);

  useEffect(() => {
    loadEvents();
  }, []);

  const loadEvents = async () => {
    setLoading(true);
    try {
      const data = await api.getEvents();
      setEvents(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleBookClick = (event) => {
    setSelectedEvent(event);
    setTicketCount(1);
    setBookingSuccess(null);
    setPaymentSuccess(null);
  };

  const handleConfirmBooking = async () => {
    try {
      const booking = await api.createBooking({
        eventId: selectedEvent.id,
        userId: user ? user.userId : 1,
        tickets: ticketCount,
        price: selectedEvent.price
      });
      setBookingSuccess(booking);
    } catch (err) {
      alert('Booking failed: ' + err.message);
    }
  };

  const handlePayNow = async () => {
    try {
      const payment = await api.processPayment({
        bookingId: bookingSuccess.id,
        amount: bookingSuccess.totalAmount
      });
      setPaymentSuccess(payment);
    } catch (err) {
      alert('Payment failed: ' + err.message);
    }
  };

  return (
    <div style={{ maxWidth: '1200px', margin: '40px auto', padding: '0 20px', width: '100%' }}>
      <div style={{ marginBottom: '32px' }}>
        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, marginBottom: '8px' }}>Upcoming Events Catalog</h1>
        <p style={{ color: 'var(--text-muted)' }}>Browse available events routed through API Gateway (`/events/**`)</p>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px', color: 'var(--text-muted)' }}>Loading events...</div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(340px, 1fr))', gap: '28px' }}>
          {events.map((evt) => (
            <div key={evt.id} className="glass-panel glass-panel-hover" style={{ padding: '28px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '14px' }}>
                  <span className="badge badge-info">{evt.location}</span>
                  <span style={{ fontSize: '1.2rem', fontWeight: 800, color: 'var(--success-color)' }}>Rs. {evt.price.toLocaleString()}</span>
                </div>
                <h3 style={{ fontSize: '1.3rem', fontWeight: 700, marginBottom: '10px' }}>{evt.title}</h3>
                
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '20px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}><MapPin size={16} color="var(--secondary-accent)" /> {evt.location}</div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}><Calendar size={16} color="var(--accent-primary)" /> {evt.date}</div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}><Ticket size={16} color="var(--warning-color)" /> {evt.availableSeats} of {evt.capacity} seats available</div>
                </div>
              </div>

              <button onClick={() => handleBookClick(evt)} className="btn btn-primary" style={{ width: '100%' }}>
                <Ticket size={18} /> Book Tickets
              </button>
            </div>
          ))}
        </div>
      )}

      {/* Booking Modal */}
      {selectedEvent && (
        <div className="modal-overlay" onClick={() => setSelectedEvent(null)}>
          <div className="modal-content glass-panel" onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h3 style={{ fontSize: '1.3rem', fontWeight: 700 }}>Book Event</h3>
              <button onClick={() => setSelectedEvent(null)} className="btn btn-secondary" style={{ padding: '6px' }}><X size={20} /></button>
            </div>

            {!bookingSuccess ? (
              <>
                <div style={{ background: 'rgba(255,255,255,0.03)', padding: '16px', borderRadius: 'var(--radius-sm)', marginBottom: '20px' }}>
                  <h4 style={{ fontWeight: 700, marginBottom: '4px' }}>{selectedEvent.title}</h4>
                  <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>{selectedEvent.location} • {selectedEvent.date}</p>
                  <p style={{ fontSize: '1.1rem', fontWeight: 700, color: 'var(--success-color)', marginTop: '8px' }}>Rs. {selectedEvent.price} / ticket</p>
                </div>

                <div className="form-group">
                  <label>Number of Tickets</label>
                  <input
                    type="number"
                    className="form-input"
                    min="1"
                    max={selectedEvent.availableSeats}
                    value={ticketCount}
                    onChange={(e) => setTicketCount(parseInt(e.target.value) || 1)}
                  />
                </div>

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', margin: '20px 0', fontSize: '1.1rem', fontWeight: 700 }}>
                  <span>Total Amount:</span>
                  <span style={{ color: 'var(--success-color)' }}>Rs. {(selectedEvent.price * ticketCount).toLocaleString()}</span>
                </div>

                <button onClick={handleConfirmBooking} className="btn btn-primary" style={{ width: '100%', padding: '12px' }}>
                  Confirm Booking (PENDING)
                </button>
              </>
            ) : !paymentSuccess ? (
              <div style={{ textAlign: 'center' }}>
                <div style={{ background: 'rgba(245, 158, 11, 0.15)', color: 'var(--warning-color)', padding: '16px', borderRadius: 'var(--radius-sm)', marginBottom: '20px' }}>
                  <CheckCircle2 size={32} style={{ marginBottom: '8px' }} />
                  <h4>Booking Created! Status: PENDING</h4>
                  <p style={{ fontSize: '0.85rem', marginTop: '4px' }}>Booking ID: #{bookingSuccess.id} • Total: Rs. {bookingSuccess.totalAmount}</p>
                </div>
                <button onClick={handlePayNow} className="btn btn-primary" style={{ width: '100%', padding: '12px' }}>
                  <DollarSign size={20} /> Process Mock Payment (Rs. {bookingSuccess.totalAmount})
                </button>
              </div>
            ) : (
              <div style={{ textAlign: 'center' }}>
                <div style={{ background: 'rgba(16, 185, 129, 0.15)', color: 'var(--success-color)', padding: '20px', borderRadius: 'var(--radius-sm)', marginBottom: '20px' }}>
                  <CheckCircle2 size={40} style={{ marginBottom: '8px' }} />
                  <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>Payment SUCCESSFUL!</h3>
                  <p style={{ fontSize: '0.9rem', marginTop: '6px' }}>Transaction ID: <b>{paymentSuccess.transactionId}</b></p>
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '4px' }}>Booking Status updated to <b>CONFIRMED</b>. Notification generated!</p>
                </div>
                <button onClick={() => setSelectedEvent(null)} className="btn btn-secondary" style={{ width: '100%' }}>Done</button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
