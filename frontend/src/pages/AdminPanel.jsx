import { useEffect, useState } from 'react'

function AdminPanel({ token, currentUsername }) {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  const loadUsers = async () => {
    setError('')
    setMessage('')

    try {
      const response = await fetch('http://localhost:8080/api/auth/users', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Nie udało się pobrać użytkowników')
      }

      setUsers(Array.isArray(data) ? data : [])
    } catch (err) {
      setError(err.message || 'Błąd pobierania użytkowników')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadUsers()
  }, [])

  const changeRole = async (id, role, username) => {
    if (username === currentUsername) {
      setError('Nie możesz zmienić roli samemu sobie.')
      return
    }

    setError('')
    setMessage('')

    try {
      const response = await fetch(`http://localhost:8080/api/auth/users/${id}/role`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ role }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Nie udało się zmienić roli')
      }

      setMessage(`Zmieniono rolę użytkownika ${data.username}`)
      await loadUsers()
    } catch (err) {
      setError(err.message || 'Błąd zmiany roli')
    }
  }

  return (
    <section className="card">
      <h2>Panel administratora</h2>

      {loading && <p>Ładowanie użytkowników...</p>}
      {error && <p className="error-text">{error}</p>}
      {message && <p className="success-text">{message}</p>}

      {!loading && users.length > 0 && (
        <div className="users-list">
          {users.map((user) => {
            const isCurrentUser = user.username === currentUsername

            return (
              <div key={user.id} className="user-row">
                <div>
                  <strong>{user.username}</strong>
                  <p>{user.email}</p>
                  <p>{user.roles?.join(', ')}</p>
                  {isCurrentUser && (
                    <p className="self-label">To jesteś Ty</p>
                  )}
                </div>

                {!isCurrentUser && (
                  <div className="role-buttons">
                    <button onClick={() => changeRole(user.id, 'ROLE_USER', user.username)}>
                      Ustaw ROLE_USER
                    </button>
                    <button onClick={() => changeRole(user.id, 'ROLE_ADMIN', user.username)}>
                      Ustaw ROLE_ADMIN
                    </button>
                  </div>
                )}
              </div>
            )
          })}
        </div>
      )}
    </section>
  )
}

export default AdminPanel