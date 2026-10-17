import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { FlaskConical, LayoutGrid, Pill, ClipboardList, LogOut, User } from 'lucide-react';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';

const navItems = [
  { to: '/inventory', icon: <Pill size={18} />, label: 'Inventory' },
  { to: '/dispense', icon: <LayoutGrid size={18} />, label: 'Dispense' },
  { to: '/history', icon: <ClipboardList size={18} />, label: 'History' },
];

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await logout();
      toast.success('Logged out');
      navigate('/login');
    } catch {
      // still navigate even if server call fails
      navigate('/login');
    }
  };

  return (
    <div className="app-shell">
      {/* ── Sidebar ── */}
      <aside className="sidebar">
        <div className="sidebar-header">
          <FlaskConical size={24} strokeWidth={1.5} />
          <span className="sidebar-brand">ourChemo</span>
        </div>

        <nav className="sidebar-nav">
          {navItems.map(({ to, icon, label }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) => `nav-item${isActive ? ' nav-item--active' : ''}`}
            >
              {icon}
              <span>{label}</span>
            </NavLink>
          ))}
        </nav>

        <div className="sidebar-footer">
          <div className="sidebar-user">
            <User size={16} />
            <span>{user?.username}</span>
          </div>
          <button className="btn-icon logout-btn" onClick={handleLogout} title="Log out">
            <LogOut size={16} />
          </button>
        </div>
      </aside>

      {/* ── Main content ── */}
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
