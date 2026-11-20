package com.blogsphere.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="users")
public class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String name;
 @Column(nullable=false,unique=true) private String email;
 @Column(nullable=false) private String password;
 @Column(length=1000) private String bio;
 private String socialLink;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role=Role.READER;
 private LocalDateTime createdAt=LocalDateTime.now();
 public User(){}
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;}
 public String getPassword(){return password;} public void setPassword(String v){password=v;}
 public String getBio(){return bio;} public void setBio(String v){bio=v;}
 public String getSocialLink(){return socialLink;} public void setSocialLink(String v){socialLink=v;}
 public Role getRole(){return role;} public void setRole(Role v){role=v;}
 public LocalDateTime getCreatedAt(){return createdAt;}
}
