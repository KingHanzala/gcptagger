package com.example.gcptagging;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.resourcemanager.v3.TagBinding;
import com.google.cloud.resourcemanager.v3.TagBindingsClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Main entry point for the GCP Resource Tagging Tool.
 * Provides functionality to create, delete, and list tag bindings for GCP resources.
 */
public class Main {
    
    /**
     * Displays usage information for the application.
     */
    private static void printUsage() {
        System.out.println("GCP Resource Tagging Tool");
        System.out.println("========================");
        System.out.println("Usage:");
        System.out.println("  java -jar gcptagging.jar <command> <args>");
        System.out.println("\nCommands:");
        System.out.println("  create <service-account-file> <resource-name> <tag-value>");
        System.out.println("    - Creates a tag binding for a resource");
        System.out.println("  delete <service-account-file> <tag-binding-name>");
        System.out.println("    - Deletes a tag binding");
        System.out.println("  list-resource <service-account-file> <resource-name>");
        System.out.println("    - Lists all tag bindings for a resource");
        System.out.println("  list-tag <service-account-file> <tag-value>");
        System.out.println("    - Lists all tag bindings for a tag value");
        System.out.println("\nExamples:");
        System.out.println("  Create a tag binding:");
        System.out.println("    java -jar gcptagging.jar create service-account.json //compute.googleapis.com/projects/my-project/zones/us-central1-a/instances/my-vm tagValues/123456789");
        System.out.println("  Delete a tag binding:");
        System.out.println("    java -jar gcptagging.jar delete service-account.json tagBindings/compute.googleapis.com@projects@my-project@zones@us-central1-a@instances@my-vm@tagValues@123456789");
        System.out.println("  List tag bindings for a resource:");
        System.out.println("    java -jar gcptagging.jar list-resource service-account.json //compute.googleapis.com/projects/my-project/zones/us-central1-a/instances/my-vm");
        System.out.println("  List tag bindings for a tag value:");
        System.out.println("    java -jar gcptagging.jar list-tag service-account.json tagValues/123456789");
    }

    /**
     * Main entry point for the application.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        String command = args[0];
        String serviceAccountFile = args[1];

        try {
            executeCommand(command, serviceAccountFile, args);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Executes commands using the GCP API.
     * @param command The command to execute
     * @param serviceAccountFile Path to the service account file
     * @param args All command line arguments
     * @throws IOException If there is an error loading credentials
     * @throws InterruptedException If an operation is interrupted
     * @throws ExecutionException If an operation execution fails
     * @throws TimeoutException If an operation times out
     */
    private static void executeCommand(String command, String serviceAccountFile, String[] args) 
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        // Load credentials from service account file
        GoogleCredentials credentials = CredentialLoader.loadCredentials(serviceAccountFile);
        
        // Create the TagBindingsClient
        try (
            TagBindingsClient tagBindingsClient = TagBindingsAuthorization.createTagBindingsClient(credentials);
            GcpResourceTagger tagger = new GcpResourceTagger(tagBindingsClient)
        ) {
            switch (command) {
                case "create":
                    if (args.length < 4) {
                        System.err.println("Error: Missing arguments for create command");
                        printUsage();
                        System.exit(1);
                    }
                    String resourceName = args[2];
                    String tagValue = args[3];
                    TagBinding binding = tagger.createTagBinding(resourceName, tagValue);
                    System.out.println("Successfully created tag binding: " + binding.getName());
                    break;
                    
                case "delete":
                    if (args.length < 3) {
                        System.err.println("Error: Missing arguments for delete command");
                        printUsage();
                        System.exit(1);
                    }
                    String tagBindingName = args[2];
                    tagger.deleteTagBinding(tagBindingName);
                    System.out.println("Successfully deleted tag binding: " + tagBindingName);
                    break;
                    
                case "list-resource":
                    if (args.length < 3) {
                        System.err.println("Error: Missing arguments for list-resource command");
                        printUsage();
                        System.exit(1);
                    }
                    resourceName = args[2];
                    List<TagBinding> resourceBindings = tagger.listTagBindingsForResource(resourceName);
                    System.out.println("Found " + resourceBindings.size() + " tag bindings for resource: " + resourceName);
                    for (TagBinding b : resourceBindings) {
                        System.out.println("  - " + b.getName() + " (Tag Value: " + b.getTagValue() + ")");
                    }
                    break;
                    
                case "list-tag":
                    if (args.length < 3) {
                        System.err.println("Error: Missing arguments for list-tag command");
                        printUsage();
                        System.exit(1);
                    }
                    tagValue = args[2];
                    List<TagBinding> tagBindings = tagger.listTagBindingsForTagValue(tagValue);
                    System.out.println("Found " + tagBindings.size() + " tag bindings for tag value: " + tagValue);
                    for (TagBinding b : tagBindings) {
                        System.out.println("  - " + b.getName() + " (Resource: " + b.getParent() + ")");
                    }
                    break;
                    
                default:
                    System.err.println("Error: Unknown command: " + command);
                    printUsage();
                    System.exit(1);
            }
        }
    }
}