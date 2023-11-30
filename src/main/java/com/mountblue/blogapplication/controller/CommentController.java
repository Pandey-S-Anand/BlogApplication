package com.mountblue.blogapplication.controller;

import com.mountblue.blogapplication.model.Comment;
import com.mountblue.blogapplication.model.Post;
import com.mountblue.blogapplication.model.User;
import com.mountblue.blogapplication.service.CommentService;
import com.mountblue.blogapplication.service.PostService;
import com.mountblue.blogapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;

@Controller
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public CommentController(CommentService commentService, PostService postService, UserService userService) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/addComment")
    public String addComment(@RequestParam("postId") int postId,@RequestParam("name") String name,@RequestParam("email") String email,@RequestParam("comment") String comment) {
        Comment userComment = new Comment();
        Post post=postService.getPostById(postId);
        userComment.setName(name);
        userComment.setEmail(email);
        userComment.setComment(comment);
        userComment.setCreatedAt(LocalDateTime.now());
        post.getComments().add(userComment);
        postService.savePost(post);
        commentService.addComment(userComment);
        return "redirect:/readMore?id=" + postId;
    }

    @PostMapping("/deleteComment")
    public String deleteComment(@RequestParam("commentId") int commentId,@RequestParam("postId") int postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());
        Post post = postService.getPostById(postId);
        if (user.getRole().equals("AUTHOR") && (user.getPosts().contains(post)==false) ) {
            return "forbidden";
        }
        commentService.deleteComment(commentId);
        return "redirect:/readMore?id="+postId;
    }
    @PostMapping("/editComment")
    public String editComment(@RequestParam("commentId") int commentId, @RequestParam("postId") int postId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());
        Post post = postService.getPostById(postId);
        if (user.getRole().equals("AUTHOR") && (user.getPosts().contains(post)==false) ) {
            return "forbidden";
        }
        Comment comment = commentService.getCommentById(commentId);
        model.addAttribute("postId",postId);
        model.addAttribute("updateComment", comment);
        return "editComment";
    }

    @PostMapping("/updateComment")
    public String updateComment(@RequestParam("commentId") int commentId,@RequestParam("postId") int postId,@ModelAttribute("updateComment") Comment updatedComment) {
        commentService.updateComment(commentId, updatedComment);
        return "redirect:/readMore?id=" + postId;
    }
}
