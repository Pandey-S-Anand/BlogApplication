package com.mountblue.blogapplication.restcontroller;

import com.mountblue.blogapplication.model.Comment;
import com.mountblue.blogapplication.model.Post;
import com.mountblue.blogapplication.model.User;
import com.mountblue.blogapplication.service.CommentService;
import com.mountblue.blogapplication.service.PostService;
import com.mountblue.blogapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {
    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public CommentRestController(CommentService commentService, PostService postService, UserService userService) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addComment(@RequestParam("postId") int postId,
                                             @RequestParam("name") String name,
                                             @RequestParam("email") String email,
                                             @RequestParam("comment") String comment) {
        Comment userComment = new Comment();
        Post post = postService.getPostById(postId);
        userComment.setName(name);
        userComment.setEmail(email);
        userComment.setComment(comment);
        userComment.setCreatedAt(LocalDateTime.now());
        post.getComments().add(userComment);
        postService.savePost(post);
        commentService.addComment(userComment);
        return new ResponseEntity<>("Comment added successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable int commentId, @RequestParam("postId") int postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());
        Post post = postService.getPostById(postId);
        if (user.getRole().equals("AUTHOR") && (!user.getPosts().contains(post))) {
            return new ResponseEntity<>("You don't have permission to delete this comment", HttpStatus.FORBIDDEN);
        }
        commentService.deleteComment(commentId);
        return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/edit/{commentId}")
    public ResponseEntity<Comment> getCommentToEdit(@PathVariable int commentId) {
        Comment comment = commentService.getCommentById(commentId);
        if (comment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PutMapping("/update/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable int commentId,
                                                @RequestParam("postId") int postId,
                                                @RequestBody Comment updatedComment) {
        commentService.updateComment(commentId, updatedComment);
        return new ResponseEntity<>("Comment updated successfully", HttpStatus.OK);
    }
}
