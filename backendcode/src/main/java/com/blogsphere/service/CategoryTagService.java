package com.blogsphere.service;

import com.blogsphere.entity.Category;
import com.blogsphere.entity.Tag;
import com.blogsphere.repository.CategoryRepository;
import com.blogsphere.repository.PostRepository;
import com.blogsphere.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryTagService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    public List<Map<String, Object>> getAllCategoriesWithCount() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(cat -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cat.getId());
            map.put("name", cat.getName());
            map.put("slug", cat.getSlug());
            map.put("icon", cat.getIcon());
            map.put("description", cat.getDescription());
            map.put("postCount", postRepository.findByStatusAndCategory("PUBLISHED", cat, null).getTotalElements());
            return map;
        }).collect(Collectors.toList());
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
}
