package com.example.gcptagging;

/**
 * Demonstration class for tagging GCP resources with tag bindings.
 * This class simulates the operations without actually calling GCP APIs.
 */
public class GcpResourceTaggerDemo {
    
    private final String credentialsFile;
    
    /**
     * Creates a new GcpResourceTaggerDemo with the specified credentials file.
     *
     * @param credentialsFile Path to the service account credentials JSON file
     */
    public GcpResourceTaggerDemo(String credentialsFile) {
        this.credentialsFile = credentialsFile;
        System.out.println("Initialized GCP Resource Tagger with credentials from: " + credentialsFile);
    }
    
    /**
     * Creates a tag binding for the specified resource and tag value.
     *
     * @param resourceName The full resource name (e.g., //compute.googleapis.com/projects/my-project/zones/us-central1-a/instances/my-vm)
     * @param tagValue The full tag value name (e.g., tagValues/123456789)
     */
    public void createTagBinding(String resourceName, String tagValue) {
        System.out.println("DEMO: Creating tag binding");
        System.out.println("  Resource: " + resourceName);
        System.out.println("  Tag Value: " + tagValue);
        System.out.println("Created tag binding successfully (simulated)");
    }
    
    /**
     * Deletes a tag binding with the specified name.
     *
     * @param tagBindingName The name of the tag binding to delete
     */
    public void deleteTagBinding(String tagBindingName) {
        System.out.println("DEMO: Deleting tag binding: " + tagBindingName);
        System.out.println("Deleted tag binding successfully (simulated)");
    }
    
    /**
     * Lists all tag bindings for the specified resource.
     *
     * @param resourceName The resource name to list tag bindings for
     */
    public void listTagBindingsForResource(String resourceName) {
        System.out.println("DEMO: Listing tag bindings for resource: " + resourceName);
        System.out.println("Tag bindings for resource " + resourceName + ":");
        System.out.println("  - tagBindings/example-binding-1 (Tag Value: tagValues/123456789)");
        System.out.println("  - tagBindings/example-binding-2 (Tag Value: tagValues/987654321)");
    }
    
    /**
     * Lists all tag bindings for the specified tag value.
     *
     * @param tagValueName The tag value name to list tag bindings for
     */
    public void listTagBindingsForTagValue(String tagValueName) {
        System.out.println("DEMO: Listing tag bindings for tag value: " + tagValueName);
        System.out.println("Tag bindings for tag value " + tagValueName + ":");
        System.out.println("  - tagBindings/example-binding-1 (Resource: //compute.googleapis.com/projects/my-project/zones/us-central1-a/instances/my-vm)");
        System.out.println("  - tagBindings/example-binding-2 (Resource: //compute.googleapis.com/projects/my-project/zones/us-central1-a/instances/my-vm2)");
    }
} 