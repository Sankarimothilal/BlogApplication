package com.blogsphere.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
public class Comment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(nullable=false) private Blog blog;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(nullable=false) private User user;
 @Column(nullable=false,length=3000) private String text;
 private Long parentCommentId;
 private LocalDateTime createdAt=LocalDateTime.now();
 public Comment(){}
 public Long getId(){return id;} public Blog getBlog(){return blog;} public void setBlog(Blog v){blog=v;}
 public User getUser(){return user;} public void setUser(User v){user=v;}
 public String getText(){return text;} public void setText(String v){text=v;}
 public Long getParentCommentId(){return parentCommentId;} public void setParentCommentId(Long v){parentCommentId=v;}
 public LocalDateTime getCreatedAt(){return createdAt;}
}
