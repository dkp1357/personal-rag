import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogOut, User, MessageSquare, FileText, Settings, Sun, Moon } from 'lucide-react';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div className="navbar bg-base-100 shadow-lg px-4 md:px-8">
      <div className="flex-1">
        <Link to="/" className="btn btn-ghost normal-case text-xl font-bold gap-2">
          <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center text-primary-content">
            <MessageSquare size={20} />
          </div>
          <span>Personal <span className="text-primary">RAG</span></span>
        </Link>
      </div>
      <div className="flex-none gap-2">
        {user ? (
          <>
            <Link to="/documents" className="btn btn-ghost btn-sm gap-2">
              <FileText size={18} />
              <span className="hidden md:inline">Documents</span>
            </Link>
            <div className="dropdown dropdown-end">
              <label tabIndex={0} className="btn btn-ghost btn-circle avatar">
                <div className="w-10 rounded-full bg-neutral text-neutral-content flex items-center justify-center">
                  <User size={20} />
                </div>
              </label>
              <ul tabIndex={0} className="mt-3 z-[1] p-2 shadow menu menu-sm dropdown-content bg-base-100 rounded-box w-52">
                <li><a onClick={handleLogout} className="text-error"><LogOut size={16} /> Logout</a></li>
              </ul>
            </div>
          </>
        ) : (
          <Link to="/login" className="btn btn-primary btn-sm px-6">Login</Link>
        )}
      </div>
    </div>
  );
};

export default Navbar;
