import { useState } from 'react'
import API_BASE_URL from '../config/api'

function Register({ onSwitchToLogin }) {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(null)

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSuccess(null)
    setLoading(true)

    try {
      const response = await fetch(`${API_BASE_URL}/auth/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Błąd rejestracji')
      }

      setSuccess(data)
      setFormData({
        username: '',
        email: '',
        password: '',
      })
    } catch (err) {
      setError(err.message || 'Wystąpił błąd')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="card">
      <h2>Rejestracja</h2>

      <form onSubmit={handleSubmit} className="form">
        <label>
          Username
          <input
            type="text"
            name="username"
            value={formData.username}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          Email
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          Hasło
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
          />
        </label>

        <button type="submit" disabled={loading}>
          {loading ? 'Rejestrowanie...' : 'Zarejestruj'}
        </button>
      </form>

      {error && <p className="error-text">{error}</p>}

      {success && (
        <div className="success-box">
          <p>Zarejestrowano użytkownika: <strong>{success.username}</strong></p>
          <p>Role: {success.roles?.join(', ')}</p>
          <button onClick={onSwitchToLogin}>Przejdź do logowania</button>
        </div>
      )}
    </section>
  )
}

export default Register