package com.example.gcptagging;

/**
 * Utility class for formatting GCP resource names in the formats required by the APIs.
 */
public class GcpResourceNames {

    /**
     * Formats a VM instance name into the canonical resource name format.
     * 
     * @param projectId The GCP project ID
     * @param zone The compute zone
     * @param instanceName The VM instance name
     * @return Canonical resource name for the VM instance
     */
    public static String formatVmInstanceName(String projectId, String zone, String instanceName) {
        return String.format("//compute.googleapis.com/projects/%s/zones/%s/instances/%s", 
                projectId, zone, instanceName);
    }
    
    /**
     * Formats a GCS bucket name into the canonical resource name format.
     * 
     * @param bucketName The GCS bucket name
     * @return Canonical resource name for the GCS bucket
     */
    public static String formatGcsBucketName(String bucketName) {
        return String.format("//storage.googleapis.com/%s", bucketName);
    }
    
    /**
     * Formats a Cloud SQL instance name into the canonical resource name format.
     * 
     * @param projectId The GCP project ID
     * @param instanceName The Cloud SQL instance name
     * @return Canonical resource name for the Cloud SQL instance
     */
    public static String formatCloudSqlInstanceName(String projectId, String instanceName) {
        return String.format("//sqladmin.googleapis.com/projects/%s/instances/%s",
                projectId, instanceName);
    }
    
    /**
     * Formats a GKE cluster name into the canonical resource name format.
     * 
     * @param projectId The GCP project ID
     * @param zone The compute zone or region
     * @param clusterName The GKE cluster name
     * @return Canonical resource name for the GKE cluster
     */
    public static String formatGkeClusterName(String projectId, String zone, String clusterName) {
        return String.format("//container.googleapis.com/projects/%s/locations/%s/clusters/%s",
                projectId, zone, clusterName);
    }
    
    /**
     * Formats a BigQuery dataset name into the canonical resource name format.
     * 
     * @param projectId The GCP project ID
     * @param datasetName The BigQuery dataset name
     * @return Canonical resource name for the BigQuery dataset
     */
    public static String formatBigQueryDatasetName(String projectId, String datasetName) {
        return String.format("//bigquery.googleapis.com/projects/%s/datasets/%s",
                projectId, datasetName);
    }
    
    /**
     * Main method for testing resource name formatting.
     * Can be run from command line with arguments.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java GcpResourceNames <command> [args...]");
            System.out.println("Commands:");
            System.out.println("  formatVmInstanceName <projectId> <zone> <instanceName>");
            System.out.println("  formatGcsBucketName <bucketName>");
            System.out.println("  formatCloudSqlInstanceName <projectId> <instanceName>");
            System.out.println("  formatGkeClusterName <projectId> <zone> <clusterName>");
            System.out.println("  formatBigQueryDatasetName <projectId> <datasetName>");
            return;
        }
        
        String command = args[0];
        
        try {
            switch (command) {
                case "formatVmInstanceName":
                    if (args.length < 4) {
                        System.out.println("Error: formatVmInstanceName requires projectId, zone, and instanceName");
                        return;
                    }
                    System.out.println(formatVmInstanceName(args[1], args[2], args[3]));
                    break;
                    
                case "formatGcsBucketName":
                    if (args.length < 2) {
                        System.out.println("Error: formatGcsBucketName requires bucketName");
                        return;
                    }
                    System.out.println(formatGcsBucketName(args[1]));
                    break;
                    
                case "formatCloudSqlInstanceName":
                    if (args.length < 3) {
                        System.out.println("Error: formatCloudSqlInstanceName requires projectId and instanceName");
                        return;
                    }
                    System.out.println(formatCloudSqlInstanceName(args[1], args[2]));
                    break;
                    
                case "formatGkeClusterName":
                    if (args.length < 4) {
                        System.out.println("Error: formatGkeClusterName requires projectId, zone, and clusterName");
                        return;
                    }
                    System.out.println(formatGkeClusterName(args[1], args[2], args[3]));
                    break;
                    
                case "formatBigQueryDatasetName":
                    if (args.length < 3) {
                        System.out.println("Error: formatBigQueryDatasetName requires projectId and datasetName");
                        return;
                    }
                    System.out.println(formatBigQueryDatasetName(args[1], args[2]));
                    break;
                    
                default:
                    System.out.println("Error: Unknown command: " + command);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
} 