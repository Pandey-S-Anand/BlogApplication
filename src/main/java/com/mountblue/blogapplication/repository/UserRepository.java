package com.mountblue.blogapplication.repository;

import com.mountblue.blogapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer>{
   User findByEmail(String email);
}
