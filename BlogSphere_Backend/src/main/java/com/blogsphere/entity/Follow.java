package com.blogsphere.entity;
import jakarta.persistence.*;
@Entity @Table(uniqueConstraints=@UniqueConstraint(columnNames={"follower_id","author_id"}))
public class Follow {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne @JoinColumn(name="follower_id",nullable=false) private User follower;
 @ManyToOne @JoinColumn(name="author_id",nullable=false) private User author;
 public Follow(){} public Follow(User f,User a){follower=f;author=a;}
 public Long getId(){return id;} public User getFollower(){return follower;} public User getAuthor(){return author;}
}
