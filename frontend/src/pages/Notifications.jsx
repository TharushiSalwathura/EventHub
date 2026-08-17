import React from 'react';
import { Bell, CheckCircle2, Ticket, ShieldCheck, Sparkles } from 'lucide-react';

export default function Notifications({ notifications = [] }) {
  return (
    <div style={{ maxWidth: '900px', margin: '40px auto', padding: '0 20px', width: '100%' }}>
      <div style={{ marginBottom: '32px' }}>
        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, marginBottom: '8px' }}>My Notifications & Payment Receipts</h1>
        <p style={{ color: 'var(--text-muted)' }}>View instant booking updates, payment confirmations, and ticket receipts.</p>
      </div>

      {notifications.length === 0 ? (
        <div className="glass-panel" style={{ textAlign: 'center', padding: '60px 20px', color: 'var(--text-muted)' }}>
          <Bell size={48} style={{ opacity: 0.4, marginBottom: '16px' }} />
          <h3 style={{ fontSize: '1.2rem', fontWeight: 700, color: 'white', marginBottom: '6px' }}>No Notifications Yet</h3>
          <p style={{ fontSize: '0.9rem' }}>When you book event tickets and complete payment, your receipts and confirmation alerts will appear here!</p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {notifications.map((n, idx) => (
            <div key={idx} className="glass-panel" style={{ padding: '24px', display: 'flex', alignItems: 'flex-start', gap: '20px', background: 'linear-gradient(135deg, rgba(30, 41, 59, 0.7), rgba(15, 23, 42, 0.8))', borderLeft: '4px solid var(--success-color)', borderRadius: '16px' }}>
              <div style={{ background: 'rgba(16, 185, 129, 0.15)', padding: '12px', borderRadius: '12px', color: 'var(--success-color)', marginTop: '2px' }}>
                <ShieldCheck size={26} />
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
                  <h4 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'white' }}>{n.title || 'Payment Successful'}</h4>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-dim)' }}>{n.timestamp || 'Just now'}</span>
                </div>
                <p style={{ fontSize: '0.95rem', lineHeight: '1.5', color: 'var(--text-main)', marginBottom: '12px' }}>{n.message}</p>

                {n.txnId && (
                  <div style={{ display: 'inline-flex', alignItems: 'center', gap: '8px', background: 'rgba(255,255,255,0.04)', padding: '6px 12px', borderRadius: '8px', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                    <span>Txn Ref: <b style={{ color: 'var(--success-color)', fontFamily: 'monospace' }}>{n.txnId}</b></span>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
