package com.blogsphere.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
public class Blog {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=250) private String title;
 @Column(nullable=false,columnDefinition="LONGTEXT") private String content;
 @Column(length=500) private String excerpt;
 private String category;
 @Column(length=2000) private String tags;
 @Column(length=1000) private String coverImage;
 @Enumerated(EnumType.STRING) private BlogStatus status=BlogStatus.DRAFT;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="author_id",nullable=false) private User author;
 private long views=0;
 private LocalDateTime createdAt=LocalDateTime.now();
 private LocalDateTime updatedAt=LocalDateTime.now();
 public Blog(){}
 public Long getId(){return id;} public String getTitle(){return title;} public void setTitle(String v){title=v;}
 public String getContent(){return content;} public void setContent(String v){content=v;}
 public String getExcerpt(){return excerpt;} public void setExcerpt(String v){excerpt=v;}
 public String getCategory(){return category;} public void setCategory(String v){category=v;}
 public String getTags(){return tags;} public void setTags(String v){tags=v;}
 public String getCoverImage(){return coverImage;} public void setCoverImage(String v){coverImage=v;}
 public BlogStatus getStatus(){return status;} public void setStatus(BlogStatus v){status=v;}
 public User getAuthor(){return author;} public void setAuthor(User v){author=v;}
 public long getViews(){return views;} public void setViews(long v){views=v;}
 public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
 @PreUpdate public void touch(){updatedAt=LocalDateTime.now();}
}
