package com.blogsphere.service;
import com.blogsphere.entity.*;import com.blogsphere.repository.*;import org.springframework.stereotype.Service;import java.util.*;
@Service public class BlogService {
 private final BlogRepository blogs;private final UserService userService;
 public BlogService(BlogRepository b,UserService u){blogs=b;userService=u;}
 public List<Blog> published(){return blogs.findByStatusOrderByCreatedAtDesc(BlogStatus.PUBLISHED);}
 public List<Blog> all(){return blogs.findAll();}
 public Blog find(Long id){return blogs.findById(id).orElseThrow(()->new ApiException("Blog not found"));}
 public List<Blog> byAuthor(Long id){return blogs.findByAuthorIdOrderByCreatedAtDesc(id);}
 public Blog save(Map<String,Object> data,User author,Blog existing){Blog b=existing==null?new Blog():existing;
 String title=str(data,"title"),content=str(data,"content");if(title.isBlank()||content.isBlank())throw new ApiException("Title and content are required");
 b.setTitle(title);b.setContent(content);b.setExcerpt(str(data,"excerpt"));b.setCategory(str(data,"category"));b.setTags(str(data,"tags"));b.setCoverImage(str(data,"coverImage"));
 String status=str(data,"status").toUpperCase();b.setStatus(status.equals("PUBLISHED")?BlogStatus.PUBLISHED:BlogStatus.DRAFT);
 if(existing==null)b.setAuthor(author);return blogs.save(b);}
 private String str(Map<String,Object> m,String k){Object v=m.get(k);return v==null?"":String.valueOf(v);}
 public Blog delete(Long id){Blog b=find(id);blogs.delete(b);return b;}
}
