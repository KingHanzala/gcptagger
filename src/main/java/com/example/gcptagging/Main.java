package com.example.gcptagging;

/**
 * Main class for the GCP Resource Tagging Tool.
 * This is a demonstration version that shows the command structure without making actual API calls.
 */
public class Main {
    
    private static final String USAGE = "GCP Resource Tagging Tool\n\n" +
            "Usage: java -jar gcptagging.jar <command> <credentials-file> [args...]\n\n" +
            "Commands:\n" +
            "  create <credentials-file> <resource-name> <tag-value>       - Create a tag binding for a resource\n" +
            "  delete <credentials-file> <tag-binding-name>                - Delete a tag binding\n" +
            "  list-resource <credentials-file> <resource-name>            - List tag bindings for a resource\n" +
            "  list-tag <credentials-file> <tag-value>                     - List tag bindings for a tag value\n\n" +
            "Examples:\n" +
            "  Create a tag binding:\n" +
            "    java -jar gcptagging.jar create service-account.json //compute.googleapis.com/projects/my-project/zones/us-central1-a/instances/my-vm tagValues/123456789\n\n" +
            "  Delete a tag binding:\n" +
            "    java -jar gcptagging.jar delete service-account.json tagBindings/compute.googleapis.com@projects@my-project@zones@us-central1-a@instances@my-vm@tagValues@123456789\n\n" +
            "  List tag bindings for a resource:\n" +
            "    java -jar gcptagging.jar list-resource service-account.json //compute.googleapis.com/projects/my-project/zones/us-central1-a/instances/my-vm\n\n" +
            "  List tag bindings for a tag value:\n" +
            "    java -jar gcptagging.jar list-tag service-account.json tagValues/123456789";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String command = args[0];
        String credentialsFile = args[1];
        
        try {
            GcpResourceTaggerDemo tagger = new GcpResourceTaggerDemo(credentialsFile);

            switch (command) {
                case "create":
                    if (args.length < 4) {
                        System.out.println("Error: Missing arguments for create command");
                        System.out.println(USAGE);
                        System.exit(1);
                    }
                    String resourceName = args[2];
                    String tagValue = args[3];
                    tagger.createTagBinding(resourceName, tagValue);
                    break;
                    
                case "delete":
                    if (args.length < 3) {
                        System.out.println("Error: Missing arguments for delete command");
                        System.out.println(USAGE);
                        System.exit(1);
                    }
                    String tagBindingName = args[2];
                    tagger.deleteTagBinding(tagBindingName);
                    break;
                    
                case "list-resource":
                    if (args.length < 3) {
                        System.out.println("Error: Missing arguments for list-resource command");
                        System.out.println(USAGE);
                        System.exit(1);
                    }
                    String resourceNameToList = args[2];
                    tagger.listTagBindingsForResource(resourceNameToList);
                    break;
                    
                case "list-tag":
                    if (args.length < 3) {
                        System.out.println("Error: Missing arguments for list-tag command");
                        System.out.println(USAGE);
                        System.exit(1);
                    }
                    String tagValueToList = args[2];
                    tagger.listTagBindingsForTagValue(tagValueToList);
                    break;
                    
                default:
                    System.out.println("Error: Unknown command: " + command);
                    System.out.println(USAGE);
                    System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
} 