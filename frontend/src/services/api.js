const GATEWAY_URL = 'http://localhost:8080';

// Helper to construct authorization headers
const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {})
  };
};

export const api = {
  // Member 1: User & Authentication
  register: async (userData) => {
    const response = await fetch(`${GATEWAY_URL}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData)
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Registration failed');
    }
    return response.json();
  },

  login: async (credentials) => {
    const response = await fetch(`${GATEWAY_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials)
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Invalid email or password');
    }
    return response.json();
  },

  getUsers: async () => {
    const response = await fetch(`${GATEWAY_URL}/users`, {
      headers: getAuthHeaders()
    });
    if (!response.ok) throw new Error('Failed to fetch users');
    return response.json();
  },

  // Member 2: Event Service
  getEvents: async () => {
    try {
      const response = await fetch(`${GATEWAY_URL}/events`, {
        headers: getAuthHeaders()
      });
      if (response.ok) return await response.json();
    } catch (e) {
      console.warn('Backend Event Service offline, using mock catalog for UI demonstration.');
    }

    // Demo Events Mock (when Member 2 service is starting)
    return [
      { id: 1, title: 'Tech Conference 2026', location: 'Colombo', date: '2026-09-20', capacity: 100, availableSeats: 65, price: 2500 },
      { id: 2, title: 'AI & Cloud Summit', location: 'Kandy', date: '2026-10-15', capacity: 150, availableSeats: 120, price: 3500 },
      { id: 3, title: 'Cybersecurity Workshop', location: 'Galle', date: '2026-11-05', capacity: 80, availableSeats: 25, price: 1800 }
    ];
  },

  // Member 3: Booking Service
  createBooking: async (bookingData) => {
    try {
      const response = await fetch(`${GATEWAY_URL}/bookings`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(bookingData)
      });
      if (response.ok) return await response.json();
    } catch (e) {
      console.warn('Backend Booking Service offline, using mock booking response.');
    }

    return {
      id: Math.floor(Math.random() * 1000) + 100,
      eventId: bookingData.eventId,
      userId: bookingData.userId || 1,
      tickets: bookingData.tickets,
      totalAmount: bookingData.tickets * (bookingData.price || 2500),
      status: 'PENDING',
      createdAt: new Date().toISOString()
    };
  },

  getMyBookings: async (userId) => {
    try {
      const response = await fetch(`${GATEWAY_URL}/bookings/user/${userId}`, {
        headers: getAuthHeaders()
      });
      if (response.ok) return await response.json();
    } catch (e) {
      console.warn('Backend Booking Service offline, using mock bookings.');
    }
    return [
      { id: 101, eventTitle: 'Tech Conference 2026', tickets: 2, totalAmount: 5000, status: 'CONFIRMED', date: '2026-09-20' }
    ];
  },

  // Member 4: Payment & Notification Service
  processPayment: async (paymentData) => {
    try {
      const response = await fetch(`${GATEWAY_URL}/payments/process`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(paymentData)
      });
      if (response.ok) return await response.json();
    } catch (e) {
      console.warn('Backend Payment Service offline, using mock payment response.');
    }

    return {
      transactionId: `TXN-2026-${Math.floor(Math.random() * 900) + 100}`,
      bookingId: paymentData.bookingId,
      amount: paymentData.amount,
      status: 'SUCCESS',
      timestamp: new Date().toISOString()
    };
  },

  getNotifications: async () => {
    try {
      const response = await fetch(`${GATEWAY_URL}/notifications`, {
        headers: getAuthHeaders()
      });
      if (response.ok) return await response.json();
    } catch (e) {
      console.warn('Backend Notification Service offline, using mock notifications.');
    }

    return [
      { id: 1, message: 'Your EventHub booking #101 has been confirmed. Payment was successful.', timestamp: 'Just now' },
      { id: 2, message: 'Welcome to EventHub! Explore upcoming events in Colombo and Kandy.', timestamp: '1 hour ago' }
    ];
  }
};
