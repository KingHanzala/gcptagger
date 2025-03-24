package com.example.gcptagging;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.resourcemanager.v3.CreateTagKeyRequest;
import com.google.cloud.resourcemanager.v3.CreateTagValueRequest;
import com.google.cloud.resourcemanager.v3.GetTagKeyRequest;
import com.google.cloud.resourcemanager.v3.GetTagValueRequest;
import com.google.cloud.resourcemanager.v3.ListTagKeysRequest;
import com.google.cloud.resourcemanager.v3.ListTagValuesRequest;
import com.google.cloud.resourcemanager.v3.TagKey;
import com.google.cloud.resourcemanager.v3.TagKeysClient;
import com.google.cloud.resourcemanager.v3.TagKeysSettings;
import com.google.cloud.resourcemanager.v3.TagValue;
import com.google.cloud.resourcemanager.v3.TagValuesClient;
import com.google.cloud.resourcemanager.v3.TagValuesSettings;
import com.google.longrunning.Operation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Utility class for managing GCP Tags.
 * Note: This is separate from tag bindings. This class helps create/manage tag keys and tag values
 * that can later be used with the GcpResourceTagger to bind to resources.
 */
public class TagsUtil {
    
    private final TagKeysClient tagKeysClient;
    private final TagValuesClient tagValuesClient;
    private final String organizationId;
    
    /**
     * Constructor for TagsUtil.
     * 
     * @param credentials The Google credentials
     * @param organizationId The organization ID (numeric format)
     * @throws IOException If clients cannot be created
     */
    public TagsUtil(GoogleCredentials credentials, String organizationId) throws IOException {
        // Create credentials provider
        FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(
                credentials.createScoped("https://www.googleapis.com/auth/cloud-platform"));
        
        // Create settings for TagKeysClient
        TagKeysSettings tagKeysSettings = TagKeysSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider)
                .build();
        
        // Create settings for TagValuesClient
        TagValuesSettings tagValuesSettings = TagValuesSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider)
                .build();
        
        // Create clients
        this.tagKeysClient = TagKeysClient.create(tagKeysSettings);
        this.tagValuesClient = TagValuesClient.create(tagValuesSettings);
        this.organizationId = organizationId;
    }
    
    /**
     * Creates a tag key in the organization.
     * 
     * @param shortName The short name for the tag key (e.g., "environment")
     * @param description The description of the tag key
     * @return The created TagKey
     * @throws IOException If the API call fails
     * @throws InterruptedException If the operation is interrupted
     * @throws ExecutionException If the operation fails
     * @throws TimeoutException If the operation times out
     */
    public TagKey createTagKey(String shortName, String description) 
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        // Create a TagKey
        TagKey tagKey = TagKey.newBuilder()
                .setParent("organizations/" + organizationId)
                .setShortName(shortName)
                .setDescription(description)
                .build();
        
        // Create request
        CreateTagKeyRequest request = CreateTagKeyRequest.newBuilder()
                .setTagKey(tagKey)
                .build();
        
        // Create the tag key using the operation method and wait for completion
        Operation operation = tagKeysClient.createTagKeyCallable()
                .futureCall(request)
                .get();
        
        // Get the fully qualified name of the created tag key
        String tagKeyName = operation.getResponse().toString();
        
        // Retrieve the complete tag key using the name
        return tagKeysClient.getTagKey(
                GetTagKeyRequest.newBuilder().setName(tagKeyName).build());
    }
    
    /**
     * Creates a tag value for a tag key.
     * 
     * @param tagKeyId The full resource name of the tag key
     * @param shortName The short name for the tag value (e.g., "production")
     * @param description The description of the tag value
     * @return The created TagValue
     * @throws IOException If the API call fails
     * @throws InterruptedException If the operation is interrupted
     * @throws ExecutionException If the operation fails
     * @throws TimeoutException If the operation times out
     */
    public TagValue createTagValue(String tagKeyId, String shortName, String description) 
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        // Create a TagValue
        TagValue tagValue = TagValue.newBuilder()
                .setParent(tagKeyId)
                .setShortName(shortName)
                .setDescription(description)
                .build();
        
        // Create request
        CreateTagValueRequest request = CreateTagValueRequest.newBuilder()
                .setTagValue(tagValue)
                .build();
        
        // Create the tag value using the operation method and wait for completion
        Operation operation = tagValuesClient.createTagValueCallable()
                .futureCall(request)
                .get();
        
        // Get the fully qualified name of the created tag value
        String tagValueName = operation.getResponse().toString();
        
        // Retrieve the complete tag value using the name
        return tagValuesClient.getTagValue(
                GetTagValueRequest.newBuilder().setName(tagValueName).build());
    }
    
    /**
     * Lists all tag keys in the organization.
     * 
     * @return List of TagKey objects
     */
    public List<TagKey> listTagKeys() {
        ListTagKeysRequest request = ListTagKeysRequest.newBuilder()
                .setParent("organizations/" + organizationId)
                .build();
        
        List<TagKey> tagKeys = new ArrayList<>();
        tagKeysClient.listTagKeys(request).iterateAll().forEach(tagKeys::add);
        
        System.out.println("Found " + tagKeys.size() + " tag keys in organization " + organizationId);
        return tagKeys;
    }
    
    /**
     * Lists all tag values for a tag key.
     * 
     * @param tagKeyId The full resource name of the tag key
     * @return List of TagValue objects
     */
    public List<TagValue> listTagValues(String tagKeyId) {
        ListTagValuesRequest request = ListTagValuesRequest.newBuilder()
                .setParent(tagKeyId)
                .build();
        
        List<TagValue> tagValues = new ArrayList<>();
        tagValuesClient.listTagValues(request).iterateAll().forEach(tagValues::add);
        
        System.out.println("Found " + tagValues.size() + " tag values for tag key " + tagKeyId);
        return tagValues;
    }
    
    /**
     * Gets a tag key by its name.
     * 
     * @param tagKeyName The full resource name of the tag key
     * @return The TagKey object
     * @throws IOException If the API call fails
     */
    public TagKey getTagKey(String tagKeyName) throws IOException {
        GetTagKeyRequest request = GetTagKeyRequest.newBuilder()
                .setName(tagKeyName)
                .build();
        
        return tagKeysClient.getTagKey(request);
    }
    
    /**
     * Gets a tag value by its name.
     * 
     * @param tagValueName The full resource name of the tag value
     * @return The TagValue object
     * @throws IOException If the API call fails
     */
    public TagValue getTagValue(String tagValueName) throws IOException {
        GetTagValueRequest request = GetTagValueRequest.newBuilder()
                .setName(tagValueName)
                .build();
        
        return tagValuesClient.getTagValue(request);
    }
    
    /**
     * Close the clients to release resources.
     * 
     * @throws Exception If the clients fail to close
     */
    public void close() throws Exception {
        if (tagKeysClient != null) {
            tagKeysClient.close();
        }
        if (tagValuesClient != null) {
            tagValuesClient.close();
        }
    }
}