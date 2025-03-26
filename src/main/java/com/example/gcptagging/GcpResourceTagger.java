package com.example.gcptagging;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of GCP resource tagging operations using direct REST API calls.
 */
public class GcpResourceTagger implements AutoCloseable {
    
    private final HttpClient httpClient;
    private static final String TAG_BINDINGS_API_URL = "https://cloudresourcemanager.googleapis.com/v3/";
    private static final int OPERATION_TIMEOUT_SECONDS = 60;
    
    /**
     * Constructor initializes with credentials.
     *
     * @param credentials GoogleCredentials object for authorization
     */
    public GcpResourceTagger(GoogleCredentials credentials) {
        this.httpClient = new HttpClient(credentials);
    }
    
    /**
     * Creates a tag binding between a resource and a tag value.
     *
     * @param resourceName The full resource name
     * @param tagValue The tag value to bind to the resource
     * @param location Optional location parameter (e.g., "us-central1-a" for a zone)
     * @return The created tag binding as a JsonObject
     * @throws IOException If an I/O error occurs
     * @throws InterruptedException If the operation is interrupted
     */
    public JsonObject createTagBinding(String resourceName, String tagValue, String location) 
            throws IOException, InterruptedException {
        
        System.out.println("Creating tag binding for resource: " + resourceName + " with tag value: " + tagValue);
        
        // Resource path might need to be modified to conform to One Platform resource name format
        String normalizedResourceName = resourceName;
        if (normalizedResourceName.contains("/compute/v1/")) {
            normalizedResourceName = normalizedResourceName.replace("/compute/v1", "");
            
            // Further normalize compute resources to follow One Platform format
            // From: //compute.googleapis.com/projects/PROJECT_ID/zones/ZONE/instances/INSTANCE_NAME
            // To:   //compute.googleapis.com/projects/PROJECT_ID/zones/ZONE/instances/INSTANCE_NAME
            
            // Make sure all segments are properly formatted
            if (normalizedResourceName.contains("projects/") && normalizedResourceName.contains("zones/") && normalizedResourceName.contains("instances/")) {
                // The format is already correct after removing the "/compute/v1" part
                System.out.println("Using normalized resource name: " + normalizedResourceName);
            }
        }
        
        // API URL depends on whether we're using a global or location-specific resource
        String baseUrl;
        if (location != null && !location.isEmpty()) {
            // Extract region from zone if needed (for compatibility, but we'll use the original location for the URL)
            String apiLocation = location;
            
            System.out.println("Using location for API endpoint: " + apiLocation);
            // For location-specific resources
            baseUrl = "https://" + apiLocation + "-cloudresourcemanager.googleapis.com/v3/tagBindings";
        } else {
            // For global resources
            baseUrl = TAG_BINDINGS_API_URL + "tagBindings";
        }
        
        System.out.println("Using API endpoint: " + baseUrl);
        
        // Create the request body with the binding details
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("parent", normalizedResourceName);
        requestBody.addProperty("tagValue", tagValue);
        
        // Submit the request to create the tag binding
        JsonObject operation = httpClient.post(baseUrl, requestBody);
        
        // Wait for the operation to complete
        if (operation.has("name")) {
            String operationName = operation.get("name").getAsString();
            waitForOperationCompletion(operationName);
            
            // Get the completed tag binding
            return getTagBindingFromOperation(operation);
        }
        
        return operation;
    }
    
    /**
     * Deletes an existing tag binding.
     *
     * @param resourceName The full resource name to unbind from the tag
     * @param tagValue The tag value to unbind from the resource
     * @param location Optional location parameter (e.g., "us-central1-a" for a zone)
     * @throws IOException If an I/O error occurs
     * @throws InterruptedException If the operation is interrupted
     */
    public void deleteTagBinding(String resourceName, String tagValue, String location) 
            throws IOException, InterruptedException {
        
        System.out.println("Deleting tag binding for resource: " + resourceName + " with tag value: " + tagValue);
        
        // Normalize resource name if needed
        String normalizedResourceName = resourceName;
        if (normalizedResourceName.contains("/compute/v1/")) {
            normalizedResourceName = normalizedResourceName.replace("/compute/v1", "");
            System.out.println("Using normalized resource name: " + normalizedResourceName);
        }
        
        // URL-encode the resource name by replacing all "/" with "%2F"
        String encodedResourceName = normalizedResourceName.replace("/", "%2F");
        
        // Format the tag binding name according to the required format:
        // tagBindings/{encoded-resource-name}/{tag-value-name}
        String formattedTagBindingName = "tagBindings/" + encodedResourceName + "/" + tagValue;
        System.out.println("Formatted tag binding name: " + formattedTagBindingName);
        
        // API URL depends on whether we're using a global or location-specific resource
        String baseUrl;
        if (location != null && !location.isEmpty()) {
            System.out.println("Using location for API endpoint: " + location);
            // For location-specific resources
            baseUrl = "https://" + location + "-cloudresourcemanager.googleapis.com/v3/" + formattedTagBindingName;
        } else {
            // For global resources
            baseUrl = TAG_BINDINGS_API_URL + formattedTagBindingName;
        }
        
        System.out.println("Using API endpoint: " + baseUrl);
        
        // Submit the request to delete the tag binding
        JsonObject operation = httpClient.delete(baseUrl);
        
        // Wait for the operation to complete
        if (operation.has("name")) {
            String operationName = operation.get("name").getAsString();
            waitForOperationCompletion(operationName);
        }
    }
    
