# Social Media Platform - API Contracts & Database Specifications

## Project Overview
- **Project**: Reddit-like Social Media Platform
- **Tech Stack**: React (Frontend), Spring Boot (Backend), MySQL (Database)
- **Team**: Frontend Developer & Backend Developer (with CoPilot assistance)
- **Date Created**: April 14, 2026

---

## 1. DATABASE SCHEMA (MySQL)

### Users Table
```sql
CREATE TABLE users (
  userId INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  passwordHash VARCHAR(255) NOT NULL,
  profilePicture VARCHAR(255),
  bio TEXT,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Topics Table
```sql
CREATE TABLE topics (
  topicId INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) UNIQUE NOT NULL,
  description TEXT,
  icon VARCHAR(255),
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Posts Table
```sql
CREATE TABLE posts (
  postId INT PRIMARY KEY AUTO_INCREMENT,
  userId INT NOT NULL,
  topicId INT NOT NULL,
  title VARCHAR(200) NOT NULL,
  content LONGTEXT NOT NULL,
  likeCount INT DEFAULT 0,
  commentCount INT DEFAULT 0,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE,
  FOREIGN KEY (topicId) REFERENCES topics(topicId) ON DELETE SET NULL,
  INDEX (topicId),
  INDEX (userId),
  INDEX (createdAt)
);
```

### Likes Table (Optional - For User Engagement Tracking)
```sql
CREATE TABLE likes (
  likeId INT PRIMARY KEY AUTO_INCREMENT,
  postId INT NOT NULL,
  userId INT NOT NULL,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_post_user (postId, userId),
  FOREIGN KEY (postId) REFERENCES posts(postId) ON DELETE CASCADE,
  FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE
);
```

---

## 2. DUMMY DATA - SQL LOAD STATEMENTS

### Insert Dummy Users
```sql
INSERT INTO users (username, email, passwordHash, bio) VALUES
('john_doe', 'john@example.com', 'hashed_password_1', 'Tech enthusiast and coder'),
('jane_smith', 'jane@example.com', 'hashed_password_2', 'Designer and creative thinker'),
('alex_tech', 'alex@example.com', 'hashed_password_3', 'Full-stack developer'),
('emma_writer', 'emma@example.com', 'hashed_password_4', 'Content creator'),
('mike_gamer', 'mike@example.com', 'hashed_password_5', 'Gaming enthusiast');
```

### Insert Dummy Topics
```sql
INSERT INTO topics (name, description) VALUES
('Technology', 'Discuss the latest tech trends and innovations'),
('Gaming', 'All things gaming - reviews, streams, discussions'),
('Design', 'UI/UX design, graphic design, and creative work'),
('Lifestyle', 'Health, fitness, personal development'),
('Entertainment', 'Movies, TV shows, music, and pop culture');
```

### Insert Dummy Posts with Hashtags
```sql
INSERT INTO posts (userId, topicId, title, content) VALUES
(1, 1, 'React 19 is Game Changing', 'Just started using React 19 and I''m blown away! #ReactJS #Frontend #WebDevelopment The new features like automatic batching really improve performance.'),
(2, 3, 'Minimalist UI Design Trends 2026', 'Minimalism continues to dominate UI/UX design. #Design #UI #Minimalism Check out my latest portfolio piece with clean, modern aesthetics.'),
(3, 1, 'Spring Boot Best Practices', 'Here are my top 10 Spring Boot best practices for production apps #SpringBoot #Backend #Java #Development Always use proper logging and error handling!'),
(1, 2, 'Elden Ring DLC Review', 'The new Elden Ring DLC is absolutely incredible #Gaming #EldenRing #Review The boss fights are challenging but fair. Highly recommended!'),
(4, 5, 'Best Netflix Shows of 2026', 'My top 5 Netflix shows this year #Entertainment #Netflix #Shows Don''t sleep on the new sci-fi series!'),
(5, 2, 'Gaming PC Build Guide 2026', 'How to build the ultimate gaming PC #Gaming #Hardware #PCBuilder Update your GPU and CPU for 4K gaming'),
(2, 3, 'Color Psychology in Design', 'Understanding how colors impact user experience #Design #ColorTheory #UX The psychology behind color choices is fascinating!'),
(3, 1, 'MySQL Performance Optimization', 'Tips to optimize your MySQL queries #Database #MySQL #Performance #Backend Always use proper indexing! #SQL');
```

---

## 3. API CONTRACTS (RESTful)

### 3.1 Authentication Endpoints

#### Login
```
POST /api/auth/login
Content-Type: application/json

Request Body:
{
  "username": "john_doe",
  "password": "password123"
}

Response (200 OK):
{
  "success": true,
  "data": {
    "userId": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "token": "jwt_token_here"
  }
}

Response (401 Unauthorized):
{
  "success": false,
  "error": "Invalid credentials",
  "statusCode": 401
}
```

