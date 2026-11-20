package com.blogsphere.config;

import com.blogsphere.entity.*;
import com.blogsphere.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Already initialized
        }

        // 1. Seed Categories
        Category tech = categoryRepository.save(new Category("Technology", "technology", "monitor", "Articles on Software, AI, Cloud, and Web Development"));
        Category edu = categoryRepository.save(new Category("Education", "education", "book-open", "Tutorials, career guidance, and academic insights"));
        Category biz = categoryRepository.save(new Category("Business", "business", "trending-up", "Startups, finance, productivity, and leadership"));
        Category life = categoryRepository.save(new Category("Lifestyle", "lifestyle", "heart", "Health, wellness, personal growth, and work-life balance"));
        Category travel = categoryRepository.save(new Category("Travel", "travel", "navigation", "Destinations, travel guides, culture, and nature"));

        // 2. Seed Tags
        Tag tagJava = tagRepository.save(new Tag("java", "java"));
        Tag tagSpring = tagRepository.save(new Tag("springboot", "springboot"));
        Tag tagWeb = tagRepository.save(new Tag("web", "web"));
        Tag tagHimachal = tagRepository.save(new Tag("himachal", "himachal"));
        Tag tagTravel = tagRepository.save(new Tag("travel", "travel"));

        // 3. Seed Users
        User admin = new User("Admin", "admin@blogsphere.com", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN);
        admin.setAvatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80");
        admin.setBio("Platform Administrator");
        userRepository.save(admin);

        User rohan = new User("Rohan Verma", "rohan@blogsphere.com", passwordEncoder.encode("password123"), Role.ROLE_AUTHOR);
        rohan.setAvatarUrl("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80");
        rohan.setBio("Java Developer and Tech Blogger. I write about Java, Spring Boot and Web Development.");
        userRepository.save(rohan);

        User ankit = new User("Ankit Sharma", "ankit@blogsphere.com", passwordEncoder.encode("password123"), Role.ROLE_READER);
        ankit.setAvatarUrl("https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=200&q=80");
        userRepository.save(ankit);

        User priya = new User("Priya Desai", "priya@blogsphere.com", passwordEncoder.encode("password123"), Role.ROLE_READER);
        priya.setAvatarUrl("https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80");
        userRepository.save(priya);

        User rahul = new User("Rahul Mehta", "rahul@blogsphere.com", passwordEncoder.encode("password123"), Role.ROLE_READER);
        rahul.setAvatarUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&q=80");
        userRepository.save(rahul);

        // 4. Seed Posts (From Mockup Image)
        Post postHimachal = new Post();
        postHimachal.setAuthor(rohan);
        postHimachal.setCategory(travel);
        postHimachal.setTitle("Exploring the Hills of Himachal");
        postHimachal.setSlug("exploring-the-hills-of-himachal");
        postHimachal.setSummary("A wonderful journey through the hills, nature and culture of Himachal.");
        postHimachal.setContentHtml("<p>A wonderful journey through the hills, nature and culture of Himachal Pradesh. From the serene valleys of Kullu to the adventurous treks of Spiti, experience pure mountain magic.</p>");
        postHimachal.setCoverImage("https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80");
        postHimachal.setStatus("PUBLISHED");
        postHimachal.setViewCount(54L);
        postHimachal.setTags(new HashSet<>(Arrays.asList(tagTravel, tagHimachal)));
        postRepository.save(postHimachal);

        Post postSpringBoot = new Post();
        postSpringBoot.setAuthor(rohan);
        postSpringBoot.setCategory(tech);
        postSpringBoot.setTitle("Understanding Spring Boot");
        postSpringBoot.setSlug("understanding-spring-boot");
        postSpringBoot.setSummary("Spring Boot is a powerful framework for building robust and scalable Java applications.");
        postSpringBoot.setContentHtml("<p>Spring Boot is a powerful framework for building robust and scalable Java applications. It makes development faster and easier with its auto-configuration and production-ready features.</p><p>With Spring Boot, you get embedded Tomcat servers, opinionated starter dependencies, and metrics out of the box.</p>");
        postSpringBoot.setCoverImage("https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=800&q=80");
        postSpringBoot.setStatus("PUBLISHED");
        postSpringBoot.setViewCount(1200L);
        postSpringBoot.setTags(new HashSet<>(Arrays.asList(tagJava, tagSpring, tagWeb)));
        postRepository.save(postSpringBoot);

        Post postApi = new Post();
        postApi.setAuthor(rohan);
        postApi.setCategory(tech);
        postApi.setTitle("Introduction to REST API");
        postApi.setSlug("introduction-to-rest-api");
        postApi.setSummary("Learn how RESTful web services work and how to design clean endpoints.");
        postApi.setContentHtml("<p>Representational State Transfer (REST) is an architectural style for providing standards between computer systems on the web.</p>");
        postApi.setCoverImage("https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=800&q=80");
        postApi.setStatus("PUBLISHED");
        postApi.setViewCount(350L);
        postApi.setTags(new HashSet<>(Collections.singletonList(tagWeb)));
        postRepository.save(postApi);

        Post draftPost = new Post();
        draftPost.setAuthor(rohan);
        draftPost.setCategory(tech);
        draftPost.setTitle("Deep Dive into Spring Security 6");
        draftPost.setSlug("deep-dive-into-spring-security-6");
        draftPost.setSummary("Draft article on JWT authentication filter chains.");
        draftPost.setContentHtml("<p>In this post, we explore how SecurityFilterChain replaced WebSecurityConfigurerAdapter...</p>");
        draftPost.setStatus("DRAFT");
        draftPost.setViewCount(0L);
        postRepository.save(draftPost);

        // 5. Seed Comments (From Mockup Image)
        commentRepository.save(new Comment(postSpringBoot, ankit, null, "Nice explanation! Very helpful for beginners."));
        commentRepository.save(new Comment(postSpringBoot, priya, null, "Can you also write about Spring Security?"));

        // 6. Seed Likes & Follows
        likeRepository.save(new Like(ankit, postSpringBoot));
        likeRepository.save(new Like(priya, postSpringBoot));
        likeRepository.save(new Like(rahul, postSpringBoot));
        likeRepository.save(new Like(ankit, postHimachal));

        followRepository.save(new Follow(rahul, rohan));
        followRepository.save(new Follow(ankit, rohan));

        // 7. Seed Notifications (From Mockup Image)
        notificationRepository.save(new Notification(rohan, ankit, "LIKE", "Ankit Sharma liked your blog \"Understanding Spring Boot\".", "/blog-detail.html?id=" + postSpringBoot.getId()));
        notificationRepository.save(new Notification(rohan, priya, "COMMENT", "Priya Desai commented on your blog \"Introduction to REST API\".", "/blog-detail.html?id=" + postApi.getId()));
        notificationRepository.save(new Notification(rohan, rahul, "FOLLOW", "Rahul Mehta started following you.", "/author-profile.html?id=" + rahul.getId()));
        notificationRepository.save(new Notification(ankit, rohan, "NEW_POST", "A new blog \"Java for Beginners\" is published by Rohan Verma.", "/blog-detail.html?id=" + postSpringBoot.getId()));

        // 8. Seed Reports (From Mockup Image)
        reportRepository.save(new Report(postApi, ankit, "Misleading"));
        reportRepository.save(new Report(postHimachal, rahul, "Spam"));
    }
}
