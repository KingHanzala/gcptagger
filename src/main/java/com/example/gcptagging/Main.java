package com.example.gcptagging;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Main entry point for the GCP Resource Tagging Tool.
 * Provides functionality to create, delete, and list tag bindings for GCP resources.
 */
public class Main {
    
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * Displays usage information for the application.
     */
    private static void printUsage() {
        System.out.println("GCP Resource Tagging Tool");
        System.out.println("========================");
        System.out.println("Usage:");
        System.out.println("  java -jar gcptagging.jar <command> <args>");
        System.out.println("\nCommands:");
        System.out.println("  create <service-account-file> <resource-name> <tag-value> [location]");
        System.out.println("    - Creates a tag binding for a resource with optional location parameter");
        System.out.println("  delete <service-account-file> <tag-binding-name> [location]");
        System.out.println("    - Deletes a tag binding with optional location parameter");
        System.out.println("  list-resource <service-account-file> <resource-name> [location]");
        System.out.println("    - Lists all tag bindings for a resource with optional location parameter");
        System.out.println("  list-tag <service-account-file> <tag-value> [location]");
        System.out.println("    - Lists all tag bindings for a tag value with optional location parameter");
        System.out.println("\nExamples:");
        System.out.println("  Create a tag binding for a Compute Engine instance (regional resource):");
        System.out.println("    java -jar gcptagging.jar create service-account.json //compute.googleapis.com/compute/v1/projects/my-project/zones/us-central1-a/instances/my-vm tagValues/123456789 us-central1");
        System.out.println("  Create a tag binding for a project (global resource):");
        System.out.println("    java -jar gcptagging.jar create service-account.json //cloudresourcemanager.googleapis.com/projects/my-project tagValues/123456789");
        System.out.println("  Delete a tag binding:");
        System.out.println("    java -jar gcptagging.jar delete service-account.json tagBindings/compute.googleapis.com@projects@my-project@zones@us-central1-a@instances@my-vm@tagValues@123456789 us-central1");
        System.out.println("  List tag bindings for a resource:");
        System.out.println("    java -jar gcptagging.jar list-resource service-account.json //compute.googleapis.com/compute/v1/projects/my-project/zones/us-central1-a/instances/my-vm us-central1");
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
     * Executes commands using the REST API.
     * @param command The command to execute
     * @param serviceAccountFile Path to the service account file
     * @param args All command line arguments
     * @throws IOException If there is an error loading credentials
     * @throws InterruptedException If an operation is interrupted
     */
    private static void executeCommand(String command, String serviceAccountFile, String[] args) 
            throws IOException, InterruptedException {
        // Load credentials from service account file
        GoogleCredentials credentials = CredentialLoader.loadCredentials(serviceAccountFile);
        
        // Create the tagger with credentials
        try (GcpResourceTagger tagger = new GcpResourceTagger(credentials)) {
            switch (command) {
                case "create":
                    if (args.length < 4) {
                        System.err.println("Error: Missing arguments for create command");
                        printUsage();
                        System.exit(1);
                    }
                    String resourceName = args[2];
                    String tagValue = args[3];
                    // Optional location parameter
                    String location = (args.length > 4) ? args[4] : null;
                    
                    JsonObject binding = tagger.createTagBinding(resourceName, tagValue, location);
                    System.out.println("Successfully created tag binding:");
                    System.out.println(gson.toJson(binding));
                    break;
                    
                case "delete":
                    if (args.length < 3) {
                        System.err.println("Error: Missing arguments for delete command");
                        printUsage();
                        System.exit(1);
                    }
                    String tagBindingName = args[2];
                    // Optional location parameter
                    location = (args.length > 3) ? args[3] : null;
                    
                    tagger.deleteTagBinding(tagBindingName, location);
                    System.out.println("Successfully deleted tag binding: " + tagBindingName);
                    break;
                    
                case "list-resource":
                    if (args.length < 3) {
                        System.err.println("Error: Missing arguments for list-resource command");
                        printUsage();
                        System.exit(1);
                    }
                    resourceName = args[2];
                    // Optional location parameter
                    location = (args.length > 3) ? args[3] : null;
                    
                    List<JsonObject> resourceBindings = tagger.listTagBindingsForResource(resourceName, location);
                    System.out.println("Found " + resourceBindings.size() + " tag bindings for resource: " + resourceName);
                    for (JsonObject bindingObj : resourceBindings) {
                        System.out.println(gson.toJson(bindingObj));
                    }
                    break;
                    
                case "list-tag":
                    if (args.length < 3) {
                        System.err.println("Error: Missing arguments for list-tag command");
                        printUsage();
                        System.exit(1);
                    }
                    tagValue = args[2];
                    // Optional location parameter
                    location = (args.length > 3) ? args[3] : null;
                    
                    List<JsonObject> tagBindings = tagger.listTagBindingsForTagValue(tagValue, location);
                    System.out.println("Found " + tagBindings.size() + " tag bindings for tag value: " + tagValue);
                    for (JsonObject bindingObj : tagBindings) {
                        System.out.println(gson.toJson(bindingObj));
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