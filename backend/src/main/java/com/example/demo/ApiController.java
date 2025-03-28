package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})  // Allow multiple frontend origins
// Enables CORS to allow frontend (Vite at localhost:5173) to call backend APIs
// Without this, browser blocks requests due to cross-origin restrictions
// Best practice: Use a global CORS config instead of adding @CrossOrigin in every controller

public class ApiController {

    @Autowired
    private GoogleDriveService googleDriveService;

    @GetMapping("/message")
    public String getMessage() {
        return "Hello from Spring Boot!";
    }

    @GetMapping("/drive/files")
    public List<GoogleDriveService.DriveFile> getDriveFiles() {
        return googleDriveService.listFiles();
    }

    @GetMapping("/drive/status")
    public Map<String, Object> getDriveStatus() {
        Map<String, Object> status = new HashMap<>();
        try {
            // Try to get the first file to test connection
            List<GoogleDriveService.DriveFile> files = googleDriveService.listFiles();
            boolean isUsingRealApi = files.size() > 0 && !files.get(0).getId().startsWith("sample-id");
            
            status.put("connected", isUsingRealApi);
            status.put("message", isUsingRealApi ? 
                "Connected to Google Drive API" : 
                "Using sample data - add credential.json to resources directory to connect to real Google Drive");
        } catch (Exception e) {
            status.put("connected", false);
            status.put("message", "Error: " + e.getMessage());
        }
        return status;
    }
}
