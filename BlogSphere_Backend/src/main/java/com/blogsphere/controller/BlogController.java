
package com.blogsphere.controller;

import com.blogsphere.entity.*;
import com.blogsphere.repository.*;
import com.blogsphere.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService bs;
    private final UserService us;
    private final BlogRepository repo;
    private final BlogLikeRepository likes;
    private final BookmarkRepository bookmarks;
    private final FollowRepository follows;

    public BlogController(
            BlogService b,
            UserService u,
            BlogRepository r,
            BlogLikeRepository l,
            BookmarkRepository bm,
            FollowRepository f) {

        bs = b;
        us = u;
        repo = r;
        likes = l;
        bookmarks = bm;
        follows = f;
    }

    @GetMapping
    public List<Map<String, Object>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        List<Blog> rows;

        if (category != null && !category.isBlank()) {
            rows = repo.findByStatusAndCategoryIgnoreCaseOrderByCreatedAtDesc(
                    BlogStatus.PUBLISHED,
                    category
            );
        } else {
            rows = bs.published();
        }

        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();

            rows = rows.stream()
                    .filter(b -> (
                            b.getTitle() + " " +
                            b.getContent() + " " +
                            b.getCategory() + " " +
                            b.getTags() + " " +
                            b.getAuthor().getName()
                    ).toLowerCase().contains(q))
                    .toList();
        }

        return rows.stream()
                .map(this::dto)
                .toList();
    }

    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        Blog b = bs.find(id);
        b.setViews(b.getViews() + 1);
        repo.save(b);
        return dto(b);
    }

    @GetMapping("/mine")
    public List<Map<String, Object>> mine(HttpSession s) {
        User u = ApiUtil.current(s, us);
        return bs.byAuthor(u.getId())
                .stream()
                .map(this::dto)
                .toList();
    }

    @PostMapping
    public Map<String, Object> create(
            @RequestBody Map<String, Object> data,
            HttpSession s) {

        User u = ApiUtil.current(s, us);
        return dto(bs.save(data, u, null));
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data,
            HttpSession s) {

        User u = ApiUtil.current(s, us);
        Blog b = bs.find(id);

        if (!b.getAuthor().getId().equals(u.getId())
                && !ApiUtil.admin(u)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN);
        }

        return dto(bs.save(data, u, b));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(
            @PathVariable Long id,
            HttpSession s) {

        User u = ApiUtil.current(s, us);
        Blog b = bs.find(id);

        if (!b.getAuthor().getId().equals(u.getId())
                && !ApiUtil.admin(u)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN);
        }

        bs.delete(id);
        return Map.of("message", "Deleted");
    }

    @PostMapping("/{id}/like")
    public Map<String, Object> like(
            @PathVariable Long id,
            HttpSession s) {

        User u = ApiUtil.current(s, us);
        Blog b = bs.find(id);

        Optional<BlogLike> old =
                likes.findByBlogIdAndUserId(id, u.getId());

        if (old.isPresent()) {
            likes.delete(old.get());
        } else {
            likes.save(new BlogLike(b, u));
        }

        return Map.of(
                "liked", old.isEmpty(),
                "likes", likes.countByBlogId(id)
        );
    }

    @PostMapping("/{id}/bookmark")
    public Map<String, Object> bookmark(
            @PathVariable Long id,
            HttpSession s) {

        User u = ApiUtil.current(s, us);
        Blog b = bs.find(id);

        Optional<Bookmark> old =
                bookmarks.findByBlogIdAndUserId(id, u.getId());

        if (old.isPresent()) {
            bookmarks.delete(old.get());
        } else {
            bookmarks.save(new Bookmark(b, u));
        }

        return Map.of("bookmarked", old.isEmpty());
    }

    @GetMapping("/bookmarks")
    public List<Map<String, Object>> saved(HttpSession s) {
        User u = ApiUtil.current(s, us);

        return bookmarks.findByUserId(u.getId())
                .stream()
                .map(x -> dto(x.getBlog()))
                .toList();
    }

    private Map<String, Object> dto(Blog b) {
        Map<String, Object> m = new LinkedHashMap<>();

        m.put("id", b.getId());
        m.put("title", b.getTitle());
        m.put("content", b.getContent());
        m.put("excerpt", b.getExcerpt());
        m.put("category", b.getCategory());
        m.put("tags", b.getTags());
        m.put("coverImage", b.getCoverImage());
        m.put("status", b.getStatus());

        m.put("author", Map.of(
                "id", b.getAuthor().getId(),
                "name", b.getAuthor().getName()
        ));

        m.put("authorName", b.getAuthor().getName());
        m.put("views", b.getViews());
        m.put("likes", likes.countByBlogId(b.getId()));
        m.put("createdAt", b.getCreatedAt());
        m.put("updatedAt", b.getUpdatedAt());

        return m;
    }
}
