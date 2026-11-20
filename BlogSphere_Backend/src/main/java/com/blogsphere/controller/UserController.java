package com.blogsphere.controller;
import com.blogsphere.entity.*;import com.blogsphere.repository.*;import com.blogsphere.service.*;import jakarta.servlet.http.HttpSession;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/users")
public class UserController {
 private final UserService us;private final FollowRepository fr;private final BlogRepository br;
 public UserController(UserService u,FollowRepository f,BlogRepository b){us=u;fr=f;br=b;}
 @GetMapping public List<Map<String,Object>> list(){return us.all().stream().map(us::publicUser).toList();}
 @GetMapping("/{id}") public Map<String,Object> profile(@PathVariable Long id){User u=us.find(id);Map<String,Object> m=us.publicUser(u);m.put("followers",fr.countByAuthorId(id));m.put("blogs",br.findByAuthorIdOrderByCreatedAtDesc(id).stream().filter(b->b.getStatus()==BlogStatus.PUBLISHED).count());return m;}
 @PutMapping("/me") public Map<String,Object> update(@RequestBody Map<String,Object> d,HttpSession s){User u=ApiUtil.current(s,us);return us.publicUser(us.update(u,d));}
 @PostMapping("/{id}/follow") public Map<String,Object> follow(@PathVariable Long id,HttpSession s){User me=ApiUtil.current(s,us),author=us.find(id);if(me.getId().equals(author.getId()))throw new ApiException("You cannot follow yourself");Optional<Follow> old=fr.findByFollowerIdAndAuthorId(me.getId(),id);if(old.isPresent())fr.delete(old.get());else fr.save(new Follow(me,author));return Map.of("following",old.isEmpty(),"followers",fr.countByAuthorId(id));}
 @GetMapping("/following") public List<Map<String,Object>> following(HttpSession s){User me=ApiUtil.current(s,us);return fr.findByFollowerId(me.getId()).stream().map(f->us.publicUser(f.getAuthor())).toList();}
}
