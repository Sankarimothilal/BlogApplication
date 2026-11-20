package com.blogsphere.controller;

import com.blogsphere.dto.response.ApiResponse;
import com.blogsphere.entity.Tag;
import com.blogsphere.service.CategoryTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryTagService categoryTagService;

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok(categoryTagService.getAllCategoriesWithCount()));
    }

    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<List<Tag>>> getTags() {
        return ResponseEntity.ok(ApiResponse.ok(categoryTagService.getAllTags()));
    }
}
