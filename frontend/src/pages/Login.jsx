import { useState } from 'react'
import API_BASE_URL from '../config/api'

function Login({ onLoginSuccess }) {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

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
    setLoading(true)

    try {
      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Nie udało się zalogować')
      }

      if (!data.token) {
        throw new Error('Brak tokenu w odpowiedzi')
      }

      onLoginSuccess(data.token)
    } catch (err) {
      setError(err.message || 'Wystąpił błąd logowania')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="card">
      <h2>Logowanie</h2>

      <form onSubmit={handleSubmit} className="form">
        <label>
          Login
          <input
            type="text"
            name="username"
            value={formData.username}
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
          {loading ? 'Logowanie...' : 'Zaloguj się'}
        </button>
      </form>

      {error && <p className="error-text">{error}</p>}
    </section>
  )
}

export default Login