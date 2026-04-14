import { useEffect, useMemo, useState } from 'react'
import './App.css'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api'
const TOKEN_KEY = 'social_media_token'

function parseApiError(error, fallback) {
  if (!error) {
    return fallback
  }

  if (typeof error === 'string') {
    return error
  }

  if (error.error) {
    return error.error
  }

  return fallback
}

function renderHashtags(text) {
  const hashtagRegex = /(#\w+)/g
  return text.split(hashtagRegex).map((part, index) => {
    if (/^#\w+$/.test(part)) {
      return (
        <span key={`${part}-${index}`} className="hashtag">
          {part}
        </span>
      )
    }
    return <span key={`${part}-${index}`}>{part}</span>
  })
}

function App() {
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY) || '')
  const [user, setUser] = useState(null)
  const [topics, setTopics] = useState([])
  const [posts, setPosts] = useState([])
  const [selectedTopic, setSelectedTopic] = useState('')
  const [page, setPage] = useState(1)
  const [pagination, setPagination] = useState({ page: 1, limit: 6, total: 0, totalPages: 0 })
  const [feedLoading, setFeedLoading] = useState(false)
  const [loginLoading, setLoginLoading] = useState(false)
  const [createLoading, setCreateLoading] = useState(false)
  const [error, setError] = useState('')
  const [loginForm, setLoginForm] = useState({ username: 'john_doe', password: 'password123' })
  const [postForm, setPostForm] = useState({ topicId: '', title: '', content: '' })

  const canLoadMore = useMemo(() => page < pagination.totalPages, [page, pagination.totalPages])

  useEffect(() => {
    if (!token) {
      return
    }

    bootstrapWithToken(token)
  }, [])

  async function apiRequest(path, options = {}, authToken = token) {
    const headers = {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    }

    if (authToken) {
      headers.Authorization = `Bearer ${authToken}`
    }

    const response = await fetch(`${API_BASE_URL}${path}`, {
      ...options,
      headers,
    })

    const body = await response.json().catch(() => ({}))
    if (!response.ok) {
      throw body
    }

    return body
  }

  async function bootstrapWithToken(activeToken) {
    try {
      setError('')
      const [userData, topicsData] = await Promise.all([
        apiRequest('/auth/me', {}, activeToken),
        apiRequest('/topics', {}, activeToken),
      ])
      setUser(userData)
      setTopics(topicsData.data || [])
      await loadPosts(1, selectedTopic, false, activeToken)
    } catch (err) {
      localStorage.removeItem(TOKEN_KEY)
      setToken('')
      setUser(null)
      setError(parseApiError(err, 'Session expired. Please log in again.'))
    }
  }

  async function loadPosts(targetPage, topicValue, append, authToken = token) {
    setFeedLoading(true)
    try {
      const query = new URLSearchParams({
        page: String(targetPage),
        limit: '6',
      })

      if (topicValue) {
        query.append('topicId', topicValue)
      }

      const response = await apiRequest(`/posts?${query.toString()}`, {}, authToken)
      const newPosts = response.data || []
      setPosts((prev) => (append ? [...prev, ...newPosts] : newPosts))
      setPagination(response.pagination || { page: targetPage, limit: 6, total: newPosts.length, totalPages: 1 })
      setPage(targetPage)
    } catch (err) {
      setError(parseApiError(err, 'Could not fetch posts.'))
    } finally {
      setFeedLoading(false)
    }
  }

  async function handleLogin(event) {
    event.preventDefault()
    setLoginLoading(true)
    setError('')

    try {
      const response = await apiRequest('/auth/login', {
        method: 'POST',
        body: JSON.stringify(loginForm),
      }, '')

      const nextToken = response?.data?.token || ''
      if (!nextToken) {
        throw new Error('Invalid login response')
      }

      setToken(nextToken)
      localStorage.setItem(TOKEN_KEY, nextToken)
      await bootstrapWithToken(nextToken)
    } catch (err) {
      setError(parseApiError(err, 'Invalid credentials.'))
    } finally {
      setLoginLoading(false)
    }
  }

  function logout() {
    setToken('')
    setUser(null)
    setTopics([])
    setPosts([])
    setSelectedTopic('')
    setPage(1)
    localStorage.removeItem(TOKEN_KEY)
  }

  async function onChangeTopic(event) {
    const nextTopic = event.target.value
    setSelectedTopic(nextTopic)
    await loadPosts(1, nextTopic, false)
  }

  async function handleCreatePost(event) {
    event.preventDefault()
    setCreateLoading(true)
    setError('')

    try {
      await apiRequest('/posts', {
        method: 'POST',
        body: JSON.stringify({
          ...postForm,
          topicId: Number(postForm.topicId),
        }),
      })

      setPostForm({ topicId: '', title: '', content: '' })
      await loadPosts(1, selectedTopic, false)
    } catch (err) {
      setError(parseApiError(err, 'Could not create post.'))
    } finally {
      setCreateLoading(false)
    }
  }

  async function toggleLike(postId) {
    try {
      const response = await apiRequest(`/posts/${postId}/like`, { method: 'POST' })
      const likeData = response.data
      setPosts((prev) =>
        prev.map((post) =>
          post.postId === postId ? { ...post, likeCount: likeData.likeCount } : post,
        ),
      )
    } catch (err) {
      setError(parseApiError(err, 'Could not toggle like.'))
    }
  }

  async function loadMore() {
    if (!canLoadMore || feedLoading) {
      return
    }
    await loadPosts(page + 1, selectedTopic, true)
  }

  if (!token || !user) {
    return (
      <main className="page-shell auth-shell">
        <section className="auth-card">
          <h1>Social Sphere</h1>
          <p className="subtitle">Sign in to explore posts by topic and share your thoughts.</p>
          <form onSubmit={handleLogin} className="form-stack">
            <label>
              Username
              <input
                value={loginForm.username}
                onChange={(event) =>
                  setLoginForm((prev) => ({ ...prev, username: event.target.value }))
                }
                required
              />
            </label>
            <label>
              Password
              <input
                type="password"
                value={loginForm.password}
                onChange={(event) =>
                  setLoginForm((prev) => ({ ...prev, password: event.target.value }))
                }
                required
              />
            </label>
            <button type="submit" disabled={loginLoading}>
              {loginLoading ? 'Signing In...' : 'Sign In'}
            </button>
          </form>
          <p className="hint">Demo user: john_doe / password123</p>
          {error ? <p className="error-banner">{error}</p> : null}
        </section>
      </main>
    )
  }

  return (
    <main className="page-shell">
      <header className="topbar">
        <div>
          <h1>Social Sphere</h1>
          <p>Welcome back, {user.username}</p>
        </div>
        <button className="ghost-btn" onClick={logout}>
          Logout
        </button>
      </header>

      {error ? <p className="error-banner">{error}</p> : null}

      <section className="controls-grid">
        <article className="panel">
          <h2>Create Post</h2>
          <form onSubmit={handleCreatePost} className="form-stack">
            <label>
              Topic
              <select
                value={postForm.topicId}
                onChange={(event) =>
                  setPostForm((prev) => ({ ...prev, topicId: event.target.value }))
                }
                required
              >
                <option value="">Choose topic</option>
                {topics.map((topic) => (
                  <option key={topic.topicId} value={topic.topicId}>
                    {topic.name}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Title
              <input
                value={postForm.title}
                onChange={(event) => setPostForm((prev) => ({ ...prev, title: event.target.value }))}
                maxLength={200}
                required
              />
            </label>
            <label>
              Content
              <textarea
                rows={4}
                value={postForm.content}
                onChange={(event) => setPostForm((prev) => ({ ...prev, content: event.target.value }))}
                required
              />
            </label>
            <button type="submit" disabled={createLoading}>
              {createLoading ? 'Publishing...' : 'Publish'}
            </button>
          </form>
        </article>

        <article className="panel">
          <h2>Feed Filter</h2>
          <label>
            Topic
            <select value={selectedTopic} onChange={onChangeTopic}>
              <option value="">All Topics</option>
              {topics.map((topic) => (
                <option key={topic.topicId} value={topic.topicId}>
                  {topic.name}
                </option>
              ))}
            </select>
          </label>
          <p className="hint">
            Page {pagination.page} of {Math.max(1, pagination.totalPages)} | {pagination.total} total posts
          </p>
        </article>
      </section>

      <section className="feed">
        {posts.map((post) => (
          <article key={post.postId} className="post-card">
            <div className="post-meta">
              <span>{post.username}</span>
              <span>{post.topicName}</span>
            </div>
            <h3>{post.title}</h3>
            <p>{renderHashtags(post.content)}</p>
            <div className="post-actions">
              <small>{new Date(post.createdAt).toLocaleString()}</small>
              <button className="ghost-btn" onClick={() => toggleLike(post.postId)}>
                Like ({post.likeCount})
              </button>
            </div>
          </article>
        ))}

        {!posts.length && !feedLoading ? <p className="hint">No posts found for this topic.</p> : null}
        {feedLoading ? <p className="hint">Loading posts...</p> : null}
      </section>

      <div className="load-more-wrap">
        <button onClick={loadMore} disabled={!canLoadMore || feedLoading}>
          {canLoadMore ? 'Load More' : 'No More Posts'}
        </button>
      </div>
    </main>
  )
}

export default App
