import { useMemo, useState } from 'react'
import { BrowserRouter, Navigate, Route, Routes, Link } from 'react-router-dom'
import './App.css'
import Login from './pages/Login'
import Register from './pages/Register'
import Exercises from './pages/Exercises'
import AdminPanel from './pages/AdminPanel'
import ExerciseDetails from './pages/ExerciseDetails'
import Plans from './pages/Plans'
import PlanDetails from './pages/PlanDetails'

function parseJwt(token) {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch {
    return null
  }
}

function AuthScreen({ onLoginSuccess }) {
  const [authMode, setAuthMode] = useState('login')

  return (
    <main className="app-shell">
      <div className="auth-switch">
        <button
          className={authMode === 'login' ? 'active' : ''}
          onClick={() => setAuthMode('login')}
        >
          Logowanie
        </button>
        <button
          className={authMode === 'register' ? 'active' : ''}
          onClick={() => setAuthMode('register')}
        >
          Rejestracja
        </button>
      </div>

      {authMode === 'login' ? (
        <Login onLoginSuccess={onLoginSuccess} />
      ) : (
        <Register onSwitchToLogin={() => setAuthMode('login')} />
      )}
    </main>
  )
}

function Layout({ user, isAdmin, onLogout, token }) {
  return (
    <main className="app-shell">
      <header className="topbar">
        <div>
          <h1>Home Workout</h1>
          <p>
            Zalogowany jako: <strong>{user.username}</strong>
          </p>
          <p>Role: {user.roles.join(', ')}</p>
        </div>

        <div className="topbar-actions">
          <Link to="/plans" className="nav-button">
            Plany
          </Link>
          <Link to="/exercises" className="nav-button">
            Ćwiczenia
          </Link>

          {isAdmin && (
            <Link to="/admin" className="nav-button">
              Panel administratora
            </Link>
          )}

          <button onClick={onLogout}>Wyloguj</button>
        </div>
      </header>

      <Routes>
  <Route path="/exercises" element={<Exercises isAdmin={isAdmin} />} />
  <Route path="/exercises/:id" element={<ExerciseDetails />} />
  <Route path="/plans" element={<Plans />} />
  <Route path="/plans/:id" element={<PlanDetails />} />
  <Route
    path="/admin"
    element={
      isAdmin ? (
        <AdminPanel token={token} currentUsername={user.username} />
      ) : (
        <Navigate to="/exercises" replace />
      )
    }
  />
  <Route path="*" element={<Navigate to="/exercises" replace />} />
</Routes>
    </main>
  )
}

function App() {
  const [token, setToken] = useState(localStorage.getItem('token') || '')

  const user = useMemo(() => {
    if (!token) return null
    const payload = parseJwt(token)
    if (!payload) return null

    return {
      username: payload.sub,
      roles: payload.roles || [],
    }
  }, [token])

  const isAdmin = user?.roles?.includes('ROLE_ADMIN')

  const handleLoginSuccess = (jwtToken) => {
    localStorage.setItem('token', jwtToken)
    setToken(jwtToken)
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    setToken('')
  }

  return (
    <BrowserRouter>
      {!token || !user ? (
        <Routes>
          <Route
            path="*"
            element={<AuthScreen onLoginSuccess={handleLoginSuccess} />}
          />
        </Routes>
      ) : (
        <Layout
          user={user}
          isAdmin={isAdmin}
          onLogout={handleLogout}
          token={token}
        />
      )}
    </BrowserRouter>
  )
}

export default App