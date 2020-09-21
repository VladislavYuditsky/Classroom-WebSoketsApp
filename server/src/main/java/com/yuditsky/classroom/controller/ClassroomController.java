package com.yuditsky.classroom.controller;

import com.yuditsky.classroom.model.AuthenticationRequest;
import com.yuditsky.classroom.model.User;
import com.yuditsky.classroom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClassroomController {

    private UserService userService;

    @Autowired
    public ClassroomController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) {
        String username = authenticationRequest.getUsername();
        User user = userService.logIn(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("handAction")
    public ResponseEntity<?> changeHandState(@RequestBody User user) {
        user = userService.findByUsername(user.getUsername());

        if (!user.isAuthorized()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            user = userService.changeHandState(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(@RequestBody User user) {
        user = userService.findByUsername(user.getUsername());

        if (!user.isAuthorized()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            userService.logOut(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @SendTo("/topic/users")
    @MessageMapping("updateState")
    public User updateState(@RequestBody User user) {
        return userService.findByUsername(user.getUsername());
    }
}
