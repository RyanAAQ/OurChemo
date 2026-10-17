import { useState, type FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FlaskConical, Loader2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const { login, isLoading } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({ username: '', password: '' });

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    try {
      await login(form);
      toast.success('Welcome back!');
      navigate('/inventory');
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'Login failed');
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-logo">
          <FlaskConical size={36} strokeWidth={1.5} />
          <span>ourChemo</span>
        </div>
        <h1 className="auth-title">Sign in</h1>
        <p className="auth-subtitle">Pharmacy inventory management</p>

        <form onSubmit={handleSubmit} className="auth-form">
          <label className="field-label">
            Username
            <input
              className="field-input"
              type="text"
              autoComplete="username"
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              required
            />
          </label>

          <label className="field-label">
            Password
            <input
              className="field-input"
              type="password"
              autoComplete="current-password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
            />
          </label>

          <button className="btn btn-primary btn-full" type="submit" disabled={isLoading}>
            {isLoading ? <Loader2 size={18} className="spin" /> : 'Sign in'}
          </button>
        </form>

        <p className="auth-footer">
          No account?{' '}
          <Link to="/register" className="link">
            Register here
          </Link>
        </p>
      </div>
    </div>
  );
}
