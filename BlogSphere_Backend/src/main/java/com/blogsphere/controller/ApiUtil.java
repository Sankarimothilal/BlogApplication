package com.blogsphere.controller;
import jakarta.servlet.http.HttpSession;import com.blogsphere.entity.User;import com.blogsphere.service.*;import org.springframework.http.HttpStatus;import org.springframework.web.server.ResponseStatusException;
public final class ApiUtil {private ApiUtil(){} public static User current(HttpSession s,UserService us){Object id=s.getAttribute("userId");if(id==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Login required");return us.find((Long)id);}public static boolean admin(User u){return u.getRole()==com.blogsphere.entity.Role.ADMIN;}}
