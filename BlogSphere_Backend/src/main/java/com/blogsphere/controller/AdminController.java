package com.blogsphere.controller;
import com.blogsphere.entity.*;import com.blogsphere.service.*;import jakarta.servlet.http.HttpSession;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/admin")
public class AdminController {
 private final UserService us;private final BlogService bs;public AdminController(UserService u,BlogService b){us=u;bs=b;}
 private User admin(HttpSession s){User u=ApiUtil.current(s,us);if(!ApiUtil.admin(u))throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN,"Admin role required");return u;}
 @GetMapping("/users") public List<Map<String,Object>> users(HttpSession s){admin(s);return us.all().stream().map(us::publicUser).toList();}
 @PutMapping("/users/{id}/role") public Map<String,Object> role(@PathVariable Long id,@RequestBody Map<String,String> d,HttpSession s){admin(s);User u=us.find(id);u.setRole(Role.valueOf(d.get("role").toUpperCase()));return us.publicUser(us.update(u,Map.of()));}
 @GetMapping("/blogs") public List<Map<String,Object>> blogs(HttpSession s){admin(s);return bs.all().stream().map(b->Map.<String,Object>of("id",b.getId(),"title",b.getTitle(),"status",b.getStatus(),"author",b.getAuthor().getName())).toList();}
 @DeleteMapping("/blogs/{id}") public Map<String,String> deleteBlog(@PathVariable Long id,HttpSession s){admin(s);bs.delete(id);return Map.of("message","Removed by admin");}
}
