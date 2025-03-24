#!/bin/bash
# Simple test script to verify the GCP tagging tool functionality

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    exit 1
fi

echo "===== GCP Resource Tagging Tool Test ====="
echo ""

# Test variables
PROJECT_ID="my-project"
ZONE="us-central1-a"
INSTANCE_NAME="my-vm"
TAG_VALUE="tagValues/123456789"

# Test resource name formatting
echo "Testing resource name formatting..."
RESOURCE_NAME=$(java -cp "target/gcptagging-1.0-SNAPSHOT.jar:target/lib/*" com.example.gcptagging.GcpResourceNames formatVmInstanceName $PROJECT_ID $ZONE $INSTANCE_NAME)
echo "Formatted VM resource name: $RESOURCE_NAME"
echo ""

# Display project info
echo "Project Information:"
echo "  Project ID: $PROJECT_ID"
echo "  Zone: $ZONE"
echo "  VM Instance: $INSTANCE_NAME"
echo "  Tag Value: $TAG_VALUE"
echo ""

echo "NOTE: This is a demo script. In a real environment, you should replace service-account.json with your actual service account credentials file."
echo ""

# Test create tag binding
echo "Testing 'create' command..."
java -cp "target/gcptagging-1.0-SNAPSHOT.jar:target/lib/*" com.example.gcptagging.Main create service-account.json $RESOURCE_NAME $TAG_VALUE
echo ""

# Test list tag bindings for resource
echo "Testing 'list-resource' command..."
java -cp "target/gcptagging-1.0-SNAPSHOT.jar:target/lib/*" com.example.gcptagging.Main list-resource service-account.json $RESOURCE_NAME
echo ""

# Test list tag bindings for tag value
echo "Testing 'list-tag' command..."
java -cp "target/gcptagging-1.0-SNAPSHOT.jar:target/lib/*" com.example.gcptagging.Main list-tag service-account.json $TAG_VALUE
echo ""

# Test delete tag binding
echo "Testing 'delete' command..."
java -cp "target/gcptagging-1.0-SNAPSHOT.jar:target/lib/*" com.example.gcptagging.Main delete service-account.json "tagBindings/compute.googleapis.com@projects@my-project@zones@us-central1-a@instances@my-vm@tagValues@123456789"
echo ""

echo "All tests completed successfully!"
echo "GCP Resource Tagging Tool is ready to use!" 