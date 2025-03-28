package com.example.demo;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleDriveService {
    
    private final Drive driveService;
    
    @Autowired
    public GoogleDriveService(Drive driveService) {
        this.driveService = driveService;
    }
    
    /**
     * Lists files in Google Drive
     * If credential.json is not yet available, falls back to sample data
     */
    public List<DriveFile> listFiles() {
        System.out.println("INFO: Attempting to list files from Google Drive...");
        try {
            if (driveService != null) {
                System.out.println("INFO: Drive service is available, fetching files from Google Drive API");
                try {
                    return listFilesFromDrive();
                } catch (Exception e) {
                    System.err.println("ERROR accessing Google Drive API: " + e.getMessage());
                    if (e.getMessage() != null && e.getMessage().contains("401")) {
                        System.err.println("Authentication error - credential.json may be invalid or expired");
                    }
                    e.printStackTrace();
                    return getSampleFiles();
                }
            } else {
                System.out.println("WARN: Drive service is null, using sample data");
                return getSampleFiles();
            }
        } catch (Exception e) {
            // Fallback to mock data if Drive API is not available yet
            System.err.println("ERROR accessing Google Drive API: " + e.getMessage());
            System.err.println("Make sure credential.json is in src/main/resources directory");
            System.err.println("Falling back to mock data");
            e.printStackTrace();
            return getSampleFiles();
        }
    }
    
    /**
     * Lists files from Google Drive using the API
     */
    private List<DriveFile> listFilesFromDrive() throws IOException {
        List<DriveFile> files = new ArrayList<>();
        
        try {
            // Reduce API calls - just get first page of results
            FileList result = driveService.files().list()
                .setPageSize(100)
                .setFields("files(id, name, mimeType, modifiedTime)")
                .execute();
                
            for (File file : result.getFiles()) {
                files.add(new DriveFile(
                    file.getId(),
                    file.getName(),
                    file.getMimeType(),
                    file.getModifiedTime() != null ? file.getModifiedTime().toString() : ""
                ));
            }
            
            return files;
        } catch (IOException e) {
            System.err.println("IOException during Drive API call: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Returns sample files as a fallback
     */
    private List<DriveFile> getSampleFiles() {
        List<DriveFile> files = new ArrayList<>();
        
        // Add sample files
        files.add(new DriveFile(
            "sample-id-1",
            "Resume.pdf",
            "application/pdf",
            "2023-03-01T10:30:00.000Z"
        ));
        
        files.add(new DriveFile(
            "sample-id-2",
            "Profile_Picture.jpg",
            "image/jpeg",
            "2023-03-02T12:15:00.000Z"
        ));
        
        files.add(new DriveFile(
            "sample-id-3",
            "Project_Plan.docx",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "2023-02-28T15:45:00.000Z"
        ));
        
        files.add(new DriveFile(
            "sample-id-4",
            "Budget.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "2023-02-25T09:00:00.000Z"
        ));
        
        files.add(new DriveFile(
            "sample-id-5",
            "Notes.txt",
            "text/plain",
            "2023-02-20T16:30:00.000Z"
        ));
        
        return files;
    }

    public static class DriveFile {
        private final String id;
        private final String name;
        private final String mimeType;
        private final String modifiedTime;

        public DriveFile(String id, String name, String mimeType, String modifiedTime) {
            this.id = id;
            this.name = name;
            this.mimeType = mimeType;
            this.modifiedTime = modifiedTime;
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getMimeType() { return mimeType; }
        public String getModifiedTime() { return modifiedTime; }
    }
} 