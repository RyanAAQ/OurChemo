import { useState, type FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FlaskConical, Loader2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';

export default function RegisterPage() {
  const { register, isLoading } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({ fullName: '', username: '', password: '', confirm: '' });

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (form.password !== form.confirm) {
      toast.error('Passwords do not match');
      return;
    }
    try {
      await register({ fullName: form.fullName, username: form.username, password: form.password });
      toast.success('Account created — welcome!');
      navigate('/inventory');
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'Registration failed');
    }
  };

  const set = (field: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm({ ...form, [field]: e.target.value });

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-logo">
          <FlaskConical size={36} strokeWidth={1.5} />
          <span>ourChemo</span>
        </div>
        <h1 className="auth-title">Create account</h1>
        <p className="auth-subtitle">Register a new chemist</p>

        <form onSubmit={handleSubmit} className="auth-form">
          <label className="field-label">
            Full name
            <input className="field-input" type="text" value={form.fullName} onChange={set('fullName')} required />
          </label>

          <label className="field-label">
            Username
            <input
              className="field-input"
              type="text"
              autoComplete="username"
              value={form.username}
              onChange={set('username')}
              required
            />
          </label>

          <label className="field-label">
            Password
            <input
              className="field-input"
              type="password"
              autoComplete="new-password"
              value={form.password}
              onChange={set('password')}
              required
            />
          </label>

          <label className="field-label">
            Confirm password
            <input
              className="field-input"
              type="password"
              autoComplete="new-password"
              value={form.confirm}
              onChange={set('confirm')}
              required
            />
          </label>

          <button className="btn btn-primary btn-full" type="submit" disabled={isLoading}>
            {isLoading ? <Loader2 size={18} className="spin" /> : 'Create account'}
          </button>
        </form>

        <p className="auth-footer">
          Already have an account?{' '}
          <Link to="/login" className="link">
            Sign in
          </Link>
        </p>
      </div>
    </div>
  );
}
