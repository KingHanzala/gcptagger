package com.example.gcptagging;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.resourcemanager.v3.TagBindingsClient;
import com.google.cloud.resourcemanager.v3.TagBindingsSettings;

import java.io.IOException;

/**
 * Utility class for handling authorization with the Cloud Resource Manager API.
 */
public class TagBindingsAuthorization {

    /**
     * Creates a TagBindingsClient using the provided credentials.
     *
     * @param credentials GoogleCredentials object
     * @return TagBindingsClient instance
     * @throws IOException If the client cannot be created
     */
    public static TagBindingsClient createTagBindingsClient(GoogleCredentials credentials) throws IOException {
        if (credentials == null) {
            throw new IllegalArgumentException("Credentials cannot be null");
        }

        try {
            // Configure client settings with the provided credentials
            TagBindingsSettings settings = TagBindingsSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build();

            // Create and return the client
            return TagBindingsClient.create(settings);
        } catch (IOException e) {
            System.err.println("Failed to create TagBindingsClient: " + e.getMessage());
            throw e;
        }
    }
}