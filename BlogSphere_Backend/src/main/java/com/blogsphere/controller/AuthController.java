package com.blogsphere.controller;
import com.blogsphere.entity.*;import com.blogsphere.service.*;import jakarta.servlet.http.HttpSession;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final UserService service;public AuthController(UserService s){service=s;}
 @PostMapping("/register") public Map<String,Object> register(@RequestBody Map<String,Object> d,HttpSession session){String name=String.valueOf(d.getOrDefault("name","")).trim(),email=String.valueOf(d.getOrDefault("email","")).trim(),password=String.valueOf(d.getOrDefault("password",""));if(name.isBlank()||email.isBlank()||password.length()<6)throw new ApiException("Name, email and password (minimum 6 characters) are required");Role role=Role.READER;User u=service.register(name,email,password,role);session.setAttribute("userId",u.getId());return Map.of("user",service.publicUser(u),"message","Registered");}
 @PostMapping("/login") public Map<String,Object> login(@RequestBody Map<String,String> d,HttpSession s){User u=service.login(d.getOrDefault("email",""),d.getOrDefault("password",""));s.setAttribute("userId",u.getId());return Map.of("user",service.publicUser(u),"message","Logged in");}
 @PostMapping("/logout") public Map<String,Object> logout(HttpSession s){s.invalidate();return Map.of("message","Logged out");}
 @GetMapping("/me") public Map<String,Object> me(HttpSession s){Object id=s.getAttribute("userId");if(id==null)return Map.of("authenticated",false);User u=service.find((Long)id);return Map.of("authenticated",true,"user",service.publicUser(u));}
 @PostMapping("/forgot-password") public Map<String,Object> forgot(@RequestBody Map<String,String> d){return Map.of("message","Password recovery can be connected to an email provider. No email was sent in this starter.");}
}
