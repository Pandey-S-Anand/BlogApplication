package com.mountblue.blogapplication.service;

import com.mountblue.blogapplication.model.Post;
import com.mountblue.blogapplication.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void savePost(Post post) {
        postRepository.save(post);
    }

    public void updatePost(int postId, Post updatedPost){
        Post existingPost=postRepository.findById(postId).orElse(null);
        if(existingPost!=null){
            existingPost.setTitle(updatedPost.getTitle());
            existingPost.setExcerpt(updatedPost.getExcerpt());
            existingPost.setContent(updatedPost.getContent());
            existingPost.setUpdatedAt(LocalDateTime.now());
            postRepository.save(existingPost);
        }
    }
    public Page<Post> getAllPosts(PageRequest pageRequest) {
        return postRepository.findAll(pageRequest);
    }

    public Page<Post> searchPosts(String searchTerm, Pageable pageable) {
        return postRepository.searchPosts(searchTerm, pageable);
    }

    public Set<String> getAllAuthors(){
        return postRepository.findAllAuthors();
    }

    public Post getPostById(int id) {
        return postRepository.findById(id).orElse(null);
    }

    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    public List<Post> filterPosts(Set<String> authors, Set<Integer> tags) {
        if (authors != null && !authors.isEmpty() && tags != null && !tags.isEmpty()) {
            // Both authors and tags are selected
            return postRepository.findByAuthorInAndTagsIdIn(authors, tags);
        } else if (authors != null && !authors.isEmpty()) {
            // Only authors are selected
            return postRepository.findByAuthorIn(authors);
        } else if (tags != null && !tags.isEmpty()) {
            // Only tags are selected
            return postRepository.findByTagsIdIn(tags);
        } else {
            // No filter selected, return all posts
            return postRepository.findAll();
        }
    }
}
