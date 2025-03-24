# GCP Resource Tagging Tool

A Java-based command-line tool for tagging Google Cloud Platform (GCP) resources using the Cloud Resource Manager Tag Bindings API.

## Features

- Create tag bindings for GCP resources
- Delete existing tag bindings
- List tag bindings for a specific resource
- List tag bindings for a specific tag value
- Utility methods for formatting resource names for various GCP services

## Prerequisites

- Java 11 or higher
- Maven for building the project
- A GCP service account with appropriate permissions:
  - `roles/resourcemanager.tagUser` - For creating and managing tag bindings

## Setup

1. **Clone the repository**

```bash
git clone https://github.com/yourusername/gcptagging.git
cd gcptagging
```

2. **Build the project**

```bash
mvn clean package
```

This will compile the code and create a JAR file in the `target` directory.

3. **Create a service account and download credentials**

- Go to the Google Cloud Console
- Navigate to "IAM & Admin" > "Service Accounts"
- Create a new service account with the required permissions
- Create and download a JSON key file
- Save the key file as `service-account.json` in the project directory

## Usage

The tool provides four main commands:

### Create a tag binding

```bash
java -jar target/gcptagging-1.0-SNAPSHOT.jar create service-account.json <resource-name> <tag-value>
```

Example:
```bash
java -jar target/gcptagging-1.0-SNAPSHOT.jar create service-account.json //compute.googleapis.com/projects/my-project/zones/us-central1-a/instances/my-vm tagValues/123456789
```

### Delete a tag binding

```bash
java -jar target/gcptagging-1.0-SNAPSHOT.jar delete service-account.json <tag-binding-name>
```

Example:
```bash
java -jar target/gcptagging-1.0-SNAPSHOT.jar delete service-account.json tagBindings/compute.googleapis.com@projects@my-project@zones@us-central1-a@instances@my-vm@tagValues@123456789
```

### List tag bindings for a resource

```bash
java -jar target/gcptagging-1.0-SNAPSHOT.jar list-resource service-account.json <resource-name>
```

Example:
```bash
java -jar target/gcptagging-1.0-SNAPSHOT.jar list-resource service-account.json //compute.googleapis.com/projects/my-project/zones/us-central1-a/instances/my-vm
```

### List tag bindings for a tag value

```bash
java -jar target/gcptagging-1.0-SNAPSHOT.jar list-tag service-account.json <tag-value>
```

Example:
```bash
java -jar target/gcptagging-1.0-SNAPSHOT.jar list-tag service-account.json tagValues/123456789
```

## Resource Name Formatting

The tool includes the `GcpResourceNames` class to help format resource names correctly:

```bash
java -cp target/gcptagging-1.0-SNAPSHOT.jar:target/lib/* com.example.gcptagging.GcpResourceNames formatVmInstanceName my-project us-central1-a my-vm
```

Note: If you're using zsh (like on macOS), you need to quote the classpath with wildcards:

```bash
java -cp "target/gcptagging-1.0-SNAPSHOT.jar:target/lib/*" com.example.gcptagging.GcpResourceNames formatVmInstanceName my-project us-central1-a my-vm
```

## Testing

A test script is provided to verify the functionality:

```bash
./test-tagging.sh
```

## Resource Types Supported

The tool can tag any GCP resource that supports tags. Some common resource types and their formatting:

- VM Instances: `//compute.googleapis.com/projects/<project-id>/zones/<zone>/instances/<instance-name>`
- GCS Buckets: `//storage.googleapis.com/<bucket-name>`
- Cloud SQL Instances: `//sqladmin.googleapis.com/projects/<project-id>/instances/<instance-name>`
- GKE Clusters: `//container.googleapis.com/projects/<project-id>/locations/<zone>/clusters/<cluster-name>`
- BigQuery Datasets: `//bigquery.googleapis.com/projects/<project-id>/datasets/<dataset-name>`

## License

MIT License # gcptagger
