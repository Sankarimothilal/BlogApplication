/**
 * BlogSphere Common Client Scripts
 * Handles Dark/Light theme switching, navigation bar states, and shared UI helpers.
 */

// Initialize Theme
(function initTheme() {
  const savedTheme = localStorage.getItem('blogsphere_theme') || 'light';
  document.documentElement.setAttribute('data-theme', savedTheme);
})();

function toggleTheme() {
  const current = document.documentElement.getAttribute('data-theme') || 'light';
  const target = current === 'light' ? 'dark' : 'light';
  document.documentElement.setAttribute('data-theme', target);
  localStorage.setItem('blogsphere_theme', target);
  updateThemeIcon();
}

function updateThemeIcon() {
  const current = document.documentElement.getAttribute('data-theme');
  const btn = document.getElementById('theme-toggle-btn');
  if (btn) {
    btn.innerHTML = current === 'dark' 
      ? `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="4"/><path d="M12 2v2"/><path d="M12 20v2"/><path d="m4.93 4.93 1.41 1.41"/><path d="m17.66 17.66 1.41 1.41"/><path d="M2 12h2"/><path d="M20 12h2"/><path d="m6.34 17.66-1.41 1.41"/><path d="m19.07 4.93-1.41 1.41"/></svg>`
      : `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"/></svg>`;
  }
}

// Navigation Bar Renderer / State updater
function renderNavbar(activePage = '') {
  const user = API.auth.getCurrentUser();
  const navContainer = document.getElementById('main-navbar');
  if (!navContainer) return;

  const authButtons = user ? `
    <div style="display:flex; align-items:center; gap:12px;">
      <a href="notifications.html" class="theme-toggle-btn" title="Notifications" style="position:relative;">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/><path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/></svg>
      </a>
      <a href="${user.role === 'ROLE_ADMIN' ? 'admin-dashboard.html' : 'dashboard.html'}" class="user-profile-pill">
        <img src="${user.avatarUrl || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80'}" class="user-avatar" alt="User">
        <span style="font-size:0.88rem; font-weight:600; color:var(--text-main);">${user.fullName.split(' ')[0]}</span>
      </a>
      <button onclick="API.auth.logout()" class="btn btn-outline btn-sm" style="color:var(--text-muted);">Logout</button>
    </div>
  ` : `
    <a href="login.html" class="btn btn-outline">Login</a>
    <a href="register.html" class="btn btn-primary">Register</a>
  `;

  navContainer.innerHTML = `
    <div class="nav-container">
      <a href="index.html" class="brand-logo">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1-2.5-2.5Z"/><path d="M6 6h10"/><path d="M6 10h10"/></svg>
        BlogSphere
      </a>

      <ul class="nav-links">
        <li><a href="index.html" class="${activePage === 'home' ? 'active' : ''}">Home</a></li>
        <li><a href="categories.html" class="${activePage === 'categories' ? 'active' : ''}">Categories</a></li>
        <li><a href="authors.html" class="${activePage === 'authors' ? 'active' : ''}">Authors</a></li>
      </ul>

      <div class="nav-search">
        <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
        <input type="text" id="global-search-input" placeholder="Search blogs..." onkeypress="handleSearch(event)">
      </div>

      <div class="nav-actions">
        <button id="theme-toggle-btn" class="theme-toggle-btn" onclick="toggleTheme()" title="Toggle Light/Dark Theme">
        </button>
        ${authButtons}
      </div>
    </div>
  `;

  updateThemeIcon();
}

function handleSearch(e) {
  if (e.key === 'Enter') {
    const query = e.target.value.trim();
    if (query) {
      window.location.href = `index.html?search=${encodeURIComponent(query)}`;
    }
  }
}

// Auth Guard for protected pages
function requireAuth(allowedRoles = []) {
  const user = API.auth.getCurrentUser();
  if (!user) {
    window.location.href = 'login.html';
    return false;
  }
  if (allowedRoles.length > 0 && !allowedRoles.includes(user.role)) {
    alert("You do not have permission to view this page.");
    window.location.href = 'index.html';
    return false;
  }
  return true;
}

// Relative time helper
function formatTimeAgo(dateString) {
  if (!dateString) return 'recently';
  const date = new Date(dateString);
  const now = new Date();
  const seconds = Math.floor((now - date) / 1000);
  if (seconds < 60) return 'Just now';
  const minutes = Math.floor(seconds / 60);
  if (minutes < 60) return `${minutes}m ago`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours}h ago`;
  const days = Math.floor(hours / 24);
  if (days < 30) return `${days}d ago`;
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}
