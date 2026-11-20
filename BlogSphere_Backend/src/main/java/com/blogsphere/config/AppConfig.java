package com.blogsphere.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
public class AppConfig implements WebMvcConfigurer {
 @Override public void addCorsMappings(CorsRegistry registry) {
   registry.addMapping("/api/**").allowedOrigins("http://127.0.0.1:5500","http://localhost:5500","http://127.0.0.1:5501","http://localhost:5501")
     .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS").allowedHeaders("*").allowCredentials(true);
 }
 @Bean public PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
}
