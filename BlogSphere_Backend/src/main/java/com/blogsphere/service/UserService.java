package com.blogsphere.service;
import com.blogsphere.entity.*;import com.blogsphere.repository.*;import org.springframework.stereotype.Service;import org.springframework.security.crypto.password.PasswordEncoder;import java.util.*;
@Service public class UserService {
 private final UserRepository users; private final PasswordEncoder encoder;
 public UserService(UserRepository u,PasswordEncoder e){users=u;encoder=e;}
 public User register(String name,String email,String password,Role role){if(users.existsByEmailIgnoreCase(email))throw new ApiException("Email already registered");User u=new User();u.setName(name);u.setEmail(email.toLowerCase());u.setPassword(encoder.encode(password));u.setRole(role==null?Role.READER:role);return users.save(u);}
 public User login(String email,String password){User u=users.findByEmailIgnoreCase(email).orElseThrow(()->new ApiException("Invalid email or password"));if(!encoder.matches(password,u.getPassword()))throw new ApiException("Invalid email or password");return u;}
 public User find(Long id){return users.findById(id).orElseThrow(()->new ApiException("User not found"));}
 public List<User> all(){return users.findAll();}
 public Map<String,Object> publicUser(User u){Map<String,Object> m=new LinkedHashMap<>();m.put("id",u.getId());m.put("name",u.getName());m.put("email",u.getEmail());m.put("bio",u.getBio());m.put("socialLink",u.getSocialLink());m.put("role",u.getRole());m.put("createdAt",u.getCreatedAt());return m;}
 public User update(User u,Map<String,Object> data){if(data.containsKey("name"))u.setName(String.valueOf(data.get("name")));if(data.containsKey("bio"))u.setBio(String.valueOf(data.get("bio")));if(data.containsKey("socialLink"))u.setSocialLink(String.valueOf(data.get("socialLink")));return users.save(u);}
}
