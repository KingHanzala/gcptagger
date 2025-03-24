package com.example.gcptagging;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.resourcemanager.v3.TagBinding;
import com.google.cloud.resourcemanager.v3.TagBindingsClient;
import com.google.cloud.resourcemanager.v3.CreateTagBindingRequest;
import com.google.cloud.resourcemanager.v3.DeleteTagBindingRequest;
import com.google.cloud.resourcemanager.v3.ListTagBindingsRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Implementation of GCP resource tagging operations.
 */
public class GcpResourceTagger implements AutoCloseable {
    
    private final TagBindingsClient tagBindingsClient;
    private static final int OPERATION_TIMEOUT_SECONDS = 60;
    
    /**
     * Constructor initializes the TagBindingsClient.
     *
     * @param tagBindingsClient Initialized TagBindingsClient
     */
    public GcpResourceTagger(TagBindingsClient tagBindingsClient) {
        this.tagBindingsClient = tagBindingsClient;
    }
    
    /**
     * Creates a new tag binding between a resource and a tag value.
     *
     * @param resourceName The full resource name (e.g., //compute.googleapis.com/projects/my-project/zones/us-central1-a/instances/my-vm)
     * @param tagValueName The full tag value name (e.g., tagValues/123456789)
     * @return The created TagBinding
     * @throws ApiException If the API call fails
     * @throws InterruptedException If the operation is interrupted
     * @throws ExecutionException If the operation execution fails
     * @throws TimeoutException If the operation times out
     */
    public TagBinding createTagBinding(String resourceName, String tagValueName) 
            throws ApiException, InterruptedException, ExecutionException, TimeoutException {
        
        System.out.println("Creating tag binding for resource: " + resourceName + " with tag value: " + tagValueName);
        
        // Build the TagBinding object
        TagBinding tagBinding = TagBinding.newBuilder()
                .setParent(resourceName)
                .setTagValue(tagValueName)
                .build();
        
        // Create the request
        CreateTagBindingRequest request = CreateTagBindingRequest.newBuilder()
                .setTagBinding(tagBinding)
                .build();
        
        // Submit the request and wait for completion
        return tagBindingsClient.createTagBindingAsync(request)
                .get(OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
    
    /**
     * Deletes an existing tag binding.
     *
     * @param tagBindingName The full name of the tag binding to delete
     * @throws ApiException If the API call fails
     * @throws InterruptedException If the operation is interrupted
     * @throws ExecutionException If the operation execution fails
     * @throws TimeoutException If the operation times out
     */
    public void deleteTagBinding(String tagBindingName) 
            throws ApiException, InterruptedException, ExecutionException, TimeoutException {
        
        System.out.println("Deleting tag binding: " + tagBindingName);
        
        // Create the request
        DeleteTagBindingRequest request = DeleteTagBindingRequest.newBuilder()
                .setName(tagBindingName)
                .build();
        
        // Submit the request and wait for completion
        tagBindingsClient.deleteTagBindingAsync(request)
                .get(OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
    
    /**
     * Lists all tag bindings associated with a specific resource.
     *
     * @param resourceName The full resource name
     * @return List of TagBinding objects associated with the resource
     * @throws ApiException If the API call fails
     */
    public List<TagBinding> listTagBindingsForResource(String resourceName) throws ApiException {
        System.out.println("Listing tag bindings for resource: " + resourceName);
        
        // Create the request
        ListTagBindingsRequest request = ListTagBindingsRequest.newBuilder()
                .setParent(resourceName)
                .build();
        
        // Collect all results
        List<TagBinding> tagBindings = new ArrayList<>();
        tagBindingsClient.listTagBindings(request).iterateAll().forEach(tagBindings::add);
        
        return tagBindings;
    }
    
    /**
     * Lists all tag bindings associated with a specific tag value.
     *
     * @param tagValueName The full tag value name
     * @return List of TagBinding objects associated with the tag value
     * @throws ApiException If the API call fails
     */
    public List<TagBinding> listTagBindingsForTagValue(String tagValueName) throws ApiException {
        System.out.println("Listing tag bindings for tag value: " + tagValueName);
        
        // Create the request with tagValue field
        ListTagBindingsRequest request = ListTagBindingsRequest.newBuilder()
                .setParent(tagValueName)
                .build();
        
        // Collect all results
        List<TagBinding> tagBindings = new ArrayList<>();
        tagBindingsClient.listTagBindings(request).iterateAll().forEach(tagBindings::add);
        
        return tagBindings;
    }
    
    /**
     * Closes the TagBindingsClient.
     */
    @Override
    public void close() {
        if (tagBindingsClient != null) {
            tagBindingsClient.close();
        }
    }
}