    /**
     * Deletes an existing tag binding using the full tag binding name.
     *
     * @param tagBindingName The full name of the tag binding to delete
     * @param location Optional location parameter (e.g., "us-central1-a" for a zone)
     * @throws IOException If an I/O error occurs
     * @throws InterruptedException If the operation is interrupted
     */
    public void deleteTagBindingByName(String tagBindingName, String location) 
            throws IOException, InterruptedException {
        
        System.out.println("Deleting tag binding by name: " + tagBindingName);
        
        // The tag binding name passed as a parameter should be the full name including "tagBindings/"
        String formattedTagBindingName = tagBindingName;
        if (!formattedTagBindingName.startsWith("tagBindings/")) {
            formattedTagBindingName = "tagBindings/" + formattedTagBindingName;
            System.out.println("Formatted tag binding name: " + formattedTagBindingName);
        }
        
        // API URL depends on whether we're using a global or location-specific resource
        String baseUrl;
        if (location != null && !location.isEmpty()) {
            System.out.println("Using location for API endpoint: " + location);
            // For location-specific resources
            baseUrl = "https://" + location + "-cloudresourcemanager.googleapis.com/v3/" + formattedTagBindingName;
        } else {
            // For global resources
            baseUrl = TAG_BINDINGS_API_URL + formattedTagBindingName;
        }
        
        System.out.println("Using API endpoint: " + baseUrl);
        
        // Submit the request to delete the tag binding
        JsonObject operation = httpClient.delete(baseUrl);
        
        // Wait for the operation to complete
        if (operation.has("name")) {
            String operationName = operation.get("name").getAsString();
            waitForOperationCompletion(operationName);
        }
    }
    
    /**
     * Lists all tag bindings associated with a specific resource.
     *
     * @param resourceName The full resource name
     * @param location Optional location parameter (e.g., "us-central1-a" for a zone)
     * @return List of tag bindings as JsonObjects
     * @throws IOException If an I/O error occurs
     */
    public List<JsonObject> listTagBindingsForResource(String resourceName, String location) 
            throws IOException {
        System.out.println("Listing tag bindings for resource: " + resourceName);
        
        // Resource path might need to be modified to conform to One Platform resource name format
        String normalizedResourceName = resourceName;
        if (normalizedResourceName.contains("/compute/v1/")) {
            normalizedResourceName = normalizedResourceName.replace("/compute/v1", "");
            System.out.println("Using normalized resource name: " + normalizedResourceName);
        }
        
        Map<String, String> params = new HashMap<>();
        params.put("parent", normalizedResourceName);
        
        // API URL depends on whether we're using a global or location-specific resource
        String baseUrl;
        if (location != null && !location.isEmpty()) {
            // Extract region from zone if needed (for compatibility, but we'll use the original location for the URL)
            String apiLocation = location;
            
            System.out.println("Using location for API endpoint: " + apiLocation);
            // For location-specific resources
            baseUrl = "https://" + apiLocation + "-cloudresourcemanager.googleapis.com/v3/tagBindings";
        } else {
            // For global resources
            baseUrl = TAG_BINDINGS_API_URL + "tagBindings";
        }
        
        String url = HttpClient.addQueryParams(baseUrl, params);
        System.out.println("Using API endpoint: " + url);
        
        return executeListRequest(url);
    }
    
