package com.mountblue.blogapplication.repository;

import com.mountblue.blogapplication.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
        @Query("SELECT p.comments FROM Post p WHERE p.id=:postId")
        List<Comment> findByPostId(int postId);
}