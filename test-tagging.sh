#!/bin/bash
# This script tests the GCP Resource Tagging functionality with actual GCP API calls.
# Make sure to set the appropriate values for your GCP environment below.

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java and try again."
    exit 1
fi

# Check if the JAR file exists
JAR_FILE="target/gcptagging-1.0-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found at $JAR_FILE"
    echo "Please build the project first using: mvn clean package"
    exit 1
fi

# Define the classpath for reliable execution
CLASSPATH="$JAR_FILE:target/lib/*"

# Replace these values with your actual GCP resources
PROJECT_ID="my-project"
ZONE="us-central1-a"
INSTANCE_NAME="my-vm"
TAG_VALUE="tagValues/123456789"  # Replace with your actual tag value ID

# Format the resource name for a VM instance using the utility class
echo "Formatting resource name..."
RESOURCE_NAME=$(java -cp "$CLASSPATH" com.example.gcptagging.GcpResourceNames formatVmInstanceName $PROJECT_ID $ZONE $INSTANCE_NAME)

# Display information
echo "==== GCP Resource Tagging Test ===="
echo "Project ID: $PROJECT_ID"
echo "Resource: $RESOURCE_NAME"
echo ""
echo "Make sure your service-account.json file has the necessary permissions"
echo "for the Cloud Resource Manager API and is in the current directory."
echo ""

# Test creating a tag binding
echo "Creating tag binding..."
java -cp "$CLASSPATH" com.example.gcptagging.Main create service-account.json "$RESOURCE_NAME" "$TAG_VALUE"
echo ""

# Test listing tag bindings for the resource
echo "Listing tag bindings for resource..."
java -cp "$CLASSPATH" com.example.gcptagging.Main list-resource service-account.json "$RESOURCE_NAME"
echo ""

# Test listing tag bindings for the tag value
echo "Listing tag bindings for tag value..."
java -cp "$CLASSPATH" com.example.gcptagging.Main list-tag service-account.json "$TAG_VALUE"
echo ""

# Format the tag binding name for deletion
echo "Formatting tag binding name..."
TAG_BINDING_NAME=$(java -cp "$CLASSPATH" com.example.gcptagging.GcpResourceNames formatTagBindingName "$RESOURCE_NAME" "$TAG_VALUE")
echo "Tag binding name: $TAG_BINDING_NAME"
echo ""

# This is commented out by default to prevent accidental deletion
# Uncomment to test deletion
#echo "Deleting tag binding..."
#java -cp "$CLASSPATH" com.example.gcptagging.Main delete service-account.json "$TAG_BINDING_NAME"
#echo ""

echo "Test completed. Review the output above for results." 