package com.example.gcptagging;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.resourcemanager.v3.TagKey;
import com.google.cloud.resourcemanager.v3.TagValue;

import java.io.FileInputStream;

/**
 * Example program that demonstrates creating tag keys and tag values in GCP.
 * Note: This requires an organization-level service account with appropriate permissions.
 */
public class CreateTagsExample {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java CreateTagsExample <service-account.json> <organization-id>");
            System.out.println("Example: java CreateTagsExample service-account.json 123456789012");
            System.exit(1);
        }

        String credentialsFile = args[0];
        String organizationId = args[1];

        try {
            // Load credentials
            GoogleCredentials credentials;
            try (FileInputStream keyStream = new FileInputStream(credentialsFile)) {
                credentials = GoogleCredentials.fromStream(keyStream)
                        .createScoped("https://www.googleapis.com/auth/cloud-platform");
            }

            // Create the TagsUtil
            TagsUtil tagsUtil = new TagsUtil(credentials, organizationId);

            // Example: Create an "environment" tag key
            TagKey environmentTagKey = tagsUtil.createTagKey(
                    "environment",
                    "Specifies the environment type for the resource"
            );
            System.out.println("Created environment tag key: " + environmentTagKey.getName());

            // Example: Create tag values for the environment tag key
            TagValue devValue = tagsUtil.createTagValue(
                    environmentTagKey.getName(),
                    "development",
                    "Development environment resources"
            );
            System.out.println("Created development tag value: " + devValue.getName());

            TagValue stageValue = tagsUtil.createTagValue(
                    environmentTagKey.getName(),
                    "staging",
                    "Staging environment resources"
            );
            System.out.println("Created staging tag value: " + stageValue.getName());

            TagValue prodValue = tagsUtil.createTagValue(
                    environmentTagKey.getName(),
                    "production",
                    "Production environment resources"
            );
            System.out.println("Created production tag value: " + prodValue.getName());

            // Example: Create a "department" tag key
            TagKey departmentTagKey = tagsUtil.createTagKey(
                    "department",
                    "Specifies the department that owns the resource"
            );
            System.out.println("Created department tag key: " + departmentTagKey.getName());

            // Example: Create tag values for the department tag key
            TagValue financeValue = tagsUtil.createTagValue(
                    departmentTagKey.getName(),
                    "finance",
                    "Finance department resources"
            );
            System.out.println("Created finance tag value: " + financeValue.getName());

            TagValue engineeringValue = tagsUtil.createTagValue(
                    departmentTagKey.getName(),
                    "engineering",
                    "Engineering department resources"
            );
            System.out.println("Created engineering tag value: " + engineeringValue.getName());

            // List all tag keys and their values
            System.out.println("\nListing all tag keys and values:");
            tagsUtil.listTagKeys().forEach(tagKey -> {
                System.out.println("Tag Key: " + tagKey.getName() + " (" + tagKey.getShortName() + ")");
                tagsUtil.listTagValues(tagKey.getName()).forEach(tagValue -> {
                    System.out.println("  - Tag Value: " + tagValue.getName() + " (" + tagValue.getShortName() + ")");
                });
            });

            // Close the TagsUtil
            tagsUtil.close();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}