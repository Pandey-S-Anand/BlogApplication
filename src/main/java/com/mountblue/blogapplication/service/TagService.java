package com.mountblue.blogapplication.service;

import com.mountblue.blogapplication.model.Tag;
import com.mountblue.blogapplication.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag getTagByName(String tagName) {
        return tagRepository.findByName(tagName).orElse(null);
    }

    public void saveTag(Tag newTag) {
        tagRepository.save(newTag);
    }

    public List<Tag> getAllTags(){
        return tagRepository.findAll();
    }
}