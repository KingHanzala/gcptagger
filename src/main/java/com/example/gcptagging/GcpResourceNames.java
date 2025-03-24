package com.example.gcptagging;

/**
 * Utility class for handling GCP resource names.
 * Helps format and parse resource names for various GCP resource types.
 */
public class GcpResourceNames {
    
    /**
     * Main method to allow running the class from the command line.
     * Usage: java ... GcpResourceNames <method> <args...>
     * Example: java ... GcpResourceNames formatVmInstanceName my-project us-central1-a my-vm
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java ... GcpResourceNames <method> <args...>");
            System.out.println("Available methods:");
            System.out.println("  formatVmInstanceName <projectId> <zone> <instanceName>");
            System.out.println("  formatDiskName <projectId> <zone> <diskName>");
            System.out.println("  formatProjectName <projectId>");
            System.out.println("  formatStorageBucketName <bucketName>");
            System.out.println("  formatBigQueryDatasetName <projectId> <datasetId>");
            System.out.println("  formatBigQueryTableName <projectId> <datasetId> <tableId>");
            System.out.println("  formatTagBindingName <resourceName> <tagValueName>");
            System.exit(1);
        }

        String method = args[0];
        try {
            switch (method) {
                case "formatVmInstanceName":
                    if (args.length < 4) {
                        System.out.println("Error: formatVmInstanceName requires projectId, zone, and instanceName");
                        System.exit(1);
                    }
                    System.out.println(formatVmInstanceName(args[1], args[2], args[3]));
                    break;
                    
                case "formatDiskName":
                    if (args.length < 4) {
                        System.out.println("Error: formatDiskName requires projectId, zone, and diskName");
                        System.exit(1);
                    }
                    System.out.println(formatDiskName(args[1], args[2], args[3]));
                    break;
                    
                case "formatProjectName":
                    if (args.length < 2) {
                        System.out.println("Error: formatProjectName requires projectId");
                        System.exit(1);
                    }
                    System.out.println(formatProjectName(args[1]));
                    break;
                    
                case "formatStorageBucketName":
                    if (args.length < 2) {
                        System.out.println("Error: formatStorageBucketName requires bucketName");
                        System.exit(1);
                    }
                    System.out.println(formatStorageBucketName(args[1]));
                    break;
                    
                case "formatBigQueryDatasetName":
                    if (args.length < 3) {
                        System.out.println("Error: formatBigQueryDatasetName requires projectId and datasetId");
                        System.exit(1);
                    }
                    System.out.println(formatBigQueryDatasetName(args[1], args[2]));
                    break;
                    
                case "formatBigQueryTableName":
                    if (args.length < 4) {
                        System.out.println("Error: formatBigQueryTableName requires projectId, datasetId, and tableId");
                        System.exit(1);
                    }
                    System.out.println(formatBigQueryTableName(args[1], args[2], args[3]));
                    break;
                    
                case "formatTagBindingName":
                    if (args.length < 3) {
                        System.out.println("Error: formatTagBindingName requires resourceName and tagValueName");
                        System.exit(1);
                    }
                    System.out.println(formatTagBindingName(args[1], args[2]));
                    break;
                    
                default:
                    System.out.println("Error: Unknown method " + method);
                    System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
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