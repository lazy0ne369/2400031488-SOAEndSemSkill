package soa.service;

import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;
import java.util.List;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class GatewayService {

    private final DiscoveryClient DC;
    private final RestTemplate RT;

    public GatewayService(DiscoveryClient DC) {
        this.DC = DC;
        this.RT = new RestTemplate();
        this.RT.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
    }

    public String getRbcServiceUrl() {
        List<ServiceInstance> instances = DC.getInstances("rbc");
        if (instances == null || instances.isEmpty()) {
            instances = DC.getInstances("RBC");
        }

        if (instances != null && !instances.isEmpty()) {
            return instances.get(0).getUri().toString();
        }

        return "http://localhost:8080";
    }

    public ResponseEntity<byte[]> forward(HttpServletRequest request, byte[] body) {
        String baseServiceUrl = getRbcServiceUrl();
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();

        String targetPath = requestUri;
        if (requestUri.startsWith("/gateway/books")) {
            targetPath = requestUri.replaceFirst("/gateway/books", "/rbc/books");
        }

        String fullUrl = baseServiceUrl + targetPath + (queryString != null && !queryString.isEmpty() ? "?" + queryString : "");
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if ("host".equalsIgnoreCase(headerName) ||
                    "content-length".equalsIgnoreCase(headerName) ||
                    "transfer-encoding".equalsIgnoreCase(headerName) ||
                    "connection".equalsIgnoreCase(headerName)) {
                    continue;
                }
                Enumeration<String> headerValues = request.getHeaders(headerName);
                while (headerValues.hasMoreElements()) {
                    headers.add(headerName, headerValues.nextElement());
                }
            }
        }

        HttpEntity<byte[]> httpEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<byte[]> response = RT.exchange(
                    URI.create(fullUrl),
                    method,
                    httpEntity,
                    byte[].class
            );

            HttpHeaders responseHeaders = new HttpHeaders();
            response.getHeaders().forEach((name, values) -> {
                if (!"transfer-encoding".equalsIgnoreCase(name)) {
                    responseHeaders.put(name, values);
                }
            });

            return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());

        } catch (Exception e) {
            HttpHeaders errHeaders = new HttpHeaders();
            errHeaders.setContentType(MediaType.APPLICATION_JSON);
            String errBody = "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
            return ResponseEntity.status(502).headers(errHeaders).body(errBody.getBytes());
        }
    }

    // ADD BOOK
    public ResponseEntity<String> addBook(String requestBody) {
        String url = getRbcServiceUrl() + "/rbc/books";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        return RT.exchange(url, HttpMethod.POST, request, String.class);
    }

    // VIEW ALL BOOKS
    public ResponseEntity<String> getAllBooks() {
        String url = getRbcServiceUrl() + "/rbc/books";
        return RT.exchange(url, HttpMethod.GET, null, String.class);
    }

    // VIEW BOOK BY ID
    public ResponseEntity<String> getBookById(Long id) {
        String url = getRbcServiceUrl() + "/rbc/books/" + id;
        return RT.exchange(url, HttpMethod.GET, null, String.class);
    }

    // UPDATE BOOK
    public ResponseEntity<String> updateBook(Long id, String requestBody) {
        String url = getRbcServiceUrl() + "/rbc/books/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        return RT.exchange(url, HttpMethod.PUT, request, String.class);
    }

    // DELETE BOOK
    public ResponseEntity<String> deleteBook(Long id) {
        String url = getRbcServiceUrl() + "/rbc/books/" + id;
        return RT.exchange(url, HttpMethod.DELETE, null, String.class);
    }
}	