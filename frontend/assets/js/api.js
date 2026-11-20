/**
 * BlogSphere Centralized API Service
 * Connects to Spring Boot backend at http://localhost:8080/api
 * Includes built-in Mock Data Fallback so pages work seamlessly offline and online.
 */

const API_BASE = 'http://localhost:8080/api';

// --- Local Storage Helpers ---
const STORAGE_TOKEN_KEY = 'blogsphere_token';
const STORAGE_USER_KEY = 'blogsphere_user';

// --- Built-in Mock Database matching wireframes ---
const MOCK_DB = {
  users: [
    {
      id: 1,
      fullName: "Admin",
      email: "admin@blogsphere.com",
      role: "ROLE_ADMIN",
      bio: "Platform Administrator & Content Moderator",
      avatarUrl: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80",
      totalBlogs: 0,
      createdAt: "2024-01-01T00:00:00"
    },
    {
      id: 2,
      fullName: "Rohan Verma",
      email: "rohan@blogsphere.com",
      role: "ROLE_AUTHOR",
      bio: "Java Developer and Tech Blogger. I write about Java, Spring Boot and Web Development.",
      avatarUrl: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80",
      totalBlogs: 12,
      followersCount: 248,
      followingCount: 32,
      createdAt: "2024-01-15T10:00:00"
    },
    {
      id: 3,
      fullName: "Ankit Sharma",
      email: "ankit@blogsphere.com",
      role: "ROLE_READER",
      bio: "Passionate reader and junior tech enthusiast.",
      avatarUrl: "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=200&q=80",
      totalBlogs: 0,
      createdAt: "2024-02-10T12:00:00"
    },
    {
      id: 4,
      fullName: "Priya Desai",
      email: "priya@blogsphere.com",
      role: "ROLE_READER",
      bio: "Reading about software engineering and lifestyle.",
      avatarUrl: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80",
      totalBlogs: 0,
      createdAt: "2024-02-14T09:30:00"
    },
    {
      id: 5,
      fullName: "Rahul Mehta",
      email: "rahul@blogsphere.com",
      role: "ROLE_READER",
      bio: "Bookworm, avid reader, and student.",
      avatarUrl: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&q=80",
      totalBlogs: 0,
      createdAt: "2024-03-01T15:00:00"
    }
  ],
  posts: [
    {
      id: 1,
      title: "Exploring the Hills of Himachal",
      slug: "exploring-the-hills-of-himachal",
      summary: "A wonderful journey through the hills, nature and culture of Himachal.",
      contentHtml: "<p>A wonderful journey through the hills, nature and culture of Himachal Pradesh. From the serene valleys of Kullu to the adventurous treks of Spiti, experience pure mountain magic.</p><p>The mountain breeze, vibrant cedar forests, and welcoming local communities make Himachal an unforgettable retreat.</p>",
      coverImage: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80",
      category: "Travel",
      tags: ["himachal", "travel"],
      authorId: 2,
      authorName: "Rohan Verma",
      authorAvatar: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80",
      createdAt: "2025-04-10T10:00:00",
      viewCount: 54,
      commentCount: 12,
      likeCount: 24,
      status: "PUBLISHED",
      likedByCurrentUser: false,
      bookmarkedByCurrentUser: false
    },
    {
      id: 2,
      title: "Understanding Spring Boot",
      slug: "understanding-spring-boot",
      summary: "Spring Boot is a powerful framework for building robust and scalable Java applications.",
      contentHtml: "<p>Spring Boot is a powerful framework for building robust and scalable Java applications. It makes development faster and easier with its auto-configuration and production-ready features.</p><p>By eliminating boilerplate XML configs and provisioning embedded web servers like Tomcat, developers can focus purely on business logic.</p>",
      coverImage: "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=800&q=80",
      category: "Technology",
      tags: ["java", "springboot", "web"],
      authorId: 2,
      authorName: "Rohan Verma",
      authorAvatar: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80",
      createdAt: "2025-04-08T14:30:00",
      viewCount: 1200,
      commentCount: 18,
      likeCount: 124,
      status: "PUBLISHED",
      likedByCurrentUser: true,
      bookmarkedByCurrentUser: false
    }
  ],
  comments: [
    {
      id: 1,
      postId: 2,
      postTitle: "Understanding Spring Boot",
      userName: "Ankit Sharma",
      userAvatar: "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=200&q=80",
      content: "Nice explanation! Very helpful for beginners.",
      createdAt: "2025-04-09T09:15:00"
    },
    {
      id: 2,
      postId: 2,
      postTitle: "Understanding Spring Boot",
      userName: "Priya Desai",
      userAvatar: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80",
      content: "Can you also write about Spring Security?",
      createdAt: "2025-04-09T11:40:00"
    }
  ],
  categories: [
    { id: 1, name: "Technology", slug: "technology", icon: "monitor", postCount: 26 },
    { id: 2, name: "Education", slug: "education", icon: "book-open", postCount: 14 },
    { id: 3, name: "Business", slug: "business", icon: "trending-up", postCount: 10 },
    { id: 4, name: "Lifestyle", slug: "lifestyle", icon: "heart", postCount: 8 },
    { id: 5, name: "Travel", slug: "travel", icon: "navigation", postCount: 12 }
  ],
  notifications: [
    {
      id: 1,
      type: "LIKE",
      message: "Ankit Sharma liked your blog \"Understanding Spring Boot\".",
      timeAgo: "2 hours ago"
    },
    {
      id: 2,
      type: "COMMENT",
      message: "Priya Desai commented on your blog \"Introduction to REST API\".",
      timeAgo: "4 hours ago"
    },
    {
      id: 3,
      type: "FOLLOW",
      message: "Rahul Mehta started following you.",
      timeAgo: "1 day ago"
    },
    {
      id: 4,
      type: "NEW_POST",
      message: "A new blog \"Java for Beginners\" is published by Rohan Verma.",
      timeAgo: "1 day ago"
    }
  ],
  authorDashboard: {
    publishedBlogs: 12,
    drafts: 3,
    totalViews: 248,
    totalLikes: 56,
    recentActivity: [
      { type: "LIKE", text: "Your blog \"Exploring Java Spring Boot\" received 5 new likes.", timeAgo: "2 hours ago" },
      { type: "COMMENT", text: "New comment on \"My Learning Journey\".", timeAgo: "4 hours ago" },
      { type: "PUBLISH", text: "You published \"Introduction to REST API\".", timeAgo: "1 day ago" }
    ]
  },
  readerDashboard: {
    articlesReadCount: 18,
    bookmarksCount: 6,
    followingAuthorsCount: 4,
    commentsCount: 9,
    savedArticles: [
      {
        id: 1,
        title: "Exploring the Hills of Himachal",
        category: "Travel",
        authorName: "Rohan Verma",
        createdAt: "2025-04-10T10:00:00",
        coverImage: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80",
        summary: "A wonderful journey through the hills, nature and culture of Himachal."
      },
      {
        id: 2,
        title: "Understanding Spring Boot",
        category: "Technology",
        authorName: "Rohan Verma",
        createdAt: "2025-04-08T14:30:00",
        coverImage: "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=800&q=80",
        summary: "Spring Boot is a powerful framework for building robust and scalable Java applications."
      }
    ],
    readingHistory: [
      {
        id: 2,
        title: "Understanding Spring Boot",
        category: "Technology",
        authorName: "Rohan Verma",
        createdAt: "2025-04-08T14:30:00"
      },
      {
        id: 1,
        title: "Exploring the Hills of Himachal",
        category: "Travel",
        authorName: "Rohan Verma",
        createdAt: "2025-04-10T10:00:00"
      }
    ],
    followedAuthors: [
      {
        id: 2,
        fullName: "Rohan Verma",
        bio: "Java Developer and Tech Blogger.",
        avatarUrl: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80",
        totalBlogs: 12,
        followersCount: 248,
        followedByCurrentUser: true
      }
    ]
  },
  adminDashboard: {
    totalUsers: 250,
    totalBlogs: 120,
    reportedCount: 18,
    pendingReportsCount: 5,
    recentReports: [
      { id: 1, postId: 3, blogTitle: "Fake News in Tech", reportedBy: "user123", reason: "Misleading", status: "PENDING" },
      { id: 2, postId: 4, blogTitle: "Spam Post Example", reportedBy: "reader45", reason: "Spam", status: "PENDING" }
    ]
  }
};