#### Get Current User
```
GET /api/auth/me
Headers: Authorization: Bearer {jwt_token}

Response (200 OK):
{
  "userId": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "bio": "Tech enthusiast and coder"
}

Response (401 Unauthorized):
{
  "success": false,
  "error": "Unauthorized",
  "statusCode": 401
}
```

---

### 3.2 Topics Endpoints

#### Get All Topics
```
GET /api/topics

Response (200 OK):
{
  "success": true,
  "data": [
    {
      "topicId": 1,
      "name": "Technology",
      "description": "Discuss the latest tech trends and innovations"
    },
    {
      "topicId": 2,
      "name": "Gaming",
      "description": "All things gaming - reviews, streams, discussions"
    },
    {
      "topicId": 3,
      "name": "Design",
      "description": "UI/UX design, graphic design, and creative work"
    },
    {
      "topicId": 4,
      "name": "Lifestyle",
      "description": "Health, fitness, personal development"
    },
    {
      "topicId": 5,
      "name": "Entertainment",
      "description": "Movies, TV shows, music, and pop culture"
    }
  ]
}
```

---

### 3.3 Posts Endpoints

#### Get All Posts (With Pagination & Filtering)
```
GET /api/posts?page=1&limit=10&topicId=1
(topicId is optional - omit to get all posts)

Response (200 OK):
{
  "success": true,
  "data": [
    {
      "postId": 1,
      "userId": 1,
      "username": "john_doe",
      "topicId": 1,
      "topicName": "Technology",
      "title": "React 19 is Game Changing",
      "content": "Just started using React 19 and I'm blown away! #ReactJS #Frontend #WebDevelopment The new features like automatic batching really improve performance.",
      "likeCount": 42,
      "commentCount": 5,
      "createdAt": "2026-04-10T14:30:00Z"
    },
    {
      "postId": 3,
      "userId": 3,
      "username": "alex_tech",
      "topicId": 1,
      "topicName": "Technology",
      "title": "Spring Boot Best Practices",
      "content": "Here are my top 10 Spring Boot best practices for production apps #SpringBoot #Backend #Java #Development Always use proper logging and error handling!",
      "likeCount": 28,
      "commentCount": 3,
      "createdAt": "2026-04-11T09:15:00Z"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 10,
    "total": 8,
    "totalPages": 1
  }
}
```

#### Get Single Post
```
GET /api/posts/:postId

Example: GET /api/posts/1

Response (200 OK):
{
  "success": true,
  "data": {
    "postId": 1,
    "userId": 1,
    "username": "john_doe",
    "topicId": 1,
    "topicName": "Technology",
    "title": "React 19 is Game Changing",
    "content": "Just started using React 19 and I'm blown away! #ReactJS #Frontend #WebDevelopment The new features like automatic batching really improve performance.",
    "likeCount": 42,
    "commentCount": 5,
    "createdAt": "2026-04-10T14:30:00Z"
  }
}

Response (404 Not Found):
{
  "success": false,
  "error": "Post not found",
  "statusCode": 404
}
```

#### Create Post
```
POST /api/posts
Headers: 
  Authorization: Bearer {jwt_token}
  Content-Type: application/json

Request Body:
{
  "topicId": 1,
  "title": "Amazing New Framework",
  "content": "I just discovered this amazing framework! #Framework #Development #Coding It's revolutionary!"
}

Response (201 Created):
{
  "success": true,
  "data": {
    "postId": 9,
    "userId": 1,
    "username": "john_doe",
    "topicId": 1,
    "topicName": "Technology",
    "title": "Amazing New Framework",
    "content": "I just discovered this amazing framework! #Framework #Development #Coding It's revolutionary!",
    "likeCount": 0,
    "commentCount": 0,
    "createdAt": "2026-04-14T10:00:00Z"
  }
}

Response (401 Unauthorized):
{
  "success": false,
  "error": "Unauthorized",
  "statusCode": 401
}

Response (400 Bad Request):
{
  "success": false,
  "error": "Missing required fields: topicId, title, content",
  "statusCode": 400
}
```

#### Like a Post (Optional)
```
POST /api/posts/:postId/like
Headers: Authorization: Bearer {jwt_token}

Example: POST /api/posts/1/like

Response (200 OK):
{
  "success": true,
  "data": {
    "postId": 1,
    "likeCount": 43,
    "liked": true
  }
}

Response (200 OK - If already liked, unlike):
{
  "success": true,
  "data": {
    "postId": 1,
    "likeCount": 42,
    "liked": false
  }
}
```

---

## 4. NAMING CONVENTIONS & DATA TYPES

All IDs and field names follow **camelCase** convention:
- `userId`, `topicId`, `postId`, `likeId`
- `username`, `email`, `passwordHash`
- `profilePicture`, `bio`
- `likeCount`, `commentCount`
- `createdAt`, `updatedAt`

---

## 5. ENHANCED FEATURES & RECOMMENDATIONS

### Must-Have Features
| Feature | Priority | Implementation |
|---------|----------|-----------------|
| User Authentication | High | JWT tokens in Spring Boot |
| Posts Creation | High | POST endpoint with userId from token |
| Topic-based Filtering | High | Query parameter in GET /api/posts |
| Hashtag Highlighting | High | Regex on frontend: `/#\w+/g` |
| Pagination | High | offset/limit parameters |

