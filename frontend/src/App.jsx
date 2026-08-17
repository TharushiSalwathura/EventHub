import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Register from './pages/Register';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Events from './pages/Events';
import MyBookings from './pages/MyBookings';
import Notifications from './pages/Notifications';

export default function App() {
  const [user, setUser] = useState(null);
  const [notifications, setNotifications] = useState([
    {
      title: '🎉 Welcome to EventHub!',
      message: 'Explore upcoming tech summits, music festivals, and book your tickets online.',
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    }
  ]);

  useEffect(() => {
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      try {
        setUser(JSON.parse(savedUser));
      } catch (e) {
        localStorage.removeItem('user');
      }
    }
  }, []);

  const handleLoginSuccess = (response) => {
    setUser({
      userId: response.userId,
      name: response.name,
      email: response.email,
      role: response.role
    });
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  const handleAddNotification = (newNotification) => {
    setNotifications((prev) => [newNotification, ...prev]);
  };

  return (
    <Router>
      <Navbar user={user} onLogout={handleLogout} notifications={notifications} />
      <main style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
        <Routes>
          <Route path="/" element={<Events user={user} onAddNotification={handleAddNotification} />} />
          <Route path="/events" element={<Events user={user} onAddNotification={handleAddNotification} />} />
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login onLoginSuccess={handleLoginSuccess} />} />
          <Route path="/dashboard" element={user ? <Dashboard user={user} /> : <Navigate to="/login" />} />
          <Route path="/my-bookings" element={user ? <MyBookings user={user} /> : <Navigate to="/login" />} />
          <Route path="/notifications" element={<Notifications notifications={notifications} />} />
        </Routes>
      </main>
    </Router>
  );
}
