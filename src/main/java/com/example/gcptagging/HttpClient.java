package com.example.gcptagging;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Simple HTTP client for making REST API calls to GCP.
 */
public class HttpClient {
    
    private final GoogleCredentials credentials;
    private static final Gson gson = new Gson();
    
    /**
     * Constructor with Google credentials.
     * 
     * @param credentials The credentials to use for authentication
     */
    public HttpClient(GoogleCredentials credentials) {
        this.credentials = credentials;
    }
    
    /**
     * Performs an HTTP GET request.
     * 
     * @param urlString The URL to send the request to
     * @return The response as a JsonObject
     * @throws IOException If an I/O error occurs
     */
    public JsonObject get(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("GET");
        setAuthHeader(connection);
        connection.setRequestProperty("Content-Type", "application/json");
        
        return processResponse(connection);
    }
    
    /**
     * Performs an HTTP POST request.
     * 
     * @param urlString The URL to send the request to
     * @param requestBody The body to include in the request
     * @return The response as a JsonObject
     * @throws IOException If an I/O error occurs
     */
    public JsonObject post(String urlString, JsonObject requestBody) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("POST");
        setAuthHeader(connection);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        
        // Log the request body for debugging
        String jsonBody = gson.toJson(requestBody);
        System.out.println("Request URL: " + urlString);
        System.out.println("Request body: " + jsonBody);
        
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        return processResponse(connection);
    }
    
    /**
     * Performs an HTTP DELETE request.
     * 
     * @param urlString The URL to send the request to
     * @return The response as a JsonObject or null if no content
     * @throws IOException If an I/O error occurs
     */
    public JsonObject delete(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("DELETE");
        setAuthHeader(connection);
        
        return processResponse(connection);
    }
    
    /**
     * Sets the Authorization header with an OAuth token.
     * 
     * @param connection The connection to set the header on
     * @throws IOException If refreshing the token fails
     */
    private void setAuthHeader(HttpURLConnection connection) throws IOException {
        // Ensure credentials are refreshed
        if (credentials.getAccessToken() == null) {
            credentials.refresh();
        }
        
        // Set Authorization header
        connection.setRequestProperty("Authorization", 
                "Bearer " + credentials.getAccessToken().getTokenValue());
    }
    
    /**
     * Processes an HTTP response and converts it to a JsonObject.
     * 
     * @param connection The connection to read the response from
     * @return The response as a JsonObject
     * @throws IOException If reading the response fails
     */
    private JsonObject processResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        
        System.out.println("Response code: " + responseCode + " (" + responseMessage + ")");
        
        // Check if the request failed
        if (responseCode >= 400) {
            String errorMessage;
            try (InputStream errorStream = connection.getErrorStream()) {
                if (errorStream != null) {
                    InputStreamReader reader = new InputStreamReader(errorStream, StandardCharsets.UTF_8);
                    JsonElement errorElement = JsonParser.parseReader(reader);
                    if (errorElement.isJsonObject()) {
                        JsonObject error = errorElement.getAsJsonObject();
                        errorMessage = error.toString();
                        // Print more detailed error info if available
                        if (error.has("error") && error.getAsJsonObject("error").has("message")) {
                            System.err.println("Error message: " + error.getAsJsonObject("error").get("message").getAsString());
                        }
                    } else {
                        errorMessage = errorElement.toString();
                    }
                } else {
                    errorMessage = responseMessage;
                }
            }
            
            throw new IOException("Request failed with response code: " + responseCode + 
                                 ", error: " + errorMessage);
        }
        
        // If there's no content, return an empty JsonObject
        if (responseCode == 204) {
            return new JsonObject();
        }
        
        // Read and parse response
        try (InputStream is = connection.getInputStream()) {
            if (is != null) {
                InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                JsonElement response = JsonParser.parseReader(reader);
                if (response.isJsonObject()) {
                    JsonObject jsonResponse = response.getAsJsonObject();
                    System.out.println("Response body: " + gson.toJson(jsonResponse));
                    return jsonResponse;
                } else {
                    System.out.println("Response is not a JSON object: " + response);
                    return new JsonObject();
                }
            } else {
                return new JsonObject();
            }
        }
    }
    
    /**
     * Adds query parameters to a URL string.
     * 
     * @param baseUrl The base URL
     * @param params Map of parameter names to values
     * @return The URL with query parameters
     */
    public static String addQueryParams(String baseUrl, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }
        
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        urlBuilder.append(baseUrl.contains("?") ? "&" : "?");
        
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                urlBuilder.append("&");
            }
            urlBuilder.append(entry.getKey())
                     .append("=")
                     .append(entry.getValue());
            first = false;
        }
        
        return urlBuilder.toString();
    }
} 