import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Calendar, MapPin, Ticket, CheckCircle2, DollarSign, X, CreditCard, Lock, ShieldCheck, Sparkles, PlusCircle, Shield } from 'lucide-react';

export default function Events({ user, onAddNotification }) {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [ticketCount, setTicketCount] = useState(1);
  const [bookingSuccess, setBookingSuccess] = useState(null);
  const [paymentSuccess, setPaymentSuccess] = useState(null);

  // Admin Event Creation Modal State
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newEvent, setNewEvent] = useState({
    title: '',
    description: '',
    location: '',
    eventDate: '2026-11-15T09:00:00',
    capacity: 200,
    price: 3500
  });

  // Payment Form Fields
  const [paymentMethod, setPaymentMethod] = useState('CREDIT_CARD');
  const [cardName, setCardName] = useState('');
  const [cardNumber, setCardNumber] = useState('');
  const [expiryDate, setExpiryDate] = useState('');
  const [cvv, setCvv] = useState('');
  const [processing, setProcessing] = useState(false);
  const [toastAlert, setToastAlert] = useState(null);

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

  const handleCreateEventSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.createEvent({
        ...newEvent,
        capacity: parseInt(newEvent.capacity),
        price: parseFloat(newEvent.price)
      });

      alert(`Event "${newEvent.title}" created successfully in MongoDB!`);
      setShowCreateModal(false);
      setNewEvent({ title: '', description: '', location: '', eventDate: '2026-11-15T09:00:00', capacity: 200, price: 3500 });
      loadEvents();
    } catch (err) {
      alert('Failed to create event: ' + err.message);
    }
  };

  const handleBookClick = (event) => {
    setSelectedEvent(event);
    setTicketCount(1);
    setBookingSuccess(null);
    setPaymentSuccess(null);
    setCardName('');
    setCardNumber('');
    setExpiryDate('');
    setCvv('');
    setPaymentMethod('CREDIT_CARD');
  };

  const handleConfirmBooking = async () => {
    try {
      const booking = await api.createBooking({
        eventId: selectedEvent.id,
        userId: user ? user.userId : 100001,
        tickets: ticketCount,
        price: selectedEvent.price
      });
      setBookingSuccess(booking);
    } catch (err) {
      alert('Booking failed: ' + err.message);
    }
  };

  const handlePayNow = async (e) => {
    e.preventDefault();
    if (paymentMethod === 'CREDIT_CARD' || paymentMethod === 'DEBIT_CARD') {
      if (!cardName || !cardNumber || !expiryDate || !cvv) {
        alert('Please fill out all card payment fields');
        return;
      }
    }

    setProcessing(true);
    try {
      const payment = await api.processPayment({
        bookingId: bookingSuccess.id,
        userId: user ? user.userId : 100001,
        amount: bookingSuccess.totalAmount || (selectedEvent.price * ticketCount),
        paymentMethod: paymentMethod
      });

      setPaymentSuccess(payment);

      const notificationMsg = {
        title: '🎉 Payment Successful!',
        message: `${ticketCount} ticket(s) confirmed for "${selectedEvent.title}". Total Paid: Rs. ${(bookingSuccess.totalAmount || (selectedEvent.price * ticketCount)).toLocaleString()}`,
        eventTitle: selectedEvent.title,
        tickets: ticketCount,
        amount: bookingSuccess.totalAmount || (selectedEvent.price * ticketCount),
        txnId: payment.transactionReference || payment.transactionId || 'TXN-84920482',
        timestamp: new Date().toLocaleString()
      };

      if (onAddNotification) {
        onAddNotification(notificationMsg);
      }

      setToastAlert(notificationMsg);
      setTimeout(() => setToastAlert(null), 8000);

      loadEvents();
    } catch (err) {
      alert('Payment failed: ' + err.message);
    } finally {
      setProcessing(false);
    }
  };

  return (
    <div style={{ maxWidth: '1200px', margin: '40px auto', padding: '0 20px', width: '100%' }}>
      
      {/* Toast Alert Banner */}
      {toastAlert && (
        <div style={{ position: 'fixed', top: '80px', right: '20px', background: 'linear-gradient(135deg, #10b981, #059669)', color: 'white', padding: '16px 24px', borderRadius: '14px', boxShadow: '0 10px 30px rgba(16, 185, 129, 0.4)', zIndex: 1000, display: 'flex', alignItems: 'center', gap: '14px', maxWidth: '420px' }}>
          <Sparkles size={24} />
          <div>
            <div style={{ fontWeight: 800, fontSize: '0.95rem' }}>{toastAlert.title}</div>
            <div style={{ fontSize: '0.85rem', opacity: 0.9 }}>{toastAlert.message}</div>
          </div>
          <button onClick={() => setToastAlert(null)} style={{ background: 'transparent', border: 'none', color: 'white', cursor: 'pointer', marginLeft: 'auto' }}><X size={18} /></button>
        </div>
      )}

      {/* Page Header & Admin Create Trigger */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '32px', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h1 style={{ fontSize: '2.2rem', fontWeight: 800, marginBottom: '8px' }}>Explore Upcoming Events</h1>
          <p style={{ color: 'var(--text-muted)' }}>Browse music festivals, tech summits, workshops, and reserve your tickets instantly.</p>
        </div>

        {/* ADMIN Create Event Button */}
        {user && user.role === 'ADMIN' && (
          <button onClick={() => setShowCreateModal(true)} className="btn btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '8px', background: 'linear-gradient(135deg, #f59e0b, #d97706)', padding: '12px 20px' }}>
            <PlusCircle size={20} /> Add New Event (Admin)
          </button>
        )}
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px', color: 'var(--text-muted)' }}>Loading event catalog...</div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(340px, 1fr))', gap: '28px' }}>
          {events.map((evt) => (
            <div key={evt.id} className="glass-panel glass-panel-hover" style={{ padding: '28px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '14px' }}>
                  <span className="badge badge-info">{evt.location ? evt.location.split(',')[0] : 'Venue'}</span>
                  <span style={{ fontSize: '1.2rem', fontWeight: 800, color: 'var(--success-color)' }}>Rs. {evt.price ? evt.price.toLocaleString() : '0'}</span>
                </div>
                <h3 style={{ fontSize: '1.3rem', fontWeight: 700, marginBottom: '10px' }}>{evt.title}</h3>
                
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '20px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}><MapPin size={16} color="var(--secondary-accent)" /> {evt.location}</div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}><Calendar size={16} color="var(--accent-primary)" /> {evt.eventDate ? evt.eventDate.replace('T', ' ') : '2026-09-30'}</div>
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

      {/* ADMIN Create Event Modal */}
      {showCreateModal && (
        <div className="modal-overlay" onClick={() => setShowCreateModal(false)}>
          <div className="modal-content glass-panel" style={{ maxWidth: '520px' }} onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Shield size={22} color="var(--warning-color)" />
                <h3 style={{ fontSize: '1.3rem', fontWeight: 700 }}>Admin: Create New Event</h3>
              </div>
              <button onClick={() => setShowCreateModal(false)} className="btn btn-secondary" style={{ padding: '6px' }}><X size={20} /></button>
            </div>

            <form onSubmit={handleCreateEventSubmit}>
              <div className="form-group" style={{ marginBottom: '12px' }}>
                <label style={{ fontSize: '0.85rem' }}>Event Title</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="e.g. Sri Lanka Cloud & DevOps Summit"
                  required
                  value={newEvent.title}
                  onChange={(e) => setNewEvent({ ...newEvent, title: e.target.value })}
                />
              </div>

              <div className="form-group" style={{ marginBottom: '12px' }}>
                <label style={{ fontSize: '0.85rem' }}>Description</label>
                <textarea
                  className="form-input"
                  style={{ height: '70px' }}
                  placeholder="Detailed agenda or event overview"
                  value={newEvent.description}
                  onChange={(e) => setNewEvent({ ...newEvent, description: e.target.value })}
                />
              </div>

              <div className="form-group" style={{ marginBottom: '12px' }}>
                <label style={{ fontSize: '0.85rem' }}>Location / Venue</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="e.g. BMICH, Colombo"
                  required
                  value={newEvent.location}
                  onChange={(e) => setNewEvent({ ...newEvent, location: e.target.value })}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '18px' }}>
                <div className="form-group">
                  <label style={{ fontSize: '0.85rem' }}>Capacity (Seats)</label>
                  <input
                    type="number"
                    className="form-input"
                    min="1"
                    required
                    value={newEvent.capacity}
                    onChange={(e) => setNewEvent({ ...newEvent, capacity: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label style={{ fontSize: '0.85rem' }}>Price (LKR)</label>
                  <input
                    type="number"
                    className="form-input"
                    min="0"
                    required
                    value={newEvent.price}
                    onChange={(e) => setNewEvent({ ...newEvent, price: e.target.value })}
                  />
                </div>
              </div>

              <button type="submit" className="btn btn-primary" style={{ width: '100%', padding: '12px', background: 'linear-gradient(135deg, #f59e0b, #d97706)' }}>
                Publish Event to MongoDB
              </button>
            </form>
          </div>
        </div>
      )}

      {/* Booking & Payment Modal */}
      {selectedEvent && (
        <div className="modal-overlay" onClick={() => setSelectedEvent(null)}>
          <div className="modal-content glass-panel" style={{ maxWidth: '520px' }} onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h3 style={{ fontSize: '1.3rem', fontWeight: 700 }}>
                {!bookingSuccess ? 'Book Event Tickets' : !paymentSuccess ? 'Payment Gateway' : 'Payment Receipt'}
              </h3>
              <button onClick={() => setSelectedEvent(null)} className="btn btn-secondary" style={{ padding: '6px' }}><X size={20} /></button>
            </div>

            {!bookingSuccess ? (
              <>
                <div style={{ background: 'rgba(255,255,255,0.03)', padding: '16px', borderRadius: 'var(--radius-sm)', marginBottom: '20px' }}>
                  <h4 style={{ fontWeight: 700, marginBottom: '4px' }}>{selectedEvent.title}</h4>
                  <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>{selectedEvent.location}</p>
                  <p style={{ fontSize: '1.1rem', fontWeight: 700, color: 'var(--success-color)', marginTop: '8px' }}>Rs. {selectedEvent.price ? selectedEvent.price.toLocaleString() : '0'} / ticket</p>
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
                  Proceed to Payment (Rs. {(selectedEvent.price * ticketCount).toLocaleString()})
                </button>
              </>
            ) : !paymentSuccess ? (
              <form onSubmit={handlePayNow}>
                <div style={{ background: 'rgba(59, 130, 246, 0.12)', border: '1px solid rgba(59, 130, 246, 0.3)', padding: '14px', borderRadius: 'var(--radius-sm)', marginBottom: '18px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Booking Ref: <b>#{bookingSuccess.id}</b></span>
                    <span style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--success-color)' }}>Rs. {(bookingSuccess.totalAmount || (selectedEvent.price * ticketCount)).toLocaleString()}</span>
                  </div>
                </div>

                <div className="form-group">
                  <label style={{ fontSize: '0.85rem', marginBottom: '6px', display: 'block' }}>Select Payment Method</label>
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px', marginBottom: '14px' }}>
                    <button
                      type="button"
                      onClick={() => setPaymentMethod('CREDIT_CARD')}
                      style={{
                        padding: '10px',
                        borderRadius: '8px',
                        border: paymentMethod === 'CREDIT_CARD' ? '2px solid var(--accent-primary)' : '1px solid rgba(255,255,255,0.1)',
                        background: paymentMethod === 'CREDIT_CARD' ? 'rgba(99, 102, 241, 0.2)' : 'rgba(255,255,255,0.03)',
                        color: 'white',
                        fontWeight: 600,
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        gap: '6px'
                      }}
                    >
                      <CreditCard size={16} /> Credit Card
                    </button>

                    <button
                      type="button"
                      onClick={() => setPaymentMethod('DEBIT_CARD')}
                      style={{
                        padding: '10px',
                        borderRadius: '8px',
                        border: paymentMethod === 'DEBIT_CARD' ? '2px solid var(--accent-primary)' : '1px solid rgba(255,255,255,0.1)',
                        background: paymentMethod === 'DEBIT_CARD' ? 'rgba(99, 102, 241, 0.2)' : 'rgba(255,255,255,0.03)',
                        color: 'white',
                        fontWeight: 600,
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        gap: '6px'
                      }}
                    >
                      <CreditCard size={16} /> Debit Card
                    </button>
                  </div>
                </div>

                <div className="form-group" style={{ marginBottom: '12px' }}>
                  <label style={{ fontSize: '0.85rem' }}>Cardholder Name</label>
                  <input
                    type="text"
                    className="form-input"
                    placeholder="e.g. Tharushi Salwathura"
                    required
                    value={cardName}
                    onChange={(e) => setCardName(e.target.value)}
                  />
                </div>

                <div className="form-group" style={{ marginBottom: '12px' }}>
                  <label style={{ fontSize: '0.85rem' }}>Card Number</label>
                  <input
                    type="text"
                    className="form-input"
                    placeholder="4532 •••• •••• 8892"
                    maxLength="19"
                    required
                    value={cardNumber}
                    onChange={(e) => setCardNumber(e.target.value)}
                  />
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '18px' }}>
                  <div className="form-group">
                    <label style={{ fontSize: '0.85rem' }}>Expiry Date</label>
                    <input
                      type="text"
                      className="form-input"
                      placeholder="MM/YY (e.g. 09/28)"
                      maxLength="5"
                      required
                      value={expiryDate}
                      onChange={(e) => setExpiryDate(e.target.value)}
                    />
                  </div>
                  <div className="form-group">
                    <label style={{ fontSize: '0.85rem' }}>CVV / CVC</label>
                    <input
                      type="password"
                      className="form-input"
                      placeholder="384"
                      maxLength="4"
                      required
                      value={cvv}
                      onChange={(e) => setCvv(e.target.value)}
                    />
                  </div>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.75rem', color: 'var(--text-dim)', marginBottom: '16px' }}>
                  <Lock size={14} color="var(--success-color)" /> Encrypted 256-Bit SSL Payment Gateway
                </div>

                <button type="submit" disabled={processing} className="btn btn-primary" style={{ width: '100%', padding: '12px', background: 'linear-gradient(135deg, #10b981, #059669)' }}>
                  {processing ? 'Processing Payment...' : `Pay Rs. ${(bookingSuccess.totalAmount || (selectedEvent.price * ticketCount)).toLocaleString()} Now`}
                </button>
              </form>
            ) : (
              <div style={{ textAlign: 'center' }}>
                <div style={{ background: 'rgba(16, 185, 129, 0.15)', border: '1px solid rgba(16, 185, 129, 0.3)', color: 'var(--success-color)', padding: '24px', borderRadius: 'var(--radius-sm)', marginBottom: '20px' }}>
                  <ShieldCheck size={48} style={{ marginBottom: '10px' }} />
                  <h3 style={{ fontSize: '1.3rem', fontWeight: 700 }}>Payment Successful!</h3>
                  <p style={{ fontSize: '0.95rem', marginTop: '8px', color: 'white' }}>Transaction Ref: <b style={{ fontFamily: 'monospace', background: 'rgba(255,255,255,0.1)', padding: '2px 8px', borderRadius: '4px' }}>{paymentSuccess.transactionReference || paymentSuccess.transactionId || 'TXN-84920482'}</b></p>
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '8px' }}>Your booking status is <b>CONFIRMED</b>. View alerts in your Notifications bar!</p>
                </div>
                <button onClick={() => setSelectedEvent(null)} className="btn btn-secondary" style={{ width: '100%', padding: '10px' }}>Close</button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
