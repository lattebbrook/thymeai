package com.thyme.ai.thymeai.controller;

import com.thyme.ai.thymeai.config.CDNSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class CommonController {

    @GetMapping("/")
    public ResponseEntity<String> getTest(){
        return ResponseEntity.ok("Status = Online"); // better use actuator
    }

    @PostMapping("/")
    public ResponseEntity<String> postTest(){
        return ResponseEntity.ok("Status = OK"); // better use actuator
    }
}
