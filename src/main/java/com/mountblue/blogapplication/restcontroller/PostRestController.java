package com.mountblue.blogapplication.restcontroller;

import com.mountblue.blogapplication.model.Comment;
import com.mountblue.blogapplication.model.Post;
import com.mountblue.blogapplication.model.Tag;
import com.mountblue.blogapplication.model.User;
import com.mountblue.blogapplication.service.CommentService;
import com.mountblue.blogapplication.service.PostService;
import com.mountblue.blogapplication.service.TagService;
import com.mountblue.blogapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {
    private final PostService postService;
    private final CommentService commentService;
    private final TagService tagService;
    private final UserService userService;

    @Autowired
    public PostRestController(PostService postService, CommentService commentService, TagService tagService, UserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.tagService = tagService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(@RequestParam(defaultValue = "0") Integer pageNo,
                                                  @RequestParam(defaultValue = "createdAt") String sortField,
                                                  @RequestParam(defaultValue = "asc") String sortOrder,
                                                  @RequestParam(required = false) String searchTerm) {
        int pageSize = 6;
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortField);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<Post> postPage;

        if(searchTerm==null || searchTerm.isEmpty()){
            postPage = postService.getAllPosts(pageRequest);
        } else{
            postPage = postService.searchPosts(searchTerm, pageRequest);
        }

        List<Post> posts = postPage.getContent();

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable int postId) {
        Post post = postService.getPostById(postId);

        if (post == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PostMapping("/newPost")
    public ResponseEntity<String> createPost(@RequestBody Post post, @RequestParam("tag") String userTag) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());
        if(user.getRole().equals("AUTHOR")){
            post.setAuthor(user.getName());
        }
        user.getPosts().add(post);
        userService.saveUser(user);

        post.setCreatedAt(LocalDateTime.now());
        String[] tags = userTag.split(",");
        Set<Tag> uniqueTags = new HashSet<>();

        for (String tag : tags) {
            tag = tag.trim();
            Tag existingTag = tagService.getTagByName(tag);
            if (existingTag != null) {
                uniqueTags.add(existingTag);
            } else {
                Tag newTag = new Tag();
                newTag.setName(tag);
                tagService.saveTag(newTag);
                uniqueTags.add(newTag);
            }
        }

        post.setTags(uniqueTags);
        postService.savePost(post);

        return new ResponseEntity<>("Post created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/readMore/{postId}")
    public ResponseEntity<Map<String, Object>> readMore(@PathVariable int postId) {
        Map<String, Object> response = new HashMap<>();
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        Post post = postService.getPostById(postId);
        Set<Tag> tags = post.getTags();

        response.put("commentsList", comments);
        response.put("post", post);
        response.put("tagsSet", tags);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/updatePost/{postId}")
    public ResponseEntity<String> updatePost(@PathVariable int postId, @RequestBody Post updatedPost) {
        postService.updatePost(postId, updatedPost);
        return new ResponseEntity<>("Post updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/deletePost/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable int postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());
        Post post = postService.getPostById(postId);
        if (user.getRole().equals("AUTHOR") && (user.getPosts().contains(post)==false) ) {
            return new ResponseEntity<>("You don't have permission to delete this post", HttpStatus.FORBIDDEN);
        }

        user.getPosts().remove(post);
        userService.saveUser(user);
        postService.deletePost(post);
        return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
    }

    @PostMapping("/filteredPosts")
    public ResponseEntity<List<Post>> filterPosts(@RequestParam(required = false ) Set<String> authors,
                                                  @RequestParam(required = false) Set<Integer> tags) {
        List<Post> filteredPosts = postService.filterPosts(authors, tags);
        return new ResponseEntity<>(filteredPosts, HttpStatus.OK);
    }
}
