package com.mountblue.blogapplication.service;

import com.mountblue.blogapplication.model.Comment;
import com.mountblue.blogapplication.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    public void addComment(Comment comment) {
        commentRepository.save(comment);
    }
    public void updateComment(int commentId,Comment updatedComment){
        Comment existingComment=commentRepository.findById(commentId).orElse(null);
        if(existingComment !=null){
            existingComment.setComment(updatedComment.getComment());
            existingComment.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(existingComment);
        }
    }
    public List<Comment> getCommentsByPostId(int postId){
        return commentRepository.findByPostId(postId);
    }

    public void deleteComment(int commentId) {
        Comment commentToDelete = commentRepository.findById(commentId).orElse(null);
        commentRepository.delete(commentToDelete);
    }

    public Comment getCommentById(int id) {
        return commentRepository.findById(id).orElse(null);
    }
}