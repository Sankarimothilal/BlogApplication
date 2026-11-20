/* BlogSphere - frontend connected to Spring Boot REST API */
const API = window.BLOGSPHERE_API || "http://localhost:8080/api";

// Keep the original home/feed markup so Back to Stories can restore it.
const originalMainHTML = document.getElementById("main")?.innerHTML || "";

const state = {
  user: null,
  blogs: [],
  category: null,
  search: "",
  view: "home",
  dark: localStorage.getItem("bs-theme") === "dark"
};

function esc(s) {
  return String(s ?? "").replace(/[&<>"']/g, c => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
  }[c]));
}

function fmtDate(d) {
  if (!d) return "";
  try {
    return new Date(d).toLocaleDateString();
  } catch {
    return "";
  }
}

function normalizeTags(tags) {
  if (Array.isArray(tags)) return tags;
  if (typeof tags === "string") {
    return tags.split(",").map(t => t.trim()).filter(Boolean);
  }
  return [];
}

const app = window.app = {

  async request(path, options = {}) {
    const config = {
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        ...(options.headers || {})
      },
      ...options
    };

    try {
      const r = await fetch(API + path, config);
      const raw = await r.text();

      let data = null;
      try {
        data = raw ? JSON.parse(raw) : null;
      } catch {
        data = raw;
      }

      if (!r.ok) {
        throw new Error(
          data?.message ||
          data?.error ||
          raw ||
          `Request failed (${r.status})`
        );
      }

      return data;
    } catch (e) {
      throw new Error(
        e.message ||
        "Could not connect to backend. Ensure Spring Boot is running on port 8080."
      );
    }
  },

  toast(msg) {
    const t = document.getElementById("toast");

    if (!t) {
      console.warn(msg);
      return;
    }

    t.textContent = msg;
    t.classList.add("show");

    setTimeout(() => t.classList.remove("show"), 2800);
  },

  home() {
    state.view = "home";
    state.category = null;
    state.search = "";

    const main = document.getElementById("main");

    if (main && originalMainHTML) {
      main.innerHTML = originalMainHTML;
    }

    this.bindFeedControls();

    const title = document.getElementById("feedTitle");

    if (title) {
      title.textContent = "Latest stories";
    }

    this.loadBlogs();
  },

  bindFeedControls() {
    const searchBtn = document.getElementById("searchBtn");
    const searchInput = document.getElementById("searchInput");
    const refreshBtn = document.getElementById("refreshBtn");

    if (searchBtn) {
      searchBtn.onclick = () => this.search();
    }

    if (searchInput) {
      searchInput.onkeydown = e => {
        if (e.key === "Enter") this.search();
      };
    }

    if (refreshBtn) {
      refreshBtn.onclick = () => this.loadBlogs();
    }
  },

  async loadBlogs() {
    let q = "/blogs";

    if (state.category) {
      q += "?category=" + encodeURIComponent(state.category);
    } else if (state.search) {
      q += "?search=" + encodeURIComponent(state.search);
    }

    try {
      const data = await this.request(q);

      state.blogs = Array.isArray(data)
        ? data
        : (data?.content || data?.blogs || []);

      this.renderBlogs();

    } catch (e) {
      state.blogs = [];
      this.renderBlogs();
      this.toast(e.message);
    }
  },

  renderBlogs() {
    const grid = document.getElementById("blogGrid");
    const empty = document.getElementById("emptyState");

    if (!grid) return;

    grid.innerHTML = "";

    state.blogs.forEach(b => {
      const id = b.id ?? b.blogId;
      const title = b.title || "Untitled";
      const content = b.content || b.body || "";
      const excerpt = b.excerpt || content.slice(0, 180);
      const category = b.category || "General";
      const tags = normalizeTags(b.tags);

      const card = document.createElement("article");
      card.className = "blog-card";

      card.innerHTML = `
        <div class="meta">
          <span>${esc(category)}</span>
          <span>${esc(b.authorName || b.author?.name || "Author")}</span>
          <span>${fmtDate(b.createdAt || b.publishedAt)}</span>
        </div>

        <h3 data-open="${esc(id)}">${esc(title)}</h3>

        <p>
          ${esc(excerpt)}
          ${content.length > 180 ? "…" : ""}
        </p>

        <div class="tags">
          ${tags.map(t => `
            <span class="tag">
              ${esc(typeof t === "string" ? t : t?.name || "")}
            </span>
          `).join("")}
        </div>

        <div class="card-actions">
          <button data-like="${esc(id)}">
            ♡ ${b.likesCount ?? b.likes ?? 0}
          </button>

          <button data-bookmark="${esc(id)}">
            ▢ Save
          </button>

          <button data-open="${esc(id)}">
            Read →
          </button>
        </div>
      `;

      grid.appendChild(card);
    });

    if (empty) {
      empty.classList.toggle("hidden", state.blogs.length > 0);
    }

    grid.querySelectorAll("[data-open]").forEach(el => {
      el.onclick = () => this.openBlog(el.dataset.open);
    });

    grid.querySelectorAll("[data-like]").forEach(el => {
      el.onclick = () => this.like(el.dataset.like);
    });

    grid.querySelectorAll("[data-bookmark]").forEach(el => {
      el.onclick = () => this.bookmark(el.dataset.bookmark);
    });
  },

  async openBlog(id) {
    try {
      const b = await this.request(`/blogs/${id}`);

      state.view = "detail";

      const main = document.getElementById("main");

      if (!main) return;

      const tags = normalizeTags(b.tags);

      main.innerHTML = `
        <article class="detail">

          <button class="ghost" id="backFeed">
            ← Back to stories
          </button>

          <div class="meta">
            ${esc(b.category || "General")} ·
            ${esc(b.authorName || b.author?.name || "Author")} ·
            ${fmtDate(b.createdAt)}
          </div>

          <h1>${esc(b.title || "Untitled")}</h1>

          <p>${esc(b.excerpt || "")}</p>

          <div class="tags">
            ${tags.map(t => `
              <span class="tag">
                ${esc(typeof t === "string" ? t : t?.name || "")}
              </span>
            `).join("")}
          </div>

          <div class="card-actions">
            <button id="detailLike">♡ Like</button>
            <button id="detailBookmark">▢ Bookmark</button>
            <button id="shareBtn">↗ Share</button>
          </div>

          <div class="detail-body">
            ${esc(b.content || b.body || "")}
          </div>

          <h2>Comments</h2>

          <div id="comments">
            Loading comments…
          </div>

          <form id="commentForm">
            <textarea
              id="commentText"
              placeholder="Write a comment…"
              required
            ></textarea>

            <button class="primary" type="submit">
              Post comment
            </button>
          </form>

        </article>
      `;

      // Back button
      const back = document.getElementById("backFeed");

      if (back) {
        back.onclick = () => this.home();
      }

      // Like
      const likeBtn = document.getElementById("detailLike");

      if (likeBtn) {
        likeBtn.onclick = () => this.like(id);
      }

      // Bookmark
      const bookmarkBtn = document.getElementById("detailBookmark");

      if (bookmarkBtn) {
        bookmarkBtn.onclick = () => this.bookmark(id);
      }

      // Share
      const shareBtn = document.getElementById("shareBtn");

      if (shareBtn) {
        shareBtn.onclick = async () => {
          try {
            await navigator.clipboard.writeText(location.href);
            this.toast("Link copied");
          } catch {
            this.toast("Unable to copy link in this browser");
          }
        };
      }

      // Comments form
      const commentForm = document.getElementById("commentForm");

      if (commentForm) {
        commentForm.onsubmit = async e => {
          e.preventDefault();

          if (!this.requireLogin()) return;

          const textBox = document.getElementById("commentText");
          const content = textBox?.value.trim();

          if (!content) return;

          const submit = commentForm.querySelector(
            "button[type='submit']"
          );

          if (submit) submit.disabled = true;

          try {
            // FIX: Backend expects "text", not "content".
            await this.request(`/blogs/${id}/comments`, {
              method: "POST",
              body: JSON.stringify({ text: content })
            });

            if (textBox) textBox.value = "";

            this.toast("Comment posted");

            await this.loadComments(id);

          } catch (err) {
            this.toast(err.message);

          } finally {
            if (submit) submit.disabled = false;
          }
        };
      }

      await this.loadComments(id);

    } catch (e) {
      this.toast(e.message);
    }
  },

  async loadComments(id) {
    const box = document.getElementById("comments");

    if (!box) return;

    try {
      const data = await this.request(`/blogs/${id}/comments`);

      const comments = Array.isArray(data)
        ? data
        : (data?.content || data?.comments || []);

      box.innerHTML = comments.map(c => `
        <div class="comment">
          <strong>
            ${esc(
              c.authorName ||
              c.user?.name ||
              c.author?.name ||
              "Reader"
            )}
          </strong>

          <p>${esc(c.content || c.text || "")}</p>

          <small>${fmtDate(c.createdAt)}</small>
        </div>
      `).join("") || "<p>No comments yet.</p>";

    } catch (e) {
      box.innerHTML = `
        <p class="muted">
          Unable to load comments: ${esc(e.message)}
        </p>
      `;
    }
  },

  requireLogin() {
    if (!state.user) {
      this.authModal("login");
      return false;
    }

    return true;
  },

  async like(id) {
    if (!this.requireLogin()) return;

    try {
      await this.request(`/blogs/${id}/like`, {
        method: "POST"
      });

      this.toast("Like updated");
      this.loadBlogs();

    } catch (e) {
      this.toast(e.message);
    }
  },

  async bookmark(id) {
    if (!this.requireLogin()) return;

    try {
      await this.request(`/blogs/${id}/bookmark`, {
        method: "POST"
      });

      this.toast("Bookmark updated");

    } catch (e) {
      this.toast(e.message);
    }
  },

  authModal(mode = "login") {
    const register = mode === "register";

    this.modal(`
      <h2>
        ${register ? "Create your BlogSphere account" : "Login to BlogSphere"}
      </h2>

      <form id="authForm">

        ${register ? `
          <div class="form-row">
            <label>Name</label>
            <input
              id="authName"
              required
              minlength="2"
              placeholder="Your name"
            >
          </div>
        ` : ""}

        <div class="form-row">
          <label>Email</label>
          <input
            id="authEmail"
            type="email"
            required
            placeholder="you@example.com"
          >
        </div>

        <div class="form-row">
          <label>Password</label>
          <input
            id="authPassword"
            type="password"
            required
            minlength="6"
            placeholder="Password"
          >
        </div>

        ${register ? `
          <div class="form-row">
            <label>Account type</label>
            <select id="authRole">
              <option value="READER">Reader</option>
              <option value="AUTHOR">Author</option>
            </select>
          </div>
        ` : ""}

        <div class="modal-actions">
          <button type="button" class="ghost" id="authCancel">
            Cancel
          </button>

          <button type="submit" class="primary">
            ${register ? "Register" : "Login"}
          </button>
        </div>
      </form>

      <p class="muted">
        ${register
          ? `Already have an account? <a href="#" id="switchLogin">Login</a>`
          : `New here? <a href="#" id="switchRegister">Create an account</a>`
        }
      </p>
    `);

    document.getElementById("authCancel").onclick = () => {
      this.closeModal();
    };

    document.getElementById(
      register ? "switchLogin" : "switchRegister"
    ).onclick = e => {
      e.preventDefault();
      this.authModal(register ? "login" : "register");
    };

    document.getElementById("authForm").onsubmit = async e => {
      e.preventDefault();

      const payload = register
        ? {
            name: document.getElementById("authName").value,
            email: document.getElementById("authEmail").value,
            password: document.getElementById("authPassword").value,
            role: document.getElementById("authRole").value
          }
        : {
            email: document.getElementById("authEmail").value,
            password: document.getElementById("authPassword").value
          };

      try {
        await this.request(
          register ? "/auth/register" : "/auth/login",
          {
            method: "POST",
            body: JSON.stringify(payload)
          }
        );

        await this.checkSession();

        if (!state.user) {
          throw new Error(
            "Authentication succeeded, but the backend session was not recognized. Check session cookie/CORS configuration."
          );
        }

        this.closeModal();

        this.toast(
          register
            ? "Account created! Welcome to BlogSphere."
            : "Login successful"
        );

      } catch (err) {
        this.toast(err.message);
      }
    };
  },

  modal(html) {
    const root = document.getElementById("modalRoot");

    if (!root) return;

    root.innerHTML = `
      <div class="modal-backdrop" id="backdrop">
        <div class="modal">${html}</div>
      </div>
    `;

    const backdrop = document.getElementById("backdrop");

    if (backdrop) {
      backdrop.onclick = e => {
        if (e.target.id === "backdrop") {
          this.closeModal();
        }
      };
    }
  },

  closeModal() {
    const root = document.getElementById("modalRoot");

    if (root) root.innerHTML = "";
  },

  async checkSession() {
    try {
      const session = await this.request("/auth/me");

      state.user = session?.authenticated === true
        ? (session.user || null)
        : null;

    } catch {
      state.user = null;
    }

    this.updateHeader();

    return state.user;
  },

  updateHeader() {
    const login = document.getElementById("loginBtn");
    const reg = document.getElementById("registerBtn");
    const menu = document.getElementById("userMenu");

    if (!login || !reg || !menu) return;

    if (state.user) {
      login.classList.add("hidden");
      reg.classList.add("hidden");
      menu.classList.remove("hidden");

      menu.innerHTML = `
        <span class="profile-name">
          Hi, ${esc(state.user.name || state.user.email || "User")}
        </span>

        <button class="ghost" id="logoutBtn">
          Logout
        </button>
      `;

      const logoutBtn = document.getElementById("logoutBtn");

      if (logoutBtn) {
        logoutBtn.onclick = () => this.logout();
      }

    } else {
      login.classList.remove("hidden");
      reg.classList.remove("hidden");
      menu.classList.add("hidden");
    }
  },

  async logout() {
    try {
      await this.request("/auth/logout", {
        method: "POST"
      });

    } finally {
      state.user = null;
      this.updateHeader();
      this.toast("Logged out");
      this.home();
    }
  },

  async write() {
    if (!this.requireLogin()) return;

    this.modal(`
      <h2>Write a story</h2>

      <form id="blogForm">

        <div class="form-row">
          <label>Title</label>
          <input
            id="blogTitle"
            required
            maxlength="180"
            placeholder="A clear, engaging title"
          >
        </div>

        <div class="form-row">
          <label>Category</label>
          <input
            id="blogCategory"
            placeholder="Technology, Education, Travel…"
            required
          >
        </div>

        <div class="form-row">
          <label>Tags (comma-separated)</label>
          <input
            id="blogTags"
            placeholder="java, learning, career"
          >
        </div>

        <div class="form-row">
          <label>Short description</label>
          <input
            id="blogExcerpt"
            maxlength="300"
            placeholder="A short summary"
          >
        </div>

        <div class="form-row">
          <label>Content</label>
          <textarea
            id="blogContent"
            required
            placeholder="Write your story here…"
          ></textarea>
        </div>

        <div class="modal-actions">
          <button type="button" class="ghost" id="draftBtn">
            Save draft
          </button>

          <button class="primary" type="submit">
            Publish
          </button>
        </div>

      </form>
    `);

    const form = document.getElementById("blogForm");

    if (form) {
      form.onsubmit = e => {
        e.preventDefault();
        this.saveBlog("PUBLISHED");
      };
    }

    const draftBtn = document.getElementById("draftBtn");

    if (draftBtn) {
      draftBtn.onclick = () => this.saveBlog("DRAFT");
    }
  },

  async saveBlog(status) {
    const body = {
      title: document.getElementById("blogTitle")?.value || "",
      category: document.getElementById("blogCategory")?.value || "",
      tags: normalizeTags(
        document.getElementById("blogTags")?.value || ""
      ),
      excerpt: document.getElementById("blogExcerpt")?.value || "",
      content: document.getElementById("blogContent")?.value || "",
      status
    };

    try {
      await this.request("/blogs", {
        method: "POST",
        body: JSON.stringify(body)
      });

      this.closeModal();

      this.toast(
        status === "DRAFT"
          ? "Draft saved"
          : "Blog published"
      );

      this.home();

    } catch (e) {
      this.toast(e.message);
    }
  },

  async dashboard() {
    if (!this.requireLogin()) return;

    state.view = "dashboard";

    const main = document.getElementById("main");

    if (!main) return;

    main.innerHTML = `
      <h1>Dashboard</h1>

      <div class="stat-grid">
        <div class="stat">
          <strong id="statPosts">—</strong>
          <span>My posts</span>
        </div>

        <div class="stat">
          <strong id="statDrafts">—</strong>
          <span>Drafts</span>
        </div>

        <div class="stat">
          <strong id="statLikes">—</strong>
          <span>Likes (if available)</span>
        </div>

        <div class="stat">
          <strong>—</strong>
          <span>Analytics</span>
        </div>
      </div>

      <div class="section-heading">
        <h2>Your writing</h2>
        <button class="primary" id="newBlog">Write</button>
      </div>

      <div id="mineGrid" class="blog-grid"></div>
    `;

    document.getElementById("newBlog").onclick = () => this.write();

    try {
      const data = await this.request("/blogs/mine");

      const blogs = Array.isArray(data)
        ? data
        : (data?.content || []);

      document.getElementById("statPosts").textContent = blogs.length;

      document.getElementById("statDrafts").textContent =
        blogs.filter(b => (b.status || "").toUpperCase() === "DRAFT").length;

      const g = document.getElementById("mineGrid");

      g.innerHTML = blogs.map(b => `
        <article class="blog-card">
          <h3>${esc(b.title)}</h3>
          <p>${esc(b.excerpt || b.content || "")}</p>

          <div class="meta">${esc(b.status || "")}</div>

          <div class="card-actions">
            <button data-edit="${b.id}">Edit</button>
            <button data-delete="${b.id}">Delete</button>
          </div>
        </article>
      `).join("");

      g.querySelectorAll("[data-delete]").forEach(btn => {
        btn.onclick = async () => {
          if (!confirm("Delete this blog?")) return;

          try {
            await this.request(
              `/blogs/${btn.dataset.delete}`,
              { method: "DELETE" }
            );

            this.dashboard();

          } catch (e) {
            this.toast(e.message);
          }
        };
      });

      g.querySelectorAll("[data-edit]").forEach(btn => {
        btn.onclick = () => this.toast(
          "Editing can be added once the backend update DTO is confirmed"
        );
      });

    } catch (e) {
      this.toast(e.message);
    }
  },

  async bookmarks() {
    if (!this.requireLogin()) return;

    state.view = "bookmarks";

    document.getElementById("main").innerHTML = `
      <h1>Bookmarks</h1>
      <div id="savedGrid" class="blog-grid"></div>
    `;

    try {
      const data = await this.request("/blogs/bookmarks");

      const items = Array.isArray(data)
        ? data
        : (data?.content || []);

      const g = document.getElementById("savedGrid");

      g.innerHTML = items.map(b => `
        <article class="blog-card">
          <h3>${esc(b.title)}</h3>
          <p>${esc(b.excerpt || "")}</p>

          <button data-open="${b.id}" class="ghost">
            Read story
          </button>
        </article>
      `).join("");

      g.querySelectorAll("[data-open]").forEach(btn => {
        btn.onclick = () => this.openBlog(btn.dataset.open);
      });

    } catch (e) {
      this.toast(e.message);
    }
  },

  async following() {
    if (!this.requireLogin()) return;

    state.view = "following";

    document.getElementById("main").innerHTML = `
      <h1>Following</h1>
      <div id="followingList" class="page-panel">
        Loading…
      </div>
    `;

    try {
      const data = await this.request("/users/following");

      const list = Array.isArray(data)
        ? data
        : (data?.content || []);

      const target = document.getElementById("followingList");

      if (target) {
        target.innerHTML = list.map(u => `
          <div class="profile-row">
            <span>${esc(u.name || u.email)}</span>
          </div>
        `).join("") || "You're not following anyone yet.";
      }

    } catch (e) {
      const target = document.getElementById("followingList");

      if (target) {
        target.textContent = e.message;
      }
    }
  },

  async admin() {
    if (!state.user) {
      this.authModal("login");
      return;
    }

    this.toast(
      "Admin panel requires an ADMIN account and backend admin endpoints."
    );
  },

  async search() {
    const input = document.getElementById("searchInput");

    state.search = input?.value.trim() || "";
    state.category = null;

    const title = document.getElementById("feedTitle");

    if (title) {
      title.textContent = state.search
        ? `Search: ${state.search}`
        : "Latest stories";
    }

    this.loadBlogs();
  },

  theme() {
    state.dark = !state.dark;

    document.body.classList.toggle("dark", state.dark);

    localStorage.setItem(
      "bs-theme",
      state.dark ? "dark" : "light"
    );
  }
};

