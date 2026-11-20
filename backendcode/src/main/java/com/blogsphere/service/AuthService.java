package com.blogsphere.service;

import com.blogsphere.dto.request.LoginRequest;
import com.blogsphere.dto.request.RegisterRequest;
import com.blogsphere.dto.response.JwtResponse;
import com.blogsphere.entity.Role;
import com.blogsphere.entity.User;
import com.blogsphere.exception.ResourceNotFoundException;
import com.blogsphere.repository.UserRepository;
import com.blogsphere.security.JwtUtils;
import com.blogsphere.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    public JwtResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        Role role = Role.ROLE_READER;
        if (registerRequest.getRole() != null) {
            String roleStr = registerRequest.getRole().trim().toUpperCase();
            if (roleStr.contains("AUTHOR")) {
                role = Role.ROLE_AUTHOR;
            } else if (roleStr.contains("ADMIN")) {
                role = Role.ROLE_ADMIN;
            }
        }

        User user = new User(
            registerRequest.getFullName(),
            registerRequest.getEmail(),
            passwordEncoder.encode(registerRequest.getPassword()),
            role
        );
        user.setAvatarUrl("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80");
        userRepository.save(user);

        return authenticateUser(new LoginRequest(registerRequest.getEmail(), registerRequest.getPassword()));
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new JwtResponse(
            jwt,
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole().name(),
            user.getAvatarUrl()
        );
    }

    public User getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        String email = auth.getName();
        return userRepository.findByEmail(email).orElse(null);
    }
}
