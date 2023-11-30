package com.mountblue.blogapplication.restcontroller;

import com.mountblue.blogapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @RequestParam("username") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password) {

        if (userService.findUserByEmail(email) != null) {
            return new ResponseEntity<>("User with this email already exists", HttpStatus.BAD_REQUEST);
        }

        userService.save(name, email, "{noop}" + password, "AUTHOR");
        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }

    @GetMapping("/login")
    public ResponseEntity<String> showLoginPage() {
        return new ResponseEntity<>("userLogin", HttpStatus.OK);
    }

    @GetMapping("/access-denied")
    public ResponseEntity<String> showAccessDenied() {
        return new ResponseEntity<>("forbidden", HttpStatus.OK);
    }

    @GetMapping("/signUp")
    public ResponseEntity<String> signUp() {
        return new ResponseEntity<>("register", HttpStatus.OK);
    }
}
