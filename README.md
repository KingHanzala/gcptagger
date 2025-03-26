# GCP Resource Tagging Tool

A Java command-line tool for managing tags on Google Cloud Platform (GCP) resources.

## Overview

The GCP Resource Tagging Tool enables you to create, delete, and list tag bindings for your GCP resources through the Resource Manager API. Tags help you organize, manage, and filter your GCP resources at scale.

## Features

- Create tag bindings between GCP resources and tag values
- Delete existing tag bindings using resource name and tag value
- Delete existing tag bindings using the full tag binding name
- List tag bindings for specific resources
- List tag bindings associated with specific tag values
- Support for various GCP resource types (VM instances, disks, buckets, etc.)
- Support for zonal and regional resources

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
java -jar target/gcptagging-1.0-SNAPSHOT.jar create <service-account-file> <resource-name> <tag-value> [location]
```

Example for a zonal resource (VM instance):
```
java -jar target/gcptagging-1.0-SNAPSHOT.jar create service-account.json \
    //compute.googleapis.com/compute/v1/projects/my-project/zones/us-central1-a/instances/my-vm \
    tagValues/123456789 us-central1-a
```

Example for a global resource (project):
```
java -jar target/gcptagging-1.0-SNAPSHOT.jar create service-account.json \
    //cloudresourcemanager.googleapis.com/projects/my-project \
    tagValues/123456789
```

#### Delete a Tag Binding Using Resource Name and Tag Value

```
java -jar target/gcptagging-1.0-SNAPSHOT.jar delete <service-account-file> <resource-name> <tag-value> [location]
```

Example:
```
java -jar target/gcptagging-1.0-SNAPSHOT.jar delete service-account.json \
    //compute.googleapis.com/compute/v1/projects/my-project/zones/us-central1-a/instances/my-vm \
    tagValues/123456789 us-central1-a
```

#### Delete a Tag Binding Using the Full Tag Binding Name

```
java -jar target/gcptagging-1.0-SNAPSHOT.jar delete-by-name <service-account-file> <tag-binding-name> [location]
```

Example:
```
java -jar target/gcptagging-1.0-SNAPSHOT.jar delete-by-name service-account.json \
    tagBindings/compute.googleapis.com@projects@my-project@zones@us-central1-a@instances@my-vm@tagValues@123456789 us-central1-a
```

#### List Tag Bindings for a Resource

```
java -jar target/gcptagging-1.0-SNAPSHOT.jar list-resource <service-account-file> <resource-name> [location]
```

Example:
```
java -jar target/gcptagging-1.0-SNAPSHOT.jar list-resource service-account.json \
    //compute.googleapis.com/compute/v1/projects/my-project/zones/us-central1-a/instances/my-vm us-central1-a
```

#### List Tag Bindings for a Tag Value

```
java -jar target/gcptagging-1.0-SNAPSHOT.jar list-tag <service-account-file> <tag-value> [location]
```

Example:
```
java -jar target/gcptagging-1.0-SNAPSHOT.jar list-tag service-account.json tagValues/123456789
```

### Resource Name Formats

The tool supports various resource name formats for different GCP resource types. Some examples:

- VM Instance: `//compute.googleapis.com/compute/v1/projects/{PROJECT_ID}/zones/{ZONE}/instances/{INSTANCE_NAME}`
- Disk: `//compute.googleapis.com/compute/v1/projects/{PROJECT_ID}/zones/{ZONE}/disks/{DISK_NAME}`
- Storage Bucket: `//storage.googleapis.com/projects/_/buckets/{BUCKET_NAME}`
- BigQuery Dataset: `//bigquery.googleapis.com/projects/{PROJECT_ID}/datasets/{DATASET_ID}`
- BigQuery Table: `//bigquery.googleapis.com/projects/{PROJECT_ID}/datasets/{DATASET_ID}/tables/{TABLE_ID}`
- Project: `//cloudresourcemanager.googleapis.com/projects/{PROJECT_ID}`

### Location Parameter

For zonal resources like Compute Engine instances, you should provide the full zone name (e.g., `us-central1-a`) in the optional location parameter. The tool will automatically:

1. Use this location for API endpoint construction
2. Normalize resource names by removing the `/compute/v1` part if present
3. URL-encode resource names as necessary for deletion operations

### Understanding Tag Binding Names

When creating a tag binding, the API returns a binding name in the format:

```
tagBindings/{encoded-resource-name}/{tag-value-name}
```

Where:
- `{encoded-resource-name}` is the URL-encoded resource name with all slashes (`/`) replaced by `%2F`
- `{tag-value-name}` is the name of the tag value (e.g., `tagValues/123456789`)

When deleting by name, you need to provide the full binding name. When deleting by resource and tag value, the tool automatically generates the proper binding name format.

## Implementation Details

- Built on the Google Cloud Resource Manager v3 API
- Uses OAuth 2.0 for authentication via service account credentials
- Handles asynchronous operations with timeouts for reliable tag management
- Supports both zonal and regional resources
- Automatically normalizes and encodes resource names as required by the API

## Troubleshooting

### Permission Errors

If you encounter permission errors, ensure your service account has the necessary IAM roles:

- `roles/resourcemanager.tagUser` (includes all required permissions)

### Location Parameter Issues

For zonal resources, make sure to provide the full zone name (e.g., `us-central1-a`), not just the region name. The tool will use this zone for the API endpoint.

### Resource Name Format Issues

If you receive errors about invalid resource names, ensure you're using the proper format for your resource type. Different GCP services use different formats.

### Tag Binding Name Format Issues

When using `delete-by-name`, ensure the tag binding name is correctly formatted:
- It should start with `tagBindings/`
- The resource name should be URL-encoded (all `/` replaced by `%2F`)
- The tag value name should be appended after the resource name

## License

This project is licensed under the MIT License - see the LICENSE file for details.
