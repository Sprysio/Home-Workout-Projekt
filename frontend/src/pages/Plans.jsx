import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import API_BASE_URL from '../config/api'

function Plans() {
  const [plans, setPlans] = useState([])
  const [planName, setPlanName] = useState('')
  const [loading, setLoading] = useState(true)
  const [creating, setCreating] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  const loadPlans = async () => {
    setError('')

    try {
      const token = localStorage.getItem('token')

      const response = await fetch(`${API_BASE_URL}/plans`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Nie udało się pobrać planów')
      }

      setPlans(Array.isArray(data) ? data : [])
    } catch (err) {
      setError(err.message || 'Błąd pobierania planów')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadPlans()
  }, [])

  const handleCreatePlan = async (e) => {
    e.preventDefault()
    setError('')
    setMessage('')
    setCreating(true)

    try {
      const token = localStorage.getItem('token')

      const response = await fetch(`${API_BASE_URL}/plans`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          name: planName,
          items: [],
        }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Nie udało się utworzyć planu')
      }

      setPlanName('')
      setMessage(`Utworzono plan: ${data.name}`)
      loadPlans()
    } catch (err) {
      setError(err.message || 'Błąd tworzenia planu')
    } finally {
      setCreating(false)
    }
  }

  const handleDeletePlan = async (id) => {
    const confirmed = window.confirm('Na pewno usunąć ten plan?')
    if (!confirmed) return

    setError('')
    setMessage('')

    try {
      const token = localStorage.getItem('token')

      const response = await fetch(`${API_BASE_URL}/plans/${id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Nie udało się usunąć planu')
      }

      setMessage(`Usunięto plan o id ${data.deleted}`)
      loadPlans()
    } catch (err) {
      setError(err.message || 'Błąd usuwania planu')
    }
  }

  return (
    <section className="card">
      <h2>Moje plany treningowe</h2>

      <form onSubmit={handleCreatePlan} className="form" style={{ marginBottom: '24px' }}>
        <label>
          Nazwa planu
          <input
            type="text"
            value={planName}
            onChange={(e) => setPlanName(e.target.value)}
            placeholder="Np. Plan Push Pull Legs"
            required
          />
        </label>

        <button type="submit" disabled={creating}>
          {creating ? 'Tworzenie...' : 'Utwórz plan'}
        </button>
      </form>

      {message && <p className="success-text">{message}</p>}
      {error && <p className="error-text">{error}</p>}
      {loading && <p>Ładowanie planów...</p>}

      {!loading && plans.length === 0 && <p>Nie masz jeszcze żadnych planów.</p>}

      {!loading && plans.length > 0 && (
        <div className="plans-list">
          {plans.map((plan) => (
            <div key={plan.id} className="plan-item">
              <div>
                <h3>{plan.name}</h3>
                <p>
                  <strong>Owner:</strong> {plan.ownerUsername}
                </p>
                <p>
                  <strong>Liczba ćwiczeń:</strong> {plan.items?.length || 0}
                </p>
              </div>

              <div className="exercise-actions">
                <Link to={`/plans/${plan.id}`} className="nav-button">
                  Otwórz plan
                </Link>

                <button onClick={() => handleDeletePlan(plan.id)}>
                  Usuń
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  )
}

export default Plans