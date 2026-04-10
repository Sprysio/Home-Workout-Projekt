import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import ExerciseForm from '../components/ExerciseForm'
import API_BASE_URL from '../config/api'

function Exercises({ isAdmin }) {
  const [exercises, setExercises] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [editingExercise, setEditingExercise] = useState(null)

  const loadExercises = async () => {
    setError('')

    try {
      const response = await fetch(`${API_BASE_URL}/exercises`)
      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Nie udało się pobrać ćwiczeń')
      }

      setExercises(Array.isArray(data) ? data : [])
    } catch (err) {
      setError(err.message || 'Błąd pobierania ćwiczeń')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadExercises()
  }, [])

  const handleDelete = async (id) => {
    const confirmed = window.confirm('Na pewno usunąć to ćwiczenie?')
    if (!confirmed) return

    setError('')
    setMessage('')

    try {
      const token = localStorage.getItem('token')

      const response = await fetch(`${API_BASE_URL}/exercises/${id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Nie udało się usunąć ćwiczenia')
      }

      setMessage(`Usunięto ćwiczenie o id ${data.deleted}`)
      loadExercises()
    } catch (err) {
      setError(err.message || 'Błąd usuwania ćwiczenia')
    }
  }

  return (
    <section className="card">
      <h2>Ćwiczenia</h2>

      {isAdmin && (
        <ExerciseForm
          onSuccess={() => {
            setEditingExercise(null)
            setMessage('Zapisano ćwiczenie')
            loadExercises()
          }}
          editingExercise={editingExercise}
          onCancelEdit={() => setEditingExercise(null)}
        />
      )}

      {message && <p className="success-text">{message}</p>}
      {error && <p className="error-text">{error}</p>}
      {loading && <p>Ładowanie...</p>}

      {!loading && exercises.length === 0 && <p>Brak ćwiczeń.</p>}

      {!loading && exercises.length > 0 && (
        <div className="exercise-grid">
          {exercises.map((exercise) => (
            <div key={exercise.id} className="exercise-item">
              <h3>{exercise.name}</h3>
              <p>
                <strong>Muscle group:</strong> {exercise.muscleGroup}
              </p>

              <div className="exercise-actions">
                <Link to={`/exercises/${exercise.id}`} className="nav-button">
                  Szczegóły
                </Link>

                {isAdmin && (
                  <>
                    <button onClick={() => setEditingExercise(exercise)}>
                      Edytuj
                    </button>
                    <button onClick={() => handleDelete(exercise.id)}>
                      Usuń
                    </button>
                  </>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  )
}

export default Exercises