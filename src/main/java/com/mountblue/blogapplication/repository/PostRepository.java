package com.mountblue.blogapplication.repository;

import com.mountblue.blogapplication.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.tags t " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ")
    Page<Post> searchPosts(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT DISTINCT p.author FROM Post p")
    Set<String> findAllAuthors();

    List<Post> findByAuthorInAndTagsIdIn(Set<String> authorNames, Set<Integer> tagIds);

    List<Post> findByAuthorIn(Set<String> authors);

    List<Post> findByTagsIdIn(Set<Integer> tags);
}