// Initial navigation and controls

const loginBtn = document.getElementById("loginBtn");

if (loginBtn) {
  loginBtn.onclick = () => app.authModal("login");
}

const registerBtn = document.getElementById("registerBtn");

if (registerBtn) {
  registerBtn.onclick = () => app.authModal("register");
}

const themeBtn = document.getElementById("themeBtn");

if (themeBtn) {
  themeBtn.onclick = () => app.theme();
}

document.querySelectorAll(".nav[data-view]").forEach(btn => {
  btn.onclick = () => {
    document.querySelectorAll(".nav").forEach(b => {
      b.classList.remove("active");
    });

    btn.classList.add("active");

    const actions = {
      home: () => app.home(),
      dashboard: () => app.dashboard(),
      write: () => app.write(),
      bookmarks: () => app.bookmarks(),
      following: () => app.following()
    };

    actions[btn.dataset.view]?.();
  };
});

document.querySelectorAll(".nav[data-category]").forEach(btn => {
  btn.onclick = () => {
    // If the feed was replaced by a detail/dashboard view, restore the original feed first.
    if (state.view !== "home") app.home();

    state.category = btn.dataset.category;
    state.search = "";

    const title = document.getElementById("feedTitle");

    if (title) {
      title.textContent = btn.dataset.category;
    }

    app.loadBlogs();
  };
});

app.bindFeedControls();

if (state.dark) {
  document.body.classList.add("dark");
}

app.checkSession();
app.loadBlogs();