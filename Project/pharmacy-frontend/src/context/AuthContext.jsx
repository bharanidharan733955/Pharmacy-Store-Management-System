import React, { createContext, useContext, useState, useEffect } from 'react';
import { api, setOnUnauthorizedCallback } from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('pharmacy_token'));
  const [loading, setLoading] = useState(true);

  const logout = () => {
    localStorage.removeItem('pharmacy_token');
    localStorage.removeItem('pharmacy_user');
    setToken(null);
    setUser(null);
  };

  useEffect(() => {
    setOnUnauthorizedCallback(logout);

    const savedUser = localStorage.getItem('pharmacy_user');
    const savedToken = localStorage.getItem('pharmacy_token');

    if (savedToken && savedUser) {
      try {
        setUser(JSON.parse(savedUser));
        setToken(savedToken);
      } catch (e) {
        logout();
      }
    }
    setLoading(false);
  }, []);

  const login = async (username, password) => {
    const data = await api.login(username, password);
    const userInfo = {
      username: data.username,
      role: data.role,
      fullName: data.fullName,
      permissions: data.permissions || [],
    };

    localStorage.setItem('pharmacy_token', data.token);
    localStorage.setItem('pharmacy_user', JSON.stringify(userInfo));
    setToken(data.token);
    setUser(userInfo);
    return userInfo;
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!user && !!token,
        role: user?.role || null,
        login,
        logout,
        loading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
