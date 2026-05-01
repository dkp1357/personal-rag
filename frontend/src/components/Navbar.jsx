import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <NavLink to="/" className="navbar-brand">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
        </svg>
        PersonalRAG
      </NavLink>

      {user && (
        <div className="navbar-links">
          <NavLink to="/chat" className={({ isActive }) => `nav-link ${isActive ? "active" : ""}`}>
            Chat
          </NavLink>
          <NavLink to="/documents" className={({ isActive }) => `nav-link ${isActive ? "active" : ""}`}>
            Documents
          </NavLink>
          <button className="btn-logout" onClick={handleLogout}>
            Log out
          </button>
        </div>
      )}
    </nav>
  );
}
