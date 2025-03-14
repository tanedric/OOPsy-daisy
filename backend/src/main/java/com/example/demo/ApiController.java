package com.example.demo;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")  // Allow Vite frontend
// Enables CORS to allow frontend (Vite at localhost:5173) to call backend APIs
// Without this, browser blocks requests due to cross-origin restrictions
// Best practice: Use a global CORS config instead of adding @CrossOrigin in every controller

public class ApiController {

    @GetMapping("/message")
    public String getMessage() {
        return "Hello from Spring Boot!";
    }
}
