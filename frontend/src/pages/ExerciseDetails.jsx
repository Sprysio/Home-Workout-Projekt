import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'

const API_BASE = 'http://localhost:8080/api'

function ExerciseDetails() {
  const { id } = useParams()
  const [exercise, setExercise] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const loadExercise = async () => {
      try {
        const response = await fetch(`${API_BASE}/exercises/${id}`)
        const data = await response.json()

        if (!response.ok) {
          throw new Error(data.error || data.message || 'Nie udało się pobrać ćwiczenia')
        }

        setExercise(data)
      } catch (err) {
        setError(err.message || 'Błąd pobierania ćwiczenia')
      } finally {
        setLoading(false)
      }
    }

    loadExercise()
  }, [id])

  return (
    <section className="card">
      <Link to="/exercises" className="nav-button">
        ← Wróć do listy
      </Link>

      {loading && <p style={{ marginTop: '16px' }}>Ładowanie...</p>}
      {error && <p className="error-text">{error}</p>}

      {exercise && (
        <div style={{ marginTop: '16px' }}>
          <h2>{exercise.name}</h2>
          <p>{exercise.description}</p>
          <p>
            <strong>Muscle group:</strong> {exercise.muscleGroup}
          </p>
          <p>
            <strong>ID:</strong> {exercise.id}
          </p>
        </div>
      )}
    </section>
  )
}

export default ExerciseDetails