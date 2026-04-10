import { useEffect, useState } from 'react'

const API_BASE = 'http://localhost:8080/api'

function ExerciseForm({ onSuccess, editingExercise, onCancelEdit }) {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    muscleGroup: '',
  })

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (editingExercise) {
      setFormData({
        name: editingExercise.name || '',
        description: editingExercise.description || '',
        muscleGroup: editingExercise.muscleGroup || '',
      })
    } else {
      setFormData({
        name: '',
        description: '',
        muscleGroup: '',
      })
    }
  }, [editingExercise])

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
      const token = localStorage.getItem('token')

      const isEditing = Boolean(editingExercise)
      const url = isEditing
        ? `${API_BASE}/exercises/${editingExercise.id}`
        : `${API_BASE}/exercises`

      const method = isEditing ? 'PUT' : 'POST'

      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(formData),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Nie udało się zapisać ćwiczenia')
      }

      setFormData({
        name: '',
        description: '',
        muscleGroup: '',
      })

      onSuccess?.(data)
    } catch (err) {
      setError(err.message || 'Błąd zapisu ćwiczenia')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="card" style={{ marginBottom: '24px' }}>
      <h3>{editingExercise ? 'Edytuj ćwiczenie' : 'Dodaj ćwiczenie'}</h3>

      <form onSubmit={handleSubmit} className="form">
        <label>
          Nazwa
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required={!editingExercise}
          />
        </label>

        <label>
          Opis
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            rows="4"
          />
        </label>

        <label>
          Muscle group
          <input
            type="text"
            name="muscleGroup"
            value={formData.muscleGroup}
            onChange={handleChange}
            placeholder="np. CHEST"
          />
        </label>

        <div className="exercise-actions">
          <button type="submit" disabled={loading}>
            {loading ? 'Zapisywanie...' : editingExercise ? 'Zapisz zmiany' : 'Dodaj ćwiczenie'}
          </button>

          {editingExercise && (
            <button type="button" onClick={onCancelEdit}>
              Anuluj
            </button>
          )}
        </div>
      </form>

      {error && <p className="error-text">{error}</p>}
    </div>
  )
}

export default ExerciseForm