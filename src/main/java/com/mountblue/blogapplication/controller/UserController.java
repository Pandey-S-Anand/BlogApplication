package com.mountblue.blogapplication.controller;

import com.mountblue.blogapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    public UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signUp")
    public String registerUser(){
        return "register";
    }
    @GetMapping("/signIn")
    public String showMyLoginPage(){
        return "userLogin";
    }

    @GetMapping("/access-denied")
    public String showAccessDenied(){
        return "forbidden";
    }


    @PostMapping("/saveRegisteredUser")
    public String saveRegisteredUser(@RequestParam("username") String name,
                                     @RequestParam("email") String email,
                                     @RequestParam("password") String password){

        if(userService.findUserByEmail(email)!=null){
            return "register";
        }

        userService.save(name,email,"{noop}"+password,"AUTHOR");
        return "userLogin";
    }

}