package com.mountblue.blogapplication.repository;

import com.mountblue.blogapplication.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag,Integer> {
    Optional<Tag> findByName(String tagName);
}