    /**
     * Lists all tag bindings associated with a specific tag value.
     *
     * @param tagValueName The full tag value name
     * @param location Optional location parameter (e.g., "us-central1-a" for a zone)
     * @return List of tag bindings as JsonObjects
     * @throws IOException If an I/O error occurs
     */
    public List<JsonObject> listTagBindingsForTagValue(String tagValueName, String location) 
            throws IOException {
        System.out.println("Listing tag bindings for tag value: " + tagValueName);
        
        Map<String, String> params = new HashMap<>();
        params.put("tagValue", tagValueName);
        
        // API URL depends on whether we're using a global or location-specific resource
        String baseUrl;
        if (location != null && !location.isEmpty()) {
            // Extract region from zone if needed (for compatibility, but we'll use the original location for the URL)
            String apiLocation = location;
            
            System.out.println("Using location for API endpoint: " + apiLocation);
            // For location-specific resources
            baseUrl = "https://" + apiLocation + "-cloudresourcemanager.googleapis.com/v3/tagBindings";
        } else {
            // For global resources
            baseUrl = TAG_BINDINGS_API_URL + "tagBindings";
        }
        
        String url = HttpClient.addQueryParams(baseUrl, params);
        System.out.println("Using API endpoint: " + url);
        
        return executeListRequest(url);
    }
    
    /**
     * Executes a list request and processes paginated results.
     *
     * @param url The URL to send the GET request to
     * @return List of JsonObjects from all pages
     * @throws IOException If an I/O error occurs
     */
    private List<JsonObject> executeListRequest(String url) throws IOException {
        List<JsonObject> results = new ArrayList<>();
        String nextPageUrl = url;
        
        while (nextPageUrl != null) {
            JsonObject response = httpClient.get(nextPageUrl);
            
            if (response.has("tagBindings")) {
                JsonArray bindings = response.getAsJsonArray("tagBindings");
                bindings.forEach(item -> results.add(item.getAsJsonObject()));
            }
            
            if (response.has("nextPageToken") && !response.get("nextPageToken").isJsonNull()) {
                String pageToken = response.get("nextPageToken").getAsString();
                Map<String, String> params = new HashMap<>();
                params.put("pageToken", pageToken);
                nextPageUrl = HttpClient.addQueryParams(url, params);
            } else {
                nextPageUrl = null;
            }
        }
        
        return results;
    }
    
    /**
     * Extracts the tag binding information from an operation.
     *
     * @param operation The operation containing tag binding information
     * @return The tag binding as a JsonObject
     */
    private JsonObject getTagBindingFromOperation(JsonObject operation) {
        if (operation.has("response") && operation.getAsJsonObject("response").has("name")) {
            String tagBindingName = operation.getAsJsonObject("response").get("name").getAsString();
            JsonObject tagBinding = new JsonObject();
            tagBinding.addProperty("name", tagBindingName);
            return tagBinding;
        }
        return operation;
    }
    
    /**
     * Waits for a long-running operation to complete.
     *
     * @param operationName The name of the operation to wait for
     * @return The result of the operation as a JsonObject
     * @throws IOException If an I/O error occurs
     * @throws InterruptedException If the operation is interrupted
     */
    private JsonObject waitForOperationCompletion(String operationName) 
            throws IOException, InterruptedException {
        
        // Determine if this is a regional operation based on the URL
        String url;
        if (operationName.contains("-cloudresourcemanager.googleapis.com")) {
            // This is a regional operation, use the full URL
            url = operationName;
            System.out.println("Waiting for regional operation to complete: " + url);
        } else {
            // This is a global operation, use the base URL + operation name
            url = TAG_BINDINGS_API_URL + operationName;
            System.out.println("Waiting for global operation to complete: " + url);
        }
        
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (OPERATION_TIMEOUT_SECONDS * 1000);
        
        while (System.currentTimeMillis() < endTime) {
            JsonObject operation = httpClient.get(url);
            
            if (operation.has("done") && operation.get("done").getAsBoolean()) {
                System.out.println("Operation completed successfully");
                
                if (operation.has("error")) {
                    throw new IOException("Operation failed: " + operation.get("error").toString());
                }
                
                if (operation.has("response")) {
                    return operation.getAsJsonObject("response");
                }
                
                return operation;
            }
            
            // Wait a bit before polling again
            System.out.println("Operation still in progress, waiting...");
            TimeUnit.SECONDS.sleep(2);
        }
        
        throw new IOException("Operation did not complete within the timeout period of " + 
                               OPERATION_TIMEOUT_SECONDS + " seconds: " + operationName);
    }
    
    /**
     * Closes any resources used by this class.
     */
    @Override
    public void close() {
        // No resources to close in this implementation
    }
}