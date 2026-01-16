package code.apiversion.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Client {

    private static final String BASE_URL = "http://localhost:8800/example";

    public static void main(String[] args) {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        invokeEndpoint(client, "/greeting");
        invokeEndpoint(client, "/details");
        /*
         * invokeEndpointWithVersion(client, "/greeting", 1);
         * invokeEndpointWithVersion(client, "/details", 1);
         * invokeEndpointWithVersion(client, "/greeting", 2);
         * invokeEndpointWithVersion(client, "/details", 2);
         */
    }

    private static void invokeEndpointWithVersion(HttpClient client, String path, int version) {
        System.out.println("Invoking: " + path);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/v" + version + path))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            System.out.println("--------------------------------------------------");
        } catch (Exception e) {
            System.err.println("Error calling " + path + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void invokeEndpoint(HttpClient client, String path) {
        System.out.println("Invoking: " + path);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            System.out.println("--------------------------------------------------");
        } catch (Exception e) {
            System.err.println("Error calling " + path + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