// Generic Fetch function with JWT Authorization
async function request(endpoint, options = {}) {
  const token = localStorage.getItem(STORAGE_TOKEN_KEY);
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...(options.headers || {})
  };

  try {
    const res = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });
    if (!res.ok) {
      if (res.status === 401) {
        localStorage.removeItem(STORAGE_TOKEN_KEY);
        localStorage.removeItem(STORAGE_USER_KEY);
      }
      const errData = await res.json().catch(() => ({}));
      throw new Error(errData.message || `HTTP error ${res.status}`);
    }
    return await res.json();
  } catch (error) {
    throw error;
  }
}

// Global API Object
const API = {
  // Auth methods
  auth: {
    async login(email, password) {
      try {
        const res = await request('/auth/login', {
          method: 'POST',
          body: JSON.stringify({ email, password })
        });
        if (res.data) {
          localStorage.setItem(STORAGE_TOKEN_KEY, res.data.token);
          localStorage.setItem(STORAGE_USER_KEY, JSON.stringify(res.data));
          return res.data;
        }
      } catch (err) {
        console.warn('Backend offline, using mock login:', err.message);
        let role = "ROLE_READER";
        let fullName = "Reader User";
        let avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=200&q=80";

        if (email.includes('admin')) {
          role = "ROLE_ADMIN";
          fullName = "Admin";
          avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80";
        } else if (email.includes('rohan') || email.includes('author')) {
          role = "ROLE_AUTHOR";
          fullName = "Rohan Verma";
          avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80";
        }

        const mockUser = {
          token: "mock-jwt-token-xyz",
          id: email.includes('admin') ? 1 : (email.includes('rohan') ? 2 : 3),
          fullName,
          email,
          role,
          avatarUrl,
          bio: role === 'ROLE_AUTHOR' ? "Java Developer and Tech Blogger." : "Avid Reader"
        };
        localStorage.setItem(STORAGE_TOKEN_KEY, mockUser.token);
        localStorage.setItem(STORAGE_USER_KEY, JSON.stringify(mockUser));
        return mockUser;
      }
    },

    async register(fullName, email, password, role) {
      try {
        const res = await request('/auth/register', {
          method: 'POST',
          body: JSON.stringify({ fullName, email, password, role })
        });
        if (res.data) {
          localStorage.setItem(STORAGE_TOKEN_KEY, res.data.token);
          localStorage.setItem(STORAGE_USER_KEY, JSON.stringify(res.data));
          return res.data;
        }
      } catch (err) {
        console.warn('Backend offline, using mock register:', err.message);
        const resolvedRole = (role && role.toLowerCase().includes('author')) ? "ROLE_AUTHOR" : "ROLE_READER";
        const mockUser = {
          token: "mock-jwt-token-xyz",
          id: Math.floor(Math.random() * 1000) + 10,
          fullName,
          email,
          role: resolvedRole,
          avatarUrl: resolvedRole === "ROLE_AUTHOR"
            ? "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80"
            : "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=200&q=80",
          bio: resolvedRole === "ROLE_AUTHOR" ? "Author at BlogSphere" : "Reader at BlogSphere"
        };
        localStorage.setItem(STORAGE_TOKEN_KEY, mockUser.token);
        localStorage.setItem(STORAGE_USER_KEY, JSON.stringify(mockUser));
        return mockUser;
      }
    },

    logout() {
      localStorage.removeItem(STORAGE_TOKEN_KEY);
      localStorage.removeItem(STORAGE_USER_KEY);
      window.location.href = 'login.html';
    },

    getCurrentUser() {
      const u = localStorage.getItem(STORAGE_USER_KEY);
      return u ? JSON.parse(u) : null;
    },

    isAuthenticated() {
      return !!localStorage.getItem(STORAGE_TOKEN_KEY);
    }
  },

  // User & Settings methods
  users: {
    async updateProfile(profileData) {
      try {
        const res = await request('/users/profile', {
          method: 'PUT',
          body: JSON.stringify(profileData)
        });
        const currentUser = API.auth.getCurrentUser();
        if (currentUser) {
          currentUser.fullName = profileData.fullName || currentUser.fullName;
          currentUser.bio = profileData.bio !== undefined ? profileData.bio : currentUser.bio;
          currentUser.avatarUrl = profileData.avatarUrl || currentUser.avatarUrl;
          localStorage.setItem(STORAGE_USER_KEY, JSON.stringify(currentUser));
        }
        return res.data;
      } catch (err) {
        console.warn('Backend offline, simulated profile update:', err.message);
        const currentUser = API.auth.getCurrentUser() || {};
        const updated = {
          ...currentUser,
          fullName: profileData.fullName || currentUser.fullName,
          bio: profileData.bio !== undefined ? profileData.bio : currentUser.bio,
          avatarUrl: profileData.avatarUrl || currentUser.avatarUrl
        };
        localStorage.setItem(STORAGE_USER_KEY, JSON.stringify(updated));
        return updated;
      }
    },

    async changePassword(passwordData) {
      try {
        const res = await request('/users/password', {
          method: 'PUT',
          body: JSON.stringify(passwordData)
        });
        return res;
      } catch (err) {
        console.warn('Backend offline, simulated password change');
        return { success: true, message: "Password updated successfully" };
      }
    },

    async getReaderDashboard() {
      try {
        const res = await request('/users/reader-dashboard');
        return res.data;
      } catch (err) {
        return MOCK_DB.readerDashboard;
      }
    },

    async getAllAuthors() {
      try {
        const res = await request('/users/authors');
        return res.data;
      } catch (err) {
        return MOCK_DB.users.filter(u => u.role === 'ROLE_AUTHOR');
      }
    }
  },

  // Post methods
  posts: {
    async getAll(params = {}) {
      try {
        const qs = new URLSearchParams(params).toString();
        const res = await request(`/posts?${qs}`);
        return res.data;
      } catch (err) {
        return { content: MOCK_DB.posts, totalElements: MOCK_DB.posts.length };
      }
    },

    async getFeatured() {
      try {
        const res = await request('/posts/featured');
        return res.data;
      } catch (err) {
        return MOCK_DB.posts;
      }
    },

    async getById(id) {
      try {
        const res = await request(`/posts/${id}`);
        return res.data;
      } catch (err) {
        return MOCK_DB.posts.find(p => p.id == id) || MOCK_DB.posts[0];
      }
    },

    async create(postData) {
      try {
        const res = await request('/posts', {
          method: 'POST',
          body: JSON.stringify(postData)
        });
        return res.data;
      } catch (err) {
        console.warn('Backend offline, simulated save:', err.message);
        return { id: Date.now(), ...postData };
      }
    }
  },

  // Comments
  comments: {
    async getByPost(postId) {
      try {
        const res = await request(`/posts/${postId}/comments`);
        return res.data;
      } catch (err) {
        return MOCK_DB.comments;
      }
    },

    async add(postId, content) {
      try {
        const res = await request(`/posts/${postId}/comments`, {
          method: 'POST',
          body: JSON.stringify({ content })
        });
        return res.data;
      } catch (err) {
        const user = API.auth.getCurrentUser() || { fullName: "Reader", avatarUrl: "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=200&q=80" };
        const newC = {
          id: Date.now(),
          postId,
          userName: user.fullName,
          userAvatar: user.avatarUrl,
          content,
          createdAt: new Date().toISOString()
        };
        MOCK_DB.comments.unshift(newC);
        return newC;
      }
    }
  },

  // Interactions (Like, Bookmark, Follow)
  interactions: {
    async toggleLike(postId) {
      try {
        const res = await request(`/posts/${postId}/like`, { method: 'POST' });
        return res.data;
      } catch (err) {
        return { liked: true, likeCount: 125 };
      }
    },

    async toggleBookmark(postId) {
      try {
        const res = await request(`/posts/${postId}/bookmark`, { method: 'POST' });
        return res.data;
      } catch (err) {
        return { bookmarked: true };
      }
    },

    async toggleFollow(authorId) {
      try {
        const res = await request(`/users/${authorId}/follow`, { method: 'POST' });
        return res.data;
      } catch (err) {
        return { following: true, followersCount: 249 };
      }
    }
  },

  // Categories
  categories: {
    async getAll() {
      try {
        const res = await request('/categories');
        return res.data;
      } catch (err) {
        return MOCK_DB.categories;
      }
    }
  },

  // Dashboards
  dashboard: {
    async getUserDashboard() {
      try {
        const res = await request('/users/dashboard');
        return res.data;
      } catch (err) {
        return MOCK_DB.authorDashboard;
      }
    },

    async getAdminDashboard() {
      try {
        const res = await request('/admin/dashboard');
        return res.data;
      } catch (err) {
        return MOCK_DB.adminDashboard;
      }
    }
  },

  // Admin Methods (Issues 2 & 3)
  admin: {
    async getAllUsers() {
      try {
        const res = await request('/admin/users');
        return res.data;
      } catch (err) {
        return MOCK_DB.users;
      }
    },

    async updateUserRole(userId, newRole) {
      try {
        const res = await request(`/admin/users/${userId}/role`, {
          method: 'PUT',
          body: JSON.stringify({ role: newRole })
        });
        return res.data;
      } catch (err) {
        const u = MOCK_DB.users.find(x => x.id == userId);
        if (u) u.role = newRole;
        return { success: true };
      }
    },

    async deleteUser(userId) {
      try {
        await request(`/admin/users/${userId}`, { method: 'DELETE' });
        return true;
      } catch (err) {
        MOCK_DB.users = MOCK_DB.users.filter(x => x.id != userId);
        return true;
      }
    },

    async getAllComments() {
      try {
        const res = await request('/admin/comments');
        return res.data;
      } catch (err) {
        return MOCK_DB.comments;
      }
    },

    async deleteComment(commentId) {
      try {
        await request(`/admin/comments/${commentId}`, { method: 'DELETE' });
        return true;
      } catch (err) {
        MOCK_DB.comments = MOCK_DB.comments.filter(c => c.id != commentId);
        return true;
      }
    }
  },

  // Notifications
  notifications: {
    async getAll() {
      try {
        const res = await request('/users/notifications');
        return res.data;
      } catch (err) {
        return MOCK_DB.notifications;
      }
    }
  }
};
