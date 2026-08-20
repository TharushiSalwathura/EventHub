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

    return [
      { id: 100001, title: 'Global Tech Summit 2026', location: 'Colombo Exhibition Centre', eventDate: '2026-09-30T09:00:00', capacity: 200, availableSeats: 185, price: 4500 },
      { id: 100002, title: 'Sri Lanka Music & Arts Fest', location: 'Galle Face Green, Colombo', eventDate: '2026-10-15T18:00:00', capacity: 500, availableSeats: 420, price: 2500 },
      { id: 100003, title: 'AI & Cloud Microservices Expo', location: 'BMICH, Colombo', eventDate: '2026-11-01T10:00:00', capacity: 150, availableSeats: 110, price: 6000 }
    ];
  },

  createEvent: async (eventData) => {
    const response = await fetch(`${GATEWAY_URL}/events`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(eventData)
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to create event');
    }
    return response.json();
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
      { id: 101, eventTitle: 'Global Tech Summit 2026', tickets: 2, totalAmount: 9000, status: 'CONFIRMED', date: '2026-09-30' }
    ];
  },

  getAllBookings: async () => {
    try {
      const response = await fetch(`${GATEWAY_URL}/bookings`, {
        headers: getAuthHeaders()
      });
      if (response.ok) return await response.json();
    } catch (e) {
      console.warn('Backend Booking Service offline.');
    }
    return [];
  },

  // Member 4 & 5: Payment & Notification Service
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

  getAllPayments: async () => {
    try {
      const response = await fetch(`${GATEWAY_URL}/payments`, {
        headers: getAuthHeaders()
      });
      if (response.ok) return await response.json();
    } catch (e) {
      console.warn('Backend Payment Service offline.');
    }
    return [];
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
