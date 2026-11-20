package com.blogsphere.controller;
import com.blogsphere.entity.*;import com.blogsphere.repository.*;import com.blogsphere.service.*;import jakarta.servlet.http.HttpSession;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/blogs/{blogId}/comments")
public class CommentController {
 private final CommentRepository cr;private final BlogService bs;private final UserService us;
 public CommentController(CommentRepository c,BlogService b,UserService u){cr=c;bs=b;us=u;}
 @GetMapping public List<Map<String,Object>> list(@PathVariable Long blogId){bs.find(blogId);return cr.findByBlogIdOrderByCreatedAtAsc(blogId).stream().map(this::dto).toList();}
 @PostMapping public Map<String,Object> add(@PathVariable Long blogId,@RequestBody Map<String,Object> d,HttpSession s){User u=ApiUtil.current(s,us);Blog b=bs.find(blogId);String text=String.valueOf(d.getOrDefault("text","")).trim();if(text.isBlank())throw new ApiException("Comment cannot be empty");Comment c=new Comment();c.setBlog(b);c.setUser(u);c.setText(text);if(d.get("parentCommentId")!=null)c.setParentCommentId(Long.valueOf(String.valueOf(d.get("parentCommentId"))));return dto(cr.save(c));}
 @DeleteMapping("/{commentId}") public Map<String,String> delete(@PathVariable Long blogId,@PathVariable Long commentId,HttpSession s){User u=ApiUtil.current(s,us);Comment c=cr.findById(commentId).orElseThrow();if(!c.getUser().getId().equals(u.getId())&&!ApiUtil.admin(u))throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN);cr.delete(c);return Map.of("message","Deleted");}
 private Map<String,Object> dto(Comment c){return Map.of("id",c.getId(),"text",c.getText(),"parentCommentId",c.getParentCommentId()==null?0:c.getParentCommentId(),"user",Map.of("id",c.getUser().getId(),"name",c.getUser().getName()),"createdAt",c.getCreatedAt());}
}
