package com.example.demo;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Configuration
public class GoogleDriveConfig {
    private static final String APPLICATION_NAME = "OOPsy-daisy";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credential.json";

    /**
     * Creates a Google Drive service.
     */
    @Bean
    public Drive driveService() throws IOException, GeneralSecurityException {
        System.out.println("INFO: Initializing Google Drive service...");
        try {
            System.out.println("INFO: Creating HTTP transport...");
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            System.out.println("INFO: Getting credentials...");
            Credential credential = getCredentials(HTTP_TRANSPORT);
            System.out.println("INFO: Successfully obtained credentials");
            System.out.println("INFO: Building Drive service...");
            Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            System.out.println("INFO: Google Drive service successfully initialized");
            return service;
        } catch (Exception e) {
            // Log the error but return null to allow the application to start
            System.err.println("ERROR initializing Google Drive service: " + e.getMessage());
            System.err.println("Using mock implementation instead");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates an authorized Credential object.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        try {
            // Load client secrets
            InputStream in = GoogleDriveConfig.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
            if (in == null) {
                System.err.println("ERROR: credential.json file not found in resources directory");
                throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH + 
                    "\nPlease ensure credential.json is in src/main/resources directory");
            }
            System.out.println("INFO: Successfully loaded credential.json");
            
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            System.out.println("INFO: Successfully parsed client secrets");

            // Build flow and trigger user authorization request
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            System.out.println("INFO: Built authorization flow, tokens directory: " + TOKENS_DIRECTORY_PATH);
            
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            System.out.println("INFO: Starting local server receiver on port 8888");
            
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } catch (Exception e) {
            System.err.println("ERROR during Google Drive authentication: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
} 