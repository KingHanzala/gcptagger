# GCP Resource Tagging Tool

A Java command-line tool for managing tags on Google Cloud Platform (GCP) resources.

## Overview

The GCP Resource Tagging Tool enables you to create, delete, and list tag bindings for your GCP resources through the Resource Manager API. Tags help you organize, manage, and filter your GCP resources at scale.

## Features

- Create tag bindings between GCP resources and tag values
- Delete existing tag bindings
- List tag bindings for specific resources
- List tag bindings associated with specific tag values
- Support for various GCP resource types (VM instances, disks, buckets, etc.)

## Prerequisites

- Java 11 or higher
- Maven (for building)
- A GCP service account with the following permissions:
  - `resourcemanager.tagBindings.create`
  - `resourcemanager.tagBindings.delete`
  - `resourcemanager.tagBindings.list`
- Service account key file (JSON format)

## Building the Project

1. Clone this repository:
   ```
   git clone https://github.com/yourusername/gcptagging.git
   cd gcptagging
   ```

2. Build with Maven:
   ```
   mvn clean package
   ```

This will create a JAR file in the `target` directory and copy all dependencies to `target/lib`.

## Usage

### Command Line Interface

There are two ways to run the application:

#### Option 1: Using the JAR with the Classpath

```
java -jar target/gcptagging-1.0-SNAPSHOT.jar <command> <args>
```

**Note:** This requires that the `lib` directory with all dependencies exists in the same directory as the JAR file.

#### Option 2: Using the Classpath Directly (ZSH Compatible)

```
java -cp "target/gcptagging-1.0-SNAPSHOT.jar:target/lib/*" com.example.gcptagging.Main <command> <args>
```

### Available Commands

#### Create a Tag Binding

```
java -jar target/gcptagging-1.0-SNAPSHOT.jar create <service-account-file> <resource-name> <tag-value>
```

Example:
```
java -jar target/gcptagging-1.0-SNAPSHOT.jar create service-account.json \
    //compute.googleapis.com/projects/my-project/zones/us-central1-a/instances/my-vm \
    tagValues/123456789
```

#### Delete a Tag Binding

```
java -jar target/gcptagging-1.0-SNAPSHOT.jar delete <service-account-file> <tag-binding-name>
```

Example:
```
java -jar target/gcptagging-1.0-SNAPSHOT.jar delete service-account.json \
    tagBindings/compute.googleapis.com@projects@my-project@zones@us-central1-a@instances@my-vm@tagValues@123456789
```

#### List Tag Bindings for a Resource

```
java -jar target/gcptagging-1.0-SNAPSHOT.jar list-resource <service-account-file> <resource-name>
```

Example:
```
java -jar target/gcptagging-1.0-SNAPSHOT.jar list-resource service-account.json \
    //compute.googleapis.com/projects/my-project/zones/us-central1-a/instances/my-vm
```

#### List Tag Bindings for a Tag Value

```
java -jar target/gcptagging-1.0-SNAPSHOT.jar list-tag <service-account-file> <tag-value>
```

Example:
```
java -jar target/gcptagging-1.0-SNAPSHOT.jar list-tag service-account.json tagValues/123456789
```

### Resource Name Formats

The tool supports various resource name formats for different GCP resource types. Some examples:

- VM Instance: `//compute.googleapis.com/projects/{PROJECT_ID}/zones/{ZONE}/instances/{INSTANCE_NAME}`
- Disk: `//compute.googleapis.com/projects/{PROJECT_ID}/zones/{ZONE}/disks/{DISK_NAME}`
- Storage Bucket: `//storage.googleapis.com/projects/_/buckets/{BUCKET_NAME}`
- BigQuery Dataset: `//bigquery.googleapis.com/projects/{PROJECT_ID}/datasets/{DATASET_ID}`
- BigQuery Table: `//bigquery.googleapis.com/projects/{PROJECT_ID}/datasets/{DATASET_ID}/tables/{TABLE_ID}`
- Project: `//cloudresourcemanager.googleapis.com/projects/{PROJECT_ID}`

### Helper Utilities

The tool also includes helper classes that can be used to format resource names:

```
java -cp "target/gcptagging-1.0-SNAPSHOT.jar:target/lib/*" com.example.gcptagging.GcpResourceNames formatVmInstanceName my-project us-central1-a my-vm
```

## Testing

A test script (`test-tagging.sh`) is provided to help you verify the functionality of the tool. You can edit the script to use your actual GCP resource information.

```
chmod +x test-tagging.sh
./test-tagging.sh
```

## Implementation Details

- Built on the Google Cloud Resource Manager v3 API
- Uses OAuth 2.0 for authentication via service account credentials
- Handles asynchronous operations with timeouts for reliable tag management

## Troubleshooting

### Permission Errors

If you encounter permission errors, ensure your service account has the necessary IAM roles:

- `roles/resourcemanager.tagUser` (includes all required permissions)

### JAR File Not Found

If you see an error like `Error: Unable to access jarfile target/gcptagging-1.0-SNAPSHOT.jar`, make sure you've built the project with Maven first:

```
mvn clean package
```

### Missing Dependencies

If you see class loading errors, make sure the `lib` directory with all dependencies exists in the same directory as the JAR file. Alternatively, you can run the application using the classpath directly:

```
java -cp "target/gcptagging-1.0-SNAPSHOT.jar:target/lib/*" com.example.gcptagging.Main <command> <args>
```

### Running from a Different Directory

If you need to run the tool from a different directory, use absolute paths:

```
java -jar /path/to/gcptagging/target/gcptagging-1.0-SNAPSHOT.jar <command> <args>
```

Or with the classpath:

```
java -cp "/path/to/gcptagging/target/gcptagging-1.0-SNAPSHOT.jar:/path/to/gcptagging/target/lib/*" com.example.gcptagging.Main <command> <args>
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
