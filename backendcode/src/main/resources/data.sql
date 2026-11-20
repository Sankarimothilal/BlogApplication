-- Seed initial categories if empty
INSERT IGNORE INTO categories (id, name, slug, icon, description) VALUES
(1, 'Technology', 'technology', 'monitor', 'Articles on Software, AI, Cloud, and Web Development'),
(2, 'Education', 'education', 'book-open', 'Tutorials, career guidance, and academic insights'),
(3, 'Business', 'business', 'trending-up', 'Startups, finance, productivity, and leadership'),
(4, 'Lifestyle', 'lifestyle', 'heart', 'Health, wellness, personal growth, and work-life balance'),
(5, 'Travel', 'travel', 'navigation', 'Destinations, travel guides, culture, and nature');

-- Seed tags
INSERT IGNORE INTO tags (id, name, slug) VALUES
(1, 'java', 'java'),
(2, 'springboot', 'springboot'),
(3, 'web', 'web'),
(4, 'travel', 'travel'),
(5, 'himachal', 'himachal'),
(6, 'restapi', 'restapi'),
(7, 'career', 'career');
