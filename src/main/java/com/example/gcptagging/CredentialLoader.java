package com.example.gcptagging;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Utility for loading service account credentials from a JSON file.
 */
public class CredentialLoader {
    
    /**
     * Default OAuth scopes for Cloud Resource Manager.
     */
    private static final List<String> DEFAULT_SCOPES = Collections.singletonList(
            "https://www.googleapis.com/auth/cloud-platform"
    );
    
    /**
     * Loads GoogleCredentials from a service account JSON file.
     * 
     * @param jsonKeyFilePath Path to the service account JSON key file
     * @return GoogleCredentials object with cloud platform scope
     * @throws IOException If the file cannot be read or credentials cannot be created
     */
    public static GoogleCredentials loadCredentials(String jsonKeyFilePath) throws IOException {
        try (FileInputStream keyStream = new FileInputStream(jsonKeyFilePath)) {
            // Load credentials using the default GoogleCredentials approach
            GoogleCredentials credentials = GoogleCredentials.fromStream(keyStream);
            
            // Apply the necessary scopes
            credentials = credentials.createScoped(DEFAULT_SCOPES);
            
            System.out.println("Successfully loaded credentials from: " + jsonKeyFilePath);
            return credentials;
        } catch (IOException e) {
            System.err.println("Failed to load credentials from file: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Extracts the project ID from the service account credentials.
     * 
     * @param credentials The GoogleCredentials to extract the project ID from
     * @return The project ID, or null if it cannot be determined
     */
    public static String getProjectIdFromCredentials(GoogleCredentials credentials) {
        if (credentials instanceof ServiceAccountCredentials) {
            return ((ServiceAccountCredentials) credentials).getProjectId();
        }
        return null;
    }
}