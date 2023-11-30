package com.mountblue.blogapplication.controller;

import com.mountblue.blogapplication.model.Comment;
import com.mountblue.blogapplication.model.Post;
import com.mountblue.blogapplication.model.Tag;
import com.mountblue.blogapplication.model.User;
import com.mountblue.blogapplication.repository.TagRepository;
import com.mountblue.blogapplication.repository.UserRepository;
import com.mountblue.blogapplication.service.CommentService;
import com.mountblue.blogapplication.service.PostService;
import com.mountblue.blogapplication.service.TagService;
import com.mountblue.blogapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class PostController {
     private final PostService postService;
     private final CommentService commentService;
     private final TagService tagService;
     private final UserService userService;

     @Autowired
     public PostController(PostService postService, CommentService commentService, TagService tagService, TagRepository tagRepository, UserRepository userRepository, UserService userService) {
          this.postService = postService;
          this.commentService = commentService;
          this.tagService = tagService;
          this.userService = userService;
     }

     @GetMapping("/")
     public String home(@RequestParam(defaultValue = "0") Integer pageNo,
                        @RequestParam(defaultValue = "createdAt") String sortField,
                        @RequestParam(defaultValue = "asc") String sortOrder ,
                        @RequestParam(required = false) String searchTerm,
                        Model model) {
          int pageSize = 6;
          Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortField);
          PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);
          Page<Post> postPage;

          if(searchTerm==null || searchTerm.isEmpty()){
               postPage = postService.getAllPosts(pageRequest);
          } else{
               postPage = postService.searchPosts(searchTerm, pageRequest);
          }

          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          User user=userService.findUserByEmail(authentication.getName());

          model.addAttribute("user",user);
          model.addAttribute("authors", postService.getAllAuthors());
          model.addAttribute("tags", tagService.getAllTags());
          model.addAttribute("posts", postPage.getContent());
          model.addAttribute("currentPage", pageNo);
          model.addAttribute("totalPages", postPage.getTotalPages());
          model.addAttribute("sortField", sortField);
          model.addAttribute("sortOrder", sortOrder);


          return "homePage";
     }

     @PostMapping("/newPost")
     public String createPostForm(Model model) {
          Post post=new Post();
          model.addAttribute("post",post);
          return "createPost";
     }

     @PostMapping("/savePost")
     public String addPost(@ModelAttribute("Post") Post post,@RequestParam("tag") String userTag) {
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
          return "redirect:/";
     }

     @GetMapping("/readMore")
     public String readMore(@RequestParam("id") int postId, Model model) {
          List<Comment> comments = commentService.getCommentsByPostId(postId);
          model.addAttribute("commentsList", comments);
          Post post = postService.getPostById(postId);
          model.addAttribute("post", post);
          Set<Tag> tags = post.getTags();
          model.addAttribute("tagsSet", tags);
          return "readMorePage";
     }

     @PostMapping("/editPost")
     public String editPostPage(@RequestParam("postId") int postId, Model model) {
          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          User user = userService.findUserByEmail(authentication.getName());
          Post post = postService.getPostById(postId);
          if (user.getRole().equals("AUTHOR") && (user.getPosts().contains(post)==false) ) {
               return "forbidden";
          }
          model.addAttribute("post", post);
          return "editPost";
     }

     @PostMapping("/updatePost")
     public String updatePost(@RequestParam("postId") int postId, @ModelAttribute("Post") Post updatedPost) {
          postService.updatePost(postId, updatedPost);
          return "redirect:/readMore?id=" + postId;
     }

     @PostMapping("/deletePost")
     public String deletePost(@RequestParam("postId") int postId) {
          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          User user = userService.findUserByEmail(authentication.getName());
          Post post = postService.getPostById(postId);
          if (user.getRole().equals("AUTHOR") && (user.getPosts().contains(post)==false) ) {
               return "forbidden";
          }

          user.getPosts().remove(post);
          userService.saveUser(user);
          postService.deletePost(post);
          return "redirect:/";
     }

     @PostMapping("/filteredPosts")
     public String filterPosts(@RequestParam(required = false ) Set<String> authors,
                               @RequestParam(required = false) Set<Integer> tags,
                               Model model) {
          List<Post> filteredPosts = postService.filterPosts(authors, tags);
          model.addAttribute("postsList", filteredPosts);
          return "filteredPosts";
     }
}