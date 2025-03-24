package com.example.gcptagging;

/**
 * Utility class for handling GCP resource names.
 * Helps format and parse resource names for various GCP resource types.
 */
public class GcpResourceNames {
    
    /**
     * Formats a VM instance resource name in the format required by the Resource Manager API.
     *
     * @param projectId The GCP project ID
     * @param zone The GCP zone where the VM is located
     * @param instanceName The VM instance name
     * @return The fully qualified resource name for the VM instance
     */
    public static String formatVmInstanceName(String projectId, String zone, String instanceName) {
        return String.format("//compute.googleapis.com/projects/%s/zones/%s/instances/%s", 
                projectId, zone, instanceName);
    }
    
    /**
     * Formats a Compute Engine Disk resource name.
     *
     * @param projectId The GCP project ID
     * @param zone The GCP zone where the disk is located
     * @param diskName The disk name
     * @return The fully qualified resource name for the disk
     */
    public static String formatDiskName(String projectId, String zone, String diskName) {
        return String.format("//compute.googleapis.com/projects/%s/zones/%s/disks/%s", 
                projectId, zone, diskName);
    }
    
    /**
     * Formats a GCP project resource name.
     *
     * @param projectId The GCP project ID
     * @return The fully qualified resource name for the project
     */
    public static String formatProjectName(String projectId) {
        return String.format("//cloudresourcemanager.googleapis.com/projects/%s", projectId);
    }
    
    /**
     * Formats a Cloud Storage bucket resource name.
     *
     * @param bucketName The Cloud Storage bucket name
     * @return The fully qualified resource name for the bucket
     */
    public static String formatStorageBucketName(String bucketName) {
        return String.format("//storage.googleapis.com/projects/_/buckets/%s", bucketName);
    }
    
    /**
     * Formats a BigQuery dataset resource name.
     *
     * @param projectId The GCP project ID
     * @param datasetId The BigQuery dataset ID
     * @return The fully qualified resource name for the dataset
     */
    public static String formatBigQueryDatasetName(String projectId, String datasetId) {
        return String.format("//bigquery.googleapis.com/projects/%s/datasets/%s", 
                projectId, datasetId);
    }
    
    /**
     * Formats a BigQuery table resource name.
     *
     * @param projectId The GCP project ID
     * @param datasetId The BigQuery dataset ID
     * @param tableId The BigQuery table ID
     * @return The fully qualified resource name for the table
     */
    public static String formatBigQueryTableName(String projectId, String datasetId, String tableId) {
        return String.format("//bigquery.googleapis.com/projects/%s/datasets/%s/tables/%s", 
                projectId, datasetId, tableId);
    }
    
    /**
     * Formats a tag binding name from its components.
     * This is used when deleting tag bindings after you know the tag binding name.
     *
     * @param resourceName The resource name to encode
     * @param tagValueName The tag value name to encode
     * @return A formatted tag binding name in the form "tagBindings/..."
     */
    public static String formatTagBindingName(String resourceName, String tagValueName) {
        // Replace forward slashes with @ symbols
        String resourcePart = resourceName.replace("//", "").replace("/", "@");
        String tagValuePart = tagValueName.replace("/", "@");
        
        // Combine to create the tag binding name
        return String.format("tagBindings/%s@%s", resourcePart, tagValuePart);
    }
}