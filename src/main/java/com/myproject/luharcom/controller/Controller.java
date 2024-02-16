package com.myproject.luharcom.controller;

import com.myproject.luharcom.models.LoginDto;
import com.myproject.luharcom.models.User;
import com.myproject.luharcom.service.UserService;
import com.myproject.luharcom.utils.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Controller {

    @Autowired
    UserService userService;

    @Autowired
    JwtHelper jwtHelper;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/login")
    public ResponseEntity<LoginDto> getUserToken(@RequestBody User user){
        LoginDto loginDto = jwtHelper.issueToken(user);
        return ResponseEntity.ok(loginDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> addUser(@RequestBody User user){
        try {
            user.setPassword(encoder.encode(user.getPassword()));
            userService.saveUser(user);
            return ResponseEntity.ok(user);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.status(500).body("Something went wrong.");
    }

    @GetMapping("/getusers")
    public ResponseEntity<Object> getAllUsers(){
        try {
            List<User> userList = userService.getAllUsers();
            return ResponseEntity.ok(userList);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.status(500).body("Something went wrong.");
    }

}
