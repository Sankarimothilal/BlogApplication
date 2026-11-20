package com.blogsphere.entity;
import jakarta.persistence.*;
@Entity @Table(uniqueConstraints=@UniqueConstraint(columnNames={"blog_id","user_id"}))
public class BlogLike {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne @JoinColumn(name="blog_id",nullable=false) private Blog blog;
 @ManyToOne @JoinColumn(name="user_id",nullable=false) private User user;
 public BlogLike(){} public BlogLike(Blog b,User u){blog=b;user=u;}
 public Long getId(){return id;} public Blog getBlog(){return blog;} public User getUser(){return user;}
}
