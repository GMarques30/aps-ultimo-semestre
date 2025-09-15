import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class HttpClient {

    public void sendRequest(String method, String uri, String json) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpUriRequestBase request = createRequest(method, uri, json);
            request.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                return;
            }

        } catch (IOException e) {
            System.err.println("Falha ao enviar requisição: " + e.getMessage());
        }
    }

    private HttpUriRequestBase createRequest(String method, String uri, String json) {
        switch (method.toUpperCase()) {
            case "POST":
                HttpPost post = new HttpPost(uri);
                if (json != null) post.setEntity(new StringEntity(json, StandardCharsets.UTF_8));
                return post;
            case "PUT":
                HttpPut put = new HttpPut(uri);
                if (json != null) put.setEntity(new StringEntity(json, StandardCharsets.UTF_8));
                return put;
            case "PATCH":
                HttpPatch patch = new HttpPatch(uri);
                if (json != null) patch.setEntity(new StringEntity(json, StandardCharsets.UTF_8));
                return patch;
            case "DELETE":
                return new HttpDelete(uri);
            case "GET":
                return new HttpGet(uri);
            default:
                throw new IllegalArgumentException("Método HTTP inválido: " + method);
        }
    }
}