### Nice-to-Have Features
| Feature | Complexity | Benefit |
|---------|-----------|---------|
| Search Posts | Low | Find posts by keyword |
| Like System | Low | Track engagement |
| User Profiles | Medium | View all posts by user |
| Comments | Medium | Threaded discussions |
| Post Editing/Deletion | Low-Medium | User content management |
| Trending Topics | Medium | Show top categories |
| Post Deletion | Low | Allow users to remove posts |
| Timestamps (relative) | Low | "2 hours ago" format |

---

## 6. FRONTEND HASHTAG HIGHLIGHTING CODE

Use this regex to parse and highlight hashtags in React:

```javascript
// Function to split text with hashtags
const parseHashtags = (text) => {
  const hashtagRegex = /#\w+/g;
  return text.split(hashtagRegex).reduce((acc, part, index) => {
    acc.push(part);
    const hashtagMatch = text.match(hashtagRegex);
    if (hashtagMatch && index < hashtagMatch.length) {
      acc.push(hashtagMatch[index]);
    }
    return acc;
  }, []);
};

// CSS Class for Hashtag Styling
.hashtag {
  color: #1da1f2;
  font-weight: 500;
  cursor: pointer;
  text-decoration: none;
}

.hashtag:hover {
  text-decoration: underline;
}
```

---

## 7. BACKEND SETUP CHECKLIST (Ashwin)

- [ ] Create Spring Boot project with Spring Data JPA, Spring Security, Spring Web
- [ ] Configure MySQL database connection in application.properties
- [ ] Create entity classes: User, Topic, Post, Like
- [ ] Create repositories: UserRepository, TopicRepository, PostRepository, LikeRepository
- [ ] Create service layer for business logic
- [ ] Create controllers for all API endpoints
- [ ] Implement JWT authentication
- [ ] Add pagination support to PostRepository.findAll()
- [ ] Create a DataLoader/CommandLineRunner to insert dummy data on startup
- [ ] Add error handling with proper HTTP status codes
- [ ] Test all endpoints with Postman
- [ ] Add request validation (e.g., @NotNull, @Size annotations)

---

## 8. FRONTEND SETUP CHECKLIST (Frontend Developer)

- [ ] Create React project (Vite or CRA)
- [ ] Setup API service layer (axios/fetch)
- [ ] Implement context API or Redux for auth state
- [ ] Create Login page
- [ ] Create Post Feed component with API integration
- [ ] Add Topic Filter slider/dropdown at top
- [ ] Implement hashtag highlighting with CSS styling
- [ ] Add Post Creation modal/form
- [ ] Implement pagination (load more button or infinite scroll)
- [ ] Add loading states and error handling
- [ ] Create responsive design
- [ ] Test all flows end-to-end

---

## 9. ERROR RESPONSE STANDARDS

All error responses from backend should follow this format:

```json
{
  "success": false,
  "error": "Error message description",
  "statusCode": 400,
  "timestamp": "2026-04-14T10:30:00Z"
}
```

HTTP Status Codes to use:
- `200` - OK / Success
- `201` - Created (POST successful)
- `400` - Bad Request (validation error)
- `401` - Unauthorized (no/invalid token)
- `403` - Forbidden (no permission)
- `404` - Not Found
- `500` - Internal Server Error

---

## 10. KEY IMPLEMENTATION NOTES

### For Backend Developer:
1. **String vs Enum**: Consider using Enum for topic names instead of text
2. **Timestamps**: Always use UTC timestamps (2026-04-14T10:30:00Z format)
3. **Password Hashing**: NEVER store plain passwords. Use BCrypt or similar
4. **Token Expiration**: Set JWT expiration (recommend 24 hours)
5. **CORS**: Enable CORS for frontend domain
6. **Validation**: Validate all inputs (length, type, required fields)
7. **Soft Delete**: Consider soft delete for posts instead of hard delete

### For Frontend Developer:
1. **Hashtag Regex**: `/#\w+/g` matches hashtags (letters, numbers, underscore)
2. **Loading States**: Show skeleton/spinner while fetching
3. **Error Toast**: Display user-friendly error messages
4. **Local Storage**: Store JWT token in localStorage or sessionStorage
5. **Relative Dates**: Convert timestamps to "2 hours ago" format
6. **Debounce Search**: If implementing search, debounce API calls

---

## 11. DEVELOPMENT WORKFLOW

1. **Backend**: Build and test all API endpoints first (use Postman)
2. **Frontend**: Consume APIs and build UI components
3. **Integration**: Test full flow end-to-end
4. **Base URL**: Frontend should use configurable API base URL (e.g., http://localhost:8080/api)
5. **Testing**: Both should test their respective components independently

---

## Contact & Clarifications
If you have any questions or need clarification on any API endpoint or database field, reach out before starting development to ensure alignment.

Happy coding! 🚀
