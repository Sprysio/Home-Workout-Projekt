import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import API_BASE_URL from '../config/api'

function PlanDetails() {
  const { id } = useParams()

  const [plan, setPlan] = useState(null)
  const [exercises, setExercises] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  const [newName, setNewName] = useState('')
  const [selectedExerciseId, setSelectedExerciseId] = useState('')
  const [sets, setSets] = useState(3)
  const [reps, setReps] = useState(8)

  const loadPlan = async () => {
    try {
      const token = localStorage.getItem('token')

      const response = await fetch(`${API_BASE_URL}/plans/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Nie udało się pobrać planu')
      }

      setPlan(data)
      setNewName(data.name || '')
    } catch (err) {
      setError(err.message || 'Błąd pobierania planu')
    }
  }

  const loadExercises = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/exercises`)
      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Nie udało się pobrać ćwiczeń')
      }

      setExercises(Array.isArray(data) ? data : [])
    } catch (err) {
      setError(err.message || 'Błąd pobierania ćwiczeń')
    }
  }

  useEffect(() => {
    const init = async () => {
      setLoading(true)
      setError('')
      await Promise.all([loadPlan(), loadExercises()])
      setLoading(false)
    }

    init()
  }, [id])

  const handleRenamePlan = async (e) => {
    e.preventDefault()
    setError('')
    setMessage('')

    try {
      const token = localStorage.getItem('token')

      const response = await fetch(`${API_BASE_URL}/plans/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ name: newName }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Nie udało się zmienić nazwy planu')
      }

      setPlan(data)
      setMessage('Zmieniono nazwę planu')
    } catch (err) {
      setError(err.message || 'Błąd zmiany nazwy planu')
    }
  }

  const handleAddExercise = async (e) => {
    e.preventDefault()
    setError('')
    setMessage('')

    try {
      const token = localStorage.getItem('token')

      const response = await fetch(`${API_BASE_URL}/plans/${id}/add`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          exerciseId: Number(selectedExerciseId),
          sets: Number(sets),
          reps: Number(reps),
        }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Nie udało się dodać ćwiczenia do planu')
      }

      setPlan(data)
      setMessage('Dodano ćwiczenie do planu')
      setSelectedExerciseId('')
      setSets(3)
      setReps(8)
    } catch (err) {
      setError(err.message || 'Błąd dodawania ćwiczenia')
    }
  }

    const getExerciseName = (exerciseId) => {
    const exercise = exercises.find((ex) => ex.id === exerciseId)
    return exercise ? exercise.name : `Ćwiczenie #${exerciseId}`
    }

  return (
    <section className="card">
      <Link to="/plans" className="nav-button">
        ← Wróć do planów
      </Link>

      {loading && <p style={{ marginTop: '16px' }}>Ładowanie...</p>}
      {error && <p className="error-text">{error}</p>}
      {message && <p className="success-text">{message}</p>}

      {plan && (
        <>
          <div style={{ marginTop: '20px' }}>
            <h2>{plan.name}</h2>
            <p>
              <strong>Owner:</strong> {plan.ownerUsername}
            </p>
            <p>
              <strong>ID planu:</strong> {plan.id}
            </p>
          </div>

          <div className="plan-sections">
            <div className="card">
              <h3>Zmień nazwę planu</h3>

              <form onSubmit={handleRenamePlan} className="form">
                <label>
                  Nowa nazwa
                  <input
                    type="text"
                    value={newName}
                    onChange={(e) => setNewName(e.target.value)}
                    required
                  />
                </label>

                <button type="submit">Zapisz nazwę</button>
              </form>
            </div>

            <div className="card">
              <h3>Dodaj ćwiczenie do planu</h3>

              <form onSubmit={handleAddExercise} className="form">
                <label>
                  Ćwiczenie
                  <select
                    value={selectedExerciseId}
                    onChange={(e) => setSelectedExerciseId(e.target.value)}
                    required
                  >
                    <option value="">Wybierz ćwiczenie</option>
                    {exercises.map((exercise) => (
                      <option key={exercise.id} value={exercise.id}>
                        {exercise.name}
                      </option>
                    ))}
                  </select>
                </label>

                <label>
                  Sets
                  <input
                    type="number"
                    min="1"
                    value={sets}
                    onChange={(e) => setSets(e.target.value)}
                    required
                  />
                </label>

                <label>
                  Reps
                  <input
                    type="number"
                    min="1"
                    value={reps}
                    onChange={(e) => setReps(e.target.value)}
                    required
                  />
                </label>

                <button type="submit">Dodaj do planu</button>
              </form>
            </div>
          </div>

          <div className="card">
            <h3>Ćwiczenia w planie</h3>

            {!plan.items || plan.items.length === 0 ? (
              <p>Ten plan nie ma jeszcze żadnych ćwiczeń.</p>
            ) : (
              <div className="plans-list">
                {plan.items.map((item, index) => (
                    <div key={index} className="plan-item">
                        <h4>{getExerciseName(item.exerciseId)}</h4>
                        <p><strong>Sets:</strong> {item.sets}</p>
                        <p><strong>Reps:</strong> {item.reps}</p>
                    </div>
                ))}
              </div>
            )}
          </div>
        </>
      )}
    </section>
  )
}

export default PlanDetails